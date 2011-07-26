/**
 * This file is part of the Iritgo/Aktera Framework.
 *
 * Copyright (C) 2005-2011 Iritgo Technologies.
 * Copyright (C) 2003-2005 BueroByte GbR.
 *
 * Iritgo licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iritgo.aktera.license;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @version $Id: LicenseTools.java,v 1.14 2006/09/15 10:29:45 grappendorf Exp $
 */
public class LicenseTools
{
	public static LicenseInfo licenseInfo;

	public static LicenseInfo getLicenseInfo ()
	{
		return licenseInfo;
	}

	public static LicenseInfo getLicenseInfo (Log log)
	{
		return getLicenseInfo (log, System.getProperty ("iritgo.license.path"));
	}

	public static LicenseInfo getLicenseInfo (Log log, String licensePath)
	{
		if (licenseInfo != null)
		{
			return licenseInfo;
		}

		synchronized (LicenseTools.class)
		{
			System.setProperty ("iritgo.license.path", licensePath);

			licenseInfo = loadLicense (log, licensePath);
		}

		return licenseInfo;
	}

	public static void clear ()
	{
		licenseInfo = null;
	}

	public static String machineInfo ()
	{
		StringBuffer machineInfo = new StringBuffer ();

		Pattern re = Pattern.compile ("eth0\\s.+\\s(..:..:..:..:..:..).*");

		try
		{
			Process proc = Runtime.getRuntime ().exec ("/sbin/ifconfig");
			BufferedReader in = new BufferedReader (new InputStreamReader (proc.getInputStream ()));
			String line = null;

			while ((line = in.readLine ()) != null)
			{
				Matcher matcher = re.matcher (line);

				if (matcher.matches ())
				{
					machineInfo.append (matcher.group (1) + "\n");
				}
			}
		}
		catch (IOException x)
		{
			System.out.println ("LicenseTools.machineInfo: " + x.getMessage ());
			x.printStackTrace ();
		}

		if (machineInfo.length () == 0)
		{
			re = Pattern.compile ("eth0\\s.+\\s(..-..-..-..-..-..-..-..-..-..-..-..-..-..-..-..).*");

			try
			{
				Process proc = Runtime.getRuntime ().exec ("/sbin/ifconfig");
				BufferedReader in = new BufferedReader (new InputStreamReader (proc.getInputStream ()));
				String line = null;

				while ((line = in.readLine ()) != null)
				{
					Matcher matcher = re.matcher (line);

					if (matcher.matches ())
					{
						machineInfo.append (matcher.group (1) + "\n");
					}
				}
			}
			catch (IOException x)
			{
				System.out.println ("LicenseTools.machineInfo: " + x.getMessage ());
				x.printStackTrace ();
			}
		}

		if (machineInfo.length () == 0)
		{
			return null;
		}

		String info = machineInfo.toString ();

		try
		{
			MessageDigest messageDigest = MessageDigest.getInstance ("MD5", "SUN");

			messageDigest.update (info.getBytes ());

			byte[] md5 = messageDigest.digest (info.getBytes ());

			return new String (Base64.encodeBase64 (md5));
		}
		catch (Exception x)
		{
			System.out.println ("LicenseTools.machineInfo: " + x.getMessage ());
			x.printStackTrace ();
		}

		return null;
	}

	/**
	 * Load a license.
	 *
	 * @param path The license file path.
	 * @return The license info.
	 */
	public static LicenseInfo loadLicense (Log log, String path)
	{
		String keyString = null;
		try
		{
			keyString = ((AkteraKeyProvider) Class.forName ("de.iritgo.aktera.license.AkteraKey").newInstance ()).getKey ();
		}
		catch (InstantiationException x1)
		{
			x1.printStackTrace();
		}
		catch (IllegalAccessException x1)
		{
			x1.printStackTrace();
		}
		catch (ClassNotFoundException x1)
		{
			x1.printStackTrace();
		}

		Properties props = new Properties ();

		try
		{
			BufferedReader in = new BufferedReader (new FileReader (path));
			StringBuffer sb = new StringBuffer ();
			String line = null;

			while ((line = in.readLine ()) != null)
			{
				if ("# SHA1 Signature".equals (line))
				{
					break;
				}
				else
				{
					sb.append (line + "\n");
				}
			}

			StringBuffer sbSig = new StringBuffer ();

			while ((line = in.readLine ()) != null)
			{
				sbSig.append (line + "\n");
			}

			X509EncodedKeySpec spec = new X509EncodedKeySpec (Base64.decodeBase64 (keyString.getBytes ()));
			KeyFactory keyFactory = KeyFactory.getInstance ("DSA", "SUN");
			PublicKey key = keyFactory.generatePublic (spec);

			Signature sig = Signature.getInstance ("SHA1withDSA", "SUN");

			sig.initVerify (key);
			sig.update (sb.toString ().getBytes (), 0, sb.toString ().getBytes ().length);

			boolean valid = sig.verify (Base64.decodeBase64 (sbSig.toString ().getBytes ()));

			if (! valid)
			{
				return null;
			}

			try
			{
				props.load (new StringReader (sb.toString ()));
			}
			catch (Exception x)
			{
				System.out.println ("LicenseTools.loadLicense: " + x.getMessage ());
				x.printStackTrace ();
				return null;
			}
		}
		catch (Exception x)
		{
			System.out.println ("LicenseTools.loadLicense: " + x.getMessage ());
			x.printStackTrace ();
			return null;
		}

		return new LicenseInfo (log, props);
	}
}
