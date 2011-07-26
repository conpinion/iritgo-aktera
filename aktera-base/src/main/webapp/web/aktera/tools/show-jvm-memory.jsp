<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml"%>

<%@ page import="java.lang.management.*"%>
<%@ page import="java.util.*"%>

<%@ include file="/aktera/templates/layout.jsp"%>

<tiles:insert beanName="site-template" flush="true">

	<tiles:put name="icon" type="string">
		<xhtml:img src="/aktera/images/std/report-32" />
	</tiles:put>

	<tiles:put name="title" type="string">
		<bean:message key="memoryUsage" bundle="connect-pbx-tb2" />
	</tiles:put>

	<tiles:put name="content" type="string">

		<xhtml:form action="bean" method="post">
			<xkeel:command name="cmd5Minutes" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
				<bean:message key="showJvmMemory5Minutes" bundle="Aktera" />
			</xkeel:command>
			<xkeel:command name="cmd30Minutes" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
				<bean:message key="showJvmMemory30Minutes" bundle="Aktera" />
			</xkeel:command>
			<xkeel:command name="cmd60Minutes" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
				<bean:message key="showJvmMemory60Minutes" bundle="Aktera" />
			</xkeel:command>
			<xkeel:command name="cmd120Minutes" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
				<bean:message key="showJvmMemory120Minutes" bundle="Aktera" />
			</xkeel:command>
			<xkeel:command name="cmdDay" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
				<bean:message key="showJvmMemoryDay" bundle="Aktera" />
			</xkeel:command>
			<xkeel:command name="cmdWeek" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
				<bean:message key="showJvmMemoryWeek" bundle="Aktera" />
			</xkeel:command>
			<xkeel:command name="cmdMonth" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
				<bean:message key="showJvmMemoryMonth" bundle="Aktera" />
			</xkeel:command>
			<xkeel:command name="cmdYear" styleClass="form-button" icon="/aktera/images/std/tool-ok-16">
				<bean:message key="showJvmMemoryYear" bundle="Aktera" />
			</xkeel:command>
			<xkeel:command name="cmdGC" styleClass="form-button" icon="/aktera/images/std/tool-reload-16">
				<bean:message key="gc" bundle="Aktera" />
			</xkeel:command>
			<hr />
			<br />
		</xhtml:form>
		<%
			Iterator iter = ManagementFactory.getMemoryPoolMXBeans ().iterator ();
					while (iter.hasNext ())
					{
						MemoryPoolMXBean item = (MemoryPoolMXBean) iter.next ();
		%>
		<h1><%=item.getName ()%></h1>
		<br />
		<table>
			<tr>
				<td><b>Type:&nbsp;</b></td>
				<td><%=item.getType ()%></td>
			</tr>
			<tr>
				<td><b>Usage:&nbsp;</b></td>
				<td><%=item.getUsage ()%></td>
			</tr>
			<tr>
				<td><b>Peak Usage:&nbsp;</b></td>
				<td><%=item.getPeakUsage ()%></td>
			</tr>
			<tr>
				<td><b>Collection Usage:&nbsp;</b></td>
				<td><%=item.getCollectionUsage ()%></td>
			</tr>
		</table>
		<br />
		<hr />
		<br />
		<%
			}
		%>

	</tiles:put>

</tiles:insert>
