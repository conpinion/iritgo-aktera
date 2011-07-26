<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<%@ include file="/aktera/templates/layout.jsp"%>
<%@ include file="/aktera/templates/box.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/debug-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="showContextObjects" bundle="Aktera" />
	</tiles:put>

	<tiles:put name="content" type="string">

		<tiles:insert beanName="box" flush="false">
			<tiles:put name="width" type="string" value="100%"/>
			<tiles:put name="content" type="string">

				<logic:iterate id="context" name="contexts" property="nested">
					<div><b><bean:write name="context" /></b></div>
					<logic:iterate id="entry" name="context" property="nested">
						<div class="hgap"><bean:write name="entry" /></div>
						<logic:iterate id="attr" name="entry" property="nested">
							<div class="hhgap"><bean:write name="attr" /></div>
						</logic:iterate>
					</logic:iterate>
				</logic:iterate>

			</tiles:put>
		</tiles:insert>

	</tiles:put>

</tiles:insert>
