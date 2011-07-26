<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>

<%@ include file="/aktera/templates/box.jsp" %>

<logic:present name="menuGroups" >

	<logic:iterate id="menuGroup" name="menuGroups" property="nested">

		<div class="functionGroup">

			<tiles:insert beanName="box" flush="false">
				<tiles:put name="width" type="string" value="100%"/>
				<tiles:put name="content" type="string">

					<bean:define id="itemNum" value="1"/>

					<h4>
						<bean:define id="bundle" name="menuGroup" property="attributes.bundle" type="java.lang.String"/>
						<bean:define id="label" name="menuGroup" property="attributes.label" type="java.lang.String"/>
						&nbsp;<xbean:message key="<%= label %>" bundle="<%= bundle %>"/>
					</h4>

					<ol>

						<logic:iterate id="menuItem" name="menuGroup" property="nested">

							<bean:define id="bundle" name="menuItem" property="attributes.bundle" type="java.lang.String"/>
							<bean:define id="label" name="menuItem" property="label" type="java.lang.String"/>

							<li>
								<xkeel:command link="true" name="menuItem" styleClass="menu">
									<xhtml:img path="/aktera/images/std/" name="menuItem" property="attributes.bigIcon"/>
									<p><xbean:message key="<%= label %>" bundle="<%= bundle %>"/></p>
								</xkeel:command>
							</li>

						</logic:iterate>

					</ol>

					<div class="clear"></div>

				</tiles:put>
			</tiles:insert>

		</div>

	</logic:iterate>

</logic:present>
