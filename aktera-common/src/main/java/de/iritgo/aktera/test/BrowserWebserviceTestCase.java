
package de.iritgo.aktera.test;


import org.junit.BeforeClass;


public class BrowserWebserviceTestCase extends BrowserTestCase
{

	protected static WebserviceTestCase webserviceTestCase = new WebserviceTestCase ();

	@BeforeClass
	public static void beforeStartup ()
	{
		BrowserTestCase.beforeStartup ();
		webserviceTestCase.beforeStartup ();
	}

	public static WebserviceRequest createWebserviceRequest (String serviceBaseURL, String serviceName, String username,
					String password)
	{
		return webserviceTestCase.createWebserviceRequest (serviceBaseURL, serviceName, username, password);
	}

}
