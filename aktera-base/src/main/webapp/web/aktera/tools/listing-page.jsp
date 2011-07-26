<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<bean:define id="listId" type="java.lang.String" value="list" />

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<logic:present name="<%= listId %>" property="attributes.icon">
			<bean:define id="icon" name="<%= listId %>" property="attributes.icon" type="java.lang.String" />
			<bean:define id="icon" type="java.lang.String" value="<%= "/aktera/images/std/" + icon %>" />
			<xhtml:img src="<%= icon %>" />
		</logic:present>
	</tiles:put>

	<tiles:put name="title" type="string">
		<logic:present name="<%= listId %>" property="attributes.title">
			<bean:define id="label" name="<%= listId %>" property="attributes.title" type="java.lang.String" />
			<bean:define id="bundle" name="<%= listId %>" property="attributes.titleBundle" type="java.lang.String" />
			<xbean:message key="<%= label %>" bundle="<%= bundle %>" />
		</logic:present>
	</tiles:put>

	<tiles:put name="content" type="string">
		<%@include file="/aktera/tools/listing.jsp"%>
	</tiles:put>

</tiles:insert>
