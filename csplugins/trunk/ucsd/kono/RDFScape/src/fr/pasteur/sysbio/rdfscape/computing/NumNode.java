/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.computing;

import java.util.ArrayList;
import java.util.Hashtable;

/*
 * Created on Oct 15, 2005
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
public class NumNode implements CompNode,CompValueNode {
	PatternNode patternNode=null;
	int indexInPatterns=0;
	String[] myObjectPatterns=null;
	double myValue=0;
	
	/**
	 * 
	 */
	public NumNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see CompNode#dump()
	 */
	public String dump() {
		if(patternNode!=null) return "NUM("+patternNode.dump()+")";
		else return "NUM";
	}

	/**
	 * @param node
	 */
	public void setPatternNode(PatternNode node) {
		patternNode=node;
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompNode#collectPatterns(java.util.ArrayList)
	 */
	public boolean collectPatterns(ArrayList expressionPatternsCollection) {
		myObjectPatterns=patternNode.getPatternVariablesArray();
		if(!expressionPatternsCollection.contains(myObjectPatterns)) expressionPatternsCollection.add(myObjectPatterns);
		indexInPatterns=expressionPatternsCollection.indexOf(myObjectPatterns);
		System.out.println("P:"+indexInPatterns);
		return true;
	}
	public CompNode optimizeTree(EvaluationNode eval, CompNode parent) {
		//don't know what to optimize
		return this;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computePartial(fr.pasteur.sysbio.rdfscape.computing.EvaluationNode, java.util.Hashtable, java.util.Hashtable, java.util.ArrayList[])
	 */
	public boolean computePartial(EvaluationNode eval, Hashtable geneMapper, Hashtable conditionMapper, ArrayList[] context) {
		System.out.print("NUM Clause, first pass...");
		ArrayList myMatchesList=context[indexInPatterns];
		System.out.print (" got array ");
		myValue=myMatchesList.size();
		System.out.println("-> #="+myValue);
		return true;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computeTotal(double[][], boolean)
	 */
	public double computeTotal(double[][] expressionData) {
		System.out.println("Computing NumNode: "+myValue);
		return myValue;
	}

	
}
