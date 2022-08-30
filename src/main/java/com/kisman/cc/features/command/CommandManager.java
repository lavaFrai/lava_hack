package com.kisman.cc.features.command;

import com.kisman.cc.features.command.commands.*;
import com.kisman.cc.util.chat.ChatHandler;

import java.util.Arrays;
import java.util.HashMap;

public class CommandManager extends ChatHandler {
    public static HashMap<String, Command> commands = new HashMap<>();
	
	public char cmdPrefix = '-';
	public String cmdPrefixStr = "" + cmdPrefix;

	public CommandManager()
	{
		addCommands();
	}

	public void addCommands() {
		add(new AntiSpammerCommand());
		add(new Bind());
		add(new ConfigCommand());
		add(new DDOSCommand());
		//add(new FormatCommand());
		add(new FriendCommand());
		add(new GetUUID());
		add(new Help());
		add(new IsOnline());
		add(new LuaCommand());
        add(new Slider());
		add(new TestCommand());
        add(new OpenDir());
        //add(new Panic());
		add(new RollBackCommand());
		add(new RollBackDupeCommand());
        //add(new ShutdownCommand());
        add(new Toggle());
		add(new MusicCommand());
	}

	private void add(Command command) {
		commands.put(command.getCommand(), command);
	}

	public void runCommand(String... args) {
		boolean commandResolved = false;

		for(Command command : commands.values()) {
			if(command.getCommand().trim().equalsIgnoreCase(args[0].trim())) {
				command.runCommand(Arrays.toString(args), args);
				commandResolved = true;
				break;
			}
		}
		if(!commandResolved) error("Cannot resolve internal command: \u00a7c" + args[0]);
	}

	public void runCommands(String s) {
		String readString = s.trim().substring(Character.toString(cmdPrefix).length()).trim();
		boolean commandResolved = false;
		boolean hasArgs = readString.trim().contains(" ");
		String commandName = hasArgs ? readString.split(" ")[0] : readString.trim();
		String[] args = hasArgs ? readString.substring(commandName.length()).trim().split(" ") : new String[0];

		for(Command command : commands.values()) {
			if(command.getCommand().trim().equalsIgnoreCase(commandName.trim())) {
				command.runCommand(readString, args);
				commandResolved = true;
				break;
			}
		}
		if(!commandResolved) error("Cannot resolve internal command: \u00a7c" + commandName);
	}
}
