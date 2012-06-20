<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel"%>

<%@ include file="/aktera/templates/layout.jsp"%>
<%@ include file="/aktera/templates/box.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/ok-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="journalCleanupNow" bundle="aktera-journal" />
	</tiles:put>

	<tiles:put name="content" type="string">
		<tiles:insert beanName="box" flush="false">
			<tiles:put name="content" type="string">
				<bean:message key="journalCleanupSuccessfull" bundle="aktera-journal" />
				<logic:present name="back">
					<br /><br />
					<xhtml:form action="model" method="post">
						<xkeel:command name="back" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
							<bean:message key="back" bundle="Aktera" />
						</xkeel:command>
					</xhtml:form>					
				</logic:present>				
			</tiles:put>
		</tiles:insert>
	</tiles:put>

</tiles:insert>
