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

package de.iritgo.aktera.fsm;


import com.evelopers.common.exception.CommonException;
import com.evelopers.unimod.core.stateworks.Event;
import com.evelopers.unimod.core.stateworks.Model;
import com.evelopers.unimod.core.stateworks.State;
import com.evelopers.unimod.core.stateworks.Transition;
import com.evelopers.unimod.runtime.AbstractEventProcessorListener;
import com.evelopers.unimod.runtime.EventProvider;
import com.evelopers.unimod.runtime.ModelEngine;
import com.evelopers.unimod.runtime.StateMachineConfig;
import com.evelopers.unimod.runtime.StateMachinePath;
import com.evelopers.unimod.runtime.context.StateMachineContext;
import java.util.List;


/**
 * This event provider is built to solve the following problems:
 *
 * 1) The start state (i.e. the successor of the initial state) is only entered
 * when the first event was send to the state engine. But if the "enter"-action
 * of the start state triggers the production of the first event, how can we
 * then produce this first event? We cannot and so we need to send an
 * initial dummy event to the state engine, that only triggers entering of the
 * start state and is then skipped. However if the start state contains an "any"
 * event transition, this transition would be fired by the state engine when
 * processing the "init"-event. To prevent this, we disable the "any" event
 * transition for the first event (by changing the event type from "any" to "no
 * event".)
 *
 * 2) Event generation often depends on the actions performed during entering
 * a state. If we need to change the event generation algorithm depending on the
 * current state, we need to be informed about entering a state. This is
 * simplified by calling a template method prepareForNextEvent() when a new state
 * was entered.
 */
public class DefaultEventProvider implements EventProvider, Runnable
{
	/** Special event name for the first init event */
	public static final String INIT_EVENT_NAME = "__INIT__";

	/** Special event name for 'leave sub machine' events */
	public static final String RETURN_EVENT_NAME = "RETURN_";

	/** The model engine for which to generate events */
	private ModelEngine engine;

	/** The state engine model */
	private Model model;

	/** True until we enter the final state */
	private boolean notInFinalState = true;

	/** The state machine's start state (i.e. the successor of the initial state) */
	private State startState = null;

	/** Becomes true on entering the start state */
	private boolean startStateVisited = false;

	/** If not null, contains the name of the sub machine to leave */
	private String returnFromSubMachineName;

	/**
	 * Set the state machine model.
	 *
	 * @param model The state machine model
	 */
	public void setModel (Model model)
	{
		this.model = model;
	}

	/**
	 * @see com.evelopers.unimod.runtime.EventProvider#dispose()
	 */
	public void dispose ()
	{
	}

	/**
	 * @see com.evelopers.unimod.runtime.EventProvider#init(com.evelopers.unimod.runtime.ModelEngine)
	 */
	public void init (ModelEngine engine) throws CommonException
	{
		this.engine = engine;
		engine.getEventProcessor ().addEventProcessorListener (new AbstractEventProcessorListener ()
		{
			@Override
			public void comeToState (StateMachineContext context, StateMachinePath path, String stateName)
			{
				State state = model.getStateMachine (path.getStateMachine ()).findState (stateName);

				if (! state.getName ().equals ("RETURN"))
				{
					prepareForNextEvent (state);
				}
				else
				{
					returnFromSubMachineName = path.getStateMachine ();
				}
			}

			@Override
			public void stateMachineCameToFinalState (StateMachineContext context, StateMachinePath path,
							StateMachineConfig config)
			{
				notInFinalState = false;
			}

			@Override
			public void eventSkipped (StateMachineContext context, StateMachinePath path, String stateName, Event event)
			{
				if (! startStateVisited)
				{
					if (model.getStateMachine (path.getStateMachine ()).findState (stateName) == startState)
					{
						startStateVisited = true;
						enableAnyEventTransition (startState);
					}
				}
			}
		});
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run ()
	{
		startState = findStartState (model);
		disableAnyEventTransition (startState);
		engine.getEventManager ().handleAndWait (new Event ("_INIT_"), new DefaultContext (null));

		while (notInFinalState)
		{
			Event event = null;

			if (returnFromSubMachineName != null)
			{
				event = new Event (RETURN_EVENT_NAME + returnFromSubMachineName);
				returnFromSubMachineName = null;
			}
			else
			{
				event = produceEvent ();
			}

			engine.getEventManager ().handleAndWait (event, new DefaultContext (event));
		}
	}

	/**
	 * Determine which event type should be provided to trigger the next
	 * transition from the specified state.
	 *
	 * Override this method if you need to change the event production
	 * algorithm depending on the possible state transitions.
	 *
	 * @param state The state to prepare for
	 */
	protected void prepareForNextEvent (State state)
	{
	}

	/**
	 * Produce the next event that should be sent to the state engine.
	 *
	 * @return The next event
	 */
	protected Event produceEvent ()
	{
		return Event.ANY;
	}

	/**
	 * Retrieve the start state, i.e. the successor of the initial state.
	 *
	 * @param model The state engine model
	 * @return The start state
	 */
	protected State findStartState (Model model)
	{
		return ((Transition) model.getRootStateMachine ().getTop ().getInitialSubstate ().getOutgoingTransitions ()
						.get (0)).getTargetState ();
	}

	/**
	 * Disable all "any" event transitions that go out from the specified state.
	 *
	 *  @param state The state
	 */
	private void disableAnyEventTransition (State state)
	{
		for (Transition transition : (List<Transition>) state.getOutgoingTransitions ())
		{
			if (transition.getEvent ().equals (Event.ANY))
			{
				transition.setEvent (Event.NO_EVENT);
			}
		}
	}

	/**
	 * Enable all "any" event transitions that go out from the specified state.
	 *
	 *  @param state The state
	 */
	private void enableAnyEventTransition (State state)
	{
		for (Transition transition : (List<Transition>) state.getOutgoingTransitions ())
		{
			if (transition.getEvent ().equals (Event.NO_EVENT))
			{
				transition.setEvent (Event.ANY);
			}
		}
	}
}
