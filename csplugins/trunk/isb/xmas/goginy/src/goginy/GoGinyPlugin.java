package goginy;

import cytoscape.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.data.Semantics;

import giny.model.*;

import cern.colt.list.*;

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
      List mf = ( List )Cytoscape.getNodeAttributeValue( node, Semantics.MOLECULAR_FUNCTION );
      List bp = ( List )Cytoscape.getNodeAttributeValue( node, Semantics.BIOLOGICAL_PROCESS );
      List cc = ( List )Cytoscape.getNodeAttributeValue( node, Semantics.CELLULAR_COMPONENT );

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

