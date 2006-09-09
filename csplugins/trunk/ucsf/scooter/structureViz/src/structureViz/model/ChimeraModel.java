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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import structureViz.model.ChimeraChain;
import structureViz.model.ChimeraResidue;

/**
 * This class provides the implementation for the ChimeraModel, 
 * ChimeraChain, and ChimeraResidue objects
 * 
 * @author scooter
 *
 */

public class ChimeraModel {
	private String name;
	private int identifier;
	private HashMap chains;
	private ArrayList residues;
	private HashMap residueMap;

	public ChimeraModel (String name, int identifier) {
		this.name = name;
		this.identifier = identifier;
		this.chains = new HashMap();
		this.residues = new ArrayList();
		this.residueMap = new HashMap();
	}

	public void addResidue (ChimeraResidue residue) {
		residues.add(residue);
		residueMap.put(residue.getIndex(),residue);
		String chainId = residue.getChainId();
		if (chainId != null) {
			addChain(chainId, residue);
		}
	}

	public void addChain(String chainId, ChimeraResidue residue) {
		ChimeraChain chain = null;
		if (!chains.containsKey(chainId)) {
			chain = new ChimeraChain(this.identifier, chainId);
			chains.put(chainId, chain);
		} else {
			chain = (ChimeraChain)chains.get(chainId);
		}
		chain.addResidue(residue);
	}

	public Set getChainNames () { return chains.keySet(); }

	public ChimeraChain getChain(String chain) {
		return (ChimeraChain)chains.get(chain);
	}

	public ArrayList getResidues () { return residues; }

	public ChimeraResidue getResidue (String index) {
		return (ChimeraResidue)residueMap.get(index);
	}

	public int getModelNumber () { return identifier; }

	public String toString() { 
		if (getChainCount() > 0) {
			return ("Model #"+identifier+" "+name+" ("+getChainCount()+"chains, "+getResidueCount()+" residues)"); 
		} else {
			return ("Model #"+identifier+" "+name+" ("+getResidueCount()+" residues)"); 
		}
	}

	public String toSpec() { return ("#"+identifier); }

	public int getChainCount () { return chains.keySet().size(); }

	public int getResidueCount () { return residues.size(); }

}
