/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 16, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.computing.builtinfunctions;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PatternDataFunction {
	double[] evaluate(int[][] dataIndexBlock,double[][] data);
	int getCardinality();
	/**
	 * @param dataIndexBlock
	 * @param expressionData
	 * @return
	 */
	
}
