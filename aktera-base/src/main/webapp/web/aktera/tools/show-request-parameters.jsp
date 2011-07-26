<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ page import="java.util.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Show request parameters</title>
  </head>

  <body>
    <h1>Show request parameters</h1>

<%
	System.out.println ("Request parameters:");
	for (Enumeration e = request.getParameterNames (); e.hasMoreElements (); )
	{
		String name = (String) e.nextElement ();
		out.println (name + " : " + request.getParameter (name) + "</br>");
		System.out.println ("Param " + name + ":" + request.getParameter (name));
	}

	System.out.println ("Request headers:");

	for (Enumeration e = request.getHeaderNames (); e.hasMoreElements (); )
	{
		String name = (String) e.nextElement ();
		out.println (name + " : " + request.getHeader (name) + "</br>");
		System.out.println ("Header " + name + ":" + request.getHeader (name));
	}
%>

  </body>
</html>
