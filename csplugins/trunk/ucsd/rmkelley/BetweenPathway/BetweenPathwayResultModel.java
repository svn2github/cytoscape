package ucsd.rmkelley.BetweenPathway;
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
import javax.swing.event.*;
import javax.swing.table.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;
import java.awt.Dimension;

class BetweenPathwayResultModel extends AbstractTableModel{
  String [] columnNames;
  List data;
  public BetweenPathwayResultModel(List data){
    columnNames = new String[]{"Network Model ID","Network Model Score"};
    this.data = data;
  }
  public int getColumnCount(){
    return columnNames.length;
  }

  public int getRowCount(){
    return data.size();
  }

  public String getColumnName(int col){
    return columnNames[col];	
  }

  public Object getValueAt(int row,int col){
    if(col == 0){
      return new Integer(((NetworkModel)data.get(row)).ID);
    }
    else if(col == 1){
      return new Double(((NetworkModel)data.get(row)).score);
    }
    else{
      return null;
    }
  }

  public Class getColumnClass(int c){
    return getValueAt(0,c).getClass();
  }
}

