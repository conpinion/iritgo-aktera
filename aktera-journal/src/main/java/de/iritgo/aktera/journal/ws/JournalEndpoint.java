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

package de.iritgo.aktera.journal.ws;


import java.sql.Timestamp;
import java.util.*;
import javax.xml.datatype.*;
import lombok.Setter;
import javax.inject.Inject;
import org.springframework.ws.server.endpoint.annotation.*;
import de.iritgo.aktera.authentication.SecurityContext;
import de.iritgo.aktera.authentication.defaultauth.entity.AkteraUser;
import de.iritgo.aktera.journal.*;
import de.iritgo.aktera.webservices.journal.*;
import de.iritgo.simplelife.constants.SortOrder;
import de.iritgo.simplelife.string.StringTools;


@Endpoint("de.iritgo.aktera.journal.JournalEndpoint")
public class JournalEndpoint
{
	@Setter
	@Inject
	private JournalManager journalManager;

	@Setter
	@Inject
	private JournalDAO journalDAO;

	@Setter
	@Inject
	private SecurityContext securityContext;

	@PayloadRoot(localPart = "countJournalRequest", namespace = "http://aktera.iritgo.de/webservices/journal")
	public CountJournalResponse count(CountJournalRequest request) throws Exception
	{
		AkteraUser user = securityContext.getUser();
		CountJournalResponse response = new CountJournalResponse();

		response.setCount((int) journalManager.countJournalEntries(request.getQuery(), new Timestamp(0), new Timestamp(
						new Date().getTime()), user.getId(), ""));

		return response;
	}

	@PayloadRoot(localPart = "listJournalRequest", namespace = "http://aktera.iritgo.de/webservices/journal")
	public ListJournalResponse list(ListJournalRequest request) throws Exception
	{
		AkteraUser user = securityContext.getUser();
		int firstResult = request.getFirstResult() != null ? request.getFirstResult().intValue() : 0;
		int maxResults = request.getMaxResults() != null ? request.getMaxResults().intValue() : 100;
		SortOrder orderDir = request.getOrderDir() != null ? SortOrder.byId(request.getOrderDir())
						: SortOrder.ASCENDING;
		String orderBy = StringTools.isNotTrimEmpty(request.getOrderBy()) ? request.getOrderBy() : "journal.occurredAt";

		ListJournalResponse response = new ListJournalResponse();
		DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();

		for (Map<String, Object> entry : journalManager.listJournalEntries(request.getQuery(), new Timestamp(0),
						new Timestamp(new Date().getTime()), user.getId(), "", orderBy, orderDir, firstResult,
						maxResults))
		{
			ListJournalResponse.Entry entryElement = new ListJournalResponse.Entry();

			entryElement.setId((long) (Integer) entry.get("id"));
			entryElement.setPrimaryType((String) entry.get("primaryType"));
			entryElement.setSecondaryType((String) entry.get("secondaryType"));
			entryElement.setSecondaryTypeText((String) entry.get("secondaryTypeText"));
			entryElement.setMessage(StringTools.trim(entry.get("message")));
			entryElement.setShortMessage(StringTools.trim(entry.get("shortMessage")));
			entryElement.setSource(StringTools.trim(entry.get("source")));

			Calendar entryCal = GregorianCalendar.getInstance();

			entryCal.setTimeInMillis(((Timestamp) entry.get("occurredAt")).getTime());

			XMLGregorianCalendar xmlCal = datatypeFactory.newXMLGregorianCalendar();

			xmlCal.setHour(entryCal.get(Calendar.HOUR_OF_DAY));
			xmlCal.setMinute(entryCal.get(Calendar.MINUTE));
			xmlCal.setSecond(entryCal.get(Calendar.SECOND));
			xmlCal.setDay(entryCal.get(Calendar.DAY_OF_MONTH));
			xmlCal.setMonth(entryCal.get(Calendar.MONTH) + 1);
			xmlCal.setYear(entryCal.get(Calendar.YEAR));
			entryElement.setOccurredAt(xmlCal);
			response.getEntry().add(entryElement);
		}

		return response;
	}
}
