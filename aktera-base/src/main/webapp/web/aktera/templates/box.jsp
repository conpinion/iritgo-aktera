<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>

<tiles:definition id="box" page="<%= request.getAttribute ("layoutUrl").toString () + "box.jsp" %>">
	<tiles:put name="width" type="string">0%</tiles:put>
	<tiles:put name="content" type="string">Content</tiles:put>
</tiles:definition>


