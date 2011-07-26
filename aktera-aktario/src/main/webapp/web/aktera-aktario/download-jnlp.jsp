<%@ page contentType="application/x-java-jnlp-file; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<% response.setHeader ("Content-disposition", "inline; filename=" + request.getAttribute ("fileName")); %>

<?xml version="1.0" encoding="UTF-8" ?>

<jnlp spec="1.0+" codebase="<bean:write name="codebase" />" href="<bean:write name="href" />">

	<information>
		<title><bean:write name="title" /></title>
		<vendor><bean:write name="vendor" /></vendor>
		<description><bean:write name="description" /></description>
		<icon href="<bean:write name="iconUrl" />" />
		<offline-allowed/>
	</information>

	<security>
		<all-permissions/>
	</security>

	<resources>
		<j2se version="1.6+" initial-heap-size="8m"/>
		<jar href="<bean:write name="aktarioFramework" />" />
		<logic:iterate id="library" name="libraries" property="nested">
			<jar href="<bean:write name="library" />" />
		</logic:iterate>
		<property name="iritgo.plugins" value="<bean:write name="plugins" />" />
		<property name="iritgo.app.title" value="<bean:write name="title" />" />
		<property name="iritgo.app.version" value="<bean:write name="version" />" />
		<property name="iritgo.app.version.long" value="<bean:write name="versionLong" />" />
		<property name="iritgo.app.copyright" value="<bean:write name="copyright" />" />
		<property name="awt.useSystemAAFontSettings" value="lcd" />
	</resources>

	<resources os="Linux">
		<logic:iterate id="library" name="librariesLinux" property="nested">
			<nativelib href="<bean:write name="library" />" />
		</logic:iterate>
	</resources>

	<resources os="Windows">
		<logic:iterate id="library" name="librariesWin32" property="nested">
			<nativelib href="<bean:write name="library" />" />
		</logic:iterate>
	</resources>

	<resources os="Mac OS">
	</resources>

	<application-desc main-class="de.iritgo.aktario.framework.IritgoClient">
		<argument>-w</argument>
		<argument><bean:write name="userName" />@<bean:write name="server" /></argument>
	</application-desc>

</jnlp>

