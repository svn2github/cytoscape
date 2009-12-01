/* vim: set ts=2: */
/**
 * Copyright (c) 2009 The Regents of the University of California.
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
package coreCommands;

import cytoscape.Cytoscape;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandNamespace;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.CytoscapeInit;
import cytoscape.logger.CyLogger;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;

import coreCommands.commands.*;

public class CoreCommands extends CytoscapePlugin {
	private CyLogger logger = null;

	/**
	 * We don't do much at initialization time
	 */
	public CoreCommands() {
		logger = CyLogger.getLogger(CoreCommands.class);

		// Register our built-ins -- these should really be
		// provided directly by the core...
		try {
			CyCommandNamespace selectNS = CyCommandManager.reserveNamespace("edge");
			CyCommandManager.register(selectNS, new SelectEdgeCommand());
			CyCommandManager.register(selectNS, new DeselectEdgeCommand());
			CyCommandManager.register(selectNS, new GetEdgeAttributeCommand());
			CyCommandManager.register(selectNS, new ImportEdgeAttributesCommand());
			CyCommandManager.register(selectNS, new SetEdgeAttributeCommand());
			CyCommandManager.register(selectNS, new GetSelectedEdgesCommand());

			CyCommandNamespace networkNS = CyCommandManager.reserveNamespace("network");
			CyCommandManager.register(networkNS, new NetworkCreateCommand());
			CyCommandManager.register(networkNS, new NetworkImportCommand());
//			CyCommandManager.register(new NetworkViewCommand());
//			CyCommandManager.register(new PropertyCommand());
//			CyCommandManager.register(new SessionCommand());
//			CyCommandManager.register(new VizMapCommand());

			CyCommandNamespace layoutNS = CyCommandManager.reserveNamespace("layout");
			CyCommandManager.register(layoutNS, new GetDefaultLayoutCommand());
			CyCommandManager.register(layoutNS, new GetCurrentLayoutCommand());
			CyCommandManager.register(layoutNS, new ApplyDefaultLayoutCommand());
			for ( CyLayoutAlgorithm alg : CyLayouts.getAllLayouts() )
				CyCommandManager.register(layoutNS, new ApplyLayoutCommand(alg));

			CyCommandNamespace nodeNS = CyCommandManager.reserveNamespace("node");
			CyCommandManager.register(nodeNS, new NodeCommandCollection("select"));
			CyCommandManager.register(nodeNS, new NodeCommandCollection("deselect"));
			CyCommandManager.register(nodeNS, new NodeCommandCollection("get selected"));
			CyCommandManager.register(nodeNS, new NodeCommandCollection("get attribute"));
			CyCommandManager.register(nodeNS, new NodeCommandCollection("set attribute"));
			CyCommandManager.register(nodeNS, new NodeCommandCollection("import attributes"));

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}
}
