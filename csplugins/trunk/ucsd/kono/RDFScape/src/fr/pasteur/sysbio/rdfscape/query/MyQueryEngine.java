/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

import javax.swing.JPanel;

import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;

public abstract class MyQueryEngine {
	/**
	 * return a Panel controlling this query method, or null if this query method has no panel
	 */
	//public abstract void QueryManagerItem(KnowledgeWrapper wp);
	public abstract JPanel getPanel();
	public abstract void setQuery(String query);
	public abstract AbstractQueryResultTable makeQuery();
	public abstract String getLabel();
	public abstract void reset();
}
