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


public class BetweenPathwayOptionsDialog extends RyanDialog{
  NetworkSelectionPanel geneticPanel,physicalPanel;
  JCheckBox selectedSearch;
  boolean cancelled = true;
  File currentDirectory;
  BetweenPathwayOptions options;
  /**
   * Sending an action to this jbutton will start the search
   */
  JButton ok;

  /**
   * Create a new options dialog and specifiy the options that should be
   * used
   */
  public BetweenPathwayOptionsDialog(){
    currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
    setTitle("BetweenPathway Search Options");
    geneticPanel = new NetworkSelectionPanel(this);
    physicalPanel = new NetworkSelectionPanel(this);
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add("Select Genetic Network",geneticPanel);
    tabbedPane.add("Select Physical Network",physicalPanel);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(tabbedPane,BorderLayout.CENTER);
    

    ok = new JButton("Start search");
    JPanel southPanel = new JPanel();
    selectedSearch = new JCheckBox("Search only from selected interactions?");
    southPanel.add(selectedSearch);
    southPanel.add(ok);
    getContentPane().add(southPanel,BorderLayout.SOUTH);
    ok.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent ae){
	  /*
	   * User did not cancel the dialog
	   */
	  cancelled = false;
	  
	  /**
	   * Store the user's options
	   */
	  BetweenPathwayOptions options = new BetweenPathwayOptions();
	  options.selectedSearch = selectedSearch.isSelected();
	  options.geneticNetwork = geneticPanel.getSelectedNetwork();
	  options.physicalNetwork = physicalPanel.getSelectedNetwork();
	  options.geneticScores = geneticPanel.getScoreFile();
	  options.physicalScores = physicalPanel.getScoreFile();
	  BetweenPathwayOptionsDialog.this.options = options;
	  
	  /**
	   * Unblock the thread waiting on the dialog
	   */
	  BetweenPathwayOptionsDialog.this.dispose();

	}});
    pack();
  }
     
   
  /**
   * Determine if the user tried to cancel this dialog box
   * or wanted to start the search
   */
  public boolean isCancelled(){
    return cancelled;
  }
  
  /**
   * Get the options
   */
  public BetweenPathwayOptions getOptions(){
    return options;
  }


  /**
   * Get the directory that should be used to look for the score file
   */
  public File getCurrentDirectory(){
    return currentDirectory;
  }

  /**
   * Set the directory that should be used to look for the score file
   */
  public void setCurrentDirectory(File currentDirectory){
    this.currentDirectory = currentDirectory;
    Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
  }
  
  public void hide(){
    super.hide();
    synchronized(this){
      notify();
    }
  }
  
  public void dispose(){
    super.dispose();
    synchronized(this){
      notify();
    }
  }
}
