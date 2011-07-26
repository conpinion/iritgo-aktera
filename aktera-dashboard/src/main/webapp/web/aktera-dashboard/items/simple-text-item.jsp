<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>

<%@ include file="/aktera/templates/layout.jsp"%>

<%@ include file="/aktera/templates/box.jsp" %>

<bean:write name="groupItem" property="attributes.label"/>:
&nbsp;<bean:write name="groupItem" property="attributes.text"/>&nbsp;/&nbsp;
<bean:write name="groupItem" property="attributes.measuringUnit"/>
