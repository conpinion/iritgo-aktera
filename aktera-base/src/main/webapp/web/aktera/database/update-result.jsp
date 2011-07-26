<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
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
		<br />
		<logic:notPresent name="updateError">
			<center><xhtml:img src="/aktera/images/std/ok-48" align="center" /> <br />
			<br />
			<logic:present name="needReboot">
				<h1><bean:message key="databaseWasUpdated" bundle="Aktera" /></h1>
				<br>
				<h1><bean:message key="databaseWasUpdatedReboot1" bundle="Aktera" /></h1>
				<br>
				<h1><bean:message key="databaseWasUpdatedReboot2" bundle="Aktera" /></h1>
			</logic:present> <logic:notPresent name="needReboot">
				<h1><bean:message key="databaseWasUpdated" bundle="Aktera" /></h1>
				<br>
				<h1><bean:message key="databaseWasUpdatedLogin" bundle="Aktera" /></h1>
			</logic:notPresent> <br />
			<br />
			<xhtml:form action="model" method="post">
				<xkeel:command name="cmd" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
					<bean:message key="login" bundle="Aktera" />
				</xkeel:command>
			</xhtml:form>
			<center>
		</logic:notPresent>

		<logic:present name="updateError">
			<center><xhtml:img src="/aktera/images/std/error-48" align="center" /> <br />
			<br />
			<h1><bean:message key="updateDatabaseError" bundle="Aktera" /></h1>
			<center>
		</logic:present>

		<br />
		<br />

		<center><logic:iterate id="module" name="modules" property="nested">
			<table width="50%" class="list">
				<tr>
					<th><bean:write name="module" property="attributes.name" /></th>
				</tr>
				<tr>
					<td><bean:write name="module" property="attributes.description" /></td>
				</tr>
				<tr>
					<td>Alte Version: <bean:write name="module" property="attributes.oldVersion" /></td>
				</tr>
				<tr>
					<td>Neue Version: <bean:write name="module" property="attributes.newVersion" /></td>
				</tr>
				<tr>
					<td><logic:notPresent name="module" property="attributes.error">
						<span class="success">Update: Ok</span>
					</logic:notPresent> <logic:present name="module" property="attributes.error">
						<table>
							<tr>
								<td><span class="error">Update: <bean:message name="module" property="attributes.error"
									bundle="Aktera" /></span></td>
							</tr>
							<tr>
								<td><span class="error"><bean:write name="module" property="attributes.errorException" /></span></td>
							</tr>
						</table>
					</logic:present></td>
				</tr>
			</table>
			<br />
		</logic:iterate></center>

		<logic:present name="databaseError">

			<table width="80%">
				<tr>
					<td align="center"><bean:message key="errors.header" bundle="Aktera" /> <bean:write name="databaseError" />
					<br>
					<br>
					<bean:write name="databaseErrorStackTrace" filter="false" /> <bean:message key="errors.footer" bundle="Aktera" />
				</tr>
				</td>
			</table>

		</logic:present>

	</tiles:put>

</tiles:insert>

