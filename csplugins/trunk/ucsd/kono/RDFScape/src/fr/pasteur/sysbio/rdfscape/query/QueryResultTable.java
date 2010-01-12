/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

package fr.pasteur.sysbio.rdfscape.query;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.table.AbstractTableModel;

public class QueryResultTable extends AbstractTableModel{

	ArrayList resultValues=null;
	Hashtable resultVar=null;
	Hashtable biteToRows=null;
		
		
	public QueryResultTable () {
		super();
		resultValues=new ArrayList();
		resultVar=new Hashtable();
		biteToRows=new Hashtable();
	}
	/**
	 * @param tempString
	 * @param i
	 * @param j
	 */
	public void addString(String tempString, int i, int j) {
		if(i>=resultValues.size()) resultValues.add(i,null);
		if(resultValues.get(i)==null) resultValues.add(i,new ArrayList());
		
		((ArrayList)resultValues.get(i)).add(j,tempString);
		//System.out.println(i+" , "+j+" -> "+tempString+": "+resultValues.size()+" x "+resultVar.size());
	}
	/**
	 * @param var
	 * @param j
	 */
	public void addVar(String var, int j) {
		resultVar.put(new Integer(j).toString(),var);
		
	}
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return resultVar.size();
	}
		

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int col) {
		return (String) resultVar.get(new Integer(col).toString());
	}
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
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
			return (String) tempRow.get(j);
		else return null;
	}
	//public Color getColorAt(int i,int j) {
	//	
	//}
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
	
		
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
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
	
	
}
