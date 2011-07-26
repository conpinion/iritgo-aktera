<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ include file="/aktera/templates/layout.jsp"%>
<%@ include file="/aktera/templates/box.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/disk-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="downloads" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">
		<tiles:insert beanName="box" flush="false">
			<tiles:put name="content" type="string">
				<table width="100%">
					<tr>
						<td colspan="4">&nbsp;</td>
					</tr>
					<logic:iterate id="file" name="list" property="nested">
						<bean:define id="icon" type="java.lang.String">
					/aktera/images/std/<bean:write name="file" property="attributes.extension" />-16
				</bean:define>
						<bean:define id="url" type="java.lang.String">
							<bean:write name="file" property="attributes.url" />
						</bean:define>
						<tr>
							<td>&nbsp;&nbsp;</td>
							<td><xhtml:img src="<%= icon %>" align="baseline" /></td>
							<td>&nbsp;&nbsp;</td>
							<td width="100%" align="left"><a href="<%= url %>"><bean:write name="file" /></a></td>
						</tr>
						<tr>
							<td colspan="4">&nbsp;</td>
						</tr>
					</logic:iterate>
				</table>
			</tiles:put>
		</tiles:insert>
	</tiles:put>

</tiles:insert>
