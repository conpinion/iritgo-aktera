<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://www.keelframework.org/struts/keel-1.0" prefix="keel"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/report-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="reportResult" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">
		<br>
		<xhtml:form action="model" method="post" focus="search">
			<table class="button-box" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1" width="0px" height="0px" /></td>
					<td class="c1" width="100%" height="0px"></td>
					<td class="r1"><html:img page="/aktera/images/blank.gif" styleClass="r1" width="0px" height="0px" /></td>
				</tr>
				<tr>
					<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2" width="0px" height="0px" /></td>
					<td class="content" width="100%">

					<table width="100%" cellpadding="0" cellspacing="0">
						<tr>
							<td width="50%">
							<table width="100%" cellpadding="0" cellspacing="0">
								<tr>
									<td width="100%"><nobr> <logic:present name="back">
										<xkeel:command name="back" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
											<bean:message name="back" property="label" bundle="Aktera" />
										</xkeel:command>
									</logic:present></nobr></td>
									<td><nobr> <logic:present name="cmdPageStart">
										<keel:command name="cmdPageStart" link="true">
											<xhtml:themeImg src="page-fast-back" border="0" align="top" />
										</keel:command>
									</logic:present> <logic:notPresent name="cmdPageStart">
										<xhtml:themeImg src="page-fast-back-grey" border="0" align="top" />
									</logic:notPresent> <span class="page-number-separator">|</span> </nobr></td>
									<td><nobr> <logic:present name="cmdPageBack">
										<keel:command name="cmdPageBack" link="true">
											<xhtml:themeImg src="page-back" border="0" align="top" />
										</keel:command>
									</logic:present> <logic:notPresent name="cmdPageBack">
										<xhtml:themeImg src="page-back-grey" border="0" align="top" />
									</logic:notPresent><span class="page-number-separator">|</span> </nobr></td>
									<logic:present name="prevPages">
										<logic:iterate id="cmdPage" name="prevPages" property="nested">
											<td valign="middle"><nobr> <keel:command name="cmdPage" link="true">
												<bean:write name="cmdPage" property="label" />
											</keel:command> <span class="page-number-separator">|</span> </nobr></td>
										</logic:iterate>
									</logic:present>
								</tr>
							</table>
							</td>
							<td><logic:present name="page">
								<nobr><span class="page-number-current"><bean:write name="page" /></span>&nbsp;</nobr>
							</logic:present></td>
							<td width="50%">
							<table width="100%" cellpadding="0" cellspacing="0">
								<tr>
									<logic:present name="nextPages">
										<logic:iterate id="cmdPage" name="nextPages" property="nested">
											<td valign="middle"><nobr> <span class="page-number-separator">|</span> <keel:command
												name="cmdPage" link="true">
												<bean:write name="cmdPage" property="label" />
											</keel:command> </nobr></td>
										</logic:iterate>
									</logic:present>
									<td><nobr> <span class="page-number-separator">|</span> <logic:present name="cmdPageNext">
										<keel:command name="cmdPageNext" link="true">
											<xhtml:themeImg src="page-next" border="0" align="top" />
										</keel:command>
									</logic:present> <logic:notPresent name="cmdPageNext">
										<xhtml:themeImg src="page-next-grey" border="0" align="top" />
									</logic:notPresent></nobr></td>
									<td width="100%"><nobr> <span class="page-number-separator">|</span> <logic:present
										name="cmdPageEnd">
										<keel:command name="cmdPageEnd" link="true">
											<xhtml:themeImg src="page-fast-next" border="0" align="top" />
										</keel:command>
									</logic:present> <logic:notPresent name="cmdPageEnd">
										<xhtml:themeImg src="page-fast-next-grey" border="0" align="top" />
									</logic:notPresent></nobr> <nobr> &nbsp; </nobr></td>
								</tr>
							</table>
							</td>
						</tr>
					</table>
					</td>
					<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2" width="0px" height="0px" /></td>
				</tr>
				<tr>
					<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3" width="0px" height="0px" /></td>
					<td class="c3" width="100%" height="0px"></td>
					<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3" width="0px" height="0px" /></td>
				</tr>
			</table>
		</xhtml:form>
		<table class="form" width="100%" cellpadding="0" cellspacing="0">
			<tr class="form">
				<td class="l1"><html:img page="/aktera/images/blank.gif" styleClass="l1" width="0px" height="0px" /></td>
				<td class="c1" width="100%" height="0px"></td>
				<td class="r1"><html:img page="/aktera/images/blank.gif" styleClass="r1" width="0px" height="0px" /></td>
			</tr>
			<tr class="form">
				<td class="l2"><html:img page="/aktera/images/blank.gif" styleClass="l2" width="0px" height="0px" /></td>
				<td class="content" width="100%" align="center"><logic:notPresent name="report" property="attributes.empty">
					<br>
					<br>
					<bean:write name="report" filter="false" />
					<br>
					<br>
					<br>
				</logic:notPresent> <logic:present name="report" property="attributes.empty">
					<br>
					<br>
					<bean:message key="reportProducedNoResults" bundle="Aktera" />
					<br>
					<br>
				</logic:present></td>
				<td class="r2"><html:img page="/aktera/images/blank.gif" styleClass="r2" width="0px" height="0px" /></td>
			</tr>
			<tr class="form">
				<td class="l3"><html:img page="/aktera/images/blank.gif" styleClass="l3" width="0px" height="0px" /></td>
				<td class="c3" width="100%" height="0px"></td>
				<td class="r3"><html:img page="/aktera/images/blank.gif" styleClass="r3" width="0px" height="0px" /></td>
			</tr>
		</table>
	</tiles:put>

</tiles:insert>
