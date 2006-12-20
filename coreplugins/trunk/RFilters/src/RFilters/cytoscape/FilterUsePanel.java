package filter.cytoscape;

import javax.swing.*;
import javax.swing.border.*;
import filter.model.*;
import filter.view.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.util.List;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import giny.model.*;
import giny.view.*;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.util.*;
import java.awt.geom.*;
import phoebe.*;

public class FilterUsePanel extends JPanel 
  implements PropertyChangeListener,
             ActionListener {
  FilterEditorPanel filterEditorPanel;
  FilterListPanel filterListPanel;
  JRadioButton hideFailed, grayFailed, selectPassed;
  JButton apply, addFilters, removeFilters;
  JList selectedFilters;
  JDialog createFilterDialog;

  JButton addButton,removeButton;
  JCheckBox select, gray, hide,  overwrite;
  JRadioButton pulsate, spiral;
  JFrame frame;
  public static String NEW_FILTER_ADDED = "NEW_FILTER_ADDED";
  
  private Runnable updateProgMon, closeProgMon, setMinAndMax;
  private int progressCount = 0, minCount=0, maxCount=100;
  private String progressNote = "";
  private String progressTitle = "Applying filter";
  private boolean applyFilterCanceled = false;

  public FilterUsePanel ( JFrame frame ) {
    super();
    this.frame = frame;
	frame.setPreferredSize(new Dimension(700,300));

    setLayout(new BorderLayout());
    //--------------------//
    // FilterEditorPanel
    filterEditorPanel = new FilterEditorPanel();
    filterEditorPanel.getPropertyChangeSupport().addPropertyChangeListener(this);

    //--------------------//
    // Selected Filter Panel
    JPanel selected_filter_panel = new JPanel();
    selected_filter_panel.setLayout(new BorderLayout());
    filterListPanel = new FilterListPanel( );
    selected_filter_panel.add( filterListPanel,BorderLayout.CENTER );
    selected_filter_panel.add(createManagePanel(),BorderLayout.NORTH);
    selected_filter_panel.add(createActionPanel(),BorderLayout.SOUTH);

    JSplitPane pane0 = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, selected_filter_panel, filterEditorPanel );
    add(pane0);
    filterListPanel.getSwingPropertyChangeSupport().addPropertyChangeListener( filterEditorPanel );
    filterListPanel.getSwingPropertyChangeSupport().addPropertyChangeListener( this );
    initProgressMonitor();
    }
  
  public FilterListPanel getFilterListPanel () {
    return filterListPanel;
  }

  public void propertyChange ( PropertyChangeEvent e ) {

    if ( e.getPropertyName() == FilterListPanel.FILTER_SELECTED ) {
      removeButton.setEnabled(true);
      apply.setEnabled(true);
      // do something on a Filter Selected
    }else if ( e.getPropertyName() == FilterListPanel.NO_SELECTION ) {
      removeButton.setEnabled(false);
      apply.setEnabled(false);
    } // end of if ()
    else if ( e.getPropertyName() == FilterEditorPanel.ACTIVE_PANEL_CHANGED ){
    	frame.setPreferredSize(new Dimension(700,300));
      frame.pack();
    }
    else if ( e.getPropertyName() == NEW_FILTER_ADDED){
    	//Select the new filter just created
    	//New filter created is always added at the end of a vector
    	int lastIndex = filterListPanel.getFilterList().getModel().getSize()-1;
        filterListPanel.getFilterList().setSelectedIndex(lastIndex);    	
    }    
   }
    
  public JPanel createManagePanel(){
    JPanel result = new JPanel();
    result.setBorder(new TitledBorder("Manage Filters"));
    addButton = new JButton("Create new filter");
    addButton.addActionListener(this);
    removeButton = new JButton("Remove selected filter");
    removeButton.addActionListener(this);
    removeButton.setEnabled(false);
    result.add(addButton);
    result.add(removeButton);
    return result;
  }

  public void actionPerformed ( ActionEvent e ) {
    if ( e.getSource() == addButton ) {
      //System.out.println( "Adding Filter from selected editor: "+getSelectedEditor() );
      if (createFilterDialog == null){
        createFilterDialog = new CreateFilterDialog(FilterEditorManager.defaultManager());
      }

      int pre_filterCount = filterListPanel.getFilterList().getModel().getSize();
      createFilterDialog.setVisible(true);
      int post_filterCount = filterListPanel.getFilterList().getModel().getSize();
      
      //Fire an event to notify the JList to select it, if new filter is added
      if (post_filterCount - pre_filterCount >0) {
          java.beans.PropertyChangeEvent evt = new java.beans.PropertyChangeEvent(this, NEW_FILTER_ADDED,null,null);
          filterListPanel.getSwingPropertyChangeSupport().firePropertyChange(evt);    	  
      }
    }
    if ( e.getSource() == removeButton ) {
      Filter filter = filterListPanel.getSelectedFilter();
      if(filter != null){
        FilterManager.defaultManager().removeFilter( filter);
      }
    }
  }

  private void initProgressMonitor()
  {
          progressTitle = "Applying filter";
          progressNote = "";
          final ProgressMonitor pm = new ProgressMonitor(frame, progressTitle,progressNote,0,100);
          pm.setMillisToDecideToPopup(500);
          pm.setMillisToPopup(2000);

          // Create these as inner classes to minimize the amount of thread creation (expensive!)...
          updateProgMon = new Runnable()
          {
              double dCount = 0;
              public void run()
              {
                  if (pm.isCanceled())
                  {
                	  applyFilterCanceled = true;
                      pm.close();
                  }
                  else
                  {
                      dCount = ((double)progressCount/(double)pm.getMaximum());
                      progressNote = ((int)(dCount * 100.0) +"% complete");
                      pm.setProgress(progressCount);
                      pm.setNote(progressNote);
                  }
              }
          };

          closeProgMon = new Runnable()
          {
              public void run()
              {
                  pm.close();
              }
          };

          setMinAndMax = new Runnable()
          {
              public void run()
              {
                  pm.setMinimum(minCount);
                  pm.setMaximum(maxCount);
              }
          };      
  }

  
  /**
   * This method will take an object and do whatever it is supposed to 
   * according to what the available actions are.
   */
  protected void passObject( Object object, boolean passes ) {
    if (passes ) {
      
      if ( object instanceof Node ) {
        Cytoscape.getCurrentNetwork().setSelectedNodeState((Node)object,true);
      } 
      else if ( object instanceof Edge ) {
        Cytoscape.getCurrentNetwork().setSelectedEdgeState((Edge)object,true);
      } 
    }
  }

  protected void testObjects () {
    
    Filter filter = filterListPanel.getSelectedFilter();
    CyNetwork network = Cytoscape.getCurrentNetwork();
				
    List nodes_list = network.nodesList();
    List edges_list = network.edgesList();
    Iterator nodes;
    Iterator edges;
    Node node;
    Edge edge;
    NodeView node_view;
    EdgeView edge_view;
    boolean passes;
    if(filter != null){
      Class [] passingTypes = filter.getPassingTypes();
      
      minCount = 0;
      maxCount = getTaskEstimation(passingTypes, nodes_list, edges_list);
      progressCount = 0;
      applyFilterCanceled = false;

      try
      {
          SwingUtilities.invokeAndWait(setMinAndMax);
      }
      catch (Exception _e) {}
      
      for(int idx = 0;idx < passingTypes.length;idx++){
        if(passingTypes[idx].equals(Node.class)){
          nodes = nodes_list.iterator();
          while ( nodes.hasNext() ) {
            node = ( Node )nodes.next();
 
            try{
            	passObject(node,filter.passesFilter(node));
            }catch(StackOverflowError soe){
              return;
            }
            
        	progressCount ++;
        	try
            {
                SwingUtilities.invokeAndWait(updateProgMon);
            }
            catch (Exception _e) {}
            
            if (applyFilterCanceled)
            {
                progressNote = "Please wait...";
                try
                {
                    SwingUtilities.invokeAndWait(updateProgMon);
                }
                catch (Exception _e) {}
                break;
            }            
          }
        }else if(passingTypes[idx].equals(Edge.class)){
          edges = edges_list.iterator();
          while ( edges.hasNext() ) {
            edge = ( Edge )edges.next();
            try{
            	passObject(edge,filter.passesFilter(edge));
            }catch(StackOverflowError soe){
              return;
            }
            
           	progressCount++;
        	try
            {
                SwingUtilities.invokeAndWait(updateProgMon);
            }
            catch (Exception _e) {}
            if (applyFilterCanceled)
            {
                progressNote = "Please wait...";
                try
                {
                    SwingUtilities.invokeAndWait(updateProgMon);
                }
                catch (Exception _e) {}
                break;
            }
          }
        }
      }// for loop
      try
      {
          SwingUtilities.invokeAndWait(closeProgMon);
      }
      catch (Exception _e) {}
    }
  }
  
  // Calculate the total tasks for the progress monitor
  private int getTaskEstimation(Class[] pClassTypes, List pNodeList, List pEdgeList)
  {
	  int taskCount = 0;
      for(int idx = 0;idx < pClassTypes.length;idx++){
          if(pClassTypes[idx].equals(Node.class)){
        	  taskCount += pNodeList.size();  
          }else if(pClassTypes[idx].equals(Edge.class)){
        	  taskCount += pEdgeList.size();        	 
          }
      }
	  return taskCount;
  }
  

  public JPanel createActionPanel () {
	    JPanel actionPanel = new JPanel();
	    //actionPanel.setBorder( new TitledBorder( "Available Actions" ) );

	    //select = new JCheckBox( "Select Passed" );
	    //hide = new JCheckBox( "Hide Failed" );
	    apply = new JButton ( new AbstractAction( "Apply selected filter" ){
	        public void actionPerformed(ActionEvent e){
	        	// We have to run "Apply filter" in a seperate thread, becasue we want to monitor the progress
	        	ApplyFilterThread applyFilterThread = new ApplyFilterThread();
	        	applyFilterThread.start();
	        }});

	    apply.setEnabled(false);
	    //actionPanel.add(select);
	    //actionPanel.add(hide);
	    actionPanel.add(apply);
	    return actionPanel;
	  }  

  
  private class ApplyFilterThread extends Thread {
      public void run(){    	  
          testObjects();
          Cytoscape.getCurrentNetworkView().updateView();
    }	  
  }
}
