/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 13, 2005
 *
 * Rev 0
 */
package fr.pasteur.sysbio.rdfscape.ontologyhandling;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.namespacemanagement.NamespaceManager;

/**
 * @author andrea@pasteur.fr
 * Makes Wrappers around Resources. This takes information from NameSpaceManager and from MyMemory.
 * A list of resources is mantained in order to enable caching and synchornization of properties.
 * This list is manteined in different hash functions for convenience.
 * 
 * Note: a different design, more memory savious, would have no information stored in this class
 * but only methods to query namespacemanager and mymemory to generate the information dynamically.
 * 
 * Here we take an hybrid approach. Since gathernig informations from namespaces require pattern
 * matching operations. These infos are stored in the object, that acts as a cache. 
 * Atribute values are provided through a link to Memory.
 *
 */
public class RDFWrappersFactory {
	private static int uniqueID;
	private RDFScape myRDFScapeInstance=null;
	//private MyMemory myMemory=null; //Just a link is provided
	private NamespaceManager nameSpaceManager=null; //Just a link.
	private Hashtable rdfWrappersByURI=null;
	private Hashtable rdfWrappersByNS=null;
	/**
	 * 
	 */
	public RDFWrappersFactory(RDFScape rs) {
		myRDFScapeInstance=rs;
		// myMemory=myRDFScapeInstance.getMyMemory();
		nameSpaceManager=myRDFScapeInstance.getNameSpaceManager();
		rdfWrappersByURI=new Hashtable();
		rdfWrappersByNS=new Hashtable();
		
		
	}
	public RDFResourceWrapper makeRDFResourceWrapper(String s) {
		System.out.println(0+s);
		RDFResourceWrapper tempRDFRW=(RDFResourceWrapper)rdfWrappersByURI.get(s); //caching!
		System.out.println(1);
		if(tempRDFRW==null) {
			//System.out.println(2);
			System.out.println("New");
			tempRDFRW=new RDFResourceWrapper(s);
			//String tempNamespace=nameSpaceManager.getNameSpace(s);
			//tempRDFRW.setNamespace(tempNamespace); // note: this does not change!
			
			fillResource(tempRDFRW);
			//System.out.println(7);
			rdfWrappersByURI.put(s,tempRDFRW);
			//System.out.println(8);
			//if(rdfWrappersByNS.get(tempNamespace)==null) {
				//System.out.println(9);
				//rdfWrappersByNS.put(tempNamespace,new ArrayList());
			//}
			//System.out.println(10);
			//((ArrayList)(rdfWrappersByNS.get(tempNamespace))).add(tempRDFRW);
			//System.out.println(11);
		}
		//return tempRDFRW;
		return null;
	}
	/**
	 * 
	 */
	public void update() {
		Collection toUpdateList=rdfWrappersByURI.values();
		for (Iterator iter = toUpdateList.iterator(); iter.hasNext();) {
			RDFResourceWrapper tempRDFRW = (RDFResourceWrapper) iter.next();
			fillResource(tempRDFRW);
		}
		
	}
	/**
	 * 
	 */
	public void updateAllNameSpaces() {
		update();
	}
	
	/**
	 * @param s
	 */
	public void updateNameSpace(String s) {
		ArrayList tempList=(ArrayList)rdfWrappersByNS.get(s);
		if(tempList!=null) {
			for (int i = 0; i < tempList.size(); i++) {
				fillResource((RDFResourceWrapper)tempList.get(i));
			}
		}
		
	}
	
	public void updateResource(String s) {
		fillResource((RDFResourceWrapper)rdfWrappersByURI.get(s));
	}
	/**
	 * Registers additional information into a RDFResourceWrapper object.
	 * @param res the resource to be filled.
	 */
	private void fillResource(RDFResourceWrapper res) {
		System.out.println("fill!");
		if(res.isValid()) {
			String tempNamespace=res.getNameSpace();
			//System.out.println(res.getURI()+"->"+tempNamespace);
			//New semantic: always active!!!
			//if((nameSpaceManager.getIsSelected(tempNamespace)).booleanValue()) {
				//System.out.println("A");
				//res.setPrefix(nameSpaceManager.getNameSpacePrefix(tempNamespace));
				//System.out.println("B");
				//res.setColor(nameSpaceManager.getNameSpaceColor(tempNamespace));
				//System.out.println("C");
				//res.setColorString(nameSpaceManager.getNameSpaceColorWord(tempNamespace));
				//System.out.println("D");
				//res.setShortURI(nameSpaceManager.getShortName(res.getURI()));
				//System.out.println("E");
				//res.setColorHexString(nameSpaceManager.getNameSpaceColorHexString(tempNamespace));
				//System.out.println("F");
				//res.setIsActive((nameSpaceManager.getIsSelected(tempNamespace)).booleanValue());
				//System.out.println("G");
				//res.registerMemory(myMemory);
				//System.out.println("H");
			//}
			//else res.setIsActive(false);
		}
	}

}
