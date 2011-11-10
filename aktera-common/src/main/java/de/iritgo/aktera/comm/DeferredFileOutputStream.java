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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;


/**
 * <p>An output stream which will retain data in memory until a specified
 * threshold is reached, and only then commit it to disk. If the stream is
 * closed before the threshold is reached, the data will not be written to
 * disk at all.</p>
 * <p>This is based very heavily on the objects DeferredFileOutputStream &
 * ThresholdingFileOutputStream from the Commons FileUpload project.</p>
 * <B>This is currently a SINGLE JVM VERSION!  Implemented in this fashion to
 * quickly allow the testing of the basic logic, and to solve a single (the current
 * for me) use case. This object does need to be enhanced!</B>
 * @author Stephen Davidson
 * @todo: Enhance to allow operation in a distributed environment.
 *
 */
public class DeferredFileOutputStream extends OutputStream implements Serializable
{
	// ----------------------------------------------------------- Data members

	/**
	 * The threshold at which the event will be triggered.
	 */
	protected int threshold;

	/**
	 * The number of bytes written to the output stream.
	 */
	protected long written;

	/**
	 * Whether or not the configured threshold has been exceeded.
	 */
	protected boolean thresholdExceeded;

	/**
	 * The output stream to which data will be written prior to the theshold
	 * being reached.
	 */
	protected transient ByteArrayOutputStream memoryOutputStream;

	/**
	 * The output stream to which data will be written after the theshold is
	 * reached.
	 */
	protected transient FileOutputStream diskOutputStream;

	/**
	 * The output stream to which data will be written at any given time. This
	 * will always be one of <code>memoryOutputStream</code> or
	 * <code>diskOutputStream</code>.
	 */
	protected transient OutputStream currentOutputStream;

	/**
	 * The file to which output will be directed if the threshold is exceeded.
	 * Note that this will may work when operating in a distributed environment.
	 * This is currently for version 1.0 of this file, operating in a single JVM
	 * environment.
	 */
	protected File outputFile;

	// ----------------------------------------------------------- Constructors

	/**
	 * Constructs an instance of this class which will trigger an event at the
	 * specified threshold.
	 *
	 * @param threshold The number of bytes at which to trigger an event.
	 */
	public DeferredFileOutputStream(int threshold)
	{
		this.threshold = threshold;
		memoryOutputStream = new ByteArrayOutputStream(threshold);
		currentOutputStream = memoryOutputStream;
	}

	/**
	 * Constructs an instance of this class which will trigger an event at the
	 * specified threshold, and save data to a file beyond that point.
	 *
	 * @param threshold  The number of bytes at which to trigger an event.
	 * @param outputFile The file to which data is saved beyond the threshold.
	 */
	public DeferredFileOutputStream(int threshold, File outputFile)
	{
		this(threshold);
		this.outputFile = outputFile;
	}

	// --------------------------------------------------- OutputStream methods

	/**
	 * Writes the specified byte to this output stream.
	 *
	 * @param b The byte to be written.
	 *
	 * @exception IOException if an error occurs.
	 */
	public void write(int b) throws IOException
	{
		checkThreshold(1);
		getStream().write(b);
		written++;
	}

	/**
	 * Writes <code>b.length</code> bytes from the specified byte array to this
	 * output stream.
	 *
	 * @param b The array of bytes to be written.
	 *
	 * @exception IOException if an error occurs.
	 */
	public void write(byte[] b) throws IOException
	{
		checkThreshold(b.length);
		getStream().write(b);
		written += b.length;
	}

	/**
	 * Writes <code>len</code> bytes from the specified byte array starting at
	 * offset <code>off</code> to this output stream.
	 *
	 * @param b   The byte array from which the data will be written.
	 * @param off The start offset in the byte array.
	 * @param len The number of bytes to write.
	 *
	 * @exception IOException if an error occurs.
	 */
	public void write(byte[] b, int off, int len) throws IOException
	{
		checkThreshold(len);
		getStream().write(b, off, len);
		written += len;
	} //end write(byte[], int, int)

	/**
	 * Flushes this output stream and forces any buffered output bytes to be
	 * written out.
	 *
	 * @exception IOException if an error occurs.
	 */
	public void flush() throws IOException
	{
		if (getStream() != null)
		{
			getStream().flush();
		}
	} //end flush

	/**
	 * Closes this output stream and releases any system resources associated
	 * with this stream.
	 *
	 * @exception IOException if an error occurs.
	 */
	public void close() throws IOException
	{
		if (getStream() != null)
		{
			try
			{
				flush();
			}
			catch (IOException ignored)
			{
				// ignore
			}

			getStream().close();
		}
	}

	// --------------------------------------------------------- Public methods

	/**
	 * Returns the threshold, in bytes, at which an event will be triggered.
	 *
	 * @return The threshold point, in bytes.
	 */
	public int getThreshold()
	{
		return threshold;
	}

	/**
	 * Returns the number of bytes that have been written to this output stream.
	 *
	 * @return The number of bytes written.
	 */
	public long getByteCount()
	{
		return written;
	}

	/**
	 * Determines whether or not the configured threshold has been exceeded for
	 * this output stream.
	 *
	 * @return <code>true</code> if the threshold has been reached;
	 *         <code>false</code> otherwise.
	 */
	public boolean isThresholdExceeded()
	{
		return (written > threshold);
	}

	/**
	 * Determines whether or not the data for this output stream has been
	 * retained in memory.
	 *
	 * @return <code>true</code> if the data is available in memory;
	 *         <code>false</code> otherwise.
	 */
	public boolean isInMemory()
	{
		return (! isThresholdExceeded());
	}

	/**
	 * Returns the data for this output stream as an array of bytes, assuming
	 * that the data has been retained in memory. If the data was written to
	 * disk, this method returns <code>null</code>.
	 *
	 * @return The data for this output stream, or <code>null</code> if no such
	 *         data is available.
	 */
	public byte[] getData()
	{
		if (memoryOutputStream != null)
		{
			return memoryOutputStream.toByteArray();
		}

		return null;
	}

	/**
	 * Returns the data for this output stream as a <code>File</code>, assuming
	 * that the data was written to disk. If the data was retained in memory,
	 * this method returns <code>null</code>.
	 *
	 * @return The file for this output stream, or <code>null</code> if no such
	 *         file exists.
	 */
	public File getFile()
	{
		return outputFile;
	}

	// ------------------------------------------------------ Protected methods

	/**
	 * Checks to see if writing the specified number of bytes would cause the
	 * configured threshold to be exceeded. If so, triggers an event to allow
	 * a concrete implementation to take action on this.
	 *
	 * @param count The number of bytes about to be written to the underlying
	 *              output stream.
	 *
	 * @exception IOException if an error occurs.
	 */
	protected void checkThreshold(int count) throws IOException
	{
		if (! thresholdExceeded && (written + count > threshold))
		{
			thresholdReached();
			thresholdExceeded = true;
		}
	}

	/**
	 * Returns the current output stream. This may be memory based or disk
	 * based, depending on the current state with respect to the threshold.
	 *
	 * @return The underlying output stream.
	 *
	 * @exception IOException if an error occurs.
	 */
	protected OutputStream getStream() throws IOException
	{
		return currentOutputStream;
	}

	/**
	 * Switches the underlying output stream from a memory based stream to one
	 * that is backed by disk. This is the point at which we realise that too
	 * much data is being written to keep in memory, so we elect to switch to
	 * disk-based storage.
	 *
	 * @exception IOException if an error occurs.
	 */
	protected void thresholdReached() throws IOException
	{
		byte[] data = memoryOutputStream.toByteArray();
		FileOutputStream fos = new FileOutputStream(outputFile);

		fos.write(data);
		diskOutputStream = fos;
		currentOutputStream = fos;
		memoryOutputStream = null;
	}

	// ------------------------------------------- Private Serialization methods
	private void writeObject(java.io.ObjectOutputStream out) throws IOException
	{
		out.writeInt(this.threshold);
		out.writeLong(this.written);
		out.writeBoolean(this.thresholdExceeded);
		out.writeObject(outputFile);

		if (! this.thresholdExceeded)
		{
			//Everything is stored in Memory, so write the data to the stream
			this.memoryOutputStream.writeTo(out);
		}
		else
		{
			//Just make sure everything is written to disk for deserialization
			flush();
		}
	} //end writeObject

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		this.threshold = in.readInt();
		this.written = in.readLong();
		this.thresholdExceeded = in.readBoolean();
		this.outputFile = (File) in.readObject();

		if (! this.thresholdExceeded)
		{
			//Bytes were written to the outputstream, so load them.
			//Note that if we are here, written will always be <= threshold, an int
			byte[] buf = new byte[(int) this.written];

			in.readFully(buf);
			this.memoryOutputStream = new ByteArrayOutputStream(buf.length);
			this.memoryOutputStream.write(buf);
		}
	} //end readObject

	protected void finalize() throws Throwable
	{
		try
		{
			this.close();
		}
		catch (IOException e)
		{
			//In garbage collector, so just flag and carry on.
			e.printStackTrace();
		}

		super.finalize();
	}
}
