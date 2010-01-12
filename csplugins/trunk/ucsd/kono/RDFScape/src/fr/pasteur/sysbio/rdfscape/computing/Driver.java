/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.computing;
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
public class Driver {
	static Parser myParser=null;
	static String rule;
	static String rule2;
	static String rule3;
	static String rule4;
	static String rule5;
	static String rule6;
	/**
	 * 
	 */
	public Driver() {
		super();
		rule="varianzaperpattern(?x)=variance(?y ?z)+3*Sum(correlation(?x)/Number(?x))+(1-connectivity(?x))";
	}

	public static void main(String[] args) {
		rule="varianzaperpattern(?x ?y ?z)=variance(?y ?z)+3*SUM(correlation(?x))/NUM(?x))+(1-connectivity(?x))";
		rule2="test(?x ?y ?z)=(4)*((5+6)^7)-2";
		rule3="test(?x)=1+2+3";
		rule4="test(?x)=(1+2+3)";
		rule5="test(?z)=1+2*4^6+4*9*8/6+4^2-2*8";
		myParser=new Parser();
		EvaluationNode myNode=myParser.parse(rule);
		System.out.println(myNode.dump());
	}
}
