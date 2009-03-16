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
package batchTool.commands;

import java.util.List;
import java.util.HashMap;

/**
 * The layout command handles all requests to layout the current network.
 * For efficiency reasons, this should be done assuming we're in headless
 * mode.
 */
public abstract class AbstractCommand implements Command {
	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public abstract String commandName();

	/**
	 * parse is the main parse routine.  It is handed the entire command
	 * along with all of its arguments.  If the command is successfully
	 * parsed, the number of arguments actually read is returned.
	 *
	 * @param args the arguments to the command
	 */
	public abstract int parse(List<String> args, HashMap<String,String> optMap) throws ParseException;

	/**
	 * execute is the routine called to actually execute the command. 
	 *
	 * @param substitutions reserved for future use
	 */
	public abstract int execute(String[] substitutions) throws Exception;

	/**
	 * commpare compares two strings but looks for potential abbreviations
	 *
	 * @param command the full command name
	 * @param userInput what the user typed
	 * @param minInput the minimum input string length
	 * @return true if there is a match, false otherwise
	 */
	public boolean commpare(String command, String userInput, int minInput) {
		if (userInput.length() < minInput) return false;
		return command.toLowerCase().startsWith(userInput);
	}

}
