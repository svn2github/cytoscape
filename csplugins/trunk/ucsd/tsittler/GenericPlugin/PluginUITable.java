package GenericPlugin;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.table.TableCellEditor;
import javax.swing.JButton;
import javax.swing.filechooser.*;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;//TablemodelListener, TableModel, etc.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;//HashMap,Vector,Iterator

/*
 * The UI JTable for Generic Cytoscape Plugins
 */
public class PluginUITable extends JTable{
    public PluginUITable(TableModel tm){
	super(tm);
    };
    public TableCellEditor getCellEditor(int row,int column){
      TableCellEditor specialEditor=((PluginUITableModel)dataModel).getSpecialCellEditor(row,column);
      if (specialEditor==null){
	  return getDefaultEditor(dataModel.getColumnClass(column));
      }
      else{
	  System.out.println("getting a new editor");
	  return specialEditor;
      }
    }
}
