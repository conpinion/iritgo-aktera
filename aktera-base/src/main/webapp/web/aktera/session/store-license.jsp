<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>

<%@ include file="/aktera/templates/layout.jsp" %>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="pageTitle" type="string"><bean:message key="licenseStored1" bundle="Aktera"/></tiles:put>
	<tiles:put name="title" type="string" value=""/>
	<tiles:put name="title-pre" type="string" value=""/>
	<tiles:put name="title-post" type="string" value=""/>
	<tiles:put name="sidebar" type="string" value=""/>
	<tiles:put name="sidebar-gap" type="string" value=""/>
	<tiles:put name="logo" type="string" value=""/>

	<tiles:put name="content" type="string">

		<xhtml:form action="model" method="post" enctype="multipart/form-data">

		<center>

		<br/><br/>

		<h1><bean:message key="licenseStored1" bundle="Aktera"/></h1>

		<br/>

		<h1><bean:message key="licenseStored2" bundle="Aktera"/></h1>

		<br/><br/>

		<xkeel:command name="cmdLogin" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
			<bean:message key="login" bundle="Aktera"/>
		</xkeel:command>

		</center>

		</xhtml:form>

	</tiles:put>
</tiles:insert>
