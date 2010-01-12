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
public class Variance implements PatternDataFunction{

	/**
	 * 
	 */
	public Variance() {
		super();
		// TODO Auto-generated constructor stub
	}


	


	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.builtinfunctions.PatternDataFunction#getCardinality()
	 */
	public int getCardinality() {
		return 1;
	}





	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.builtinfunctions.PatternDataFunction#evaluate(int[][], double[][])
	 */
	public double[] evaluate(int[][] dataIndexBlock, double[][] data) {
		double result[]=new double[dataIndexBlock.length];
		for (int i = 0; i < dataIndexBlock.length; i++) {
			int gene1Index=dataIndexBlock[i][0];
			double mean=0;
			for (int j = 0; j < data[i].length; j++) {
				mean+=data[gene1Index][j];
				
			}
			mean=mean/data.length;
			System.out.println("Mean");
			double squareMean=0;
			for (int j = 0; j < data[i].length; j++) {
				squareMean+=(data[gene1Index][j]-mean)*(data[gene1Index][j]-mean);
				
			}
			squareMean=squareMean/data[i].length;
			result[i]=squareMean;
		}
		return result;
		
	}
}
