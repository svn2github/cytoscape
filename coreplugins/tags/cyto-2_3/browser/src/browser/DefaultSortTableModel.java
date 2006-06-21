/*
 * $Archive: SourceJammer$
 * $FileName: DefaultSortTableModel.java$
 * $FileID: 3982$
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

  DefaultSortTableModel.java

  Created by Claude Duguay
  Copyright (c) 2002

=====================================================================
*/
package browser;

import java.util.Collections;
 import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class DefaultSortTableModel
  extends DefaultTableModel
  implements SortTableModel
{
  public DefaultSortTableModel() {}

  public DefaultSortTableModel(int rows, int cols)
  {
    super(rows, cols);
  }

  public DefaultSortTableModel(Object[][] data, Object[] names)
  {
    super(data, names);
  }

  public DefaultSortTableModel(Object[] names, int rows)
  {
    super(names, rows);
  }

  public DefaultSortTableModel(Vector names, int rows)
  {
    super(names, rows);
  }

  public DefaultSortTableModel(Vector data, Vector names)
  {
    super(data, names);
  }

  public boolean isSortable(int col)
  {
    return true;
  }

  public void sortColumn(int col, boolean ascending)
  {
    Collections.sort(getDataVector(),
      new ColumnComparator(col, ascending));
  }

  public void setValueAt ( Object aValue,
                           int rowIndex,
                           int columnIndex ) {
    System.out.println( "SetValueAt: "+aValue+" "+rowIndex+" "+columnIndex );
    super.setValueAt( aValue, rowIndex, columnIndex );
  }

}
