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
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.awt.Color;

import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraResidue;
import structureViz.model.Structure;

/**
 * This class provides the implementation for the ChimeraModel, 
 * ChimeraChain, and ChimeraResidue objects
 * 
 * @author scooter
 *
 */

public class ChimeraModel implements ChimeraStructuralObject {
	private String name; 				// The name of this model
	private int modelNumber; 		// The model number
	private int subModelNumber; 		// The sub-model number
	private TreeMap<String,ChimeraChain> chains; 		// The list of chains
	private TreeMap<String,ChimeraResidue> residues; 	// The list of residues
	private HashMap<String,ChimeraResidue> residueMap;	// A map of residue names and residues
	private Structure structure;// A pointer to the structure
	private Color modelColor;		// The color of this model (from Chimera)
	private Object userData;		// User data associated with this model
	private boolean selected = false;	// The selected state of this model

	/**
	 * Constructor to create a model 
	 *
	 * @param name the name of this model
	 * @param structure the Structure associated with this ChimeraModel
	 * @param color the model Color
	 */
	public ChimeraModel (String name, Structure structure, Color color) {
		this.name = name;
		if (structure != null) {
			this.modelNumber = structure.modelNumber();
			this.subModelNumber = structure.subModelNumber();
		}
		this.chains = new TreeMap();
		this.residues = new TreeMap();
		this.residueMap = new HashMap();
		this.structure = structure;
		this.modelColor = color;
	}

	/**
	 * Constructor to create a model from the Chimera input line
	 *
	 * @param inputLine Chimera input line from which to construct this model
	 */
	public ChimeraModel (String inputLine) {
		this.name = parseModelName(inputLine);
		this.identifier = parseModelNumber(inputLine);
		this.chains = new TreeMap();
		this.residues = new TreeMap();
		this.residueMap = new HashMap();
	}

	/**
	 * Constructor to create a model from the Chimera input line when the
	 * structure is known
	 *
	 * @param structure Chimera structure for this model
	 * @param inputLine Chimera input line from which to construct this model
	 */
	public ChimeraModel (Structure structure, String inputLine) {
		this.name = structure.name();
		this.structure = structure;
		this.identifier = parseModelNumber(inputLine);
		this.chains = new TreeMap();
		this.residues = new TreeMap();
		this.residueMap = new HashMap();
	}

	/**
	 * Set the selected state of this model
	 *
	 * @param selected a boolean to set the selected state to
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Return the selected state of this model
	 *
	 * @return the selected state
	 */
	public boolean isSelected() { return selected; }

	/**
	 * Get the list of chain names associated with this model
	 *
	 * @return return the list of chain names for this model
	 */
	public Set getChainNames () { return chains.keySet(); }

	/**
	 * Return the chains in this model as a colleciton
	 *
	 * @return the chains in this model
	 */
	public Collection<ChimeraChain> getChains () { return chains.values(); }

	/**
	 * Return the chains in this model as a List
	 *
	 * @return the chains in this model as a list
	 */
	public List<ChimeraChain>getChildren () {
		return new ArrayList(chains.values());
	}

	/**
	 * Get the model color of this model
	 *
	 * @return model color of this model
	 */
	public Color getModelColor () { return this.modelColor; }

	/**
	 * Set the color of this model
	 *
	 * @param color Color of this model
	 */
	public void setModelColor (Color color) { 
		this.modelColor = color;
	}

	/**
	 * Get a specific chain from the model
	 *
	 * @param chain the ID of the chain to return
	 * @return ChimeraChain associated with the chain
	 */
	public ChimeraChain getChain(String chain) {
		return chains.get(chain);
	}

	/**
	 * Get the residues associated with this model
	 *
	 * @return the list of residues in this model
	 */
	public Collection<ChimeraResidue> getResidues () { return residues.values(); }

	/**
	 * Return a specific residue based on its index
	 *
	 * @param index of the residue to return
	 * @return the residue associated with that index
	 */
	public ChimeraResidue getResidue (String index) {
		return residueMap.get(index);
	}

	/**
	 * Return the name of this model
	 *
	 * @return model name
	 */
	public String getModelName () { return this.name; }

	/**
	 * Set the name of this model
	 *
	 * @param name model name
	 */
	public void setModelName (String name) { this.name = name; }

	/**
	 * Get the model number of this model
	 *
	 * @return integer model number 
	 */
	public float getModelNumber () { return this.identifier; }

	/**
	 * Set the model number of this model
	 *
	 * @param modelNumber integer model number 
	 */
	public void setModelNumber (float modelNumber) { this.identifier = modelNumber; }

	/**
	 * Get the ChimeraModel (required for ChimeraStructuralObject interface)
	 *
	 * @return ChimeraModel
	 */
	public ChimeraModel getChimeraModel () { return this; }

	/**
	 * Get the structure object associated with this model
	 *
	 * @return Structure associated with this model
	 */
	public Structure getStructure() { return this.structure; }

	/**
	 * Set the structure object for this model
	 *
	 * @param structure Structure to associate with this model
	 */
	public void setStructure(Structure structure) { this.structure = structure; }

	/**
	 * Get the number of chains in this model
	 *
	 * @return integer chain count
	 */
	public int getChainCount () { return chains.keySet().size(); }

	/**
	 * Get the number of residues in this model
	 *
	 * @return integer residues count
	 */
	public int getResidueCount () { return residues.size(); }

	/**
	 * Get the user data for this model
	 *
	 * @return user data
	 */
	public Object getUserData () {return userData;}

	/**
	 * Set the user data for this model
	 *
	 * @param data user data to associate with this model
	 */
	public void setUserData (Object data) {
		this.userData = data;
	}

	/**
	 * Add a residue to this model
	 *
	 * @param residue to add to the model
	 */
	public void addResidue (ChimeraResidue residue) {
		residue.setChimeraModel(this);
		residueMap.put(residue.getIndex(),residue);
		String chainId = residue.getChainId();
		if (chainId != null) {
			addResidue(chainId, residue);
		} else {
			addResidue("_", residue);
		}
		// Put it in our map so that we can return it in order
		residues.put(residue.getIndex(), residue);
	}

	/**
	 * Add a residue to a chain in this model.  If the chain associated
	 * with chainId doesn't exist, it will be created.
	 *
	 * @param chainId to add the residue to
	 * @param residue to add to the chain
	 */
	public void addResidue(String chainId, ChimeraResidue residue) {
		ChimeraChain chain = null;
		if (!chains.containsKey(chainId)) {
			chain = new ChimeraChain(this.identifier, chainId);
			chain.setChimeraModel(this);
			chains.put(chainId, chain);
		} else {
			chain = chains.get(chainId);
		}
		chain.addResidue(residue);
	}

	public String displayName() {
		return toString();
	}

	/**
	 * Return a string representation for the model
	 */
	public String toString() { 
		String nodeName = "{none}";
		if (structure != null && structure.getIdentifier() != null)
			nodeName = structure.getIdentifier();
		String displayName = name;
		if (name.length() > 14)
			displayName = name.substring(0,13)+"...";
		if (getChainCount() > 0) {
			return ("Node "+nodeName+" [Model #"+identifier+" "+displayName+" ("+getChainCount()+" chains, "+getResidueCount()+" residues)]"); 
		} else if (getResidueCount() > 0) {
			return ("Node "+nodeName+" [Model #"+identifier+" "+displayName+" ("+getResidueCount()+" residues)]"); 
		} else {
			return ("Node "+nodeName+" [Model #"+identifier+" "+displayName+"]"); 
		}
	}

	/**
	 * Return the Chimera specification for this model
	 */
	public String toSpec() { return ("#"+identifier); }

	/**
	 * Parse the model number returned by Chimera and return
	 * the float value
	 */
	private float parseModelNumber(String inputLine) {
		int hash = inputLine.indexOf('#');
		int space = inputLine.indexOf(' ',hash);
		// model number is between hash+1 and space
		try {
			Float modelNumber = new Float(inputLine.substring(hash+1,space));
			return modelNumber.floatValue();
		} catch (Exception e) {
			cytoscape.logger.CyLogger.getLogger(ChimeraResidue.class).error("Unexpected return from Chimera: "+inputLine);
			return -1;
		}
	}

	/**
	 * Parse the model identifier returned by Chimera and return
	 * the String value
	 */
	private String parseModelName(String inputLine) {
		int start = inputLine.indexOf("name ");
		if (start < 0) return null;
		// Might get a quoted string (don't understand why, but there you have it)
		if (inputLine.startsWith("\"", start+5)) {
			start += 6; // Skip over the first quote
			int end = inputLine.lastIndexOf('"');
			if (end >= 1) {
							return inputLine.substring(start,end);
			} else
							return inputLine.substring(start);
		} else {
			return inputLine.substring(start+5);
		}
	}
}
