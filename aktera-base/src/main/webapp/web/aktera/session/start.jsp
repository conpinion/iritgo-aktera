<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<%@ include file="/aktera/templates/layout.jsp" %>

<tiles:insert beanName="site-template" flush="true">
	<tiles:put name="title" value="Wilkommen!"/>
		<tiles:put name="content" type="string">
			<h1><bean:message key="welcomeToAktera" bundle="Aktera"/></h1>
	</tiles:put>
</tiles:insert>
