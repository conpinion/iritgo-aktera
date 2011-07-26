<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel"%>

<xkeel:callModel model="aktera.tools.themer" />
<xkeel:callModel model="aktera.session.get-session-info" />
<xkeel:callModel model="aktera.current-menu" />
<xkeel:callModel model="aktera.ui.check-config-change" />

<%
	session.setAttribute ("org.apache.struts.action.LOCALE",
					((de.iritgo.aktera.model.Output) ((de.iritgo.aktera.clients.ResponseElementDynaBean) pageContext
									.getAttribute ("sessionLanguage",
													javax.servlet.jsp.PageContext.REQUEST_SCOPE))
									.getResponseElement ()).getContent ());
%>

<tiles:definition id="site-template" page="<%= request.getAttribute ("layoutUrl").toString () + "layout.jsp" %>">
	<tiles:put name="head" type="string" value="" />
	<tiles:put name="pageTitle" type="string" value="" />
	<tiles:put name="title" type="string" value="Aktera" />
	<tiles:put name="icon" type="string" value="" />
	<tiles:put name="header" value="<%= request.getAttribute ("includeUrl").toString () + "header.jsp" %>" />
	<tiles:put name="logo" value="<%= request.getAttribute ("includeUrl").toString () + "logo.jsp" %>" />
	<tiles:put name="sidebar" value="<%= request.getAttribute ("includeUrl").toString () + "sidebar.jsp" %>" />
	<tiles:put name="sidebar-gap" type="string">
		<html:img page="/aktera/images/blank.gif" width="16" border="0" />
	</tiles:put>
	<tiles:put name="content" value="Content" />
	<tiles:put name="title-pre" type="string">
		<div class="title">
			<div class="t"><div class="b"><div class="l"><div class="r"><div class="bl"><div class="br"><div class="tl"><div class="tr">
	</tiles:put>
	<tiles:put name="title-post" type="string">
			</div></div></div></div></div></div></div></div>
		</div>
	</tiles:put>
	<tiles:put name="subtitle" type="string" value="Subtitle" />
	<tiles:put name="footer" value="<%= request.getAttribute ("includeUrl").toString () + "footer.jsp" %>" />
	<tiles:put name="errors" type="string">
		<xkeel:errors bundle="Aktera" />
	</tiles:put>
	<tiles:put name="messages" type="string">
		<logic:present name="IRITGO_formMessages">
			<logic:iterate id="message" name="IRITGO_formMessages" property="nested">
				<center>
				<table class="message-box" cellspacing="0" cellpadding="0">
					<tr>
						<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1" /></td>
						<td class="c1" width="100%"></td>
						<td class="r1"><html:img page="/aktera/images/blank.gif" styleClass="r1" /></td>
					</tr>
					<tr>
						<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2" /></td>
						<td class="content" width="100%"><bean:define id="text" name="message" property="content"
							type="java.lang.String" /> <bean:define id="bundle" name="message" property="attributes.bundle"
							type="java.lang.String" /> <bean:message key="<%= text %>" bundle="<%= bundle %>" /></td>
						<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2" /></td>
					</tr>
					<tr>
						<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3" /></td>
						<td class="c3" width="100%"></td>
						<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3" /></td>
					</tr>
				</table>
				</center>
			</logic:iterate>
		</logic:present>
	</tiles:put>
</tiles:definition>
