<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="logo" type="string" value="" />
	<tiles:put name="sidebar" type="string" value="" />
	<tiles:put name="sidebar-gap" type="string" value="" />

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/database-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="createDatabase" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">
		<center>
		<table width="80%">
			<tr>
				<td align="center"><br>
				<br>
				<br>

				<logic:present name="databaseError">
					<table>
						<tr>
							<td><xhtml:img src="/aktera/images/std/error-48" align="center" /></td>
							<td width="16px">&nbsp;</td>
							<td>
							<h1><bean:message key="createDatabaseError" bundle="Aktera" /></h1>
							</td>
						</tr>
					</table>
					<br />
					<br />
					<div class="warning-box">
						<div class="t"><div class="b"><div class="l"><div class="r"><div class="bl"><div class="br"><div class="tl"><div class="tr">
							<bean:write name="databaseError" filter="false" />
						</div></div></div></div></div></div></div></div>
					</div>
					<br />
					<br />
					<div class="warning-box">
						<div class="t"><div class="b"><div class="l"><div class="r"><div class="bl"><div class="br"><div class="tl"><div class="tr">
							<bean:write name="databaseErrorStackTrace" filter="false" />
						</div></div></div></div></div></div></div></div>
					</div>
				</logic:present>
				<logic:notPresent name="databaseError">
					<table>
						<tr>
							<td><xhtml:img src="/aktera/images/std/ok-48" align="center" /></td>
							<td width="16px">&nbsp;</td>
							<td>
							<h1><bean:message key="createDatabaseSuccess" bundle="Aktera" /></h1>
							</td>
						</tr>
					</table>
				</logic:notPresent></td>
			</tr>
		</table>
		</center>
	</tiles:put>

</tiles:insert>

