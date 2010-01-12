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
public class FastVariance implements PatternDataFunction {

	/**
	 * 
	 */
	public FastVariance() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public static void main(String[] args) {
	}
	public int getCardinality() {
		return 1;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.builtinfunctions.PatternDataFunction#evaluate(int[][], double[][])
	 */
	public double[] evaluate(int[][] dataIndexBlock, double[][] data) {
		
		return null;
	}
}
