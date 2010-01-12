/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jan 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.knowledge;

import java.util.ArrayList;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.RichResource;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class TripleStoreWrapper extends KnowledgeWrapper {
	ArrayList ontologiesList=null;
	public TripleStoreWrapper() throws Exception {
		super();
		ontologiesList=new ArrayList();
		System.out.println("Starring: TripleStore Wrapper");
		
	}

	public String[][] getReasonerOptions() { return null;}

	public String[] getReasonerActualOptions() { return null; }

	
	public void setReasonerActualOptions(String[] settings) {
		// nothing here
	}
	public String[][] getRuleOptions() { return null; }
	public String[] getRuleOptionDefaultValues() { return null; }
	public String[] getRuleOptionsNames() { return null; }


	public String[] getReasonerOptionNames() { return null; }
	public String[] getReasonerOptionsDefaultValues() { return null; }


	
	public boolean canAddontologies() { return true; }
	public boolean canAddRules() { return false; }
	
	
	
	public String addOntology(String address) {
		ontologiesList.add(address);
		return "Unconnected (Unsupported yet)";
	}

	
	public boolean addRuleSet(ArrayList rules) { return false; }

	public void setReasonerParameter(String arg, String value) {
		// We don't have parameters....
	}
	

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#getResourceHandler(java.lang.String)
	 */
	public RichResource getResourceHandler(String URI) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#getRichResourcesFromSPQRLQuery(java.lang.String)
	 */
	public RichResource[] getRichResourcesFromSPQRLQuery(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#getFromSPQRLQuery(java.lang.String)
	 */
	public String[] getFromSPQRLQuery(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#clear()
	 */
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#reset()
	 */
	public void reset() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#touch()
	 */
	public void touch() {
		// TODO Auto-generated method stub
		
	}

	public String validateRule(String rule) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList getAvailableQueryManagers() {
		return new ArrayList();
	}


}
