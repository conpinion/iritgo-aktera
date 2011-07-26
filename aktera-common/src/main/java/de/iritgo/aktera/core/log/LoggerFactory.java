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

package de.iritgo.aktera.core.log;


import org.apache.avalon.framework.logger.Logger;


/**
 * <Replace with description for LoggerFactory>
 *
 * @version        $Revision: 1.1 $        $Date: 2003/12/30 01:29:35 $
 * @author Schatterjee
 * Created on Dec 23, 2003
 */
public interface LoggerFactory
{
	public Logger getLoggerForCategory (String category);

	public Logger getDefaultLogger ();
}
