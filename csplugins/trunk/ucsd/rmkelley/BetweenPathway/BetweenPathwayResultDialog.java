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
import javax.swing.border.TitledBorder;

class BetweenPathwayResultDialog extends JDialog implements ListSelectionListener{
  Vector results;
  JTable table;
  JButton viewButton;
  CyNetwork geneticNetwork,physicalNetwork;
  public BetweenPathwayResultDialog(CyNetwork geneticNetwork, CyNetwork physicalNetwork, Vector results){
    this.results = results;
    this.geneticNetwork = geneticNetwork;
    this.physicalNetwork = physicalNetwork;


    setTitle("Results");
    /*
     * Initialize the table which is usedto display the results
     */
    table = new JTable(new BetweenPathwayResultModel(results));
    table.getSelectionModel().addListSelectionListener(this);
    table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    getContentPane().setLayout(new BorderLayout());
    

    /*
     * Create the center panel containing the results
     */
    JPanel centerPanel = new JPanel();
    centerPanel.setBorder(new TitledBorder("Result Table"));
    centerPanel.setLayout(new BorderLayout());
    JScrollPane scroller = new JScrollPane(table);
    scroller.setPreferredSize(new Dimension(300,200));
    centerPanel.add(scroller,BorderLayout.CENTER);
    getContentPane().add(centerPanel,BorderLayout.CENTER);
    

    /**
     * Create the bottom panel containg the action buttons
     */
    JPanel southPanel = new JPanel();
    southPanel.setBorder(new TitledBorder("Actions"));
    
    viewButton = new JButton("Display selected model");
    viewButton.setEnabled(false);
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
    southPanel.add(viewButton);
    getContentPane().add(southPanel,BorderLayout.SOUTH);
    pack();
  }

  public void valueChanged(ListSelectionEvent e){
    int index = table.getSelectedRow();
    if(index > -1){
      viewButton.setEnabled(true);
      NetworkModel model = (NetworkModel)results.get(index);
      geneticNetwork.setFlaggedNodes(model.one,true);
      geneticNetwork.setFlaggedNodes(model.two,true);
      physicalNetwork.setFlaggedNodes(model.one,true);
      physicalNetwork.setFlaggedNodes(model.two,true);
    }
    else{
      viewButton.setEnabled(false);
    }
  }
  
}
