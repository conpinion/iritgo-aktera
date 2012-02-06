package de.iritgo.aktera.command;

public interface CommandManager
{
	public void executeIfAvailable(String string, CommandRequest req);
}
