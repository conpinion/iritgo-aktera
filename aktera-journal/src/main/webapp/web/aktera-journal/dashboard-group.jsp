<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/bean-2.1" prefix="xbean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>

<%@ include file="/aktera/templates/layout.jsp"%>

<%@ include file="/aktera/templates/box.jsp" %>

<tiles:insert beanName="box" flush="false">
	<tiles:put name="width" type="string" value="100%"/>
	<tiles:put name="content" type="string">

	<h4>
		<bean:write name="dashboardGroup" property="attributes.title" />
	</h4>
	<ol>
		<logic:iterate id="item" name="dashboardGroup" property="content">
			<bean:define id="renderInclude" name="item" property="attributes.renderInclude" type="java.lang.String"/>
			<bean:define id="item" name="item"  scope="page" toScope="request"/>
			<li class="news">
				<div class="news">
					<p>
						<jsp:include page="<%= renderInclude %>" flush="false"/>
					</p>
				</div>
			</li>
		</logic:iterate>
	</ol>
	<div class="clear"></div>
	</tiles:put>
</tiles:insert>
