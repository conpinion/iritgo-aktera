/**
 * This file is part of the Iritgo/Aktera Framework.
 *
 * Copyright (C) 2005-2011 Iritgo Technologies.
 * Copyright (C) 2003-2005 BueroByte GbR.
 *
 * Iritgo licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iritgo.aktera.comm;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;


/**
 * Handles the transfer and serialization issues for BLOBs and other binary objects.
 * Designed specifically to handle VERY large objects (1GB+) as well as small objects.
 *
 * Based VERRY Heavily on the DefaultFileItem object from the Jarkarta Commons Fileupload
 * project.
 *
 * <B>This is currently a SINGLE JVM VERSION!  Implemented in this fashion to
 * quickly allow the testing of the basic logic, and to solve a single (the current
 * for me) use case. This object does need to be enhanced!</B>  Before anybody actually
 * enhances this object, one should spend some time looking at the DefaultFileItem
 * object from the Jakarta Commons Fileupload project.
 *
 * @author Stephen Davidson
 */
public class BinaryWrapper implements Serializable
{
	//==========================================================================
	//Protected inner-class SlowStreamReader
	//==========================================================================

	/**
	 * Depending on the circumstances, sometimes InputStreams can be slow.  And
	 * sometimes the are done, but they do not signal so with a -1, but instead
	 * cause the read command to hang indefinately.  The purpose of this class
	 * is to allow a slow read to be monitored, and if needed, killed.
	 *
	 * @author Stephen Davidson
	 *
	 */
	protected class SlowStreamReader implements Runnable
	{
		byte b = - 2; //Unitialized MAGIC VALUE

		final InputStream inStream;

		IOException e;

		protected SlowStreamReader (final InputStream inStream)
		{
			this.inStream = inStream;
		}

		public void run ()
		{
			try
			{
				b = (byte) inStream.read ();
			}
			catch (IOException e)
			{
				e.printStackTrace ();
				this.e = e;
			}
		} //end run
	} //end inner class SlowStreamReader

	// ----------------------------------------------------------- Data members

	/**
	 * How many milliseconds to sleep before retrying Input Stream
	 */
	public static final int TIME_OUT = 5000;

	/**
	 * Counter used in unique identifier generation.
	 */
	private static int counter = 0;

	/**
	 * The content type passed by the browser, or <code>null</code> if
	 * not defined.
	 */
	protected String contentType;

	/**
	 * The original filename in the user's filesystem.
	 */
	protected String fileName;

	/**
	 * The threshold above which uploads will be stored on disk.
	 */
	protected int sizeThreshold;

	/**
	 * The directory in which uploaded files will be stored, if stored on disk.
	 */
	protected File repository;

	/**
	 * Cached contents of the file.
	 */
	protected transient byte[] cachedContent;

	/**
	 * Output stream for this item.
	 */
	protected DeferredFileOutputStream dfos;

	/**
	 * A BinaryWrapper can be a reference to an external process (e.g.
	 * some other, possibly even non-Java executable application) which
	 * will supply the output for this BinaryWrapper.
	 * If the execPath is not null, then this BinaryWrapper is pointing
	 * to such an executable.
	 */
	private String execPath = null;

	/**
	 * Handle to the current process, if any
	 */
	private transient Process currentProcess = null;

	/**
	 * The keel server that this ojbect originates on.
	 */
	protected String keelServer;

	// ----------------------------------------------------------- Constructors

	/**
	 * Constructs a new <code>BinaryWrapper</code> instance.
	 *
	 * @param keelServer    The Server name/id that this object was created on.
	 * @param contentType   The content type passed by the browser or
	 *                      <code>null</code> if not specified.
	 * @param fileName      The original filename in the user's filesystem, or
	 *                      <code>null</code> if not specified.
	 * @param sizeThreshold The threshold, in bytes, below which items will be
	 *                      retained in memory and above which they will be
	 *                      stored as a file.
	 * @param repository    The data repository, which is the directory in
	 *                      which files will be created, should the item size
	 *                      exceed the threshold.   If null, will be stored in
	 *                      temporarily in the system tmp directory.
	 */
	public BinaryWrapper (String keelServer, String contentType, String fileName, int sizeThreshold, File repository)
	{
		//Parameter valid value checks...
		if (sizeThreshold <= 512)
		{
			//1/2K is smallest buffer size allowed
			throw new IllegalArgumentException ("Threshold is too small for buffer: " + sizeThreshold);
		}

		if (contentType == null)
		{
			throw new NullPointerException ("ContentType is null");
		}

		if (fileName == null)
		{
			throw new NullPointerException ("Filename is null");
		}

		//else needed parameters supplied, with hopefully valid values.
		this.keelServer = keelServer;
		this.contentType = contentType;
		this.fileName = fileName;
		this.sizeThreshold = sizeThreshold;
		this.repository = repository;
	}

	/**
	 * Specify a path to an executable from which this BinaryWrapper
	 * get's it's content. You can supply command-line arguments as part
	 * of the string, as necessary.
	 *
	 * @param newPath
	 */
	public void setExecutablePath (String newPath)
	{
		assert newPath != null;
		execPath = newPath;
	}

	/**
	 * Does this BinaryWrapper refer to some external executable program
	 * in order to get it's content?
	 *
	 * @return True if this is an "executable"-style BinaryWrapper, false if it's not.
	 */
	public boolean isExecutable ()
	{
		if (execPath != null)
		{
			return true;
		}

		return false;
	}

	// ------------------------------- Methods from javax.activation.DataSource

	/**
	 * Returns an {@link java.io.InputStream InputStream} that can be
	 * used to retrieve the contents of the file.
	 *
	 * @return An {@link java.io.InputStream InputStream} that can be
	 *         used to retrieve the contents of the file.
	 *
	 * @exception IOException if an error occurs.
	 */
	public InputStream getInputStream () throws IOException
	{
		if (execPath != null)
		{
			if (currentProcess != null)
			{
				throw new IOException ("InputStream already open, please close first");
			} //else No currently running processes, so we can get input stream
			//Note that some processes may lock files when running, and some
			//can only be called one at a time.

			currentProcess = Runtime.getRuntime ().exec (execPath);

			return currentProcess.getInputStream ();
		}

		if (dfos == null)
		{
			//Fresh Wrapper, with Data available on Disk...
			return new FileInputStream (new File (this.repository, this.fileName));
		}
		else
		{
			if (! dfos.isInMemory ())
			{
				return new FileInputStream (dfos.getFile ());
			}

			if (cachedContent == null)
			{
				cachedContent = dfos.getData ();
			}

			return new ByteArrayInputStream (cachedContent);
		} //end else
	} //end getInputStream()

	/**
	 * Returns the content type passed by the browser or <code>null</code> if
	 * not defined.
	 *
	 * @return The content type passed by the browser or <code>null</code> if
	 *         not defined.
	 */
	public String getContentType ()
	{
		return contentType;
	}

	/**
	 * Returns the original filename in the client's filesystem.
	 *
	 * @return The original filename in the client's filesystem.
	 */
	public String getName ()
	{
		return fileName;
	}

	// ------------------------------------------------------- FileItem methods

	/**
	 * Provides a hint as to whether or not the file contents will be read
	 * from memory.
	 *
	 * @return <code>true</code> if the file contents will be read
	 *         from memory; <code>false</code> otherwise.
	 */
	public boolean isInMemory ()
	{
		return (dfos.isInMemory ());
	}

	/**
	 * Returns the size of the file.
	 *
	 * @return The size of the file, in bytes.
	 */
	public long getSize ()
	{
		if (this.execPath != null)
		{
			//Size unkown/irrelevant
			return (0L);
		}

		if (cachedContent != null)
		{
			return cachedContent.length;
		}
		else if (dfos != null)
		{
			if (dfos.isInMemory ())
			{
				return dfos.getData ().length;
			}
			else
			{
				return dfos.getFile ().length ();
			}
		}
		else
		{
			//No output stream, so probably fresh wrapper for an input Stream
			final File thisFile = new File (this.repository, this.fileName);

			if (! (thisFile.exists () && thisFile.isFile ()))
			{
				throw new IllegalStateException ("File does not exist or is a directory: "
								+ thisFile.getAbsolutePath ());
			}

			return (thisFile.length ());
		}
	}

	/**
	 * Returns the contents of the file as an array of bytes.  If the
	 * contents of the file were not yet cached in memory, they will be
	 * loaded from the disk storage and cached.
	 *
	 * @return The contents of the file as an array of bytes.
	 */
	public byte[] get ()
	{
		if (dfos.isInMemory ())
		{
			if (cachedContent == null)
			{
				cachedContent = dfos.getData ();
			}

			return cachedContent;
		}

		byte[] fileData = new byte[(int) getSize ()];
		FileInputStream fis = null;

		try
		{
			fis = new FileInputStream (dfos.getFile ());
			fis.read (fileData);
		}
		catch (IOException e)
		{
			fileData = null;
		}
		finally
		{
			if (fis != null)
			{
				try
				{
					fis.close ();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}

		return fileData;
	}

	/**
	 * Returns the contents of the file as a String, using the specified
	 * encoding.  This method uses {@link #get()} to retrieve the
	 * contents of the file.
	 *
	 * @param encoding The character encoding to use.
	 *
	 * @return The contents of the file, as a string.
	 *
	 * @exception UnsupportedEncodingException if the requested character
	 *                                         encoding is not available.
	 */
	public String getString (String encoding) throws UnsupportedEncodingException
	{
		return new String (get (), encoding);
	}

	/**
	 * Returns the contents of the file as a String, using the default
	 * character encoding.  This method uses {@link #get()} to retrieve the
	 * contents of the file.
	 *
	 * @return The contents of the file, as a string.
	 */
	public String getString ()
	{
		return new String (get ());
	}

	/**
	 * A convenience method to write an uploaded item to disk. The client code
	 * is not concerned with whether or not the item is stored in memory, or on
	 * disk in a temporary location. They just want to write the uploaded item
	 * to a file.
	 * <p>
	 * This implementation first attempts to rename the uploaded item to the
	 * specified destination file, if the item was originally written to disk.
	 * Otherwise, the data will be copied to the specified file.
	 * <p>
	 * This method is only guaranteed to work <em>once</em>, the first time it
	 * is invoked for a particular item. This is because, in the event that the
	 * method renames a temporary file, that file will no longer be available
	 * to copy or rename again at a later time.
	 *
	 * @param file The <code>File</code> into which the uploaded item should
	 *             be stored.
	 *
	 * @exception IOException if an error occurs.
	 */
	public void write (File file) throws IOException
	{
		if (isInMemory ())
		{
			FileOutputStream fout = null;

			try
			{
				fout = new FileOutputStream (file);
				fout.write (get ());
			}
			finally
			{
				if (fout != null)
				{
					fout.close ();
				}
			}
		}
		else
		{
			File outputFile = getStoreLocation ();

			if (outputFile != null)
			{
				/*
				 * The uploaded file is being stored on disk
				 * in a temporary location so move it to the
				 * desired file.
				 */
				if (! outputFile.renameTo (file))
				{
					BufferedInputStream in = null;
					BufferedOutputStream out = null;

					try
					{
						in = new BufferedInputStream (new FileInputStream (outputFile));
						out = new BufferedOutputStream (new FileOutputStream (file));

						byte[] bytes = new byte[2048];
						int s = 0;

						while ((s = in.read (bytes)) != - 1)
						{
							out.write (bytes, 0, s);
						}
					}
					finally
					{
						try
						{
							in.close ();
						}
						catch (IOException e)
						{
							// ignore
						}

						try
						{
							out.close ();
						}
						catch (IOException e)
						{
							// ignore
						}
					}
				}
			}
			else
			{
				// For whatever reason we cannot write the file to disk.
				throw new IOException ("Cannot write file data to disk!");
			} //end else
		} //end else
	} //end write

	/**
	 * Closes and flushes the underlying output stream.
	 * @throws IOException Thrown if any errors occur with the output streams
	 */
	public void close () throws IOException
	{
		IOException ioe = null;

		if (this.dfos != null)
		{
			try
			{
				this.dfos.flush ();
			}
			catch (IOException e)
			{
				ioe = e;
			}
			finally
			{
				//Force a close attempt even if flush fails...
				try
				{
					this.dfos.close ();
				}
				catch (IOException e)
				{
					if (ioe != null)
					{
						//Cascading errors.  Print current one before dumping.
						ioe.printStackTrace ();
					}

					ioe = e;
				}
			} //end finally
		} //end if

		if (currentProcess != null)
		{
			//Streams are closed, kill process?
			try
			{
				final int exitValue = currentProcess.exitValue ();

				if (exitValue != 0)
				{
					System.err.println (this.getClass ().getName () + ".close:\'" + this.execPath
									+ "\' exited with value of " + exitValue);
				} //end if
				//Otherwise normal exit.
			}
			catch (IllegalThreadStateException e)
			{
				//Still running, so kill process.
				currentProcess.destroy ();
			}

			currentProcess = null;
		}

		if (ioe != null)
		{
			throw ioe;
		}
	} //end close

	/**
	 * Deletes the underlying storage for a file item, including deleting any
	 * associated temporary disk file. Although this storage will be deleted
	 * automatically when the <code>FileItem</code> instance is garbage
	 * collected, this method can be used to ensure that this is done at an
	 * earlier time, thus preserving system resources.
	 */
	public void delete ()
	{
		cachedContent = null;

		File outputFile = getStoreLocation ();

		if (outputFile != null && outputFile.exists ())
		{
			outputFile.delete ();
		}
	}

	/**
	 * Returns an {@link java.io.OutputStream OutputStream} that can
	 * be used for storing the contents of the file.
	 *
	 * @return An {@link java.io.OutputStream OutputStream} that can be used
	 *         for storing the contensts of the file.
	 *
	 * @exception IOException if an error occurs.
	 */
	public OutputStream getOutputStream () throws IOException
	{
		if (execPath != null)
		{
			if (currentProcess != null)
			{
				currentProcess = Runtime.getRuntime ().exec (execPath);
			}

			return currentProcess.getOutputStream ();
		}

		if (dfos == null)
		{
			File outputFile = getTempFile ();

			dfos = new DeferredFileOutputStream (sizeThreshold, outputFile);
		}

		return dfos;
	}

	// --------------------------------------------------------- Public methods

	/**
	 * Returns the {@link java.io.File} object for the <code>FileItem</code>'s
	 * data's temporary location on the disk. Note that for
	 * <code>FileItem</code>s that have their data stored in memory,
	 * this method will return <code>null</code>. When handling large
	 * files, you can use {@link java.io.File#renameTo(java.io.File)} to
	 * move the file to new location without copying the data, if the
	 * source and destination locations reside within the same logical
	 * volume.
	 *
	 * @return The data file, or <code>null</code> if the data is stored in
	 *         memory.
	 */
	public File getStoreLocation ()
	{
		return dfos.getFile ();
	}

	/**
	 * Writes the data from an Input stream to the DeferredFileOutputStream.
	 * This allows for serialized transfer of FileData, as needed.
	 * @param inStream The stream to read from
	 * @return The number of bytes written.
	 * @throws IOException Thrown if an error occurs reading/writing streams.
	 */
	public long writeFrom (final InputStream inStream) throws IOException
	{
		final OutputStream outStream = this.getOutputStream ();

		try
		{
			//Don't close local output stream here, as more may get written later.
			return (write (inStream, outStream));
		}
		finally
		{
			if (this.isInMemory ())
			{
				//reload/resize cache (or clear if dfos data not cached.
				this.cachedContent = dfos.getData ();
			} //end if
		} //end finally
	} //end writeFrom

	/**
	 * Writes the data stored to the supplied output stream.  Ready method for
	 * writing out to Browsers and other clients as needed.
	 * @param outStream The stream to write to
	 * @return The number of bytes written.
	 * @throws IOException Thrown if an error occurs reading/writing streams.
	 */
	public long writeTo (final OutputStream outStream) throws IOException
	{
		final InputStream inStream = new BufferedInputStream (this.getInputStream ());

		try
		{
			return (write (inStream, outStream));
		}
		finally
		{
			try
			{
				this.close ();
			}

			//Closing anyways, so just log the exception and carry on.
			catch (IOException e)
			{
				e.printStackTrace ();
			}
		} //end finally
	} //end writeTo

	protected long write (final InputStream inStream, final OutputStream outStream) throws IOException
	{
		int len;
		long written = 0;

		//Clear cache of stale data, and reset size to max
		this.cachedContent = new byte[this.sizeThreshold];

		//TODO: See if one (or both) of the streams support Channels, and specifically Byte channel operations
		int available = inStream.available ();

		//SPDDBG:
		//         System.out.println("***Available = " + available);
		if (available > 0)
		{
			if (available >= this.sizeThreshold)
			{
				for (int totalRead = 0; totalRead < available; totalRead += len)
				{
					len = inStream.read (cachedContent, 0,
									this.sizeThreshold < available - totalRead ? this.sizeThreshold : available
													- totalRead);
					outStream.write (cachedContent, 0, len);
					written += len;
				} //end for
			}
			else
			{
				//First block available not size of cache, but everything seems
				//to be running normally...
				while ((available > 0) && (len = inStream.read (cachedContent, 0, available)) >= 0)
				{
					//SPDDBG:
					//                     System.out.println("***Read Bytes: " + len);
					outStream.write (cachedContent, 0, len);
					written += len;
					available = inStream.available ();

					//SPDDBG:
					//                     System.out.println("***Available Bytes: " + available);
					if (available < 1)
					{
						//Check to see if EOF -- Danger, this can hang!
						final SlowStreamReader reader = new SlowStreamReader (inStream);
						final Thread t = new Thread (reader);

						t.start ();

						try
						{
							t.join (TIME_OUT);
						}
						catch (InterruptedException e)
						{
							//Something woke us up.
							e.printStackTrace ();
						}

						if (reader.e != null)
						{
							throw reader.e;
						} //else

						if (t.isAlive ())
						{
							//Still running, but is hung
							System.err.println ("Hung Reader -- Killing it.");
							t.interrupt ();
							reader.b = - 2;
						}

						final byte b = reader.b;

						//                        final byte b = (byte)inStream.read();
						if (b == - 2)
						{
							//Hung read, was killed
							available = 0;
						}
						else if (b == - 1)
						{
							//EOF, set available to 1, and loop control will take it from there.
							available = 1;
						}
						else
						{
							outStream.write (b);
							written++;

							//Recursive call, as entire routine needs to be restarted.
							return (written + write (inStream, outStream));
						}
					} //end if (available
				} //end while
			} //end else
		}
		else
		{
			//Nothing avaible, Sleep & Retry two more times, then check to see if
			//Error available and dump that to System.err
			sleep ();

			if (inStream.available () > 0)
			{
				return (write (inStream, outStream));
			}
			else
			{
				sleep ();

				if (inStream.available () > 0)
				{
					return (write (inStream, outStream));
				}
				else
				{
					//Instream is empty????
					if (currentProcess != null)
					{
						//Get Error stream, and any messages on it.
						final InputStream processError = currentProcess.getErrorStream ();

						if (processError.available () > 0)
						{
							//Process has error data available!
							final StringBuffer error = new StringBuffer (processError.available ());

							for (; processError.available () > 0;)
							{
								final char c = (char) processError.read ();

								if (c >= 0)
								{
									error.append (c);
								} //end if b
							} //end for

							throw new IOException (error.toString ());
						} //end if(processError.available()
					} //end if (currentProcess
				} //end else
			} //end else
		} //end else

		//        while((len = inStream.read(cachedContent, 0, this.sizeThreshold)) >= 0){
		//            outStream.write(cachedContent, 0, len);
		//            written += len;
		//        }
		outStream.flush ();

		if (currentProcess != null)
		{
			//Get Error stream, and any messages on it.
			final InputStream processError = currentProcess.getErrorStream ();

			if (processError.available () > 0)
			{
				//Process has error data available!
				write (processError, System.err);
			} //end if(processError.available()
		}

		return (written);
	} //end write

	private void sleep ()
	{
		try
		{
			Thread.sleep (TIME_OUT);
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace ();
		}
	} //end sleep

	// ------------------------------------------------------ Protected methods

	/**
	 * Removes the file contents from the temporary storage.
	 */
	protected void finalize () throws Throwable
	{
		try
		{
			this.close ();
		}
		catch (IOException e)
		{
			//In garbage collector, so just flag and carry on.
			e.printStackTrace ();
		}

		File outputFile = dfos.getFile ();

		if (outputFile != null && outputFile.exists ()
						&& (outputFile.getName ().startsWith ("upload_") && outputFile.getName ().endsWith (".tmp")))
		{
			outputFile.delete ();
		} //else not a temp file, so don't delete it.

		super.finalize ();
	}

	/**
	 * Creates and returns a {@link java.io.File File} representing a uniquely
	 * named temporary file in the configured repository path.
	 *
	 * @return The {@link java.io.File File} to be used for temporary storage.
	 */
	protected File getTempFile ()
	{
		File tempDir = repository;

		if (tempDir == null)
		{
			tempDir = new File (System.getProperty ("java.io.tmpdir"));
		}

		String fileName = "upload_" + getUniqueId () + ".tmp";

		File f = new File (tempDir, fileName);

		f.deleteOnExit ();

		return f;
	}

	// -------------------------------------------------------- Private methods

	/**
	 * Returns an identifier that is unique within the class loader used to
	 * load this class, but does not have random-like apearance.
	 *
	 * @return A String with the non-random looking instance identifier.
	 */
	private static String getUniqueId ()
	{
		int current;
		String id;

		synchronized (BinaryWrapper.class)
		{
			current = counter++;

			if (counter > 999999999)
			{
				//If counter >= 100 million, flip it back to 0, to keep it below 8 chars.
				counter = 0;
			}

			id = Integer.toString (current);
		}

		id = ("00000000" + id).substring (id.length ());

		return id;
	}
} //end BinaryWrapper
