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

import java.util.List;
import java.util.ArrayList;

import cytoscape.*;

/**
 * The Structure class provides a link between the Chimera data
 * and the Cytoscape data.
 */
public class Structure {
	static int nStructures = 0;
	static int nextModel = 0;
	String structureName;
	List<String> residueList;
	CyNode cytoscapeNode;
	int modelNumber;
	StructureType type;

	public enum StructureType {PDB_MODEL, MODBASE_MODEL, SMILES};

	/**
	 * Get the next available model number
	 *
	 * @return the next model number
	 */
	public static int getNextModel() {return nextModel++;}

	/**
 	 * Create a new Structure
	 *
	 * @param name the name of the structure
	 * @param node the CyNode that this structure points to
	 */
	public Structure (String name, CyNode node, StructureType type) {
		this.structureName = name;
		this.cytoscapeNode = node;
		this.modelNumber = nextModel;
		this.residueList = null;
		this.type = type;
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
	public CyNode node() {return this.cytoscapeNode;}

	/**
	 * Get the modelNumber for this structure
	 *
	 * @return the model number as a float
	 */
	public int modelNumber() {return this.modelNumber;}

	/**
	 * Set the modelNumber for this structure
	 *
	 * @param number the model number
	 */
	public void setModelNumber (float number) {
		Float floatNumber = new Float(number);
		this.modelNumber = floatNumber.intValue();
		if (this.modelNumber >= nextModel) nextModel = this.modelNumber+1;
	}

	/**
	 * Get the identifier of the cytoscape node this structure
	 * is an attribute of.
	 *
	 * @return identifier of the CyNode as a String
	 */
	public String getIdentifier() {
		if (cytoscapeNode == null)
			return null;
		return cytoscapeNode.getIdentifier();
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
	 * Return the "active site" or "special" residues
	 *
	 * @return String representation of the residues (comma separated)
	 */
	public List<String> getResidueList() {
		return residueList;
	}

	/**
	 * Set the "active site" or "special" residues
	 *
	 * @param residues String representation of the residues (comma separated)
	 */
	public void setResidueList(String residues) {
		this.residueList = new ArrayList();
		if (residues == null) {
			return;
		}
		String[] list = residues.split(",");
		for (int i = 0; i < list.length; i++) {
			String residue = "";
			// Parse out the structure, if there is one
			String[] components = list[i].split("#");
			if (components.length > 1 && structureName.equals(components[0])) {
				residue = components[1];
			} else if (components.length == 1) {
				residue = components[0];
			}
			// Check to see if we have a range-spec
			String resRange = "";
			if (residue == null || residue.equals("") || residue.length() == 0)
				continue;
			String[] range = residue.split("-",2);
			for (int res = 0; res < range.length; res++) {
				if (res == 1) resRange = resRange.concat("-");
				// Convert to legal atom-spec
				if (Character.isDigit(residue.charAt(0))) {
					resRange = resRange.concat(range[res]);
				} else if (Character.isDigit(residue.charAt(1))) {
					resRange = resRange.concat(range[res].substring(1));
				} else if (residue.charAt(0) == '.') {
					// Do we have a chain spec?
					resRange = resRange.concat(range[res]);
				} else {
					resRange = resRange.concat(range[res].substring(3));
				}
			}
			this.residueList.add(resRange);
		}
	}
}
