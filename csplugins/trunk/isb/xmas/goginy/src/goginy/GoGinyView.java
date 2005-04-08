package goginy;

// Java Import
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.Color;
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

public class GoGinyView 
  extends 
    JPanel 
  implements 
    PropertyChangeListener,
    GraphPerspectiveChangeListener {

  GraphPerspective gp;
  PGraphView view;
  Ontology ontology;

  HierarchicalLayoutListener layout;

  public GoGinyView ( Ontology ontology ) {
    super();
    this.ontology = ontology;

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

    this.add( view.getComponent() );
    //this.setSize( 800, 400 );
    this.layout();
    //this.setVisible( true );
    
  }

  public java.awt.Component getComponent () {
    return view.getComponent();
  }

  public void propertyChange ( PropertyChangeEvent event ) {
    refreshAll( new int[] {});
  }

  public void graphPerspectiveChanged ( GraphPerspectiveChangeEvent event ) {

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
      view.addNodeView( nodes[i] );
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
      view.addEdgeView( edges[i] );
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

    for ( int i = 0; i < selected_nodes.length; ++i ) {
      //System.out.println( "setting : "+selected_nodes[i] );
      view.getNodeView( selected_nodes[i] ).setUnselectedPaint( Color.orange );
    }
    
    layout.layout( view );

  }


}
