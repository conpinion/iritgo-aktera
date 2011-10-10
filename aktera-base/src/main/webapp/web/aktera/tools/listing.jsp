<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.keelframework.org/struts/keel-1.0" prefix="keel" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/logic-2.1" prefix="xlogic" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean" %>

<bean:define scope="page" id="listId" type="java.lang.String">
	<%= request.getParameter ("listId") != null ? request.getParameter ("listId") : "list" %>
</bean:define>

<bean:define scope="page" id="width" type="java.lang.String">
	<%= ((request.getParameter ("width") != null && ! "0".equals (request.getParameter ("width"))) ? request.getParameter ("width") : "100") + "%" %>
</bean:define>

<logic:present name="<%= listId %>">

	<table class="list-box" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1" width="0px" height="0px"/></td>
			<td class="c1" width="100%" height="0px"></td>
			<td class="r1"><html:img page="/aktera/images/blank.gif" styleClass="r1" width="0px" height="0px"/></td>
		</tr>
		<tr>
			<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2" width="0px" height="0px"/></td>
			<td class="content" width="100%">


	<bean:define id="bundle" type="java.lang.String" name="<%= listId %>" property="attributes.bundle"/>
	<bean:define id="columnCount" type="java.lang.Integer" name="<%= listId %>" property="attributes.columnCount"/>
	<bean:define id="keyName" type="java.lang.String" name="<%= listId %>" property="attributes.keyName"/>

	<logic:present name="<%= listId %>" property="attributes.headerTitle">
		<div class="list-title">
			<div class="t"><div class="b"><div class="l"><div class="r"><div class="bl"><div class="br"><div class="tl"><div class="tr">
				<table width="100%" cellpadding="0" cellspacing="0">
					<tr>
						<td width="100%">
							<h1><bean:message name="<%= listId %>" property="attributes.headerTitle" bundle="<%= bundle %>"/></h1>
						</td>
						<logic:equal name="<%= listId %>" property="attributes.commandStyle" value="toolbar">
						<logic:present name="<%= listId %>" property="attributes.commands">
						<logic:iterate id="command" name="<%= listId %>" property="attributes.commands.nested">
							<logic:equal name="command" property="attributes.style" value="title">
								<td>&nbsp;</td>
								<td>
									<keel:command link="true" name="command">
										<xhtml:img path="/aktera/images/std" name="command" property="attributes.icon"/>
									</keel:command>
								</td>
							</logic:equal>
						</logic:iterate>
						</logic:present>
						</logic:equal>
					</tr>
				</table>
			</div></div></div></div></div></div></div></div>
		</div>
		<div class="vgap" />
	</logic:present>

	<bean:define id="focus" type="java.lang.String" value=""/>
	<logic:present name="<%= listId %>" property="attributes.cmdSearch">
		<bean:define id="focus" type="java.lang.String" value="search"/>
	</logic:present>

	<bean:define id="formName" type="java.lang.String"><%= listId %>Form</bean:define>
	<bean:define id="formAction" type="java.lang.String" name="<%= listId %>" property="attributes.formAction"/>
	<bean:define id="searchInputName" type="java.lang.String"><%= listId %>Search</bean:define>
	<bean:define id="searchCategoryInputName" type="java.lang.String"><%= listId %>SearchCategory</bean:define>

	<xhtml:form formName="<%= formName %>" action="<%= formAction %>" method="post" focus="<%= focus %>">

	<table width="<%= width %>" cellpadding="0" cellspacing="0">
		<tr>
			<logic:present name="<%= listId %>" property="attributes.cmdSearch">
				<input type="hidden" name="MODEL_PARAMS_PARAM" value="" />
				<logic:present name="default" property="<%= searchCategoryInputName %>">
					<td>
						<logic:present name="<%= searchCategoryInputName %>" property="validValues">
							<xhtml:select name="default" property="<%= searchCategoryInputName %>" onchange="<%= listId + "DoSearch();" %>">
								<xhtml:optionsCollection bundle="<%= bundle %>" name="<%= searchCategoryInputName %>" property="validValues"/>
							</xhtml:select>
						</logic:present>
					</td>
					<td>&nbsp;</td>
				</logic:present>
				<td>
					<html:text name="default" property="<%= searchInputName %>" size="40" onkeypress="<%= listId + "SearchFieldAction(event);" %>"/>
				</td>
				<td width="8px">&nbsp;</td>
				<td>
					<xkeel:command name="<%= listId %>" property="attributes.cmdSearch" styleClass="form-button" icon="/aktera/images/std/tool-search-16">
						<bean:message name="<%= listId %>" property="attributes.cmdSearch.label" bundle="Aktera"/>
					</xkeel:command>
				</td>
			</logic:present>

			<td width="100%"></td>

			<logic:present name="<%= listId %>" property="attributes.cmdEdit">
				<td>
					<xkeel:command name="<%= listId %>" property="attributes.cmdEdit" styleClass="form-button" style="visibility:hidden;">
						<bean:message name="<%= listId %>" property="attributes.cmdEdit.label" bundle="Aktera"/>
					</xkeel:command>
				</td>
			</logic:present>

			<logic:notEqual name="<%= listId %>" property="attributes.commandStyle" value="toolbar">
			<logic:present name="<%= listId %>" property="attributes.commands">
				<logic:iterate id="command" name="<%= listId %>" property="attributes.commands.nested">

					<bean:define id="iconName" value="" type="java.lang.String"/>
					<logic:present name="command" property="attributes.icon">
						<bean:define id="iconName" type="java.lang.String">
							/aktera/images/std/<bean:write name="command" property="attributes.icon"/>
						</bean:define>
					</logic:present>

					<td align="center" valign="top">
						<xkeel:command name="command" styleClass="form-button" icon="<%= iconName %>">
							<bean:define id="cmdBundle" name="command" property="attributes.bundle" type="java.lang.String"/>
							<bean:define id="cmdLabel" name="command" property="label" type="java.lang.String"/>
							<bean:message key="<%= cmdLabel %>" bundle="<%= cmdBundle %>"/>
						</xkeel:command>
					</td>
				</logic:iterate>
			</logic:present>
			</logic:notEqual>

			<logic:present name="<%= listId %>" property="attributes.cmdNew">
				<bean:define id="uncheckAllFunction" type="java.lang.String"><%= listId %>UncheckAll()</bean:define>
				<td>
					<xkeel:command name="<%= listId %>" property="attributes.cmdNew" styleClass="form-button" icon="/aktera/images/std/tool-new-16" onclick="<%= uncheckAllFunction %>">
						<bean:message name="<%= listId %>" property="attributes.cmdNew.label" bundle="Aktera"/>
					</xkeel:command>
				</td>
			</logic:present>

			<logic:present name="<%= listId %>" property="attributes.cmdBack">
				<td>
					<xkeel:command name="<%= listId %>" property="attributes.cmdBack" styleClass="form-button" icon="/aktera/images/std/tool-cancel-16">
						<bean:message name="<%= listId %>" property="attributes.cmdBack.label" bundle="Aktera"/>
					</xkeel:command>
				</td>
			</logic:present>

		</tr>
	</table>

	<html:img page="/aktera/images/blank.gif" width="0px" height="4px"/>

	<table class="list" width="<%= width %>" cellspacing="0" cellpadding="4">

		<tr>
			<th width="0%">
				<input type="checkbox" name="selectAll" onclick="<%= listId %>CheckAll()">
			</th>
			<logic:iterate id="column" name="<%= listId %>" property="attributes.header.nested">

				<logic:notPresent name="column" property="attributes.hide">

					<bean:define id="titleBundle" type="java.lang.String" name="column" property="attributes.bundle"/>
					<bean:define id="titleWidth" type="java.lang.Integer" name="column" property="attributes.width"/>

					<th width="<%= titleWidth + "%" %>" align="left">

						<logic:present name="column" property="attributes.sortCommand">
							<xkeel:command link="true" name="column" property="attributes.sortCommand">
								<nobr>
								<bean:message name="column" property="attributes.label" bundle="<%= titleBundle %>"/>
								<logic:present name="column" property="attributes.sort">
									<logic:equal name="column" property="attributes.sort" value="D">
										<xhtml:themeImg src="sort-up"/>
									</logic:equal>
									<logic:equal name="column" property="attributes.sort" value="U">
										<xhtml:themeImg src="sort-down"/>
									</logic:equal>
								</logic:present>
								</nobr>
							</xkeel:command>
						</logic:present>
						<logic:notPresent name="column" property="attributes.sortCommand">
							<nobr>
							<bean:message name="column" property="attributes.label" bundle="<%= titleBundle %>"/>
							</nobr>
						</logic:notPresent>

					</th>

				</logic:notPresent>

			</logic:iterate>
			<logic:present name="<%= listId %>" property="attributes.itemCommands">
				<th>&nbsp;</th>
			</logic:present>
		</tr>

		<logic:iterate id="item" name="<%= listId %>" property="nested">

			<logic:notPresent name="item" property="attributes.empty">

				<bean:define id="itemId" type="java.lang.String" name="item" property="attributes.id"/>

				<logic:equal name="item" property="attributes.odd" value="true">
					<tr class="odd">
				</logic:equal>

				<logic:equal name="item" property="attributes.even" value="true">
					<tr class="even">
				</logic:equal>

					<td width="0%" valign="top">
						<input type="checkbox" name="<%= keyName %>" value="<%= itemId %>">
					</td>

					<logic:iterate id="column" name="item" property="nested">

						<logic:notPresent name="column" property="attributes.hide">

							<bean:define id="align" type="java.lang.String">
								<xlogic:switch name="column" property="attributes.viewer">
									<xlogic:case value="error">left</xlogic:case>
									<xlogic:case value="text">left</xlogic:case>
									<xlogic:case value="date">left</xlogic:case>
									<xlogic:case value="time">left</xlogic:case>
									<xlogic:case value="datetime">left</xlogic:case>
									<xlogic:case value="timestamp">left</xlogic:case>
									<xlogic:case value="check">center</xlogic:case>
									<xlogic:case value="icon">center</xlogic:case>
									<xlogic:case value="combo">left</xlogic:case>
									<xlogic:case value="message">left</xlogic:case>
									<xlogic:case value="weekday">left</xlogic:case>
									<xlogic:case value="year">left</xlogic:case>
									<xlogic:case value="month">left</xlogic:case>
									<xlogic:case value="day">left</xlogic:case>
									<xlogic:case value="country">left</xlogic:case>
									<xlogic:case value="province">left</xlogic:case>
									<xlogic:case value="js">center</xlogic:case>
									<xlogic:case value="bytesize">left</xlogic:case>
									<xlogic:default>left</xlogic:default>
								</xlogic:switch>
							</bean:define>

							<bean:define id="valign" type="java.lang.String">
								<xlogic:switch name="column" property="attributes.viewer">
									<xlogic:case value="icon">middle</xlogic:case>
									<xlogic:case value="icon">middle</xlogic:case>
									<xlogic:case value="js">middle</xlogic:case>
									<xlogic:default>top</xlogic:default>
								</xlogic:switch>
							</bean:define>

							<bean:define id="cellId" type="java.lang.String">
								<%= listId %>-<bean:write name="column" property="name"/>-<%= itemId %>
							</bean:define>

							<bean:define id="value" type="java.lang.String">

								<xlogic:switch name="column" property="attributes.viewer">

									<xlogic:case value="error">
										<span class="error"><bean:write name="column" property="content"/></span>&nbsp;
									</xlogic:case>

									<xlogic:case value="text">
										<bean:write name="column" property="content"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="date">
										<bean:write name="column" property="content" formatKey="0.date.short" bundle="Aktera"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="time">
										<bean:write name="column" property="content" formatKey="0.time.short" bundle="Aktera"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="datetime">
										<bean:write name="column" property="content" formatKey="0.date.long" bundle="Aktera"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="timestamp">
										<bean:write name="column" property="content" formatKey="0.date.long" bundle="Aktera"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="check">
										<logic:equal name="column" property="content" value="true">
											&radic;
										</logic:equal>
										<logic:notEqual name="column" property="content" value="true">
											&nbsp;
										</logic:notEqual>
									</xlogic:case>

									<xlogic:case value="icon">
										<bean:define id="imageName" name="column" property="content" type="java.lang.String"/>
										<xhtml:img src="<%= "/aktera/images/std/" + imageName %>" align="center"/>
									</xlogic:case>

									<xlogic:case value="js">
										<bean:define id="imageName" name="column" property="content" type="java.lang.String"/>
										<div id="<%= cellId %>">
											<xhtml:img src="<%= "/aktera/images/std/" + imageName %>" align="center"/>
										</div>
									</xlogic:case>

									<xlogic:case value="combo">
										<bean:define id="cellBundle" name="column" property="attributes.bundle" type="java.lang.String"/>
										<bean:define id="cellText" name="column" property="content" type="java.lang.String"/>
										<bean:message key="<%= cellText %>" bundle="<%= cellBundle %>"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="message">
										<bean:define id="cellBundle" name="column" property="attributes.bundle" type="java.lang.String"/>
										<bean:define id="cellText" name="column" property="content" type="java.lang.String"/>
										<xbean:message key="<%= cellText %>" bundle="<%= cellBundle %>"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="weekday">
										<xhtml:weekdaySelect readOnly="true" name="column" property="content" bundle="Aktera"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="year">
										<xhtml:yearSelect readOnly="true" name="column" property="content" bundle="Aktera"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="month">
										<xhtml:monthSelect readOnly="true" name="column" property="content" bundle="Aktera"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="day">
										<xhtml:daySelect readOnly="true" name="column" property="content" bundle="Aktera"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="country">
										<xhtml:countrySelect readOnly="true" name="column" property="content" bundle="Aktera"/>&nbsp;
									</xlogic:case>

									<xlogic:case value="province">
										<xhtml:provinceSelect readOnly="true" name="column" property="content" bundle="Aktera" />&nbsp;
									</xlogic:case>

									<xlogic:case value="bytesize">
										<xhtml:byteSize readOnly="true" name="column" property="content" />&nbsp;
									</xlogic:case>

									<xlogic:default><b><font color="#DD0000">!!! Unknown viewer: <bean:write name="column" property="attributes.viewer"/> !!!!</font></b></xlogic:default>

								</xlogic:switch>

							</bean:define>

							<xlogic:switch name="column" property="attributes.viewer">
								<xlogic:case value="js">
									<td align="<%= align %>" valign="<%= valign %>" onclick="<%= listId %>CellClicked(event, '<%= cellId %>', '<%= itemId %>')">
										<%= value %>
									</td>
								</xlogic:case>
								<xlogic:default>
									<td align="<%= align %>" valign="<%= valign %>" onClick="<%= listId %>Edit('<%= itemId %>')">
										<%= value %>
									</td>
								</xlogic:default>
							</xlogic:switch>

						</logic:notPresent>

					</logic:iterate>

					<logic:present name="<%= listId %>" property="attributes.itemCommands">
						<td>
							<nobr>

							<logic:iterate id="command" name="<%= listId %>" property="attributes.itemCommands.nested">
							<logic:present name="command" property="attributes.style">

							<logic:equal name="command" property="attributes.style" value="tool">
								<logic:present name="command" property="model">
									<bean:define id="model" name="command" property="model" type="java.lang.String"/>
									<a href="javascript: <%= listId %>Execute('<%= itemId %>','<%= model %>')">
										<xhtml:img path="/aktera/images/std/" name="command" property="attributes.icon" border="0" align="baseline"/>
									</a>
								</logic:present>
								<logic:notPresent name="command" property="model">
									<bean:define id="bean" name="command" property="bean" type="java.lang.String"/>
									<a href="javascript: <%= listId %>ExecuteBean('<%= itemId %>','<%= bean %>')">
										<xhtml:img path="/aktera/images/std/" name="command" property="attributes.icon" border="0" align="baseline"/>
									</a>
								</logic:notPresent>
							</logic:equal>

							<logic:equal name="command" property="attributes.style" value="toollink">
								<xkeel:command link="true" name="command" params="<%= "id=" + itemId %>" target="_blank">
									<xhtml:img path="/aktera/images/std/" name="command" property="attributes.icon" border="0" align="baseline"/>
								</xkeel:command>
							</logic:equal>

							</logic:present>
							</logic:iterate>
							</nobr>
						</td>
					</logic:present>

				</tr>

			</logic:notPresent>

			<logic:present name="item" property="attributes.empty">

				<tr class="empty">
					<td colspan="<%= columnCount.intValue () + 1 %>">&nbsp;</td>
				</tr>

			</logic:present>

		</logic:iterate>

	</table>

	<logic:notEqual name="<%= listId %>" property="attributes.pageCount" value="1">
		<div class="vgap"/>
		<table width="<%= width %>" cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<%@ include file="page-navigation.jsp" %>
				</td>
			</tr>
		</table>
	</logic:notEqual>

	<logic:present name="<%= listId %>" property="attributes.itemCommands">
		<div class="vgap"/>
		<table cellspacing="0" cellpadding="0">
			<tr>
				<td>
					<select name="<%= listId %>ExecuteModel" style="width:24em"">
						<option value="null"><bean:message key="chooseItemAction" bundle="Aktera"/></option>
						<logic:iterate id="command" name="<%= listId %>" property="attributes.itemCommands.nested">
							<logic:notEqual name="command" property="attributes.style" value="toollink">
								<logic:present name="command" property="model">
									<bean:define id="bundle" type="java.lang.String" name="command" property="attributes.bundle"/>
									<bean:define id="model" type="java.lang.String" name="command" property="model"/>
									<option value="<%= model %>"><bean:message name="command" property="label" bundle="<%= bundle %>"/></option>
								</logic:present>
								<logic:notPresent name="command" property="model">
									<bean:define id="bundle" type="java.lang.String" name="command" property="attributes.bundle"/>
									<bean:define id="bean" type="java.lang.String" name="command" property="bean"/>
									<option value="_BEAN_.<%= bean %>"><bean:message name="command" property="label" bundle="<%= bundle %>"/></option>
								</logic:notPresent>
							</logic:notEqual>
						</logic:iterate>
					</select>
				</td>
				<td width="8px">&nbsp;</td>
				<td>
					<xkeel:command name="<%= listId %>" property="attributes.cmdExecute" styleClass="form-button" icon="/aktera/images/std/tool-execute-16">
						<bean:message name="<%= listId %>" property="attributes.cmdExecute.label" bundle="Aktera"/>
					</xkeel:command>
				</td>
			</tr>
		</table>
	</logic:present>

</xhtml:form>

<script type="text/javascript" language="JavaScript">
<!--
	function <%= listId %>CheckAll()
	{
		if (document.<%= formName %>.<%= keyName %>.length == undefined)
		{
			document.<%= formName %>.<%= keyName %>.checked = document.<%= formName %>.selectAll.checked;
		}
		else
		{
			for (i = 0; i < document.<%= formName %>.<%= keyName %>.length; i++)
			{
				document.<%= formName %>.<%= keyName %>[i].checked = document.<%= formName %>.selectAll.checked;
			}
		}
	}

	function <%= listId %>DoSearch()
	{
		document.<%= formName %>.MODEL_PARAMS_PARAM.value = '<%= listId %>CmdSearch';
		document.<%= formName %>.COMMAND_<%= listId %>CmdSearch.click();
	}

	function <%= listId %>SearchFieldAction(event)
	{
		if (event.keyCode == 13)
		{
			<%= listId %>DoSearch();
		}
	}

	function <%= listId %>Execute(anId,model)
	{
		if (! document.<%= formName %>.COMMAND_<%= listId %>CmdExecute ||
			model == '')
		{
			return;
		}

		if (! document.<%= formName %>.<%= keyName %>.length)
		{
			document.<%= formName %>.<%= keyName %>.checked = true;
		}
		else
		{
			for (i = 0; i < document.<%= formName %>.<%= keyName %>.length; i++)
			{
				document.<%= formName %>.<%= keyName %>[i].checked = (document.<%= formName %>.<%= keyName %>[i].value == anId);
			}
		}
		document.<%= formName %>.<%= listId %>ExecuteModel.value = model;
		document.<%= formName %>.COMMAND_<%= listId %>CmdExecute.click();
	}

	function <%= listId %>ExecuteBean(anId,model)
	{
		<%= listId %>Execute(anId, "_BEAN_." + model);
	}

	function <%= listId %>Edit(anId)
	{
		if (document.<%= formName %>.COMMAND_<%= listId %>CmdEdit == undefined)
		{
			return;
		}

		if (document.<%= formName %>.<%= keyName %>.length == undefined)
		{
			document.<%= formName %>.<%= keyName %>.checked = true;
		}
		else
		{
			for (i = 0; i < document.<%= formName %>.<%= keyName %>.length; i++)
			{
				document.<%= formName %>.<%= keyName %>[i].checked = (document.<%= formName %>.<%= keyName %>[i].value == anId);
			}
		}
		document.<%= formName %>.COMMAND_<%= listId %>CmdEdit.click();
	}

// -->
</script>

			</td>
			<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2" width="0px" height="0px"/></td>
		</tr>
		<tr>
			<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3" width="0px" height="0px"/></td>
			<td class="c3" width="100%" height="0px"></td>
			<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3" width="0px" height="0px"/></td>
		</tr>
	</table>

</logic:present>

</span>
