package ar.edu.itba.pdc.parser.executors;

import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class ListCommandExecutor implements CommandExecutor{

	private static ListCommandExecutor instance = null;
	private ConfigurationCommands commandManager;
	
	public static ListCommandExecutor getInstance() {
		if (instance == null)
			instance = new ListCommandExecutor();		
		return instance;
	}
	
	public ListCommandExecutor() {
		commandManager = ConfigurationCommands.getInstance();
	}
	
	public boolean execute(String command, String value) {	
		String oldValue = "";
		if (commandManager.hasProperty(command))
			oldValue = commandManager.getProperty(command);
		String newValue = value;
		
		if (oldValue != null && oldValue.contains(newValue)) {
			return false;
		}

		if (oldValue != null && !oldValue.equals(""))
			commandManager.setProperty(command, oldValue + ";"
					+ newValue);
		else
			commandManager.setProperty(command, newValue);
		return true;
	}

}