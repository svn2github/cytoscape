/*
 * $Archive: SourceJammer$
 * $FileName: DefaultSortTableModel.java$
 * $FileID: 3982$
 *
 * Last change:
 * $AuthorName: Timo Haberkern$
 * $Date: 2007-02-13 12:13:13 -0800 (Tue, 13 Feb 2007) $
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
package cytoscape.browser;

import javax.swing.table.DefaultTableModel;
import java.util.Collections;
import java.util.Vector;


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
		Collections.sort(getDataVector(), new ColumnComparator(col, ascending));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param aValue DOCUMENT ME!
	 * @param rowIndex DOCUMENT ME!
	 * @param columnIndex DOCUMENT ME!
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		System.out.println("SetValueAt: " + aValue + " " + rowIndex + " " + columnIndex);
		super.setValueAt(aValue, rowIndex, columnIndex);
	}
}
