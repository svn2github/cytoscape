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
import cytoscape.CyEdge;
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
import cytoscape.data.Semantics;
import cytoscape.layout.Tunable;
import cytoscape.task.util.TaskManager;

import giny.model.GraphObject;

// chemViz imports
import chemViz.model.ChemInfoProperties;
import chemViz.model.Compound;
import chemViz.model.Compound.AttriType;
import chemViz.model.Compound.DescriptorType;

/**
 * Inner class to handle CyCommands
 */
public class ValueUtils {
	static final String ALL = "all";
	static final String ATTRIBUTE = "attribute";
	static final String CURRENT = "current";
	static final String DESCRIPTORS = "descriptors";
	static final String EDGE = "edge";
	static final String EDGELIST = "edgelist";
	static final String NETWORK = "network";
	static final String NODE = "node";
	static final String NODELIST = "nodelist";
	static final String SELECTED = "selected";
	static final String SMILES = "smiles";

	static public List<GraphObject> getGraphObjectList(String command, Map<String,Object> args) {
		if (!args.containsKey(NODE) && !args.containsKey(NODELIST) &&
		    !args.containsKey(EDGE) && !args.containsKey(EDGE))
			return null;

		if (args.containsKey(NODE) && args.containsKey(NODELIST))
			throw new RuntimeException("chemviz "+command+": can't have both 'node' and 'nodeList'");

		if (args.containsKey(EDGE) && args.containsKey(EDGELIST))
			throw new RuntimeException("chemviz "+command+": can't have both 'edge' and 'edgeList'");

		CyNetwork network = getNetwork(args);

		// OK, nodes or edges?
		if (args.containsKey(NODE) || args.containsKey(NODELIST))
			return getNodeList(command, args, network);
		else 
			return getEdgeList(command, args, network);
	}

	static public CyNetwork getNetwork(Map<String,Object> args) {
		// Get the network
		CyNetwork network = Cytoscape.getCurrentNetwork();
		if (args.containsKey(NETWORK)) {
			String netName = args.get(NETWORK).toString();
			if (!netName.equals(CURRENT) && Cytoscape.getNetwork(netName) != null)
				network = Cytoscape.getNetwork(netName);
		}
		return network;
	}

	static public List<GraphObject> getNodeList(String command, Map<String,Object> args, CyNetwork network) {
		List<GraphObject> objList = new ArrayList<GraphObject>();
		if (args.containsKey(NODE)) {
			objList.add(getNode(command, args.get(NODE).toString()));
		} else if (args.containsKey(NODELIST)) {
			String nodes = args.get(NODELIST).toString();
			if (nodes == null || nodes.length() == 0) return null;
			// Special case for "selected" nodes
			if (nodes.equals(SELECTED)) {
				objList.addAll(network.getSelectedNodes());
			} else if (nodes.equals(ALL)) {
				objList.addAll(network.nodesList());
			} else {
				String[] nodeArray  = nodes.split(",");
				for (String str: nodeArray)
					objList.add(getNode(command, str.trim()));
			}
		}
		return objList;
	}

	static public List<GraphObject> getEdgeList(String command, Map<String,Object> args, CyNetwork network) {
		List<GraphObject> objList = new ArrayList<GraphObject>();
		if (args.containsKey(EDGE)) {
			objList.add(getEdge(command, args.get(EDGE).toString()));
		} else if (args.containsKey(EDGELIST)) {
			String edges = args.get(EDGELIST).toString();
			if (edges == null || edges.length() == 0) return null;
			// Special case for "selected" nodes
			if (edges.equals(SELECTED)) {
				objList.addAll(network.getSelectedEdges());
			} else if (edges.equals(ALL)) {
				objList.addAll(network.edgesList());
			} else {
				String[] edgeArray  = edges.split(",");
				for (String str: edgeArray)
					objList.add(getEdge(command, str.trim()));
			}
		}
		return objList;
	}

	static public GraphObject getNode(String command, String nodeID) {
		if (Cytoscape.getCyNode(nodeID, false) != null)
			return (GraphObject)Cytoscape.getCyNode(nodeID, false);

		if (Cytoscape.getCyNode(nodeID) != null)
			return (GraphObject)Cytoscape.getCyNode(nodeID);

		throw new RuntimeException("chemviz "+command+": can't find node '"+nodeID+"'");
	}


	static public GraphObject getEdge(String command, String edgeID) {
		CyEdge edge = null;
		String comp[] = edgeID.split("[()]");
		CyNode source = Cytoscape.getCyNode(comp[0].trim(), false);
		CyNode target = Cytoscape.getCyNode(comp[2].trim(), false);
		if (source != null && target != null) {
			edge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, comp[1].trim(), false);
		}
		if (edge == null)
			throw new RuntimeException("chemviz "+command+": can't find edge '"+edgeID+"'");
		return edge;
	}

	static public List<DescriptorType> getDescriptors(String command, String desc) {
		if (desc == null || desc.length() == 0) 
			throw new RuntimeException("chemviz "+command+": descriptors list cannot be empty");

		List<DescriptorType> fullList = Compound.getDescriptorList();
		List<DescriptorType> resultList = new ArrayList<DescriptorType>();

		if (desc.trim().equals(ALL)) {
			return fullList;
		}

		String[] descArray = desc.split(",");
		for (String descriptor: descArray) {
			if (getDescriptor(fullList,descriptor.trim()) == null)
				throw new RuntimeException("chemviz "+command+": descriptor '"+descriptor+"' isn't supported");
			resultList.add(getDescriptor(fullList,descriptor.trim()));
		}
		return resultList;
	}

	static public DescriptorType getDescriptor(List<DescriptorType> fullList, String desc) {
		for (DescriptorType type: fullList)
			if (type.getShortName().equals(desc))
				return type;
		return null;
	}

	static public List<Compound> getCompounds(List<GraphObject> objList, String mstring, AttriType type, 
	                                          List<String> sList, List<String> iList) {
		List<Compound> compoundList = new ArrayList<Compound>();

		// Handle special case of a bare smiles string
		if (mstring != null) {
			if (objList == null || objList.size() == 0) {
				Compound c = new Compound(null, null, mstring, type, false);
				compoundList.add(c);
			} else {
				for (GraphObject obj: objList) {
					if (obj instanceof CyNode)
						compoundList.add(new Compound(obj, null, mstring, type, false));
					else
						compoundList.add(new Compound(obj, null, mstring, type, false));
				}
			}
			return compoundList;
		}

		for (GraphObject obj: objList) {
			if (obj instanceof CyNode)
				compoundList.addAll(getCompounds(obj, Cytoscape.getNodeAttributes(), sList, iList, false));
			else
				compoundList.addAll(getCompounds(obj, Cytoscape.getEdgeAttributes(), sList, iList, false));
		}

		return compoundList;
	}

	/**
 	 * Returns all of the Compounds for a single graph object (Node or Edge) based on the SMILES
 	 * and InChI attributes.
 	 *
 	 * @param go the graph object we're looking at
 	 * @param attributes the appropriate set of attributes (nodeAttributes or edgeAttributes)
 	 * @param sList the list of attributes that contain SMILES strings
 	 * @param iList the list of attributes that contain InChI strings
 	 * @param noStructures if 'true', the structures are fetched in the background
 	 * @return the list of compounds.  If the compounds have not already been created, they are created
 	 *         as a byproduct of this method.
 	 */
	public static List<Compound> getCompounds(GraphObject go, CyAttributes attributes, 
	                                          List<String> sList, List<String> iList, 
	                                          boolean noStructures) {
		if ((sList == null || sList.size() == 0) 
		    && (iList == null || iList.size() == 0))
			return null;
		
		List<Compound> cList = new ArrayList();

		// Get the compound list from each attribute
		for (String attr: sList) {
			cList.addAll(getCompounds(go, attributes, attr, AttriType.smiles, noStructures));
		}

		for (String attr: iList) {
			cList.addAll(getCompounds(go, attributes, attr, AttriType.inchi, noStructures));
		}

		return cList;
	}

	/**
 	 * Returns all of the Compounds for a single graph object (Node or Edge) based on the designated
 	 * attribute of the specific type
 	 *
 	 * @param go the graph object we're looking at
 	 * @param attributes the appropriate set of attributes (nodeAttributes or edgeAttributes)
 	 * @param attr the attribute that contains the compound descriptor
 	 * @param type the type of the attribute (smiles or inchi)
 	 * @param noStructures if 'true', the structures are fetched in the background
 	 * @return the list of compounds.  If the compounds have not already been created, they are created
 	 *         as a byproduct of this method.
 	 */
	public static List<Compound> getCompounds(GraphObject go, CyAttributes attributes, 
	                                          String attr, AttriType type,
	                                          boolean noStructures) {
		byte atype = attributes.getType(attr);
		List<Compound> cList = new ArrayList();
			
		if (!attributes.hasAttribute(go.getIdentifier(), attr)) 
			return cList;
		if (atype == CyAttributes.TYPE_STRING) {
			String cstring = attributes.getStringAttribute(go.getIdentifier(), attr);
			cList.addAll(getCompounds(go, attr, cstring, type, noStructures));
		} else if (atype == CyAttributes.TYPE_SIMPLE_LIST) {
			List<String> stringList = attributes.getListAttribute(go.getIdentifier(), attr);
			for (String cstring: stringList) {
				cList.addAll(getCompounds(go, attr, cstring, type, noStructures));
			}
		}
		return cList;
	}

	public static List<Compound> getCompounds(GraphObject go, String attr, 
	                                          String compoundString, AttriType type,
	                                          boolean noStructures) {
		List<Compound> cList = new ArrayList();

		String[] cstrings = null;

		if (type == AttriType.smiles) {
			cstrings = compoundString.split(",");
		} else {
			cstrings = new String[1];
			cstrings[0] = compoundString;
		}

		for (int i = 0; i < cstrings.length; i++) {

			Compound c = Compound.getCompound(go, attr, cstrings[i], type);
			if (c == null)
				c = new Compound(go, attr, cstrings[i], type, noStructures);

			cList.add(c);
				return cList;
		}

		return cList;
	}

	static public void setTunables(ChemInfoProperties props, Collection<Tunable>args) throws Exception {
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

	static public void setListTunable(Tunable listTunable, String value) {
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
