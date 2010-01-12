/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.computing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;

import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.computing.builtinfunctions.FunctionManager;
import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;

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
public class EvaluationNode implements CompNode{
	String name=null;
	
	PatternNode patternNode=null;
	CompNode expressionNode=null;
	boolean hasErrors=false;
	String errorString;
	FunctionManager myFunctionManager=null;
	
	//We need something to compute values....
	String[] bitePattern;			//f(?x ?y ?z)   this pattern
	String[][] otherPatterns; 		// =NUM(?x ?y) + SUM(VAR(?z)) this part
	
		
	ArrayList otherPatternsTemp;	
	
	Hashtable biteToPatternHash=null;	// xx yy zz -> {zz yy-> ; zz ->  }
	
	//results
	PatternEvaluatedTable myOutputTable;
	
	boolean patternIndependentEvaluationResult=false;
	boolean randomIndependentEvaluationResult=false;
	
	
	int optAction=0;
	int optStep=0;
	/**
	 * @param myRDFScapeInstance
	 * 
	 */
	public EvaluationNode() {
		super();
		otherPatternsTemp=new ArrayList();
		
		
	}

	public String dump() {
		if(patternNode!=null && expressionNode!=null)
			return "Eval("+patternNode.dump()+")="+expressionNode.dump();
		else return "";
	}

	/**
	 * @param expressionName
	 */
	public void setName(String expressionName) {
		name=expressionName;
		
	}

	/**
	 * @param myPattern
	 */
	public void setPatternNode(PatternNode myPattern) {
		patternNode=myPattern;
		
	}

	/**
	 * @param myExpression
	 */
	public void setExpressionNode(CompNode myExpression) {
		expressionNode=myExpression;
		
	}

	

	/**
	 * @return Returns the hasErrors.
	 */
	public boolean hasErrors() {
		return hasErrors;
	}
	/**
	 * @param hasErrors The hasErrors to set.
	 */
	public void setHasErrors(boolean bv) {
		this.hasErrors = bv;
	}

	/**
	 * @param globalErrorString
	 */
	public void setErrorString(String globalErrorString) {
		errorString=globalErrorString;
		
	}

	/**
	 * @return
	 */
	public String getGlobalErrorString() {
		return errorString;
		
	}
	
	public boolean setupPatternIndependentEvaluation() {
		if(patternNode==null || expressionNode==null) {
			System.out.println("There is not a valid function to evaluate");
			hasErrors=true;
			return false;
		}
		
		System.out.println("Begin pattern setup");	//test
		
		bitePattern=patternNode.getPatternVariablesArray();
		System.out.print("Byte pattern: ");		//test
		dumpStringArray(bitePattern); 			//test
		
		collectPatterns(otherPatternsTemp);
		otherPatterns=new String[otherPatternsTemp.size()][];
		int k=0;
		for (Iterator iter = otherPatternsTemp.iterator(); iter.hasNext();) {
			String[] tpattern= (String[]) iter.next();
			otherPatterns[k]=tpattern;
			System.out.println("Pattern #"+k);	//test
			dumpStringArray(tpattern);			//test
			k++;
		}
		
		
		
		
		
		/*We have some problem with recursion & optimization: when we skip a sum 
		 * (only one term) things loops, probaly for nodes down nodes up. Possibly also in other
		 * parts of the tree.
		 */
		
		//optimizeTree();
		
		//setPatternMatches(inputTable);
		
		patternIndependentEvaluationResult=true;
		return true;
	}
	
	
	
	public void optimizeTree() {
		System.out.println("Ostart");
		if(hasErrors) {
			System.out.println("Can't optimize... ");
			return;
		}
		int oldOptCount=optAction;
		expressionNode.optimizeTree(this,null);
		optStep++;
		System.out.println("----1------OPT------1----");
		expressionNode.dump();
		while(optAction>oldOptCount) {
			oldOptCount=optAction;
			expressionNode.optimizeTree(this,null);
			optStep++;
			System.out.println("-----------OPT------------");
			expressionNode.dump();
		}
		System.out.println("Optimization ended after "+optStep+ " iteractions ("+optAction+" actions)");
		
	}
	
	public CompNode optimizeTree(EvaluationNode eval,CompNode cp) {
		return this;
	}
	
	
	
	

	/**
	 * @param otherPatternsTemp2
	 */
	public boolean collectPatterns(ArrayList me) {
		System.out.println("Pattern Collection Begin");
		expressionNode.collectPatterns(me);
		System.out.println("Pattern Collection End");
		return true;
	}

	/**
	 * @param myResultTable
	 */
	public void setPatternMatches(AbstractQueryResultTable myInputTable) {
		if(!patternIndependentEvaluationResult) return;
		//This is an overapproximation, the real table is smaller and equal to the number of bitePatterns
		myOutputTable=new PatternEvaluatedTable(myInputTable.getRowCount());
		Hashtable indexsOfVarsFromString=new Hashtable();
		
		//get the indexes of variables in the table
		System.out.println("Establishing correspondence between variable and indexes in table");
		for (int i = 0; i < myInputTable.getColumnCount(); i++) {
			indexsOfVarsFromString.put("?"+myInputTable.getColumnName(i),new Integer(i));
			System.out.println("index :"+myInputTable.getColumnName(i)+"->"+i);
		}
		
		int[] bitePatternIndexes=new int[bitePattern.length];
		//Check that we can satisfy the query... (and make up index)
		System.out.println("Looking for pattern bite variables...");
		for (int i = 0; i < bitePattern.length; i++) {
			System.out.print(bitePattern[i]+" ");
			if(indexsOfVarsFromString.containsKey(bitePattern[i])) {
				System.out.println("OK ("+((Integer)indexsOfVarsFromString.get(bitePattern[i])).intValue()+")");
				bitePatternIndexes[i]=((Integer)indexsOfVarsFromString.get(bitePattern[i])).intValue();
			}
			else {
				System.out.println("UNKNOWN");
				hasErrors=true;
				return;
			}
		}
		//Check that we can satisfy all patterns... (and make up indexes)
		int[][] otherPatternsIndexesArray=new int[otherPatterns.length][]; 
		System.out.println("Looking for other variables...");
		for (int i = 0; i < otherPatterns.length; i++) {
			otherPatternsIndexesArray[i]=new int[otherPatterns[i].length];
			for (int j = 0; j < otherPatterns[i].length; j++) {
				System.out.print(otherPatterns[i][j]+" ");
				if(indexsOfVarsFromString.containsKey(otherPatterns[i][j])) {
					System.out.println("OK ("+((Integer)indexsOfVarsFromString.get(otherPatterns[i][j])).intValue()+")");
					otherPatternsIndexesArray[i][j]=((Integer)indexsOfVarsFromString.get(otherPatterns[i][j])).intValue();
				}
				else {
					System.out.println("UNKNOWN");
					hasErrors=true;
					return;
				}
			}
		}
		
		
		System.out.println("indexes and indexes and indexes setup....");
		biteToPatternHash=new Hashtable();
		int currentOutputRow=0;
		
		//ArrayList tempBiteList=new ArrayList();
		
		Hashtable biteToCheckIfSeenPattern=new Hashtable();
		for(int row=0; row<myInputTable.getRowCount();row++) {
			System.out.println("Starting table analysis for row: "+row);
			String myBite=new String();
			for (int k = 0; k < bitePatternIndexes.length; k++) {
				myBite=myBite.concat(myInputTable.getURI(row,bitePatternIndexes[k]));
				
			}
			System.out.println("Bite: "+myBite);
			/* here we have the key, for each key we associate an hashmap with
			 * patterns as keys
			 */
			
			//First time we find this key
			if(biteToPatternHash.get(myBite)==null) {
				System.out.println("First time for Bite: "+myBite);
				currentOutputRow++;
				ArrayList[] tempList=new ArrayList[otherPatterns.length];
				HashSet[] patternSeen=new HashSet[otherPatterns.length];
				for (int x = 0; x < otherPatterns.length; x++) {
					tempList[x]=new ArrayList();
					patternSeen[x]=new HashSet();
				}
				biteToPatternHash.put(myBite,tempList);
				biteToCheckIfSeenPattern.put(myBite,patternSeen);
			}
			//if we already know the key...
			ArrayList[] tempList=(ArrayList[])(biteToPatternHash.get(myBite));
			HashSet[] patternSeen=(HashSet[])(biteToCheckIfSeenPattern.get(myBite));
			System.out.println("Recovering "+myBite);
			for (int x = 0; x < tempList.length; x++) {
				String[] patternEvaluated=new String[otherPatterns[x].length];
				String patternBite=new String();
				for (int z = 0; z < patternEvaluated.length; z++) {
					patternEvaluated[z]=(myInputTable.getURI(row,otherPatternsIndexesArray[x][z]));
					patternBite=patternBite.concat(patternEvaluated[z]);
				}
				System.out.print("Considering pattern: ");
				dumpStringArray(patternEvaluated);
				System.out.println("with bite: "+patternBite);
				
				if(!patternSeen[x].contains(patternBite)) {
					patternSeen[x].add(patternBite);
					tempList[x].add(patternEvaluated);
					System.out.println("new");
				}
				else {
					System.out.println("Duplicated");
					
				}
			}
			
			
			
			myInputTable.setBite(myBite,row);
			//myOutputTable.setValueAt(myBite,currentOutputRow,0);
			
		}
		myOutputTable=new PatternEvaluatedTable(biteToPatternHash.keySet().size());
		int k=0;
		for (Iterator iter = biteToPatternHash.keySet().iterator(); iter.hasNext();) {
			String myBite = (String) iter.next();
			myOutputTable.setValueAt(myBite,k,0);
			k++;
		}
		
		/*
		 * Test begin
		 */
		for (Iterator iter = biteToPatternHash.keySet().iterator(); iter.hasNext();) {
			String bite = (String) iter.next();
			System.out.println("Bite: "+bite);
			ArrayList[] patterrns=(ArrayList[]) biteToPatternHash.get(bite);
			for (int i = 0; i < patterrns.length; i++) {
				System.out.println("   Pattern: #"+i);
				for (Iterator iterator = patterrns[i].iterator(); iterator
						.hasNext();) {
					String[] pattern = (String[]) iterator.next();
					
					for (int j = 0; j < pattern.length; j++) {
						System.out.println("     "+pattern[j]);
					}
					
				}
			}
			
		}
		
		
		
		/* Test end
		 * 
		 */
		
	}

	/**
	 * @return
	 */
	public PatternEvaluatedTable getDataResultTable() {
		return myOutputTable;
	
	}
	
	public boolean computeValues(int randomNumber, String randomMode) {
		if(hasErrors) return false;
		myFunctionManager=new FunctionManager();
		System.out.println("Going to compute "+myOutputTable.getRowCount()*randomNumber+1+" trees");
		JProgressBar myProgress=new JProgressBar();
		myProgress.setBorder(new TitledBorder("Computing"));
		myProgress.setStringPainted(true);
		myProgress.setMinimum(0);
		myProgress.setMaximum(myOutputTable.getRowCount()*randomNumber+1);
		JOptionPane.showMessageDialog(null,myProgress);
		
		int progc=0;
		for(int currentBiteNumber=0;currentBiteNumber<myOutputTable.getRowCount();currentBiteNumber++) {
			String currentBite=(String)myOutputTable.getValueAt(currentBiteNumber,0);
			System.out.println("Eval: "+currentBite);
			Hashtable genesMapping=RDFScape.getCytoMapper().getGenesMapping();
			Hashtable conditionsMapping=RDFScape.getCytoMapper().getConditionMappings();	
			double[][] values=RDFScape.getCytoMapper().getDataMatrix();
			if(((CompValueNode)expressionNode).computePartial(this, genesMapping, conditionsMapping, (ArrayList[])(biteToPatternHash.get(currentBite)))) {
				double myvalue=((CompValueNode)expressionNode).computeTotal(values);
				System.out.println("==="+myvalue);
				myOutputTable.setValueAt(new Double(myvalue),currentBiteNumber,1);
			}
			else {
				//myOutputTable.setValueAt(new String("NC"),currentBiteNumber,1);
			}
			progc++;
			myProgress.setValue(progc);
		}
	
		return true;
	}
	
	//test
	private void dumpStringArray(String[] s) {
		for (int i = 0; i < s.length; i++) {
			System.out.print(s[i]+" ");
		}
		System.out.println();
	}
	
	//end test
	
	
}
