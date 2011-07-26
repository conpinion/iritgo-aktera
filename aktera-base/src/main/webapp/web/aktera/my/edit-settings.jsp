<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/settings-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="editSettings" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">
		<jsp:include page="/aktera/tools/formular.jsp" flush="true" />
	</tiles:put>

</tiles:insert>
