/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;

import fr.pasteur.sysbio.rdfscape.CommonMemory;
import fr.pasteur.sysbio.rdfscape.RDFScape;
import fr.pasteur.sysbio.rdfscape.knowledge.KnowledgeWrapper;
import fr.pasteur.sysbio.rdfscape.patterns.PatternElement;

public abstract class AbstractQueryResultTable extends AbstractTableModel{
	CommonMemory commonMemory=null;
	ArrayList resultValues=null;
	Hashtable col2VarName=null;
	Hashtable varName2Col=null;
	Hashtable biteToRows=null;
		
		
	public AbstractQueryResultTable ()  {
		super();
		/**
		 * For performance reason this object "locks" a CommonMemory when constructed
		 */
		if(RDFScape.getCommonMemory()==null) {
			//throw new Exception("Unable to build QueryResultTable : missingCommonMemory");
			System.out.println("Missing CommonMemory in QueryResultTaable... we are going to be in trouble!");
		}
		commonMemory=RDFScape.getCommonMemory();
		resultValues=new ArrayList();
		col2VarName=new Hashtable();
		varName2Col=new Hashtable();
		biteToRows=new Hashtable();
		
	}
	/**
	 * @param object to add
	 * @param row
	 * @param column
	 */
	
	public void addObject(Object tempObject, int row, int column) {
		if(row>=resultValues.size()) resultValues.add(row,null);
		if(resultValues.get(row)==null) resultValues.add(row,new ArrayList());
		
		((ArrayList)resultValues.get(row)).add(column,tempObject);
		//System.out.println(i+" , "+j+" -> "+tempString+": "+resultValues.size()+" x "+resultVar.size());
	}
	
	/**
	 * @param var
	 * @param column
	 */
	public void addVar(String var, int column) {
		col2VarName.put(new Integer(column).toString(),var);
		varName2Col.put(var,new Integer(column));
		
		
	}
	public int getVarColumn(String var) {
		Integer intN=(Integer) varName2Col.get(var);
		if(intN!=null) return intN.intValue();
		else return -1;
	}
	
	
	public int getColumnCount() {
		if(resultValues.size()==0) return 0;
		ArrayList firstLine=(ArrayList)resultValues.get(0);
		if(firstLine!=null) return firstLine.size();
		else return col2VarName.size();
	}
	

	
	public String getColumnName(int col) {
		return (String) col2VarName.get(new Integer(col).toString());
	}
	
	
	public int getRowCount() {
		return resultValues.size()-1;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int i, int j) {
		ArrayList tempRow=(ArrayList)resultValues.get(i);
		//System.out.println("get("+i+","+j+")");
		if(tempRow!=null)
			return  tempRow.get(j);
		else return null;
	}
	public abstract Color getColor(int x,int y);
	public abstract String getLabel(int x, int y);
	public abstract boolean isURI(int x,int y);
	public abstract boolean isLiteral(int x, int y);
	public abstract boolean isBlank(int x, int y);
	public abstract String getNamespace(int x, int y);
	public abstract String getID(int x, int y);
	public abstract String getURI(int x, int y);
	public abstract String getDatatypeValue(int x,int y);
	public abstract String getDatatypeType(int x,int y);
	public abstract String getDatatypeLanguage(int x, int y);
	public abstract String getLocalName(int x, int y);
	public abstract boolean isNamespaceSelected(int x, int y);
	
	
	/**
	 * @param myBite
	 * @param row
	 */
	public void setBite(String myBite, int row) {
		System.out.println("Adding input line "+row+" for output line: "+myBite);
		if(biteToRows.get(myBite)==null) {
			biteToRows.put(myBite,new ArrayList());
		}
		((ArrayList)(biteToRows.get(myBite))).add(new Integer(row));
		
	}
	
		
	
	public Class getColumnClass(int arg0) {
		
		return String.class;
	}
	
	/**
	 * @param bite
	 * @return
	 */
	public int[] getBiteIndexes(String bite) {
		ArrayList myList=((ArrayList)(biteToRows.get(bite)));
		System.out.println("Bite has taken "+myList.size()+" values");
		int result[]=new int[myList.size()];
		for (int i = 0; i < myList.size(); i++) {
			result[i]=((Integer)myList.get(i)).intValue();
			System.out.println(result[i]);
		}
		return result;
	}
	/**
	 * Wether the content of this result item can be represented as a graph
	 */
	abstract public boolean hasGraph();
	/**
	 * Wether the content of this result item can be represented as a table
	 */
	abstract public boolean hasTable();
	/**
	 * return a ub-table composed only of selected rows.
	 * @param selectedRows Array of indeces of selected rows
	 * @return a new QueryResultTable... of the same kind of the originating one.
	 */
	abstract public AbstractQueryResultTable getSubsetByRows(int[] selectedRows);
	abstract public AbstractQueryResultTable getExpandedInPattern(PatternElement myPattern); 
		
	
	
}
