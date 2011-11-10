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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @version $Id$
 */
public class LicenseInfoV1
{
	public static final String MIN_VERSION = "1.1";

	public static final String UNLIMITED = "unlimited";

	public static final String TYPE_FREE = "free";

	public static final String TYPE_PERSONAL = "personal";

	public static final String TYPE_HOST = "host";

	public static final String TYPE_TB2 = "tb2";

	public static final String TYPE_MASTER = "master";

	public static final String TYPE_STANDARD = "standard";

	private String id;

	private String vendor;

	private String product;

	private String version;

	private String name;

	private String company;

	private String type;

	private String serial;

	private Date validUntil;

	private Integer users;

	private boolean valid;

	private String host;

	private String[] blackList = new String[]
	{
		"5c2fe8cd-1044791f229-7fff"
	};

	public LicenseInfoV1()
	{
	}

	public LicenseInfoV1(Properties props)
	{
		id = props.getProperty("id");
		vendor = props.getProperty("vendor");
		product = props.getProperty("product");
		version = props.getProperty("version");
		type = props.getProperty("type");
		serial = props.getProperty("serial");
		name = props.getProperty("name");
		company = props.getProperty("company");
		host = props.getProperty("host");

		if (UNLIMITED.equals(props.getProperty("users")))
		{
			users = null;
		}
		else
		{
			try
			{
				users = new Integer(Integer.parseInt(props.getProperty("users")));
			}
			catch (NumberFormatException x)
			{
				users = new Integer(5);
			}
		}

		if (UNLIMITED.equals(props.getProperty("validity")))
		{
			validUntil = null;
		}
		else
		{
			try
			{
				validUntil = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US).parse(
								props.getProperty("validity"));
			}
			catch (ParseException x)
			{
				validUntil = new Date(System.currentTimeMillis() + 5 * 60 * 1000);
			}
		}

		checkLicense();
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getVendor()
	{
		return vendor;
	}

	public void setVendor(String vendor)
	{
		this.vendor = vendor;
	}

	public String getProduct()
	{
		return product;
	}

	public void setProduct(String product)
	{
		this.product = product;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getSerial()
	{
		return serial;
	}

	public void setSerial(String serial)
	{
		this.serial = serial;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getCompany()
	{
		return company;
	}

	public void setCompany(String company)
	{
		this.company = company;
	}

	public Date getValidUntil()
	{
		return validUntil;
	}

	public void setValidUntil(Date validUntil)
	{
		this.validUntil = validUntil;
	}

	public Integer getUsers()
	{
		return users;
	}

	public int getUserCount()
	{
		return users.intValue();
	}

	public boolean hasUserLimit()
	{
		return users != null;
	}

	public void setUsers(Integer users)
	{
		this.users = users;
	}

	public boolean isValid()
	{
		return valid && (validUntil == null || System.currentTimeMillis() <= validUntil.getTime());
	}

	public void setValid(boolean valid)
	{
		this.valid = valid;
	}

	protected void checkLicense()
	{
		valid = id.endsWith("License V1.0");

		if (! valid)
		{
			System.out.println("[LicenseTools] Wrong license file version");
		}

		boolean inBlackList = false;

		for (int i = 0; i < blackList.length; ++i)
		{
			if (blackList[i].equals(serial))
			{
				inBlackList = true;
			}
		}

		valid = valid && ! inBlackList;

		if (! valid)
		{
			System.out.println("[LicenseTools] Serial number is blacklisted");
		}

		if (! TYPE_MASTER.equals(type) && ! TYPE_PERSONAL.equals(type))
		{
			boolean validHost = verifyHostInfo();

			valid = valid && validHost;
		}

		String[] thisVersion = version.split("\\.");
		String[] minVersion = MIN_VERSION.split("\\.");

		if (thisVersion != null && thisVersion.length == 3 && minVersion != null && minVersion.length == 2)
		{
			int[] thisVersionI = new int[thisVersion.length];
			int[] minVersionI = new int[minVersion.length];

			for (int i = 0; i < thisVersionI.length; ++i)
			{
				try
				{
					thisVersionI[i] = Integer.parseInt(thisVersion[i]);
				}
				catch (NumberFormatException x)
				{
				}
			}

			for (int i = 0; i < minVersionI.length; ++i)
			{
				try
				{
					minVersionI[i] = Integer.parseInt(minVersion[i]);
				}
				catch (NumberFormatException x)
				{
				}
			}

			if (thisVersionI[0] < minVersionI[0] || thisVersionI[0] == minVersionI[0]
							&& thisVersionI[1] < minVersionI[1])
			{
				valid = false;
			}
		}
		else
		{
			valid = false;
		}

		if (! TYPE_HOST.equals(type) && ! TYPE_TB2.equals(type) && ! TYPE_STANDARD.equals(type)
						&& ! TYPE_PERSONAL.equals(type))
		{
			valid = false;
		}

		if (! valid)
		{
			System.out.println("[LicenseTools] Unable to verify application version");
		}
	}

	public boolean moduleAllowed(String moduleName)
	{
		if ("All Products".equals(product))
		{
			return true;
		}

		if ("connect-pbx-arep".equals(moduleName) && ! product.contains("AREP"))
		{
			return false;
		}

		if ("connect-pbx-artg".equals(moduleName) && ! product.contains("ARTG"))
		{
			return false;
		}

		return true;
	}

	public boolean appAllowed(String appName)
	{
		if ("All Products".equals(product))
		{
			return true;
		}

		if ("connect-pbx-arep".equals(appName) && ! product.contains("AREP"))
		{
			return false;
		}

		if ("connect-pbx-artg".equals(appName) && ! product.contains("ARTG"))
		{
			return false;
		}

		return true;
	}

	private boolean verifyHostInfo()
	{
		boolean validHost = false;

		Pattern re = Pattern.compile(".+ (..:..:..:..:..:..).*");

		try
		{
			Process proc = Runtime.getRuntime().exec("/sbin/ifconfig");
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;

			while ((line = in.readLine()) != null)
			{
				Matcher matcher = re.matcher(line);

				if (matcher.matches())
				{
					if (matcher.group(1).equals(host))
					{
						validHost = true;
					}
				}
			}
		}
		catch (IOException x)
		{
			System.out.println("[LicenseTools] " + x);
		}

		re = Pattern.compile(".+ (..-..-..-..-..-..-..-..-..-..-..-..-..-..-..-..).*");

		try
		{
			Process proc = Runtime.getRuntime().exec("/sbin/ifconfig");
			BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;

			while ((line = in.readLine()) != null)
			{
				Matcher matcher = re.matcher(line);

				if (matcher.matches())
				{
					if (matcher.group(1).equals(host))
					{
						validHost = true;
					}
				}
			}
		}
		catch (IOException x)
		{
			System.out.println("[LicenseTools] " + x);
		}

		if (! validHost)
		{
			System.out.println("[LicenseTools] Unable to verify host info");
		}

		return validHost;
	}
}
