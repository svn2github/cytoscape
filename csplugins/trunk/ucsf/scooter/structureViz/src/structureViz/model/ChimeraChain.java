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
import java.util.TreeMap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import structureViz.model.ChimeraModel;

/**
 * This class provides the implementation for the ChimeraChain 
 * object
 * 
 * @author scooter
 *
 */

public class ChimeraChain implements ChimeraStructuralObject {
	private int model;
	private ChimeraModel chimeraModel;
	private String chainId;
	private TreeMap residueList;
	private Object userData;
	private boolean selected = false;

	public ChimeraChain(int model, String chainId) {
		this.model = model;
		this.chainId = chainId;
		residueList = new TreeMap();
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() { return selected; }

	public void addResidue(ChimeraResidue residue) {
		// Get the value of the index (should be an int!)
		Integer index = new Integer(residue.getIndex());
		// Put it in our map so that we can return it in order
		residueList.put(index.intValue(), residue);
	}

	public Collection getResidues() { return residueList.values(); }

	public List getChildren() { 
		return new ArrayList(residueList.values()); 
	}

	public ChimeraResidue getResidue(String residueIndex) {
		Integer index = new Integer(residueIndex);
		return (ChimeraResidue)residueList.get(index.intValue());
	}

	public String getChainId() { return chainId; }

	public int getModelNumber() { return model; }
		
	public String toString() { 
		if (chainId.equals("_")) {
			return("Chain (no ID) ("+getResidueCount()+" residues)");
		} else {
			return("Chain "+chainId+" ("+getResidueCount()+" residues)");
		}
	}

	public String toSpec() { 
		if (chainId.equals("_")) {
			return("#"+model+":."); 
		} else {
			return("#"+model+":."+chainId); 
		}		
	}

	public int getResidueCount() { return residueList.size(); }

	public void setChimeraModel(ChimeraModel model) { this.chimeraModel = model; }

	public ChimeraModel getChimeraModel() { return chimeraModel; }

	public Object getUserData () {return userData;}

	public void setUserData (Object data) {
		this.userData = data;
	}

}
