<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://www.keelframework.org/struts/keel-1.0" prefix="keel" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>

<table width="100%">
	<tr>

		<td width="50%">

			<table width="100%">
				<tr>

					<td width="100%">
						<nobr>
						&nbsp;
						<logic:present name="back">
							<keel:command name="back" styleClass="cancel-button"/>
						</logic:present>
						</nobr>
					</td>

					<td>
						<nobr>
							<logic:present name="<%= listId %>" property="attributes.cmdPageStart">
								<xkeel:command name="<%= listId %>" property="attributes.cmdPageStart" link="true" bundle="Aktera" titleKey="start">
									<xhtml:themeImg src="page-fast-back" border="0" align="top"/>
								</xkeel:command>
							</logic:present>
							<logic:notPresent name="<%= listId %>" property="attributes.cmdPageStart">
									<xhtml:themeImg src="page-fast-back-grey" border="0" align="top"/>
							</logic:notPresent>
							<span class="page-number-separator">|<span>
						</nobr>
					</td>
					<td>
						<nobr>
							<logic:present name="<%= listId %>" property="attributes.cmdPageBack">
								<xkeel:command name="<%= listId %>" property="attributes.cmdPageBack" link="true" bundle="Aktera" titleKey="back">
									<xhtml:themeImg src="page-back" border="0" align="top"/>
								</xkeel:command>
							</logic:present>
							<logic:notPresent name="<%= listId %>" property="attributes.cmdPageBack">
								<xhtml:themeImg src="page-back-grey" border="0" align="top"/>
							</logic:notPresent>
							<span class="page-number-separator">|<span>
						</nobr>
					</td>

					<logic:present name="<%= listId %>" property="attributes.prevPages">
						<logic:iterate id="cmdPage" name="<%= listId %>" property="attributes.prevPages.nested">
							<td valign="middle">
								<nobr>
								<keel:command name="cmdPage" link="true">
									<bean:write name="cmdPage" property="label"/>
								</keel:command>
								<span class="page-number-separator">|<span>
								</nobr>
							</td>
						</logic:iterate>
					</logic:present>

				</tr>
			</table>
		</td>

		<td align="center">
			<logic:present name="<%= listId %>" property="attributes.page">
				<nobr><div class="page-number-current"><bean:write name="<%= listId %>" property="attributes.page"/></div></nobr>
			</logic:present>
		</td>

		<td width="50%">

			<table width="100%">
				<tr>

					<logic:present name="<%= listId %>" property="attributes.nextPages">
						<logic:iterate id="cmdPage" name="<%= listId %>" property="attributes.nextPages.nested">
							<td valign="middle">
								<nobr>
								<span class="page-number-separator">|<span>
								<keel:command name="cmdPage" link="true">
									<bean:write name="cmdPage" property="label"/>
									</keel:command>
								</nobr>
							</td>
						</logic:iterate>
					</logic:present>

					<td>
						<nobr>
							<span class="page-number-separator">|<span>
							<logic:present name="<%= listId %>" property="attributes.cmdPageNext">
								<xkeel:command name="<%= listId %>" property="attributes.cmdPageNext" link="true" bundle="Aktera" titleKey="next">
									<xhtml:themeImg src="page-next" border="0" align="top"/>
								</xkeel:command>
							</logic:present>
							<logic:notPresent name="<%= listId %>" property="attributes.cmdPageNext">
								<xhtml:themeImg src="page-next-grey" border="0" align="top"/>
							</logic:notPresent>
						</nobr>
					</td>

					<td width="100%">
						<nobr>
							<span class="page-number-separator">|<span>
							<logic:present name="<%= listId %>" property="attributes.cmdPageEnd">
								<xkeel:command name="<%= listId %>" property="attributes.cmdPageEnd" link="true" bundle="Aktera" titleKey="end">
									<xhtml:themeImg src="page-fast-next" border="0" align="top"/>
								</xkeel:command>
							</logic:present>
							<logic:notPresent name="<%= listId %>" property="attributes.cmdPageEnd">
								<xhtml:themeImg src="page-fast-next-grey" border="0" align="top"/>
							</logic:notPresent>
						</nobr>
						<nobr>
						&nbsp;
						</nobr>
					</td>

				</tr>
			</table>

		</td>

	</tr>
</table>

