<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/admin-user-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="adminPersonalPage" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">
		<table width="100%">
			<tr>
				<td width="100%" valign="top"><jsp:include page="/aktera/tools/listing.jsp" flush="true">
					<jsp:param name="listId" value="taskList" />
				</jsp:include> <br>
				<br>
				<jsp:include page="/aktera/tools/listing.jsp" flush="true">
					<jsp:param name="listId" value="noteList" />
				</jsp:include></td>
				<td width="16" valign="top">&nbsp;</td>
				<td valign="top"><%@ include file="../appointment/calendar-compackt-month.jsp"%></td>
			</tr>
		</table>
	</tiles:put>

</tiles:insert>
