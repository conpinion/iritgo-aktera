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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Properties;


/**
 * @version $Id$
 */
public class LicenseToolsV1
{
	public static LicenseInfoV1 licenseInfo;

	public static LicenseInfoV1 getLicenseInfo()
	{
		return getLicenseInfo(System.getProperty("iritgo.license.path"));
	}

	public static LicenseInfoV1 getLicenseInfo(String licensePath)
	{
		if (licenseInfo != null)
		{
			return licenseInfo;
		}

		String keyString = null;
		try
		{
			keyString = ((AkteraKeyProvider) Class.forName("de.iritgo.aktera.license.AkteraKey").newInstance())
							.getKey();
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

		synchronized (LicenseTools.class)
		{
			System.setProperty("iritgo.license.path", licensePath);

			Properties props = new Properties();

			try
			{
				BufferedReader in = new BufferedReader(new FileReader(licensePath));
				StringBuffer sb = new StringBuffer();
				String line = null;

				while ((line = in.readLine()) != null)
				{
					if ("# SHA1 Signature".equals(line))
					{
						break;
					}
					else
					{
						sb.append(line + "\n");
					}
				}

				StringBuffer sbSig = new StringBuffer();

				while ((line = in.readLine()) != null)
				{
					sbSig.append(line + "\n");
				}

				X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decodeBase64(keyString.getBytes()));
				KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
				PublicKey key = keyFactory.generatePublic(spec);

				Signature sig = Signature.getInstance("SHA1withDSA", "SUN");

				sig.initVerify(key);
				sig.update(sb.toString().getBytes(), 0, sb.toString().getBytes().length);

				boolean valid = sig.verify(Base64.decodeBase64(sbSig.toString().getBytes()));

				if (! valid)
				{
					System.out.println("[LicenseTools] Unable to verify license signature");
					System.out.println("[LicenseTools] Public key info:");
					System.out.println(key.toString());
					System.out.println("[LicenseTools] Signature info:");
					System.out.println(sig.toString());
					System.out.println("[LicenseTools] License signature : " + sbSig.toString());

					return null;
				}

				try
				{
					props.load(new StringReader(sb.toString()));
				}
				catch (Exception x)
				{
					System.out.println("[LicenseTools] " + x);
				}
			}
			catch (Exception x)
			{
				System.out.println("[LicenseTools] " + x);

				return null;
			}

			licenseInfo = new LicenseInfoV1(props);
		}

		return licenseInfo;
	}

	public static void clear()
	{
		licenseInfo = null;
	}
}
