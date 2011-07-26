<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>

<%@ include file="/aktera/templates/layout.jsp" %>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="pageTitle" type="string"><bean:message key="userLogin" bundle="Aktera"/></tiles:put>
	<tiles:put name="title" type="string" value=""/>
	<tiles:put name="title-pre" type="string" value=""/>
	<tiles:put name="title-post" type="string" value=""/>
	<tiles:put name="logo" type="string" value=""/>

	<tiles:put name="content" type="string">
		<center>

		<br/><br/>

		<table><tr>
		<td><xhtml:img src="/aktera/images/std/error-48" border="0" align="center"/></td>
		<td><html:img page="/aktera/images/blank.gif" width="32px" height="0px"/></td>
		<td><h1><b><bean:message key="errorDuringFormularSave" bundle="Aktera"/></b></h1></td>
		</tr></table>

		<br/><br/>

		<bean:message key="errorDuringFormularSave1" bundle="Aktera"/>
		<br><br>
		<bean:message key="errorDuringFormularSave2" bundle="Aktera"/>
		<br><br>
		<bean:message key="errorDuringFormularSave3" bundle="Aktera"/>
		<br><br>
		<bean:message key="errorDuringFormularSave4" bundle="Aktera"/>
		<br><br>
		<bean:message key="errorDuringFormularSave5" bundle="Aktera"/>

		</center>

	</tiles:put>
</tiles:insert>
