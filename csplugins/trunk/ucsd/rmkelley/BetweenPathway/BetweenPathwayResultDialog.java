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

class BetweenPathwayResultDialog extends JDialog implements ListSelectionListener{
  Vector results;
  JTable table;
  CyNetwork geneticNetwork,physicalNetwork;
  public BetweenPathwayResultDialog(CyNetwork geneticNetwork, CyNetwork physicalNetwork, Vector results){
    this.results = results;
    this.geneticNetwork = geneticNetwork;
    this.physicalNetwork = physicalNetwork;
    table = new JTable(new BetweenPathwayResultModel(results));
    table.getSelectionModel().addListSelectionListener(this);
    getContentPane().setLayout(new BorderLayout());
    JScrollPane scroller = new JScrollPane(table);
    scroller.setPreferredSize(new Dimension(30,100));
    getContentPane().add(scroller,BorderLayout.CENTER);
    JButton viewButton = new JButton("view");
    viewButton.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  NetworkModel model = (NetworkModel)BetweenPathwayResultDialog.this.results.get(table.getSelectionModel().getMinSelectionIndex());
	  List allNodes = new Vector();
	  allNodes.addAll(model.one);
	  allNodes.addAll(model.two);
	  
	  List allEdges = new Vector();
	  allEdges.addAll(BetweenPathwayResultDialog.this.geneticNetwork.getConnectingEdges(allNodes));
	  allEdges.addAll(BetweenPathwayResultDialog.this.physicalNetwork.getConnectingEdges(allNodes));

	  CyNetwork newNetwork = Cytoscape.createNetwork(allNodes,allEdges,"Network Model: "+model.ID);
	  CyNetworkView newView = Cytoscape.getNetworkView(newNetwork.getIdentifier());
	  if(newView != null){
	    CircleGraphLayout layout = new CircleGraphLayout(newView,model.one,model.two);
	    layout.construct();
	  }
	}
      });
    getContentPane().add(viewButton,BorderLayout.SOUTH);
    pack();
  }

  public void valueChanged(ListSelectionEvent e){
    int index = e.getFirstIndex();
    NetworkModel model = (NetworkModel)results.get(index);
    geneticNetwork.setFlaggedNodes(model.one,true);
    geneticNetwork.setFlaggedNodes(model.two,true);
    physicalNetwork.setFlaggedNodes(model.one,true);
    physicalNetwork.setFlaggedNodes(model.two,true);
  }
  
}
