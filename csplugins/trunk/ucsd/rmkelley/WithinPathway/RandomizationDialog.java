package ucsd.rmkelley.WithinPathway;
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
import ucsd.rmkelley.Util.IntegerVerification;
import ucsd.rmkelley.Util.DoubleVerification;


public class RandomizationDialog extends RyanDialog{
  JTable table;
  EdgeRandomizationTableModel tableModel;
  List directedTypes;
  int iterations = 100;
  JTextField iterationText,alphaText;
  double alpha = 0.05;
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
  BetweenPathwayOptions options;
  
  public RandomizationDialog(CyNetwork currentNetwork, BetweenPathwayOptions options){
    //figure out what the different edge types are in the graph
    this.currentNetwork = currentNetwork;
    this.options = options;
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
    northPanel.setBorder(new TitledBorder("Set parameters"));

    iterationText = new JTextField(Integer.toString(iterations));
    new IntegerVerification(iterationText,1,1000,100);
   
    alphaText = new JTextField(Double.toString(alpha));
    new DoubleVerification(alphaText,0.01,0.99,0.05);
    
    northPanel.add(new JLabel("Iterations: "));
    northPanel.add(iterationText);
    northPanel.add(new JLabel("Alpha: "));
    northPanel.add(alphaText);

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
	  RandomizationDialog.this.options.directedTypes = new Vector();
	  for(int idx=0;idx<tableModel.getRowCount();idx++){
	    if(((Boolean)tableModel.getValueAt(idx,1)).booleanValue()){
	      RandomizationDialog.this.options.directedTypes.add(tableModel.getValueAt(idx,0));
	    }
	  }
	  RandomizationDialog.this.options.iterations = Integer.parseInt(iterationText.getText());
	  RandomizationDialog.this.options.alpha = Double.parseDouble(alphaText.getText());
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

