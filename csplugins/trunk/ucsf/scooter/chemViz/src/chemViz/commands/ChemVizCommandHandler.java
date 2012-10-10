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
package chemViz.commands;

import java.lang.RuntimeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;


// Cytoscape imports
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.command.AbstractCommandHandler;
import cytoscape.command.CyCommandException;
import cytoscape.command.CyCommandHandler;
import cytoscape.command.CyCommandNamespace;
import cytoscape.command.CyCommandManager;
import cytoscape.command.CyCommandResult;
import cytoscape.data.CyAttributes;
import cytoscape.layout.Tunable;
import cytoscape.task.util.TaskManager;

import giny.model.GraphObject;

// chemViz imports
import chemViz.model.ChemInfoProperties;
import chemViz.model.Compound;
import chemViz.model.Compound.DescriptorType;
import chemViz.ui.ChemInfoSettingsDialog;

enum Command {
	ATTACH("attach",
	       "Attach 2D structures to nodes",
				 "nodelist|node|attribute|smiles"),
	CALCULATE("calculate similarity",
	          "Create a similarity network for the current nodes",
	          "nodelist"),
	CLOSESTRUCTURES("close structures",
	                "Close the 2D structure grid",
	                ""),
	CLOSETABLE("close table",
	           "Close the structure table",
	           ""),
	GETDESC("get descriptors",
	        "Return chemical descriptors for nodes or edges",
	        "attribute|descriptors|edge|edgelist|network=current|node|nodelist|smiles"),
	REMOVE("remove",
	       "Remove 2D structures from nodes",
				 "nodelist|node"),
	SHOWSTRUCTURES("show structures",
	               "Popup the 2D structures for a node/edge or group of nodes/edges",
	               "node|nodelist|edge|edgelist"),
	SHOWTABLE("show table",
	          "Show the structure table for a node/edge or group of nodes/edges",
	          "edge|edgelist|node|nodelist|descriptors|attributes"),
	SETPARAM("set parameter",
	         "Set chemViz parameters",
	         "fingerprinter=CDK|smilesAttributes|inchiAttributes|nodeSize=100|position=Centered|imageLabel");

	private String command = null;
	private String argList = null;
	private String desc = null;

	Command(String command, String description, String argList) {
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
public class ChemVizCommandHandler extends AbstractCommandHandler {
	ChemInfoProperties props;
	static final String ATTRIBUTE = "attribute";
	static final String CURRENT = "current";
	static final String DESCRIPTORS = "descriptors";
	static final String EDGE = "edge";
	static final String EDGELIST = "edgelist";
	static final String NETWORK = "network";
	static final String NODE = "node";
	static final String NODELIST = "nodelist";
	static final String SMILES = "smiles";

	public ChemVizCommandHandler (ChemInfoSettingsDialog settingsDialog) {
		super(CyCommandManager.reserveNamespace("chemViz"));

		props = settingsDialog.getProperties();
	}

	public CyCommandResult execute(String command, Collection<Tunable>args)
                                                      throws CyCommandException, RuntimeException {
		return execute(command, createKVMap(args));
	}

	public CyCommandResult execute(String command, Map<String, Object>args) 
                                                      throws CyCommandException, RuntimeException {
		CyCommandResult result = new CyCommandResult();

		List<String> legalArgs = getArguments(command);
		for (String arg: args.keySet()) {
			if (!legalArgs.contains(arg))
				throw new RuntimeException("chemviz "+command+": unknown argument: "+arg);
		}

		// Pull out common args
		List<GraphObject> gObjList = getGraphObjectList(command, args);

		String smiles = null;
		if (args.containsKey(SMILES))
			smiles = args.get(SMILES).toString();

		String attribute = null;
		if (args.containsKey(ATTRIBUTE))
			attribute = args.get(ATTRIBUTE).toString();

		// Main command cascade

		//	ATTACH("attach",
		//	       "Attach 2D structures to nodes",
		//				 "nodelist|node|attribute|smiles"),
		if (Command.ATTACH.equals(command)) {

		//	CALCULATE("calculate similarity",
		//	          "Create a similarity network for the current nodes",
		//	          "nodelist"),
		} else if (Command.CALCULATE.equals(command)) {

		//	CLOSESTRUCTURES("close structures",
		//	                "Close the 2D structure grid",
		//	                ""),
		} else if (Command.CLOSESTRUCTURES.equals(command)) {

		//	CLOSETABLE("close table",
		//	           "Close the structure table",
		//	           ""),
		} else if (Command.CLOSETABLE.equals(command)) {

		//	GETDESC("get descriptors",
		//	        "Return chemical descriptors for a node",
		//	        "descriptors|node|nodelist|smiles"),
		} else if (Command.GETDESC.equals(command)) {
			if (gObjList != null && smiles != null) 
				throw new RuntimeException("chemviz "+command+": can't have both smiles string and nodes");

			if (!args.containsKey(DESCRIPTORS))
				throw new RuntimeException("chemviz "+command+": descriptor list must be specified");
			List<DescriptorType> descriptors = getDescriptors(command, args.get(DESCRIPTORS).toString());

			List<Compound> compoundList = getCompounds(gObjList, smiles, attribute);
			for (Compound compound: compoundList) {
				for (DescriptorType type: descriptors) {
					result.addResult(compound.toString()+":"+type.getShortName(), compound.getDescriptor(type));
					result.addMessage("Compound "+compound.toString()+" "+type.toString()+" = "+compound.getDescriptor(type).toString());
				}
			}
		
		//	REMOVE("remove",
		//	       "Remove 2D structures from nodes",
		//				 "nodelist|node"),
		} else if (Command.REMOVE.equals(command)) {
		
		//	SHOWSTRUCTURES("show structures",
		//	               "Popup the 2D structures for a node or group of nodes",
		//	               "node|nodelist"),
		} else if (Command.SHOWSTRUCTURES.equals(command)) {
		
		//	SHOWTABLE("show table",
		//	          "Show the structure table for a node or group of nodes",
		//	          "node|nodelist|descriptors|attributes"),
		} else if (Command.SHOWTABLE.equals(command)) {
		}
		
		return result;
	}

	private void addCommand(String command, String description, String argString) {
		// Add the description first
		addDescription(command, description);

		if (argString == null) {
			addArgument(command);
			return;
		}

		// Split up the options
		String[] options = argString.split("\\|");
		for (int opt = 0; opt < options.length; opt++) {
			String[] args = options[opt].split("=");
			if (args.length == 1)
				addArgument(command, args[0]);
			else
				addArgument(command, args[0], args[1]);
		}
	}

	private List<GraphObject> getGraphObjectList(String command, Map<String,Object> args) {
		if (!args.containsKey(NODE) && !args.containsKey(NODELIST) &&
		    !args.containsKey(EDGE) && !args.containsKey(EDGE))
			return null;

		if (args.containsKey(NODE) && args.containsKey(NODELIST))
			throw new RuntimeException("chemviz "+command+": can't have both 'node' and 'nodeList'");

		if (args.containsKey(EDGE) && args.containsKey(EDGELIST))
			throw new RuntimeException("chemviz "+command+": can't have both 'edge' and 'edgeList'");

		CyNetwork network = Cytoscape.getCurrentNetwork();

		if (args.containsKey(NETWORK)) {
			String netName = args.get(NETWORK).toString();
			if (!netName.equals(CURRENT) && Cytoscape.getNetwork(netName) != null)
				network = Cytoscape.getNetwork(netName);
		}

		List<GraphObject> objList = new ArrayList<GraphObject>();
		if (args.containsKey(NODE)) {
			objList.add(getNode(command, args.get(NODE).toString()));
		} else if (args.containsKey(NODELIST)) {
			String nodes = args.get(NODELIST).toString();
			if (nodes == null || nodes.length() == 0) return null;
			String[] nodeArray  = nodes.split(",");
			for (String str: nodeArray)
				objList.add(getNode(command, str.trim()));
		} else if (args.containsKey(EDGE)) {
			objList.add(getEdge(command, args.get(EDGE).toString()));
		} else if (args.containsKey(EDGELIST)) {
			String edges = args.get(EDGELIST).toString();
			if (edges == null || edges.length() == 0) return null;
			String[] edgeArray  = edges.split(",");
			for (String str: edgeArray)
				objList.add(getEdge(command, str.trim()));
		}
		return objList;
	}

	private GraphObject getNode(String command, String nodeID) {
		if (Cytoscape.getCyNode(nodeID, false) != null)
			return (GraphObject)Cytoscape.getCyNode(nodeID, false);

		if (Cytoscape.getCyNode(nodeID) != null)
			return (GraphObject)Cytoscape.getCyNode(nodeID);

		throw new RuntimeException("chemviz "+command+": can't find node '"+nodeID+"'");
	}

	private GraphObject getEdge(String command, String edgeID) {
		throw new RuntimeException("chemviz "+command+": edge support isn't implemented yet");
	}

	private List<DescriptorType> getDescriptors(String command, String desc) {
		if (desc == null || desc.length() == 0) 
			throw new RuntimeException("chemviz "+command+": descriptors list cannot be empty");

		List<DescriptorType> fullList = Compound.getDescriptorList();
		List<DescriptorType> resultList = new ArrayList<DescriptorType>();

		String[] descArray = desc.split(",");
		for (String descriptor: descArray) {
			if (getDescriptor(fullList,descriptor.trim()) == null)
				throw new RuntimeException("chemviz "+command+": descriptor '"+descriptor+"' isn't supported");
			resultList.add(getDescriptor(fullList,descriptor.trim()));
		}
		return resultList;
	}

	private DescriptorType getDescriptor(List<DescriptorType> fullList, String desc) {
		for (DescriptorType type: fullList)
			if (type.getShortName().equals(desc))
				return type;
		return null;
	}

	private List<Compound> getCompounds(List<GraphObject> objList, String smiles, String attribute) {
		return null;
	}

	private void addArguments(String command) {
		if (props == null) {
			addArgument(command);
			return;
		}

		for (Tunable t: props.getTunables()) {
			if (t.getType() == Tunable.BUTTON || t.getType() == Tunable.GROUP)
				continue;

			// Is there a default value for this prop?
			if (t.getValue() != null)
				addArgument(command, t.getName(), t.getValue().toString());
			else
				addArgument(command, t.getName());
		}
	}

	private void setTunables(ChemInfoProperties props, Collection<Tunable>args) throws Exception {
		// Set the Tunables
		for (Tunable t: args) {
			if (props.get(t.getName()) != null) {
				Tunable target = props.get(t.getName());
				Object value = t.getValue();
				try {
					if ((target.getType() == Tunable.LIST) &&
					    (t.getType() == Tunable.STRING)) {
						setListTunable(target, value.toString());
					} else {
						target.setValue(value.toString());
					}
					target.updateValueListeners();
				} catch (Exception e) {
					throw new Exception("Unable to parse value for "+
					                    t.getName()+": "+value.toString());
				}
			}
		}
	}

	private void setListTunable(Tunable listTunable, String value) {
		Object[] optionList = (Object [])listTunable.getLowerBound();
		String[] inputList = value.split(",");
		String v = "";
		Integer first = null;
		for (int i = 0; i < inputList.length; i++) {
			for (int j = 0; j < optionList.length; j++) {
				if (optionList[j].toString().equals(inputList[i])) {
					v = v+","+j;
					if (first == null) first = new Integer(j);
				}
			}
		}
		v = v.substring(1);
		if (listTunable.checkFlag(Tunable.MULTISELECT)) {
			listTunable.setValue(v);
		} else {
			listTunable.setValue(first);
		}
	}

}
