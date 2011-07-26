<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@ page import="de.iritgo.aktera.license.LicenseTools" %>

<%@ include file="/aktera/templates/layout.jsp" %>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="pageTitle" type="string"><bean:message key="invalidLicense" bundle="Aktera"/></tiles:put>
	<tiles:put name="title" type="string" value=""/>
	<tiles:put name="title-pre" type="string" value=""/>
	<tiles:put name="title-post" type="string" value=""/>
	<tiles:put name="sidebar" type="string" value=""/>
	<tiles:put name="sidebar-gap" type="string" value=""/>
	<tiles:put name="logo" type="string" value=""/>

	<tiles:put name="content" type="string">

		<html:form action="/model.do?model=aktera.session.store-license" method="post" enctype="multipart/form-data">

		<center>

		<br /><br />

		<h1><bean:message key="noValidLicenseFound" bundle="Aktera"/></h1>

		<br /><br />

		<h1><bean:message key="machineInfo" bundle="Aktera"/></h1>

		<%
			String machineInfo = LicenseTools.machineInfo ();
			if (machineInfo == null)
			{
				machineInfo = "Unknown";
			}
		%>

		<span style="border: solid 1px #aaaaaa; padding: 4px;"><%= machineInfo %></span>

		<br /><br /><br />

		<h1><bean:message key="uploadLicenseKeyHere" bundle="Aktera"/></h1>

		<!-- <xhtml:textarea name="default" property="license" cols="80" rows="10" wrap="false"/> -->

		<html:file name="default" property="fileUpload1" size="40"/>

		<br /><br /><br />

		<html:submit styleClass="form-button">
			<bean:message key="storeLicense" bundle="Aktera"/>
		</html:submit>

		<br /><br />

		</center>

		</html:form>

	</tiles:put>
</tiles:insert>
