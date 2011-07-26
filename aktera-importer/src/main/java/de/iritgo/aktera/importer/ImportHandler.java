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

package de.iritgo.aktera.importer;


import de.iritgo.aktera.i18n.I18N;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import javax.xml.xpath.XPathExpressionException;
import java.io.PrintWriter;
import java.util.Properties;


/**
 * Import handlers need to implement this interface.
 */
public interface ImportHandler
{
	/**
	 * Analyze a given XML import file. This method generates output elements describing the
	 * contents of the import file.
	 *
	 * @param req A model request.
	 * @param doc The XML document.
	 * @param importElem The import element node.
	 * @param reporter A PrintWriter receiving the import messages.
	 * @param i18n I18N service.
	 * @return False in case of a failure.
	 */
	public boolean analyze (ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
					Properties properties) throws ModelException, XPathExpressionException;

	/**
	 * Perform the XML import.
	 *
	 * @param req A model request.
	 * @param doc The XML document.
	 * @param importElem The import element node.
	 * @param reporter A PrintWriter receiving the import messages.
	 * @param i18n I18N service.
	 * @param properties TODO
	 */
	public boolean perform (ModelRequest req, Document doc, Node importElem, PrintWriter reporter, I18N i18n,
					Properties properties) throws ModelException, XPathExpressionException;

	/**
	 * Called when the XML root tag was entered.
	 *
	 * @param uri Tag uri
	 * @param localName Tag local name
	 * @param name Tag name
	 * @param attributes Tag attributes
	 * @param reporter TODO
	 * @param properties TODO
	 */
	public void startRootElement (String uri, String localName, String name, Attributes attributes,
					PrintWriter reporter, Properties properties);

	/**
	 * Called when the XML root tag was left
	 *
	 * @param uri Tag uri
	 * @param localName Tag local name
	 * @param name Tag name
	 * @param reporter TODO
	 * @param properties TODO
	 * @throws SAXException In case of an error
	 */
	public void endRootElement (String uri, String localName, String name, PrintWriter reporter, Properties properties)
		throws SAXException;

	/**
	 * Called when a XML tag was entered.
	 *
	 * @param uri Tag uri
	 * @param localName Tag local name
	 * @param name Tag name
	 * @param attributes Tag attributes
	 * @param reporter TODO
	 * @param properties TODO
	 */
	public void startElement (String uri, String localName, String name, Attributes attributes, PrintWriter reporter,
					Properties properties);

	/**
	 * Called when a XML tag was left
	 *
	 * @param uri Tag uri
	 * @param localName Tag local name
	 * @param name Tag name
	 * @param reporter TODO
	 * @param properties TODO
	 * @throws SAXException In case of an error
	 */
	public void endElement (String uri, String localName, String name, PrintWriter reporter, Properties properties)
		throws SAXException;

	/**
	 * Called when a XML tag content was read.
	 * @param uri TODO
	 * @param localName Tag local name
	 * @param name TODO
	 * @param content The tag content
	 * @param reporter TODO
	 * @param properties TODO
	 * @throws SAXException In case of an error
	 */
	public void elementContent (String uri, String localName, String name, String content, PrintWriter reporter,
					Properties properties) throws SAXException;
}
