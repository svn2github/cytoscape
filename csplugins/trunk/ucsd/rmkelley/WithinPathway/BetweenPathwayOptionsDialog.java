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
import javax.swing.event.*;
import javax.swing.table.*; 
import java.awt.BorderLayout;
import java.awt.event.*;
import cytoscape.layout.*;
import java.awt.Dimension;
import java.beans.*;
import ucsd.rmkelley.Util.RyanDialog;

public class BetweenPathwayOptionsDialog extends RyanDialog implements PropertyChangeListener{
  NetworkSelectionPanel geneticPanel,physicalPanel;
  SearchOptionsPanel searchOptionsPanel;
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
    setTitle("Between-Pathway Search Options");
    geneticPanel = new NetworkSelectionPanel(this);
    physicalPanel = new NetworkSelectionPanel(this);
    searchOptionsPanel = new SearchOptionsPanel();
    /*
     * Register this dialog as a listener to its sub-panels
     */
    geneticPanel.getPropertyChangeSupport().addPropertyChangeListener(this);
    physicalPanel.getPropertyChangeSupport().addPropertyChangeListener(this);


    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add("Select Genetic Network",geneticPanel);
    tabbedPane.add("Select Physical Network",physicalPanel);
    tabbedPane.add("Additional Search Options",searchOptionsPanel);
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(tabbedPane,BorderLayout.CENTER);
    

    ok = new JButton("Start search");
    ok.setEnabled(false);
    setSearchToolTipText();
    JPanel southPanel = new JPanel();
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
	  options.selectedSearch = searchOptionsPanel.selectedSearch();
	  options.newScore = searchOptionsPanel.newScore();
	  options.generateCutoff = searchOptionsPanel.generateCutoff();
	  options.cutoff = searchOptionsPanel.getCutoff();
	  //options.alpha = searchOptionsPanel.getAlpha();
	  //options.iterations = searchOptionsPanel.getIterations();
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
  }

  
  boolean physicalValidated = false,geneticValidated;
  /**
   * Implemented as part of PropertyChangeListener contract
   */
  public void propertyChange(PropertyChangeEvent pce){
    if(pce.getSource() == geneticPanel){
      geneticValidated = geneticPanel.validateInput();
    }
    else if(pce.getSource() == physicalPanel){
      physicalValidated = physicalPanel.validateInput();
    }
    else{
      throw new RuntimeException("Unexpected source of property change");
    }
    if(geneticValidated && physicalValidated){
      ok.setEnabled(true);
    }
    else{
      ok.setEnabled(false);
    }
    setSearchToolTipText();
  }


  /**
   * Generates an informative tooltiptext for the "Begin Search" button
   */

  protected void setSearchToolTipText(){
    if(ok.isEnabled()){
      ok.setToolTipText("<html>This action will begin the<br> search for network models");
    }
    else{
      String text = "<html>In order to begin the search,<br>the following errors must be corrected<br>";
      Vector geneticErrors = geneticPanel.getErrors();
      if(geneticErrors.size() > 0){
	text += "<UL>";
	for(Iterator errorIt = geneticErrors.iterator();errorIt.hasNext();){
	  text += "<LI>" + errorIt.next() + " (genetic network)</LI><br>";
	}
      }
      Vector physicalErrors = physicalPanel.getErrors();
      if(physicalErrors.size() > 0){
	for(Iterator errorIt = physicalErrors.iterator();errorIt.hasNext();){
	  text += "<LI>" + errorIt.next() + " (physical network)</LI><br>";
	}
      }
      text += "</UL>";
      ok.setToolTipText(text);
    }

  }
}
