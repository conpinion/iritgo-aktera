<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://aktera.iritgo.de/taglibs/keel-2.1" prefix="xkeel" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<xkeel:callModel model="aktera.tools.get-current-date"/>

<html>
  <head>
    <title>Current date</title>
  </head>

  <body>

	<bean:write name="currentDate"/>

  </body>

</html>
