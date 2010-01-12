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
public class PatternNode implements CompNode {
	String[] myVariables=null;
	/**
	 * 
	 */
	public PatternNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see compNode#dump()
	 */
	public String dump() {
		if(myVariables==null) return "Pattern";
		String answer=new String();
		answer=answer.concat("Pattern(");
		for (int i = 0; i < myVariables.length; i++) {
			if(myVariables[i]!=null) answer=answer.concat(" "+myVariables[i]);
		}
		answer=answer.concat(")");
		return answer;
	}

	/**
	 * @param patternVars
	 */
	public void addVariables(ArrayList patternVars) {
		myVariables=new String[patternVars.size()];
		int k=0;
		for (Iterator iter = patternVars.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			myVariables[k]=element;
			k++;
		}
		
	}

	/**
	 * @return
	 */
	public String[] getPatternVariablesArray() {
		return myVariables;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompNode#collectPatterns(java.util.ArrayList)
	 */
	public boolean collectPatterns(ArrayList patternCollection) {
		return false;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompNode#optimizeTree(fr.pasteur.sysbio.rdfscape.computing.EvaluationNode, fr.pasteur.sysbio.rdfscape.computing.CompNode)
	 */
	public CompNode optimizeTree(EvaluationNode eval, CompNode parent) {
		//don't know what to optimize
		return this;
	}



}
