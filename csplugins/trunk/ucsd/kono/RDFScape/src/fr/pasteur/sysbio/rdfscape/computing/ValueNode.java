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
public class ValueNode implements CompNode,CompValueNode {
	double value;
	String valueString=null;
	/**
	 * 
	 */
	public ValueNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see CompNode#dump()
	 */
	public String dump() {
		if(valueString!=null)
			return valueString;
		else return "";
	}
	/**
	 * @param token
	 */
	public void setValueString(String s) {
		valueString=s;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompNode#collectPatterns(java.util.ArrayList)
	 */
	public boolean collectPatterns(ArrayList patternCollection) {
		return false;
	}
	
	public CompNode optimizeTree(EvaluationNode eval,CompNode cp) {
		value=Double.valueOf(valueString).doubleValue();
		return this;
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computePartial(fr.pasteur.sysbio.rdfscape.computing.EvaluationNode, java.util.Hashtable, java.util.Hashtable, java.util.ArrayList[])
	 */
	public boolean computePartial(EvaluationNode eval, Hashtable geneMapper, Hashtable conditionMapper, ArrayList[] context) {
		try{
			Double tempValue=new Double(valueString);
			value=tempValue.doubleValue();
		} catch (Exception e) {
			System.out.println(valueString +" is not a number !");
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computeTotal(double[][], boolean)
	 */
	public double computeTotal(double[][] expressionData) {
		System.out.println("Computing ValueNode: "+value);
		return value;
	}
	
}
