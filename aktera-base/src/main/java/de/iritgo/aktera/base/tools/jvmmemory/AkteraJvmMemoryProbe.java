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

package de.iritgo.aktera.base.tools.jvmmemory;


import de.iritgo.aktera.tools.FileTools;
import static org.rrd4j.ConsolFun.AVERAGE;
import static org.rrd4j.ConsolFun.MAX;
import static org.rrd4j.ConsolFun.MIN;
import org.rrd4j.DsType;
import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.graph.RrdGraph;
import org.rrd4j.graph.RrdGraphDef;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class AkteraJvmMemoryProbe
{
	private String name;

	private org.rrd4j.core.Sample probe;

	private RrdDb rrdDb;

	private long max;

	public AkteraJvmMemoryProbe (String name, long startTimestamp, long max)
	{
		this.name = name;
		this.max = max;
	}

	public void init (String name, long startTimestamp) throws IOException
	{
		File file = FileTools.newAkteraFile ("/var/aktera/jvm-memory/" + name + ".rrd");
		String fileName = file.getAbsolutePath ();

		if (file.exists ())
		{
			rrdDb = new RrdDb (fileName);
		}
		else
		{
			RrdDef rrdDef = new RrdDef (fileName, startTimestamp - 1, AkteraJvmMemoryManager.DATA_CAPTURE_INTERVAL);

			rrdDef.addDatasource ("Init", DsType.GAUGE, max, 0, Double.NaN);
			rrdDef.addDatasource ("Used", DsType.GAUGE, max, 0, Double.NaN);
			rrdDef.addDatasource ("Committed", DsType.GAUGE, max, 0, Double.NaN);
			rrdDef.addDatasource ("Max", DsType.GAUGE, max, 0, Double.NaN);
			rrdDef.addArchive (AVERAGE, 0.5, 1, 1440);
			rrdDef.addArchive (AVERAGE, 0.5, 30, 336);
			rrdDef.addArchive (AVERAGE, 0.5, 60, 744);
			rrdDef.addArchive (AVERAGE, 0.5, 1440, 336);
			rrdDef.addArchive (MIN, 0.5, 1, 1440);
			rrdDef.addArchive (MIN, 0.5, 30, 336);
			rrdDef.addArchive (MIN, 0.5, 60, 744);
			rrdDef.addArchive (MIN, 0.5, 1440, 336);
			rrdDef.addArchive (MAX, 0.5, 1, 1440);
			rrdDef.addArchive (MAX, 0.5, 30, 336);
			rrdDef.addArchive (MAX, 0.5, 60, 744);
			rrdDef.addArchive (MAX, 0.5, 1440, 336);
			rrdDb = new RrdDb (rrdDef);
		}

		probe = rrdDb.createSample ();
		rrdDb.close ();
	}

	public void startMeasurand (String name) throws IOException
	{
		File file = FileTools.newAkteraFile ("/var/aktera/jvm-memory/" + name + ".rrd");
		String fileName = file.getAbsolutePath ();

		rrdDb = new RrdDb (fileName);
		probe = rrdDb.createSample ();
	}

	public void measurand (String name, long time, long value) throws IOException
	{
		probe.setTime (time);
		probe.setValue (name, value);
	}

	public void commitMeasurand () throws IOException
	{
		probe.update ();
		rrdDb.close ();
	}

	public void close ()
	{
		try
		{
			rrdDb.close ();
		}
		catch (IOException x)
		{
		}
	}

	public void generateGraph (long startTimestamp, long stopTimestamp, BufferedImage bufferedImage) throws Exception
	{
		File file = FileTools.newAkteraFile ("/var/aktera/jvm-memory/" + name + ".rrd");
		String fileName = file.getAbsolutePath ();

		RrdGraphDef gDef = new RrdGraphDef ();

		gDef.setWidth (1024);
		gDef.setHeight (768);
		gDef.setFilename (fileName + ".png");
		gDef.setLazy (false);
		gDef.setStartTime (startTimestamp);
		gDef.setEndTime (stopTimestamp);
		gDef.setTitle (name);
		gDef.setVerticalLabel ("Memory");
		gDef.datasource ("Init", fileName, "Init", AVERAGE);
		gDef.datasource ("Used", fileName, "Used", AVERAGE);
		gDef.datasource ("Committed", fileName, "Committed", AVERAGE);
		gDef.datasource ("Max", fileName, "Max", AVERAGE);

		gDef.line ("Init", Color.GREEN, "Init memory");
		gDef.area ("Max", Color.RED, "Max memory\n");
		gDef.area ("Committed", Color.gray, "Committed memory");
		gDef.area ("Used", Color.GREEN, "Used memory");

		gDef.gprint ("Used", MAX, "used-max = %.3f%s");
		gDef.gprint ("Committed", MAX, "committed-max = %.3f%S\\r");
		gDef.gprint ("Max", MAX, "max = %.3f%S");
		gDef.gprint ("Used", AVERAGE, "used-avg = %.3f%S\\r");
		gDef.setImageInfo ("<img src='%s' width='%d' height = '%d'>");
		gDef.setPoolUsed (false);
		gDef.setImageFormat ("png");
		gDef.setAltAutoscaleMax (true);
		gDef.setAntiAliasing (true);

		// create graph finally
		RrdGraph graph = new RrdGraph (gDef);

		graph.render (bufferedImage.getGraphics ());
	}
}
