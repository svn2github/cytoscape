/*
 * $Archive: SourceJammer$
 * $FileName: SortTableModel.java$
 * $FileID: 3983$
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

  SortTableModel.java

  Created by Claude Duguay
  Copyright (c) 2002

=====================================================================
*/
package browser;

import javax.swing.table.TableModel;


/**
 *
  */
public interface SortTableModel extends TableModel {
	/**
	 *  DOCUMENT ME!
	 *
	 * @param col DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isSortable(int col);

	/**
	 *  DOCUMENT ME!
	 *
	 * @param col DOCUMENT ME!
	 * @param ascending DOCUMENT ME!
	 */
	public void sortColumn(int col, boolean ascending);
}
