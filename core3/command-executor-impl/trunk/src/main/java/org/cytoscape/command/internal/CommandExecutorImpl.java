package org.cytoscape.command.internal;


import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.task.NetworkTaskFactory;

public class CommandExecutorImpl {

	private final Map<String,Executor> commandExecutorMap = new HashMap<String,Executor>();
	private final static Logger logger = LoggerFactory.getLogger(CommandExecutorImpl.class);

	public void addTaskFactory(TaskFactory tf, Map props) {
		String commandName = (String)props.get("commandName");
		if ( commandName != null ) 
			commandExecutorMap.put(commandName, new TFExecutor(tf));
	}

	public void removeTaskFactory(TaskFactory tf, Map props) {
		String commandName = (String)props.get("commandName");
		if ( commandName != null ) 
			commandExecutorMap.remove(commandName);
	}

	public void addNetworkTaskFactory(NetworkTaskFactory tf, Map props) {
		String commandName = (String)props.get("commandName");
		if ( commandName != null ) 
			commandExecutorMap.put(commandName, new NTFExecutor(tf));
	}

	public void removeNetworkTaskFactory(NetworkTaskFactory tf, Map props) {
		String commandName = (String)props.get("commandName");
		if ( commandName != null ) 
			commandExecutorMap.remove(commandName);
	}

	public void executeList(List<String> commandLines) {
		try {
			for ( String line : commandLines ) { 
				System.out.println("begin parsing line: '" + line + "'");
				for ( String command : commandExecutorMap.keySet() ) {
					System.out.println("testing command: '" + command + "'");
					if ( line.length() < command.length() ) 
						continue;
					final String comm = line.substring(0,command.length()); 
					final String args = line.substring(command.length(),line.length()); 
					System.out.println("comm: '" + comm + "'");
					System.out.println("args: '" + args + "'");
					if ( comm.equals(command) ) {
						System.out.println(" -- executing");
						Executor e = commandExecutorMap.get(command);
						e.execute(args);
						break;
					} else {
						System.out.println(" -- trying next");
					}
				}
			}
		} catch (Exception e) {
			logger.error("Command parsing error: ", e);
		}
	}
}
