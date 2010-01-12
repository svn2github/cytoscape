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
public class ExpNode implements CompNode,CompValueNode {
	double exp=1;
	String doubleString="1";
	CompNode atomNode=null;
	
	/**
	 * 
	 */
	public ExpNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see CompNode#dump()
	 */
	public String dump() {
		String answer=new String();
		answer=answer.concat("exp(");
		if(atomNode!=null) answer=answer.concat(atomNode.dump());
		answer=answer.concat(")^"+doubleString);
		return answer;
	}
	public void setExp(double e) {
		exp=e;
	}

	/**
	 * @param atom
	 */
	public void addValueNode(CompNode atom) {
		atomNode=atom;
		
	}

	/**
	 * @param token
	 */
	public void setExpString(String token) {
		doubleString=token;
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompNode#collectPatterns(java.util.ArrayList)
	 */
	public boolean collectPatterns(ArrayList patternCollection) {
		//System.out.println("Ex");
		atomNode.collectPatterns(patternCollection);
		return true;
	}
	public CompNode optimizeTree(EvaluationNode eval,CompNode cp) {
		atomNode.optimizeTree(eval,cp);
		if(doubleString.equals("1")) {
			return atomNode;
		}
		else return this;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computePartial(fr.pasteur.sysbio.rdfscape.computing.EvaluationNode, java.util.Hashtable, java.util.Hashtable, java.util.ArrayList[])
	 */
	public boolean computePartial(EvaluationNode eval, Hashtable geneMapper, Hashtable conditionMapper, ArrayList[] context) {
		Double tempDouble=new Double(doubleString);
		exp=tempDouble.doubleValue();
		
		return ((CompValueNode)atomNode).computePartial(eval, geneMapper, conditionMapper, context);
		
		
		
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computeTotal(double[][], boolean)
	 */
	public double computeTotal(double[][] expressionData) {
		double result=((CompValueNode)atomNode).computeTotal(expressionData);
		if(exp==1) ;
		else result=Math.pow(result,exp);
		System.out.println("ExpValue: "+result);
		return result;
	}
	
}
