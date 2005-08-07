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

import rowan.browser.*;

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

    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout" ).add( new LayoutManager() );
 


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
    
  
    JMenuItem first = new JMenuItem( new AbstractAction( "First Neighbors" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                NeighborView nv = new NeighborView();
               

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add( first );


    JMenuItem nb = new JMenuItem( new AbstractAction( "Node Browser" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                
                DataTable table = new DataTable(Cytoscape.getNodeNetworkData(), DataTable.NODES );

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add( nb );

    JMenuItem eb = new JMenuItem( new AbstractAction( "Edge Browser" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                
                DataTable table = new DataTable(Cytoscape.getEdgeNetworkData(), DataTable.EDGES );

              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add( eb );


  }



}
