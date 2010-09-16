/*
 * $Archive: SourceJammer$
 * $FileName: SortHeaderRenderer.java$
 * $FileID: 3985$
 *
 * Last change:
 * $AuthorName: Timo Haberkern$
 * $Date$
 * $Comment: $
 *
 * $KeyWordsOff: $
 */

/*
=====================================================================

  SortHeaderRenderer.java

  Created by Claude Duguay
  Copyright (c) 2002

=====================================================================
*/
package browser.ui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;



/**
 *
 */
public class SortHeaderRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	public static Icon NONSORTED = new SortArrowIcon(SortArrowIcon.NONE);

	/**
	 * 
	 */
	public static Icon ASCENDING = new SortArrowIcon(SortArrowIcon.ASCENDING);

	/**
	 * 
	 */
	public static Icon DECENDING = new SortArrowIcon(SortArrowIcon.DECENDING);

	/**
	 * Creates a new SortHeaderRenderer object.
	 */
	public SortHeaderRenderer() {
		setHorizontalTextPosition(LEFT);
		setHorizontalAlignment(CENTER);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param table DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 * @param isSelected DOCUMENT ME!
	 * @param hasFocus DOCUMENT ME!
	 * @param row DOCUMENT ME!
	 * @param col DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
	                                               boolean hasFocus, int row, int col) {
		int index = -1;
		boolean ascending = true;

		if (table instanceof CyAttributeBrowserTable) {
			CyAttributeBrowserTable sortTable = (CyAttributeBrowserTable) table;
			index = sortTable.getSortedColumnIndex();
			ascending = sortTable.isSortedColumnAscending();
		}

		if (table != null) {
			JTableHeader header = table.getTableHeader();

			if (header != null) {
				setForeground(header.getForeground());
				setBackground(header.getBackground());
				setFont(header.getFont());
			}
		}

		Icon icon = ascending ? ASCENDING : DECENDING;
		setIcon((col == index) ? icon : NONSORTED);
		setText((value == null) ? "" : value.toString());
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));

		return this;
	}
}
