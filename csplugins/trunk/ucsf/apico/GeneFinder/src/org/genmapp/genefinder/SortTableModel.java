package org.genmapp.genefinder;

import javax.swing.table.TableModel;

public interface SortTableModel
  extends TableModel
{
  public boolean isSortable(int col);
  public void sortColumn(int col, boolean ascending);
}
