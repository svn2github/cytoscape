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
 * This class controls data within a JTable UI
 */
class PluginUITableModel implements TableModel{

    public HashMap args;
    String action;
    Vector argNames;
    Vector argVals;
    HashMap classCellEditor;
    Vector listeners;
    GenericPlugin plugin;

    FileCellEditor fileEditor;
    String file_split_string="|";
    String[] columnNames={"Name","Value"};
    Class[] columnClasses={String.class,String.class};
    Vector[] columns;
    
    public PluginUITableModel(HashMap args,GenericPlugin plugin)
    {
	this.plugin=plugin;
	this.action=action;
	this.args=new HashMap(args);
	this.argNames=new Vector();
	this.argVals=new Vector();
	//there are only 2 columns in tihs tablemodel:
	Vector[] tmpColumns={argNames,argVals};
	this.columns=tmpColumns;
	this.listeners=new Vector();
	Iterator it=this.args.keySet().iterator();
	while (it.hasNext()){
	    String nextName=it.next().toString();
	    argNames.add(nextName);
	    argVals.add(args.get(nextName));
	}
	//instantiate editors and the classes they correspond to
	fileEditor=new FileCellEditor(argVals);
	this.classCellEditor=new HashMap(2);
	this.classCellEditor.put(new File(""),fileEditor);    
	this.classCellEditor.put(new File[1],fileEditor);
    }

    public PluginUITableModel(HashMap args,GenericPlugin plugin,String file_split_string)
    {
	this.plugin=plugin;
	this.action=action;
	this.args=new HashMap(args);
	this.argNames=new Vector();
	this.argVals=new Vector();
	//there are only 2 columns in tihs tablemodel:
	Vector[] tmpColumns={argNames,argVals};
	this.columns=tmpColumns;
	this.listeners=new Vector();
	Iterator it=this.args.keySet().iterator();
	while (it.hasNext()){
	    String nextName=it.next().toString();
	    argNames.add(nextName);
	    argVals.add(args.get(nextName));
	}
	this.file_split_string=file_split_string;
	//instantiate editors and the classes they correspond to
	fileEditor=new FileCellEditor(argVals);
	this.classCellEditor=new HashMap(2);
	this.classCellEditor.put(File.class,fileEditor);    
	this.classCellEditor.put(File[].class,fileEditor);
    }

    /*
     * must implement these methods for the class to work
     */
    public void addTableModelListener(TableModelListener tml){
	this.listeners.add(tml);
	return;
    }

    public void removeTableModelListener(TableModelListener tml){
      this.listeners.remove(tml);
      return;
    }
    //end necessary methods 

    public TableCellEditor getSpecialCellEditor(int row,int col){
	//if the cell in question is either a file or files, use the special editor, if the class is not found in the classCellEditor hash, returns null	
	return (TableCellEditor)classCellEditor.get(columns[col].elementAt(row).getClass());
    }

    public String getColumnName(int columnIndex){
	return columnNames[columnIndex];	
    }
  
  
    public java.lang.Class getColumnClass(int columnIndex){
	return columnClasses[columnIndex];
    }
  
    public int getColumnCount(){
	return columns.length;
    }
    
    public int getRowCount(){
	return argNames.size();
    }

    public String fileArrayClassToString(File[] inFiles){
	String exportStr=new String();
	if (inFiles[0]!=null){exportStr=inFiles[0].toString();}
	for(int i=1;i<inFiles.length;i++){
	    if (inFiles[i]!=null){exportStr=exportStr + file_split_string + inFiles[i].toString();}
	}
	return exportStr;
    }
    
    public Object getValueAt(int rowIndex, int columnIndex){
	Object curVal=columns[columnIndex].elementAt(rowIndex);
	if (curVal.getClass().equals(File[].class)){
	    return fileArrayClassToString((File[])curVal);
	}
	return curVal.toString();
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex){
	Class valClass=aValue.getClass();
	if (columns[columnIndex]==argNames) {
	    throw new RuntimeException("Cannot edit argument names");
	}

	//newVal should be a string from the table.  If not, perhaps it was handled by one of the specialized Cell Editors.  Turn aVal to string newVal:
	String newVal=new String();
	if (valClass.equals(String.class)){
	    newVal=aValue.toString();
	}
	else{
	    if (valClass.equals(File.class)){
		newVal=aValue.toString();
	    }
	    else if (valClass.equals(File[].class)){
		newVal=fileArrayClassToString((File[])aValue);
	    }
	    else if (valClass.toString().equals("class sun.awt.shell.DefaultShellFolder")){
		newVal=aValue.toString();
	    } 
	    else{throw new ClassCastException("Cannot process "+valClass.toString()+ " at PluginUITableModel.getValue for argument "+argNames.elementAt(rowIndex).toString());}
	}
	System.out.println("setting arg "+(String)argNames.elementAt(rowIndex)+" to "+newVal);
	if (plugin.setValue(args,(String)argNames.elementAt(rowIndex),newVal)){
	    //args['argName'] is set by verify.  Keep argVals and args in sync
	    System.out.println("set successful");
	    argVals.set(rowIndex,args.get(argNames.elementAt(rowIndex)));
	}
	else{
	    //probably should do something
	}
    }


    public HashMap getArgs(){
	return args;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex){
	//compare the pointers
	if ( columns[columnIndex] != argNames){
	    return true;
	}
    return false;
    }
    
}
