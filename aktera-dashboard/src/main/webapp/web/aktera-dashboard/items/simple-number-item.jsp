<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>

<%@ include file="/aktera/templates/layout.jsp"%>

<%@ include file="/aktera/templates/box.jsp" %>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/tip-32" align="middle" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:define id="overviewTitle" name="title" property="content" type="java.lang.String" />
		<bean:define id="overviewBundle" name="bundle" property="content" type="java.lang.String" />
		<bean:message key="<%= overviewTitle %>" bundle="<%= overviewBundle %>" />
	</tiles:put>



	<tiles:put name="content" type="string">

	<table width="100%">
	<tr>
		<td width="100%" valign="top">
		<div class="functionGroup">

		<tiles:insert beanName="box" flush="false">
			<tiles:put name="width" type="string" value="100%"/>
			<tiles:put name="content" type="string">

			<h4>
				<bean:define id="journalTitle" name="journalTitle" property="content" type="java.lang.String" />
				<bean:define id="journalBundle" name="journalBundle" property="content" type="java.lang.String" />
				&nbsp;<xbean:message key="<%= journalTitle %>" bundle="<%= journalBundle %>"/>
			</h4>

			<ol>
				<logic:iterate id="journalEntry" name="journalEntries" property="nested">
					<bean:define id="imageUrl" name="journalEntry" property="attributes.imageUrl" type="java.lang.String"/>
					<li class="news">
						<div class="news">
							<div class="pic">
								<img src="<%= imageUrl %>"></img>
							</div>
							<div class="text">
								<div class="news-type"><bean:write name="journalEntry" property="attributes.typeOfNews"/></div>
								<div class="news-text"><h1><b><bean:write name="journalEntry" property="attributes.headerLine"/></b></h1></div>
								<div class="news-text">
									<p>
									<bean:write name="journalEntry" property="attributes.message"/>
									</p>
								</div>
								<div class="news-footer"><bean:write name="journalEntry" property="attributes.date"/>&nbsp;<bean:write name="journalEntry" property="attributes.producer"/>
								</div>
							</div>
						<div class="footer"></div>
						</div>
					</li>
				</logic:iterate>
			</ol>
			<div class="clear"></div>
		</tiles:put>
		</tiles:insert>




		<tiles:insert beanName="box" flush="false">
			<tiles:put name="width" type="string" value="100%"/>
			<tiles:put name="content" type="string">

			<h4>
				&nbsp;<xbean:message key="licenseOverview" bundle="IPtellBase"/>
			</h4>

			<ol>
				<li>
					<p>
						<xbean:message key="licenseUserLimits" bundle="IPtellBase"/>:&nbsp;
						<bean:write name="licenseCurrentUsers" property="content"/>
						/
						<bean:write name="licenseUserLimits" property="content"/>
					</p>
				</li>
				<li>
					<p>
						<xbean:message key="licenseVoiceboxLimits" bundle="IPtellBase"/>:&nbsp;
						<bean:write name="licenseCurrentVoiceboxes" property="content"/>
						/
						<bean:write name="licenseVoiceboxLimits" property="content"/>
					</p>
				</li>
			</ol>

			<div class="clear"></div>
		</tiles:put>
		</tiles:insert>

		<tiles:insert beanName="box" flush="false">
			<tiles:put name="width" type="string" value="100%"/>
			<tiles:put name="content" type="string">

			<h4>
				&nbsp;<xbean:message key="systemInformation" bundle="IPtellBase"/>
			</h4>
			<ol>
				<li>
					<p>
						Test
					</p>
				</li>
			</ol>


			<div class="clear"></div>
		</tiles:put>
		</tiles:insert>

		</div>
		</td>
	</tr>
	</table>

	</tiles:put>

</tiles:insert>
