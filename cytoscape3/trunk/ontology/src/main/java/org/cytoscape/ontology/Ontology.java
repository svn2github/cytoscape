package org.cytoscape.ontology;

import org.cytoscape.model.CyNetwork;

/**
 * Represents an ontology DAG.
 * 
 * <p>
 * Internally, ontology DAG is stored as a CyNetwork network.
 * This is a wrapper for it with some utility methods.
 * 
 * @author kono
 *
 */
public interface Ontology {
	
	/**
	 * ID of this ontology DAG.
	 * @return
	 */
	public String getID();
	
	/**
	 * Human-readable name of this DAG.
	 * 
	 * @return
	 */
	public String getName();
	
	public Term getRootTerm();
	
	public Ontology getPathToRoot(Term target);
	
	public CyNetwork getDAG();

}
