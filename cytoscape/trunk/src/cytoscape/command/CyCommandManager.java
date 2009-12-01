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
	private static Map<CyCommandNamespace, Map<String,CyCommand>> comMap;
	private static Map<String, CyCommandNamespace> nsMap;
	private final static String HELP = "help"; 
	private final static CyCommandNamespace HELP_NS = new CyCommandNamespaceImpl(HELP);

	static {
		comMap = new HashMap<CyCommandNamespace, Map<String,CyCommand>>();
		nsMap = new HashMap<String, CyCommandNamespace>();

		// special case for help
		comMap.put(HELP_NS, new HashMap<String,CyCommand>());
		comMap.get(HELP_NS).put(HELP, new HelpCommand());
		nsMap.put(HELP,HELP_NS);
	}

    public static CyCommandNamespace reserveNamespace(final String namespace) 
		throws RuntimeException {

		if (nsMap.containsKey(namespace))
			throw new RuntimeException("Command namespace: "+namespace+" is already reserved");
		
		CyCommandNamespace ns = new CyCommandNamespaceImpl(namespace);
		nsMap.put(namespace, ns);
		comMap.put(ns, new HashMap<String,CyCommand>());
		
		comMap.get(HELP_NS).put(namespace, new HelpSpecificCommand(namespace));

		return ns;
	}

    /**
     * register a new CyCommand.
     *
     * @param com the command we want to register
     * @throws RuntimeException if the command is already registered
     */
	public static void register(CyCommandNamespace ns, CyCommand com) throws RuntimeException {
		if (com == null) 
			return;
		if (ns == null) 
			return;

		if ( !ns.getNamespaceName().equals(com.getNamespace()) ) 
			throw new RuntimeException("Command: " + com.toString() + 
			                           " is not part of namespace: " + ns.getNamespaceName());

		Map<String,CyCommand> subComMap = comMap.get(ns);

		if ( subComMap == null ) 
			throw new RuntimeException("Namespace is not registered!");

		if ( subComMap.containsKey( com.getCommandName() ) )
			throw new RuntimeException("Command: " + com.getNamespace() + " " + 
			                           com.getCommandName()+ " already exists!");

		subComMap.put(com.getCommandName(), com);

		// add help for this namespace if necessary
		if ( subComMap.size() == 1 )
			comMap.get(HELP_NS).put(com.getNamespace(), 
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

		CyCommandNamespace ns = nsMap.get(namespace);

		if ( ns == null )
			throw new RuntimeException("namespace has not been registered");

		Map<String,CyCommand> subComMap = comMap.get(ns);

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
		if ((namespace == null) || (namespace.length() == 0)) 
			throw new RuntimeException("null or zero length namespace");

		CyCommandNamespace ns = nsMap.get(namespace);

		if ( ns == null )
			throw new RuntimeException("namespace has not been registered");

		List<CyCommand> list = new ArrayList<CyCommand>();

		list.addAll( comMap.get(ns).values() ); 

		return list;
	}

    /**
     * Unregister a command
     *
     * @param com the command to unregister
     */
	public static void unRegister(CyCommand com) {
		CyCommandNamespace ns = nsMap.get(com.getNamespace());
		if ( ns == null )
			throw new RuntimeException("namespace has not been registered");

		Map<String,CyCommand> subComMap = comMap.get(ns);

		subComMap.remove( com.getCommandName() );

		// if this is the last command for a namespace, 
		// remove the help for the namespace
		if ( subComMap.size() == 0 ) {
			comMap.get(HELP_NS).remove(com.getNamespace());
			// TODO also make the namespace available again?
		}
	}

    /**
     * Internal implementation for the CyCommandNamespace class.
     */
    private static class CyCommandNamespaceImpl implements CyCommandNamespace {
        private String namespace;

        CyCommandNamespaceImpl(String namespace) {
            this.namespace = namespace;
        }

        public String getNamespaceName() { return namespace; }
    }
}
