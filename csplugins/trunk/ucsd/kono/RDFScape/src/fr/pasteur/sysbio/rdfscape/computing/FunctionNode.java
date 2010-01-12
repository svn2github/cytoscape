/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.computing;

import java.util.ArrayList;
import java.util.Hashtable;

import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.computing.builtinfunctions.PatternDataFunction;

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
public class FunctionNode implements CompNode,CompValueNode {
	String functionName="";
	PatternNode patternNode=null;
	//Something to make computations here
	int indexInPatterns=0;
	String[] myObjectPattern=null;
	PatternDataFunction myFunction=null;
	int[][] dataIndexBlock=null;
	double[] myAttributeValues=null;
	boolean isBuiltIn=false;
	boolean isCytoValue=false;
	boolean isFromSUM=false;
	
	/**
	 * 
	 */
	public FunctionNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see CompNode#dump()
	 */
	public String dump() {
		if(patternNode!=null) return functionName+"("+patternNode.dump()+")";
		else return("()");
	}

	/**
	 * @param node
	 */
	public void SetPatternNode(PatternNode node) {
		patternNode=node;
		
	}

	/**
	 * @param token
	 */
	public void setFunctionName(String token) {
		functionName=token;
		
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompNode#collectPatterns(java.util.ArrayList)
	 */
	public boolean collectPatterns(ArrayList patternCollection) {
		myObjectPattern=patternNode.getPatternVariablesArray();
		System.out.print("F:"+myObjectPattern);
		if(!patternCollection.contains(myObjectPattern)) patternCollection.add(myObjectPattern);
		indexInPatterns=patternCollection.indexOf(myObjectPattern);
		System.out.println(indexInPatterns);
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
		System.out.print("Function "+functionName+" first pass ");
		ArrayList myPatternList=context[indexInPatterns];
		if(myPatternList.size()==0) {
			System.out.println("Empty matches should be rare...");
			return false;
		}
		if(myPatternList.size()>1 && isFromSUM==false) {
			System.out.println("Need an aggregator for this function in this case...");
			return false;
		}
		if(eval.myFunctionManager.isBuiltInFunction(functionName)) {
			System.out.println("Found builtin");
			
			myFunction=eval.myFunctionManager.getFunction(functionName);
			if(myFunction!=null) System.out.println("Got function");
			dataIndexBlock=new int[myPatternList.size()][];
			int functionCardinality=myFunction.getCardinality();
			
			//Note: this should be moved in previous stage (compile time check!)
			if(functionCardinality!=eval.otherPatterns[indexInPatterns].length) {
				System.out.println("Wrong cardinality for this function");
				return false;
			}
			System.out.print("My pattern: ");
			for( int i=0;i<eval.otherPatterns[indexInPatterns].length;i++) {
				System.out.print(eval.otherPatterns[indexInPatterns][i]);
			}
			System.out.println();
				
			String[] currentPattern=null;
			for (int y = 0; y < myPatternList.size(); y++) {
				System.out.print("Pattern# "+y+" : ");
				currentPattern=(String[]) myPatternList.get(y);
				int[] patternIndex=new int[currentPattern.length];
				for (int i = 0; i < currentPattern.length; i++) {
					
					System.out.print(currentPattern[i]+" ");
					if(geneMapper.get(currentPattern[i])==null) {
						System.out.println("Unable to find data for var: "+currentPattern[i]);
						return false;
					}
					else {
						patternIndex[i]=((Integer)geneMapper.get(currentPattern[i])).intValue();
					}
				}
				dataIndexBlock[y]=patternIndex;
			}
			isBuiltIn=true;
		
		}
		else {
			System.out.println("Looking for an attribute");
			myAttributeValues=new double[myPatternList.size()];
			for (int y = 0; y < myPatternList.size(); y++) {
				System.out.print("Pattern# "+y+" : ");
				String[] currentPattern=(String[]) myPatternList.get(y);
				if(currentPattern.length==1) {
					System.out.println(currentPattern[0]+ " !");
					Double tempValue=RDFScape.getCytoscapeDealer().getNumericAttributeValueByURI(currentPattern[0],functionName);
					if(tempValue==null) { 
						System.out.println("Attribute not found!"); 
						return false;
					}
					else myAttributeValues[y]=tempValue;
					
					/*
					String myValue=RDFScape.getCytoscapeDealer().getNodeAttributeValueByURI(currentPattern[0],functionName);
					if(myValue==null) System.out.println("Attribute not found!");
					try{
						Double tempValue=new Double(myValue);
						myAttributeValues[y]=tempValue.doubleValue();
					} catch (Exception e) {
						System.out.println("Not a number...");
						return false;
					}
					*/
					System.out.println("Value="+myAttributeValues[y]);
					
				}
				else if(currentPattern.length==2) {
					System.out.println("Looking for an Edge atribute of type "+functionName+" for ("+currentPattern[0]+","+currentPattern[1]+")");
					Double tempValue=RDFScape.getCytoscapeDealer().getEdgeAttributeNumericValueByURI(currentPattern[0],currentPattern[1],functionName);
					if(tempValue==null) { 
						System.out.println("Attribute not found!"); 
						return false;
					}
					else myAttributeValues[y]=tempValue;
					/*
					 
					String myValue=RDFScape.getCytoscapeDealer().getEdgeAttributeValueByURI(currentPattern[0],currentPattern[1],functionName);
					if(myValue==null) System.out.println("Attribute not found!");
					try{
						Double tempValue=new Double(myValue);
						myAttributeValues[y]=tempValue.doubleValue();
					} catch (Exception e) {
						System.out.println("Not a number...");
						return false;
					}
					*/
					System.out.println("Value="+myAttributeValues[y]);
				}
				else {
					System.out.println("Hypernodes are not supported....");
					return false;
				}
				
				
			}
			isCytoValue=true;
		}
		
		// test begin
		if(dataIndexBlock!=null) {
			for (int i = 0; i < dataIndexBlock.length; i++) {
				for (int j = 0; j < dataIndexBlock[i].length; j++) {
					System.out.print(dataIndexBlock[i][j]+" ");
				}
				System.out.println();
			}
		}
		//test end
		
		return true;
	}

	/* (non-Javadoc)
	 * @see fr.pasteur.sysbio.rdfscape.computing.CompValueNode#computeTotal(double[][], boolean)
	 */
	public double computeTotal(double[][] expressionData) {
		if(isCytoValue) {
			System.out.println("FunctionValue: "+myAttributeValues[0]);
			return myAttributeValues[0];
			
		}
		else if(isBuiltIn) {
			double[] result=myFunction.evaluate( dataIndexBlock,expressionData);
			return result[0];
		}
		else {
			System.out.println("Panic!");
			return 0;
		}
		
	}
	public double[] computeTotalVector(double[][] expressionData) {
		if(isCytoValue) {
			return myAttributeValues;
		}
		else if(isBuiltIn) {
			double[] result=myFunction.evaluate( dataIndexBlock,expressionData);
			return result;
		}
		else {
			System.out.println("Panic!");
			return new double[0];
		}
	}

	/**
	 * @param b
	 */
	public void setFromSUM(boolean b) {
		isFromSUM=true;
		
	}

}
