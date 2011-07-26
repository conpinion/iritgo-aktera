<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>

<%@ include file="/aktera/templates/layout.jsp" %>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/error-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="error" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">

		<center>

			<br>

			<xhtml:img src="/aktera/images/std/error-64" align="center"/>

			<br><br><br>

			<bean:message key="anErrorOccurredDescription1" bundle="Aktera"/>
			<br>
			<bean:message key="anErrorOccurredDescription2" bundle="Aktera"/>

			<br><br>

			<table width="0%">
				<tr><td><div class="subtitle">
				<html:link page="/index.jsp"><bean:message key="clickToGoToYourPersonalPage" bundle="Aktera"/></html:link>
				</div></td></tr>
			</table>

			<br><br>

			<bean:message key="anErrorOccurredDescription3" bundle="Aktera"/>

			<br><br><br>

			<logic:present name="test">
				<bean:write name="test"/>
			</logic:present>

			<table width="70%">
				<tr><td><xkeel:errors displayAllErrors="true" bundle="Aktera"/></td></tr>
			</table>

		</center>

	</tiles:put>

</tiles:insert>
