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

/*
 * Copyright (c) 2002, The Keel Group, Ltd. All rights reserved.
 *
 * This software is made available under the terms of the license found
 * in the LICENSE file, included with this source code. The license can
 * also be found at:
 * http://www.keelframework.net/LICENSE
 */

package de.iritgo.aktera.struts;


import de.iritgo.aktera.clients.ClientException;
import de.iritgo.aktera.clients.ResponseElementDynaBean;
import de.iritgo.aktera.clients.webapp.AbstractWebappClientConnector;
import de.iritgo.aktera.clients.webapp.DefaultWebappRequest;
import de.iritgo.aktera.clients.webapp.DefaultWebappResponse;
import de.iritgo.aktera.clients.webapp.WebappRequest;
import de.iritgo.aktera.clients.webapp.WebappResponse;
import de.iritgo.aktera.comm.BinaryWrapper;
import de.iritgo.aktera.comm.ModelRequestMessage;
import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.KeelRequest;
import de.iritgo.aktera.model.KeelResponse;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.Output;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.aktera.util.i18n.Message;
import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.DynaActionForm;
import org.apache.struts.upload.FormFile;
import org.apache.struts.util.MessageResources;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;


/**
 * The glue to connect Struts to Keel
 *
 * @version $Revision: 1.18 $ $Date: 2006/09/20 15:57:14 $
 * @author Michael Nash
 * @author Schatterjee Created on May 24, 2003
 */
public class StrutsClientConnector extends AbstractWebappClientConnector
{
	/**
	 * Write Buffer for writing files.
	 */
	private final static int BUFFER_SIZE = 1024 * 1024; // 1K * 1K = 1MB

	private HttpServletRequest hreq = null;

	private HttpServletResponse hres = null;

	private ActionServlet servlet = null;

	private ActionForm form = null;

	public StrutsClientConnector()
	{
	}

	public StrutsClientConnector(HttpServletRequest hreq, HttpServletResponse hres, ActionForm form,
					ActionServlet servlet)
	{
		this.hreq = hreq;
		this.hres = hres;
		this.form = form;
		this.servlet = servlet;
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappClientConnector#execute(de.iritgo.aktera.clients.webapp.WebappRequest,
	 *      de.iritgo.aktera.clients.webapp.WebappResponse, java.lang.String)
	 */
	public KeelResponse execute() throws ClientException, ModelException
	{
		WebappRequest wreq = new DefaultWebappRequest(hreq);
		WebappResponse wres = new DefaultWebappResponse(hres);
		String model = hreq.getParameter("model");
		KeelResponse kres = super.execute(wreq, wres, model);

		model = (String) kres.getAttribute("model");
		createDynaBean(kres, wreq, wres, model);
		handleErrors(kres, wreq, wres, model);

		return kres;
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.WebappClientConnector#getForward()
	 */
	public String getForward(KeelResponse kres)
	{
		String fwd = hreq.getParameter("forward");

		if (fwd == null)
		{
			fwd = (String) kres.getAttribute("forward");
		}

		if (fwd == null)
		{
			if (log.isWarnEnabled())
			{
				log.warn("Request '" + hreq.getRequestURL() + "?" + hreq.getQueryString()
								+ "' had no 'forward' attribute - returning default forward");
			}

			fwd = "default";
		}

		return fwd;
	}

	/**
	 * <Replace with description for createDynaBean>
	 *
	 * @param kres
	 * @param wres
	 */
	protected void createDynaBean(KeelResponse kres, WebappRequest wreq, WebappResponse wres, String modelName)
		throws ClientException
	{
		/* Populate the response context with the elements from */
		/* the response */
		ArrayList inputs = new ArrayList();
		ArrayList outputs = new ArrayList();
		ArrayList commands = new ArrayList();
		Iterator allElements = kres.getAll();

		/* If we've got nothing but errors, give up now... */
		if (! allElements.hasNext())
		{
			log.error("No elements in response from server");
		}

		/**
		 * Now attempt to Internationalize the response. This consists of
		 * looking through the response and translating any "Message" objects we
		 * find, as well as any strings that begin with "$". To begin a string
		 * with "$" and *not* have it internationalized, just use "$$". This
		 * will be converted to a single "$" for display. Once we've done
		 * internationalization, create a DynaBean from the response.
		 */
		MessageResources messages = getMessageResources(kres, wreq, wres, modelName);
		ResponseElement re = null;

		for (Iterator i = allElements; i.hasNext();)
		{
			re = (ResponseElement) i.next();

			internationalize(re, messages);

			/* Now make a dynabean for the given element */
			ResponseElementDynaBean reAsBean = new ResponseElementDynaBean(re);

			wreq.setAttribute(re.getName(), reAsBean);

			if (re instanceof Input)
			{
				inputs.add(reAsBean);
			}
			else if (re instanceof Output)
			{
				// HACK: For the moment, if re is output file, write to stream
				// here, as not being written otherwise
				final String outputType = (String) re.getAttribute("type");

				if ((outputType != null) && outputType.equals("binary"))
				{
					// Binary data, so dump to output stream now....
					log.debug("File Data is available");

					final BinaryWrapper data = (BinaryWrapper) ((Output) re).getContent();

					//					hres.setContentLength(new
					//					Integer(((Long)re.getAttribute("ContentLength")).toString()).intValue());
					final long dataSize = data.getSize();

					if ((dataSize > 0) && (dataSize < Integer.MAX_VALUE))
					{
						// Have a valid content length.
						hres.setContentLength((int) data.getSize());
					}

					hres.setContentType(data.getContentType());
					hres.setHeader("Content-Disposition", (String) re.getAttribute("Content-Disposition"));

					//					String encodings = hreq.getHeader ("Accept-Encoding");
					BufferedOutputStream buffOut = null;

					try
					{
						// BUG #844574:Writing using GZip compression is very
						// slow
						// HACK: Disable GZip compression until speed/threading
						// issues are worked out.
						//						if (encodings != null && encodings.indexOf ("gzip") != -1)
						//						{
						//							log.info ("Writing data using GZip compression");
						//
						//							final OutputStream out = hres.getOutputStream ();
						//
						//							buffOut = new BufferedOutputStream(
						//									new GZIPOutputStream(out), BUFFER_SIZE);
						//							hres.setHeader ("Content-Encoding", "gzip");
						//						}
						//						else
						//						{
						log.info("Writing data with no compression");

						OutputStream out = hres.getOutputStream();

						buffOut = new BufferedOutputStream(out, BUFFER_SIZE);
						//						}
						data.writeTo(buffOut);
						log.trace("Wrote Buffer.");
					}
					catch (IOException e)
					{
						e.printStackTrace();
						log.error("Exception during file read/write:", e);
						throw new ClientException("Exception during file read/write", e);
					}
					finally
					{
						// Flush all streams, and close input streams
						try
						{
							data.close();
						}
						catch (IOException e1)
						{
							e1.printStackTrace();
						}

						try
						{
							buffOut.flush();
						}
						catch (IOException e2)
						{
							e2.printStackTrace();
						}

						// Do NOT close the Output stream here, as the
						// underlying output stream is/should be closed later.
					}
				}
				else
				{
					outputs.add(reAsBean);
				}
			}
			else if (re instanceof Command)
			{
				commands.add(reAsBean);
			}
		}

		wreq.setAttribute("inputs", inputs);
		wreq.setAttribute("outputs", outputs);
		wreq.setAttribute("commands", commands);

		int inputCount = 0;
		DynaProperty[] dps = new DynaProperty[inputs.size()];
		ResponseElementDynaBean oneInput = null;

		for (Iterator ii = inputs.iterator(); ii.hasNext();)
		{
			oneInput = (ResponseElementDynaBean) ii.next();

			Object defValue = oneInput.get("defaultValue");
			DynaProperty dp = null;

			if (defValue != null)
			{
				dp = new DynaProperty((String) oneInput.get("name"), oneInput.get("defaultValue").getClass());
			}
			else
			{
				try
				{
					dp = new DynaProperty((String) oneInput.get("name"), Class.forName("java.lang.String"));
				}
				catch (ClassNotFoundException e)
				{
					throw new ClientException("Cannot create String dynaproperty", e);
				}
			}

			dps[inputCount++] = dp;
		}

		BasicDynaClass bd;

		try
		{
			bd = new BasicDynaClass(modelName, Class.forName("org.apache.commons.beanutils.BasicDynaBean"), dps);

			BasicDynaBean newForm = (BasicDynaBean) bd.newInstance();

			// Now populate the newForm's properties
			for (Iterator i2 = inputs.iterator(); i2.hasNext();)
			{
				oneInput = (ResponseElementDynaBean) i2.next();
				newForm.set((String) oneInput.get("name"), oneInput.get("defaultValue"));
			}

			wreq.setAttribute("default", newForm);
		}
		catch (ClassNotFoundException e)
		{
			throw new ClientException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new ClientException(e);
		}
		catch (InstantiationException e)
		{
			throw new ClientException(e);
		}
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.AbstractWebappClientConnector#preProcessParamName(java.lang.String)
	 */
	protected String preProcessParamName(String oneParamName)
	{
		// Added by Brian Rosenthal - 01/29/03
		// Using input type="image" causes some erratic behavior. This check is
		// a work around.
		if ((oneParamName.endsWith(".x")) || (oneParamName.endsWith(".y")))
		{
			oneParamName = oneParamName.substring(0, oneParamName.length() - 2);
		}

		return oneParamName;
	}

	/**
	 * @see de.iritgo.aktera.clients.webapp.AbstractWebappClientConnector#setKeelRequestParameters(javax.servlet.http.HttpServletRequest,
	 *      de.iritgo.aktera.model.KeelRequest)
	 */
	protected void setRequestParameters(WebappRequest wreq, KeelRequest kreq) throws ClientException
	{
		super.setRequestParameters(wreq, kreq);

		setRequestSource(wreq, kreq);
		setRequestHeaders(wreq, kreq);
		setRequestLocale(wreq, kreq);

		kreq.setScheme(wreq.getScheme());
		kreq.setServerName(wreq.getServerName());
		kreq.setServerPort(wreq.getServerPort());
		kreq.setContextPath(wreq.getContextPath());
		kreq.setRequestUrl(wreq.getRequestURL());
		kreq.setQueryString(wreq.getQueryString());

		if (form != null)
		{
			if (form instanceof DynaActionForm)
			{
				// Check that we have a file upload request
				final boolean isMultipart = ServletFileUpload.isMultipartContent(hreq);

				if (isMultipart)
				{
					log.debug("MultipartContent Form...");
					// Parse any files for uploading.
					// Obtain the max file size allowed for upload
					// Required to filter out large files right on the struts
					// client side
					// String useSize = wreq.getParameter("max_file_size");
					// int maxSize = 4 * 1024 * 1024;
					// //BAD idea, as this has already been called and read the stuff....
					// //final DiskFileUpload dfu = new DiskFileUpload();
					// if ((useSize != null) && (useSize.length()>0)) {
					// try{
					// final int intUseSize = new Integer(useSize).intValue();
					// if (intUseSize > 0){
					// //TODO: Set the Apache Module Config Max File size,
					// //BEFORE the FormFile object is created...
					// maxSize = intUseSize * 1024 * 1024;
					// } else if (intUseSize == -1){
					// maxSize = -1; //No upper limit
					// }
					// } catch(NumberFormatException nfe){
					// throw new ClientException(nfe.getMessage());
					// }
					// } else {
					// //By default 4 Megs allowed - already set.
					// }
					log.debug("Starting request parse...");

					DynaActionForm df = (DynaActionForm) form;
					DynaClass dc = df.getDynaClass();

					if (dc == null)
					{
						throw new ClientException("Null dynaclass from the DynaActionForm - can't read properties");
					}

					DynaProperty[] props = dc.getDynaProperties();
					DynaProperty oneProp = null;

					if (log.isDebugEnabled())
					{
						// They are not available in Struts Dynaform objects.
						for (final Enumeration enumeration = this.hreq.getParameterNames(); enumeration
										.hasMoreElements();)
						{
							// get parameter name
							// Get paramater values...
							// See if present in form.
							final String name = (String) enumeration.nextElement();

							try
							{
								for (int idx = 0;; idx++)
								{
									final Object value = df.get(name, idx);

									log.debug("Array Access Parameter/Value/Index: " + name + '/' + value + '/' + idx);
								}
							}
							catch (Exception e)
							{
								log.debug("Exception: " + e);
								log.debug("No more values for: " + name);
							}
						}

						for (final Enumeration enumeration = hreq.getParameterNames(); enumeration.hasMoreElements();)
						{
							final String name = (String) enumeration.nextElement();
							final String[] values = hreq.getParameterValues(name);

							log.debug("Servlet Parameter name: " + name);
							log.debug("Number of values: " + values.length);

							for (int idx = 0; idx < values.length; idx++)
							{
								log.debug("Idx/Value: " + idx + '/' + values[idx]);
							}
						}

						log.debug("# of properties: " + props.length);
					}

					for (int i = 0; i < props.length; i++)
					{
						oneProp = props[i];

						String oneName = oneProp.getName();
						final Object value = df.get(oneName);

						log.debug("Getting parameter/value/type:" + oneName + '/' + value + '/'
										+ (value == null ? "null" : value.getClass().getName()));

						// TODO: Handle mapped and indexed properties here
						// getName(), getType, isIndexed(), isMapped()
						if (df.get(oneName) != null && df.get(oneName) instanceof FormFile)
						{
							log.debug("Formfile");

							FormFile fileInfo = (FormFile) df.get(oneName);

							if (fileInfo != null && fileInfo.getFileSize() > 0)
							{
								BufferedInputStream inStream = null;

								try
								{
									inStream = new BufferedInputStream(fileInfo.getInputStream(), BUFFER_SIZE);
								}
								catch (IOException e)
								{
									throw new ClientException(e.getMessage(), e);
								}

								// Problem - DefaultFileItem is not comprised
								// completely of serializable components.
								// final FileItem fileItem =
								// DFIF.createItem(oneName,
								// fileInfo.getContentType(), false,
								// fileInfo.getFileName());
								// So we use BinaryWrapper instead, which is
								// completely serializable
								// TODO: Reset First parameter with Context Name
								// so that this will work over distributed
								// environments(I could not find the call to get
								// it - SPD)
								final BinaryWrapper fileWrapper = new BinaryWrapper(null, hreq.getContentType(),
												fileInfo.getFileName(), BUFFER_SIZE, null);

								try
								{
									final long written = fileWrapper.writeFrom(inStream);

									if (this.log.isDebugEnabled())
									{
										log.debug("Read/Wrote " + written + "bytes.");
									}
								}
								catch (IOException e)
								{
									throw new ClientException(e.getMessage(), e);
								}
								finally
								{
									if (inStream != null)
									{
										try
										{
											inStream.close();
										}

										// Done Anyways, so just print for log
										// (hopefully) and carry on.
										catch (IOException e)
										{
											e.printStackTrace();
										}
									} // end if(inStream

									try
									{
										fileWrapper.close();
									}

									// Done Anyways, so just print for log
									// (hopefully) and carry on.
									catch (IOException e)
									{
										e.printStackTrace();
									}
								} // end finally

								if (log.isDebugEnabled())
								{
									log.debug("Setting FormFile parameter/value:" + oneName + '/'
													+ fileWrapper.getName());
								}

								kreq.setParameter(oneName, fileWrapper);
							} // end if (fileInfo!= null..
						} // end if(df.get(oneName)!=null
						else if (df.get(oneName) != null && df.get(oneName) instanceof java.io.Serializable)
						{
							if (log.isDebugEnabled())
							{
								log.debug("Setting FormField parameter/value:" + oneName + '/' + df.get(oneName));
							}

							// TODO: Fix this.
							// BUG # 880906 Need to get value(s) directly from
							// request.
							final String[] values = hreq.getParameterValues(oneName);

							if (values.length < 1)
							{
								log.debug("No values, so setting value=null");
								kreq.setParameter(oneName, null);
							}
							else if (values.length == 1)
							{
								log.debug("One value, saving as string");
								kreq.setParameter(oneName, values[1]);
							}
							else
							{
								// More than one value in this list, so send the
								// entire array
								log.debug("Many values, saving as array");
								kreq.setParameter(oneName, values);
							}
						} // end else if(df.get(oneName)!=null...instanceof
						// Serializable

						log.debug("Name/Value written to request: " + oneName);
					} // end for
				}
				else
				{
					// isMultipart == false - Just a regular dynaform
					log.debug("Standard Dyna Form...");

					// Populate the model parameters from the form
					DynaActionForm df = (DynaActionForm) form;
					DynaClass dc = df.getDynaClass();

					if (dc == null)
					{
						throw new ClientException("Null dynaclass from the DynaActionForm - can't read properties");
					}

					DynaProperty[] props = dc.getDynaProperties();
					DynaProperty oneProp = null;

					for (int i = 0; i < props.length; i++)
					{
						oneProp = props[i];

						String oneName = oneProp.getName();
						final Object value = df.get(oneName);

						if (log.isDebugEnabled())
						{
							log.debug("Getting parameter/value/type:" + oneName + '/' + value + '/'
											+ (value == null ? "null" : value.getClass().getName()));
						}

						// TODO: Handle mapped and indexed properties here
						// getName(), getType, isIndexed(), isMapped()
						// if(df.get(oneName)!=null && df.get(oneName) instanceof FormFile){
						//
						// FormFile fileInfo = (FormFile)df.get(oneName);
						// if(fileInfo!=null && fileInfo.getFileSize()>0){
						// //BUG #819550: Large file uploads mishandled
						// //Bad things will happen here if somebody tries to upload
						// //a GB+ file. This is normal in industries like Media
						// //Broadcast. - SPD Oct 7/2003
						// //TODO: Where does new FileSerializable object live?
						// //TODO: A new Serializable object needs to be created as
						// //specified in the bug report. As this object will NOT
						// //be struts specific, where it lives will need to be
						// //determined. - SPD Oct 7/2003
						// //TODO: Investigate using Struts FileUpload here.
						// ByteArrayOutputStream baos = new ByteArrayOutputStream(0);
						// InputStream stream = null;
						// try{
						// stream = fileInfo.getInputStream();
						// } catch(IOException ioe){
						// throw new ClientException(ioe.getMessage());
						// }
						// byte[] data = null;
						// //only write files out that are allowed by max_file_size param
						// double allowed_file_size = 0;
						// try{
						// allowed_file_size = (new Integer(max_file_size).intValue()) * 1024000;
						// } catch(NumberFormatException nfe){
						// throw new ClientException(nfe.getMessage());
						// }
						// if (fileInfo.getFileSize() < allowed_file_size) {
						//
						// byte[] buffer = new byte[8192];
						// int bytesRead = 0;
						// try{
						// while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
						// baos.write(buffer, 0, bytesRead);
						// }
						// } catch(IOException ioe){
						// throw new ClientException(ioe.getMessage());
						// }
						// data = baos.toByteArray();
						//
						// //HACK: Temporary fix until bug #819550 gets fixed.
						// //Then file object can be queried on file name, and will
						// //not need to embed in attribute key.
						// kreq.setParameter(oneName + "_file_data_"+ fileInfo.getFileName(),data);
						// kreq.setParameter("mimetype_"+
						// fileInfo.getFileName(),fileInfo.getContentType());
						// if(wreq.getParameter("file_desc_"+ oneName) != null){
						// kreq.setParameter(oneName + "_file_desc_"+
						// fileInfo.getFileName(),wreq.getParameter("file_desc_"+ oneName));
						// }
						// }
						// else {
						// String dataStr = new String("The file \""+fileInfo.getFileName()+"\" is
						// greater than "+max_file_size+"MB in size, " +
						// " and has not been written to stream." +
						// " File Size: " + fileInfo.getFileSize() + " bytes. ");
						//
						// kreq.setParameter("file_data_error",dataStr);
						// }//end else
						// } //end if (fileInfo!=null...
						// }//end if(dg.get(oneName)!=null...instanceof FormFile

						/* else */if (df.get(oneName) != null && df.get(oneName) instanceof java.io.Serializable)
						{
							if (log.isDebugEnabled())
							{
								log.debug("Setting parameter/value:" + oneName + '/' + df.get(oneName));
							}

							kreq.setParameter(oneName, df.get(oneName));
						} // end else if(df.get(oneName)!=null...instanceof
						// Serializable
					} // end for
				} // end else just a regular DynaForm
			}
			else
			{
				log.debug("Standard Form...");

				/*
				 * Use introspection to get the parameters from a normal form
				 * bean
				 */
				PropertyDescriptor[] pd = PropertyUtils.getPropertyDescriptors(form);
				PropertyDescriptor oneDescriptor = null;
				String onePropertyName = null;

				for (int i = 0; i < pd.length; i++)
				{
					oneDescriptor = pd[i];
					onePropertyName = oneDescriptor.getName();

					try
					{
						kreq.setParameter(onePropertyName, PropertyUtils.getProperty(form, onePropertyName));
					}
					catch (IllegalAccessException e)
					{
						throw new ClientException(e);
					}
					catch (InvocationTargetException e)
					{
						throw new ClientException(e);
					}
					catch (NoSuchMethodException e)
					{
						throw new ClientException(e);
					}
				}
			}
		}
	}

	protected void handleErrors(KeelResponse kres, WebappRequest wreq, WebappResponse wres, String modelName)
	{
		/** Deal with any errors from the model execution */
		MessageResources messages = getMessageResources(kres, wreq, wres, modelName);

		Map errors = kres.getErrors();
		StringBuffer fatalMessage = new StringBuffer();

		if (errors.size() > 0)
		{
			String oneKey = null;
			String oneErrorMessage = null;
			String translatedMessage = null;

			ActionErrors ae = new ActionErrors();

			for (Iterator ei = errors.keySet().iterator(); ei.hasNext();)
			{
				oneKey = ei.next().toString();

				Object o = errors.get(oneKey);

				if (o != null)
				{
					oneErrorMessage = o.toString();
				}
				else
				{
					oneErrorMessage = "No error message provided";
				}

				if (oneErrorMessage.startsWith("\n"))
				{
					oneErrorMessage = oneErrorMessage.substring(1);
				}

				translatedMessage = translateString(oneErrorMessage, messages);

				fatalMessage.append(translatedMessage + "\n");

				String t = kres.getStackTrace(oneKey);

				if (t != null)
				{
					log.debug("Exception for error '" + oneKey + "' (" + translatedMessage + ")\n" + t);
					fatalMessage.append(t + "\n");
				}

				// if (t != null) {
				// log.error(
				// "Stack for error '"
				// + oneKey
				// + "' ("
				// + translatedMessage
				// + ")\n"
				// + t);
				// fatalMessage.append(t + "\n");
				// } else {
				// log.error(
				// "No stack for error '"
				// + oneKey
				// + "' ("
				// + translatedMessage
				// + ")");
				// }
				String errorType = kres.getErrorType(oneKey);

				if (errorType != null)
				{
					if (errorType.equals("java.lang.SecurityException"))
					{
						throw new SecurityException(translatedMessage);
					}
					else if (errorType.equals("de.iritgo.aktera.permissions.PermissionException"))
					{
						throw new PermissionException(translatedMessage);
					}
				}

				/*
				 * Note that the string we put in the message itself is *not*
				 * the translated error message, but the original. If there's a
				 * "$" in front of it, we chop it off. This means the
				 * <html:errors/> on your JSP must specify the correct bundle
				 * for translation if these error messages are in the
				 * application-specific message bundle. E.g. for poll, you
				 * probably want <html:errors bundle="poll"/>
				 */
				if (oneErrorMessage.startsWith("$") && (! oneErrorMessage.startsWith("$$")))
				{
					oneErrorMessage = oneErrorMessage.substring(1);
				}
				else
				{
					if (log.isDebugEnabled())
					{
						log.debug("WARNING: Non-internationalized message '" + oneErrorMessage
										+ "' added to ActionErrors. <html:errors/> may not work correctly");
					}
				}

				ActionMessage oneError = null;
				int pipePosition = oneErrorMessage.indexOf("|");

				if (pipePosition >= 0)
				{
					oneError = new ActionMessage(oneErrorMessage.substring(0, pipePosition),
									makeArgArray(oneErrorMessage));
				}
				else
				{
					oneError = new ActionMessage(oneErrorMessage);
				}

				ae.add(oneKey, oneError);
			}

			wreq.setAttribute(Globals.ERROR_KEY, ae);
		}
	}

	/**
	 * @param oneErrorMessage
	 * @return
	 */
	private Object[] makeArgArray(String msg)
	{
		ArrayList args = new ArrayList(3);

		int pipeStart = msg.indexOf("|");
		int pipeEnd = - 1;

		while (pipeStart >= 0)
		{
			String oneArg = null;

			pipeEnd = msg.indexOf("|", pipeStart + 1);

			if (pipeEnd >= 0)
			{
				oneArg = msg.substring(pipeStart + 1, pipeEnd);
			}
			else
			{
				oneArg = msg.substring(pipeStart + 1);
			}

			args.add(oneArg);
			pipeStart = pipeEnd;
		}

		return args.toArray();
	}

	private ActionServlet getServlet()
	{
		return servlet;
	}

	protected MessageResources getMessageResources(KeelResponse kres, WebappRequest wreq,
					@SuppressWarnings("unused") WebappResponse wres, String modelName)
	{
		MessageResources appMessages = (MessageResources) wreq.getAttribute(Globals.MESSAGES_KEY);

		if (appMessages.getReturnNull())
		{
			appMessages.setReturnNull(false);
		}

		/**
		 * Determine if there is an application-specific message bundle
		 * available. Ordinarily, we are using the default
		 * "ApplicationResources.properties" bundle, but if there is a bundle
		 * identified with the "key" of the application, we use it instead. E.g.
		 * for models beginning with "poll.", we look for a bundle under the key
		 * "poll". The model itself may override this by supplying a response
		 * attribute called "bundle" - this is used as the bundle key instead if
		 * it is present.
		 */
		String modelBundle = (String) kres.getAttribute("bundle");

		if (modelBundle == null)
		{
			if (modelName.indexOf(".") > 0)
			{
				String appName = modelName.substring(0, modelName.indexOf("."));
				MessageResources newMessages = (MessageResources) getServlet().getServletContext()
								.getAttribute(appName);

				if (newMessages != null)
				{
					appMessages = newMessages;

					if (log.isDebugEnabled())
					{
						log.debug("Application-specific message bundle for model '" + modelName + "' found under key '"
										+ appName + "'");
					}
				}
				else
				{
					if (log.isDebugEnabled())
					{
						log.debug("No application-specific message bundle for model '" + modelName
										+ "' found under key '" + appName + "'");
					}
				}
			}
		}
		else
		{
			MessageResources newMessages = (MessageResources) getServlet().getServletContext()
							.getAttribute(modelBundle);

			if (newMessages != null)
			{
				appMessages = newMessages;

				log.warn("Model specified message bundle '" + modelBundle + ", but no bundle was found under that key");
			}
		}

		appMessages.setReturnNull(false);

		return appMessages;
	}

	protected String translateString(String orig, MessageResources messages)
	{
		if (orig == null)
		{
			return null;
		}

		messages.setReturnNull(true);

		if (orig.startsWith("$$"))
		{
			return orig.substring(1);
		}

		if (orig.startsWith("$"))
		{
			if (orig.indexOf("|") > 0)
			{
				String argString = orig.substring(orig.indexOf("|") + 1);
				Object[] args = tokenize(argString, messages);
				String key = orig.substring(1, orig.indexOf("|"));
				String xlatedMsg = messages.getMessage(key, args);

				if (xlatedMsg == null)
				{
					// log.warn(
					// "Unable to translate message '"
					// + key
					// + "' with arguments '"
					// + argString
					// + "' using bundle '"
					// + messages.getConfig()
					// + "'");
					return orig;
				}

				return xlatedMsg;
			}

			String xlated = messages.getMessage(orig.substring(1));

			if (xlated == null)
			{
				// log.warn(
				// "Unable to translate message '"
				// + orig.substring(1)
				// + "' using bundle '"
				// + messages.getConfig()
				// + "'");
				return orig;
			}

			return xlated;
		}

		return orig;
	}

	/**
	 * Utility method to translate a message. The result is stored back in the
	 * Message object itself. The Message may or may not specify a message
	 * bundle key to be used for the translation. If it does not, we use the
	 * application-specific bundle we would normally use for a string instead.
	 *
	 * @returns The same Message object, but with the translated string set as
	 *          it's result string. Calling toString on this message would now
	 *          return the translated result.
	 */
	private Message translateMessage(Message message, MessageResources messages)
	{
		MessageResources useMessages = messages;

		if (message.getBundle() != null)
		{
			useMessages = (MessageResources) getServlet().getServletContext().getAttribute(message.getBundle());

			if (useMessages == null)
			{
				useMessages = messages;

				log.warn("Message '" + message.getKey() + "' specified message bundle '" + message.getBundle()
								+ ", but no bundle was found under that key");
			}
		}

		/* Now translate any values within this message that also */
		/* require translation */
		Object[] values = message.getValues();

		for (int i = 0; i < values.length; i++)
		{
			if (values[i] instanceof String)
			{
				String oneString = (String) values[i];

				values[i] = translateString(oneString, useMessages);
			}
		}

		message.setValues(values);
		message.setResultString(useMessages.getMessage(message.getKey(), message.getValues()));

		return message;
	}

	protected void internationalize(ResponseElement re, MessageResources messages)
	{
		/* Check for nested elements, internationalize each one */
		ResponseElement oneNested = null;

		for (Iterator i = re.getAll().iterator(); i.hasNext();)
		{
			oneNested = (ResponseElement) i.next();
			internationalize(oneNested, messages);
		}

		/*
		 * Check for attributes that are strings beginning with "$" or Message
		 * objects, or ResponseElements themselves
		 */
		String oneAttribKey = null;
		Object oneAttrib = null;
		Map attribs = re.getAttributes();

		for (Iterator ia = attribs.keySet().iterator(); ia.hasNext();)
		{
			oneAttribKey = (String) ia.next();
			oneAttrib = attribs.get(oneAttribKey);

			if (oneAttrib instanceof String)
			{
				String s = (String) oneAttrib;

				re.setAttribute(oneAttribKey, translateString(s, messages));
			}
			else if (oneAttrib instanceof Message)
			{
				Message m = (Message) oneAttrib;

				re.setAttribute(oneAttribKey, translateMessage(m, messages));
			}
			else if (oneAttrib instanceof ResponseElement)
			{
				internationalize((ResponseElement) oneAttrib, messages);
			}
		}

		if (re instanceof Input)
		{
			Input i = (Input) re;

			i.setLabel(translateString(i.getLabel(), messages));

			Map validValues = i.getValidValues();

			if (validValues != null)
			{
				TreeMap newMap = new TreeMap();
				String oneKey = null;
				Object oneValue = null;

				for (Iterator iv = validValues.keySet().iterator(); iv.hasNext();)
				{
					oneKey = iv.next().toString();
					oneValue = validValues.get(oneKey);

					if (oneValue instanceof String)
					{
						newMap.put(oneKey, translateString(oneValue.toString(), messages));
					}
					else
					{
						newMap.put(oneKey, oneValue);
					}
				}

				i.setValidValues(newMap);
			}
		}
		else if (re instanceof Output)
		{
			Output o = (Output) re;
			Object c = o.getContent();

			if (c instanceof String)
			{
				o.setContent(translateString((String) c, messages));
			}

			if (c instanceof Message)
			{
				o.setContent(translateMessage((Message) c, messages));
			}
		}
		else if (re instanceof Command)
		{
			Command c = (Command) re;

			c.setLabel(translateString(c.getLabel(), messages));
		}
	}

	protected Object[] tokenize(String orig, MessageResources messages)
	{
		ArrayList params = new ArrayList();
		StringTokenizer stk = new StringTokenizer(orig, "|");

		while (stk.hasMoreTokens())
		{
			params.add(stk.nextToken());
		}

		Object[] args = new Object[params.size()];
		Iterator ai = params.iterator();
		Object oneParam = null;

		for (int i = 0; i < params.size(); i++)
		{
			oneParam = ai.next();

			if ((oneParam != null) && (oneParam instanceof String))
			{
				args[i] = translateString(oneParam.toString(), messages);
			}
			else
			{
				args[i] = ai.next();
			}
		}

		return args;
	}

	public boolean allowed(String resource, String operation)
	{
		KeelRequest keelRequest = new ModelRequestMessage();

		keelRequest.setModel("security.authorization");
		keelRequest.setAttribute("sessionid", hreq.getSession().getId());
		keelRequest.setParameter("component", resource);

		if (operation != null)
		{
			keelRequest.setParameter("operation", operation);
		}

		try
		{
			KeelResponse kres = execute();

			return ((Boolean) kres.getAttribute("allowed")).booleanValue();
		}
		catch (ModelException e)
		{
			log.error("Unable to check authorization", e);
		}
		catch (ClientException e)
		{
			log.error("Unable to check authorization", e);
		}

		throw new RuntimeException("Unable to execute AuthorizationModel");
	}

	/**
	 */
	public KeelResponse execute(WebappRequest wreq, WebappResponse wres, String model)
		throws ClientException, ModelException
	{
		KeelResponse kres = super.execute(wreq, wres, model);

		return kres;
	}

	/***
	 */
	protected void setRequestHeaders(WebappRequest wreq, KeelRequest kreq)
	{
		String oneHeaderName = null;

		if (wreq.getHeaderNames() != null)
		{
			for (Enumeration e = wreq.getHeaderNames(); e.hasMoreElements();)
			{
				oneHeaderName = (String) e.nextElement();

				kreq.setHeader(oneHeaderName, wreq.getHeader(oneHeaderName));
			}
		}
	}

	/***
	 */
	protected void setRequestSource(WebappRequest wreq, KeelRequest kreq)
	{
		kreq.setSource(wreq.getSource());
	}

	/***
	 */
	protected void setRequestLocale(WebappRequest wreq, KeelRequest kreq)
	{
		kreq.setLocale(wreq.getLocale());
	}

	public void startClient() throws ModelException, ClientException, Exception
	{
		getClient().start();
	}

	public void stopClient() throws ModelException, ClientException, Exception
	{
		getClient().stop();
	}
}
