package csplugins.layout;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.ui.JTask;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.util.*;
import java.awt.event.*;

import java.beans.*;
import java.io.*;


public class JGraphLayoutMenu extends JMenu {


  public JGraphLayoutMenu () {
    super("JGraph Layouts");
    initialize();
  }

  protected void initialize () {

    // NOTE: the following layout has been commented out because it is broken.
  /*
    JMenuItem dot = new JMenuItem( new AbstractAction( "Dot" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphExport export = new JGraphExport( Cytoscape.getCurrentNetworkView(), 0 );
                export.doExport();
                
              } } ); } } );
    this.add( dot );
  */

    // NOTE: the following layout has been commented out because it is broken.
  /*
    JMenuItem anneal = new JMenuItem( new AbstractAction( "Annealing" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 0, 0 );
                layout.doLayout();
                
              } } ); } } );
    this.add( anneal );
  */

    // NOTE: the following layout has been commented out because it is broken.
  /*
    JMenuItem moen = new JMenuItem( new AbstractAction( "Moen" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 1, 0 );
                layout.doLayout();
                
              } } ); } } );
    this.add( moen );
  */

    // NOTE: the following layout has been commented out because it is not
    //       needed.

  /*
 JMenuItem circle = new JMenuItem( new AbstractAction( "Circle" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 2, 0 );
                layout.doLayout();
                
              } } ); } } );
    this.add( circle );
  */

    JMenuItem radial = new JMenuItem( new AbstractAction( "Radial" ) {
      public void actionPerformed ( ActionEvent e ) {
        Task layoutTask = new Task()
        //SwingUtilities.invokeLater(new Runnable()
	{
          public void run()
	  {
	    JGraphLayoutWrapper layout = new JGraphLayoutWrapper
	      (Cytoscape.getCurrentNetworkView(),
	       JGraphLayoutWrapper.RADIAL_TREE);
	       
	    layout.doLayout();
          }
	  
	  public String getTitle()
	  {
	    return "Performing Radial Layout...";
	  }

	  public void halt() {}
	  public void setTaskMonitor(TaskMonitor _monitor) {}
        }; // end new Task()
	
	JTaskConfig taskConfig = getNewDefaultTaskConfig();
	TaskManager.executeTask(layoutTask, taskConfig);
	
      } // end actionPerformed()
      
    }); // end new AbstractAction()
    this.add( radial );

    // NOTE: the following layout has been commented out because it is broken.
/*
  JMenuItem gem = new JMenuItem( new AbstractAction( "GEM" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 4, 0 );
                 layout.doLayout();
             
               } } ); } } );
    this.add( gem );
*/

    JMenuItem spring = new JMenuItem( new AbstractAction( "Spring" ) {
      public void actionPerformed ( ActionEvent e ) {
        
	Task layoutTask = new Task()
        //SwingUtilities.invokeLater(new Runnable() 
	{
          public void run()
	  {
	    JGraphLayoutWrapper layout = new JGraphLayoutWrapper
	      (Cytoscape.getCurrentNetworkView(),
	       JGraphLayoutWrapper.SPRING_EMBEDDED);
	    layout.doLayout();
          }
	  
	  public String getTitle()
	  {
	    return "Performing Spring Layout...";
	  }

	  public void halt() {}
	  public void setTaskMonitor(TaskMonitor _monitor) {}
        }; // end new Task()
	
	JTaskConfig taskConfig = getNewDefaultTaskConfig();
	TaskManager.executeTask(layoutTask, taskConfig);
	
      } // end actionPerformed()
      
    }); // end new AbstractAction()
    
    this.add( spring );

    JMenuItem sug = new JMenuItem( new AbstractAction( "Sugiyama" ) {
      public void actionPerformed ( ActionEvent e ) {
        Task layoutTask = new Task()
        {
          public void run()
	  {
	    JGraphLayoutWrapper layout = new JGraphLayoutWrapper
	      (Cytoscape.getCurrentNetworkView(),
	       JGraphLayoutWrapper.SUGIYAMA);
	    layout.doLayout();
          }
	  
	  public String getTitle()
	  {
	    return "Performing Sugiyama Layout...";
	  }

	  public void halt() {}
	  public void setTaskMonitor(TaskMonitor _monitor) {}
        }; // end new Task()
	
	JTaskConfig taskConfig = getNewDefaultTaskConfig();
	TaskManager.executeTask(layoutTask, taskConfig);
	
      } // end actionPerformed()
      
    }); // end new AbstractAction()
    this.add( sug );

    // NOTE: the following layout has been commented out because it is broken.
  /*
  JMenuItem tree = new JMenuItem( new AbstractAction( "Tree" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView() );
                 layout.doLayout(7, 0);
                
               } } ); } } );
    this.add( tree );
  */

  }

  private JTaskConfig getNewDefaultTaskConfig()
  {
    JTaskConfig result = new JTaskConfig();
    
    result.displayCancelButton(false);
    result.displayCloseButton(false);
    result.displayStatus(false);
    result.displayTimeElapsed(false);
    result.displayTimeRemaining(false);
    result.setAutoDispose(true);
    result.setModal(true);
    result.setOwner(Cytoscape.getDesktop());
    
    return result;
  }
}
