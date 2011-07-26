<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>

<logic:iterate id="menu" name="menuList" property="nested">

	<logic:notEqual name="menu" property="attributes.numVisibleItems" value="0">
	<logic:equal name="menu" property="attributes.title" value="session">

	<table class="menu" cellspacing="0" cellpadding="0" width="100%">
		<tr class="box">
			<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1"/></td>
			<td class="c1" width="100%"></td>
			<td class="r1"><html:img page="/aktera/images/blank.gif" styleClass="r1"/></td>
		</tr>
		<tr class="box">
			<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2"/></td>
			<td width="100%" class="title">
				<nobr><bean:message name="menu" property="attributes.title" bundle="Aktera"/></nobr>
			</td>
			<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2"/></td>
		</tr>
		<tr class="box">
			<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3"/></td>
			<td class="c3"></td>
			<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3"/></td>
		</tr>

		<logic:iterate id="item" name="menu" property="nested">

			<bean:define id="bundle" name="item" property="attributes.bundle" type="java.lang.String"/>
			<bean:define id="label" name="item" property="label" type="java.lang.String"/>

			<tr class="normal">
				<td class="l1"></td>
				<td class="c1"></td>
				<td class="r1"></td>
			</tr>
			<tr class="normal">
				<td class="l2"></td>
				<td class="item">
					<nobr>

					<logic:present name="item" property="attributes.icon">
						<xhtml:img path="/aktera/images/std/" name="item" property="attributes.icon" align="top"/>
					</logic:present>
					<logic:notPresent name="item" property="attributes.icon">
						<xhtml:themeImg src="menu-bullet" align="middle"/>
					</logic:notPresent>

					<xkeel:command link="true" name="item" styleClass="menu">
						<bean:message key="<%= label %>" bundle="<%= bundle %>"/>
					</xkeel:command>

					</nobr>
				</td>
				<td class="r2"></td>
			</tr>
			<tr class="normal">
				<td class="l3"></td>
				<td class="c3"></td>
				<td class="r3"></td>
			</tr>

		</logic:iterate>

		<tr class="box">
			<td class="l4"><html:img page="/aktera/images/blank.gif" styleClass="l4"/></td>
			<td class="c4"></td>
			<td class="r4""><html:img page="/aktera/images/blank.gif" styleClass="r4"/></td>
		</tr>
	</table>

	</logic:equal>
	</logic:notEqual>

</logic:iterate>
