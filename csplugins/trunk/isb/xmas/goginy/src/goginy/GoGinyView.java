package goginy;

// Java Import
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.*;
// violin Strings Import
import ViolinStrings.Strings;

// colt import
import cern.colt.map.*;
import cern.colt.list.*;

import giny.model.*;
import fing.model.*;
import giny.view.*;
import phoebe.*;

import goginy.layout.HierarchicalLayoutListener;
import ViolinStrings.Strings;

import java.beans.*;

import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import phoebe.*;
public class GoGinyView
  implements 
    ActionListener,
    PropertyChangeListener,
    GraphPerspectiveChangeListener {

  GraphPerspective gp;
  PGraphView view;
  Ontology ontology;

  HierarchicalLayoutListener layout;

  JButton searchButton;
  JTextField searchField;
  
  JSplitPane split;
  
  
  public GoGinyView ( Ontology ontology ) {
    super();
    this.ontology = ontology;


    ontology.pcs.addPropertyChangeListener( this );

    layout = new HierarchicalLayoutListener();
    
    gp = ontology.getGraphPerspective();

    // create the View
    view = new PGraphView( "goginy",  gp );
    view.enableNodeSelection();
    view.disableEdgeSelection();
    view.setBackgroundPaint( Color.white );
    
    gp.addGraphPerspectiveChangeListener( this );

    startView();

    refreshAll( new int[] {});



    searchButton = new JButton( "Search" );
    searchButton.addActionListener( this );
    searchField = new JTextField( 20 );

    JPanel lower = new JPanel();
    lower.add( searchButton );
    lower.add( searchField );
    lower.setPreferredSize( new java.awt.Dimension( 200, 30 ) );
   
    split = new JSplitPane( JSplitPane.VERTICAL_SPLIT,  view.getComponent(), lower );
    split.setResizeWeight(1);
  }

  public java.awt.Component getComponent () {
    return split;
  }

  public int[] getSelectedNodeIndices() {
    return view.getSelectedNodeIndices();
  }
  

  public void actionPerformed ( ActionEvent e ) {
    ontology.searchFor( searchField.getText() );
  } 

  public void propertyChange ( PropertyChangeEvent event ) {
    refreshAll( (int[])event.getNewValue() );
  }

  public void graphPerspectiveChanged ( GraphPerspectiveChangeEvent event ) {

    //System.out.println( "GP EVENT: "+event );

    // nodes restored
    if ( event.isNodesRestoredType() ) 
      addNodes( event.getRestoredNodeIndices() );
    
    // nodes hidden
    if ( event.isNodesHiddenType() ) 
      removeNodes( event.getHiddenNodeIndices() );
    
    // edges restored
    if ( event.isEdgesRestoredType() ) 
      addEdges( event.getRestoredEdgeIndices() );
    
    // edges hidden
    if ( event.isEdgesHiddenType() ) 
      removeEdges( event.getHiddenEdgeIndices() );

  } 

  protected void startView () {

    for ( Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
      NodeView nv = ( NodeView )i.next();
      nv.setUnselectedPaint( java.awt.Color.lightGray );
      nv.setShape( nv.RECTANGLE );
      edu.umd.cs.piccolo.PNode label = ( edu.umd.cs.piccolo.PNode )nv.getLabel();
      ( ( Label )label).setText( ( String )ontology.descriptionForUID( nv.getRootGraphIndex() ) );
      nv.setWidth( label.getWidth() +6);
      nv.setHeight( label.getHeight() +6);
    }

    for ( Iterator i = view.getEdgeViewsIterator(); i.hasNext(); ) {
      EdgeView ev = ( EdgeView )i.next();
      ev.setUnselectedPaint( Color.gray );
      ev.setTargetEdgeEndPaint( Color.gray ); 
      ev.setSourceEdgeEnd( EdgeView.NO_END );
    }
  }

  private void addNodes ( int[] nodes ) {
    for ( int i = 0; i < nodes.length; ++i ) {
      NodeView nv = view.addNodeView( nodes[i] );
    
      nv.setShape( nv.RECTANGLE );
      edu.umd.cs.piccolo.PNode label = ( edu.umd.cs.piccolo.PNode )nv.getLabel();
      ( ( Label )label).setText( ( String )ontology.descriptionForUID( nodes[i] ) );
      nv.setWidth( label.getWidth() +6);
      nv.setHeight( label.getHeight() +6);
    }

  }

  private void removeNodes ( int[] nodes ) {
    for ( int i = 0; i < nodes.length; ++i ) 
      view.removeNodeView( nodes[i] );
  }

  private void addEdges ( int[] edges ) {
    for ( int i = 0; i < edges.length; ++i ) {
      EdgeView ev = view.addEdgeView( edges[i] );
      ev.setUnselectedPaint( Color.gray );
      ev.setTargetEdgeEndPaint( Color.gray ); 
      ev.setSourceEdgeEnd( EdgeView.NO_END );
    }
  }

  private void removeEdges ( int[] edges ) {
    for ( int i = 0; i < edges.length; ++i ) 
      view.removeEdgeView( edges[i] );
  }

  /**
   * THis does the new layout and colors and what not...
   */
  private void refreshAll ( int[] selected_nodes ) {
   
    selected_nodes = ontology.flagged_nodes;

    for ( Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
      NodeView nv = ( NodeView )i.next();
      nv.setUnselectedPaint( java.awt.Color.lightGray );
      

      //insert extra stuff here

    }

   
    
    layout.layout( view );

    if ( selected_nodes.length == 0 ) {return;}
    double bigX;
    double bigY;
    double smallX;
    double smallY;
    double W;
    double H;
    NodeView first = ( NodeView )view.getNodeView( selected_nodes[0] );
    bigX = first.getXPosition();
    smallX = bigX;
    bigY = first.getYPosition();
    smallY = bigY;
    
    for ( int i = 0; i < selected_nodes.length; ++i ) {
    
      if ( view.getNodeView( selected_nodes[i] ) == null ) {
      } else {
        NodeView nv = view.getNodeView( selected_nodes[i] );
        nv.setUnselectedPaint( Color.orange );
        double x = nv.getXPosition();
        double y = nv.getYPosition();

        if ( x > bigX ) {
          bigX = x;
        } else if ( x < smallX ) {
          smallX = x;
        }
        
          if ( y > bigY ) {
            bigY = y;
          } else if ( y < smallY ) {
            smallY = y;
          }
      }
    }

    PBounds zoomToBounds;
    if (selected_nodes.length == 1) {
      zoomToBounds = new PBounds( smallX - 100 , smallY - 100 , ( bigX - smallX + 200 ), ( bigY - smallY + 200 ) );
    } else {
      zoomToBounds = new PBounds( smallX - 100 , smallY - 100 , ( bigX - smallX + 100 ), ( bigY - smallY + 100 ) );
    }
    PTransformActivity activity =  ( ( PGraphView )view).getCanvas().getCamera().animateViewToCenterBounds( zoomToBounds, true, 500 );


  }


}
