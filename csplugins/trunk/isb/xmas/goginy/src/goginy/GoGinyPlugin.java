package goginy;

import cytoscape.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.data.Semantics;

import giny.model.*;
import giny.view.*;

import cern.colt.list.*;

import cern.jet.math.Arithmetic;

import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import filter.model.*;

public class GoGinyPlugin extends CytoscapePlugin {
  static GoOntology go;
  
  static int dot = 0;

  public static final String MOLECULAR_FUNCTION = "molecular_function";
  public static final String BIOLOGICAL_PROCESS = "biological_process";
  public static final String CELLULAR_COMPONENT = "cellular_component";

  GoFilter mf_filter;
  GoFilter bp_filter;
  GoFilter cc_filter;

  public GoGinyPlugin () {
    initialize();
  }

  protected void initialize () {

    String[] args = CytoscapeInit.getArgs();
    for ( int i = 0; i < args.length; ++i ) {
      if ( args[i].startsWith( "-go" ) ) {
        i++;
        go = new GoOntology( args[i] );
        break;
      }
    }
    
    JMenuItem add = new JMenuItem( new AbstractAction( "Show Go Categories for Selected Nodes" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                go.show();
                catsFromNodes(true);

                if ( dot == 0 ) {
                  init();
                  dot++;
                }

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "GO" ).add( add );
    
     JMenuItem add2 = new JMenuItem( new AbstractAction( "Show Go Categories for All Nodes" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                go.show();
                catsFromNodes(false);

                if ( dot == 0 ) {
                  init();
                  dot++;
                }

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "GO" ).add( add2 );


    JMenuItem mfb = new JMenuItem( new AbstractAction( "Show" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                go.show();
                
                if ( dot == 0 ) {
                  init();
                  dot++;
                }
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "GO" ).add( mfb );
    
    JMenuItem prob = new JMenuItem( new AbstractAction( "Prob" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                printTermProb();
                  
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "GO" ).add(prob );
    
    JMenuItem test = new JMenuItem( new AbstractAction( "test" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {

                int[] subset = Cytoscape.getCurrentNetwork().getFlaggedNodeIndicesArray();
                int node = subset[0];

                 List term_list = Cytoscape.getNodeNetworkData().getAttributeValueList( Cytoscape.getRootGraph().getNode( node ).getIdentifier(),
                                                                                         CELLULAR_COMPONENT);

                 System.out.println( "node: "+Cytoscape.getRootGraph().getNode( node ).getIdentifier()+" list size: "+term_list.size()+" LIST: "+term_list );
                 for ( Iterator t = term_list.iterator(); t.hasNext(); ) {
                   String term = ( String )t.next();
                   System.out.println( " term: "+term );
                 }

                  
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "GO" ).add(test );



  }


  public static void printTermProb () {

    go.getMFView().printTermProb( MOLECULAR_FUNCTION );
    go.getBPView().printTermProb( BIOLOGICAL_PROCESS );
    go.getCCView().printTermProb( CELLULAR_COMPONENT );
  }

  public void init () {

    mf_filter = new GoFilter( go.getMFView(),
                              go.getMF(),
                              MOLECULAR_FUNCTION );
    FilterManager.defaultManager().addFilter( mf_filter );
    
    bp_filter = new GoFilter( go.getBPView(),
                              go.getBP(),
                              BIOLOGICAL_PROCESS );
    FilterManager.defaultManager().addFilter( bp_filter );

    cc_filter = new GoFilter( go.getCCView(),
                              go.getCC(),
                              CELLULAR_COMPONENT );
    FilterManager.defaultManager().addFilter( cc_filter );

  }
  
 
 

  public static void catsFromNodes ( boolean show_selected ) {

    IntArrayList mf_list = new IntArrayList();
    IntArrayList bp_list = new IntArrayList();
    IntArrayList cc_list = new IntArrayList();

    Iterator i;
    if ( show_selected ) 
      i = Cytoscape.getCurrentNetwork().getFlaggedNodes().iterator();
    else 
      i = Cytoscape.getCurrentNetwork().nodesIterator();

    while ( i.hasNext() ) {

      Node node = ( Node )i.next();
      List mf = Cytoscape.getNodeNetworkData().getAttributeValueList( node.getIdentifier(), MOLECULAR_FUNCTION );
      List bp = Cytoscape.getNodeNetworkData().getAttributeValueList( node.getIdentifier(), BIOLOGICAL_PROCESS );
      List cc = Cytoscape.getNodeNetworkData().getAttributeValueList( node.getIdentifier(), CELLULAR_COMPONENT );

      for ( Iterator j = mf.iterator(); j.hasNext(); ) {
        String go = ( String )j.next();
        try { 
          int term = Integer.parseInt( go.substring( 4 ) );
          mf_list.add( term );
        } catch ( Exception e ) {
          System.out.println( "Node MF: "+node.getIdentifier()+": "+go );
        }
       
      }
      
      for ( Iterator j = cc.iterator(); j.hasNext(); ) {
        String go = ( String )j.next();
        try {
          int term = Integer.parseInt( go.substring( 4 ) );
          cc_list.add( term );
        } catch ( Exception e ) {
          System.out.println( "Node CC: "+node.getIdentifier()+": "+go );
        }
      }
      
      for ( Iterator j = bp.iterator(); j.hasNext(); ) {
        String go = ( String )j.next();
        try { 
          int term = Integer.parseInt( go.substring( 4 ) );
          bp_list.add( term );
        } catch ( Exception e ) {
          System.out.println( "Node BP: "+node.getIdentifier()+": "+go );
        }
       }
    }
    mf_list.trimToSize();
    bp_list.trimToSize();
    cc_list.trimToSize();
    
    Ontology mfo = go.getMF();
    mfo.showTerms( mf_list.elements() );
    
    Ontology cco = go.getCC();
    cco.showTerms( cc_list.elements() );
    
    Ontology bpo = go.getBP();
    bpo.showTerms( bp_list.elements() );
    
  }


}

