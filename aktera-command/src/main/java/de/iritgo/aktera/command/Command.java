package de.iritgo.aktera.command;

public interface Command
{
	public CommandResponse execute(CommandRequest request);
}
