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
public class FactorNode implements CompNode,CompValueNode {
	ArrayList myFactors=new ArrayList();
	ArrayList myOperations=new ArrayList();
	/**
	 * 
	 */
	public FactorNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see compNode#dump()
	 */
	public String dump() {
		String answer=new String();
		answer=answer.concat("Product(");
		for (int i = 0; i < myFactors.size(); i++) {
			answer=answer.concat((String)myOperations.get(i));
			answer=answer.concat(((CompNode)myFactors.get(i)).dump()+" ");
			
		}
		answer=answer.concat(")");
		return answer;
		
	}
	public void add(CompNode myNode, String string) {
		//System.out.println("ADD");
		myFactors.add(myNode);
		myOperations.add(string);
		
	}
	
	public boolean collectPatterns(ArrayList patternCollection) {
		//System.out.println("F");
		for (Iterator iter = myFactors.iterator(); iter.hasNext();) {
			CompNode child = (CompNode) iter.next();
				child.collectPatterns(patternCollection);
		}
		return true;
	}
	
	
	public CompNode optimizeTree(EvaluationNode eval, CompNode parent) {
		//System.out.println("OF");
		/*
		for (int i=0 ; i<myFactors.size(); i++) {
			CompNode child = (CompNode) myFactors.get(i);
			myFactors.set(i,optimizeTree(eval,child));	
			
		}
		*/
		/*
		 * Here we should add a step to pre-compute products
		 */
		
		if(myFactors.size()==1) {
			eval.optAction++;
			return (CompNode)myFactors.get(0);
		}
		else return this;
	}
	
	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computePartial(fr.pasteur.sysbio.rdfscape.computing.EvaluationNode, java.util.Hashtable, java.util.Hashtable, java.util.ArrayList[])
	 */
	public boolean computePartial(EvaluationNode eval, Hashtable geneMapper, Hashtable conditionMapper, ArrayList[] context) {
		boolean answer=true;
		//System.out.println("F");
		for (Iterator iter = myFactors.iterator(); iter.hasNext();) {
			CompValueNode child = (CompValueNode) iter.next();
				answer=answer&&child.computePartial(eval, geneMapper, conditionMapper,context);
		}
		return answer;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computeTotal(double[][], boolean)
	 */
	public double computeTotal(double[][] expressionData) {
		double result=1;
	
		for (int i=0; i< myFactors.size(); i++) {
			CompValueNode child = (CompValueNode) myFactors.get(i);
			String myOp=(String) (myOperations.get(i));	
			if(myOp.equals("*")) result=result*child.computeTotal(expressionData);
			if(myOp.equals("/")) result=result/child.computeTotal(expressionData);
		}
		System.out.println("FactorValue: "+result);
		return result;
	}
	

}
