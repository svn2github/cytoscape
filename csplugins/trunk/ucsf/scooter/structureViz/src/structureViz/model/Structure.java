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
	String structureName;
	List<String> residueList;
	CyNode cytoscapeNode;
	int modelNumber;

	/**
 	 * Create a new Structure
	 *
	 * @param name the name of the structure
	 * @param node the CyNode that this structure points to
	 */
	public Structure (String name, CyNode node) {
		this.structureName = name;
		this.cytoscapeNode = node;
		this.modelNumber = nStructures++;
		this.residueList = null;
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
	 * @return the model number as an integer
	 */
	public int modelNumber() {return this.modelNumber;}

	/**
	 * Set the modelNumber for this structure
	 *
	 * @param number the model number
	 */
	public void setModelNumber (int number) {
		this.modelNumber = number;
	}

	/**
	 * Get the identifier of the cytoscape node this structure
	 * is an attribute of.
	 *
	 * @return identifier of the CyNode as a String
	 */
	public String getIdentifier() {
		return cytoscapeNode.getIdentifier();
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
			// Parse out the structure, if there is one
			String[] components = list[i].split("#");
			if (components.length > 1 && structureName.equals(components[0])) {
				this.residueList.add(components[1]);
			} else if (components.length == 1) {
				this.residueList.add(components[0]);
			}
		}
	}
}
