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

class BetweenPathwayOptionsDialog extends JDialog{
  NetworkSelectionPanel geneticPanel,physicalPanel;
  public BetweenPathwayOptionsDialog(){
    setModal(true);
    geneticPanel = new NetworkSelectionPanel();
    physicalPanel = new NetworkSelectionPanel();
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add("Select Genetic Network",geneticPanel);
    tabbedPane.add("Select Physical Network",physicalPanel);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(tabbedPane,BorderLayout.CENTER);
    JButton ok = new JButton("OK");
    JPanel southPanel = new JPanel();
    southPanel.add(ok);
    getContentPane().add(southPanel,BorderLayout.SOUTH);
    ok.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  BetweenPathwayOptionsDialog.this.dispose();
	}
      });
    pack();
  }
     
  public boolean getSearchFromSelected(){
    return true;
  }
  public CyNetwork getGeneticNetwork(){
    return geneticPanel.getSelectedNetwork();
  }
    
  public CyNetwork getPhysicalNetwork(){
    return physicalPanel.getSelectedNetwork();
  }
    
  public File getPhysicalScores(){
    return physicalPanel.getScoreFile();
  }
    
  public File getGeneticScores(){
    return geneticPanel.getScoreFile();
  }
    
}
