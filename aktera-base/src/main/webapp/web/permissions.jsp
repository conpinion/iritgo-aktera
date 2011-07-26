<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/error-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="insufficientPermissions" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">

		<br />
		<br />

		<center><b> <bean:message key="insufficientPermissionsDescription1" bundle="Aktera" /><br>
		<br />
		<bean:message key="insufficientPermissionsDescription2" bundle="Aktera" /><br>
		<br />
		<ul>
			<li><bean:message key="insufficientPermissionsDescription3" bundle="Aktera" /></li>
			<li><bean:message key="insufficientPermissionsDescription4" bundle="Aktera" /></li>
		</ul>
		</b></center>

	</tiles:put>

</tiles:insert>
