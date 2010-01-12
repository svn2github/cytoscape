/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Oct 27, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape.cytomapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.table.AbstractTableModel;

import fr.pasteur.sysbio.rdfscape.query.AbstractQueryResultTable;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MapperTable extends AbstractTableModel {
	private HashSet uriId=null;
	private Hashtable uriToID=null;
	private Hashtable idToURI=null;
	int conflicts=0;
	int multi=0;
	/**
	 * 
	 */
	public MapperTable() {
		super();
		uriId=new HashSet();
		//uriToID=new Hashtable();
		//idToURI=new Hashtable();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return uriId.size();
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int x, int y) {
		String myLine=(String)uriId.toArray()[x];
		String[] myValue=myLine.split("@@@");
		return myValue[y];
	}

	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int y) {
		if(y==0) return("URI");
		else return ("ID");
	}

	/**
	 * @param table
	 */
	/*
	public void add(PatternMatchedTable dataTable) {
		for (int i = 0; i < dataTable.getRowCount(); i++) {
			String last=(String) dataTable.getValueAt(i,1);
			if(last.indexOf("^^")>0) last=last.substring(0,last.indexOf("^^"));
			uriId.add(dataTable.getValueAt(i,0)+"@@@"+last);
		}
		System.out.println("Size: "+uriId.size());
		fireTableDataChanged();
	}
	
	

	/**
	 * @return
	 */
	/*
	public int getTotalURIs() {
		return uriToID.size();
	}
*/
	/**
	 * @return
	 */
	/*
	public int getTotalIDs() {
		return idToURI.size();
	}
*/
	public void add(AbstractQueryResultTable table) {
		System.out.println("Adding....");
		for (int i = 0; i < table.getRowCount(); i++) {
			
			uriId.add(table.getURI(i,0)+"@@@"+table.getDatatypeValue(i,1));
		}
		System.out.println("Size: "+getRowCount());
		fireTableDataChanged();
		
	}

	public void reset() {
		uriId=new HashSet();
		fireTableDataChanged();
		
	}
}
