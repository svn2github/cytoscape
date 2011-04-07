/* vim: set ts=2: */
/**
 * Copyright (c) 2008 The Regents of the University of California.
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
package clusterMaker.commands;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

// Cytoscape imports
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;

// clusterMaker imports
import clusterMaker.ui.ClusterViz;
import clusterMaker.algorithms.ClusterProperties;


/**
 * Inner class to handle CyCommands
 */
public class VizCommandHandler extends ClusterMakerCommandHandler {
	Map<String, ClusterViz> vizMap;

	public VizCommandHandler (Map<String, ClusterViz>vizMap) {
		super("clusterviz");

		this.vizMap = vizMap;

		// For each visualization, add the visualization and the tunables
		for (String vizName: vizMap.keySet()) {
			ClusterViz viz = vizMap.get(vizName);
			ClusterProperties props = viz.getSettings();
			addDescription(vizName.toLowerCase(), vizName+" cluster visualizer");
			addArguments(vizName.toLowerCase(), props);
		}

	}

	public CyCommandResult execute(String command, Map<String, Object>args) 
                                                      throws CyCommandException {
		return execute(command, createTunableCollection(args));
	}

	public CyCommandResult execute(String command, Collection<Tunable>args)
                                                      throws CyCommandException {
		CyCommandResult result = new CyCommandResult();
		for (String vizname: vizMap.keySet()) {
			if (command.equalsIgnoreCase(vizname)) {
				// Get the algorithm
				ClusterViz viz = vizMap.get(vizname);
				ClusterProperties props = viz.getSettings();
	
				viz.updateSettings(true);

				try {
					setTunables(props, args);
					viz.startViz();
				} catch (Exception e) {
					result.addError(e.getMessage());
				}
				return result;
			}
			//??
		}

		result.addError("clusterviz: unknown visualizer "+command);
		return result;
	}
}
