<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="de.iritgo.aktera.clients.ResponseElementDynaBean" %>

<%@ page extends="javax.servlet.http.HttpServlet" %>

<%!

	protected void service (HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		String contentType = null;
		String fileName = null;
		byte[] report = null;

		List outputs = (List) req.getAttribute ("outputs");

		if (outputs != null)
		{
			for (Iterator i = outputs.iterator (); i.hasNext ();)
			{
				ResponseElementDynaBean output = (ResponseElementDynaBean) i.next ();

				String name = (String) output.get ("name");

				if ("report".equals (name))
				{
					report = (byte[]) output.get ("content");
				}
				else if ("contentType".equals (name))
				{
					contentType = (String) output.get ("content");
				}
				else if ("fileName".equals (name))
				{
					fileName = (String) output.get ("content");
				}
			}
		}

		if (report == null)
		{
			throw new ServletException ("Missing output 'report'!");
		}
		if (contentType == null)
		{
			throw new ServletException ("Missing output 'contentType'!");
		}
		if (fileName == null)
		{
			throw new ServletException ("Missing output 'fileName'!");
		}

		res.setContentType (contentType);
		res.setHeader ("Content-disposition", "inline; filename=" + fileName);
		res.setContentLength (report.length);
		ServletOutputStream out = res.getOutputStream ();
		out.write (report);
	}

%>
