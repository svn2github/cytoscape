/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 15, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.computing;

import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PatternEvaluatedTable extends AbstractTableModel {
	String functionTitle="function";
	String[] patternNames;
	Double[] values;
	Double[] pvalues;
	/**
	 * @param i
	 * @param myResultTable
	 * 
	 */
	public PatternEvaluatedTable(int i) {
		super();
		patternNames=new String[i];
		values=new Double[i];
		pvalues=new Double[i];
		
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 3;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return patternNames.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int x, int arg1) {
		if(arg1==0) return (String) patternNames[x];
		if(arg1==1) return values[x];
		if(arg1==2) return pvalues[x];
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object arg, int x, int y) {
		if(y==0) {patternNames[x]=(String) arg;}
		if(y==1) {values[x]=(Double) arg;}
		if(y==2) {pvalues[x]=(Double) arg;}

	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int i) {
		if(i==0) return "Pattern";
		//if(i==1) return functionTitle;
		if(i==1) return "Value";
		if(i==2) return "Pvalue";
		return "";
	}

	

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int y) {
		if(y==0) return String.class;
		if(y==1 || y==2) return Double.class;
		else return super.getColumnClass(y);
	}

	
}
