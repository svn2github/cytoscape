/*
 * $Archive: SourceJammer$
 * $FileName: SortTableModel.java$
 * $FileID: 3983$
 *
 * Last change:
 * $AuthorName: Timo Haberkern$
 * $Date: 2005/12/13 00:42:44 $
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

public interface SortTableModel
  extends TableModel
{
  public boolean isSortable(int col);
  public void sortColumn(int col, boolean ascending);
}
