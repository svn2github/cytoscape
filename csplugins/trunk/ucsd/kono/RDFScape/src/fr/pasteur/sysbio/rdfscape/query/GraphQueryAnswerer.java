/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

public interface GraphQueryAnswerer {
	public AbstractQueryResultTable getLeftOfNode(Object node);
	public AbstractQueryResultTable getRightOfNode(Object node);
	public String getRDFLabelForURI(String uri);
	public String getShortLabelForURI(String uri);
	public String[][] getDatatypeAttributeBox(String uri);
	public String[] getClassURIList();
	public void addDataStatement(String uri, String string, String labelString);
	public void addTypedDataStatement(String uri, String property, Object value, String type);
	
	
}
