/*
 * $Archive: SourceJammer$
 * $FileName: DefaultSortTableModel.java$
 * $FileID: 3982$
 *
 * Last change:
 * $AuthorName: Timo Haberkern$
 * $Date: 2008-08-16 20:01:50 -0700 (Sat, 16 Aug 2008) $
 * $Comment: $
 *
 * $KeyWordsOff: $
 */

/*
=====================================================================

  DefaultSortTableModel.java

  Created by Claude Duguay
  Copyright (c) 2002

=====================================================================
*/
package browser.tests;

import java.util.Collections;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import cytoscape.data.CyAttributes;

import browser.ColumnComparator;
import browser.SortTableModel;


/**
 *
 */
public class DefaultSortTableModel extends DefaultTableModel implements SortTableModel {
	/**
	 * Creates a new DefaultSortTableModel object.
	 */
	public DefaultSortTableModel() {
	}

	/**
	 * Creates a new DefaultSortTableModel object.
	 *
	 * @param rows  DOCUMENT ME!
	 * @param cols  DOCUMENT ME!
	 */
	public DefaultSortTableModel(int rows, int cols) {
		super(rows, cols);
	}

	/**
	 * Creates a new DefaultSortTableModel object.
	 *
	 * @param data  DOCUMENT ME!
	 * @param names  DOCUMENT ME!
	 */
	public DefaultSortTableModel(Object[][] data, Object[] names) {
		super(data, names);
	}

	/**
	 * Creates a new DefaultSortTableModel object.
	 *
	 * @param names  DOCUMENT ME!
	 * @param rows  DOCUMENT ME!
	 */
	public DefaultSortTableModel(Object[] names, int rows) {
		super(names, rows);
	}

	/**
	 * Creates a new DefaultSortTableModel object.
	 *
	 * @param names  DOCUMENT ME!
	 * @param rows  DOCUMENT ME!
	 */
	public DefaultSortTableModel(Vector names, int rows) {
		super(names, rows);
	}

	/**
	 * Creates a new DefaultSortTableModel object.
	 *
	 * @param data  DOCUMENT ME!
	 * @param names  DOCUMENT ME!
	 */
	public DefaultSortTableModel(Vector data, Vector names) {
		super(data, names);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param col DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isSortable(int col) {
		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param col DOCUMENT ME!
	 * @param ascending DOCUMENT ME!
	 */
	public void sortColumn(int col, boolean ascending) {
		// All of our test data is Integer
		Collections.sort(getDataVector(), 
		                 new ColumnComparator(col, CyAttributes.TYPE_INTEGER, ascending));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param aValue DOCUMENT ME!
	 * @param rowIndex DOCUMENT ME!
	 * @param columnIndex DOCUMENT ME!
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// System.out.println("SetValueAt: " + aValue + " " + rowIndex + " " + columnIndex);
		super.setValueAt(aValue, rowIndex, columnIndex);
	}
}
