/*
File: CyCommandManager.java

Copyright (c) 2009, The Cytoscape Consortium (www.cytoscape.org)

The Cytoscape Consortium is:
- Institute for Systems Biology
- University of California San Diego
- Memorial Sloan-Kettering Cancer Center
- Institut Pasteur
- Agilent Technologies
- University of California San Francisco

This library is free software; you can redistribute it and/or modify it
under the terms of the GNU Lesser General Public License as published
by the Free Software Foundation; either version 2.1 of the License, or
any later version.

This library is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
documentation provided hereunder is on an "as is" basis, and the
Institute for Systems Biology and the Whitehead Institute
have no obligations to provide maintenance, support,
updates, enhancements or modifications.  In no event shall the
Institute for Systems Biology and the Whitehead Institute
be liable to any party for direct, indirect, special,
incidental or consequential damages, including lost profits, arising
out of the use of this software and its documentation, even if the
Institute for Systems Biology and the Whitehead Institute
have been advised of the possibility of such damage.  See
the GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this library; if not, write to the Free Software Foundation,
Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.command;

import java.io.StreamTokenizer;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * CyCommandManager is a singleton Cytoscape class that provides a global registry
 * of {@link CyCommand}s.  Each CyCommand is added to the registry through the register
 * method. Commands may be accessed with a combination of command name and namespace.
 */
public class CyCommandManager {
	private static Map<String, Map<String,CyCommand>> comMap;
	private final static String HELP = "help";

	static {
		comMap = new HashMap<String, Map<String,CyCommand>>();

		// special case for help
		comMap.put( HELP, new HashMap<String,CyCommand>());
		comMap.get(HELP).put(HELP, new HelpCommand());
	}

    /**
     * register a new CyCommandHandler.
     *
     * @param com the command we want to register
     * @throws RuntimeException if the command is already registered
     */
	public static void register(CyCommand com) throws RuntimeException {
		if (com == null) 
			return;

		if (!comMap.containsKey( com.getNamespace())) 
			comMap.put( com.getNamespace(), new HashMap<String,CyCommand>() );

		Map<String,CyCommand> subComMap = comMap.get(com.getNamespace());

		if ( subComMap.containsKey( com.getCommandName() ) )
			throw new RuntimeException("Command: " + com.getNamespace() + " " + 
			                           com.getCommandName()+ " already exists!");

		subComMap.put(com.getCommandName(), com);

		// add help for this namespace if necessary
		if ( subComMap.size() == 1 )
			comMap.get(HELP).put(com.getNamespace(), 
			                       new HelpSpecificCommand(com.getNamespace()));
	}

    /**
     * return a CyCommand by either name or class name.
     *
     * @param name either the name of the class or command
     * @return the command, or null if a command with name or class doesn't exist
     */
	public static CyCommand getCommand(String namespace, String name) {
		if ((namespace == null) || (namespace.length() == 0)) 
			throw new RuntimeException("null or zero length namespace");

		if ((name == null) || (name.length() == 0)) 
			throw new RuntimeException("null or zero length command name");

		Map<String,CyCommand> subComMap = comMap.get(namespace);
		if ( subComMap == null )
			throw new RuntimeException("namespace " + namespace + " does not exist!");

		return subComMap.get(name);
	}

    /**
     * Get the list of all commands that are currently registered.
     *
     * @return the list of commands that are currently registered
     */
	public static List<CyCommand> getCommandList() {
		List<CyCommand> list = new ArrayList<CyCommand>();

		for ( Map<String,CyCommand> subComMap : comMap.values() ) 
			list.addAll( subComMap.values() );

		return list;
	}

    /**
     * Get the list of all commands that are currently registered
	 * for the specified namespace.
     *
	 * @param namespace the namespace of the commands to retrieve.
     * @return the list of commands that are currently registered
     */
	public static List<CyCommand> getCommandList(String namespace) {
		List<CyCommand> list = new ArrayList<CyCommand>();

		if ( comMap.containsKey( namespace ) )
			list.addAll( comMap.get(namespace).values() ); 

		return list;
	}

    /**
     * Unregister a command
     *
     * @param com the command to unregister
     */
	public static void unRegister(CyCommand com) {

		Map<String,CyCommand> subComMap = comMap.get(com.getNamespace());

		subComMap.remove( com.getCommandName() );

		// if this is the last command for a namespace, 
		// remove the help for the namespace
		if ( subComMap.size() == 0 )
			comMap.get(HELP).remove(com.getNamespace());
	}
}
