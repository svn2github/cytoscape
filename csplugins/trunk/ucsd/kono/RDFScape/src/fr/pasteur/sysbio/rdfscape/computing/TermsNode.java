/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.computing;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

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
public class TermsNode implements CompNode,CompValueNode {
	ArrayList myTerms=new ArrayList();
	ArrayList myOperations=new ArrayList();
	/**
	 * 
	 */
	public TermsNode() {
		super();
		
	}

	/* (non-Javadoc)
	 * @see compNode#dump()
	 */
	public String dump() {
		String answer=new String();
		answer=answer.concat("Addition(");
		for (int i = 0; i < myTerms.size(); i++) {
			answer=answer.concat((String)myOperations.get(i));
			answer=answer.concat(((CompNode)myTerms.get(i)).dump()+" ");
			
		}
		answer=answer.concat(")");
		return answer;
	}

	

	/**
	 * @param myNode
	 * @param string
	 */
	public void add(CompNode myNode, String string) {
		//System.out.println("ADD");
		myTerms.add(myNode);
		myOperations.add(string);
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompNode#collectPatterns(java.util.ArrayList)
	 */
	public boolean collectPatterns(ArrayList patternCollection) {
		//System.out.println("T");
		for (Iterator iter = myTerms.iterator(); iter.hasNext();) {
			CompNode child = (CompNode) iter.next();
				child.collectPatterns(patternCollection);
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompNode#optimizeTree(fr.pasteur.sysbio.rdfscape.computing.EvaluationNode, fr.pasteur.sysbio.rdfscape.computing.CompNode)
	 */
	public CompNode optimizeTree(EvaluationNode eval, CompNode parent) {
		//System.out.println("OT");
		/*
		for (int i=0 ; i<myTerms.size(); i++) {
			System.out.println(i+);
			CompNode child = (CompNode) myTerms.get(i);
			optimizeTree(eval,child);
			
		}
		System.out.println("+");
		*/
		/*
		 * Here we should add a step to pre-compute products
		 */
		
		if(myTerms.size()==1) {
			eval.optAction++;
			return (CompNode)myTerms.get(0);
		}
		else return this;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computePartial(fr.pasteur.sysbio.rdfscape.computing.EvaluationNode, java.util.Hashtable, java.util.Hashtable, java.util.ArrayList[])
	 */
	public boolean computePartial(EvaluationNode eval, Hashtable geneMapper, Hashtable conditionMapper, ArrayList[] context) {
		boolean answer=true;
		//System.out.println("T");
		for (Iterator iter = myTerms.iterator(); iter.hasNext();) {
			
			
			CompValueNode child = (CompValueNode) iter.next();
				answer=answer&&child.computePartial(eval, geneMapper, conditionMapper,context);
		}
		return answer;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computeTotal(double[][], boolean)
	 */
	public double computeTotal(double[][] expressionData) {
		double result=0;
		
		
		for (int i=0;i<myTerms.size(); i++) {
			CompValueNode child = (CompValueNode) myTerms.get(i);
			String myOp=(String)(myOperations.get(i));	
			if(myOp.equals("+")) result=result+child.computeTotal(expressionData);
			if(myOp.equals("-")) result=result-child.computeTotal(expressionData);
			System.out.println("Computing TermsNode: "+result);
		}
		return result;
		
	}

}
