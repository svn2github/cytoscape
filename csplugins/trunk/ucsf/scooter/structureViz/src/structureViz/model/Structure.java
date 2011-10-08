/* vim: set ts=2: */
/**
 * Copyright (c) 2006 The Regents of the University of California.
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
package structureViz.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cytoscape.CyNode;
import giny.model.GraphObject;

import structureViz.model.ChimeraResidue;

/**
 * The Structure class provides a link between the Chimera data
 * and the Cytoscape data.
 */
public class Structure {
	static int nextModel = 0;
	static Map<String, Structure> structureMap = new HashMap<String, Structure>();
	String structureName;
	Map<GraphObject, List<String>> residueMap;
	Map<GraphObject, GraphObject> graphObjectMap;
	int modelNumber;
	int subModelNumber;
	StructureType type;

	public enum StructureType {PDB_MODEL, MODBASE_MODEL, SMILES};

	/**
	 * Get the next available model number
	 *
	 * TODO: This really should be a table of models based
	 * on what the Chimera module tells us.
	 *
	 * @return the next model number
	 */
	public static int getNextModel() {return nextModel++;}

	public static void closeAll() {
		nextModel = 0;
		structureMap.clear();
	}

	public static void close(Structure structure) {
		if (structureMap.containsKey(structure.name()))
			structureMap.remove(structure.name());

		if (structureMap.size() == 0) nextModel = 0;
	}

	public static Structure getStructure(String name, CyNode node, 
	                                     StructureType type) {
		// System.out.println("Getting structure for "+name);
		if (structureMap.containsKey(name)) {
			Structure s = structureMap.get(name);
			// System.out.println("Already have it: "+s.toString());
			s.addGraphObject(node);
			return s;
		}
		return new Structure(name, node, type);
	}

	public static Structure getStructure(String name) {
		if (structureMap.containsKey(name))
			return structureMap.get(name);

		return null;
	}

	/**
 	 * Create a new Structure
	 *
	 * @param name the name of the structure
	 * @param node the CyNode that this structure points to
	 */
	protected Structure (String name, GraphObject node, StructureType type) {
		this(name, new HashMap<GraphObject, GraphObject>(), type);
		if (node != null)
			this.setNode(node);
	}

	protected Structure (String name, Map<GraphObject, GraphObject> goMap, 
	                     StructureType type) {
		this.structureName = name;
		this.modelNumber = nextModel;
		this.subModelNumber = 0;
		this.residueMap = new HashMap<GraphObject, List<String>>();
		this.type = type;
		if (goMap != null)
			this.graphObjectMap = goMap;
		else
			this.graphObjectMap = new HashMap<GraphObject, GraphObject>();
		structureMap.put(name,this);
	}

	public Structure makeSubModel(int subModelNumber) {
		Structure st = new Structure(structureName, graphObjectMap, type);
		st.setModelNumber(modelNumber, subModelNumber);
		return st;
	}

	/**
	 * Get the name of the structure
	 *
	 * @return the name of the structure as a String
	 */
	public String name() {return this.structureName;}

	/**
	 * Get the CyNode this structure is associated with
	 *
	 * @return the CyNode this structure is an attribute of
	 */
	public CyNode node() {
		for (GraphObject obj: graphObjectMap.keySet()) {
			if (obj instanceof CyNode)
				return (CyNode)obj;
		}
		return null;
	}

	/**
	 * Set the CyNode this structure is associated with
	 *
	 * @param node the CyNode this structure is an attribute of
	 */
	public void setNode(GraphObject node) {
		graphObjectMap.put(node,node);
	}

	/**
 	 * Add a CyNode or CyEdge to the list of graph objects this structure is associated with
 	 *
	 * @param node the GraphObject this structure is an attribute of
	 */
	public void addGraphObject(GraphObject go) {
		// System.out.println("Adding "+go.getIdentifier()+" to "+this.toString());
		if (!graphObjectMap.containsKey(go))
			graphObjectMap.put(go, go);
	}

	/**
 	 * Add the Objects and residues from a Structure to this Structure
 	 *
 	 * @param structure the structure we're adding
 	 */
	public void addStructure(Structure structure) {
		// Add any graph objects...
		for (GraphObject obj: structure.getGraphObjectList()) {
			if (!graphObjectMap.containsKey(obj)) {
				// New graph object -- add the object and it's residues
				graphObjectMap.put(obj, obj);
				residueMap.put(obj, structure.getResidueList(obj));
			}
		}
	}

	/**
 	 * Remove a Graph Object from the list of objects this structure is associated with
 	 *
	 * @param node the Graph Object to remove from this structure's list
	 */
	public void removeGraphObject(GraphObject go) {
		if (graphObjectMap.containsKey(go))
			graphObjectMap.remove(go);
	}

	/**
 	 * Get the list of GraphObjects for this structure
 	 *
	 * @return objList the GraphObjects this structure is an attribute of
	 */
	public List<GraphObject> getGraphObjectList() {
		return new ArrayList<GraphObject>(graphObjectMap.values());
	}

	/**
 	 * Get the list of GraphObjects for this structure that match a list
 	 * of residues.
 	 *
 	 * @param residueList the list of residues we're matching
	 * @return objList the GraphObjects this structure is an attribute of
	 */
	public List<GraphObject> getGraphObjectList(List<ChimeraResidue> residueList) {
		List<GraphObject>goList = new ArrayList<GraphObject>();
		if (residueList == null || residueList.size() == 0)
			return goList;

		for (GraphObject obj: graphObjectMap.values()) {
			if (residuesMatch(obj, residueList)) {
				// System.out.println("Found match for "+obj.getIdentifier());
				goList.add(obj);
			}
		}
		return goList;
	}

	/**
	 * Get the modelNumber for this structure
	 *
	 * @return the model number as an int
	 */
	public int modelNumber() {return this.modelNumber;}

	/**
	 * Get the subModelNumber for this structure
	 *
	 * @return the subModel number as an int
	 */
	public int subModelNumber() {return this.subModelNumber;}

	/**
	 * Set the modelNumber for this structure
	 *
	 * @param number the model number
	 */
	public void setModelNumber (int number, int subNumber) {
		this.modelNumber = number;
		this.subModelNumber = subNumber;
		if (this.modelNumber >= nextModel) nextModel = this.modelNumber+1;
	}

	/**
	 * Get the identifier of the cytoscape node this structure
	 * is an attribute of.
	 *
	 * @return identifier of the Graph Object as a String
	 */
	public String getIdentifier() {
		if (graphObjectMap == null || graphObjectMap.size() == 0)
			return "(none)";
		else if (graphObjectMap.size() == 1) {
			// Weird construct, but no easy way to get the value...
			String id = "";
			for (GraphObject obj: graphObjectMap.keySet()) {
				id = obj.getIdentifier();
				break;
			}
			return id;
		}
		return constructListOfGraphObjects(graphObjectMap.values());
	}

	/**
	 * Get the type of the model corresponding to this structure
	 *
	 * @return model type
	 */
	public StructureType getType() {
		return this.type;
	}

	/**
	 * Return the string representation of this structure
	 *
	 * @return String
	 */
	public String toString() {
		return "Node "+getIdentifier()+"; model "+structureName;
	}

	/**
	 * Return the "active site" or "special" residues for a single node or edge
	 *
	 * @param graphObject the Graph Object this list applies to
	 * @return String representation of the residues (comma separated)
	 */
	public List<String> getResidueList(GraphObject obj) {
		if (residueMap.containsKey(obj))
			return residueMap.get(obj);
		return null;
	}

	/**
	 * Return the "active site" or "special" residues for all nodes or edges
	 *
	 * @return String representation of the residues (comma separated)
	 */
	public List<String> getResidueList() {
		List<String> residueList = new ArrayList<String>();
		for (GraphObject obj: residueMap.keySet()) {
			residueList.addAll(getResidueList(obj));
		}
		return residueList;
	}

	/**
	 * Set the "active site" or "special" residues
	 *
	 * @param residues String representation of the residues (comma separated)
	 */
	public void setResidueList(GraphObject obj, String residues) {
		List<String> residueList = new ArrayList<String>();
		if (residues == null) {
			return;
		}
		String[] list = residues.split(",");
		for (int i = 0; i < list.length; i++) {
			String residue = "";
			// Parse out the structure, if there is one
			String[] components = list[i].split("#");
			if (components.length > 1 && !structureName.equals(components[0])) {
				continue;
			} else if (components.length > 1) {
				residue = components[1];
			} else if (components.length == 1) {
				residue = components[0];
			}
			// Check to see if we have a range-spec
			String resRange = "";
			if (residue == null || residue.equals("") || residue.length() == 0)
				continue;
			String[] range = residue.split("-",2);
			String chain = null;
			for (int res = 0; res < range.length; res++) {
				if (res == 1) {
					resRange = resRange.concat("-");
					if (chain != null && range[res].indexOf('.') == -1)
						range[res] = range[res].concat("."+chain);
				}

				if (res == 0 && range.length >= 2 && range[res].indexOf('.') > 0) {
					// This is a range spec with the leading residue containing a chain spec
					String[] resChain = range[res].split("\\.");
					chain = resChain[1];
					range[res] = resChain[0];
				}
				// Fix weird SFLD syntax...
				if (range[res].indexOf('|') > 0 && 
				    Character.isDigit(range[res].charAt(0))) {
					int offset = range[res].indexOf('|');
					String str = range[res].substring(offset+1) + 
					             range[res].substring(0, offset);
					range[res] = str;
				}

				// Convert to legal atom-spec
				if (Character.isDigit(range[res].charAt(0))) {
					resRange = resRange.concat(range[res]);
				} else if (Character.isDigit(range[res].charAt(1))) {
					resRange = resRange.concat(range[res].substring(1));
				} else if (range[res].charAt(0) == '.') {
					// Do we have a chain spec?
					resRange = resRange.concat(range[res]);
				} else {
					resRange = resRange.concat(range[res].substring(3));
				}
			}
			residueList.add(resRange);
		}
		residueMap.put(obj, residueList);
	}

	private String constructListOfGraphObjects(Collection<GraphObject> objList) {
		String list = null;
		for (GraphObject obj: objList) {
			if (list == null)
				list = ""+obj.getIdentifier();
			else
				list += ";"+obj.getIdentifier();
		}

		// We really don't want this to be too long -- truncate, if necessary
		if (list.length() > 14) {
			list = list.substring(0, 13).concat("...");
		}
		return list;
	}

	private boolean residuesMatch(GraphObject obj, 
	                              List<ChimeraResidue>residueList) {
		if (!residueMap.containsKey(obj))
			return true;

		List<String>residues = residueMap.get(obj);
		for (ChimeraResidue res: residueList) {
			for (String atomSpec: residues) {
				if (res.matchesAtomSpec(atomSpec))
					return true;
			}
		}
		return false;
	}
}
