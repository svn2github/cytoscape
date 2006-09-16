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
import java.util.Iterator;
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
	private String name;
	private int identifier;
	private TreeMap chains;
	private TreeMap residues;
	private HashMap residueMap;
	private Structure structure;
	private Color modelColor;
	private Object userData;

	public ChimeraModel (String name, Structure structure, Color color) {
		this.name = name;
		if (structure != null)
			this.identifier = structure.modelNumber();
		this.chains = new TreeMap();
		this.residues = new TreeMap();
		this.residueMap = new HashMap();
		this.structure = structure;
		this.modelColor = color;
	}

	public ChimeraModel (String inputLine) {
		this.name = parseModelName(inputLine);
		this.identifier = parseModelNumber(inputLine);
		this.chains = new TreeMap();
		this.residues = new TreeMap();
		this.residueMap = new HashMap();
	}

	public ChimeraModel (Structure structure, String inputLine) {
		this.name = structure.name();
		this.structure = structure;
		this.identifier = parseModelNumber(inputLine);
		this.chains = new TreeMap();
		this.residues = new TreeMap();
		this.residueMap = new HashMap();
	}

	public Set getChainNames () { return chains.keySet(); }

	public Collection getChains () { return chains.values(); }

	public Color getModelColor () { return this.modelColor; }

	public void setModelColor (Color color) { 
		this.modelColor = color;
	}

	public ChimeraChain getChain(String chain) {
		return (ChimeraChain)chains.get(chain);
	}

	public Collection getResidues () { return residues.values(); }

	public ChimeraResidue getResidue (String index) {
		return (ChimeraResidue)residueMap.get(index);
	}

	public String getModelName () { return this.name; }

	public void setModelName (String name) { this.name = name; }

	public int getModelNumber () { return this.identifier; }

	public void setModelNumber (int modelNumber) { this.identifier = modelNumber; }

	public ChimeraModel getChimeraModel () { return this; }

	public Structure getStructure() { return this.structure; }

	public void setStructure(Structure structure) { this.structure = structure; }

	public int getChainCount () { return chains.keySet().size(); }

	public int getResidueCount () { return residues.size(); }

	public Object getUserData () {return userData;}

	public void setUserData (Object data) {
		this.userData = data;
	}

	public void addResidue (ChimeraResidue residue) {
		residue.setChimeraModel(this);
		residueMap.put(residue.getIndex(),residue);
		String chainId = residue.getChainId();
		if (chainId != null) {
			addChain(chainId, residue);
			residues.put(residue.getIndex(),residue);
		} else {
			// Get the value of the index (should be an int!)
			Integer index = new Integer(residue.getIndex());
			// Put it in our map so that we can return it in order
			residues.put(index.intValue(), residue);
		}
	}

	public void addChain(String chainId, ChimeraResidue residue) {
		ChimeraChain chain = null;
		if (!chains.containsKey(chainId)) {
			chain = new ChimeraChain(this.identifier, chainId);
			chain.setChimeraModel(this);
			chains.put(chainId, chain);
		} else {
			chain = (ChimeraChain)chains.get(chainId);
		}
		chain.addResidue(residue);
	}

	public String toString() { 
		String nodeName = "{none}";
		if (structure != null)
			nodeName = structure.getIdentifier();
		if (getChainCount() > 0) {
			return ("Node "+nodeName+" [Model #"+identifier+" "+name+" ("+getChainCount()+" chains, "+getResidueCount()+" residues)]"); 
		} else {
			return ("Node "+nodeName+" [Model #"+identifier+" "+name+" ("+getResidueCount()+" residues)]"); 
		}
	}

	public String toSpec() { return ("#"+identifier); }

	private int parseModelNumber(String inputLine) {
		int hash = inputLine.indexOf('#');
		int space = inputLine.indexOf(' ',hash);
		// model number is between hash+1 and space
		Integer modelInteger = new Integer(inputLine.substring(hash+1,space));
		return modelInteger.intValue();
	}

	private String parseModelName(String inputLine) {
		int start = inputLine.indexOf("name ");
		if (start < 0) return null;
		return inputLine.substring(start+5);
	}

}
