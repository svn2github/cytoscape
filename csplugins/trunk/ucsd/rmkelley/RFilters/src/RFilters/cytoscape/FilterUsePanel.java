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
import cytoscape.CyNetwork;
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
  CyNetwork network;
  CyWindow window;
  JButton addButton,removeButton;
  JCheckBox select, gray, hide,  overwrite;
  JRadioButton pulsate, spiral;

  public FilterUsePanel ( CyNetwork network, CyWindow window ) {
    super();
    this.network = network;
    this.window = window;

    //--------------------//
    // FilterEditorPanel
    filterEditorPanel = new FilterEditorPanel();

    //--------------------//
    // Selected Filter Panel
    JPanel selected_filter_panel = new JPanel();
    selected_filter_panel.setLayout(new BorderLayout());
    //selected_filter_panel.setBorder( new TitledBorder( "Available Filters" ) );
    filterListPanel = new FilterListPanel( );
    selected_filter_panel.add( filterListPanel,BorderLayout.CENTER );
    selected_filter_panel.add(createManagePanel(),BorderLayout.NORTH);
    selected_filter_panel.add(createActionPanel(),BorderLayout.SOUTH);

    JSplitPane pane0 = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, selected_filter_panel, filterEditorPanel );
    //JSplitPane pane1 = new JSplitPane( JSplitPane.VERTICAL_SPLIT, filterEditorPanel, pane0 );
    //add( pane0 );
    add(pane0);
    filterListPanel.getSwingPropertyChangeSupport().addPropertyChangeListener( filterEditorPanel );
  
  }
  
  public FilterListPanel getFilterListPanel () {
    return filterListPanel;
  }

  public void propertyChange ( PropertyChangeEvent e ) {

    if ( e.getPropertyName() == FilterListPanel.FILTER_SELECTED ) {
      // do something on a Filter Selected
    }

  }
  public JPanel createManagePanel(){
    JPanel result = new JPanel();
    result.setBorder(new TitledBorder("Manage Filters"));
    addButton = new JButton("Create new filter");
    addButton.addActionListener(this);
    removeButton = new JButton("Remove selected filter");
    removeButton.addActionListener(this);
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
      createFilterDialog.show();
    }
    if ( e.getSource() == removeButton ) {
      Filter filter = filterListPanel.getSelectedFilter();
      if(filter != null){
	FilterManager.defaultManager().removeFilter( filter);
      }
    }
  }


  /**
   * This method will take an object and do whatever it is supposed to 
   * according to what the available actions are.
   */
  protected void passObject( Object object, boolean passes ) {
    if (passes ) {
      
      if ( object instanceof Node ) {
	NodeView nv =   window.getView().getNodeView( ( Node )object );
	if ( select.isSelected() ) {
	  nv.setSelected( true );
	}
      } 
      else if ( object instanceof Edge ) {
	EdgeView nv =   window.getView().getEdgeView( ( Edge )object );
	if ( select.isSelected() ) {
	  nv.setSelected( true );
	}
      } 
    } else {
      if ( object instanceof Node ) {
	NodeView nv =   window.getView().getNodeView( ( Node )object );
	if ( hide.isSelected() ) {
	  ( ( phoebe.PGraphView )window.getView() ).hideNodeView( nv );
	}
      }
	    
      else if ( object instanceof Edge ) {
	EdgeView nv =   window.getView().getEdgeView( ( Edge )object );

	if ( hide.isSelected() ) {
	  ( ( phoebe.PGraphView )window.getView() ).hideEdgeView( nv );
	}
      }
    }
  }

  protected void testObjects () {
    
    Filter filter = filterListPanel.getSelectedFilter();
    //System.out.println( "Window: "+window );
    network = window.getNetwork();
    //System.out.println( "Network: "+network );
    //System.out.println( "GP: "+network.getGraphPerspective() );

    List nodes_list = network.getGraphPerspective().nodesList();
    List edges_list = network.getGraphPerspective().edgesList();
    Iterator nodes;
    Iterator edges;
    Node node;
    Edge edge;
    NodeView node_view;
    EdgeView edge_view;
    boolean passes;
    if(filter != null){
      nodes = nodes_list.iterator();
      while ( nodes.hasNext() ) {
        node = ( Node )nodes.next();
	try{
	  passObject(node,filter.passesFilter(node));
	}catch(StackOverflowError soe){
	  return;
	}
      }
      
      edges = edges_list.iterator();
      while ( edges.hasNext() ) {
        edge = ( Edge )edges.next();
	try{
	  passObject(edge,filter.passesFilter(edge));
	}catch(StackOverflowError soe){
	  return;
	}
      }
    }
  }

  public JPanel createActionPanel () {
    JPanel actionPanel = new JPanel();
    actionPanel.setBorder( new TitledBorder( "Available Actions" ) );

    select = new JCheckBox( "Select Passed" );
    hide = new JCheckBox( "Hide Failed" );
    actionPanel.add(select);
    actionPanel.add(hide);

    actionPanel.add( new JButton (new AbstractAction( "Go!" ) {
	public void actionPerformed ( ActionEvent e ) {
	  // Do this in the GUI Event Dispatch thread...
	  SwingUtilities.invokeLater( new Runnable() {
	      public void run() {
		//do the action
		testObjects();
	      } //run
	    } ); } } ) );
    return actionPanel;
  }
  
}
