<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@ include file="/aktera/templates/layout.jsp" %>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="title" type="string">
		ShowModelRequest
	</tiles:put>

	<tiles:put name="content" type="string">

		<hr /><br />

		<h1>General:</h1><br />

		<b>Source:&nbsp;</b><bean:write name="source"/><br />

		<br /><hr /><br />

		<h1>Headers:</h1><br />

		<table>
			<logic:iterate id="header" name="headers" property="nested">
				<tr>
					<td><b><bean:write name="header"/>:&nbsp;</td>
					<td><bean:write name="header" property="attributes.value"/><br /></td>
				</tr>
			</logic:iterate>
		</table>

		<br /><hr /><br />

		<h1>Parameters:</h1><br />

		<table>
			<logic:iterate id="parameter" name="parameters" property="nested">
				<tr>
					<td><b><bean:write name="parameter"/>:&nbsp;</td>
					<td><bean:write name="parameter" property="attributes.value"/><br /></td>
				</tr>
			</logic:iterate>
		</table>

		<br /><hr /><br />

		<h1>Attributes:</h1><br />

		<table>
			<logic:iterate id="attribute" name="attributes" property="nested">
				<tr>
					<td><b><bean:write name="attribute"/>:&nbsp;</td>
					<td><bean:write name="attribute" property="attributes.value"/><br /></td>
				</tr>
			</logic:iterate>
		</table>

	</tiles:put>

</tiles:insert>
