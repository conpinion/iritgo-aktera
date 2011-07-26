<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>

<div id="getPhoneNumbers">
	<table>
		<logic:present name="phoneNumberB">
			<tr>
				<td>
					<xhtml:img src="/aktera/images/std/tool-phone-green-10" align="baseline"/>&nbsp;
					<bean:message key="phoneNumberB" bundle="Aktera"/>:
				</td>
				<td>
					<xkeel:command name="phoneNumberB" link="true"><bean:write name="phoneNumberB" property="label"/></xkeel:command>
				</td>
			</tr>
		</logic:present>
		<logic:present name="phoneNumberBDD">
			<tr>
				<td>
					<xhtml:img src="/aktera/images/std/tool-phone-green-10" align="baseline"/>&nbsp;
					<bean:message key="phoneNumberBDD" bundle="Aktera"/>:
				</td>
				<td>
					<xkeel:command name="phoneNumberBDD" link="true"><bean:write name="phoneNumberBDD" property="label"/></xkeel:command>
				</td>
			</tr>
		</logic:present>
		<logic:present name="phoneNumberBF">
			<tr>
				<td>
					<xhtml:img src="/aktera/images/std/tool-phone-green-10" align="baseline"/>&nbsp;
					<bean:message key="phoneNumberBF" bundle="Aktera"/>:
				</td>
				<td>
					<xkeel:command name="phoneNumberBF" link="true"><bean:write name="phoneNumberBF" property="label"/></xkeel:command>
				</td>
			</tr>
		</logic:present>
		<logic:present name="phoneNumberBM">
			<tr>
				<td>
					<xhtml:img src="/aktera/images/std/tool-phone-green-10" align="baseline"/>&nbsp;
					<bean:message key="phoneNumberBM" bundle="Aktera"/>:
				</td>
				<td>
					<xkeel:command name="phoneNumberBM" link="true"><bean:write name="phoneNumberBM" property="label"/></xkeel:command>
				</td>
			</tr>
		</logic:present>
		<logic:present name="phoneNumberP">
			<tr>
				<td>
					<xhtml:img src="/aktera/images/std/tool-phone-green-10" align="baseline"/>&nbsp;
					<bean:message key="phoneNumberP" bundle="Aktera"/>:
				</td>
				<td>
					<xkeel:command name="phoneNumberP" link="true"><bean:write name="phoneNumberP" property="label"/></xkeel:command>
				</td>
			</tr>
		</logic:present>
		<logic:present name="phoneNumberPF">
			<tr>
				<td>
					<xhtml:img src="/aktera/images/std/tool-phone-green-10" align="baseline"/>&nbsp;
					<bean:message key="phoneNumberPF" bundle="Aktera"/>:
				</td>
				<td>
					<xkeel:command name="phoneNumberPF" link="true"><bean:write name="phoneNumberPF" property="label"/></xkeel:command>
				</td>
			</tr>
		</logic:present>
		<logic:present name="phoneNumberPM">
			<tr>
				<td>
					<xhtml:img src="/aktera/images/std/tool-phone-green-10" align="baseline"/>&nbsp;
					<bean:message key="phoneNumberPM" bundle="Aktera"/>:
				</td>
				<td>
					<xkeel:command name="phoneNumberPM" link="true"><bean:write name="phoneNumberPM" property="label"/></xkeel:command>
				</td>
			</tr>
		</logic:present>
		<logic:present name="phoneNumberVOIP">
			<tr>
				<td>
					<xhtml:img src="/aktera/images/std/tool-phone-green-10" align="baseline"/>&nbsp;
					<bean:message key="phoneNumberVOIP" bundle="Aktera"/>:
				</td>
				<td>
					<xkeel:command name="phoneNumberVOIP" link="true"><bean:write name="phoneNumberVOIP" property="label"/></xkeel:command>
				</td>
			</tr>
		</logic:present>
	</table>
</div>
