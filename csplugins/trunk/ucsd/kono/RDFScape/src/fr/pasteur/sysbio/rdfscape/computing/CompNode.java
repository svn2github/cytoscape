/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.computing;

import java.util.ArrayList;

/*
 * Created on Oct 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CompNode {
	public String dump();

	/**
	 * @param patternCollection
	 * @return
	 */
	public boolean collectPatterns(ArrayList patternCollection);
	public CompNode optimizeTree(EvaluationNode eval,CompNode parent);

	

	
}
