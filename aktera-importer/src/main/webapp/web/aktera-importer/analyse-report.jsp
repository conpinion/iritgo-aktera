<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<logic:present name="cmdReport">
		<tiles:put name="head" type="string">
			<bean:define id="refreshModel">
				<bean:write name="cmdReport" property="model" />
				<logic:iterate id="param" name="cmdReport" property="parameters">&<bean:write name="param" />
				</logic:iterate>
			</bean:define>
			<meta http-equiv="refresh" content="<%= "4; url=model.do?model=" + refreshModel %>">
		</tiles:put>
	</logic:present>

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/import-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="analyzeImport" bundle="aktera-importer" />
	</tiles:put>

	<tiles:put name="content" type="string">
		<center><logic:present name="report">
			<logic:present name="cmdReport">
				<xhtml:img src="/aktera/images/std/timer-48" align="center" />
				<br>
				<br>
				<xhtml:form action="model" method="post">
					<xkeel:command name="cmdReport" styleClass="form-button" icon="/aktera/images/std/tool-reload-16">
						<bean:message key="reload" bundle="Aktera" />
					</xkeel:command>
				</xhtml:form>
				<br>
				<br>
			</logic:present>
			<br>
			<br>
			<bean:message key="importAnalyzeReport" bundle="aktera-importer" />
			<br>
			<br>
			<table class="report" width="100%" cellpadding="8" cellspacing="0">
				<tr>
					<td><pre><bean:write name="report" /></pre></td>
				</tr>
			</table>
			<br>
			<br>
			<xhtml:form action="model" method="post">
				<logic:present name="cmdBack">
					<xkeel:command name="cmdBack" styleClass="form-button" icon="/aktera/images/std/tool-cancel-16">
						<bean:message key="back" bundle="Aktera" />
					</xkeel:command>
				</logic:present>
				<logic:present name="cmdImport">
					<xkeel:command name="cmdImport" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
						<bean:message key="import" bundle="Aktera" />
					</xkeel:command>
				</logic:present>
			</xhtml:form>
		</logic:present> <logic:notPresent name="report">
			<br>
			<xhtml:img src="/aktera/images/std/error-64" align="center" />
			<br>
			<br>
			<h1><bean:message key="importAllreadyRunning1" bundle="aktera-importer" /></h1>
			<br>
			<bean:message key="importAllreadyRunning2" bundle="aktera-importer" />
			<br>
			<br>
			<bean:message key="importAllreadyRunning3" bundle="aktera-importer" />
			<br>
			<br>
			<br>
			<br>
			<xhtml:form action="model" method="post">
				<table>
					<tr>
						<td><xkeel:command name="cmdBack" styleClass="form-button" icon="/aktera/images/std/tool-cancel-16">
							<bean:message key="back" bundle="Aktera" />
						</xkeel:command></td>
						<td>
						<div class="space-horizontal" />
						</td>
						<td><xkeel:command name="cmdForce" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
							<bean:message key="forceImport" bundle="aktera-importer" />
						</xkeel:command></td>
					</tr>
				</table>
			</xhtml:form>
		</logic:notPresent>
		<center>
	</tiles:put>

</tiles:insert>
