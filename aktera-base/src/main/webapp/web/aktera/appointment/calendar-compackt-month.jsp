<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.keelframework.org/struts/keel-1.0" prefix="keel" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>

<table class="calendar-box" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1" width="0px" height="0px"/></td>
		<td class="c1" width="100%" height="0px"></td>
		<td class="r1"><html:img page="/aktera/images/blank.gif" styleClass="r1" width="0px" height="0px"/></td>
	</tr>
	<tr>
		<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2" width="0px" height="0px"/></td>
		<td class="content" width="100%">

<table class="calendar">

	<tr class="control">
		<td colspan="8">
			<table cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<keel:command styleId="calendarPrevYear" name="calendar" property="attributes.cmdPrevYear" link="true" onclick="javascript:calendarPrevYear()">
							<xhtml:themeImg src="calendar-fast-back" border="0" align="middle"/>
						</keel:command>
					</td>
					<td>
						<keel:command styleId="calendarPrevMonth" name="calendar" property="attributes.cmdPrevMonth" link="true" onclick="javascript:calendarPrevMonth()">
							<xhtml:themeImg src="calendar-back" border="0" align="middle"/>
						</keel:command>
					</td>
					<td width="100%" align="center">
						<nobr>
						<div id="calendarTitle"><bean:write name="calendar" property="attributes.showDate" formatKey="0.date.monthAndYear" bundle="Aktera"/></div>
						</nobr>
					</td>
					<td>
						<keel:command styleId="calendarNextMonth" name="calendar" property="attributes.cmdNextMonth" link="true" onclick="javascript:calendarNextMonth()">
							<xhtml:themeImg src="calendar-next" border="0" align="middle"/>
						</keel:command>
					</td>
					<td>
						<keel:command styleId="calendarNextYear" name="calendar" property="attributes.cmdNextYear" link="true" onclick="javascript:calendarNextYear()">
							<xhtml:themeImg src="calendar-fast-next" border="0" align="middle"/>
						</keel:command>
					</td>
				</tr>
			</table>
		</td>
	</tr>

	<tr class="header">
		<td>Kw</td>
		<td>Mo</td>
		<td>Di</td>
		<td>Mi</td>
		<td>Do</td>
		<td>Fr</td>
		<td>Sa</td>
		<td>So</td>
	</tr>

	<% int weekNum = 0; int dayNum = 0; %>
	<logic:iterate id="week" name="calendar" property="attributes.weeks.nested">
		<tr>
			<td id="<%= "calendarWeek" + weekNum %>" class="header"><bean:write name="week" property="attributes.num"/></td>
			<logic:iterate id="day" name="week" property="nested">

				<logic:present name="day" property="attributes.isCurrent">
					<td id="<%= "calendarDay" + dayNum %>" class="day-selected">
				</logic:present>
				<logic:present name="day" property="attributes.isSecondary">
					<td id="<%= "calendarDay" + dayNum %>" class="day-secondary">
				</logic:present>
				<logic:notPresent name="day" property="attributes.isCurrent">
					<logic:notPresent name="day" property="attributes.isSecondary">
						<td class="day" id="<%= "calendarDay" + dayNum %>">
					</logic:notPresent>
				</logic:notPresent>

				<logic:present name="day" property="attributes.cmdDoDay">
					<keel:command name="day" property="attributes.cmdDoDay" link="true">
						<bean:write name="day" property="attributes.num"/>
					</keel:command>
				</logic:present>
				<logic:notPresent name="day" property="attributes.cmdDoDay">
					<bean:write name="day" property="attributes.num"/>
				</logic:notPresent>

				</td>

				<% dayNum += 1; %>

			</logic:iterate>

		</tr>

		<% weekNum += 1; %>

	</logic:iterate>

	<tr class="footer">

		<td colspan="8" align="center" class="footer">
			<keel:command name="calendar" property="attributes.cmdCurrentMonth" link="true">
				<div id="calendarDate"><bean:write name="calendar" property="attributes.currentDate" formatKey="0.date.short" bundle="Aktera"/></div>
			</keel:command>
		</td>
	</tr>

</table>

		</td>
		<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2" width="0px" height="0px"/></td>
	</tr>
	<tr>
		<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3" width="0px" height="0px"/></td>
		<td class="c3" width="100%" height="0px"></td>
		<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3" width="0px" height="0px"/></td>
	</tr>
</table>

<script type="text/javascript">
<!--

var calendarDate = new Date ();

var calendarMonthNames = new Array (
	"Januar", "Februar", "M�rz", "April", "Mai", "Juni", "Juli",
	"August", "September", "Oktober", "November", "Dezember");

var calendarMonthShortNames = new Array (
	"Jan", "Feb", "M�r", "Apr", "Mai", "Jun", "Jul",
	"Aug", "Sep", "Okt", "Nov", "Dez");

registerBodyLoadFunction (calendarInitialize);

function calendarInitialize ()
{
	document.getElementById ("calendarPrevYear").href = "javascript:;";
	document.getElementById ("calendarPrevMonth").href = "javascript:;";
	document.getElementById ("calendarNextMonth").href = "javascript:;";
	document.getElementById ("calendarNextYear").href = "javascript:;";

	calendarUpdate ();
}

function calendarUpdate ()
{
	var elemTitle = document.getElementById ("calendarTitle");
	elemTitle.replaceChild (document.createTextNode (
		calendarMonthNames[calendarDate.getMonth ()] + " " + calendarDate.getFullYear ()),
		elemTitle.firstChild);

	var elemDate = document.getElementById ("calendarDate");
	elemDate.replaceChild (document.createTextNode (
		calendarDate.getDate () + ". " + calendarMonthShortNames[calendarDate.getMonth ()] + ". " + calendarDate.getFullYear ()),
		elemDate.firstChild);

	for (var row = 0; row <= 5; ++row)
	{
		for (var col = 0; col <= 6; ++col)
		{
			var elemDay = document.getElementById ("calendarDay" + (row * 7 + col));
			elemDay.replaceChild (document.createTextNode (col), elemDay.firstChild);
		}
	}
}

function calendarPrevMonth ()
{
	if (calendarDate.getMonth () == 0)
	{
		calendarDate.setMonth (11);
		calendarDate.setFullYear (calendarDate.getFullYear () - 1);
	}
	else
	{
		calendarDate.setMonth (calendarDate.getMonth () - 1);
	}
	calendarUpdate ();
}

function calendarNextMonth ()
{
	if (calendarDate.getMonth () == 11)
	{
		calendarDate.setMonth (0);
		calendarDate.setFullYear (calendarDate.getFullYear () + 1);
	}
	else
	{
		calendarDate.setMonth (calendarDate.getMonth () + 1);
	}
	calendarUpdate ();
}

function calendarPrevYear ()
{
	calendarDate.setFullYear (calendarDate.getFullYear () - 1);
	calendarUpdate ();
}

function calendarNextYear ()
{
	calendarDate.setFullYear (calendarDate.getFullYear () + 1);
	calendarUpdate ();
}

-->
</script>
