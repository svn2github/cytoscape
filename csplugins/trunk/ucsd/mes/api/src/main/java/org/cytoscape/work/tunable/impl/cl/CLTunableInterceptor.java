
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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

package org.cytoscape.work.tunable.impl.cl;

import org.apache.commons.cli.*;

import org.cytoscape.work.tunable.*;

import java.lang.annotation.*;
import java.lang.reflect.*;

import java.util.*;


/**
 * This would presumably be a Service.
 */
public class CLTunableInterceptor extends AbstractTunableInterceptor<CLHandler> {
	private String[] args;

	/**
	 * Creates a new CLTunableInterceptor object.
	 *
	 * @param args  DOCUMENT ME!
	 */
	public CLTunableInterceptor(String[] args) {
		super(new CLHandlerFactory());
		this.args = (String[]) args.clone();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param lh DOCUMENT ME!
	 */
	protected void process(List<CLHandler> lh) {
		Options options = new Options();

		for (CLHandler h : lh)
			options.addOption(h.getOption());

		options.addOption("h", "help", false, "Print this message.");

		// try to parse the cmd line
		CommandLineParser parser = new PosixParser();
		CommandLine line = null;

		try {
			line = parser.parse(options, args);
		} catch (ParseException e) {
			System.err.println("Parsing command line failed: " + e.getMessage());
			printHelp(options);
			System.exit(1);
		}

		// use what is found on the command line to set values
		if (line.hasOption("h")) {
			printHelp(options);
			System.exit(0);
		}

		for (CLHandler h : lh)
			h.handleLine(line);
	}

	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -Xmx512M -jar cytoscape.jar [OPTIONS]", options);
	}
}
