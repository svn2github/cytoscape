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
  JRadioButton and, or, xor;
  CyNetwork network;
  CyWindow window;

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
    //selected_filter_panel.setBorder( new TitledBorder( "Available Filters" ) );
    filterListPanel = new FilterListPanel( FilterListPanel.SHOW_TOGETHER );
    selected_filter_panel.add( filterListPanel );
    

    //--------------------//
    // Use Panel
    JPanel use_panel = new JPanel();
    use_panel.setBorder( new TitledBorder( "Take Action and Combine" ) );
    use_panel.add( createActionPanel(), BorderLayout.SOUTH );
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
    use_panel.add( logic_panel, BorderLayout.NORTH );


    JSplitPane pane0 = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, selected_filter_panel, use_panel );
    JSplitPane pane1 = new JSplitPane( JSplitPane.VERTICAL_SPLIT, filterEditorPanel, pane0 );
    add( pane1 );

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
        for ( int i = 0; i < filters.length; ++i ) {
          boolean passed = filters[i].passesFilter( node );
          if ( !passed ) {
            passes = false;
          }
        }
        passObject( node, passes ); 
      }

      edges = edges_list.iterator();
      while ( edges.hasNext() ) {
        edge = ( Edge )edges.next();
        passes = true;
        for ( int i = 0; i < filters.length; ++i ) {
          boolean passed = filters[i].passesFilter( edge );
          if ( !passed ) {
            passes = false;
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
        for ( int i = 0; i < filters.length; ++i ) {
          boolean passed = filters[i].passesFilter( node );
          if ( passed ) {
            passes = true;
          }
        }
        passObject( node, passes ); 
      }

      edges = edges_list.iterator();
      while ( edges.hasNext() ) {
        edge = ( Edge )edges.next();
        passes = false;
        for ( int i = 0; i < filters.length; ++i ) {
          boolean passed = filters[i].passesFilter( edge );
          if ( !passed ) {
            passes = true;
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
    if (passes ) {
      
      if ( object instanceof Node ) {
        NodeView nv =   window.getView().getNodeView( ( Node )object );
        
        System.out.println( "Node: "+nv+" passes" );

        // things to do if passes
        if ( select.isSelected() ) {
          nv.setSelected( true );
        }
      
        if ( pulsate.isSelected() ) {
          final PNodeView node = ( PNodeView )nv;

          PActivityScheduler scheduler = node.getRoot().getActivityScheduler();
          
         
          PAffineTransform at_shrink =  node.getTransformReference( true );
          PAffineTransform at_grow = node.getTransform();
          at_grow.scaleAboutPoint( 10, node.getX() + .5 * node.getWidth(), node.getY() + .5 * node.getHeight());

          node.moveToFront();
          PColorActivity repeatReversePulseActivity = new PColorActivity( 500, 0, 12, PInterpolatingActivity.SOURCE_TO_DESTINATION_TO_SOURCE, new PColorActivity.Target() {
              public Color getColor() {
                return (Color) node.getPaint();
              }
              public void setColor(Color color) {
                node.setPaint(color);
              }
            }, Color.WHITE);
          
          PActivity grow = node.animateToTransform( at_grow, 3000 );
          PActivity shrink = node.animateToTransform( at_shrink, 3000 );

          scheduler.addActivity( repeatReversePulseActivity );
          scheduler.addActivity( grow );
          shrink.startAfter( grow );


        } else if ( spiral.isSelected() ) {

          final PNodeView node = ( PNodeView )nv;
          PActivityScheduler scheduler = node.getRoot().getActivityScheduler();
                   
          PAffineTransform at_shrink =  node.getTransformReference( true );
          PAffineTransform at_grow = node.getTransform();
          double x = node.getX();
          double y = node.getY();
          at_grow.scaleAboutPoint( 10, x + .5 * node.getWidth(), y + .5 * node.getHeight());

          PAffineTransform at_rotate = node.getTransform();
          at_rotate.setRotation( Math.PI );
         //  PTransformActivity rotate = new PTransformActivity( 3000, 0, 2, PInterpolatingActivity.SOURCE_TO_DESTINATION_TO_SOURCE, new PTransformActivity.Target() {
//               public void getSourceMatrix ( double[] aSource ) {
//                 node.getTransformReference( true ).getMatrix( aSource );
//               }
//               public void setTransform ( AffineTransform aTransform ) {
//                 node.setTransform( aTransform );
//               }
//             },  at_rotate );
          

          node.moveToFront();
          
          PActivity rotate = node.animateToPositionScaleRotation(  x + .5 * node.getWidth(), y + .5 * node.getHeight(), 10,  Math.PI, 3000 );
          //PActivity grow = node.animateToTransform( at_grow, 3000 );
          PActivity shrink = node.animateToTransform( at_shrink, 3000 );

          //scheduler.addActivity( rotate );
          //scheduler.addActivity( grow );
          shrink.startAfter( rotate );
        }


        if ( overwrite.isSelected() ) {
          // things to overwrite if passes
          if ( gray.isSelected() ) {
            nv.setTransparency( 1f );
          } 
          if ( hide.isSelected() ) {
            ( ( phoebe.PGraphView )window.getView() ).showNodeView( nv );
          }
        }
      } 

      else if ( object instanceof Edge ) {
        EdgeView nv =   window.getView().getEdgeView( ( Edge )object );
        
        System.out.println( "Edge: "+nv+" passes" );

        // things to do if passes
        if ( select.isSelected() ) {
          nv.setSelected( true );
        }
      
        if ( overwrite.isSelected() ) {
          // things to overwrite if passes
          if ( gray.isSelected() ) {
            // nv.setTransparency( 1f );
          } 
          if ( hide.isSelected() ) {
            ( ( phoebe.PGraphView )window.getView() ).showEdgeView( nv );
          }
        }
      } 



    } else {
      if ( object instanceof Node ) {
        NodeView nv =   window.getView().getNodeView( ( Node )object );
        
        // things to do if failed
        if ( gray.isSelected() ) {
          nv.setTransparency( 0.5f );
        } 
        if ( hide.isSelected() ) {
          ( ( phoebe.PGraphView )window.getView() ).hideNodeView( nv );
        }
        if ( overwrite.isSelected() ) {
          // things to overwrite if failed
          if ( select.isSelected() ) {
            nv.setSelected( false );
          }
        }
        
      }

      else if ( object instanceof Edge ) {
         EdgeView nv =   window.getView().getEdgeView( ( Edge )object );
        
         // things to do if failed
         if ( gray.isSelected() ) {
           //nv.setTransparency( 0.5f );
         } 
         if ( hide.isSelected() ) {
           ( ( phoebe.PGraphView )window.getView() ).hideEdgeView( nv );
         }
         if ( overwrite.isSelected() ) {
          // things to overwrite if failed
           if ( select.isSelected() ) {
             nv.setSelected( false );
           }
         }
        
      }


    }
  }

  public JPanel createActionPanel () {
    JPanel actionPanel = new JPanel();
    actionPanel.setBorder( new TitledBorder( "Available Actions" ) );

    select = new JCheckBox( "Select Passed" );
    gray = new JCheckBox( "Gray Failed" );
    hide = new JCheckBox( "Hide Failed" );
    pulsate = new JRadioButton( "Pulsate" );
    spiral = new JRadioButton( "Spiral" );
    overwrite = new JCheckBox( "Overwrite State" );

    JPanel boxes = new JPanel();
    boxes.setLayout( new GridLayout( 0, 1 ) );
    boxes.add( select );
    boxes.add( gray );
    boxes.add( hide );
    boxes.add( pulsate );
    boxes.add( spiral );
    
    ButtonGroup g = new ButtonGroup();
    g.add( spiral );
    g.add( pulsate );

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
