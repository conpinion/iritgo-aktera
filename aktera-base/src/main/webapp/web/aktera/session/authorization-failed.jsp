<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@ include file="/aktera/templates/layout.jsp" %>

<tiles:insert beanName="site-template" flush="true">
	<tiles:put name="title" value="Keine Berechtigung!"/>
		<tiles:put name="content" type="string">

			<br/><br/>

			<table>
				<tr>
					<td valign="top"><xhtml:themeImg src="password" border="0"/></td>
					<td><xhtml:themeImg src="blank" width="8" height="0"/></td>
					<td>
						<h1><bean:message key="authorizationFailed" bundle="Aktera"/></h1>
						<h2><bean:message key="authorizationFailed2" bundle="Aktera"/></h2>
						<bean:message key="authorizationFailed3" bundle="Aktera"/>
					</td>
				</tr>
			</table>

	</tiles:put>
</tiles:insert>
