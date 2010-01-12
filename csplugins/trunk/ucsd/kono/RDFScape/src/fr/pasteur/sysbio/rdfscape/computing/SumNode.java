/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.computing;

import java.util.ArrayList;
import java.util.Hashtable;

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
public class SumNode implements CompNode,CompValueNode {
	FunctionNode functionNode=null;
	/**
	 * 
	 */
	public SumNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see CompNode#dump()
	 */
	public String dump() {
		if(functionNode!=null)
			return "SUM("+functionNode.dump()+")";
		else return("SUM");
	}

	/**
	 * @param node
	 */
	public void setFunctionNode(FunctionNode node) {
		functionNode=node;
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompNode#collectPatterns(java.util.ArrayList)
	 */
	public boolean collectPatterns(ArrayList patternCollection) {
		//System.out.println("S");
		functionNode.collectPatterns(patternCollection);
		return true;
	}
	
	public CompNode optimizeTree(EvaluationNode eval, CompNode parent) {
		return this;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computePartial(fr.pasteur.sysbio.rdfscape.computing.EvaluationNode, java.util.Hashtable, java.util.Hashtable, java.util.ArrayList[])
	 */
	public boolean computePartial(EvaluationNode eval, Hashtable geneMapper, Hashtable conditionMapper, ArrayList[] context) {
		functionNode.setFromSUM(true);
		return functionNode.computePartial(eval,geneMapper, conditionMapper, context);
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computeTotal(double[][], boolean)
	 */
	public double computeTotal(double[][] expressionData) {
		double result=0;
		double[] myResultToAggregate=functionNode.computeTotalVector(expressionData);
		for (int i = 0; i < myResultToAggregate.length; i++) {
			result=result+myResultToAggregate[i];
		}
		System.out.println("Computing SumNode: "+result);
		return result;
	}

	
}
