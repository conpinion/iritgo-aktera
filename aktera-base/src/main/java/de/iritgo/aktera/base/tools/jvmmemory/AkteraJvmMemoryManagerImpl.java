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


import de.iritgo.aktera.logger.Logger;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.scheduler.ScheduleRepeated;
import de.iritgo.aktera.scheduler.Scheduleable;
import de.iritgo.aktera.scheduler.Scheduler;
import de.iritgo.aktera.startup.ShutdownException;
import de.iritgo.aktera.startup.StartupException;
import de.iritgo.aktera.startup.StartupHandler;
import de.iritgo.aktera.tools.ModelTools;
import org.rrd4j.core.Util;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Jvm memory manager
 */
public class AkteraJvmMemoryManagerImpl implements AkteraJvmMemoryManager, Scheduleable, StartupHandler
{
	private Logger logger;

	private Scheduler scheduler;

	private HashMap<String, AkteraJvmMemoryProbe> probes = new HashMap<String, AkteraJvmMemoryProbe>();

	private int startTime = 1;

	public void schedule(Map parameters)
	{
		measurandRound(Util.getTime());
	}

	/**
	 * @param logger The new logger.
	 */
	public void setLogger(Logger logger)
	{
		this.logger = logger;
	}

	/**
	 * @param scheduler The new scheduler.
	 */
	public void setScheduler(Scheduler scheduler)
	{
		this.scheduler = scheduler;
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#startup()
	 */
	public void startup() throws StartupException
	{
		ModelRequest request = null;

		initRound();

		try
		{
			request = ModelTools.createModelRequest();

			logger.info("Aktera jvm memory monitor started...(Interval: " + DATA_CAPTURE_INTERVAL + ")");

			request = ModelTools.createModelRequest();
			scheduler.scheduleBean("AkteraJvmMemoryManager", AkteraJvmMemoryManager.ID, AkteraJvmMemoryManager.ID,
							null, new ScheduleRepeated().interval(DATA_CAPTURE_INTERVAL));
		}
		catch (Exception x)
		{
			logger.error("Aktera jvm memory monitor startup error: " + x);
		}
		finally
		{
			ModelTools.releaseModelRequest(request);
		}
	}

	public void initRound()
	{
		long start = Util.getTime();

		Iterator iter = ManagementFactory.getMemoryPoolMXBeans().iterator();

		while (iter.hasNext())
		{
			MemoryPoolMXBean item = (MemoryPoolMXBean) iter.next();
			String name = item.getName();

			try
			{
				AkteraJvmMemoryProbe probeUsage = new AkteraJvmMemoryProbe(name + "Usage", start, item.getUsage()
								.getMax());

				probeUsage.init(name + "Usage", start);
				probeUsage.startMeasurand(name + "Usage");
				probeUsage.measurand("Init", start, item.getUsage().getInit());
				probeUsage.measurand("Used", start, item.getUsage().getUsed());
				probeUsage.measurand("Committed", start, item.getUsage().getCommitted());
				probeUsage.measurand("Max", start, item.getUsage().getMax());
				probeUsage.commitMeasurand();
				probes.put(name + "Usage", probeUsage);

				AkteraJvmMemoryProbe probePeak = new AkteraJvmMemoryProbe(name + "Peak", start, item.getPeakUsage()
								.getMax());

				probePeak.init(name + "Peak", start);
				probePeak.startMeasurand(name + "Peak");
				probePeak.measurand("Init", start, item.getPeakUsage().getInit());
				probePeak.measurand("Used", start, item.getPeakUsage().getUsed());
				probePeak.measurand("Committed", start, item.getPeakUsage().getCommitted());
				probePeak.measurand("Max", start, item.getPeakUsage().getMax());
				probePeak.commitMeasurand();
				probes.put(name + "Peak", probePeak);

				if (item.getCollectionUsage() != null)
				{
					AkteraJvmMemoryProbe probeCollection = new AkteraJvmMemoryProbe(name + "Collection", start, item
									.getCollectionUsage().getMax());

					probeCollection.init(name + "Collection", start);
					probeCollection.startMeasurand(name + "Collection");
					probeCollection.measurand("Init", start, item.getCollectionUsage().getInit());
					probeCollection.measurand("Used", start, item.getCollectionUsage().getUsed());
					probeCollection.measurand("Committed", start, item.getCollectionUsage().getCommitted());
					probeCollection.measurand("Max", start, item.getCollectionUsage().getMax());
					probeCollection.commitMeasurand();
					probes.put(name + "Collection", probeCollection);
				}
			}
			catch (IOException x)
			{
				logger.debug("Error: " + x);
			}
		}
	}

	synchronized public void measurandRound(long time)
	{
		Iterator iter = ManagementFactory.getMemoryPoolMXBeans().iterator();

		while (iter.hasNext())
		{
			MemoryPoolMXBean item = (MemoryPoolMXBean) iter.next();
			String name = item.getName();

			try
			{
				AkteraJvmMemoryProbe probeUsage = probes.get(name + "Usage");

				if (probeUsage != null)
				{
					probeUsage.startMeasurand(name + "Usage");
					probeUsage.measurand("Init", time, item.getUsage().getInit());
					probeUsage.measurand("Used", time, item.getUsage().getUsed());
					probeUsage.measurand("Committed", time, item.getUsage().getCommitted());
					probeUsage.measurand("Max", time, item.getUsage().getMax());
					probeUsage.commitMeasurand();
				}

				AkteraJvmMemoryProbe probePeak = probes.get(name + "Peak");

				if (probePeak != null)
				{
					probePeak.startMeasurand(name + "Peak");
					probePeak.measurand("Init", time, item.getPeakUsage().getInit());
					probePeak.measurand("Used", time, item.getPeakUsage().getUsed());
					probePeak.measurand("Committed", time, item.getPeakUsage().getCommitted());
					probePeak.measurand("Max", time, item.getPeakUsage().getMax());
					probePeak.commitMeasurand();
				}

				AkteraJvmMemoryProbe probeCollection = probes.get(name + "Collection");

				if (probeCollection != null)
				{
					probeCollection.startMeasurand(name + "Collection");
					probeCollection.measurand("Init", time, item.getCollectionUsage().getInit());
					probeCollection.measurand("Used", time, item.getCollectionUsage().getUsed());
					probeCollection.measurand("Committed", time, item.getCollectionUsage().getCommitted());
					probeCollection.measurand("Max", time, item.getCollectionUsage().getMax());
					probeCollection.commitMeasurand();
				}
			}
			catch (IOException x)
			{
				x.printStackTrace();
			}
		}
	}

	/**
	 * @see de.iritgo.aktera.base.tools.jvmmemory.AkteraJvmMemoryManager#generateGraph(java.lang.String)
	 */
	public void generateGraph(String name, BufferedImage bufferedImage)
	{
		AkteraJvmMemoryProbe probe = probes.get(name);

		if (probe == null)
		{
			return;
		}

		try
		{
			probe.generateGraph(Util.getTime() - (startTime * 60), Util.getTime(), bufferedImage);
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}

	public void setStartTime(int startTime)
	{
		this.startTime = startTime;
	}

	/**
	 * @see de.iritgo.aktera.startup.StartupHandler#shutdown()
	 */
	public void shutdown() throws ShutdownException
	{
		scheduler.removeAllJobsInGroup(AkteraJvmMemoryManager.ID);
	}
}
