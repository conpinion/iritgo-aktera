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

package de.iritgo.aktera.models.util;


import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.Logger;


/**
 * A SequenceInfo is a class that lets you get information about all of the steps in a sequence.
 * The information that can be obtained is information such as each model name and the order
 * in which the models are called.  This information may be useful in some cases, for example,
 * if you wanted to dynamically create a side-bar navigation that displays the sequences, then you would need
 * information about the sequences and their steps to created it.
 *
 * This class provides a wrapper around a sequenceContext and methods for reading information from the sequenceContext.
 * This class was created to hide the complexity and ambiguity of the sequenceContext from programmers who are trying to
 * get some infromation about a sequence context.
 *
 * @version                $Revision: 1.1 $  $Date: 2003/12/29 07:09:30 $
 * @author                Philip Brown
 */
public class SequenceInfo implements LogEnabled
{
	private Logger theSequenceInfoLogger;

	private String sequenceName = null;

	private int seqStepNum = 0;

	private Configuration[] sequenceSteps = null;

	/**
	 * @param sc
	 */
	public SequenceInfo(SequenceContext sc)
	{
		this.sequenceSteps = sc.getSequenceSteps();
		this.seqStepNum = sc.getSeqStepNum();
		this.sequenceName = sc.getSequenceName();
	}

	public static SequenceInfo getSequenceInfo(Context context) throws ModelException
	{
		SequenceContext sc = SequenceContext.getSequenceContext(context);
		SequenceInfo sequenceInfo = new SequenceInfo(sc);

		return sequenceInfo;
	}

	public static SequenceInfo getSequenceInfo(ModelRequest req) throws ModelException
	{
		SequenceContext sc = SequenceContext.getSequenceContext(req);
		SequenceInfo sequenceInfo = new SequenceInfo(sc);

		return sequenceInfo;
	}

	public static Configuration getCurrentStep(ModelRequest req) throws ModelException
	{
		Configuration config = null;
		SequenceInfo si = getSequenceInfo(req);

		if (si != null)
		{
			config = si.getCurrentStep();
		}

		return config;
	}

	public static Configuration getCurrentStep(Context context) throws ModelException
	{
		Configuration config = null;
		SequenceInfo si = getSequenceInfo(context);

		if (si != null)
		{
			config = si.getCurrentStep();
		}

		return config;
	}

	/**
	 * Gets and returns a Configuration object which contains information about the current step in the sequence.
	 * Returns null if it is unable to properly get that information or if the seqStepNum is greater than the number of steps
	 * or less than one.
	 * @return
	 */
	public Configuration getCurrentStep()
	{
		Configuration oneElement = null;

		if (! (seqStepNum > sequenceSteps.length) && ! (seqStepNum < 1))
		{
			oneElement = sequenceSteps[seqStepNum - 1];

			//oneElement.getName() should be either "model" or "if" 
			//Attributes can be read as follows: oneElement.getAttribute("return", "")
		}

		return oneElement;
	}

	public static Configuration[] getSequenceSteps(ModelRequest req) throws ModelException
	{
		Configuration[] theSeqSteps = null;
		SequenceInfo si = getSequenceInfo(req);

		if (si != null)
		{
			theSeqSteps = si.getSequenceSteps();
		}

		return theSeqSteps;
	}

	public static Configuration[] getSequenceSteps(Context context) throws ModelException
	{
		Configuration[] theSeqSteps = null;
		SequenceInfo si = getSequenceInfo(context);

		if (si != null)
		{
			theSeqSteps = si.getSequenceSteps();
		}

		return theSeqSteps;
	}

	public Configuration[] getSequenceSteps() throws ModelException
	{
		return sequenceSteps;
	}

	/**
	 * @see org.apache.avalon.framework.logger.LogEnabled#enableLogging(org.apache.avalon.framework.logger.Logger)
	 */
	public void enableLogging(Logger logger)
	{
		theSequenceInfoLogger = logger;
	}

	protected void debug(String msg)
	{
		if (theSequenceInfoLogger != null && theSequenceInfoLogger.isDebugEnabled())
		{
			theSequenceInfoLogger.debug(msg);
		}
	}
}
