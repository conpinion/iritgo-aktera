<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>
<%@ taglib uri="http://aktera.iritgo.de/taglibs/html-2.1" prefix="xhtml" %>

<xhtml:form action="bean">

<p>
Hello <bean:write name="name" />!
</p>

<p>
<xkeel:command name="reload" />
</p>

</xhtml:form>
