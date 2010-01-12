/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jul 9, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.patterns;

import java.awt.Image;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.computing.EvaluationNode;
import fr.pasteur.sysbio.rdfscape.computing.Parser;
import fr.pasteur.sysbio.rdfscape.computing.PatternEvaluatedTable;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.JenaQueryResultTable;
import fr.pasteur.sysbio.rdfscape.query.RDQLQueryAnswerer;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PatternElement extends AbstractTableModel{
	private Image myImage=null;					//image of the pattern (Cytoscape snapshot)
	private String[][] myTriplesArray=null;
	private Hashtable preciseFilterConditions=null;
	private Hashtable filterConditions=null;
	
	
	
	
	//private String myRDQLquery=null;			//RDQL query correpsonding to the pattern
	//private String type=null;					
	private String name=null;					//Name
	private String formula=null;				//Associated evaluation function
	private EvaluationNode myRuleEvaluationNode=null;	//Root of the parsed avaluation function
	
	private String ontologyCondition=null;		//Condition to trigger the addition of rules
	//Ontology tree
	private int randomgeneration=0;
	private PatternElementPanel myPanel=null;	// myPanel
	
	PatternManager myPatternManager=null;
	
	private String selectClause=null;			//
	private String patternClause=null;			//
	private String andClause=null;				//
	private String namespaceClause=null;		//
	private String[] varList=null;				//
	
	private AbstractQueryResultTable tableOfPatternMatches=null;	//TableResults
	private boolean hasValidTableOfPatternMatches=false;
	private PatternEvaluatedTable patternEvaluatedTable=null;
	private boolean hasValidPatternEvaluatedTable=false;
	/**
	 * 
	 */
	public PatternElement(String pname,PatternManager pm) {
		super();
		name=pname;
		tableOfPatternMatches=new JenaQueryResultTable();
		preciseFilterConditions=new Hashtable();
		filterConditions=new Hashtable();
		myPatternManager=pm;
		
		
		
	}

	/**
	 * @return
	 */
	public Image getImage() {
		return myImage;
	}

	
	/**
	 * @return Returns the formula.
	 */
	public String getFormula() {
		return formula;
	}
	/**
	 * @param formula The formula to set.
	 */
	public void setFormula(String formula) {
		this.formula = formula;
		hasValidPatternEvaluatedTable=false;
	}
	/**
	 * @return Returns the myImage.
	 */
	public Image getMyImage() {
		return myImage;
	}
	/**
	 * @param myImage The myImage to set.
	 */
	public void setMyImage(Image myImage) {
		this.myImage = myImage;
	}
	
	
	/**
	 * @return Returns the myRDQLquery.
	 */
	//public String getMyRDQLquery() {
	//	return myRDQLquery;
	//}
	/**
	 * @param myRDQLquery The myRDQLquery to set.
	 */
	//public void setMyRDQLquery(String myRDQLquery) {
	//	this.myRDQLquery = myRDQLquery;
	//}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return Returns the type.
	 */
	/*
	public String getType() {
		return type;
	}
	*/
	/**
	 * @param type The type to set.
	 */
	/*
	public void setType(String type) {
		this.type = type;
	}
	*/
	/**
	 * @return Returns the myPanel.
	 */
	public PatternElementPanel getMyPanel() {
		return myPanel;
	}
	/**
	 * @return Returns the randomgeneration.
	 */
	public int getRandomgeneration() {
		return randomgeneration;
	}
	/**
	 * @return Returns the ontologyCondition.
	 */
	public String getOntologyCondition() {
		return ontologyCondition;
	}
	/**
	 * @param ontologyCondition The ontologyCondition to set.
	 */
	public void setOntologyCondition(String ontologyCondition) {
		this.ontologyCondition = ontologyCondition;
	}
	
	public PatternElementPanel getPanel() {
		if(myPanel==null) myPanel=new PatternElementPanel(this);
		return myPanel;
	}

	/**
	 * @return
	 */
	public String getSelectClause() {
		return selectClause;
	}
	/**
	 * @return
	 */
	public String getPatternClause() {
		return patternClause;
	}
	public String getWhereClause() {
		return "WHERE  "+getPatternClause();
	}
	
	/**
	 * @return
	 */
	public String getANDClause() {
		return andClause;
	}

	
	
	/**
	 * @return
	 */
	public String getNamespaceClause() {
		return namespaceClause;
	}

	/**
	 * @param queryNameSpacesClause
	 */
	public void setNamespaceClause(String queryNameSpacesClause) {
		namespaceClause=queryNameSpacesClause;
		
	}

	/**
	 * @param patterns
	 */
	/*
	public void addPatternCollection(ArrayList patterns) {
		hasValidPatternMatchedTable=false;
		hasValidPatternMatchedTable=false;
		//System.out.println("Incoming Size "+patterns.size());
		myTriplesArray=new String[patterns.size()][6];
		int k=0;
		HashSet vars=new HashSet();
		for (Iterator iter = patterns.iterator(); iter.hasNext();) {
			String[] element = (String[]) iter.next();
			for(int j=0;j<3;j++) {
				myTriplesArray[k][j]=element[j].trim();
				myTriplesArray[k][j+3]=element[j+3];
				if(myTriplesArray[k][j].indexOf("?")==0) {
					vars.add(myTriplesArray[k][j]);
					//System.out.println("Found var: "+myPatternList[k][j]);
				} 
			}
			k++;
		}
		
		//Vars
		varList=new String[vars.size()];
		k=0;
		for (Iterator iter = vars.iterator(); iter.hasNext();) {
			varList[k] = (String) iter.next();
			k++;
		}
		//Select clause
		selectClause=new String();
		selectClause=selectClause.concat("SELECT ");
		for (int i = 0; i < varList.length; i++) {
			selectClause=selectClause.concat(" "+varList[i]);
		}
		selectClause=selectClause.concat("\n");
		//System.out.println(selectClause+"!");
		
		//Pattern/AND clause
		patternClause=new String();
		HashSet andClauses=new HashSet();
		
		patternClause=patternClause.concat(" ");
		for (int i = 0; i < myTriplesArray.length; i++) {
			patternClause=patternClause.concat(" ( ");
			for(int j=0;j<3;j++) {
					if(myTriplesArray[i][j].indexOf("?")==0) {
						patternClause=patternClause.concat(" "+ myTriplesArray[i][j]);
						if(myTriplesArray[i][j+3]!=null)
							andClauses.add("AND "+myTriplesArray[i][j]+" =~ /"+myTriplesArray[i][j+3]+"/\n");
					}
					else patternClause=patternClause.concat(" <"+ myTriplesArray[i][j]+">");
					
			}
			patternClause=patternClause.concat(") \n");
		}
		andClause=new String();
		for (Iterator iter = andClauses.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			andClause=andClause.concat(element);
		}
		
		//System.out.println(patternClause+andClause+namespaceClause+"!");
	}
	*/
	/**
	 * 
	 */
	public void searchPattern(boolean searchOnly) {
		fillTableOfPatternMatches();
		System.out.println("Rows: "+tableOfPatternMatches.getRowCount());
		if(tableOfPatternMatches.getRowCount()==0) return;
		System.out.println("+");
		Hashtable varIndexes=new Hashtable();
		for (int i = 0; i < tableOfPatternMatches.getColumnCount(); i++) {
			varIndexes.put("?"+tableOfPatternMatches.getColumnName(i),new Integer(i));
			System.out.println("index :"+tableOfPatternMatches.getColumnName(i)+"->"+i);
			System.out.println(i);
		}
		System.out.println("->"+myTriplesArray.length);
		for(int row=0;row<tableOfPatternMatches.getRowCount();row++) {
			//for each pattern we "expand" the network...
			System.out.println("Answer row: "+row);
			for (int i = 0; i < myTriplesArray.length; i++) {
				System.out.println("Pattern row: "+i);
				String triple[]=new String[3];
				
				for (int j = 0; j < 3; j++) {
					System.out.println(">"+myTriplesArray[i][j]);
					if(myTriplesArray[i][j].indexOf("?")!=0) {
						triple[j]=myTriplesArray[i][j];
					}
					else {
						int index=( (Integer)(varIndexes.get(myTriplesArray[i][j])) ).intValue();
						System.out.println("->"+i);
						triple[j]=(String)tableOfPatternMatches.getValueAt(row,index);
					}
					
				}
				// TODO to revise!!!
				//if(!searchOnly) myPatternManager.addStatementToGraph(triple,type);
				//else myPatternManager.searchStatementInGraph(triple);
			}
		}
		
	}

	/**
	 * @param manager
	 */
	
	public void setPatternManager(PatternManager manager) {
		myPatternManager=manager;
		
	}

	/**
	 * TODO to revise!!! 
	 * @param text
	 */
	public  void addPatternFunction(String text) {
		hasValidPatternEvaluatedTable=false;
	
		Parser myParser=new Parser();
		myRuleEvaluationNode=myParser.parse(text);
		System.out.println(myRuleEvaluationNode.dump());
		if(myRuleEvaluationNode.hasErrors()) {
			RDFScape.warn("Wrong Syntax: "+myRuleEvaluationNode.getGlobalErrorString());
		}
		myRuleEvaluationNode.setupPatternIndependentEvaluation();
		System.out.println(myRuleEvaluationNode.dump()); //note: no opt now
		
		
	}

	/**
	 * 
	 */
	public PatternEvaluatedTable computeFunction(int number,String randomMode) {
		fillTableOfPatternMatches();
		System.out.println("Number of matches: "+tableOfPatternMatches.getRowCount());
		
		if(tableOfPatternMatches.getRowCount()>=0) { 
			System.out.println("Passing matches to function evaluator");
			myRuleEvaluationNode.setPatternMatches(tableOfPatternMatches);
			System.out.println("Computing... random #="+number+" ("+randomMode+")");
			myRuleEvaluationNode.computeValues(number, randomMode);
			patternEvaluatedTable=myRuleEvaluationNode.getDataResultTable();
			//hasValidPatternEvaluatedTable=true;
			return patternEvaluatedTable;
		} else return new PatternEvaluatedTable(0);
	}

	

	/**
	 * @return
	 */
	public boolean hasValidRule() {
		if(myRuleEvaluationNode==null) return false;
		else return !myRuleEvaluationNode.hasErrors();
	}

	/**
	 * @param myTableData
	 * @param b
	 */
	public void addValues() {
		
		
	}

	/**
	 * @param myTableData
	 */
	public void addPValues() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @param string
	 * @param string2
	 * @param d
	 * @param text
	 * @param r
	 */
	public void triggerRule(String valueType, String operator, String s, String text, String ruleOrPattern) {
		System.out.println("Triggering "+valueType+" "+operator+" "+s+"-> Rule: "+text+" ("+ruleOrPattern+" )");
		if(!hasValidPatternEvaluatedTable || !hasValidTableOfPatternMatches) {
			System.out.println("No evaluation done");
			return;
		}
		//PatternEvaluatedTable myDataResultTable =(PatternEvaluatedTable) myPanel.myComputedTableData;
		for (int i = 0; i < patternEvaluatedTable.getRowCount(); i++) {
			Double myValueObject=(Double) patternEvaluatedTable.getValueAt(i,1);
			double myValue=myValueObject.doubleValue();
			Double valueToCompareToDouble=new Double(s);
			double valueToCompareTo=valueToCompareToDouble.doubleValue();
			boolean conditionTrue=false;
			if(valueType.equals("value")) {
				if(operator.equals(">")) {
					conditionTrue=(myValue>valueToCompareTo);	
				}
				if(operator.equals("<")) {
					conditionTrue=(myValue<valueToCompareTo);	
			
				}
			}		
			System.out.println("Building rule!!");
			if(ruleOrPattern.equalsIgnoreCase("rule")) myPatternManager.makeRuleOutOf(text,tableOfPatternMatches,(String)patternEvaluatedTable.getValueAt(i,0));
			if(ruleOrPattern.equalsIgnoreCase("pattern"))myPatternManager.makePatternOutOf(text,tableOfPatternMatches,(String)patternEvaluatedTable.getValueAt(i,0));
		}
	}

	/**
	 * @return
	 */
	public TableModel getPatternMatchedTable() {
		return tableOfPatternMatches;
	}

	
	private void fillTableOfPatternMatches() {
		if(!hasValidTableOfPatternMatches) {
			//String query=getSelectClause()+getWhereClause()+getANDClause()+getNamespaceClause();
			//TODO to fix
			if(!KnowledgeWrapper.hasRDQLSupport(RDFScape.getKnowledgeEngine() ) ) {
				return; //TODO can't make RDQL query
			}
			tableOfPatternMatches=((RDQLQueryAnswerer)RDFScape.getKnowledgeEngine()).makeRDQLQuery(myPatternManager.makeRDQLQueryFromPattern(this));
			if(tableOfPatternMatches!=null) hasValidTableOfPatternMatches=true;
		}
	}

	/**
	 * @return
	 */
	public PatternEvaluatedTable getPatternEvaluatedData() {
		if(hasValidPatternEvaluatedTable) return patternEvaluatedTable;
		else return new PatternEvaluatedTable(0);
		
	}

	public void addTriples(String[][] tripleListWithVariables) {
		myTriplesArray=tripleListWithVariables;
		HashSet tempVarList=new HashSet();
		varList=new String[0];
		for (int i = 0; i < tripleListWithVariables.length; i++) {
			for (int j = 0; j < tripleListWithVariables[i].length; j++) {
				if(tripleListWithVariables[i][j].indexOf("?")==0) tempVarList.add(tripleListWithVariables[i][j]);
			}
		}
		varList=(String[])tempVarList.toArray(varList);
		
		fireTableDataChanged();
		
	}

	public void addFilterConditions(String[][] filterElements) {
		for (int i = 0; i < filterElements.length; i++) {
			if(filterElements[i][2].equalsIgnoreCase("P")) {
				preciseFilterConditions.put(filterElements[i][0],filterElements[i][1]);
			}
			if(filterElements[i][2].equalsIgnoreCase("F")) {
				filterConditions.put(filterElements[i][0],filterElements[i][1]);
			}
		}
		fireTableDataChanged();
		
	}

	public int getColumnCount() {
		return 3;
	}

	public int getRowCount() {
		return myTriplesArray.length;
	}

	public String getColumnName(int c) {
		if(c==0) return "Source";
		if(c==1) return "Property";
		if(c==2) return "Object";
		return "";
	}

	public Object getValueAt(int x, int y) {
		String tempElement=myTriplesArray[x][y];
		String filter=(String)filterConditions.get(tempElement);
		String preciseFilter=(String)preciseFilterConditions.get(tempElement);
		if(filter!=null) {
			return "~/"+filter+"/";
		}
		else if(preciseFilter!=null) {
			return preciseFilter;
		}
		else if(RDFScape.getCommonMemory().getLabelForURI(tempElement)!=null) {
			return RDFScape.getCommonMemory().getLabelForURI(tempElement);
		}
		else {
			return tempElement;
		}
	}

	public  boolean canAddAndSearchPatterns() {
		return myPatternManager.canAddAndSearchPatterns();
	}

	public String[] getVariables() {
		return varList;
	}

	public String[][] getTriples() {
		return myTriplesArray;
	}

	public Hashtable getFilterConditions() {
		return filterConditions;
	}

	public Hashtable getPreciseFilterConditions() {
		return preciseFilterConditions;
	}

	public AbstractQueryResultTable searchVariable() {
		return myPatternManager.searchVariable(this);
	}

	public AbstractQueryResultTable addVariable() {
		return myPatternManager.addVariable(this);
	}

	public AbstractQueryResultTable searchPattern() {
		return myPatternManager.searchPattern(this);
	}

	public AbstractQueryResultTable addPattern() {
		return myPatternManager.addPattern(this);
	}

	public void remove() {
		myPatternManager.remove(this);
		
	}
	
	
	
	
	
}
