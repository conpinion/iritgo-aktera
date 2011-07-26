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
		<hr />
		<br />
		Tenured Gen<br />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Tenured GenUsage" />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Tenured GenPeak" />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Tenured GenCollection" />
		<br />
		<br />
		Survivor Space<br />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Survivor SpaceUsage" />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Survivor SpacePeak" />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Survivor SpaceCollection" />
		<br />
		<br />
		Perm Gen<br />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Perm GenUsage" />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Perm GenPeak" />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Perm GenCollection" />
		<br />
		<br />
		Eden Space<br />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Eden SpaceUsage" />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Eden SpacePeak" />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Eden SpaceCollection" />
		<br />
		<br />
		Code Cache<br>
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Code CacheUsage" />
		<html:img width="300" height="250" styleId="systemTime"
			page="/model.do?model=aktera.tools.show-jvm-diagram&name=Code CachePeak" />
		<br />
		<br />
	</tiles:put>

</tiles:insert>
