package de.iritgo.aktera.base.server;

import java.io.*;

import de.iritgo.simplelife.process.*;

public class SystemFirewall
{
	public static void disable ()
	{
		ProcessCommunicator processCommunicator = new ProcessCommunicator ("/usr/bin/sudo", "/opt/iritgo/scripts/unblock-network-access.sh");
		try
		{
			processCommunicator.start();
		}
		catch (IOException x)
		{
			System.out.println ("Can't execute firewall script..." + x);
		}
	}

	public static void enable ()
	{
		ProcessCommunicator processCommunicator = new ProcessCommunicator ("/usr/bin/sudo", "/opt/iritgo/scripts/block-network-access.sh");
		try
		{
			processCommunicator.start();
		}
		catch (IOException x)
		{
			System.out.println ("Can't execute firewall script..." + x);
		}
	}
}
