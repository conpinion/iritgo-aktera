<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>

<logic:present name="functions" >

	<table width="100%" cellpadding="8">

		<tr>

			<logic:iterate id="function" name="functions" property="nested">

				<td class="function" align="center">
					<bean:define id="bundle" name="function" property="attributes.bundle" type="java.lang.String"/>
					<bean:define id="label" name="function" property="label" type="java.lang.String"/>
					<xkeel:command link="true" name="function" styleClass="menu">
						<xhtml:img path="/aktera/images/std/" name="function" property="attributes.bigIcon" border="0"/><br/><br/>
						<bean:message key="<%= label %>" bundle="<%= bundle %>"/>
					</xkeel:command>
				</td>

				<td><html:img page="/aktera/images/blank.gif" width="16" border="0"/></td>

				<logic:present name="function" property="attributes.lastInRow">
					</tr><tr>
				</logic:present>

			</logic:iterate>

		</tr>

	</table>


</logic:present>
