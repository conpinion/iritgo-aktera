<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="logo" type="string" value="" />
	<tiles:put name="sidebar" type="string" value="" />
	<tiles:put name="sidebar-gap" type="string" value="" />

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/database-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="updateDatabase" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">
		<center>
		<table width="50%">
			<tr>
				<td align="center"><br>
				<br>
				<br>
				<h1><bean:message key="updateDatabaseDescription" bundle="Aktera" /></h1>
				<br>
				<br>
				<br>
				<xhtml:form action="model" method="post">
					<xkeel:command name="update" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
						<bean:message key="startDatabaseUpdate" bundle="Aktera" />
					</xkeel:command>
				</xhtml:form>
			</tr>
			</td>
		</table>
		</center>
	</tiles:put>

</tiles:insert>
