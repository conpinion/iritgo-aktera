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


import de.iritgo.aktera.model.Command;
import de.iritgo.aktera.model.Input;
import de.iritgo.aktera.model.ModelException;
import de.iritgo.aktera.model.ModelRequest;
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.ResponseElement;
import de.iritgo.aktera.model.StandardLogEnabledModel;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.DefaultContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A Sequence Model runs other models in a specified sequence. The sequence
 * is highly configurable. To use a sequence, you must specify a "sequence"
 * element in configuration, like so:
 * <br>
 * <pre>
 * &lt;sequence id="somesequence"&gt;
 *     &lt;model name="somemodel"/&gt;
 *     &lt;model name="someothermodel"/&gt;
 * &lt;/sequence&gt;
 * </pre>
 * <br>
 * This sequence would be called with "model=somesequence" (in Struts) or
 * with /model/somesequence.html (in Cocoon), just like any other model. It would
 * (as configured above) execute "somemodel" and return the results to the
 * user. When the user submitted their next request, it would run whatever that
 * request was (presumably a "submit" of a command emitted by "somemodel"), then
 * immediately run "someothermodel", again returning the response to the user.
 * <br>
 * By specifying the attribute "return" with value "false" on a "model" line
 * within a sequence, you can skip the return to the user - this has the effect
 * of "combining" or "merging" the responses from the two models into a single
 * response, then returning that combined response to the user. For example:
 * <br>
 * Also, you can specify an attribute "merge" with a value of "true" or "false". By
 * default, all responses from each running model in the sequence are merged.
 * If you do not want that (annoying for command-line models) specify
 * merge="false" for each model in the sequence definition.
 * <pre>
 * &lt;sequence id="somesequence"&gt;
 *     &lt;model name="nav.navigate" return="false"/&gt;
 *     &lt;model name="somemodel"/&gt;
 * &lt;/sequence&gt;
 * </pre>
 * <br>
 * Would return the results from "somemodel" which would now include any results
 * produced by "nav.navigate" - this can be useful when displaying a navigation
 * menu with the "somemodel" output.
 * <br>
 * As another example, say a particular model must be run only by users
 * who are logged in to the system:
 * <pre>
 * &lt;sequence id="somesequence"&gt;
 *     &lt;model name="register.verify-login" return="ifinput"/&gt;
 *     &lt;model name="somemodel"/&gt;
 * &lt;/sequence&gt;
 * </pre>
 * <br>
 * In the above configuration, the special value "ifinput" is used on the
 * register.verify-login model. If this model's execution emits any Input
 * objects, control will be returned to the user at that point. If not,
 * execution continues with the "somemodel" model. This can be very useful
 * in controlling the flow of a sequence.
 *
 * A sequence can be called in either of the following two ways:  <br>
 *
 *    model=mySequence?seq=1  (calls the first step of mySequence)  or  <br>
 *    model=mySequence?step=1 (also calls the first step of mySequence).
 *
 * The "step" parameter was added for naming clarity.  The "seq" parameter was left in to
 * ensure backwards compatibility.
 *
 * @avalon.component
 * @avalon.service type=de.iritgo.aktera.model.Model
 * @x-avalon.info name=sequence
 * @x-avalon.lifestyle type=request
 *
 * @version                $Revision: 1.1 $  $Date: 2005/08/15 08:23:44 $
 * @author                Michael Nash
 */
public class Sequence extends StandardLogEnabledModel implements Poolable
{
	public static final String SEQUENCE_NAME = "SEQUENCE_NAME";

	public static final String SEQUENCE_NUMBER = "SEQUENCE_NUMBER";

	static final boolean sequenceWideMergeDefaultIfNotSpecified = true;

	private Map params = new HashMap();

	//The default is to mergeResponses
	public ModelResponse execute(ModelRequest req) throws ModelException
	{
		try
		{
			Configuration config = getConfiguration();

			/*
			 * Clear the params HashMap class variable if specified in system.xconf
			 * Default is false for backwards compatibility
			 * Added by Santanu Dutt
			 */
			boolean clearParams = config.getAttributeAsBoolean("clearParams", true);

			if (clearParams)
			{
				this.params.clear();
			}

			//Get "merge" attribute from the <sequence> tag, or use the hard-coded default if there isn't one.
			boolean mergeDefaultFromSequenceDefinition = config.getAttributeAsBoolean("merge",
							sequenceWideMergeDefaultIfNotSpecified);

			return runSequence(config, req, mergeDefaultFromSequenceDefinition);
		}
		catch (ConfigurationException ce)
		{
			throw new ModelException("Configuration exception in sequence", ce);
		}
	}

	private ModelResponse runSequence(Configuration myConf, ModelRequest req, boolean mergeDefaultForThisSequence)
		throws ModelException, ConfigurationException
	{
		boolean createNewSeqContext = false;
		boolean keepRunning = true;
		boolean mergeResponses = mergeDefaultForThisSequence;

		String seqString = (String) req.getParameter("seq");

		if (seqString == null)
		{
			seqString = (String) req.getParameter("step");
		}

		String seqName = req.getModel();

		//         if (log.isDebugEnabled()) {

		//             log.debug(
		//                 "Called seq " + req.getModel() + " with seq " + seqString);
		//         }
		int seq = 1;

		/* See if we were given a "seq" parameter */
		if ((seqString != null) && (! seqString.equals("")))
		{
			try
			{
				seq = new Integer(seqString).intValue();
			}
			catch (NumberFormatException ne)
			{
				throw new ModelException("Invalid sequence '" + seqString + "'", ne);
			}
		}

		ModelResponse currentResponse = null;
		SequenceContext seqContext = null;

		try
		{
			seqContext = SequenceContext.getSequenceContext(req);

			if (seqContext != null)
			{
				//                 log.debug("Found existing sequence context. Seq= " + (seq));
				String oldSeqName = seqContext.getSequenceName();

				/**
				 * Added by ACR. Up until now, problems occured when seq steps from one sequence would
				 * bleed into the next sequence, jumping the user up steps.
				 * I have changed the system to now assume that when you jump from one sequence to another
				 * you want to go to the first step of the next sequence.
				 *
				 */
				if (! seqName.equals(oldSeqName))
				{
					//                     if (log.isDebugEnabled()) {
					//                       log.debug(
					//                         "Sequence is transitioning, from sequence "
					//                             + oldSeqName
					//                             + " to sequence "
					//                             + seqName
					//                             + ". Clearing sequence variables....");
					//                     }
					//Don't change the seq parameter, because we already read it in above, and we have already set it 1 if 
					//there was no sequence param.
					clearSequence(req);
					currentResponse = req.createResponse();
				}
				else
				{
					if (seq != 1)
					{
						//we want to "recycle" the response to keep it going, since we are still in the same sequence....
						currentResponse = seqContext.getCurrentResponse();
					}
					else
					{
						currentResponse = req.createResponse();
					}
				} //end-if-else
			}
			else
			{ //seqContext is null, so create a new one
				createNewSeqContext = true;
			} //end-if-else
		}
		catch (ModelException e)
		{
			//Error occured. Start a fresh response....
			currentResponse = req.createResponse();
			createNewSeqContext = true;
			log.error(e.toString());
		}

		clearSequence(req);
		createNewSeqContext = true;
		//Set flag to create a new sequence context.
		params.putAll(req.getParameters());

		Configuration[] children = myConf.getChildren();

		if (seq > children.length)
		{
			log.warn("Requested seq " + seq + " which is more than available steps " + children.length);
			clearSequence(req);

			if (currentResponse != null)
			{
				return currentResponse;
			}
			else
			{
				throw new ModelException("Response for end of sequence was null");
			}
		}

		//Added by Phil Brown to create and save a new Sequence Context if we don't have
		//This is desired so that our models have the sequenceContext available in case they
		//wish to examine it.
		if (seqContext == null || createNewSeqContext)
		{
			seqContext = setNewSequenceContext(seqName, seq, children, mergeDefaultForThisSequence, req,
							currentResponse);
		}

		/* If a particular step in the sequence has "return='false'" then */
		/* we simply continue on to the next step, without returning control */
		/* to the user. If "return" is not specified, it is assumed "true" */
		ModelResponse previousResponse = SequenceContext.mergeResponse(req.createResponse(), currentResponse);
		ModelResponse theResponse = req.createResponse();

		//Create a response to return. We will merge later if we need to.
		while (keepRunning)
		{
			//             log.debug("Seq is " + seq);
			if (seq > children.length)
			{
				keepRunning = false;

				//                 log.debug(
				//                     "No more steps in sequence (length "
				//                         + children.length
				//                         + ")");
			}
			else
			{
				Configuration oneElement = children[seq - 1];

				seqContext.setSeqStepNum(seq);
				//Update the current step in the seqContext -- add by Phil Brown
				seqContext.setCurrentResponse(currentResponse);

				//Update the "current" response.  -- add by Phil Brown
				if (oneElement.getName().equals("model"))
				{
					currentResponse = runModel(oneElement, req, previousResponse, seq, seqName, seqContext);
				}
				else if (oneElement.getName().equals("if"))
				{
					//                     log.debug("Processing conditional in sequence");
					currentResponse = runIfTrue(oneElement);
				}
				else
				{
					throw new ConfigurationException("Element '" + oneElement.getName()
									+ "' should be either 'model' or 'if'");
				}

				if (oneElement.getAttribute("return", "").equals("ifinput"))
				{
					//                     log.debug("We return if model just run has inputs");
					if (containsInputs(currentResponse))
					{
						keepRunning = false;

						//                         log.debug("Model just run has inputs - returning");
					}
					else
					{
						seq = getNextSequence(seq, req);
					}
				}
				else
				{
					if (oneElement.getAttributeAsBoolean("return", true))
					{
						keepRunning = false;

						//                         log.debug("Model has input set to true - returning");
					}
					else
					{
						seq = getNextSequence(seq, req);
					}
				}

				//ACR & PJB - Gets merge attribute from <model> element.  If not found, then uses default from SequenceContext
				if (oneElement.getAttributeAsBoolean("merge", seqContext.getMergeResponsesDefault()))
				{
					mergeResponses = true;
				}
				else
				{
					mergeResponses = false;
				}
			} /* else we're still on a valid sequence */
			//ACR: Here is the merge...if a condition has been met where
			//multiple models are run in this "pass", they are each merged here.
			if (mergeResponses)
			{
				theResponse.removeAttribute("forward");
				theResponse.removeAttribute("stylesheet");
				theResponse = SequenceContext.mergeResponse(theResponse, currentResponse);
			}
			else
			{
				theResponse = currentResponse;
			}

			//            if (log.isDebugEnabled()) {
			//                log.debug("Current merged context:" + theResponse.toString());
			//            }
		} //end-while

		/* Put our "placeholder" in the user context */
		if (seq < children.length)
		{
			setNewSequenceContext(req.getModel(), seq + 1, children, mergeDefaultForThisSequence, req, theResponse);

			//             log.debug("Stored seq " + (seq + 1));
		}
		else
		{
			//             log.debug("Sequence completed");
			clearSequence(req);
		}

		return theResponse;
	}

	/**
	 * @param string
	 * @param i
	 * @param children
	 * @param req
	 * @param res
	 */
	private SequenceContext setNewSequenceContext(String modelName, int seq, Configuration[] children,
					boolean mergeDefault, ModelRequest req, ModelResponse res) throws ModelException
	{
		SequenceContext mySeqContext = new SequenceContext(modelName, seq, children, res);

		mySeqContext.setCurrentResponse(res);
		mySeqContext.setMergeResponsesDefault(mergeDefault);

		//TODO -  Also, set this from the sequence config, if there is an attribute
		DefaultContext dc = (DefaultContext) req.getContext();

		dc.put(SequenceContext.CONTEXT_KEY, mySeqContext);

		return mySeqContext;
	}

	private void clearSequence(ModelRequest req) throws ModelException
	{
		DefaultContext dc = (DefaultContext) req.getContext();

		dc.put(SequenceContext.CONTEXT_KEY, null);

		//         log.debug("Sequence " + req.getModel() + " cleared");
	}

	private ModelResponse runModel(Configuration modelConfig, ModelRequest req, ModelResponse existingResponse,
					int seqNumber, String seqName, SequenceContext seqContext)
		throws ConfigurationException, ModelException
	{
		//         if (log.isDebugEnabled()) {
		//           log.debug("Run model " + modelConfig.getAttribute("name"));
		//         }
		Configuration[] children = modelConfig.getChildren();
		Configuration oneParam = null;
		HashMap postAttribs = new HashMap();

		for (int i = 0; i < children.length; i++)
		{
			oneParam = children[i];

			if (oneParam.getName().equals("parameter"))
			{
				String paramName = oneParam.getAttribute("name");

				params.put(paramName, oneParam.getAttribute("value"));
			}
			else if (oneParam.getName().equals("attribute"))
			{
				postAttribs.put(oneParam.getAttribute("name"), oneParam.getAttribute("value"));
			}
		}

		String modelName = getModelName(modelConfig, req);

		//         log.debug("Creating a command for modelName: " + modelName);
		Command c = existingResponse.createCommand(modelName);

		for (Iterator i = params.keySet().iterator(); i.hasNext();)
		{
			String paramName = (String) i.next();

			c.setParameter(paramName, params.get(paramName));

			//             if (log.isDebugEnabled()) {
			//               log.debug(
			//                 "Setting parameter:" + paramName + "=" + params.get(paramName));
			//             }
		}

		//         log.debug("About to execute the new command for " + modelName);
		c.setParameter(Sequence.SEQUENCE_NAME, seqName);
		c.setParameter(Sequence.SEQUENCE_NUMBER, "" + seqNumber);

		//c.setParameter(SequenceContext.CONTEXT_KEY, seqContext);
		//Add by Phil Brow to pass the seq Context as a param to each model
		//MN: Can't serialize a SequenceContext in the response
		ModelResponse commandRes = c.execute(req, existingResponse);

		//         log.debug("Executed command.");
		for (Iterator ii = postAttribs.keySet().iterator(); ii.hasNext();)
		{
			String oneKey = (String) ii.next();

			commandRes.setAttribute(oneKey, postAttribs.get(oneKey));
		}

		return commandRes;
	}

	private ModelResponse runIfTrue(Configuration ifConfig)
	{
		return null;
	}

	private boolean containsInputs(ModelResponse res)
	{
		for (Iterator i = res.getAll(); i.hasNext();)
		{
			if (containsInputs((ResponseElement) i.next()))
			{
				return true;
			}
		}

		for (Iterator j = res.getAttributes().keySet().iterator(); j.hasNext();)
		{
			Object oneAttrib = res.getAttribute((String) j.next());

			if (oneAttrib instanceof ResponseElement)
			{
				if (containsInputs((ResponseElement) oneAttrib))
				{
					return true;
				}
			}
		}

		return false;
	}

	private boolean containsInputs(ResponseElement re)
	{
		if (re instanceof Input)
		{
			return true;
		}

		for (Iterator i = re.getAll().iterator(); i.hasNext();)
		{
			if (containsInputs((ResponseElement) i.next()))
			{
				return true;
			}
		}

		for (Iterator j = re.getAttributes().keySet().iterator(); j.hasNext();)
		{
			//Fixed by ACR. Old code was throwing a classcast exception.
			Object key = j.next();
			String keyString = key.toString();
			Object oneAttrib = re.getAttribute(keyString);

			if (oneAttrib instanceof ResponseElement)
			{
				if (containsInputs((ResponseElement) oneAttrib))
				{
					return true;
				}
			}
		}

		return false;
	}

	protected int getNextSequence(int seq, ModelRequest req)
	{
		return seq + 1;
	}

	protected String getModelName(Configuration modelConfig, ModelRequest request) throws ConfigurationException
	{
		return modelConfig.getAttribute("name");
	}
}
