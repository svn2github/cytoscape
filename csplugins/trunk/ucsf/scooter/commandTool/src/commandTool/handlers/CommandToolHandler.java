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
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;

import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;

import cytoscape.layout.Tunable;
import cytoscape.logger.CyLogger;

import commandTool.CommandTool;

public class CommandToolHandler extends AbstractCommandHandler {
	private static String DURATION = "duration";
	private static String FILE = "file";
	private static String MESSAGE = "message";
	private static String PAUSE = "pause";
	private static String RUN = "run";
	private static String SLEEP = "sleep";

	public CommandToolHandler() {
		super(CyCommandManager.reserveNamespace("commandTool"));
		addDescription(RUN, "Run a command script from a file");
		addArgument(RUN,FILE);
		addDescription(SLEEP, "Sleep for a certain number of seconds");
		addArgument(SLEEP,DURATION);
		addDescription(PAUSE, "Pause and wait for user input");
		addArgument(PAUSE, MESSAGE, "Press OK to proceed");
	}

	public CyCommandResult execute(String command, Collection<Tunable>args)
	                         throws CyCommandException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args)
	                         throws CyCommandException {
		CyCommandResult result = new CyCommandResult();
		if (command.equalsIgnoreCase(RUN)) {
			if (!args.containsKey(FILE))
				throw new RuntimeException(RUN+" command requires a 'file' argument");

			File inputFile = null;
			try {
				inputFile = new File(args.get(FILE).toString());
				CommandHandler.handleCommandFile(new FileReader(inputFile), args);
	
				result.addMessage("Completed execution of "+inputFile.toString());
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Unable to open file "+inputFile.toString());
			}
		} else if (command.equalsIgnoreCase(SLEEP)) {
			if (!args.containsKey(DURATION))
				throw new RuntimeException(SLEEP+" command requires a 'duration' argument");
			try {
				double secs = Double.parseDouble(args.get(DURATION).toString());
				result.addMessage("Sleeping for "+secs+" seconds");
				Thread.sleep((long)secs*1000);
				result.addMessage("Slept for "+secs+" seconds");
			} catch (Exception e) {
				result.addError("Sleep failed: "+e.getMessage());
			}
		} else if (command.equalsIgnoreCase(PAUSE)) {
			String message = args.get(MESSAGE).toString();
			if (message == null || message.length() == 0)
				message = "Press OK to proceed";
			// Popup a JOptionPane and wait
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message, 
			                             "Paused", JOptionPane.PLAIN_MESSAGE);
			result.addMessage("Paused...continuing");
		} else 
			throw new RuntimeException("Unknown command: "+command);

		return result;
	}
}
