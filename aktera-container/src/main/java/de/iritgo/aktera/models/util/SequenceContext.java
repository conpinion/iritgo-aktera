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
import de.iritgo.aktera.model.ModelResponse;
import de.iritgo.aktera.model.ResponseElement;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.DefaultContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A simple class an instance of which is stored in the "per user" context
 * when a user is running a "sequence" type model. When a model returns a response
 * with no inputs, control is transferred back to the sequence as of the
 * appropriate sequence number.
 *
 * @author Michael Nash, with small modifications from Phil Brown.
 */
public class SequenceContext implements Serializable
{
	public static final String CONTEXT_KEY = "de.iritgo.aktera.models.util.SequenceContext";

	private String sequenceName = null;

	/*
	 * The name of the <model ...> node within the the <sequence ...> that is defined.
	 */
	private String modelName = null;

	/**
	 * seqStepNum is an int that indicates the current step in a sequence.  The first step in a sequence is always 1.
	 */
	private int seqStepNum = 0;

	private transient ModelResponse currentResponse = null;

	private Configuration[] sequenceSteps = null;

	private boolean mergeDefault = Sequence.sequenceWideMergeDefaultIfNotSpecified;

	//True = default action for sequences is merge="true".  False --> merge="false" is default.
	/**
	 * Constructor for SequenceContext.
	 */
	public SequenceContext ()
	{
		super ();
	}

	public SequenceContext (String theSequenceName, int newSeq)
	{
		super ();
		sequenceName = theSequenceName;
		seqStepNum = newSeq;
	}

	/**
	 * Creates a new SequenceContext, using the passed in parameters to initialize itself.
	 * @param theSequenceName
	 * @param sequenceStep - one based int which indicates which is the "current" element in the sequence.
	 * @param children - An array of Configuration elements.  Each element in the Configuration[] represents a step in the sequence.
	 * @param res - The current response object.
	 */
	public SequenceContext (String theSequenceName, int sequenceStep, Configuration[] children, ModelResponse res)
	{
		super ();
		sequenceName = theSequenceName;
		seqStepNum = sequenceStep;
		sequenceSteps = children;
		currentResponse = res;
		modelName = findModelName (children, sequenceStep);
	}

	/**
	 * This method gets the correct child node based on the sequenceStep, and returns the "name" attribute fro mthat node.
	 * @param children
	 * @param sequenceStep
	 * @return
	 */
	private String findModelName (Configuration[] children, int sequenceStep)
	{
		String retVal = "";

		if (children != null)
		{
			if (sequenceStep - 1 < children.length)
			{
				Configuration config = children[sequenceStep - 1];

				if (config != null)
				{
					retVal = config.getAttribute ("name", "");
				}
			}
		}

		return retVal;
	}

	/**
	 * This convenience method will return the first sequence step
	 * found for a given model name. Keep in mind that the same
	 * model name can be found many times in the sequence. This method
	 * only finds the first one. If a match is not found, the method returns zero.
	 *
	 * @param modelName
	 * @return
	 */
	public int getFirstSequenceStep (String modelName)
	{
		int returnStep = 0;

		if (this.sequenceSteps == null || modelName == null)
		{
			//cant make a match on this. Return zero.
			return returnStep;
		}

		Configuration config = null;

		for (int i = 0; i < this.sequenceSteps.length; i++)
		{
			config = this.sequenceSteps[i];

			if (config != null)
			{
				if (modelName.equals (config.getAttribute ("name", "")))
				{
					returnStep = i + 1;
				}
			}
		}

		return returnStep;
	}

	/**
	 * Gets and returns the sequenceContext for the current sequence, using the ModelRequest req to
	 * look up the current sequenceContext from the Context.  Returns null if there is no sequenceContext
	 * in the current context.
	 * @param req
	 * @return
	 * @throws ModelException
	 */
	public static SequenceContext getSequenceContext (ModelRequest req) throws ModelException
	{
		SequenceContext sc = null;

		try
		{
			Context ctx = req.getContext ();

			if (ctx instanceof DefaultContext)
			{
				DefaultContext defContext = (DefaultContext) ctx;

				sc = (SequenceContext) defContext.get (CONTEXT_KEY);
			}
			else
			{
				sc = (SequenceContext) ctx.get (CONTEXT_KEY);
			}
		}
		catch (ContextException e)
		{ //We get a ContextException if the key isn't found.
			sc = null;

			//Do we need to create new SequenceContext here, or is the code OK as is?
		}
		catch (ClassCastException e)
		{
			throw new ModelException (e.getLocalizedMessage (), e);
		}
		catch (NullPointerException e)
		{
			sc = null;
		}

		return sc;
	}

	/**
	 * Gets and returns the sequenceContext for the current sequence, using the context passed in to
	 * look up the current sequenceContext.  Returns null if there is no sequenceContext
	 * in the context passed in or if the context passed in is null.
	 * @param context
	 * @return
	 * @throws ModelException
	 */
	public static SequenceContext getSequenceContext (Context context) throws ModelException
	{
		try
		{
			SequenceContext sc = null;

			sc = (SequenceContext) context.get (SequenceContext.CONTEXT_KEY);

			return sc;
		}
		catch (ContextException e)
		{
			throw new ModelException (e.getLocalizedMessage (), e);
		}
		catch (ClassCastException e)
		{
			throw new ModelException (e.getLocalizedMessage (), e);
		}
		catch (NullPointerException e)
		{
			return null;
		}
	}

	/**
	 * Returns an int indicating which is the current step in the the seqStepNum.  This int is one based, meaning the the first step in a sequence
	 * is step number 1 (not step number zero).
	 * @return int
	 * @deprecated  - use getSeqStepNum instead
	 */
	public int getSeq ()
	{
		return seqStepNum;
	}

	/**
	 * Returns the name of the sequence
	 * @return String
	 */
	public String getSequenceName ()
	{
		return sequenceName;
	}

	/**
	 * Returns the sequenceName.
	 * @return String
	 * @deprecated - use getSequenceName() instead
	 */
	public String getSequenceModel ()
	{
		return sequenceName;
	}

	/**
	 * Sets the seqStepNum.  The sequence is one-based meaning the the first step in a sequence
	 * is step number 1 (not step number zero).
	 * @param seqStepNum The seqStepNum to set
	 * @deprecated - use setSeqStepNum instead
	 */
	public void setSeq (int seq)
	{
		this.seqStepNum = seq;
	}

	/**
	 * Sets the sequenceName.
	 * @param sequenceName The sequenceName to set
	 * @deprecated - use setSequenceName instead
	 */
	public void setSequenceModel (String sequenceModel)
	{
		this.sequenceName = sequenceModel;
	}

	/**
	 * Returns the currentResponse.
	 * @return Modelesponse
	 */
	public ModelResponse getCurrentResponse ()
	{
		return currentResponse;
	}

	/**
	 * Sets the currentResponse.
	 * @param currentResponse The currentResponse to set
	 */
	public void setCurrentResponse (ModelResponse newResponse) throws ModelException
	{
		this.currentResponse = newResponse;
	}

	public static ModelResponse mergeResponse (ModelResponse base, ModelResponse layer) throws ModelException
	{
		if (layer == null)
		{
			return base;
		}

		if (base == null)
		{
			return layer;
		}

		for (Iterator i = layer.getAll (); i.hasNext ();)
		{
			ResponseElement newElement = (ResponseElement) i.next ();

			base.add (newElement);
		}

		String oneAttribName = null;

		for (Iterator j = layer.getAttributes ().keySet ().iterator (); j.hasNext ();)
		{
			oneAttribName = (String) j.next ();
			base.setAttribute (oneAttribName, layer.getAttribute (oneAttribName));
		}

		Map errors = new HashMap (layer.getErrors ());
		String oneErrorName = null;
		Throwable throwable = null;

		for (Iterator k = errors.keySet ().iterator (); k.hasNext ();)
		{
			oneErrorName = (String) k.next ();
			throwable = layer.getThrowable (oneErrorName);

			String oneMessage = (String) errors.get (oneErrorName);

			if (throwable != null)
			{
				base.addError (oneErrorName, oneMessage, throwable);
			}
			else
			{
				base.addError (oneErrorName, oneMessage);
			}
		}

		return base;
	}

	/**
	 * @return
	 */
	public Configuration[] getSequenceSteps ()
	{
		return sequenceSteps;
	}

	/**
	 * @param configurations
	 */
	public void setSequenceSteps (Configuration[] configurations)
	{
		sequenceSteps = configurations;
	}

	/**
	 * @return
	 */
	public int getSeqStepNum ()
	{
		return seqStepNum;
	}

	/**
	 * @param i
	 */
	public void setSeqStepNum (int i)
	{
		seqStepNum = i;
	}

	/**
	 * @param string
	 */
	public void setSequenceName (String string)
	{
		sequenceName = string;
	}

	/**
	 * This method sets the value that will be returned by a call to getMergeDefault()
	 * @param mergeDefault
	 */
	public void setMergeResponsesDefault (boolean mergeDefault)
	{
		this.mergeDefault = mergeDefault;
	}

	/**
	 * Returns the value last set by setMergeDefault(...). <br>
	 * If setMergeDefault wasn't called, then it returns the static final boolean value Sequence.defaultForMergeResponse
	 * @return
	 */
	public boolean getMergeResponsesDefault ()
	{
		return mergeDefault;
	}

	/**
	 * @return
	 */
	public String getModelName ()
	{
		return modelName;
	}

	/**
	 * @param string
	 */
	public void setModelName (String string)
	{
		modelName = string;
	}
}
