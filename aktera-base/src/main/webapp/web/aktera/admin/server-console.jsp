<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>

<%@ include file="/aktera/templates/layout.jsp" %>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="title" type="string">
		<bean:message key="serverConsole" bundle="Aktera"/>
	</tiles:put>

	<tiles:put name="content" type="string">

		<table style="margin-left:6px;margin-right:6px;" width="100%" height="600" bgcolor="#000000">
			<tr>
				<td><font color="#ffffff"><b><tt>Server Console Ready!<br>&gt;</b></tt></font></td>
			</tr>
			<tr height="100%">
				<td>&gt;</td>
			</tr>
		</table>
	</tiles:put>

</tiles:insert>
