/*
 * $Archive: SourceJammer$
 * $FileName: JSortTable.java$
 * $FileID: 3984$
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

  JSortTable.java

  Created by Claude Duguay
  Copyright (c) 2002

=====================================================================
*/
package rowan.browser;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

public class JSortTable extends JTable
  implements MouseListener
{
  protected int sortedColumnIndex = -1;
  protected boolean sortedColumnAscending = true;

  public JSortTable()
  {
    this(new DefaultSortTableModel());
  }

  public JSortTable(int rows, int cols)
  {
    this(new DefaultSortTableModel(rows, cols));
  }

  public JSortTable(Object[][] data, Object[] names)
  {
    this(new DefaultSortTableModel(data, names));
  }

  public JSortTable(Vector data, Vector names)
  {
    this(new DefaultSortTableModel(data, names));
  }

  public JSortTable(SortTableModel model)
  {
    super(model);
    initSortHeader();
  }

  public JSortTable(SortTableModel model,
    TableColumnModel colModel)
  {
    super(model, colModel);
    initSortHeader();
  }

  public JSortTable(SortTableModel model,
    TableColumnModel colModel,
    ListSelectionModel selModel)
  {
    super(model, colModel, selModel);
    initSortHeader();
  }

  protected void initSortHeader()
  {
    JTableHeader header = getTableHeader();
    header.setDefaultRenderer(new SortHeaderRenderer());
    header.addMouseListener(this);

    setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );

    addMouseListener( new MouseListener() {
        
        public void mouseClicked(MouseEvent e) {
          TableColumnModel columnModel = getColumnModel();
          int column = columnModel.getColumnIndexAtX(e.getX());
          int row = e.getY() / getRowHeight();
          if (row >= getRowCount() || row < 0
              || column >= getColumnCount() || column < 0)
            return;

          Object cellValue = getValueAt(row, column);
          try {
            if (cellValue != null
                && cellValue.getClass() == Class.forName("java.lang.String")) {
              // see if the String representation is a URL
              // if it is not, a MalformedURLException gets thrown, and we
              // ignore it
              java.net.URL url = new java.net.URL((String) cellValue);
              cytoscape.util.OpenBrowser.openURL( url.toString() );
            }

          } catch (Exception ignore) {}
        
        } // mouseClicked 
        public void mouseExited(MouseEvent e) {}
        public void mousePressed(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {} 
        public void mouseReleased(MouseEvent e) {}
      }
                      );


  }

  public int getSortedColumnIndex()
  {
    return sortedColumnIndex;
  }

  public boolean isSortedColumnAscending()
  {
    return sortedColumnAscending;
  }

  public void mouseReleased(MouseEvent event)
  {
  }

  public void mousePressed(MouseEvent event) {}

  public void mouseClicked(MouseEvent event) {
    int cursorType = getTableHeader().getCursor().getType();
    if (event.getButton() == MouseEvent.BUTTON1 && 
        cursorType != Cursor.E_RESIZE_CURSOR &&
        cursorType != Cursor.W_RESIZE_CURSOR){
      TableColumnModel colModel = getColumnModel();
      int index = colModel.getColumnIndexAtX(event.getX());
      int modelIndex = colModel.getColumn(index).getModelIndex();
  
      SortTableModel model = (SortTableModel)getModel();
      if (model.isSortable(modelIndex))
      {
        // toggle ascension, if already sorted
        if (sortedColumnIndex == index)
        {
          sortedColumnAscending = !sortedColumnAscending;
        }
        sortedColumnIndex = index;
  
        model.sortColumn(modelIndex, sortedColumnAscending);
      }
    }
  }
  public void mouseEntered(MouseEvent event) {}
  public void mouseExited(MouseEvent event) {}
}
