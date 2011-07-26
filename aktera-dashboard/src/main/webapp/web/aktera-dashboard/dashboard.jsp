<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>

<%@ include file="/aktera/templates/layout.jsp"%>

<%@ include file="/aktera/templates/box.jsp" %>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/dashboard-32" align="middle" />
	</tiles:put>

	<tiles:put name="title" type="string">
	Dashboard
	</tiles:put>

	<tiles:put name="content" type="string">

	<table width="100%">
	<tr>
		<td width="100%" valign="top">
		<div class="functionGroup">
			<logic:iterate id="group" name="dashboardGroups" property="nested">
				<bean:define id="renderInclude" name="group" property="attributes.renderInclude" type="java.lang.String"/>
				<bean:define id="dashboardGroup" name="group"  scope="page" toScope="request"/>
				<jsp:include page="<%= renderInclude %>" flush="false"/>
			</logic:iterate>
		</div>
		</td>
	</tr>
	</table>

	</tiles:put>

</tiles:insert>
