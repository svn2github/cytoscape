/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

public interface SPARQLQueryAnswerer {
	public  AbstractQueryResultTable makeSPAQRLQuery(String query);
}
