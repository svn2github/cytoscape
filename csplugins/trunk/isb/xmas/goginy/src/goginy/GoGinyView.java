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
import cern.jet.math.Arithmetic;
import cytoscape.Cytoscape;

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

  public GraphView getGraphView() {
    return view;
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
      nv.setBorderPaint( Color.black );
      nv.setToolTip( null );
      //insert extra stuff here

    }

   
    
    layout.layout( view );

    if ( selected_nodes.length == 0 ) {return;}
    double bigX;
    double bigY;
    double smallX;
    double smallY;
    double W = 2000;
    double H = 300;
    NodeView first = ( NodeView )view.getNodeView( selected_nodes[0] );
    bigX = first.getXPosition();
    smallX = bigX;
    bigY = first.getYPosition();
    smallY = bigY;
    W = first.getWidth();
    H = first.getHeight();

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

        if ( nv.getHeight() > H ) 
          H = nv.getHeight();
        
        if ( nv.getWidth() > W ) 
          W = nv.getWidth();


      }
    }

    PBounds zoomToBounds;
    if (selected_nodes.length == 1) {
      zoomToBounds = new PBounds( smallX - W , smallY - H , ( bigX - smallX + W ), ( bigY - smallY + H ) );
    } else {
      zoomToBounds = new PBounds( smallX - W , smallY - H , ( bigX - smallX + W ), ( bigY - smallY + H ) );
    }
    PTransformActivity activity =  ( ( PGraphView )view).getCanvas().getCamera().animateViewToCenterBounds( zoomToBounds, true, 500 );


  }


  public static int[] nodesWithTerm ( int[] subset,
                                      String category,
                                      int go_term ) {

    OpenIntIntHashMap match = new OpenIntIntHashMap();
    for ( int i = 0; i < subset.length; ++i ) {
      List term_list = Cytoscape.getNodeAttributes().getAttributeList( Cytoscape.getRootGraph().getNode( subset[i] ).getIdentifier(),
                                                                       category );
      for ( Iterator t = term_list.iterator(); t.hasNext(); ) {
        String term = ( String )t.next();
        try {
          int term_id = Integer.parseInt( term.substring( 4 ) );
          
          // TODO: walk up or down the tree
          if ( go_term == term_id ) 
            match.put( subset[i], 1 );
        } catch ( Exception e ) {
          //System.out.println( "Term: "+term+ " is bad" );
        }
      }
    }

    IntArrayList keys = new IntArrayList();
    match.keys( keys );
    keys.trimToSize();
    return keys.elements();

  }

  // TODO: use a property
  public String locusFromNodes ( int[] nodes ) {
    String tip = "";
    
    for ( int i = 0; i < nodes.length; ++i ) {
      String locus = ( String )Cytoscape.getNodeAttributes().getStringAttribute( Cytoscape.getRootGraph().getNode( nodes[i] ).getIdentifier(), "Locus" );
      if ( i % 5 == 0 && i != 0) {
        tip = tip+" "+locus+"\n";
      } else {
        tip = tip+" "+locus;
      }
    }
    return tip;
  }


  public void printTermProb ( String category ) {

    int[] fullset = Cytoscape.getRootGraph().getNodeIndicesArray();
    int[] subset = Cytoscape.getCurrentNetwork().getNodeIndicesArray();
    
    int[] go_terms = ontology.getTerms( ontology.getGraphPerspective().getNodeIndicesArray() );
    //int[] go_terms = ontology.getTerms( view.getSelectedNodeIndices() );
    for ( int i = 0; i < go_terms.length; ++i ) {
      double prob =   genesAnnotatedToTerm( go_terms[i], 
                                            category, 
                                            fullset, 
                                            subset );

      System.out.println( go_terms[i]+": "+ontology.descriptionForID( go_terms[i] )+" pvalue: "+prob );

      int go_term_node_id = ontology.uidForGID( go_terms[i] );
      
      int[] nodes_wit_term = nodesWithTerm( subset,
                                            category,
                                            go_terms[i] );
      String tip = locusFromNodes( nodes_wit_term );


      NodeView nv = getGraphView().getNodeView( go_term_node_id );
     
      

      if ( nodes_wit_term.length == 0 ) {
        nv.setBorderPaint( Color.gray );
        nv.setToolTip( null );
      } else {
        nv.setBorderPaint( Color.black );
        nv.setToolTip(  go_terms[i]+": "+ontology.descriptionForID( go_terms[i] )+"\npvalue: "+prob+"\n"+tip );
      }
      if ( prob < .00000000001 )
        nv.setUnselectedPaint( Color.orange );
      else if ( prob < .000000001 ) 
        nv.setUnselectedPaint( Color.yellow );
      else if( prob < .0000001 )
        nv.setUnselectedPaint( Color.green );
      else if( prob < .00001 )
        nv.setUnselectedPaint( Color.cyan );
      else if( prob < .001 )
        nv.setUnselectedPaint( Color.blue );
      else 
        nv.setUnselectedPaint( Color.white );

    }

  }


   public static double genesAnnotatedToTerm( int go_term, 
                                              String category, 
                                              int[] fullset, 
                                              int[] subset ) {

    // total number of genes with GO terms
    int N = fullset.length;

    // number of genes in the fullset annoated to a term
    int G = nodesWithTerm( fullset, category, go_term ).length;

    // number of genes in the subset
    int n = subset.length;

    // number of genes in the subset annoated to the term
    int x = nodesWithTerm( subset, category, go_term).length;
    
    // first p
    double p = (double)G/(double)N;

    // total prob
    double prob = 0;

    System.out.println( "G: "+G+" N: "+N+" P: "+p +" x:"+x);

    for ( int j = x; j < n; ++j ) {

      double n_fact = Arithmetic.factorial( n );
      double j_fact = Arithmetic.factorial( j );
      double n_minus_j_fact = Arithmetic.factorial( (n-j) );
      
      double t1 = ( n_fact / ( j_fact * n_minus_j_fact ) );


      double t2 = Math.pow( p, (double)j );

      double base = ( 1 - p );
      double exp = ( n -j );

      double t3 = Math.pow( base, exp );

      double fin = ( t1 * t2 * t3 );

      prob = (fin + prob);


      //System.out.println( "t1: "+t1+ " t2: "+t2+" t3: "+t3 +" fin:"+fin);

    }
    
    return prob;

  }






}
