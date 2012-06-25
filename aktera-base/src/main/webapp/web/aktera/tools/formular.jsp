<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/logic-2.1" prefix="xlogic" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean" %>

<bean:define id="focusField" value=""/>

<logic:present name="focus">
	<bean:define id="focusField" name="focus" property="content" type="java.lang.String"/>
</logic:present>

<bean:define id="formAction" type="java.lang.String" name="formAction" property="content" />

<xhtml:form formName="formular" action="<%= formAction %>" method="post" enctype="multipart/form-data" focus="<%= focusField %>">

	<input type="hidden" name="AKTERA_auto" value="false"/>
	<bean:define id="currentPage" name="currentPage" property="content" type="java.lang.Integer"/>
	<input type="hidden" name="AKTERA_page" value="<%= currentPage %>"/>

	<logic:present name="default" property="id">
		<html:hidden name="default" property="id"/>
	</logic:present>

	<logic:present name="groups">

		<bean:define id="labelWidth" name="labelWidth" property="content" type="java.lang.Integer"/>
		<bean:define id="fieldWidth" type="java.lang.String" value="<%= String.valueOf (100 - labelWidth.intValue ()) %>"/>

		<table class="form" width="100%" cellpadding="0" cellspacing="0">

			<logic:notPresent name="pages">
				<tr class="form">
					<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1" width="0px" height="0px"/></td>
					<td class="c1" width="100%" height="0px"></td>
					<td class="r1"><html:img page="/aktera/images/blank.gif" styleClass="r1" width="0px" height="0px"/></td>
				</tr>
			</logic:notPresent>

			<logic:present name="pages">
				<tr class="pages">
					<td colspan="3">

						<table class="pages" cellspacing="0" cellpadding="0" width="100%">
							<tr width="100%">
								<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1" width="0px" height="0px"/></td>
								<logic:iterate id="aPage" name="pages" property="nested">
									<bean:define id="pageStyle" type="java.lang.String" name="aPage" property="attributes.style"/>
									<td class="<%= pageStyle + "-l1" %>"><html:img page="/aktera/images/blank.gif" styleClass="<%= pageStyle + "-l1" %>" width="0px" height="0px"/></td>
									<td class="<%= pageStyle + "-c1" %>" height="0px"></td>
									<td class="<%= pageStyle + "-r1" %>"><html:img page="/aktera/images/blank.gif" styleClass="<%= pageStyle + "-r1" %>" width="0px" height="0px"/></td>
								</logic:iterate>
								<td class="c1" width="100%"></td>
								<td class="r1" ><html:img page="/aktera/images/blank.gif" styleClass="r1" width="0px" height="0px"/></td>
							</tr>
							<tr width="100%">
								<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2" width="0px" height="0px"/></td>
								<logic:iterate id="aPage" name="pages" property="nested">

									<bean:define id="bundle" name="aPage" property="attributes.bundle" type="java.lang.String"/>
									<bean:define id="label" name="aPage" property="label" type="java.lang.String"/>
									<bean:define id="name" name="aPage" property="name" type="java.lang.String"/>
									<bean:define id="num" name="aPage" property="attributes.page" type="java.lang.String"/>
									<bean:define id="pageStyle" type="java.lang.String" name="aPage" property="attributes.style"/>

									<td class="<%= pageStyle + "-l2" %>"><html:img page="/aktera/images/blank.gif" styleClass="<%= pageStyle + "-l2" %>" width="0px" height="0px"/></td>
									<td class="<%= pageStyle + "-title" %>">
										<logic:present name="aPage" property="attributes.active">
											<nobr>
											<logic:present name="aPage" property="attributes.icon">
												<xhtml:img path="/aktera/images/std/" name="aPage" property="attributes.icon" border="0" align="top"/>
											</logic:present>
											<bean:message key="<%= label %>" bundle="<%= bundle %>"/>
											</nobr>
										</logic:present>
										<logic:notPresent name="aPage" property="attributes.active">
											<a href="javascript: showPage(<%= num %>)">
												<nobr>
												<logic:present name="aPage" property="attributes.inactiveIcon">
													<xhtml:img path="/aktera/images/std/" name="aPage" property="attributes.inactiveIcon" border="0" align="top "/>
												</logic:present>
												<bean:message key="<%= label %>" bundle="<%= bundle %>"/>
												</nobr>
											</a>
										</logic:notPresent>
									</td>
									<td class="<%= pageStyle + "-r2" %>"><html:img page="/aktera/images/blank.gif" styleClass="<%= pageStyle + "-r2" %>" width="0px" height="0px"/></td>
								</logic:iterate>
								<td class="c2" width="100%"></td>
								<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2" width="0px" height="0px"/></td>
							</tr>
							<tr width="100%">
								<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3" width="0px" height="0px"/></td>
								<logic:iterate id="aPage" name="pages" property="nested">
									<bean:define id="pageStyle" type="java.lang.String" name="aPage" property="attributes.style"/>
									<td class="<%= pageStyle + "-l3" %>"><html:img page="/aktera/images/blank.gif" styleClass="<%= pageStyle + "-l3" %>" width="0px" height="0px"/></td>
									<td class="<%= pageStyle + "-c3" %>" height="0px"></td>
									<td class="<%= pageStyle + "-r3" %>"><html:img page="/aktera/images/blank.gif" styleClass="<%= pageStyle + "-r3" %>" width="0px" height="0px"/></td>
								</logic:iterate>
								<td class="c3" width="100%"></td>
								<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3" width="0px" height="0px"/></td>
							</tr>
						</table>

					</td>
				</tr>
			</logic:present>

			<tr class="form">
				<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2" width="0px" height="0px"/></td>
				<td class="content" width="100%">

					<bean:define id="formContentClass" type="java.lang.String" value="form-content"/>
					<logic:present name="groups" property="attributes.readOnly">
						<bean:define id="formContentClass" type="java.lang.String" value="form-content-readonly"/>
					</logic:present>

					<table class="<%= formContentClass %>" width="100%" cellpadding="0" cellspacing="0">

						<logic:iterate id="group" name="groups" property="nested">

							<bean:define id="bundle" name="group" property="attributes.bundle" type="java.lang.String"/>
							<bean:define id="label" name="group" property="content" type="java.lang.String"/>

							<logic:notPresent name="group" property="attributes.hideTitle">
								<tr width="100%">
									<td colspan="3">

										<table class="group-title" cellspacing="0" cellpadding="0" width="100%">
											<tr class="group-title">
												<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1" width="0px" height="0px"/></td>
												<td class="c1" width="100%" height="0px"></td>
												<td class="r1"><html:img page="/aktera/images/blank.gif" styleClass="r1" width="0px" height="0px"/></td>
											</tr>
											<tr class="group-title">
												<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2" width="0px" height="0px"/></td>
												<td class="title" width="100%">

													<logic:present name="group" property="attributes.icon">
														<xhtml:img path="/aktera/images/std/" name="group" property="attributes.icon" border="0" align="top"/>
													</logic:present>
													<bean:message key="<%= label %>" bundle="<%= bundle %>"/>

												</td>
												<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2" width="0px" height="0px"/></td>
											</tr>
											<tr class="group-title">
												<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3" width="0px" height="0px"/></td>
												<td class="c3" width="100%" height="0px"></td>
												<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3" width="0px" height="0px"/></td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td colspan="3" class="groupsep"></td>
								</tr>
							</logic:notPresent>

							<logic:iterate id="field" name="group" property="nested">

								<bean:define id="fieldName" name="field" property="name" type="java.lang.String"/>
								<bean:define id="bundle" name="field" property="attributes.bundle" type="java.lang.String"/>

								<logic:notPresent name="field" property="attributes.desc">
								<logic:notPresent name="field" property="attributes.jsp">
								<logic:notEqual name="field" property="attributes.editor" value="hidden">

									<xkeel:hasError property="<%= fieldName %>">
										<tr class="error">
									</xkeel:hasError>
									<xkeel:notHasError property="<%= fieldName %>">
										<logic:notPresent name="group" property="attributes.readOnly">
											<tr>
										</logic:notPresent>
										<logic:present name="group" property="attributes.readOnly">
											<logic:equal name="field" property="attributes.odd" value="true">
												<tr class="odd">
											</logic:equal>
											<logic:equal name="field" property="attributes.even" value="true">
												<tr class="even">
											</logic:equal>
										</logic:present>
									</xkeel:notHasError>

									<bean:define id="labelAlign" type="java.lang.String">

										<xlogic:switch name="field" property="attributes.editor">

											<xlogic:case value="textarea">top</xlogic:case>
											<xlogic:case value="list">top</xlogic:case>
											<xlogic:case value="dipswitch">top</xlogic:case>
											<xlogic:case value="seconds">top</xlogic:case>
											<xlogic:case value="minutes">top</xlogic:case>
											<xlogic:case value="hours">top</xlogic:case>
											<xlogic:case value="days">top</xlogic:case>
											<xlogic:case value="months">top</xlogic:case>
											<xlogic:case value="weekdays">top</xlogic:case>
											<xlogic:default>middle</xlogic:default>

										</xlogic:switch>

									</bean:define>

									<bean:define id="fieldColSpan" type="java.lang.String" value="1"/>
									<bean:define id="fieldClass" type="java.lang.String" value="formitem"/>

									<logic:notPresent name="field" property="attributes.noLabel">

										<td class="formlabel" valign="<%= labelAlign %>" width="<%= labelWidth %>%">
											<logic:notPresent name="field" property="attributes.toolTip">
												<xbean:label name="field" property="label" bundle="<%= bundle %>"/>
											</logic:notPresent>
											<logic:present name="field" property="attributes.toolTip">
												<span class="tooltip"><xbean:label name="field" property="label" bundle="<%= bundle %>"/>
													<span><bean:message name="field" property="attributes.toolTip" bundle="<%= bundle %>"/></span>
												</span>
											</logic:present>
										</td>

										<td valign="<%= labelAlign %>">
											<logic:present name="field" property="attributes.duty">
												<logic:notPresent name="group" property="attributes.readOnly">
													<span class="duty">*</span>
												</logic:notPresent>
											</logic:present>
											<logic:notPresent name="field" property="attributes.duty">
												<logic:notPresent name="group" property="attributes.readOnly">
													<logic:notEqual name="labelWidth" value="0">
														<span class="duty"></span>
													</logic:notEqual>
												</logic:notPresent>
											</logic:notPresent>
										</td>

									</logic:notPresent>

									<logic:present name="field" property="attributes.noLabel">

										<bean:define id="fieldColSpan" type="java.lang.String" value="3"/>
										<bean:define id="fieldClass" type="java.lang.String" value="formlabel"/>

									</logic:present>

									<logic:notPresent name="field" property="attributes.readOnly">

										<bean:define id="disabled" type="java.lang.Boolean" name="field" property="attributes.disabled"/>
										
										<td class="<%= fieldClass %>" valign="<%= labelAlign %>" width="<%= fieldWidth %>%" colspan="<%= fieldColSpan %>">

											<logic:present name="field" property="attributes.selectable">
												<input type="checkbox" name="<%= fieldName + "_Selected" %>">
											</logic:present>

											<xlogic:switch name="field" property="attributes.editor">

												<xlogic:case value="text">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:text name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="message">
													<xbean:message name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="number">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:text name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="integer">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:text name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="digits">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:text name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="realnumber">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:text name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="textarea">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<logic:present name="field" property="attributes.rows">
														<bean:define id="fieldRows" name="field" property="attributes.rows" type="java.lang.Integer"/>
														<xhtml:textarea name="default" property="<%= fieldName %>" cols="<%= fieldSize.toString () %>" rows="<%= fieldRows.toString () %>" wrap="false"/>
													</logic:present>
													<logic:notPresent name="field" property="attributes.rows">
														<xhtml:textarea name="default" property="<%= fieldName %>" cols="<%= fieldSize.toString () %>" rows="" wrap="false"/>
													</logic:notPresent>
												</xlogic:case>

												<xlogic:case value="password">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:password name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="ipaddress">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:text name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="macaddress">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:text name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="email">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:text name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="nospacetext">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:text name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="identifier">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:text name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="language">
													<xhtml:languageSelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="salutation">
													<xhtml:salutationSelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="country">
													<logic:present name="field" property="attributes.submit">
														<xhtml:countrySelect name="default" property="<%= fieldName %>" bundle="Aktera" onchange="javascript:comboBoxSubmit()"/>
													</logic:present>
													<logic:notPresent name="field" property="attributes.submit">
														<xhtml:countrySelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
													</logic:notPresent>
												</xlogic:case>

												<xlogic:case value="province">
													<xhtml:provinceSelect name="default" property="<%= fieldName %>" countryProperty='<%= fieldName + "Country" %>' bundle="Aktera" />
												</xlogic:case>

												<xlogic:case value="weekday">
													<xhtml:weekdaySelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="weekdaycheck">
													<xhtml:weekDayCheck name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="year">
													<xhtml:yearSelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="month">
													<xhtml:monthSelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="day">
													<xhtml:daySelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="datetext">
													<html:text name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="datecombo">
													<xhtml:dateSelect name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="timetext">
													<html:text name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="timecombo">
													<xhtml:timeSelect name="default" property="<%= fieldName %>" disabled="<%= disabled %>"/>
												</xlogic:case>

												<xlogic:case value="timestamptext">
													<html:text name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="timestampcombo">
													<xhtml:dateSelect name="default" property="<%= fieldName %>"/><html:img page="/aktera/images/blank.gif" width="32" border="0"/><xhtml:timeSelect name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="check">
													<logic:present name="field" property="attributes.submit">
														<html:checkbox name="default" property="<%= fieldName %>" styleClass="checkbox" onchange="javascript:autoSubmit()"/>
													</logic:present>
													<logic:notPresent name="field" property="attributes.submit">
														<html:checkbox name="default" property="<%= fieldName %>" styleClass="checkbox"/>
													</logic:notPresent>
												</xlogic:case>

												<xlogic:case value="money">
													<xhtml:money name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="combo">
													<logic:present name="field" property="attributes.submit">
														<xhtml:select name="default" property="<%= fieldName %>" disabled="<%= disabled %>" onchange="javascript:comboBoxSubmit()">
															<logic:present name="field" property="validValues">
																<xhtml:optionsCollection bundle="<%= bundle %>" name="field" property="validValues" />
															</logic:present>
														</xhtml:select>
													</logic:present>
													<logic:notPresent name="field" property="attributes.submit">
														<xhtml:select name="default" property="<%= fieldName %>" disabled="<%= disabled %>" >
															<logic:present name="field" property="validValues">
																<xhtml:optionsCollection bundle="<%= bundle %>" name="field" property="validValues"/>
															</logic:present>
														</xhtml:select>
													</logic:notPresent>
												</xlogic:case>

												<xlogic:case value="list">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<bean:define id="listId" name="field" property="attributes.listId" type="java.lang.String"/>
													<jsp:include page="/aktera/tools/listing-embedded.jsp" flush="true">
														<jsp:param name="width" value="<%= fieldSize %>"/>
														<jsp:param name="listId" value="<%= listId %>"/>
													</jsp:include>
												</xlogic:case>

												<xlogic:case value="file">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<html:file name="default" property="<%= fieldName %>" size="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="dipswitch">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<xhtml:dipSwitch name="default" property="<%= fieldName %>" numberingOffset="1" cols="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="seconds">
													<xhtml:secondsMultiSelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="minutes">
													<xhtml:minutesMultiSelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="hours">
													<xhtml:hoursMultiSelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="days">
													<xhtml:daysMultiSelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="months">
													<xhtml:monthsMultiSelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="weekdays">
													<xhtml:weekdaysMultiSelect name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value=""></xlogic:case>

												<xlogic:default><b><font color="#DD0000">!!! Unknown editor: <bean:write name="field" property="attributes.editor"/> !!!</font></b></xlogic:default>

											</xlogic:switch>

											<logic:present name="field" property="attributes.commands">
												<nobr>
												<logic:iterate id="command" name="field" property="attributes.commands.nested">

													<bean:define id="label" name="command" property="label" type="java.lang.String"/>

													<bean:define id="iconName" value="" type="java.lang.String"/>
													<logic:present name="command" property="attributes.icon">
														<bean:define id="iconName" type="java.lang.String">
															/aktera/images/std/<bean:write name="command" property="attributes.icon"/>
														</bean:define>
													</logic:present>

													<bean:define id="bundle" value="Aktera" type="java.lang.String"/>
													<logic:present name="command" property="attributes.bundle">
														<bean:define id="bundle" name="command" property="attributes.bundle" type="java.lang.String"/>
													</logic:present>

													<bean:define id="confirmMessage" type="java.lang.String"><xbean:message name="command" property="attributes.confirm" bundle="<%= bundle %>" defaultMessage="__EMPTY__"/></bean:define>
													<bean:define id="confirm" type="java.lang.String">return buttonConfirmAndClick('<%= confirmMessage %>', this);</bean:define>

													<xkeel:command name="command" styleClass="form-button" icon="<%= iconName %>" onclick="<%= confirm %>">
														<logic:notEqual name="command" property="label" value="">
															<bean:message key="<%= label %>" bundle="<%= bundle %>"/>
														</logic:notEqual>
													</xkeel:command>

												</logic:iterate>
												</nobr>
											</logic:present>

										</td>

									</logic:notPresent>

									<logic:present name="field" property="attributes.readOnly">
										<td class="formitem-readonly" width="<%= fieldWidth %>%">

											<xlogic:switch name="field" property="attributes.editor">

												<xlogic:case value="text">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="message">
													<xbean:message name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="nospacetext">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="identifier">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="number">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="integer">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="digits">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="realnumber">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="textarea">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="password">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="ipaddress">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="macaddress">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="email">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="language">
													<xhtml:languageSelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="salutation">
													<xhtml:salutationSelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="country">
													<xhtml:countrySelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="province">
													<xhtml:provinceSelect readOnly="true" name="default" property="<%= fieldName %>" countryProperty='<%= fieldName + "Country" %>' bundle="Aktera" country="DE"/>
												</xlogic:case>

												<xlogic:case value="weekday">
													<xhtml:weekdaySelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="weekdaycheck">
													<xhtml:weekDayCheck readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="year">
													<xhtml:yearSelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="month">
													<xhtml:monthSelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="day">
													<xhtml:daySelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="datetext">
													<bean:write name="default" property="<%= fieldName %>" formatKey="0.date.short" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="datecombo">
													<bean:write name="default" property="<%= fieldName %>" formatKey="0.date.short" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="timetext">
													<bean:write name="default" property="<%= fieldName %>" formatKey="0.time.short" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="timecombo">
													<bean:write name="default" property="<%= fieldName %>" formatKey="0.time.short" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="timestamptext">
													<bean:write name="default" property="<%= fieldName %>" formatKey="0.date.long" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="timestampcombo">
													<bean:write name="default" property="<%= fieldName %>" formatKey="0.date.long" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="check">
													<logic:equal name="default" property="<%= fieldName %>" value="true">
														<bean:message key="yes" bundle="Aktera"/>
													</logic:equal>
													<logic:equal name="default" property="<%= fieldName %>" value="false">
														<bean:message key="no" bundle="Aktera"/>
													</logic:equal>
												</xlogic:case>

												<xlogic:case value="money">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="combo">
													<xhtml:select readOnly="true" name="default" property="<%= fieldName %>">
														<logic:present name="field" property="validValues">
															<xhtml:optionsCollection bundle="<%= bundle %>" name="field" property="validValues"/>
														</logic:present>
													</xhtml:select>
												</xlogic:case>

												<xlogic:case value="list">
													<jsp:include page="/aktera/tools/listing-embedded.jsp" flush="true">
														<jsp:param name="listId" value="<%= fieldName %>"/>
													</jsp:include>
												</xlogic:case>

												<xlogic:case value="file">
													<bean:write name="default" property="<%= fieldName %>"/>
												</xlogic:case>

												<xlogic:case value="dipswitch">
													<bean:define id="fieldSize" name="field" property="attributes.size" type="java.lang.Integer"/>
													<xhtml:dipSwitch readOnly="true" name="default" property="<%= fieldName %>" numberingOffset="1" cols="<%= fieldSize.toString () %>"/>
												</xlogic:case>

												<xlogic:case value="seconds">
													<xhtml:secondsMultiSelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="minutes">
													<xhtml:minutesMultiSelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="hours">
													<xhtml:hoursMultiSelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="days">
													<xhtml:daysMultiSelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="months">
													<xhtml:monthsMultiSelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value="weekdays">
													<xhtml:weekdaysMultiSelect readOnly="true" name="default" property="<%= fieldName %>" bundle="Aktera"/>
												</xlogic:case>

												<xlogic:case value=""></xlogic:case>

												<xlogic:default>!!! Unknown editor: <bean:write name="field" property="attributes.editor"/> !!!</xlogic:default>

											</xlogic:switch>

										</td>
									</logic:present>

								</tr>

								</logic:notEqual>
								</logic:notPresent>
								</logic:notPresent>

								<logic:equal name="field" property="attributes.editor" value="hidden">

									<logic:equal name="field" property="attributes.editor" value="hidden">
										<html:hidden name="default" property="<%= fieldName %>"/>
									</logic:equal>

								</logic:equal>

								<logic:present name="field" property="attributes.desc">
								<logic:notPresent name="groups" property="attributes.readOnly">
									<tr>
										<td colspan="3" class="formdesc">
											<bean:message name="field" property="label" bundle="<%= bundle %>"/>
										</td>
									</tr>
								</logic:notPresent>
								</logic:present>

								<logic:present name="field" property="attributes.jsp">
									<bean:define id="url" name="field" property="attributes.jsp" type="java.lang.String"/>
									<jsp:include page="<%= url %>" flush="true"/>
								</logic:present>

							</logic:iterate>

							<tr>
								<td colspan="3" class="groupsep"></td>
							</tr>

						</logic:iterate>

					</table>
				</td>
				<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2" width="0px" height="0px"/></td>
			</tr>
			<tr class="form">
				<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3" width="0px" height="0px"/></td>
				<td class="c3" width="100%" height="0px"></td>
				<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3" width="0px" height="0px"/></td>
			</tr>

		</table>

		<bean:define id="buttonBarVisible" value="visible"/>
		<logic:present name="hideButtonBar">
			<bean:define id="buttonBarVisible" value="hidden"/>
		</logic:present>

		<div class="button-box">
			<div class="t"><div class="b"><div class="l"><div class="r"><div class="bl"><div class="br"><div class="tl"><div class="tr">

				<logic:iterate id="command" name="commands">

					<bean:define id="label" name="command" property="label" type="java.lang.String"/>
					<bean:define id="visibility" type="java.lang.String" value=""/>
					<bean:define id="name" name="command" property="name" type="java.lang.String"/>

					<logic:present name="command" property="attributes.hide">
						<bean:define id="visibility" type="java.lang.String" value="visibility:hidden;position:absolute;"/>
					</logic:present>

					<bean:define id="iconName" value="" type="java.lang.String"/>
					<logic:present name="command" property="attributes.icon">
						<bean:define id="iconName" type="java.lang.String">
							/aktera/images/std/<bean:write name="command" property="attributes.icon"/>
						</bean:define>
					</logic:present>

					<bean:define id="bundle" value="Aktera" type="java.lang.String"/>
					<logic:present name="command" property="attributes.bundle">
						<bean:define id="bundle" name="command" property="attributes.bundle" type="java.lang.String"/>
					</logic:present>

					<logic:notPresent name="command" property="attributes.cancel">
						<xkeel:command name="command" styleId="<%= name %>" styleClass="form-button" style="<%= visibility %>" icon="<%= iconName %>">
							<bean:message key="<%= label %>" bundle="<%= bundle %>"/>
						</xkeel:command>
					</logic:notPresent>
					<logic:present name="command" property="attributes.cancel">
						<xkeel:command name="command" styleId="<%= name %>" styleClass="cancel-button" style="<%= visibility %>" icon="<%= iconName %>">
							<bean:message key="<%= label %>" bundle="<%= bundle %>"/>
						</xkeel:command>
					</logic:present>

				</logic:iterate>

			</div></div></div></div></div></div></div></div>
		</div>

	</logic:present>

</xhtml:form>

<script type="text/javascript" language="JavaScript">
<!--
	<%@ include file="/aktera/tools/formular.js" %>
//-->
</script>
