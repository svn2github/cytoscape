/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.computing;
import java.util.ArrayList;

import fr.pasteur.sysbio.rdfscape.RDFScape;

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
public class Parser {
	String token;
	String fullRule;
	String remainingRule;
	String errorMessage;
	String globalErrorString;
	boolean hasErrors=false;
	
	/**
	 * 
	 */
	public Parser() {
		super();
		globalErrorString=new String();
		
	}
	
	

	/**
	 * @param rule
	 */
	public EvaluationNode  parse(String rule) {
		EvaluationNode myEvaluationNode=new EvaluationNode();
		fullRule=rule;
		remainingRule=rule;
		token=getNextToken();
		
		String expressionName=null;
		PatternNode myPatternNode=null;
		CompNode expRoot=null;
		//System.out.println(token + tokenIsString(token)+tokenIsAggregator(token));
		if(tokenIsString(token) && ! tokenIsAggregator(token)) {
			expressionName=token;
			token=getNextToken();
			if(!token.equals("(")) error("Expecting \"(\"");
			token=getNextToken();
			myPatternNode=parsePattern();
			if(!token.equals(")")) error("xpecting \")\"");
			token=getNextToken();
			if(!token.equals("=")) error("Expecting \"=\"");
			token=getNextToken();
			expRoot=parseTerms();
		}
		else error("Expecting an identifier and not an aggregator");
		
		
		myEvaluationNode.setName(expressionName);
		myEvaluationNode.setPatternNode(myPatternNode);
		myEvaluationNode.setExpressionNode(expRoot);
		
		
		System.out.println("ENDFILE");
		if(hasErrors) {
			myEvaluationNode.setHasErrors(true);
			myEvaluationNode.setErrorString(globalErrorString);
		}
		return myEvaluationNode;
	}
	
	private CompNode parseTerms() {
		//System.out.println("Parse terms "+token+" ["+remainingRule+ "]");
		TermsNode myNode=new TermsNode();
		FactorNode firstTerm=parseFactors();
		myNode.add(firstTerm,"+");
		while(token.equals("+") || token.equals("-")) {
			String op=token;
			token=getNextToken();
			FactorNode tempnode=parseFactors();
			myNode.add(tempnode,op);
		}
		return myNode;
	}
	
	private FactorNode parseFactors() {
		//System.out.println("Parse factors "+token+" ["+remainingRule+ "]");
		FactorNode myNode=new FactorNode();
		ExpNode firstFactor=parseExps();
		myNode.add(firstFactor,"*");
		while(token.equals("*") || token.equals("/")) {
			String op=token;
			token=getNextToken();
			ExpNode tempnode=parseExps();
			myNode.add(tempnode,op);
		}
		return myNode;
		
	}
	
	private ExpNode parseExps() {
		//System.out.println("Parse exps "+token+" ["+remainingRule+ "]");
		ExpNode myNode=new ExpNode();
			CompNode atom=parseAtom();
			myNode.addValueNode(atom);
			if(token.equals("^")) {
				token=getNextToken();
				if(!tokenIsNumber(token)) error("^ requires a number!");
				else {
					myNode.setExpString(token);
					token=getNextToken();
				}
			}
		
		return myNode;
	}
	
	private CompNode parseAtom() {
		//System.out.println("Parse atom "+token+" ["+remainingRule+ "]");
		CompNode myNode=null;
		if(tokenIsNumber(token)) {
			myNode=new ValueNode();
			((ValueNode)myNode).setValueString(token);
			token=getNextToken();
		}
		else if(token.equals("SUM")) {
			token=getNextToken();
			if(token.equals("(")) token=getNextToken();
			else error("Expecting \"(\"");
			myNode=new SumNode();
			((SumNode)myNode).setFunctionNode(parseFunction());
			
			if(token.equals(")")) token=getNextToken();
			else error("Expecting \")\"");
		}
		else if(token.equals("NUM")) {
			token=getNextToken();
			if(token.equals("(")) token=getNextToken();
			else error("Expecting \"(\"");
			myNode=new NumNode();
			((NumNode)myNode).setPatternNode(parsePattern());
			
			if(token.equals(")")) token=getNextToken();
			else error("Expecting \")\"");
		}
		else if(tokenIsString(token)) {
			myNode=parseFunction();
		}
		else if(token.equals("(")) {
			token=getNextToken();
			myNode=parseTerms();
			if(token.equals(")")) token=getNextToken();
			else error("Expecting \")\"");
		}
		else error("Expecting SUM,NUM,[Attribute name or Built in function],\"(\"");
		return myNode;
	}

	private FunctionNode parseFunction() {
		FunctionNode myNode=new FunctionNode();
		if(!tokenIsString(token)) error("Expecting a function name");
		
		else {
			myNode.setFunctionName(token);
			token=getNextToken();
			if(token.equals("(")) token=getNextToken();
			else error("Expecting \"(\"");
			myNode.SetPatternNode(parsePattern());
			if(token.equals(")")) token=getNextToken();
			else error("Expecting \")\"");
		}
		return myNode;
	}
	
	
	/*
	private void parseExpression() {
		System.out.println("Parse exp "+token);
		ArrayList terms=new ArrayList();
		ExpressionNode myExpNode=new ExpressionNode();
		TermsNode myAdditionNode=new TermsNode(); 
		if(token.equals("(")) {
			token=getNextToken();
			myExpNode=parseExpression();
			if(token.equals(")")) token=getNextToken();
			else error("Missing \")\"");
		}
		else {
			
			myAdditionNode=parseTerm();
			myExpNode.addNode(myAdditionNode);
		}
		
		return myExpNode;
	}
	
	
	private TermsNode parseTerm() {
		System.out.println("Parse Term "+token);
		TermsNode myAdditionNode=new TermsNode();
		if(token.equals("(")) {
			token=getNextToken();
			compNode myNode=parseExpression();
			myAdditionNode.add(myNode,"+");
			if(token.equals(")")) token=getNextToken();
			else error("Missing \")\"");
		}
		else if(tokenIsStringOrNumber(token)) {
			compNode myNode=parseFactor();
			myAdditionNode.add(myNode,"+");
			
			while(token.equals("+") || token.equals("-")) {
				String op=token;
				token=getNextToken();
				compNode myNode2=parseFactor();
				myAdditionNode.add(myNode2,op);
			}
		}
		
		else ;
		return myAdditionNode;
	}
	
	private FactorNode parseFactor() {
		
		FactorNode myFactorNode=new FactorNode();
		System.out.println("Parse Factor "+token);
		if(token.equals("(")) {
			token=getNextToken();
			parseExpression();
			if(token.equals(")")) token=getNextToken();
			else error("Missing \")\"");
		}
		else if(tokenIsStringOrNumber(token)) {
			parseExp();
			while(token.equals("*") || token.equals("/")) {
				token=getNextToken();
				parseExp();
			}
		}
		
		else ;
		return myFactorNode;
	}
	
	private void parseExp() {
		System.out.println("Parse Exp "+token);
		if(token.equals("(")) {
			token=getNextToken();
			parseExpression();
			if(token.equals(")")) token=getNextToken();
			else error("Missing \")\"");
		}
		else if(tokenIsStringOrNumber(token)) {
			parseFunc();
			if(token.equals("^")) {
				token=getNextToken();
				if(!tokenIsNumber(token)) error("^ has only a nmber as argument");
			}
		}
		
	}
	
	private void parseFunc() {
		System.out.println("Parse Func "+token);
		if(tokenIsNumber(token)) {
			System.out.println("Number");
			token=getNextToken();
		}
		else if(tokenIsAggregator(token)) {
			token=getNextToken();
			if(token.equals("(")) {
				token=getNextToken();
				parseExpression();
				if(token.equals(")")) token=getNextToken();
				else error("Missing \")\"");
			}
		}
		else parseFunction();
		
	}
	
	private void parseFunction() {
		if(tokenIsString(token)) {
			token=getNextToken();
			if(token.equals("(")) {
				token=getNextToken();
				parsePattern();
				if(token.equals(")")) token=getNextToken();
				else error("Missing \")\"");
			}
		}
	}
	
	*/
	private PatternNode parsePattern() {
		ArrayList patternVars=new ArrayList();
		PatternNode myPatternNode=new PatternNode();
		
		if(!tokenIsVar(token)) {
			error("Expecting a variable");
			return myPatternNode;
		}
		else {
			patternVars.add(token.trim());
			token=getNextToken();
			while(tokenIsVar(token)) {
				patternVars.add(token.trim());
				token=getNextToken();
			}
		}
		myPatternNode.addVariables(patternVars);
		return myPatternNode;
		
	}
	
	
	private String getNextToken() {
		String myToken="";
		remainingRule=remainingRule.trim();
		int i=remainingRule.length();
		if(i==0) return myToken;
		if(remainingRule.indexOf(" ")>=0 && remainingRule.indexOf(" ")<i) i=remainingRule.indexOf(" ");
		if(remainingRule.indexOf("=")>=0 && remainingRule.indexOf("=")<i) i=remainingRule.indexOf("=");
		if(remainingRule.indexOf("(")>=0 && remainingRule.indexOf("(")<i) i=remainingRule.indexOf("(");
		if(remainingRule.indexOf(")")>=0 && remainingRule.indexOf(")")<i) i=remainingRule.indexOf(")");
		if(remainingRule.indexOf("+")>=0 && remainingRule.indexOf("+")<i) i=remainingRule.indexOf("+");
		if(remainingRule.indexOf("-")>=0 && remainingRule.indexOf("-")<i) i=remainingRule.indexOf("-");
		if(remainingRule.indexOf("*")>=0 && remainingRule.indexOf("*")<i) i=remainingRule.indexOf("*");
		if(remainingRule.indexOf("/")>=0 && remainingRule.indexOf("/")<i) i=remainingRule.indexOf("/");
		if(remainingRule.indexOf("^")>=0 && remainingRule.indexOf("^")<i) i=remainingRule.indexOf("^");
		if(i==0) {
			myToken=remainingRule.substring(0,1);
			remainingRule=remainingRule.substring(1,remainingRule.length());
		}
		else {
			myToken=remainingRule.substring(0,i);
			remainingRule=remainingRule.substring(i,remainingRule.length());
		}
		return myToken;
	}
	
	private boolean tokenIsStringOrVar(String myToken) {
		boolean answer=tokenIsStringOrNumber(myToken);
		boolean isNumber=true;
		try {
			
			Double.parseDouble(myToken);
		} catch (Exception e) {isNumber=false; };
		return answer&&!isNumber;
	}
	private boolean tokenIsStringOrNumber(String myToken) {
		boolean answer=true;
		if(myToken.indexOf("=")>=0) answer=false; 
		if(myToken.indexOf("(")>=0) answer=false;  
		if(myToken.indexOf(")")>=0) answer=false;  
		if(myToken.indexOf("+")>=0) answer=false;  
		if(myToken.indexOf("-")>=0) answer=false;  
		if(myToken.indexOf("*")>=0) answer=false;  
		if(myToken.indexOf("/")>=0) answer=false;  
		if(myToken.indexOf("^")>=0) answer=false;  
		return answer;
	}
	
	private boolean tokenIsString(String myToken) {
		return tokenIsStringOrVar(myToken)&&(myToken.indexOf("?")!=0);
	}
	
	private boolean tokenIsNumber(String myToken) {
		boolean answer=true;
		try {
			Double.parseDouble(myToken);
		} catch (Exception e) {answer=false; };
		return answer;
	}
	private boolean tokenIsAggregator(String myToken) {
		boolean answer=false;
		if(myToken.equals("SUM")) answer=true;
		if(myToken.equals("NUM")) answer=true;
		return answer;
	}
	
	private void error(String er) {
		globalErrorString=globalErrorString.concat("For token "+token + " Error: "+er+" near "+remainingRule+"\n");
		System.out.println("For token "+token + " Error: "+er+" near "+remainingRule);
		hasErrors=true;
		return;
	}
	
	private boolean tokenIsVar(String myToken) {
		boolean answer=true;
		if(!tokenIsStringOrVar(myToken)) answer=false;
		if(myToken.indexOf("?")!=0) answer=false;
		return answer;
	}
	
	
}
