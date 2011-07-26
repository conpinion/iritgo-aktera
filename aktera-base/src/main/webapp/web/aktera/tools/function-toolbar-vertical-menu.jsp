<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>

<%@ page language="java" import="de.iritgo.aktera.model.Command" %>

<logic:present name="functions" >

<table class="menu" cellspacing="0" cellpadding="0" width="100%">
	<tr class="box">
		<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1"/></td>
		<td class="c1" width="100%"></td>
		<td class="r1"><html:img page="/aktera/images/blank.gif" styleClass="r1"/></td>
	</tr>
	<tr class="box">
		<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2"/></td>
		<td width="100%" class="title">
			<bean:define id="title" name="functions" property="attributes.title" type="java.lang.String"/>
			<bean:define id="bundle" name="functions" property="attributes.bundle" type="java.lang.String"/>
			<nobr><xbean:message key="<%= title %>" bundle="<%= bundle %>"/></nobr>
		</td>
		<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2"/></td>
	</tr>
	<tr class="box">
		<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3"/></td>
		<td class="c3"></td>
		<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3"/></td>
	</tr>

	<logic:iterate id="function" name="functions" property="nested">

		<bean:define id="itemClass" type="java.lang.String" value="normal"/>
		<logic:present name="function" property="attributes.active">
			<bean:define id="itemClass" type="java.lang.String" value="active"/>
		</logic:present>

		<tr class="<%= itemClass %>">
			<td class="l1"></td>
			<td class="c1"></td>
			<td class="r1"></td>
		</tr>

		<tr class="<%= itemClass %>">
			<td class="l2"></td>
			<td class="item">

				<bean:define id="bundle" name="function" property="attributes.bundle" type="java.lang.String"/>
				<bean:define id="label" name="function" property="label" type="java.lang.String"/>
				<bean:define id="id" name="function" property="attributes.id" type="java.lang.String"/>
				<nobr>
				<xhtml:img path="/aktera/images/std/" name="function" property="attributes.icon" border="0" align="top"/>
				<xkeel:command link="true" name="function" styleClass="menu" styleId="<%= id %>">
					<bean:message key="<%= label %>" bundle="<%= bundle %>"/>
				</xkeel:command>
				</nobr>

			</td>
			<td class="r2"></td>
		</tr>

		<tr class="<%= itemClass %>">
			<td class="l3"></td>
			<td class="c3"></td>
			<td class="r3"></td>
		</tr>

	</logic:iterate>

	<tr class="box">
		<td class="l4"><html:img page="/aktera/images/blank.gif" styleClass="l4"/></td>
		<td class="c4"></td>
		<td class="r4"><html:img page="/aktera/images/blank.gif" styleClass="r4"/></td>
	</tr>
</table>

</logic:present>
