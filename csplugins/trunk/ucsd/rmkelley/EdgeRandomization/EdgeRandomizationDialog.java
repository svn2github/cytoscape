package ucsd.rmkelley.EdgeRandomization;
import java.io.*;
import java.util.*;
import edu.umd.cs.piccolo.activities.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import giny.view.NodeView;
import giny.model.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import phoebe.PNodeView;
import phoebe.PGraphView;
import cytoscape.data.Semantics;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;
import javax.swing.border.TitledBorder;
import java.awt.Dimension;
import ucsd.rmkelley.Util.RyanDialog;

public class EdgeRandomizationDialog extends RyanDialog{
  JTable table;
  EdgeRandomizationThread thread;
  EdgeRandomizationTableModel tableModel;
  List directedTypes;
  int iterations = 10000;
  JTextField iterationText;
  /**
   * The network for which scores are being determined
   */
  CyNetwork currentNetwork;
  /**
   * Assume the dialog is cancelld until the ok button 
   * is pressed
   */
  boolean cancelled = true;
  /**
   * The options specified by the dialog
   */
  EdgeRandomizationOptions options;
  
  public EdgeRandomizationDialog(EdgeRandomizationOptions t_options){
    //figure out what the different edge types are in the graph
    this.currentNetwork = t_options.currentNetwork;
    this.options = t_options;
    Set typeSet = new HashSet();
    for(Iterator edgeIt = currentNetwork.edgesIterator();edgeIt.hasNext();){
      Edge edge = (Edge)edgeIt.next();
      if(edge.getSource() == edge.getTarget()){
	continue;
      }
      String type = (String)currentNetwork.getEdgeAttributeValue(edge,Semantics.INTERACTION);
      typeSet.add(type);
    }
   
    Vector types = new Vector(typeSet);

    getContentPane().setLayout(new BorderLayout());
    
    JPanel northPanel = new JPanel();
    northPanel.setBorder(new TitledBorder("Set number of iterations"));
    northPanel.add(new JLabel("Iterations: "));
    iterationText = new JTextField((new Integer(iterations)).toString());
    iterationText.addFocusListener(new FocusListener(){
	public void focusGained(FocusEvent e){}
	public void focusLost(FocusEvent e){
	  try{
	    Integer temp = new Integer(iterationText.getText());
	    iterations = temp.intValue();
	    if(iterations < 100){
	      iterations = 100;
	      throw new RuntimeException("Value too small");
	    }
	  }catch(Exception except){
	    iterationText.setText((new Integer(iterations)).toString());
	  }}});
    northPanel.add(iterationText);
    getContentPane().add(northPanel,BorderLayout.NORTH);


    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new BorderLayout());
    centerPanel.setBorder(new TitledBorder("Choose directed interaction types"));
    
    tableModel = new EdgeRandomizationTableModel(types);
    table = new JTable(tableModel);
    JScrollPane scroller = new JScrollPane(table);
    scroller.setPreferredSize(new Dimension(250,200));
    centerPanel.add(scroller,BorderLayout.CENTER);
    getContentPane().add(centerPanel,BorderLayout.CENTER);
    
    JPanel southPanel = new JPanel();
    JButton ok = new JButton("Begin Randomization");
    southPanel.add(ok);
    getContentPane().add(southPanel,BorderLayout.SOUTH);
    ok.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  /*
	   * Remeber that the user requested the action, and did
	   * not cancel the dialog
	   */
	  cancelled = false;

	  /*
	   * Set the options
	   */
	  options.directedTypes = new Vector();
	  for(int idx=0;idx<tableModel.getRowCount();idx++){
	    if(((Boolean)tableModel.getValueAt(idx,1)).booleanValue()){
	      options.directedTypes.add(tableModel.getValueAt(idx,0));
	    }
	  }
	  options.iterations = iterations;
	  options.currentNetwork = EdgeRandomizationDialog.this.currentNetwork;
	  JFileChooser chooser = new JFileChooser();
	  chooser.setApproveButtonText("OK");
	  chooser.setDialogTitle("Choose Destination File");
	  EdgeRandomizationDialog.this.disableInput();
	  int returnVal = chooser.showSaveDialog(Cytoscape.getDesktop());
	  if(returnVal == JFileChooser.APPROVE_OPTION){
	    options.saveFile = chooser.getSelectedFile();
	  }
	  else{
	    cancelled = true;
	  }
	  EdgeRandomizationDialog.this.enableInput();
	  /*
	   * Get rid of the dialog
	   */
	  
	  dispose();
	}});
    pack();

  }


  public boolean isCancelled(){
    return cancelled;
  }

}

class EdgeRandomizationTableModel extends AbstractTableModel{
  List columnNames;
  List [] data;
  int BOOLEAN_COLUMN = 1;

  public EdgeRandomizationTableModel(List types){
    columnNames = Arrays.asList(new String[]{"Type","Directed"});
    data = new List [2];
    data[0] = new Vector(types);
    Vector booleans = new Vector();
    for(int idx=0;idx<types.size();idx++){
      booleans.add(new Boolean(false));
    }
    data[1] = booleans;
  }

  public int getColumnCount(){
    return columnNames.size();
  }

  public int getRowCount(){
    return data[0].size();
  }

  public String getColumnName(int col){
    return (String)columnNames.get(col);
  }

  public Object getValueAt(int row, int col){
    return data[col].get(row);
  }

  public Class getColumnClass(int c){
    return getValueAt(0,c).getClass();
  }

  public boolean isCellEditable(int row, int column){
    if(column == BOOLEAN_COLUMN){
      return true;
    }
    else{
      return false;
    }
  }

  public void setValueAt(Object value, int row, int column){
    data[column].set(row,value);
  }
}

