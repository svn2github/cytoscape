/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.plugin.cheminfo.model;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;
import cytoscape.util.URLUtil;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.fingerprint.Fingerprinter;
import org.openscience.cdk.inchi.InChIGenerator;
import org.openscience.cdk.inchi.InChIGeneratorFactory;
import org.openscience.cdk.inchi.InChIToStructure;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;
import org.openscience.cdk.smiles.SmilesParser;
// import org.openscience.cdk.tools.MFAnalyser;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

import net.sf.jniinchi.INCHI_RET;

import giny.model.GraphObject;

/**
 * The Compound class provides the main interface to molecule compounds.  A given node or edge in Cytoscape
 * could have multiple Compounds, either by having multiple attributes that contain compound descriptors or
 * by having a single attribute that contains multiple descriptors (e.g. comma-separated SMILES strings).  The
 * creation of a Compound results in the building of a cached 2D image for that compound, as well as the creation
 * of the CDK IMolecule, which is used for conversion from InChI to SMILES, for calculation of the molecular weight,
 * and for the calculation of Tanimoto coefficients.
 */

public class Compound {
	public enum AttriType { smiles, inchi };

	// Class variables
	static private HashMap<GraphObject, List<Compound>> compoundMap;
	static private CyLogger logger = CyLogger.getLogger(Compound.class);


	/********************************************************************************************************************* 
	 *                                                Class (static) methods                                             *
	 ********************************************************************************************************************/ 

	/**
 	 * Returns all of the Compounds for a list of graph objects (Nodes or Edges) based on the SMILES
 	 * and InChI attributes.
 	 *
 	 * @param goSet the Collection of graph objects we're looking at
 	 * @param attributes the appropriate set of attributes (nodeAttributes or edgeAttributes)
 	 * @param sList the list of attributes that contain SMILES strings
 	 * @param iList the list of attributes that contain InChI strings
 	 * @return the list of compounds.  If the compounds have not already been created, they are created
 	 *         as a byproduct of this method.
 	 */
	static public List<Compound> getCompounds(Collection<GraphObject> goSet, CyAttributes attributes, 
	                                          List<String> sList, List<String> iList) {
		List<Compound> cList = new ArrayList();
		for (GraphObject go: goSet)
			cList.addAll(getCompounds(go, attributes, sList, iList, false));

		return cList;
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
	static public List<Compound> getCompounds(GraphObject go, CyAttributes attributes, 
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
 	 * Returns the compound that matches the passed arguments or null if no such compound exists.
 	 *
 	 * @param go the graph object we're looking at
 	 * @param attr the attribute that contains the compound descriptor
 	 * @param molString the compound descriptor
 	 * @param type the type of the attribute (smiles or inchi)
 	 * @return the compound that matched or 'null' if no such compound exists.
 	 */
	static public Compound getCompound(GraphObject go, String attr, String molString, AttriType type) {
		if (compoundMap == null) return null;

		if (!compoundMap.containsKey(go))
			return null;

		List<Compound>compoundList = compoundMap.get(go);
		for (Compound c: compoundList) {
			if (c.getAttribute().equals(attr) && c.getMoleculeString().equals(molString))
				return c;
		}
		return null;
	}

	/**
 	 * Returns the list of attributes that might contain compound descriptors and are
 	 * used by any of the passed graph objects.
 	 *
 	 * @param goList the list of graph objects we're looking at
 	 * @param attributes the appropriate set of attributes (nodeAttributes or edgeAttributes)
 	 * @param attrList the entire list of compound attributes
 	 * @return the list of attributes that are in the attrList and used by objects in the goList
 	 */
	static public List<String> findAttributes(Collection<GraphObject> goList, CyAttributes attributes, 
	                                          List<String> attrList) {

		// Now get the names of all of the object attributes
		String[] attrNames = attributes.getAttributeNames();

		// Now see if any of the attributes are in our list
		ArrayList<String>attrsFound = new ArrayList();
		for (int i = 0; i < attrNames.length; i++) {
			if (attrList.contains(attrNames[i])) {
				attrsFound.add(attrNames[i]);
			}
		}

		if (attrsFound.size() == 0)
			return null;

		if (goList == null)
			return attrsFound;

		// We we know all of the attributes we're interested in -- see if these objects have any of them
		ArrayList<String>hasAttrs = new ArrayList();
		for (GraphObject go: goList) {
			for (String attribute: attrsFound) {
				if (attributes.hasAttribute(go.getIdentifier(),attribute)) {
					hasAttrs.add(attribute);
				}
			}
		}

		if (hasAttrs.size() > 0)
			return hasAttrs;

		return null;
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
	static private List<Compound> getCompounds(GraphObject go, CyAttributes attributes, 
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
			for (String cstring: stringList)
				cList.addAll(getCompounds(go, attr, cstring, type, noStructures));
		}
		return cList;
	}

	/**
 	 * Returns all of the Compounds for a single graph object (Node or Edge) based on the designated
 	 * attribute of the specific type
 	 *
 	 * @param go the graph object we're looking at
 	 * @param attr the attribute that contains the compound descriptor
 	 * @param compundString the compound descriptor
 	 * @param type the type of the attribute (smiles or inchi)
 	 * @param noStructures if 'true', the structures are fetched in the background
 	 * @return the list of compounds.  If the compounds have not already been created, they are created
 	 *         as a byproduct of this method.
 	 */
	static private List<Compound> getCompounds(GraphObject go, String attr, 
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
			Compound c = getCompound(go, attr, cstrings[i], type);
			if (c == null)
				c = new Compound(go, attr, cstrings[i], type, noStructures);

			cList.add(c);
		}
		return cList;
	}

	/********************************************************************************************************************* 
	 *                                                Instance methods                                                   *
	 ********************************************************************************************************************/ 

	// Instance variables
	private GraphObject source;
	private String smilesStr;
	private String moleculeString;
	private String attribute;
	protected Image renderedImage;
	protected boolean gettingImage;
	private AttriType attrType;
	private IMolecule iMolecule;
	private BitSet fingerPrint;

	/**
 	 * The constructor is called from the various static getCompound methods to create a compound and store it in
 	 * the compound map.
 	 *
 	 * @param source the graph object that holds this compound
 	 * @param attribute the attribute that has the compound string
 	 * @param mstring the compound descriptor itself
 	 * @param attrType the type of the compound descriptor (inchi or smiles)
 	 * @param noStructures if 'true' get the structures on a separate thread
 	 */
	public Compound(GraphObject source, String attribute, String mstring, 
	                   AttriType attrType, boolean noStructures) {
		this.source = source;
		this.attribute = attribute;
		this.moleculeString = mstring;
		this.attrType = attrType;
		this.renderedImage = null;
		this.gettingImage = false;
		this.iMolecule = null;
		this.fingerPrint = null;
		if (attrType == AttriType.inchi) {
			// Convert to smiles 
			this.smilesStr = convertInchiToSmiles(mstring);
		} else {
			this.smilesStr = mstring;
			// Create the CDK Molecule object
			SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder
							.getInstance());
			try {
				iMolecule = sp.parseSmiles(this.smilesStr);
				Fingerprinter fp = new Fingerprinter();
				fingerPrint = fp.getFingerprint(iMolecule);
			} catch (InvalidSmilesException e) {
				iMolecule = null;
				fingerPrint = null;
			} catch (CDKException e1) {
				fingerPrint = null;
			}
		}

		List<Compound> mapList = null;
		if (Compound.compoundMap == null) 
			Compound.compoundMap = new HashMap();

		if (Compound.compoundMap.containsKey(source)) {
			mapList = Compound.compoundMap.get(source);
		} else {
			mapList = new ArrayList();
		}
		mapList.add(this);
		Compound.compoundMap.put(source, mapList);

		if (!noStructures) {
			// Get the image right now
			this.renderedImage = depictWithUCSFSmi2Gif();
			return;
		}

	}

	/**
 	 * Return the original molecular string
 	 *
 	 * @return the SMILES or InChI string
 	 */
	public String getMoleculeString() {
		return moleculeString;
	}

	/**
 	 * Return the graph object (Node or Edge) that holds this compound
 	 *
 	 * @return the Node or Edge that holds this compound
 	 */
	public GraphObject getSource() {
		return source;
	}

	/**
 	 * Return the attribute that holds this compound
 	 *
 	 * @return the name of the attribute that holds this compound descriptor
 	 */
	public String getAttribute() {
		return attribute;
	}

	/**
 	 * Return the smiles string for this compound
 	 *
 	 * @return the smiles string
 	 */
	public String getSmiles() {
		return smilesStr;
	}

	/**
 	 * Return the CDK IMolecule for this compound
 	 *
 	 * @return the IMolecule for this compound
 	 */
	public IMolecule getIMolecule() {
		return iMolecule;
	}

	/**
 	 * Return the CDK fingerprint for this compound
 	 *
 	 * @return the fingerprint for this compound
 	 */
	public BitSet getFingerprint() {
		return fingerPrint;
	}
	
	/**
 	 * Return true if this compound has a moleculeString
 	 *
 	 * @return 'true' if this compound has a moleculeString
 	 */
	public boolean hasMolecule() {
		return null != moleculeString && !"".equals(moleculeString);
	}

	/**
 	 * Method to compare two compounds lexigraphically.  At this point
 	 * this is done by comparing the SMILES strings.
 	 *
 	 * @param o the compound we're being compared to
 	 * @return 0 if the structures are equal, -1 if o is less than us, 1 if it is greater
 	 */
	public int compareTo(Compound o) {
		return this.getSmiles().compareTo(o.getSmiles());
	}

	/**
 	 * Return the 2D image for this compound.  Note that this might sleep if we're in the process
 	 * of fetching the image already.
 	 *
 	 * @return the fetched image
 	 */
	public Image getImage() {
		if (renderedImage == null) {
			renderedImage = depictWithUCSFSmi2Gif();
		}
		return renderedImage;
	}

	/**
 	 * Return the molecular weight of this compound.
 	 *
 	 * @return the molecular weight
 	 */
	public double getMolecularWeight() {
		if (iMolecule == null) return 0.0f;

		IMolecularFormula mfa = MolecularFormulaManipulator.getMolecularFormula(iMolecule);
		return MolecularFormulaManipulator.getTotalMassNumber(mfa);
	}

	/**
 	 * Return the exact mass of this compound.
 	 *
 	 * @return the exact mass
 	 */
	public double getExactMass() {
		if (iMolecule == null) return 0.0f;

		IMolecularFormula mfa = MolecularFormulaManipulator.getMolecularFormula(iMolecule);
		return MolecularFormulaManipulator.getTotalExactMass(mfa);
	}
	
	/**
 	 * Use the chimeraservices smi2gif service to get a 2D image of this compound.
 	 *
 	 * @return the fetched image
 	 */
	public Image depictWithUCSFSmi2Gif() {
		if (this.moleculeString == null || "".equals(moleculeString)) {
			return null;
		}
		String url = getDepictURL();
		Image image = null;
		try {
			InputStream in = URLUtil.getInputStream(new URL(url));
			image = ImageIO.read(in);
		} catch (MalformedURLException muex) {
			logger.error("Unable to connect to UCSF SMILES depiction services: "+muex.getMessage(), muex);
			return null;
		} catch (IOException ioex) {
			logger.error("Unable to connect to UCSF SMILES depiction services: "+ioex.getMessage(), ioex);
			return null;
		}
		return image;
	}
	
	/**
 	 * Use the chimeraservices smi2gif service to get a 2D image of a specific size for this compound.
 	 *
 	 * @param width the width of the rendered image
 	 * @param height the height of the rendered image
 	 * @param bgcolor the background color of the rendered image
 	 * @return the fetched image
 	 */
	public Image depictWithUCSFSmi2Gif(int width, int height, String bgcolor) {
		if (this.moleculeString == null || "".equals(moleculeString)) {
			return null;
		}
		String url = getDepictURL(width, height, bgcolor);
		Image image = null;
		try {
			InputStream in = URLUtil.getInputStream(new URL(url));
			image = ImageIO.read(in);
		} catch (MalformedURLException muex) {
			logger.error("Unable to connect to UCSF SMILES depiction services: "+muex.getMessage(), muex);
			return null;
		} catch (IOException ioex) {
			logger.error("Unable to connect to UCSF SMILES depiction services: "+ioex.getMessage(), ioex);
			return null;
		}
		return image;
	}	

	/**
	 * Convert from an InChI string to a SMILES string
	 * 
	 * @param inchi InChI string
	 * @return SMILES string
	 */
	private String convertInchiToSmiles(String inchi) {

		try {
			// Get the factory	
			InChIGeneratorFactory factory = new InChIGeneratorFactory();
			InChIToStructure intostruct = factory.getInChIToStructure(inchi, DefaultChemObjectBuilder.getInstance());

		// Get the structure
		INCHI_RET ret = intostruct.getReturnStatus();
		if (ret == INCHI_RET.WARNING) {
			logger.warning("InChI warning: " + intostruct.getMessage());
		} else if (ret != INCHI_RET.OKAY) {
			logger.error("Structure generation failed failed: " + ret.toString()
                    + " [" + intostruct.getMessage() + "]");
			return null;
		}

		iMolecule = new Molecule(intostruct.getAtomContainer());
		// Use the molecule to create a SMILES string
		SmilesGenerator sg = new SmilesGenerator();
		return sg.createSMILES(iMolecule);
		} catch (Exception e) {
			logger.error("Structure generation failed failed: " + e.getMessage());
			return null;
		}

		/*
		String url = "http://www.chemspider.com/inchi.asmx/InChIToSMILES?inchi="
				+ inchi.trim();
		String smiles = null;
		try {
			String result = URLUtil.download(new URL(url));
			Pattern pattern = Pattern.compile(".*<[^>]*>([^<]*)</string>");
			Matcher matcher = pattern.matcher(result);
			if (matcher.find()) {
				smiles = matcher.group(1);
			}
		} catch (MalformedURLException muex) {
			logger.error("Unable to connect to chemspider conversion services: "+muex.getMessage(), muex);
			return null;
		} catch (IOException ioex) {
			logger.error("Unable to connect to chemspider conversion services: "+ioex.getMessage(), ioex);
			return null;
		}
		return smiles;
		*/
	}

	/**
 	 * Returns the URL to use for getting the rendered image
 	 */
	private String getDepictURL() {
		String url = "http://chimeraservices.compbio.ucsf.edu/cgi-bin/smi2gif.cgi?"
			+ "smiles=" + this.smilesStr + "&format=png&width=640&height=640&bgcolor=white&linewidth=2&symbolfontsize=24";	
		return url;
	}	
	
	/**
 	 * Returns the URL to use for getting the rendered image
 	 *
 	 * @param width the width parameter to put into the URL
 	 * @param height the height parameter to put into the URL
 	 * @param bgcolor the bgcolor parameter to put into the URL
 	 */
	private String getDepictURL(int width, int height, String bgcolor) {
		String url = "http://chimeraservices.compbio.ucsf.edu/cgi-bin/smi2gif.cgi?"
			+ "smiles=" + this.smilesStr + "&width=" + width + "&height=" + height + "&bgcolor=" + bgcolor;
		return url;
	}
}
