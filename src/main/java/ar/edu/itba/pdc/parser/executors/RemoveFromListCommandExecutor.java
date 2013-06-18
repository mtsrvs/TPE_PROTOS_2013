package ar.edu.itba.pdc.parser.executors;

import ar.edu.itba.pdc.utils.ConfigurationCommands;

public class RemoveFromListCommandExecutor implements CommandExecutor {

	private static RemoveFromListCommandExecutor instance = null;
	private ConfigurationCommands commandManager;
	
	public static RemoveFromListCommandExecutor getInstance() {
		if (instance == null)
			instance = new RemoveFromListCommandExecutor();		
		return instance;
	}
	
	public RemoveFromListCommandExecutor() {
		commandManager = ConfigurationCommands.getInstance();
	}
	
	public String execute(String command, String value) {	
		String newValue = "";
		String oldValue = "";
		
		if (commandManager.hasProperty("silenceuser"))
			oldValue = commandManager.getProperty("silenceuser");
		
		if (oldValue != "" && oldValue != null && !oldValue.contains(value)) {
			return null;
		}

		String[] oldValuesArray = oldValue.split(";");
		
		for (String auxVal : oldValuesArray) {
			if (!auxVal.equals(value)) {
				newValue += ";" + auxVal;
			}
		}
		
		commandManager.setProperty("silenceuser", newValue.substring(1));
		
		return "OK";
	}
	
}
