<%@ page import="javax.servlet.http.HttpServlet" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="de.iritgo.aktera.clients.ResponseElementDynaBean" %>
<%@ page import="de.iritgo.aktera.comm.BinaryWrapper" %>

<%@ page extends="javax.servlet.http.HttpServlet" %>

<%!
	protected void doGet (HttpServletRequest req, HttpServletResponse res)
		throws ServletException, IOException
	{
		try
		{
			String contentType = null;
			String fileName = null;
			byte[] data = null;

			List outputs = (List) req.getAttribute ("outputs");

			if (outputs != null)
			{
				for (Iterator i = outputs.iterator (); i.hasNext ();)
				{
					ResponseElementDynaBean output = (ResponseElementDynaBean) i.next ();

					String name = (String) output.get ("name");

					if ("data".equals (name))
					{
						data = (byte[]) output.get ("content");
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

			res.setContentType (contentType);
			res.setHeader ("Content-disposition", "filename=" + fileName);
			res.setContentLength (data.length);
			ServletOutputStream out = res.getOutputStream ();
			out.write (data);
		}
		catch (Exception x)
		{
			res.sendError (404);
		}
	}
%>
