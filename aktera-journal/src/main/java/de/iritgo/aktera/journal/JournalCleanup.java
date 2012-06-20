/**
 * This file is part of the IPtell Application.
 *
 * Copyright (C) 2006-2011 TELCAT MULTICOM GmbH.
 * Copyright (C) 2004-2006 BueroByte GbR.
 */

package de.iritgo.aktera.journal;


import de.iritgo.aktera.model.*;
import de.iritgo.aktera.spring.SpringTools;
import de.iritgo.simplelife.string.StringTools;


/**
 * @avalon.component
 * @avalon.service type="de.iritgo.aktera.model.Model"
 * @x-avalon.info name="de.iritgo.aktera.journal.JournalCleanup"
 * @x-avalon.lifestyle type="singleton"
 * @model.model name="de.iritgo.aktera.journal.JournalCleanup"
 *              id="de.iritgo.aktera.journal.JournalCleanup" logger="pbx"
 * @model.attribute name="forward"
 *                  value="de.iritgo.aktera.journal.JournalCleanup"
 */
public class JournalCleanup extends SecurableStandardLogEnabledModel
{
	@Override
	public ModelResponse execute(ModelRequest request) throws ModelException
	{
		JournalManager journalManager = (JournalManager) SpringTools.getBean(JournalManager.ID);
		journalManager.journalCleanup();
		ModelResponse response = request.createResponse();
		String backModel = request.getParameterAsString("backModel");
		if (StringTools.isNotTrimEmpty(backModel))
		{
			Command back = response.createCommand(backModel);
			back.setName("back");
			back.setLabel("back");
			response.add(back);
		}
		return response;
	}
}
