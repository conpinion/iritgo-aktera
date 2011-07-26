<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="pageTitle" type="string">
		<bean:message key="userLogin" bundle="Aktera" />
	</tiles:put>
	<tiles:put name="title" type="string" value="" />
	<tiles:put name="title-pre" type="string" value="" />
	<tiles:put name="title-post" type="string" value="" />
	<tiles:put name="logo" type="string" value="" />
	<tiles:put name="sidebar" type="string" value="" />
	<tiles:put name="sidebar-gap" type="string" value="" />

	<tiles:put name="content" type="string">
		<center><br />
		<br />

		<xhtml:img src="/aktera/images/std/app-logo-title" border="0" /> <br />
		<br />
		<br />

		<bean:message key="loginGreeting" bundle="Aktera" /> <br />
		<br />

		<xhtml:form action="model" method="post" focus="loginName">
			<table class="form-content" cellpadding="0" cellspacing="0">
				<tr>
					<td class="formlabel"><bean:message key="userName" bundle="Aktera" />:</td>
					<td class="formitem"><html:text name="default" property="loginName" /></td>
				</tr>
				<tr>
					<td class="formlabel"><bean:message key="password" bundle="Aktera" />:</td>
					<td class="formitem"><html:password name="default" property="password" /></td>
				</tr>
				<tr>
					<td class="formlabel"><bean:message key="rememberLoginData" bundle="Aktera" />:</td>
					<td class="formitem"><html:checkbox name="default" property="remember" /></td>
				</tr>
				<tr>
					<td colspan="2" align="center">&nbsp;<br>
					</td>
				</tr>
				<tr>
					<td colspan="2" align="center">
					<table>
						<tr>
							<td><xhtml:img src="/aktera/images/std/keys4-64" border="0" align="top" /></td>
							<td><xkeel:command styleId="login" name="login" styleClass="form-button"
								icon="/aktera/images/std/tool-ok-16">
									<bean:message key="login" bundle="Aktera" />
								</xkeel:command>
								</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
		</xhtml:form></center>
	</tiles:put>
</tiles:insert>
