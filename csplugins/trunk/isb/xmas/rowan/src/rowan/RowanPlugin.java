package rowan;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;

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

    JMenuItem des = new JMenuItem( new AbstractAction( "destroy" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                
                CyNetwork net = Cytoscape.getCurrentNetwork();
                Cytoscape.destroyNetwork( net, true );

               

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add( des );
    
    CreateAddNetwork can = new CreateAddNetwork( null );
    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( can );
    FilterDataView fdv = new FilterDataView( null );
    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( fdv );


    JMenuItem credits = new JMenuItem( new AbstractAction( "Credits" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {

                StringBuffer lines = new StringBuffer();
                  lines.append("Cytoscape is a collaboration \n" );
                  lines.append("between the Institute for Systems\n" );
                  lines.append("Biology, University of California,\n" );
                  lines.append("San Diaego, Memorial Sloan Kettering\n" );
                  lines.append("Cancer Center and the\n" );
                  lines.append("Institute Pasteur\n" ); 
                  lines.append(" \n" );
                  lines.append("Cytosape 2.0 Primary Developers\n" );
                  lines.append("Iliana Avila-Campillo,  Ethan Cerami,\n" );
                  lines.append("Rowan Christmas, Ryan Kelley, Andrew\n" );
                  lines.append("Markiel, and Chris Workman\n" );
                  lines.append(" \n" );
                  lines.append(" \n" );
                  lines.append("ISB: Hamid Bolouri (PI) \n" );
                  lines.append("Paul Shannon, David Reiss, James\n" );
                  lines.append("Taylor, Larissa KamenkoVich and \n" );
                  lines.append("Paul Edlefsen ( GINY Library )\n" );
                  lines.append(" \n" );
                  lines.append("UCSD: Trey Ideker (PI) \n" );
                  lines.append("Jonathan Wang,  Nada Amin, and \n" );
                  lines.append("Owen Ozier\n" );
                  lines.append(" \n" );
                  lines.append("MSKCC: Chris Sander (PI) \n" );
                  lines.append("Gary Bader,  Robert Sheridan\n" );
                  lines.append(" \n" );
                  lines.append("IP: Benno Shwikowski (PI) \n" );
                  lines.append(" \n" );
                  lines.append("Addional Collaborators\n" );
                  
                  
                CreditScreen.showCredits( getClass().getResource("/cytoscape/images/cytoSplash.jpg"), lines.toString() );
                                          
               

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Credits" ).add( credits );


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
                File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
                JFileChooser chooser = new JFileChooser(currentDirectory);
                if ( chooser.showOpenDialog( Cytoscape.getDesktop() ) == 
                     chooser.APPROVE_OPTION) {
                  currentDirectory = chooser.getCurrentDirectory();
                  Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
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
