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
public class Correlation implements PatternDataFunction {

	/**
	 * 
	 */
	public Correlation() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	public static void main(String[] args) {
	}
	public int getCardinality() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.builtinfunctions.PatternDataFunction#evaluate(int[][], double[][])
	 */
	public double[] evaluate(int[][] dataIndexBlock, double[][] data) {
		double result[]=new double[dataIndexBlock.length];
		for (int i = 0; i < dataIndexBlock.length; i++) {
			int gene1Index=dataIndexBlock[i][0];
			int gene2Index=dataIndexBlock[i][1];
			
			double sum1=0;
			double sum2=0;
			
			double product=1;
			double squaresum1=0;
			double squaresum2=0;
			
			for (int j = 0; j < data[i].length; j++) {
				sum1+=data[gene1Index][j];
				sum2+=data[gene2Index][j];
				product=product*data[gene1Index][j]*data[gene2Index][j];
				squaresum1+=data[gene1Index][j]*data[gene1Index][j];
				squaresum2+=data[gene2Index][j]*data[gene2Index][j];
			}
			double corr=(product-(sum1*sum2)/data[i].length)/(Math.sqrt((squaresum1-sum1*sum1/data[i].length)*(squaresum2-sum2*sum2/data[i].length)));
		
			result[i]=corr;
			
		}
		return result;
	}
}
