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

package de.iritgo.aktera.ui;


import de.iritgo.aktera.core.exception.NestedException;


public class UIControllerException extends NestedException
{
	/** */
	private static final long serialVersionUID = 1L;

	public UIControllerException ()
	{
		super ();
	}

	public UIControllerException (String s)
	{
		super (s);
	}

	public UIControllerException (String s, String newErrorKey)
	{
		super (s, newErrorKey);
	}

	public UIControllerException (String message, Throwable newNested)
	{
		super (message, newNested);
	}

	public UIControllerException (String message, Throwable newNested, String newErrorKey)
	{
		super (message, newNested, newErrorKey);
	}

	public UIControllerException (Throwable newNested)
	{
		super (newNested);
	}

	public UIControllerException (Throwable newNested, String newErrorKey)
	{
		super (newNested, newErrorKey);
	}
}
