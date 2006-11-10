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

import structureViz.model.ChimeraModel;

/**
 * This class provides the implementation for the ChimeraResidue, 
 * object
 * 
 * @author scooter
 *
 */

public class ChimeraResidue implements ChimeraStructuralObject {

	public static final int SINGLE_LETTER = 0;
	public static final int THREE_LETTER = 1;
	public static final int FULL_NAME = 2;

	private String type;
	private String index;
	private String chainId;
	private int modelNumber;
	private ChimeraModel chimeraModel;
	private Object userData;
	private HashMap aaNames = null;
	private static int displayType = THREE_LETTER;
	private boolean selected = false;

	public ChimeraResidue (String type, String index, int modelNumber) {
		this.type = type;
		this.index = index;
		this.modelNumber = modelNumber;
		initNames();
	}

	public ChimeraResidue (String line) {
		initNames();

		String[] split1 = line.split(":"); 

		// First half has model number -- get the number
		int numberOffset = split1[0].indexOf('#');
		String model = split1[0].substring(numberOffset+1);
		this.modelNumber = (new Integer(model)).intValue();

		// Second half has residue info: index & type
		String[] rTokens = split1[1].split(" ");
		this.type = rTokens[2];

		String[] iTokens = rTokens[0].split("\\.");
		if (iTokens.length > 0) {
			this.index = iTokens[0];

			// Careful, might or might not have a chainID
			if (iTokens.length > 1)
				this.chainId = iTokens[1];
			else
				this.chainId = "_";
		} else
			this.index = rTokens[0];
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() { return selected; }

	public List getChildren() { 
		ArrayList v = new ArrayList();
		v.add(this);
		return v; 
	}

	private void initNames() {
		// Create our residue name table
		this.aaNames = new HashMap();
		aaNames.put("ALA","A Ala Alanine");
		aaNames.put("ARG","R Arg Arginine");
		aaNames.put("ASN","N Asn Asparagine");
		aaNames.put("ASP","D Asp Aspartic_acid");
		aaNames.put("CYS","C Cys Cysteine");
		aaNames.put("GLN","Q Gln Glutamine");
		aaNames.put("GLU","E Glu Glumatic_acid");
		aaNames.put("GLY","G Gly Glycine");
		aaNames.put("HIS","H His Histidine");
		aaNames.put("ILE","I Ile Isoleucine");
		aaNames.put("LEU","L Leu Leucine");
		aaNames.put("LYS","K Lys Lysine");
		aaNames.put("MET","M Met Methionine");
		aaNames.put("PHE","F Phe Phenylalanine");
		aaNames.put("PRO","P Pro Proline");
		aaNames.put("SER","S Ser Serine");
		aaNames.put("THR","T Thr Threonine");
		aaNames.put("TRP","W Trp Tryptophan");
		aaNames.put("TYR","Y Tyr Tyrosine");
		aaNames.put("VAL","V Val Valine");
		aaNames.put("ASX","B Asx Aspartic_acid_or_Asparagine");
		aaNames.put("GLX","Z Glx Glutamine_or_Glutamic_acid");
		aaNames.put("XAA","X Xaa Any_or_unknown_amino_acid");
		aaNames.put("HOH","HOH HOH Water");
	}

	public String toString () {
		if (displayType == FULL_NAME) {
			return (toFullName(type)+" "+index);
		} else if (displayType == SINGLE_LETTER) {
			return (toSingleLetter(type)+" "+index);
		} else if (displayType == THREE_LETTER) {
			return (toThreeLetter(type)+" "+index);
		} else {
			return (type+" "+index);
		}
	}

	public String toSpec () {
		if (!chainId.equals("_"))
			return("#"+modelNumber+":"+index+"."+chainId);
		else
			return("#"+modelNumber+":"+index+".");
	}

	public String getIndex () { return this.index; }

	public String getChainId () { return this.chainId; }

	public String getType () { return this.type; }

	public int getModelNumber () { return this.modelNumber; }
	public ChimeraModel getChimeraModel () { return this.chimeraModel; }
	public void setChimeraModel (ChimeraModel chimeraModel) { 
		this.chimeraModel = chimeraModel; 
	}

	public static void setDisplayType (int type) {
		displayType = type;
	}

	public Object getUserData () {return userData;}

	public void setUserData (Object data) {
		this.userData = data;
	}

	private String toFullName(String aaType) {
		if (!aaNames.containsKey(aaType))
			return aaType;
		String[] ids = ((String)aaNames.get(aaType)).split(" ");
		return ids[2].replace('_',' ');
	}

	private String toSingleLetter(String aaType) {
		if (!aaNames.containsKey(aaType))
			return aaType;
		String[] ids = ((String)aaNames.get(aaType)).split(" ");
		return ids[0];
	}

	private String toThreeLetter(String aaType) {
		if (!aaNames.containsKey(aaType))
			return aaType;
		String[] ids = ((String)aaNames.get(aaType)).split(" ");
		return ids[1];
	}
}
