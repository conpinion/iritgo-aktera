<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>

<%@ include file="/aktera/templates/layout.jsp" %>


<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="title" value="Benutzeranmeldung"/>
	<tiles:put name="title-pre" type="string" value=""/>
	<tiles:put name="title-post" type="string" value=""/>
	<tiles:put name="sidebar" type="string" value=""/>
	<tiles:put name="sidebar-gap" type="string" value=""/>

	<tiles:put name="content" type="string">

		<center>

			<br><br>
			<xhtml:img src="/aktera/images/std/error-64" align="center"/>
			<br><br>
			<br><br>
			<span class="error"><bean:message key="anErrorOccurred" bundle="Aktera"/></span>

		</center>

	</tiles:put>
</tiles:insert>
