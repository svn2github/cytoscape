package csplugins.layout;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;
import cytoscape.layout.*;
import csplugins.layout.jgraphaddons.*;
import cytoscape.view.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import giny.util.GraphPartition;

public class LayoutPlugin extends CytoscapePlugin {

  public LayoutPlugin () {
    initialize();
  }

  protected void initialize () {

    

    JMenuItem radial = new JMenuItem( new AbstractAction( "Radial" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 final SwingWorker worker = new SwingWorker(){
                     public Object construct(){


                       CyNetwork network = Cytoscape.getCurrentNetwork();
                       CyNetworkView network_view = Cytoscape.getCurrentNetworkView();
                       
                       List partions = GraphPartition.partition( network );
                       Iterator i = partions.iterator();
                       float r, g, b;
                       while ( i.hasNext() ) {
                         int[] array = ( int[] )i.next();
                         r = ( float )Math.random();
                         g = ( float )Math.random();
                         b = ( float )Math.random();
                         for ( int j = 0; j < array.length; ++j ) {
                           //network_view.getNodeView( network.getEdgeTargetIndex( array[j] ) ).select();
                           //network_view.getNodeView( network.getEdgeSourceIndex( array[j] ) ).select();
                           
                           // network_view.getNodeView( array[j] ).select();
                           
                           network_view.getNodeView( array[j] ).setUnselectedPaint( new java.awt.Color( r, g, b ) );
                           network_view.getNodeView( array[j] ).setUnselectedPaint( new java.awt.Color( r, g, b ) );
                         }
                       }
        

                       LayoutAlgorithm layout = new RadialTreeLayoutAlgorithm( Cytoscape.getCurrentNetworkView() );
                       Cytoscape.getCurrentNetworkView().applyLayout( layout );
                       return null;
                     }
                   };
                 worker.start();
               } } ); } } );


    JMenuItem circle = new JMenuItem( new AbstractAction( "Circle" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 final SwingWorker worker = new SwingWorker(){
                     public Object construct(){
                       LayoutAlgorithm layout = new CircleGraphLayout( Cytoscape.getCurrentNetworkView() );
                       Cytoscape.getCurrentNetworkView().applyLayout( layout );
                       return null;
                     }
                   };
                 worker.start();
               } } ); } } );


     JMenuItem spring = new JMenuItem( new AbstractAction( "Spring" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 final SwingWorker worker = new SwingWorker(){
                     public Object construct(){
                       LayoutAlgorithm layout = new SpringEmbeddedLayoutAlgorithm( Cytoscape.getCurrentNetworkView() );
                       Cytoscape.getCurrentNetworkView().applyLayout( layout );
                       return null;
                     }
                   };
                 worker.start();
               } } ); } } );
    

    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout" ).add( radial );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout" ).add( circle );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout" ).add( spring );
  }


}


