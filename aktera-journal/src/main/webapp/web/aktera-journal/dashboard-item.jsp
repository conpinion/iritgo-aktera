<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>

<%@ include file="/aktera/templates/layout.jsp"%>

<%@ include file="/aktera/templates/box.jsp" %>

<bean:define id="imageUrl" name="item" property="attributes.imageUrl" type="java.lang.String"/>
<div class="pic">
	<img src="<%= imageUrl %>"></img>
</div>

<div class="text">
	<div class="news-type"><bean:write name="item" property="attributes.typeOfNews"/></div>
	<div class="news-text"><h1><b><bean:write name="item" property="attributes.headerLine"/></b></h1></div>
	<div class="news-text">
		<p>
			<bean:write name="item" property="attributes.message"/>
		</p>
	</div>
	<div class="news-footer"><bean:write name="item" property="attributes.date"/>&nbsp;<bean:write name="item" property="attributes.producer"/>
	</div>
</div>
<div class="footer"></div>
