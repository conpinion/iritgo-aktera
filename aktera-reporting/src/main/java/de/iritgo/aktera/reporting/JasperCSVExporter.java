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

package de.iritgo.aktera.reporting;


import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JRPrintText;
import net.sf.jasperreports.engine.JRStyledTextAttributeSelector;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.CutsInfo;
import net.sf.jasperreports.engine.export.ExporterNature;
import net.sf.jasperreports.engine.export.JRCsvExporterNature;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRExportProgressMonitor;
import net.sf.jasperreports.engine.export.JRExporterGridCell;
import net.sf.jasperreports.engine.export.JRGridLayout;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.util.JRStyledText;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.StringTokenizer;


public class JasperCSVExporter extends JRAbstractExporter
{
	private static final String CSV_EXPORTER_PROPERTIES_PREFIX = JRProperties.PROPERTY_PREFIX + "export.csv.";

	protected String delimiter = null;

	protected String recordDelimiter = null;

	protected Writer writer = null;

	protected JRExportProgressMonitor progressMonitor = null;

	protected ExporterNature nature = null;

	public void exportReport () throws JRException
	{
		progressMonitor = (JRExportProgressMonitor) parameters.get (JRExporterParameter.PROGRESS_MONITOR);

		/*   */
		setOffset ();

		/*   */
		setInput ();

		if (! parameters.containsKey (JRExporterParameter.FILTER))
		{
			filter = createFilter (CSV_EXPORTER_PROPERTIES_PREFIX);
		}

		/*   */
		if (! isModeBatch)
		{
			setPageRange ();
		}

		nature = new JRCsvExporterNature (filter);

		String encoding = getStringParameterOrDefault (JRExporterParameter.CHARACTER_ENCODING,
						JRExporterParameter.PROPERTY_CHARACTER_ENCODING);

		delimiter = getStringParameterOrDefault (JRCsvExporterParameter.FIELD_DELIMITER,
						JRCsvExporterParameter.PROPERTY_FIELD_DELIMITER);

		recordDelimiter = getStringParameterOrDefault (JRCsvExporterParameter.RECORD_DELIMITER,
						JRCsvExporterParameter.PROPERTY_RECORD_DELIMITER);

		StringBuffer sb = (StringBuffer) parameters.get (JRExporterParameter.OUTPUT_STRING_BUFFER);

		if (sb != null)
		{
			try
			{
				writer = new StringWriter ();
				exportReportToWriter ();
				sb.append (writer.toString ());
			}
			catch (IOException e)
			{
				throw new JRException ("Error writing to StringBuffer writer : " + jasperPrint.getName (), e);
			}
			finally
			{
				if (writer != null)
				{
					try
					{
						writer.close ();
					}
					catch (IOException e)
					{
					}
				}
			}
		}
		else
		{
			writer = (Writer) parameters.get (JRExporterParameter.OUTPUT_WRITER);

			if (writer != null)
			{
				try
				{
					exportReportToWriter ();
				}
				catch (IOException e)
				{
					throw new JRException ("Error writing to writer '" + jasperPrint.getName () + "'", e);
				}
			}
			else
			{
				OutputStream os = (OutputStream) parameters.get (JRExporterParameter.OUTPUT_STREAM);

				if (os != null)
				{
					try
					{
						writer = new OutputStreamWriter (os, encoding);
						exportReportToWriter ();
					}
					catch (IOException e)
					{
						throw new JRException ("Error writing to OutputStream writer : " + jasperPrint.getName (), e);
					}
				}
				else
				{
					File destFile = (File) parameters.get (JRExporterParameter.OUTPUT_FILE);

					if (destFile == null)
					{
						String fileName = (String) parameters.get (JRExporterParameter.OUTPUT_FILE_NAME);

						if (fileName != null)
						{
							destFile = new File (fileName);
						}
						else
						{
							throw new JRException ("No output specified for the exporter.");
						}
					}

					try
					{
						os = new FileOutputStream (destFile);
						writer = new OutputStreamWriter (os, encoding);
						exportReportToWriter ();
					}
					catch (IOException e)
					{
						throw new JRException ("Error writing to file writer : " + jasperPrint.getName (), e);
					}
					finally
					{
						if (writer != null)
						{
							try
							{
								writer.close ();
							}
							catch (IOException e)
							{
							}
						}
					}
				}
			}
		}
	}

	/**
	 *
	 */
	protected void exportReportToWriter () throws JRException, IOException
	{
		for (int reportIndex = 0; reportIndex < jasperPrintList.size (); reportIndex++)
		{
			jasperPrint = (JasperPrint) jasperPrintList.get (reportIndex);

			List pages = jasperPrint.getPages ();

			if (pages != null && pages.size () > 0)
			{
				if (isModeBatch)
				{
					startPageIndex = 0;
					endPageIndex = pages.size () - 1;
				}

				for (int i = startPageIndex; i <= endPageIndex; i++)
				{
					if (Thread.currentThread ().isInterrupted ())
					{
						throw new JRException ("Current thread interrupted.");
					}

					JRPrintPage page = (JRPrintPage) pages.get (i);

					/*   */
					exportPage (page);
				}
			}
		}

		writer.flush ();
	}

	/**
	 *
	 */
	protected void exportPage (JRPrintPage page) throws IOException
	{
		JRGridLayout layout = new JRGridLayout (nature, page.getElements (), jasperPrint.getPageWidth (), jasperPrint
						.getPageHeight (), globalOffsetX, globalOffsetY, null //address
		);

		JRExporterGridCell[][] grid = layout.getGrid ();

		CutsInfo xCuts = layout.getXCuts ();
		CutsInfo yCuts = layout.getYCuts ();

		StringBuffer rowbuffer = null;

		JRPrintElement element = null;
		String text = null;
		boolean isFirstColumn = true;

		for (int y = 0; y < grid.length; y++)
		{
			rowbuffer = new StringBuffer ();

			if (yCuts.isCutNotEmpty (y))
			{
				isFirstColumn = true;

				for (int x = 0; x < grid[y].length; x++)
				{
					if (grid[y][x].getWrapper () != null)
					{
						element = grid[y][x].getWrapper ().getElement ();

						if (element instanceof JRPrintText)
						{
							JRStyledText styledText = getStyledText ((JRPrintText) element);

							if (styledText == null)
							{
								text = "";
							}
							else
							{
								text = styledText.getText ();
							}

							if (! isFirstColumn)
							{
								rowbuffer.append (delimiter);
							}

							rowbuffer.append (prepareText (text));
							isFirstColumn = false;
						}
					}
					else
					{
						if (xCuts.isCutNotEmpty (x))
						{
							if (! isFirstColumn)
							{
								rowbuffer.append (delimiter);
							}

							isFirstColumn = false;
						}
					}
				}

				if (rowbuffer.length () > 0)
				{
					writer.write (rowbuffer.toString ());
					writer.write (recordDelimiter);
				}
			}
		}

		if (progressMonitor != null)
		{
			progressMonitor.afterPageExport ();
		}
	}

	/**
	 *
	 */
	protected JRStyledText getStyledText (JRPrintText textElement)
	{
		return textElement.getFullStyledText (JRStyledTextAttributeSelector.NONE);
	}

	/**
	 *
	 */
	protected String prepareText (String source)
	{
		String str = null;

		if (source != null)
		{
			boolean putQuotes = false;

			if (source.indexOf (delimiter) >= 0 || source.indexOf (recordDelimiter) >= 0)
			{
				putQuotes = true;
			}

			StringBuffer sbuffer = new StringBuffer ();
			StringTokenizer tkzer = new StringTokenizer (source, "\"\n", true);
			String token = null;

			while (tkzer.hasMoreTokens ())
			{
				token = tkzer.nextToken ();

				if ("\"".equals (token))
				{
					putQuotes = true;
					sbuffer.append ("\"\"");
				}
				else if ("\n".equals (token))
				{
					//sbuffer.append(" ");
					putQuotes = true;
					sbuffer.append ("\n");
				}
				else
				{
					sbuffer.append (token);
				}
			}

			str = sbuffer.toString ();

			if (putQuotes)
			{
				str = "\"" + str + "\"";
			}
		}

		return str;
	}
}
