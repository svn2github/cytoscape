/* vim: set ts=2: */
/**
 * Copyright (c) 2010 The Regents of the University of California.
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
package commandTool.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.layout.Tunable;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;

class HelpHandler {

	static public CyCommandResult getHelpReturn(String inputLine) throws CyCommandException {
		List<String> namespaces = CyCommandManager.getNamespaceList();
		String words[] = inputLine.split(" ");
		CyCommandResult comRet = new CyCommandResult();

		// Anything besides help?
		if (words.length <= 1)
			// No, just return a list of the namespaces
			return namespaceList(comRet, namespaces);

		int start = 1;
		String namespace = findKeywords(words, start, namespaces);
		if (namespace == null) {
			throw new CyCommandException("No such command: "+combineWords(words, start, words.length-1));
		}

		start += namespace.split(" ").length;

		List<String> comList = CyCommandManager.getCommandList(namespace);
		if (words.length <= start) {
			return commandList(comRet, namespace, comList);
		}

		// OK, now see if we have a command...
		String command = findKeywords(words, start, comList);
		if (command == null) {
			throw new CyCommandException("No such command: "+combineWords(words, 1, words.length-1));
		}

		// We do -- so provide full information
		CyCommandHandler comHandler = CyCommandManager.getCommand(namespace, command);
		String desc = comHandler.getDescription(command);
		Map<String, Tunable> argumentMap = comHandler.getTunables(command);
		if (argumentMap == null) {
			argumentMap = makeTunableMap(comHandler.getSettings(command));
		}

		List<Tunable> args = new ArrayList();
		for (String key: argumentMap.keySet()) { 
			args.add(argumentMap.get(key)); 
		}

		return formatDescription(comRet, namespace, command, desc, args);
	}

	static private CyCommandResult commandList(CyCommandResult comRet, String namespace, List<String> commands) {
		comRet.addMessage("Available "+namespace+" commands: ");
		addList(comRet, namespace, commands);
		return comRet;
	}

	static private CyCommandResult namespaceList(CyCommandResult comRet, List<String>nsList) {
		comRet.addMessage("Available commands: ");
		addList(comRet, "", nsList);
		comRet.addMessage("For detailed information type: ");
		comRet.addMessage(" help command");
		return comRet;
	}

	static private void addList(CyCommandResult comRet, String prefix, List<String>list) {
		for (String str: list) {
			comRet.addMessage("  "+prefix+" "+str);
		}
	}

	static private String findKeywords(String[] words, int start, List<String>keywords) {
		for (String kw: keywords) {
			for (int count = words.length; count >= start; count--) {
				if (kw.equals(combineWords(words, start, count))) {
					return kw;
				}
			}
		}
		return null;
	}

	static private String combineWords(String[] words, int start, int count) {
		String combine = "";
		for (int i = start; i < count; i++) {
			combine += words[i] + " ";
		}
		return combine.trim();
	}

	/**
 	 * This method can be used to create a formatted description that includes
 	 * the namespace, command, and options.
 	 *
 	 * @param command the command 
 	 * @param description the textual description
 	 * @param args the list of arguments for this command
 	 * @return the formatted text
 	 */
	static private CyCommandResult formatDescription(CyCommandResult ret, String namespace, 
	                                      String command, String description, Collection<Tunable>args) {
		ret.addMessage(namespace+" "+command+": "+description);
		if (args == null || args.size() == 0) return ret;
		ret.addMessage("  Arguments:");
		for (Tunable arg: args) {
		  String descr = "    [";
			if (arg.getName() != null)
				descr += arg.getName();
			if (arg.getDescription() != null && !arg.getDescription().equals(arg.getName()))
				descr += "("+arg.getDescription()+")";
			if (arg.getValue() != null)
				descr += "="+arg.getValue();
			else
				descr += "=value";
			descr += "]";
			ret.addMessage(descr);
		}
		return ret;
	}

	static private Map<String, Tunable> makeTunableMap(Map<String, Object> args) {
		Map<String, Tunable> map = new HashMap();
		for (String key: args.keySet()) {
			Tunable t = makeTunable(key, args.get(key));
			if (t != null) map.put(key, t);
		}
		return map;
	}

  static private Tunable makeTunable(String name, Object value) {
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
