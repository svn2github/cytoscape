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

import java.util.HashMap;

import cytoscape.layout.CyLayouts;
import cytoscape.layout.Tunable;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.LayoutProperties;

import batchTool.commands.ParseException;

/**
 * The layout command handles all requests to layout the current network.
 * For efficiency reasons, this should be done assuming we're in headless
 * mode.
 */
public class LayoutCommand extends AbstractCommand {
	private String layoutName;
	private	CyLayoutAlgorithm layoutAlgorithm;
	private LayoutProperties propertyList;

	/**
	 * commandName returns the command name.  This is used to build the
	 * hash table of commands to hand to the command parser
	 *
	 * @return name of the command
	 */
	public String commandName() { return "layout"; }

	/**
	 * parse is the main parse routine.  It is handed the entire command
	 * along with all of its arguments.  If the command is successfully
	 * parsed, the number of arguments actually read is returned.
	 *
	 * @param args the arguments to the command.  The "layout" command
	 * takes an initial, mandatory argument, which must be the name of
	 * a registered layout algorithm.  Subsequent arguments can be parameters
	 * to be passed to the layout algorihm of the form of name=value pairs.
	 * For example, to perform a force-directed layout, you would use:
	 *
	 * layout force-directed iterations=100
	 */
	public int parse(String[] args) throws ParseException {
		// Second argument must be a registered layout
		layoutName = args[1];
		System.out.println("Layout type: "+layoutName);
		layoutAlgorithm = CyLayouts.getLayout(layoutName);
		if (layoutAlgorithm == null) {
			throw new ParseException("The layout "+args[1]+" isn't available",
			                      args[0].length()+1);
		}

		propertyList = layoutAlgorithm.getSettings();
		if (propertyList == null)
			return args.length;

		for (int i=2; i < args.length; i++) {
			System.out.println(args[i]);
			// Split the pair
			String[] pair = args[i].split("=");
			Tunable tunable = propertyList.get(pair[0]);
			if (tunable == null) {
				throw new ParseException("No such property: "+pair[0],-1);
			}
			System.out.println("Setting tunable "+pair[0]+" to "+pair[1]);
			tunable.setValue(pair[1]);
			layoutAlgorithm.updateSettings();
		}

		return args.length;
	}

	/**
	 * execute the layout
	 *
	 * @param substitutions reserved for future use
	 */
	public int execute(String[] substitutions) throws Exception {
		// Do the appropriate substitutions (if any)
		System.out.println("LayoutCommand: executing");
		layoutAlgorithm.doLayout();
		return -1;
	}

}


