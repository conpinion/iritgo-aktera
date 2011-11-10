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

package de.iritgo.aktera.core.config;


import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * @author MNash
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class KeelErrorHandler implements ErrorHandler
{
	/**
	 * Constructor for KeelErrorHandler.
	 */
	public KeelErrorHandler()
	{
		super();
	}

	private void showError(String msg, SAXParseException arg0) throws SAXException
	{
		System.err.println(msg + " at Line " + arg0.getLineNumber() + ", column " + arg0.getColumnNumber() + " in "
						+ arg0.getSystemId());
		arg0.printStackTrace(System.err);
	}

	/**
	 * @see org.xml.sax.ErrorHandler#error(SAXParseException)
	 */
	public void error(SAXParseException arg0) throws SAXException
	{
		showError("Error", arg0);
	}

	/**
	 * @see org.xml.sax.ErrorHandler#fatalError(SAXParseException)
	 */
	public void fatalError(SAXParseException arg0) throws SAXException
	{
		showError("Fatal Error", arg0);
	}

	/**
	 * @see org.xml.sax.ErrorHandler#warning(SAXParseException)
	 */
	public void warning(SAXParseException arg0) throws SAXException
	{
		showError("Warning", arg0);
	}
}
