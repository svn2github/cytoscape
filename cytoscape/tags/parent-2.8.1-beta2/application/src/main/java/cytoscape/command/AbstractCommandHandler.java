/* vim: set ts=2: */
/**
 * Copyright (c) 2007 The Regents of the University of California.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions, and the following disclaimer.
 *   2. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions, and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   3. Redistributions must acknowledge that this software was
 *      originally developed by the UCSF Computer Graphics Laboratory
 *      under support by the NIH National Center for Research Resources,
 *      grant P41-RR01081.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 * BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package cytoscape.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;

/**
 * This abstract class provides a convenient (but not necessary) base class
 * for writing CyCommandHandlers.  It may be used whether your handler handles
 * a single command or multiple commands.  The general use of this base class
 * is to add a set of known arguments using the {@link AbstractCommandHandler#addArgument}() method
 * and {@link AbstractCommandHandler#addDescription}() methods within the constructor of your
 * command handler:
 * <pre><code>
 * public class MyCommandHandler extends AbstractCommandHandler {
 *   // Define our command name
 *   private static String COMMAND = "my command";
 *
 *   // Settings
 *   private static String ARGKEY1 = "argkey1";
 *   private static String ARGKEY2 = "argkey2";
 *   private static String ARGKEY3 = "argkey3";
 *
 *   public MyCommandHandler({@link CyCommandNamespace} ns) {
 *     super(ns);
 *     {@link AbstractCommandHandler#addDescription}(COMMAND, "This command does something really cool");
 *     {@link AbstractCommandHandler#addArgument}(COMMAND, ARGKEY1);
 *     {@link AbstractCommandHandler#addArgument}(COMMAND, ARGKEY2);
 *     {@link AbstractCommandHandler#addArgument}(COMMAND, ARGKEY3, "defaultValue");
 *   }
 *
 *   public {@link CyCommandResult} execute(String command, Map&lt;String, Object&gt;args) throws {@link CyCommandException} {
 *     // Your execution code goes here....
 *   }
 *   ... or ...
 *   public {@link CyCommandResult} execute(String command, Collection&lt;Tunable&gt;args) throws {@link CyCommandException} {
 *     // Your execution code goes here....
 *   }
 * }
 * </code></pre>
 * Note that both of the two {@link CyCommandHandler#execute} methods must be overridden, but only one of which must include
 * your functionality.  AbstractCommandHandler provides two additional methods to assist command authors with the conversion between
 * the two execute methods.  If your plugin uses {@link Tunable}s, to handle the Map version of the execute method, implement
 * the following method:
 * <code><pre>
 * public class execute(String command, Map<String,Object> arguments) {
 *   return execute(command, createTunableCollection(arguments));
 * } 
 * </pre></code>
 * where the createTunableCollection method will create a list of {@link Tunable}s to call the Tunable version of execute.  On
 * the other hand, if your plugin does not use {@link Tunable}s, you can use:
 * <code><pre>
 * public CyCommandResult execute(String command, Collection<Tunable> arguments) {
 *   return execute(command, createKVMap(arguments));
 * }
 * </pre></code>
 * where the createKVMap method will take a Collection of Tunables and create the corresponding Map to call your execute method.
 *
 * The {@link AbstractCommandHandler#addDescription}() method is used by the AbstractCommandHandler's {@link AbstractCommandHandler#getDescription}()
 * method to produce a formatted description of the command, including the namespace, command name, description, and options 
 * (with default values).  To avoid the formatting and provide your own full description, just override the getDescription() method
 * and return your own description.
 *
 * Also note that the {@link CyCommandNamespace} must be reserved before the command handler is initialized.
 *
 *
 */
public abstract class AbstractCommandHandler implements CyCommandHandler {
	protected Map<String, List<Tunable>> argumentMap = new HashMap();
	protected Map<String, String> descriptionMap = new HashMap();
	protected CyCommandNamespace namespace = null;

	/**
 	 * You do not need to call this constructor from you class, but if
 	 * you don't, you will need to make sure you assign the namespace
 	 * appropriately.
 	 *
 	 * @param ns the {@link CyCommandNamespace} for this command
 	 */
	public AbstractCommandHandler(CyCommandNamespace ns) { namespace = ns; }

	/**
	 * Return the command or commands supported by this handler
	 *
	 * @return a list of strings with one or more commands
	 */
	public List<String> getCommands() { return new ArrayList(argumentMap.keySet()); }

	/**
	 * Return the arguments for a given command
	 *
	 * @param command the command to check for arguments
	 * @return the list of arguments this command will take
	 */
	public List<String> getArguments(String command) { 
		if (!argumentMap.containsKey(command)) {
			return null;
		}

		List<String> argList = new ArrayList();
		for (Tunable t: argumentMap.get(command)) {
			argList.add(t.getName());
		}
		return argList;
	}

	/**
	 * Return the current settings for a given command.  At this point, for simplicity
	 * the only Object type supported as values are those that have "toString()" methods.
	 * This allows for simple mappings between textual input and settings without
	 * resorting to inspection of the object types. This signature is expected to be
	 * removed sometime in the 3.0 timeframe and only the getTunables version will remain.
	 *
	 * @param command the command we want the settings for
	 * @return the current settings as a map
	 */
	public Map<String, Object> getSettings(String command) { 
		Map<String, Object> kvSettings = createKVSettings(command);
		if (kvSettings != null)
			return kvSettings;
		return null;
	}

	/**
	 * Return the current settings for a given command as a map of setting name: tunable.
	 *
	 * @param command the command we want the settings for
	 * @return the current settings as a map of setting name: tunable.
	 */
	public Map<String, Tunable> getTunables(String command) { 
		if (argumentMap.containsKey(command)) {
			Map<String, Tunable> tunableMap = new HashMap();
			for (Tunable t: argumentMap.get(command)) {
				tunableMap.put(t.getName(), t);
			}
			return tunableMap;
		}
		return null;
	}

	/**
 	 * This method returns a formatted description of the command, including
 	 * the namespace, command name, plugin-provided description, and arguments.
 	 * Override this to provide your own description.
 	 *
 	 * @param command the command we're inquiring about
 	 * @return the description/documentation for this command
 	 */
	public String getDescription(String command) { 
		if (descriptionMap.containsKey(command)) {
			return descriptionMap.get(command);
		}
		return null;
	}

	/**
 	 * Execute a given command with a particular set of arguments.  As with the

	/**
 	 * Use this method to support the Tunable version of the execute call if you don't
 	 * support Tunables.  This is actually pretty simple -- just add the following:
 	 * <pre><code>
 	 * public CyCommandResult execute(String command, Collection&lt;Tunable&gt;args) throws CyCommandException {
 	 *   return execute(command, createKVMap(args));
 	 * }
 	 * </code></pre>
 	 * assuming that you've implemented the Map<String,Object> version of execute.  Note that
 	 * you <i>must</i> handle one version of execute at least.
 	 *
 	 * @param tList a Collection of Tunables
 	 * @return a Map of String:Object pairs derived from tList
 	 */
	protected Map<String, Object> createKVMap(Collection<Tunable> tList) {
		Map<String, Object> kvSettings = new HashMap();
		for (Tunable t: tList) {
			Object v = t.getValue();
			if (v != null)
				kvSettings.put(t.getName(), v.toString());
			else
				kvSettings.put(t.getName(), null);
		}
		return kvSettings;
	}

	/**
 	 * Use this method to support the Map version of the execute call if you
 	 * support Tunables.  This is actually pretty simple -- just add the following:
 	 * <pre><code>
 	 * public CyCommandResult execute(String command, Map&lt;String,Object&gt;args) throws CyCommandException {
 	 *   return execute(command, createTunableCollection(args));
 	 * }
 	 * </code></pre>
 	 * assuming that you've implemented the Collection<Tunable> version of execute.  Note that
 	 * you <i>must</i> handle one version of execute at least.
 	 *
 	 * @param args a Map of String,Object pairs
 	 * @return a Collection of Tunables that correspond to the String,Object pairs
 	 */
	protected Collection<Tunable> createTunableCollection(Map<String, Object>args) {
		List<Tunable> tCol = new ArrayList();
		for (String key: args.keySet()) {
			Tunable t = makeTunable(key, args.get(key));
			if (t != null) tCol.add(t);
		}
		return tCol;
	}

	/**
 	 * This method adds a new description for a command supported by this command
 	 * handler.  addDescription, in conjunction with the getDescription method
 	 * provided by this class provide an easy way to get a formatted description,
 	 * including available options.  On the other hand, if a plugin want's to provide
 	 * its own description, without any additional formatting, it should override
 	 * getDescription and return the description directly
 	 * 
 	 * @param command the name of the command to add the description to
 	 * @param description the description to add
 	 */
	protected void addDescription(String command, String description) {
		if (descriptionMap == null)
			descriptionMap = new HashMap();
		descriptionMap.put(command, description);
	}

	/**
 	 * This method adds a new command to the list of command supported by this command
 	 * handler.  This assumes that this command has no arguments.
 	 * 
 	 * @param command the name of the command to add
 	 */
	protected void addArgument(String command) {
		if (argumentMap == null)
			argumentMap = new HashMap();
		if (!argumentMap.containsKey(command)) {
			argumentMap.put(command, new ArrayList());
			CyCommandManager.register(namespace, command, (CyCommandHandler)this);
		} 
	}

	/**
 	 * Add a new argument key to a command supported by this command handler.  If
 	 * the command hasn't already been registered, register it.
 	 *
 	 * @param command the name of the command to add this argument key to
 	 * @param vKey the key to add
 	 */
	protected void addArgument(String command, String vKey) {
		addArgument(command, vKey, null);
	}

	/**
 	 * Add a new argument key with a value to a command supported by this command 
 	 * handler.  If the command hasn't already been registered, register it.
 	 *
 	 * @param command the name of the command to add this argument key to
 	 * @param vKey the key to add
 	 * @param value the value to associate with the key
 	 */
	protected void addArgument(String command, String vKey, String value) {
		Tunable t = new Tunable(vKey, vKey, Tunable.STRING, value);
		addArgument(command, t);
	}

	/**
 	 * Add a new argument Tunable to a command supported by this command
 	 * handler.  If the command hasn't already been registered, register it.
 	 *
 	 * @param command the name of the command to add this argument key to
 	 * @param t the Tunable to add
 	 */
	protected void addArgument(String command, Tunable t) {
		if (argumentMap == null)
			argumentMap = new HashMap();
		
		if (!argumentMap.containsKey(command)) {
			argumentMap.put(command, new ArrayList());
			CyCommandManager.register(namespace, command, this);
		}

		List<Tunable> tList = argumentMap.get(command);
		tList.add(t);
	}

	/**
 	 * Return the string value of a specific argument from an args map.
 	 *
 	 * @param command the command we're getting the argument for
 	 * @param key the argument we're interested in
 	 * @param args the map of arguments that was passed to us
 	 * @return the String value corresponding to the key
 	 */
	protected String getArg(String command, String key, Map<String,Object>args) {
		// Do we have the key in our settings map?
		String value = null;

		if (argumentMap.containsKey(command)) {
			List<Tunable> tL = argumentMap.get(command);
			for (Tunable t: tL) {
				if (t.getName().equals(key)) {
					Object v = t.getValue();
					if (v != null)
						value = v.toString();
					break;
				}
			}
		}

		if (args == null || args.size() == 0 || !args.containsKey(key))
			return value;

		return args.get(key).toString();
	}

	/**
	 * This method is useful for converting from Tunable lists to key-value settings
	 */
	private	Map<String, Object> createKVSettings(String command) {
		if (!argumentMap.containsKey(command)) return null;
		return createKVMap(argumentMap.get(command));
	}

	static public Tunable makeTunable(String name, Object value) {
		Class vClass = value.getClass();
		if (vClass == Double.class || vClass == Float.class) {
			return new Tunable(name, name, Tunable.DOUBLE, value);
		} else if (vClass == Integer.class) {
			return new Tunable(name, name, Tunable.INTEGER, value);
		} else if (vClass == Boolean.class) {
			return new Tunable(name, name, Tunable.BOOLEAN, value);
		} else if (vClass == String.class) {
			return new Tunable(name, name, Tunable.STRING, value);
		} else {
			return new Tunable(name, name, Tunable.STRING, value.toString());
		}
	}

}
