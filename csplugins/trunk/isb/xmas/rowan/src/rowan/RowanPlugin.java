package rowan;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;


import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.io.*;
import java.util.List;
import giny.view.*;
import edu.umd.cs.piccolo.PNode;
import javax.swing.*;
import phoebe.*;

public class RowanPlugin extends CytoscapePlugin {

  public RowanPlugin () {

    initialize();
  }

  protected void initialize () {

    System.out.println( "initialize rowan plugin" );


    JMenuItem add = new JMenuItem( new AbstractAction( "Add Atts" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                
                AddAttributes adda = new AddAttributes();

               

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add( add );

    JMenuItem des = new JMenuItem( new AbstractAction( "destroy" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                
                CyNetwork net = Cytoscape.getCurrentNetwork();
                Cytoscape.destroyNetwork( net, true );

               

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add( des );
    
       JMenuItem isom = new JMenuItem( new AbstractAction( "ISOM new" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 final SwingWorker worker = new SwingWorker(){
                     public Object construct(){
                       ISOMLayout layout = new ISOMLayout( Cytoscape.getCurrentNetworkView() );
                       Cytoscape.getCurrentNetworkView().applyLayout( layout );
                       return null;
                     }
                   };
                 worker.start();
               } } ); } } );


    CreateAddNetwork can = new CreateAddNetwork( null );
    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( can );
    FilterDataView fdv = new FilterDataView( null );
    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( fdv );


  


    JMenuItem group = new JMenuItem( new AbstractAction( "Group" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                
                PGraphView view = ( PGraphView )Cytoscape.getCurrentNetworkView();
                List nodes = view.getSelectedNodes();
                NodeGroup ng = new NodeGroup();
                view.addToEdgeLayer( ng );
                float r = ( float )Math.random();
                float g = ( float )Math.random();
                float b = ( float )Math.random();
                ng.setPaint( new java.awt.Color( r, g, b, .2f ) );
                ng.setStrokePaint( new java.awt.Color( r, g, b ) );
                ng.moveToBack();
                for ( Iterator i = nodes.iterator(); i.hasNext(); ) {
                  //ng.addChild( (PNode)i.next() );
                  //((PNode)i.next()).reparent( ng );
                  ng.addGroupie( (PNode)i.next() );
                }
                ng.computeGroupBounds();

               

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add( group );
    
    JMenuItem shadow = new JMenuItem( new AbstractAction( "Drop Shadow" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                PGraphView view = ( PGraphView )Cytoscape.getCurrentNetworkView();
                DropShadowTest.createDropShadowNode( view.getNodeLayer() );
               

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add( shadow );


    JMenuItem first = new JMenuItem( new AbstractAction( "First Neighbors" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                NeighborView nv = new NeighborView();
               

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add( first );


    JMenuItem complex = new JMenuItem( new AbstractAction( "Complex" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
               
                File file = null;
                File currentDirectory = CytoscapeInit.getMRUD();
                JFileChooser chooser = new JFileChooser(currentDirectory);
                if ( chooser.showOpenDialog( Cytoscape.getDesktop() ) == 
                     chooser.APPROVE_OPTION) {
                  currentDirectory = chooser.getCurrentDirectory();
                  CytoscapeInit.setMRUD(currentDirectory);
                  file = chooser.getSelectedFile();
                }

                CyNetworkView view = Cytoscape.getCurrentNetworkView();
                PGraphView pview = ( PGraphView )view;

                try {
                  BufferedReader in
                    = new BufferedReader(new FileReader( file ) );
                  String oneLine = in.readLine();
                  int count = 0;
                  while (oneLine != null ) {//&& count++ < 20 ) {
         
                    if (oneLine.startsWith("#")) {
                      // comment
                    } else {
                      // read nodes in
                      ArrayList nodes = new ArrayList();
                      String[] line = oneLine.split( " " );
                      for ( int i = 0; i < line.length; ++i ) {
                        CyNode node = Cytoscape.getCyNode( line[i] );
                        if ( node != null ) {
                          NodeView nv = view.getNodeView( node );
                          if ( nv != null ) {
                            nodes.add( nv );
                            System.out.println( "Found complex node: "+line[i] );
                          }
                        }
                      }
                     
                      // make group
                      if ( nodes.size() != 0 ) {
                        
                        NodeGroup ng = new NodeGroup();
                        pview.addToEdgeLayer( ng );
                        float r = ( float )Math.random();
                        float g = ( float )Math.random();
                        float b = ( float )Math.random();
                        ng.setPaint( new java.awt.Color( r, g, b, .2f ) );
                        ng.setStrokePaint( new java.awt.Color( r, g, b ) );
                        ng.moveToBack();
                        for ( Iterator i = nodes.iterator(); i.hasNext(); ) {
                          ng.addGroupie( (PNode)i.next() );
                        }
                        ng.computeGroupBounds();
                      }

  
                    }        
                    oneLine = in.readLine();
                  }
      
                  in.close();
                } catch ( Exception ex ) {
                  System.out.println( "File Read error" );
                  ex.printStackTrace();
                }



               

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add( complex );



  }



}
