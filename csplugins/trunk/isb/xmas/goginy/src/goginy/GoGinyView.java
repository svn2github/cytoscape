package goginy;

// Java Import
import java.util.*;
import java.io.*;
import javax.swing.*;

// Violin Strings Import
import ViolinStrings.Strings;

// colt import
import cern.colt.map.*;

import giny.model.*;
import fing.model.*;
import giny.view.*;
import phoebe.*;

import goginy.layout.HierarchicalLayoutListener;



public class GoGinyView extends JFrame {

  RootGraph root;
  GraphPerspective gp;
  OpenIntIntHashMap uidGidMap;
  OpenIntIntHashMap gidUidMap;
  Map gdescGidMap;
  OpenIntObjectHashMap gidGdescMap;
  PGraphView view;

  HierarchicalLayoutListener layout;

  public GoGinyView ( String obo ) {
    super("GoGiny");
    
    GoParser.parseOBO( obo );
    root = GoParser.root;
    uidGidMap = GoParser.uidGidMap;
    gidUidMap = GoParser.gidUidMap;
    gdescGidMap =  GoParser.gdescGidMap;
    gidGdescMap = GoParser.gidGdescMap;

    layout = new HierarchicalLayoutListener();

  
    // create the sub graph
    int[] nodes = root.getNodeIndicesArray();
    int[] edges = root.getEdgeIndicesArray();
    gp = root.createGraphPerspective( nodes, edges );
    System.out.println( "GP: "+gp);

    // create the View
    view = new PGraphView( "goginy",  gp );

    //gp.restoreNode( -50 );
    //view.addNodeView( -50 );

    refreshAll();

    view.enableNodeSelection();
    view.disableEdgeSelection();
    
    

    this.getContentPane().add( view.getComponent() );

    this.layout();
    this.setVisible( true );
    
  }


  private void refreshAll () {
    
    for ( Iterator i = view.getNodeViewsIterator(); i.hasNext(); ) {
      NodeView nv = ( NodeView )i.next();
      nv.setUnselectedPaint( java.awt.Color.gray );
      nv.setWidth( 20 );
      nv.setHeight( 20 );
      
      view.showGraphObject( nv );
      nv.getLabel().setText( ( String )gidGdescMap.get( uidGidMap.get( nv.getRootGraphIndex() ) ) );
      nv.setShape( nv.TRIANGLE );
      //System.out.println( nv );
    }


    for ( Iterator i = view.getEdgeViewsIterator(); i.hasNext(); ) {
      EdgeView ev = ( EdgeView )i.next();
      ev.setSourceEdgeEnd( EdgeView.NO_END );
    }

    layout.layout( view );

  }


  public static void main ( String[] args ) {
    GoGinyView ggv = new GoGinyView( args[0] );
  }



}
