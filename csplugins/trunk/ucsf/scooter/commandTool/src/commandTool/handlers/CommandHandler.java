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
package commandTool.handlers;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StreamTokenizer;
import java.io.StringReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.logger.CyLogger;

import commandTool.CommandTool;

public class CommandHandler {

	// TODO: use args to do variable substitution
	public static void handleCommandFile(File inputFile, Map<String,Object>args) {
		try {
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				String line = null;
				while ((line = input.readLine()) != null) {
					line = line.trim();
					handleCommand(CommandTool.getMessageHandlerContext(), line);
				}
			} 
			finally { input.close(); }
		} catch (Exception e) {
			CommandTool.getMessageHandlerContext().appendError(e.getMessage());
		}
	}

	public static void handleCommand(MessageHandler resultsText, String input) {
		CyCommandResult results = null;

		try {
			// Handle our built-ins
			if (input.startsWith("help")) {
				results = HelpHandler.getHelpReturn(input);
			} else {
				String ns = null;
	
				if ((ns = isNamespace(input)) != null) {
					results = handleCommand(input, ns);
				} else {
					throw new RuntimeException("Unknown command: "+input);
				}
			}
			// Get all of the error messages from our results
			for (String s: results.getErrors()) {
				resultsText.appendError("  "+s);
			}

			// Get all of the messages from our results
			for (String s: results.getMessages()) {
				resultsText.appendMessage("  "+s);
			}
		} catch (CyCommandException e) {
			resultsText.appendError("  "+e.getMessage());
		} catch (RuntimeException e) {
			resultsText.appendError("  "+e.getMessage());
		}
		resultsText.appendMessage("");
	}

	private static String isNamespace(String input) {
		String namespace = null;
		for (String ns: CyCommandManager.getNamespaceList()) {
			if (input.toLowerCase().startsWith(ns.toLowerCase()) && 
			    (namespace == null || ns.length() > namespace.length()))
				namespace = ns;
		}
		return namespace;
	}

	private static CyCommandResult handleCommand(String inputLine, String ns) 
	                                 throws CyCommandException {
		String sub = null;

		// Parse the input, breaking up the tokens into appropriate
		// commands, subcommands, and maps
		Map<String,Object> settings = new HashMap();
		String comm = parseInput(inputLine.substring(ns.length()).trim(), settings);

		for (String command: CyCommandManager.getCommandList(ns)) {
			if (command.toLowerCase().equals(comm.toLowerCase())) {
				sub = command;
				break;
			}
		}

		if (sub == null && (comm != null && comm.length() > 0))
			throw new CyCommandException("Unknown argument: "+comm);
		
		return CyCommandManager.execute(ns, sub, settings);
	}

	private static String parseInput(String input, Map<String,Object> settings) {

		// Tokenize
		StringReader reader = new StringReader(input);
		StreamTokenizer st = new StreamTokenizer(reader);

		// We don't really want to parse numbers as numbers...
		st.ordinaryChar('/');
		st.ordinaryChar('-');
		st.ordinaryChar('.');
		st.ordinaryChars('0', '9');

		st.wordChars('/', '/');
		st.wordChars('-', '-');
		st.wordChars('.', '.');
		st.wordChars('0', '9');

		List<String> tokenList = new ArrayList();
		int tokenIndex = 0;
		int i;
		try {
			while ((i = st.nextToken()) != StreamTokenizer.TT_EOF) {
				switch(i) {
					case '=':
						// Get the next token
						i = st.nextToken();
						if (i == StreamTokenizer.TT_WORD || i == '"') {
							tokenIndex--;
							String key = tokenList.get(tokenIndex);
							settings.put(key, st.sval);
							tokenList.remove(tokenIndex);
						}
						break;
					case '"':
					case StreamTokenizer.TT_WORD:
						tokenList.add(st.sval);
						tokenIndex++;
						break;
					default:
						break;
				}
			} 
		} catch (Exception e) { return ""; }

		// Concatenate the commands together
		String command = "";
		for (String word: tokenList) command += word+" ";

		// Now, the last token of the args goes with the first setting
		return command.trim();
	}
}
