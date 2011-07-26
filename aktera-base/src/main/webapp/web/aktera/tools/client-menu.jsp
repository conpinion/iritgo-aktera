<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>

<logic:present name="sessionLoginName">

<table class="menu" cellspacing="0" cellpadding="0" width="100%">
	<tr class="box">
		<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1"/></td>
		<td class="c1" width="100%"></td>
		<td class="r1"><html:img page="/aktera/images/blank.gif" styleClass="r1"/></td>
	</tr>
	<tr class="box">
		<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2"/></td>
		<td class="title" width="100%">
			<nobr><bean:message key="client" bundle="Aktera"/></nobr>
		</td>
		<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2"/></td>
	</tr>
	<tr class="box">
		<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3"/></td>
		<td class="c3"></td>
		<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3"/></td>
	</tr>

	<tr class="normal">
		<td class="l1"></td>
		<td class="c1"></td>
		<td class="r1"></td>
	</tr>
	<tr class="normal">
		<td class="l2"></td>
		<td class="item">
			<nobr>
			<html:link page="/model.do?model=aktera.aktario.start-jnlp-client" styleId="client">
				<xhtml:img src="/aktera/images/std/execute-16" align="top"/><bean:message key="clientStart" bundle="Aktera"/>
			</html:link>
			</nobr>
		</td>
		<td class="r2"></td>
	</tr>
	<tr class="normal">
		<td class="l3"></td>
		<td class="c3"></td>
		<td class="r3"></td>
	</tr>

	<tr class="box">
		<td class="l4"><html:img page="/aktera/images/blank.gif" styleClass="l4"/></td>
		<td class="c4"></td>
		<td class="r4"><html:img page="/aktera/images/blank.gif" styleClass="r4"/></td>
	</tr>
</table>

</logic:present>
