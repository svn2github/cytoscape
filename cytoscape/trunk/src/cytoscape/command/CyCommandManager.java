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

import cytoscape.layout.Tunable;


/**
 * CyCommandManager is a singleton Cytoscape class that provides a global registry
 * of {@link CyCommand}s and {@link CyCommandNamespace}s.  Each CyCommand is added to the registry through the register
 * method. Commands may be accessed with a combination of command name and namespace.
 */
public class CyCommandManager {
	private static Map<CyCommandNamespace, Map<String,CyCommandHandler>> comMap;
	private static Map<String, CyCommandNamespace> nsMap;

  static {
    comMap = new HashMap<CyCommandNamespace, Map<String,CyCommandHandler>>();
		nsMap = new HashMap<String, CyCommandNamespace>();
	}

	public static CyCommandNamespace reserveNamespace(String namespace) throws RuntimeException {
		if (namespace == null || namespace.length() == 0) return null;

		namespace = namespace.toLowerCase();
		if (nsMap.containsKey(namespace))
			throw new RuntimeException("Command namespace: "+namespace+" is already reserved");

		CyCommandNamespace ns = new CyCommandNamespaceImpl(namespace);
		nsMap.put(namespace, ns);
		comMap.put(ns, new HashMap());

		return ns;
	}

	/**
	 * register a new CyCommandHandler.
	 *
	 * @param ns the namespace for this command
	 * @param command the command string that identified this command
	 * @param com the command handler for the command we want to register
	 * @throws RuntimeException if the command is already registered
	 */
	public static void register(CyCommandNamespace ns, String command, CyCommandHandler com) throws RuntimeException {
		if (com == null) 
			return;

		if (!comMap.containsKey(ns))
			throw new RuntimeException("Command namespace: "+ns.getNamespaceName()+" is not yet registered");

		Map<String,CyCommandHandler> subComMap = comMap.get(ns);

		if ( subComMap.containsKey( command ) )
			throw new RuntimeException("Command: " + ns.getNamespaceName() + " " + 
			                           command + " already exists!");

		subComMap.put(command, com);
	}

	/**
	 * return a CyCommandHandler by either name or class name.
	 *
	 * @param name either the name of the class or command
	 * @return the command, or null if a command with name or class doesn't exist
	 */
	public static CyCommandHandler getCommand(String namespace, String name) {
		if ((namespace == null) || (namespace.length() == 0)) 
			throw new RuntimeException("null or zero length namespace");

		namespace = namespace.toLowerCase();

		if ((name == null) || (name.length() == 0)) 
			throw new RuntimeException("null or zero length command name");

		CyCommandNamespace ns = nsMap.get(namespace);
		if ( ns == null )
			throw new RuntimeException("namespace '" + namespace + "' does not exist!");

		Map<String,CyCommandHandler> subComMap = comMap.get(ns);
		return subComMap.get(name);
	}

	/**
	 * Get the list of all of the currently registered namespaces.  Note
	 * that only the <i>name</i> of the namespace is returned.  Only the
	 * plugin that registered the namespace may have access to the namespace
	 * object itself.
	 *
	 * @return list of namespaces
	 */
	public static List<String> getNamespaceList() {
		List<String>list = new ArrayList();
		list.addAll(nsMap.keySet());
		return list;
	}

	/**
	 * Get the list of all commands that are currently registered,
	 * organized by namespaces
	 *
	 * @return the map of commands that are currently registered, with
	 * the namespaced used to register that command as a key
	 */
	public static Map<String, List<CyCommandHandler>> getCommandMap() {
		Map<String, List<CyCommandHandler>> map = new HashMap<String,List<CyCommandHandler>>();

		for ( CyCommandNamespace namespace : comMap.keySet() )  {
			List<CyCommandHandler> list = new ArrayList<CyCommandHandler>();
			list.addAll( comMap.get(namespace).values() );
			map.put(namespace.getNamespaceName(), list);
		}

		return map;
	}

	/**
	 * Get the list of all commands that are currently registered
	 * for the specified namespace.
	 *
	 * @param namespace the namespace of the commands to retrieve.
	 * @return the list of commands that are currently registered
	 */
	public static List<String> getCommandList(String namespace) {
		List<String> list = new ArrayList<String>();
		if (!nsMap.containsKey(namespace))
			return null;

		CyCommandNamespace ns = nsMap.get(namespace.toLowerCase());

		if ( comMap.containsKey( ns ) )
			list.addAll( comMap.get(ns).keySet() ); 

		return list;
	}

	/**
	 * Unregister a command
	 *
	 * @param ns the namespace of the command to unregister
	 * @param command the command name to unregister
	 */
	public static void unRegister(CyCommandNamespace ns, String command) {

		if (!comMap.containsKey(ns))
			return;

		Map<String,CyCommandHandler> subComMap = comMap.get(ns);

		subComMap.remove( command );
	}

	/**
	 * Execute a command.
	 *
	 * @param namespace the namespace for this command
	 * @param command the command
	 * @param arguments the argument list to pass to the command
	 * @return the CyCommandResult
	 * @throws RuntimeException is the namespace or command is not yet registered
	 * @throws CyCommandException if there is an error with the execution
	 */
	public static CyCommandResult execute(String namespace, String command, Map<String,Object>arguments) throws CyCommandException, RuntimeException {
		CyCommandHandler handler = getHandler(namespace, command);

		return handler.execute(command, arguments);
	}

	/**
	 * Execute a command
	 *
	 * @param namespace the namespace for this command
	 * @param command the command
	 * @param arguments the argument list to pass to the command
	 * @return the CyCommandResult
	 * @throws RuntimeException is the namespace or command is not yet registered
	 * @throws CyCommandException if there is an error with the execution
	 */
	public static CyCommandResult execute(String namespace, String command, List<Tunable>arguments) throws CyCommandException, RuntimeException {
		CyCommandHandler handler = getHandler(namespace, command);

		return handler.execute(command, arguments);
	}

	/**
	 * Internal method to get a CyCommandHandler when given a namespace and a command.
	 *
	 * @param namespace the namespace of the command
	 * @param command the command itself
	 */
	private static CyCommandHandler getHandler(String namespace, String command) {
		if (!nsMap.containsKey(namespace))
			throw new RuntimeException("The namespace "+namespace+" is unknown");

		CyCommandNamespace ns = nsMap.get(namespace);
		Map<String,CyCommandHandler> subComMap = comMap.get(ns);

		if (!subComMap.containsKey(command))
			throw new RuntimeException("The command "+namespace+" "+command+" isn't registered");

		return subComMap.get(command);
	}

	/**
	 * Internal implementation for the CyCommandNamespace class.
	 */
	private static class CyCommandNamespaceImpl implements CyCommandNamespace {
		private String ns;

		protected CyCommandNamespaceImpl(String namespace) {
			this.ns = namespace;
		}

		public String getNamespaceName() { return ns; }
	}
}
