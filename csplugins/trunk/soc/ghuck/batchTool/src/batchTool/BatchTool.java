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
package batchTool;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.CytoscapeInit;
import cytoscape.logger.CyLogger;

import batchTool.commands.*;

/**
 * The batchTool plugin is a proof-of-concept for a tool that will read
 * in a rudimentary script file and execute the contents.  Eventually,
 * this plugin should be replaced by a core scripting facility.
 */
public class BatchTool extends CytoscapePlugin {
	private HashMap<String,Command> commandMap = new HashMap();
	private List<File> scriptList = new ArrayList();
	private boolean initialized = false;
	private CyLogger logger = null;

	public BatchTool() {
		logger = CyLogger.getLogger(BatchTool.class);

		if (!initialized) {
			// Initialize our built-ins
			addCommand(new ApplyCommand());
			addCommand(new DestroyCommand());
			addCommand(new ExitCommand());
			addCommand(new ExportCommand());
			addCommand(new ImportCommand());
			addCommand(new LayoutCommand());
			addCommand(new OpenCommand());
			addCommand(new SaveCommand());
			addCommand(new SetCommand());
			initialized = true;
		}

		// Get the command arguments
		String[] args = CytoscapeInit.getCyInitParams().getArgs();
		/*
		System.out.print("BatchTool: args = ");
		for (int i = 0; i < args.length; i++) {
			System.out.print(args[i]+" ");
		}
		System.out.println();
		*/

		// See if there are any scripts
		for (int arg = 0; arg < args.length; arg++) {
			if (args[arg].equals("-S")) {
				// Yup, put it in our file list
				logger.debug("Opening file "+args[arg+1]);
				File file = new File(args[++arg]);
				if (file == null) {
					// Display an error
					logger.error("Unable to open file "+args[arg]);
				} else {
					scriptList.add(file);
				}
			}
		}

		Iterator<File>fileIter = scriptList.iterator();
		while (fileIter.hasNext()) {
			try {
				processScriptFile(fileIter.next());
			} catch (Exception e) {
				logger.error("Parsing exception: "+e.getMessage());
			}
		}
		// Process each script line
	}

	public void addCommand(Command command) {
		if (command == null) return;
		commandMap.put(command.commandName(), command);
	}

	private void processScriptFile(File file) throws Exception {
		logger.info("Processing script file "+file);
		// Open the file
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = reader.readLine()) != null) {
			// Split
			ArrayList<String>tokens = new ArrayList();
			HashMap<String,String>optMap = new HashMap();
			String command = tokenize(line, tokens, optMap);
			// Execute it
			if (commandMap.containsKey(command)) {
				Command com = commandMap.get(command);
				com.parse(tokens, optMap);
				com.execute(null);
			} else {
				throw new ParseException("Unknown command: "+tokens.get(0));
			}
		}
	}

	private String tokenize(String input, List<String> args, HashMap<String,String> optMap) {
		// Currently, this is just a simple approach.  This eventually needs
		// to handle things like string quoting, etc.
		if (input == null) return null;

		boolean inQuote = false;
		boolean inPair = false;

		String key = null;
		String str = null;
		int tokenStart = 0;

		for (int i = 0; i <= input.length(); i++) {
			char c = '\0';
			if (i < input.length()) c = input.charAt(i);

			switch (c) {

			case '\0':
			case ' ':
				if (c == ' ' && inQuote) continue;
				str = input.substring(tokenStart, i);

				if (inPair) {
					optMap.put(key, str);
					inPair = false;
				} else {
					args.add(str);
				}

				tokenStart = i+1;
				break;

			case '#':
				if (inQuote) continue;
				if (i > tokenStart+1) {
					str = input.substring(tokenStart, i);
					args.add(str);
					i = input.length();
				}
				break;

			case '"':
				if (inQuote) 
					inQuote=false;
				else
					inQuote = true;
				break;

			case '=':
				if (inQuote) continue;
				key = input.substring(tokenStart, i);
				inPair = true;
				tokenStart = i+1;
			}
		}

/*
		Iterator<String> tokenIter = args.iterator();
		while (tokenIter.hasNext()) {
			System.out.println("token: "+tokenIter.next());
		}
*/

		if (args.size() > 0)
			return args.get(0).toLowerCase();

		return null;
	}
}


