/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jan 24, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.knowledge;

import java.util.ArrayList;
import java.util.Hashtable;

import com.hp.hpl.jena.rdf.model.StmtIterator;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.RichResource;
import fr.pasteur.sysbio.rdfscape.query.GraphQueryAnswerer;
import fr.pasteur.sysbio.rdfscape.query.RDQLQueryAnswerer;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class KnowledgeWrapper {
	//protected CommonMemory commonMemory;
	
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper#setMemory(fr.pasteur.sysbio.rdfscape.CommonMemory)
	 */
	
	abstract boolean canAddontologies();
	public abstract boolean canAddRules();
	public abstract String addOntology(String addresss);
	
	public abstract boolean addRuleSet(ArrayList rules);
	public abstract void setReasonerParameter(String arg, String value);
	abstract RichResource getResourceHandler(String URI);
	
	
	

	abstract public void clear();
	abstract public void reset();
	abstract public void touch();
	/**
	 * @return
	 */
	abstract public String[][] getReasonerOptions();
	abstract public String[] getReasonerActualOptions();
	//abstract public void setReasonerActualOptions(String[] settings);
	abstract public String[] getReasonerOptionNames();
	abstract  public String[] getReasonerOptionsDefaultValues();
	
	
	abstract public String[][] getRuleOptions();
	public abstract String[] getRuleOptionDefaultValues();
	public abstract String[] getRuleOptionsNames();
	public abstract String validateRule(String rule);
	//public abstract QueryManagerItem[] getQueryManagerModes();
	public abstract  ArrayList getAvailableQueryManagers();
	
	public KnowledgeWrapper() throws Exception {
		System.out.println("\tBuilding KnowledgeWraper");
		if(RDFScape.getCommonMemory()==null) {
			throw new Exception("Unable to initialize knowledge wrapper : missing CommonMemory");
		}
		RDFScape.getCommonMemory().relinkURIID();
	}
	
	public static  boolean hasRDQLSupport(KnowledgeWrapper knowledge) {
		try {
			RDQLQueryAnswerer test=(RDQLQueryAnswerer)knowledge;
			} catch (Exception e) {
				return false;
			}
			return true;
		
	}
	public static  boolean hasGraphAccessSupport(KnowledgeWrapper knowledge) {
		try {
			GraphQueryAnswerer test=(GraphQueryAnswerer)knowledge;
			} catch (Exception e) {
				return false;
			}
			return true;
		
	}
	
	 
	
	
}
