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

public class FilterUsePanel extends JPanel 
  implements PropertyChangeListener,
             ActionListener {

  FilterListPanel filterListPanel;
  JRadioButton hideFailed, grayFailed, selectPassed;
  JButton apply, addFilters, removeFilters;
  JList selectedFilters;
  JRadioButton and, or, xor;
  CyNetwork network;
  CyWindow window;

  JCheckBox select, gray, hide, overwrite;

  public FilterUsePanel ( CyNetwork network, CyWindow window ) {
    super();
    this.network = network;
    this.window = window;
    
    add( createActionPanel(), BorderLayout.NORTH );


    //--------------------//
    // Selected Filter Panel
    JPanel selected_filter_panel = new JPanel();
    selected_filter_panel.setBorder( new TitledBorder( "Selected Filters" ) );
    filterListPanel = new FilterListPanel( FilterListPanel.SHOW_TOGETHER );
    selected_filter_panel.add( filterListPanel );
    add( selected_filter_panel, BorderLayout.CENTER );

    ButtonGroup logic_group = new ButtonGroup();
    and = new JRadioButton( "AND", true );
    or = new JRadioButton( "OR", false );
    xor = new JRadioButton( "XOR", false );
    JPanel logic_panel = new JPanel();
    logic_panel.setBorder( new TitledBorder( "Filter Combo Type" ) );
    logic_panel.add( and );
    logic_panel.add( or );
    logic_panel.add( xor );
    logic_group.add( and );
    logic_group.add( or );
    logic_group.add( xor );
    selected_filter_panel.add( logic_panel, BorderLayout.NORTH );


    JPanel filter_control_panel = new JPanel();
    addFilters = new JButton( "<-" );
    addFilters.addActionListener( this );
    removeFilters = new JButton( "->" );
    removeFilters.addActionListener( this );
   


  
  }
  
  public void propertyChange ( PropertyChangeEvent e ) {

     if ( e.getPropertyName() == FilterListPanel.FILTER_SELECTED ) {
       // do something on a Filter Selected
     }

  }
  
  public void actionPerformed ( ActionEvent e ) {}


  protected void testObjects () {
    
    Filter[] filters = filterListPanel.getSelectedFilters();
    System.out.println( "Window: "+window );
    network = window.getNetwork();
    System.out.println( "Network: "+network );
    System.out.println( "GP: "+network.getGraphPerspective() );

    List nodes_list = network.getGraphPerspective().nodesList();
    List edges_list = network.getGraphPerspective().edgesList();
    Iterator nodes;
    Iterator edges;
    Node node;
    Edge edge;
    NodeView node_view;
    EdgeView edge_view;
    boolean passes;

    // and combo
    if ( and.isSelected() ) {
      nodes = nodes_list.iterator();
      while ( nodes.hasNext() ) {
        node = ( Node )nodes.next();
        passes = true;
        while ( passes ) {
          for ( int i = 0; i < filters.length; ++i ) {
            boolean passed = filters[i].passesFilter( node );
            if ( !passed ) {
              passes = false;
            }
          }
        }
        passObject( node, passes ); 
      }

      edges = edges_list.iterator();
      while ( edges.hasNext() ) {
        edge = ( Edge )edges.next();
        passes = true;
        while ( passes ) {
          for ( int i = 0; i < filters.length; ++i ) {
            boolean passed = filters[i].passesFilter( edge );
            if ( !passed ) {
              passes = false;
            }
          }
        }
        passObject( edge, passes ); 
      }
      
    }
    // or combo
    else if ( or.isSelected() ) {
      nodes = nodes_list.iterator();
      while ( nodes.hasNext() ) {
        node = ( Node )nodes.next();
        passes = false;
        while ( !passes ) {
          for ( int i = 0; i < filters.length; ++i ) {
            boolean passed = filters[i].passesFilter( node );
            if ( passed ) {
              passes = true;
            }
          }
        }
        passObject( node, passes ); 
      }

      edges = edges_list.iterator();
      while ( edges.hasNext() ) {
        edge = ( Edge )edges.next();
        passes = false;
        while ( !passes ) {
          for ( int i = 0; i < filters.length; ++i ) {
            boolean passed = filters[i].passesFilter( edge );
            if ( !passed ) {
              passes = true;
            }
          }
        }
        passObject( edge, passes ); 
      }


    }
    // xor combo
    else if ( xor.isSelected() ) {

      nodes = nodes_list.iterator();
      while ( nodes.hasNext() ) {
        node = ( Node )nodes.next();
        passes = false;
        boolean failed = false;
        for ( int i = 0; i < filters.length; ++i ) {
          boolean passed = filters[i].passesFilter( node );
          if ( passed && !failed ) {
            if ( passes ) {
              passes = false;
              failed = true;
            } else {
              passes = true;
            }
          }
        }        
        passObject( node, passes ); 
      }

      edges = edges_list.iterator();
      while ( edges.hasNext() ) {
        edge = ( Edge )edges.next();
        passes = false;
        boolean failed = false;
        for ( int i = 0; i < filters.length; ++i ) {
          boolean passed = filters[i].passesFilter( edge );
          if ( passed && !failed ) {
            if ( passes ) {
              passes = false;
              failed = true;
            } else {
              passes = true;
            }
          }
        }        
        passObject( edge, passes ); 
      }
    } 
  }

  /**
   * This method will take an object and do whatever it is supposed to 
   * according to what the available actions are.
   */
  protected void passObject( Object object, boolean passes ) {
    if (passes ) 
      System.out.println( "Object: "+object+" passed? "+passes );

  }


  public JPanel createActionPanel () {
    JPanel actionPanel = new JPanel();
    actionPanel.setBorder( new TitledBorder( "Available Actions" ) );

    select = new JCheckBox( "Select Passed" );
    gray = new JCheckBox( "Gray Failed" );
    hide = new JCheckBox( "Hide Failed" );
    overwrite = new JCheckBox( "Overwrite State" );

    JPanel boxes = new JPanel();
    boxes.setLayout( new GridLayout( 0, 1 ) );
    boxes.add( select );
    boxes.add( gray );
    boxes.add( hide );

    actionPanel.add( boxes );
    actionPanel.add( overwrite );

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
