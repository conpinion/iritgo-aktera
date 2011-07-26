<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="pageTitle" type="string" value="Systeminformationen" />
	<tiles:put name="title" type="string" value="" />
	<tiles:put name="title-pre" type="string" value="" />
	<tiles:put name="title-post" type="string" value="" />
	<tiles:put name="logo" type="string" value="" />
	<tiles:put name="sidebar" type="string" value="" />
	<tiles:put name="sidebar-gap" type="string" value="" />

	<tiles:put name="content" type="string">

		<br />
		<center><xhtml:img src="/aktera/images/std/app-logo-title" align="center" /> <br />
		<br />
		<b><bean:write name="name" /> <bean:write name="version" /></b> <br />
		<bean:write name="copyright" filter="false" />
		<center><br />
		<br />
		<br />
		<b><bean:message key="appInfo" bundle="Aktera" /></b> <br />
		<br />

		<center><logic:iterate id="app" name="apps" property="nested">
			<table width="50%" class="list">
				<tr>
					<th><bean:write name="app" property="attributes.name" /></th>
				</tr>
				<tr>
					<td>Version: <bean:write name="app" property="attributes.version" /></td>
				</tr>
				<tr>
					<td width="50%"><bean:write name="app" property="attributes.description" /></td>
				</tr>
				<tr>
					<td width="50%"><bean:write name="app" property="attributes.copyright" filter="false"/></td>
				</tr>
			</table>
			<br />
		</logic:iterate></center>

		<br />
		<br />
		<b><bean:message key="moduleInfo" bundle="Aktera" /></b> <br />
		<br />

		<center><logic:iterate id="module" name="modules" property="nested">
			<table width="50%" class="list">
				<tr>
					<th><bean:write name="module" property="attributes.name" /></th>
				</tr>
				<tr>
					<td>Version: <bean:write name="module" property="attributes.version" /></td>
				</tr>
				<tr>
					<td><bean:write name="module" property="attributes.description" /></td>
				</tr>
				<tr>
					<td>Typ: <bean:message name="module" property="attributes.type" bundle="Aktera" /></td>
				</tr>
				<tr>
					<td><bean:write name="module" property="attributes.copyright" filter="false"/></td>
				</tr>
			</table>
			<br />
		</logic:iterate></center>
	</tiles:put>

</tiles:insert>
