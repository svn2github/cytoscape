//---------------------------------------------------------------------------------------
package csplugins.isb.pshannon.dataMatrix.gui;
//---------------------------------------------------------------------------------------
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.ImageIcon;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;


//---------------------------------------------------------------------------------------
public class CheckBoxHeader extends JCheckBox implements TableCellRenderer, MouseListener {

  protected CheckBoxHeader rendererComponent;

  protected int column;
  protected boolean mousePressed = false;

//------------------------------------------------------------------------------------------
public CheckBoxHeader (ItemListener itemListener) 
{
  rendererComponent = this;
  rendererComponent.addItemListener (itemListener);
  setSelected (true);
}
//------------------------------------------------------------------------------------------
public Component getTableCellRendererComponent (JTable table, Object value,
                                                boolean isSelected, boolean hasFocus, 
                                                int row, int column) 
{
 if (table != null) {
   JTableHeader header = table.getTableHeader ();
   if (header != null) {
     rendererComponent.setForeground (header.getForeground ());
     rendererComponent.setBackground (header.getBackground ());
     rendererComponent.setFont (header.getFont ());
     header.addMouseListener (rendererComponent);
     }
   }

  setColumn (column);
  rendererComponent.setText ((value == null) ? "" : value.toString ());
                              setBorder (UIManager.getBorder ("TableHeader.cellBorder"));

    return rendererComponent;
}
//------------------------------------------------------------------------------------------
public void fireStateChanged ()
{
  super.fireStateChanged ();
}
//------------------------------------------------------------------------------------------
protected void setColumn (int column) 
{
  this.column = column;
}
//------------------------------------------------------------------------------------------
public int getColumn () 
{
  return column;
}
//------------------------------------------------------------------------------------------
protected void handleClickEvent (MouseEvent e) 
{
    // Workaround: dozens of mouseevents occur for only one mouse click.
    // First MousePressedEvents, then MouseReleasedEvents, (then
    // MouseClickedEvents).
    // The boolean flag 'mousePressed' is set to make sure
    // that the action is performed only once.

 if (mousePressed) {
   mousePressed=false;
   JTableHeader header = (JTableHeader) (e.getSource ());
   JTable tableView = header.getTable ();
   TableColumnModel columnModel = tableView.getColumnModel ();
   int viewColumn = columnModel.getColumnIndexAtX (e.getX ());
   int column = tableView.convertColumnIndexToModel (viewColumn);

   if (viewColumn == this.column && e.getClickCount () == 1 && column != -1)
     doClick ();
    } // if mousePressed

} // handleClickEvent
//------------------------------------------------------------------------------------------
public void mouseClicked (MouseEvent e) 
{
  handleClickEvent (e);
    
  ((JTableHeader) e.getSource()).repaint(); //Header doesn't repaint itself properly
}
//------------------------------------------------------------------------------------------
  public void mousePressed (MouseEvent e) {
    mousePressed = true;
    }
  public void mouseReleased (MouseEvent e) { }
  public void mouseEntered (MouseEvent e) {}
  public void mouseExited (MouseEvent e) { }
//------------------------------------------------------------------------------------------
} // class CheckBoxHeader

