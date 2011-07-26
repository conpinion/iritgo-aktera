<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<logic:present name="cmdReport">
		<tiles:put name="head" type="string">
			<bean:define id="refreshModel">
				<bean:write name="cmdReport" property="model" />
			</bean:define>
			<meta http-equiv="refresh" content="<%="4; url=model.do?model=" + refreshModel%>">
		</tiles:put>
	</logic:present>

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/clock-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="progress" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">
		<center>
			<br> <br>
			<logic:present name="cmdReport">
				<br>
				<xhtml:form action="model" method="post">
					<xkeel:command name="cmdReport" styleClass="form-button" icon="/aktera/images/std/tool-reload-16">
						<bean:message key="reload" bundle="Aktera" />
					</xkeel:command>
				</xhtml:form>
				<br>
				<br>
			</logic:present>
			<bean:message key="progressReport" bundle="Aktera" />
			<br> <br>
			<table class="report" width="100%" cellpadding="8" cellspacing="0">
				<tr>
					<td><xbean:write name="report" filter="true" convertNewlinesToBr="true" /></td>
				</tr>
			</table>
			<br> <br>
			<logic:present name="cmdOk">
				<xhtml:form action="model" method="post">
					<xkeel:command name="cmdOk" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
						<bean:message key="ok" bundle="Aktera" />
					</xkeel:command>
				</xhtml:form>
			</logic:present>
			<logic:present name="cmdError">
				<xhtml:form action="model" method="post">
					<xkeel:command name="cmdError" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
						<bean:message key="ok" bundle="Aktera" />
					</xkeel:command>
				</xhtml:form>
			</logic:present>
			<center>
	</tiles:put>

</tiles:insert>
