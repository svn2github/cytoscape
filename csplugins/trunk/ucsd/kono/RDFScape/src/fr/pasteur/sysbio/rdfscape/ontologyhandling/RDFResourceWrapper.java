/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 13, 2005
 * author andrea@pasteur.fr
 * Rev 0 Basic clean code 
 * ... should use meaningful values as formal parameters... also for the docs!
 * Rev 1... not so clean anymore... TODO : should put a link from NameSpaceManager in Wrapper Object
 * and move Factory Logic here.
 * 
 */
package fr.pasteur.sysbio.rdfscape.ontologyhandling;

import java.awt.Color;
import java.util.Map;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;


import fr.pasteur.sysbio.rdfscape.RDFScape;

/**
 * @author andrea@pasteur.fr
 * Contains additional information related to a Resource.
 * Used to pack resource information for visualization and other operations.
 * Note that this object should ideally be a subclass of a RDFnode object.
 * Right now it's just a wrapper and does not even include a link to the RDFNode resource!!! 
 */
public class RDFResourceWrapper {
	private String URI=null;
	private String shortURI=null;
	private String colorString=null;
	private String colorHexString=null;
	private Color  color=null;
	private String namespace=null;
	private String prefix=null;
	private String shortname=null;
	private Map    attributes=null;
	private String displayText=null;
	private String idAttribute=null;
	private RDFScape myRDFScapeInstance=null;
	private RDFNode myNode=null;
	private boolean active=false;
	//private MyMemoryInterface myMemory=null;
	private boolean isValid=true;
	private Color defaultColor=Color.BLACK; // TODO this should not be here
	private String defaultColorString="BLACK"; // TODO this should not be here
	private String defaultColorStringHex="#000000"; // TODO this should not be here
	private Resource jenaNode=null;
	private boolean isLiteral=false;
	
	private static int bnodeCounter=0;
	private String bnodeString=null;
	/**
	 * @param s the URI of the resource. 
	 */
	public RDFResourceWrapper(String s) {
		URI=s;
		displayText=s; //this is the default, if the object is not active
		bnodeString="?b"+bnodeCounter;
		bnodeCounter++;
	}

	/**
	 * @return Returns the uRI.
	 */
	public String getURI() {
		return URI;
	}

	/**
	 * @param pr The prefix
	 */
	public void setPrefix(String pr) {
		prefix=pr;
	}

	/**
	 * @param mycolor 
	 * 
	 */
	public void setColor(Color mycolor) {
		color=mycolor;
	}

	/**
	 * @param b
	 * 
	 */
	public void setIsActive(boolean b) {
		active=b;
	}

	/**
	 * @param myMemory
	 */
	/*
	public void registerMemory(MyMemoryInterface memory) {
		myMemory=memory;
		
	}
*/

	/**
	 * @param tempNamespace
	 */
	public void setNamespace(String tempNamespace) {
		namespace=tempNamespace;
		//System.out.println("namespace:"+tempNamespace);
		if(namespace==null) {
			//System.out.println("NULL");
			isValid=false;
			active=false;
			checkIfLiteral();
		}
		if(namespace.equals("")) {
			//System.out.println("WHITE");
			isValid=false;
			active=false;
			checkIfLiteral();
		}
		
	}

	/**
	 * 
	 */
	private void checkIfLiteral() {
		if(URI.indexOf("^^")>=0) isLiteral=true;
		displayText=shortname;
		
	}

	/**
	 * @param nameSpaceColorWord
	 */
	public void setColorString(String nameSpaceColorWord) {
		colorString=nameSpaceColorWord;
		
	}

	/**
	 * @param shortName
	 */
	public void setShortURI(String tempShortName) {
		shortname=tempShortName;
		
	}

	/**
	 * @param nameSpaceColorHexString
	 */
	public void setColorHexString(String nameSpaceColorHexString) {
		colorHexString=nameSpaceColorHexString;
		
	}

	
	/**
	 * @return Returns the attributes.
	 */
	/*
	public Map getAttributes() {
		return myMemory.getNodeAttributeMap(URI);
	}
	*/
	/**
	 * @return Returns the color.
	 */
	public Color getColor() {
		if(active) return color;
		else return defaultColor;
	}
	/**
	 * @return Returns the colorHexString.
	 */
	public String getColorHexString() {
		if(active) return colorHexString;
		else return defaultColorStringHex;
	}
	/**
	 * @return Returns the colorString.
	 */
	public String getColorString() {
		if(active) return colorString;
		else return defaultColorString;
	}
	/**
	 * @return Returns the displayText.
	 */
	public String getDisplayText() {
		if(isLiteral) {
			int divisor=URI.indexOf("^^",0);
			return URI.substring(0,divisor);
		}
		if(active) return shortname;
		else return URI;
	}

	/**
	 * @return
	 */
	public boolean getIsActive() {
		return active;
	}

	/**
	 * @return
	 */
	public String getDisplayTextBracket() {
		String temp=new String("<");
		return temp.concat(getDisplayText()).concat(">");
	}

	/**
	 * @return
	 */
	public String getNameSpace() {
		return namespace;
	}

	/**
	 * @return
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * @param mySubject
	 */
	public void addJenaNode(Resource myOntoNode) {
		jenaNode=myOntoNode;
	}
	/**
	 * In doubt, this is not a BNODE.
	 * @return
	 */
	public boolean isBlank() {
		if(jenaNode==null) return false;
		return jenaNode.isAnon();
	}
	public Resource getOntoNode() {
		return jenaNode;
	}

	/**
	 * @return
	 */
	public boolean isLiteral() {
		
		return isLiteral;
	}

	/**
	 * @return
	 */
	public String getBnodeVarName() {
		return bnodeString;
	}
	
	
}
