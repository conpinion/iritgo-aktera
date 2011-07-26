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

package de.iritgo.aktera.util.thread;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * @author pjbrown
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ThreadUtil
{
	//--- Do not allow this to be instantiated since it is a Singleton.
	private ThreadUtil ()
	{
	} // ThreadUtil

	public static final void showInfo ()
	{
		ThreadUtil.showInfo (Thread.currentThread ());
	}

	public static final void showInfo (Thread thread)
	{
		ThreadGroup tGroup = thread.getThreadGroup ();
		String tgName = tGroup.getName ();
		String tName = thread.getName ();

		print ("<This thread is:> " + tName);
		print ("<This thread is part of the following thread group:> " + tgName);
		print ("");
		print ("<***ThreadGroup Hierarchy is below (" + tgName + ") ***>");
		showThreads (tGroup);
	}

	public static final void showThreads (ThreadGroup tGroup)
	{
		showThreads (tGroup, true);
	}

	public static final void showThreads (ThreadGroup tGroup, boolean recurseThroughParents)
	{
		ThreadGroup parentGroup = tGroup.getParent ();

		if (recurseThroughParents)
		{
			if (parentGroup != null)
			{
				showThreads (parentGroup, recurseThroughParents);
			}
			else
			{
				print ("<* Parent Group is null*>");
			}
		}

		print ("<***Threads in Group: " + tGroup.getName () + " ***>");

		int size = tGroup.activeCount ();
		Thread[] threadArray = new Thread[size + 4];
		int len = tGroup.enumerate (threadArray); //Popluate array with all threads in this group

		for (int i = 0; i < len; i++)
		{
			Thread thread = threadArray[i];

			if (thread != null)
			{
				print ("  <Thread " + Integer.toString (i) + "> " + thread.getName ());
			}
		}
	}

	public static final void print (String string)
	{
		System.err.println (string);
	}

	/**
	 * Searches the current threadgroup and all its parent thread groups to find a thread group that contains the name groupName (case insensitive).
	 * It then searches all threads that are directly in that thread group to find a thread that contains the strings contains1 and contains2 (also case insensitive).
	 * It returns the first thread it finds that meets this criteria.  If no thread meets this criteria then it returns null;   If contains2 is an empty string
	 * or contains2 is null, then it only checks for a match in contains1.
	 */
	public static final Thread findThread (Thread threadToSearch, String groupName, String contains1, String contains2,
					boolean recurseThroughSubGroups)
	{
		Thread foundThread = null;
		ThreadGroup tGroup = findThreadGroup (threadToSearch, groupName);
		List threadList = listThreads (tGroup, recurseThroughSubGroups);
		Iterator iter = threadList.iterator ();
		boolean found = false;

		while (iter.hasNext () && ! found)
		{
			Thread thread = (Thread) iter.next ();
			String tName = thread.getName ().toUpperCase ();

			if (contains1 != null && contains1.length () > 0 && contains2 != null && contains2.length () > 0)
			{
				if (tName.indexOf (contains1.toUpperCase ()) >= 0 && tName.indexOf (contains2.toUpperCase ()) >= 0)
				{
					found = true;
				}
			}
			else if (contains1 != null && contains1.length () > 0)
			{
				if (tName.indexOf (contains1.toUpperCase ()) >= 0)
				{
					found = true;
				}
			} //end if-else-if

			if (found)
			{
				foundThread = thread;
			}
		} //end-while

		return foundThread;
	}

	/**
	 * Creates and returns a list of all threads contained within tGroup.  If recurse == true, then it includes all threads
	 * within sub-groups of tGroup.  If no threads are found or if tGroup is null then it returns an empty list.
	 * @param tGroup
	 * @return
	 */
	private static List listThreads (ThreadGroup tGroup, boolean recurse)
	{
		List threadList = new ArrayList ();

		if (tGroup != null)
		{
			int size = 0;

			if (recurse)
			{
				size = 2048;
			}
			else
			{
				size = tGroup.activeCount () + 16;
			}

			Thread[] threadArray = new Thread[size];
			int numThreads = tGroup.enumerate (threadArray, recurse);

			for (int i = 0; i < numThreads; i++)
			{
				threadList.add (threadArray[i]);
			}
		}

		return threadList;
	}

	/**
	 * Looks through the ThreadGroup of threadToSearch and all of it's parent threadGroups.
	 * Returns the first threadGroup found that contains groupName (case insensitive match).
	 * If no threadgroup is found to match, or if groupName is null or the empty string, then this method returns
	 * null.
	 * @param threadToSearch
	 * @param groupName
	 * @return
	 */
	private static ThreadGroup findThreadGroup (Thread threadToSearch, String groupName)
	{
		ThreadGroup foundThreadGroup = null;
		boolean found = false;

		if (groupName != null && groupName.length () > 0)
		{
			ThreadGroup tGroup = threadToSearch.getThreadGroup ();

			while (tGroup != null && ! found)
			{
				if (tGroup.getName ().toUpperCase ().indexOf (groupName.toUpperCase ()) >= 0)
				{
					found = true;
				}

				if (found)
				{
					foundThreadGroup = tGroup;
				}
				else
				{
					tGroup = tGroup.getParent ();
				}
			} //end-while
		}

		return foundThreadGroup;
	}
}
