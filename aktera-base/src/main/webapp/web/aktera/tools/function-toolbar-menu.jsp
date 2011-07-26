<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>

<%@ page language="java" import="de.iritgo.aktera.model.Command" %>

<logic:present name="functions" >
<logic:equal name="functions" property="attributes.style" value="toolbar">

	<table class="toolbar" cellspacing="8" width="100%">

		<tr>

			<td class="pre">&nbsp;</td>

			<logic:iterate id="function" name="functions" property="nested">

			<logic:present name="function" property="attributes.active">
				<td align="center" class="active" valign="top">
			</logic:present>
			<logic:notPresent name="function" property="attributes.active">
				<td align="center" valign="top">
			</logic:notPresent>

					<bean:define id="bundle" name="function" property="attributes.bundle" type="java.lang.String"/>
					<bean:define id="label" name="function" property="label" type="java.lang.String"/>
					<xkeel:command link="true" name="function" styleClass="menu" title="<%= label %>" bundle="<%= bundle %>">
						<xhtml:img path="/aktera/images/std/" name="function" property="attributes.icon" border="0" align="middle"/><br/>
					</xkeel:command>
				</td>

			</logic:iterate>

			<td width="100%">&nbsp;</td>

			<td class="post">&nbsp;</td>

			</tr>

		</tr>

	</table>

	<br>

</logic:equal>
</logic:present>
