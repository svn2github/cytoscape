/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

public interface RDQLQueryAnswerer {
	public  AbstractQueryResultTable makeRDQLQuery(String query);
	 
}
