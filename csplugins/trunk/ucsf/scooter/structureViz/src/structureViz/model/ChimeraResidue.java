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

	/* Constants */
	public static final int SINGLE_LETTER = 0; // Display residues as a single letter
	public static final int THREE_LETTER = 1; // Display residues as three letters
	public static final int FULL_NAME = 2; // Display full residue names

	private String type;	// Residue type
	private String index;	// Residue index
	private String chainId; // ChainID for this residue
	private int modelNumber; // model number for this residue
	private int subModelNumber; // sub-model number for this residue
	private ChimeraModel chimeraModel; // ChimeraModel thie residue is part of
	private Object userData; // user data to associate with this residue
	private static HashMap aaNames = null; // a map of amino acid names
	private static int displayType = THREE_LETTER; // the current display type
	private boolean selected = false; // the selection state

	/**
	 * Constructor to create a new ChimeraResidue
	 *
	 * @param type the residue type
	 * @param index the index of the residue
	 * @param modelNumber the model number this residue is part of
	 */
	public ChimeraResidue (String type, String index, int modelNumber) {
		this(type, index, modelNumber, 0);
	}

	/**
	 * Constructor to create a new ChimeraResidue
	 *
	 * @param type the residue type
	 * @param index the index of the residue
	 * @param modelNumber the model number this residue is part of
	 * @param subModelNumber the sub-model number this residue is part of
	 */
	public ChimeraResidue (String type, String index, int modelNumber, int subModelNumber) {
		this.type = type;
		this.index = index;
		this.modelNumber = modelNumber;
		this.subModelNumber = subModelNumber;
		if (aaNames == null)
			initNames();
	}

	/**
	 * Constructor to create a new ChimeraResidue from an input line
	 *
	 * @param line a Chimera residue description
	 */
	public ChimeraResidue (String line) {
		initNames();

		String[] split1 = line.split(":"); 

		// TODO: this should be merged with the parse line code in
		// ChimeraModel

		// First half has model number -- get the number
		int numberOffset = split1[0].indexOf('#');
		String model = split1[0].substring(numberOffset+1);
		int decimalOffset = model.indexOf('.');	// Do we have a sub-model?
		try {
			this.subModelNumber = 0;
			if (decimalOffset > 0) {
				this.subModelNumber = Integer.parseInt(model.substring(decimalOffset+1));
				this.modelNumber = Integer.parseInt(model.substring(0, decimalOffset));
			} else {
				this.modelNumber = Integer.parseInt(model);
			}
		} catch (Exception e) {
			cytoscape.logger.CyLogger.getLogger(ChimeraResidue.class).error("Unexpected return from Chimera in ChimeraResidue: "+model);
			this.modelNumber = -1;
		}

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

	/**
	 * Set the selected state for this residue
	 *
	 * @param selected the selection state to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/**
	 * Return the selected state of this residue
	 *
	 * @return the selected state
	 */
	public boolean isSelected() { return selected; }

	/**
	 * Return an array made up of this residue (required
	 * for ChimeraStructuralObject interface
	 *
	 * @return a List with this residue as its sole member
	 */
	public List getChildren() { 
		ArrayList v = new ArrayList();
		v.add(this);
		return v; 
	}

	/**
	 * Return the string representation of this residue as follows:
	 * 	"<i>residue_name</i> <i>index</i>" 
	 * where <i>residue_name</i> could be either the single letter,
	 * three letter, or full name representation of the amino acid.
	 *
	 * @return the string representation
	 */
	public String displayName () {
		return toString();
	}

	/**
	 * Return the string representation of this residue as follows:
	 * 	"<i>residue_name</i> <i>index</i>" 
	 * where <i>residue_name</i> could be either the single letter,
	 * three letter, or full name representation of the amino acid.
	 *
	 * @return the string representation
	 */
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


	/**
	 * Return the Chimera specification for this Residue
	 *
	 * @return Chimera specification
	 */
	public String toSpec () {
		if (!chainId.equals("_"))
			return("#"+modelNumber+":"+index+"."+chainId);
		else
			return("#"+modelNumber+":"+index+".");
	}

	/**
 	 * Return true if this residue is included in the atom
 	 * spec passed to it...
 	 *
 	 * @param atomSpec the Chimera atom spec
 	 * @return true if this residue is addressed by this atom spec
 	 */
	public boolean matchesAtomSpec(String atomSpec) {
		// We need to parse the atom spec to see if it includes this residue
		// Possibilities:
		// 	1) Atom spec is just a residue -- compare and return result
		// 	2) Atom spec is a range, need to see if this residue is in that range
		// 	3) Atom spec is a chain -- see if this residue is part of that chain
		if (atomSpec.charAt(0) == '.') {
			// System.out.println("Chain match: chainId = "+chainId+" atomSpec = "+atomSpec);
			// We have a chain
			if (atomSpec.substring(1).equalsIgnoreCase(chainId))
				return true;
		} else if (atomSpec.indexOf("-") > 0) {
			// We have a range
			String range[] = atomSpec.split("-");
			if (range[1].indexOf('.') > 0) {
				String chainSpec[] = range[1].split("\\.");
				if (!chainSpec[1].equalsIgnoreCase(chainId))
					return false;
				range[1] = chainSpec[0];
			}
			// System.out.println("Residue match: index = "+index+" range = "+range[0]+"-"+range[1]);
			int thisResidue = Integer.parseInt(index);
			int rangeStart = Integer.parseInt(range[0]);
			int rangeEnd = Integer.parseInt(range[1]);
			if (rangeStart <= thisResidue && rangeEnd >= thisResidue)
				return true;
		} else {
			// Residue
			if (atomSpec.indexOf('.') > 0) {
				// We've got a chain -- make sure it's our chain
				// System.out.println("Chain match: chainId = "+chainId+" atomSpec = "+atomSpec);
				String chainSpec[] = atomSpec.split("\\.");
				if (!chainSpec[1].equalsIgnoreCase(chainId))
					return false;
				atomSpec = chainSpec[0];
			}
			// System.out.println("Residue match: index = "+index+" atomSpec = "+atomSpec);
			if (index.equals(atomSpec))
				return true;
		}
		return false;
	}

	/**
	 * Get the index of this residue
	 *
	 * @return residue index
	 */
	public String getIndex () { return this.index; }

	/**
	 * Get the chainID for this residue
	 *
	 * @return String value of the chainId
	 */
	public String getChainId () { return this.chainId; }

	/**
	 * Get the type for this residue
	 *
	 * @return residue type
	 */
	public String getType () { return this.type; }

	/**
	 * Get the model number for this residue
	 *
	 * @return the model number
	 */
	public int getModelNumber () { return this.modelNumber; }

	/**
	 * Get the sub-model number for this residue
	 *
	 * @return the sub-model number
	 */
	public int getSubModelNumber () { return this.subModelNumber; }

	/**
	 * Get the model this residue is part of
	 *
	 * @return the ChimeraModel
	 */
	public ChimeraModel getChimeraModel () { return this.chimeraModel; }

	/**
	 * Set the model this residue is part of
	 *
	 * @param chimeraModel the ChimeraModel this model is part of
	 */
	public void setChimeraModel (ChimeraModel chimeraModel) { 
		this.chimeraModel = chimeraModel; 
	}

	/**
	 * Get the user data for this residue
	 *
	 * @return user data
	 */
	public Object getUserData () {return userData;}

	/**
	 * Set the user data for this Residue
	 *
	 * @param data the user data to associate with this residue
	 */
	public void setUserData (Object data) {
		this.userData = data;
	}

	/**********************************************
	 * Static routines
	 *********************************************/

	/**
	 * Initialize the residue names
	 */
	private static void initNames() {
		// Create our residue name table
		aaNames = new HashMap();
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

	/**
	 * Set the display type.
	 *
	 * @param type the display type
	 */
	public static void setDisplayType (int type) {
		displayType = type;
	}

	public static int getDisplayType() {return displayType;}

	/**
	 * Convert the amino acid type to a full name
	 *
	 * @param aaType the residue type to convert
	 * @return the full name of the residue
	 */
	private static String toFullName(String aaType) {
		if (!aaNames.containsKey(aaType))
			return aaType;
		String[] ids = ((String)aaNames.get(aaType)).split(" ");
		return ids[2].replace('_',' ');
	}

	/**
	 * Convert the amino acid type to a single letter
	 *
	 * @param aaType the residue type to convert
	 * @return the single letter representation of the residue
	 */
	private static String toSingleLetter(String aaType) {
		if (!aaNames.containsKey(aaType))
			return aaType;
		String[] ids = ((String)aaNames.get(aaType)).split(" ");
		return ids[0];
	}

	/**
	 * Convert the amino acid type to three letters
	 *
	 * @param aaType the residue type to convert
	 * @return the three letter representation of the residue
	 */
	private static String toThreeLetter(String aaType) {
		if (!aaNames.containsKey(aaType))
			return aaType;
		String[] ids = ((String)aaNames.get(aaType)).split(" ");
		return ids[1];
	}
}
