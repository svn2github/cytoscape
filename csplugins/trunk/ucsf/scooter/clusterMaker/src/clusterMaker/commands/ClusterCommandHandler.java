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
package clusterMaker.commands;

import java.lang.RuntimeException;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


// Cytoscape imports
import cytoscape.CyNode;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.layout.Tunable;
import cytoscape.task.util.TaskManager;

// clusterMaker imports
import clusterMaker.ui.ClusterTask;
import clusterMaker.algorithms.AbstractNetworkClusterer;
import clusterMaker.algorithms.ClusterAlgorithm;
import clusterMaker.algorithms.ClusterProperties;

enum BuiltIn {
	HASCLUSTER("hasCluster", "Test to see if this network has a cluster of the requested type", "type"),
	GETCLUSTER("getCluster", "Get a cluster of the requested type and the requested clustertype (node or attribute)",
             "type|clustertype=node"),
	GETHCLUSTER("getHierarchy", "Get a hierarchy of the requested clustertype (node or attribute)", "clustertype=node");

	private String command = null;
	private String argList = null;
	private String desc = null;

	BuiltIn(String command, String description, String argList) {
		this.command = command;
		this.argList = argList;
		this.desc = description;
	}

	public String getCommand() { return command; }
	public String getArgString() { return argList; }
	public String getDescription() { return desc; }
	public boolean equals(String com) { return command.equals(com); }
}

/**
 * Inner class to handle CyCommands
 */
public class ClusterCommandHandler extends ClusterMakerCommandHandler {
	Map<String, ClusterAlgorithm> algMap;

	public ClusterCommandHandler (Map<String, ClusterAlgorithm>algMap) {
		super("clusterMaker");

		this.algMap = algMap;

		// Now, for each algorithm, add the algorithm and the tunables for that algorithm
		for (String algName: algMap.keySet()) {
			ClusterAlgorithm alg = algMap.get(algName);
			ClusterProperties props = alg.getSettings();
			addDescription(algName, algName+" cluster algorithm");
			addArguments(algName, props);
		}

		// Finally, add our built-in commands
		for (BuiltIn command: BuiltIn.values()) {
			addCommand(command.getCommand(), command.getDescription(), command.getArgString());
		}
	}

	public CyCommandResult execute(String command, Map<String, Object>args) 
                                                      throws CyCommandException, RuntimeException {
		return execute(command, createTunableCollection(args));
	}

	public CyCommandResult execute(String command, Collection<Tunable>args)
                                                      throws CyCommandException, RuntimeException {
		CyCommandResult result = new CyCommandResult();

		if (BuiltIn.HASCLUSTER.equals(command)) {
			ClusterAlgorithm alg = getAlgorithm(args, command);
			// See if it's available
			boolean avail = alg.isAvailable();
			if (avail)
				result.addMessage("Cluster results for '"+alg+"' are available");
			else
				result.addMessage("Cluster results for '"+alg+"' are not available");
			result.addResult(new Boolean(avail));
		} else if (BuiltIn.GETCLUSTER.equals(command)) {
			return getNodeClusters(args,command);
		} else if (BuiltIn.GETHCLUSTER.equals(command)) {
			// Get the algorithm
			ClusterAlgorithm alg = getAlgorithm(args, command);

			// Get the cluster we want
			Tunable t = getTunable(args, "clustertype");
			if (t.getValue() == "node") {
				return getGeneHierarchy();
			} else if (t.getValue() == "attribute") {
				return getAttributeHierarchy();
			}

		} else if (algMap.containsKey(command)) {
			// Get the algorithm
			ClusterAlgorithm alg = algMap.get(command);
			alg.initializeProperties();
			ClusterProperties props = alg.getSettings();

			alg.updateSettings(true);

			try {
				setTunables(props, args);
			} catch (Exception e) {
				result.addError(e.getMessage());
				return result;
			}

			alg.updateSettings();

			ClusterTask clusterTask = new ClusterTask(alg, null);
			TaskManager.executeTask( clusterTask,
			                         ClusterTask.getDefaultTaskConfig() );
			// Now, wait until cluster is complete.
			while (!clusterTask.done()) {
				Thread.yield();
				try {
					Thread.sleep(100);
				} catch (Exception e) {}
			}
			result.addMessage("Clustering complete");
		} else {
			// Throw
			throw new RuntimeException("clusterMaker has no "+command+" command");
		}
		return result;
	}

	private void addCommand(String command, String description, String argString) {
		if (argString == null) {
			addArgument(command);
			return;
		}

		// Split up the options
		String[] options = argString.split("|");
		for (int opt = 0; opt < options.length; opt++) {
			String[] args = options[opt].split("=");
			if (args.length == 1)
				addArgument(command, args[0]);
			else
				addArgument(command, args[0], args[1]);
		}
	}

	private ClusterAlgorithm getAlgorithm(Collection<Tunable> args, String command) {
		// The the algorithm type
		Tunable t = getTunable(args, "type");
		if (t == null)
			throw new RuntimeException("Must supply a 'type' argument for "+command);

		// Get the name of the algorithm
		ClusterAlgorithm alg = algMap.get(t.getValue());
		if (alg == null)
			throw new RuntimeException("Don't know about algorithm '"+t.getValue()+"'");

		return alg;
	}

	private CyCommandResult getNodeClusters(Collection<Tunable> args, String command ) {
		CyCommandResult result = new CyCommandResult();
		ClusterAlgorithm alg = getAlgorithm(args, command);
		if (alg instanceof AbstractNetworkClusterer) {

			// Get the clusters from this algorithm
			if (alg.isAvailable()) {
				List<List<CyNode>> clusterList = ((AbstractNetworkClusterer)alg).getNodeClusters();
				if (clusterList == null) {
					result.addError("Either no clusters exist for this network or cluster algorithm doesn't support node clusters");
				}
				result.addResult("nodeClusters", clusterList);
				result.addMessage("Node clusters: ");
				for (List<CyNode> nodeList: clusterList) {
					String out = "   [";
					for (CyNode node: nodeList) {
						out += node.getIdentifier()+",";
					}
					result.addMessage(out.substring(0, out.length()-1)+"]");
				}
			} else {
				result.addError("Cluster results for '"+alg+"' are not available");
			}
		} else {
			result.addError("Cluster algorithm '"+alg+"' is not a node clusterer");
		}
		return result;
	}

	private CyCommandResult getGeneHierarchy() {
		CyCommandResult result = new CyCommandResult();
		return result;
	}

	private CyCommandResult getAttributeHierarchy() {
		CyCommandResult result = new CyCommandResult();
		return result;
	}
}
