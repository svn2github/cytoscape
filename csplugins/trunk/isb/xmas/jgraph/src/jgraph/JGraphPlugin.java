package jgraph;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;

import javax.swing.*;

import java.util.*;
import java.awt.event.*;

import java.beans.*;
import java.io.*;


public class JGraphPlugin extends CytoscapePlugin {


  public JGraphPlugin () {
    initialize();
  }

  protected void initialize () {

    JMenuItem dot = new JMenuItem( new AbstractAction( "Dot" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphExport export = new JGraphExport( Cytoscape.getCurrentNetworkView(), 0 );
                export.doExport();
                
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout.JGraph.Export" ).add( dot );
    
    JMenuItem anneal = new JMenuItem( new AbstractAction( "Annealing" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 0, 0 );
                layout.doLayout();
                
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout.JGraph" ).add( anneal );
    
    JMenuItem moen = new JMenuItem( new AbstractAction( "Moen" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 1, 0 );
                layout.doLayout();
                
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout.JGraph" ).add( moen );

 JMenuItem circle = new JMenuItem( new AbstractAction( "Circle" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 2, 0 );
                layout.doLayout();
                
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout.JGraph" ).add( circle );

 JMenuItem radial = new JMenuItem( new AbstractAction( "Radial" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 3, 0 );
                layout.doLayout();
                
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout.JGraph" ).add( radial );

  JMenuItem gem = new JMenuItem( new AbstractAction( "GEM" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 4, 0 );
                 layout.doLayout();
             
               } } ); } } );
     Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout.JGraph" ).add( gem );

 JMenuItem spring = new JMenuItem( new AbstractAction( "Spring" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 5, 0 );
                layout.doLayout();
                
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout.JGraph" ).add( spring );

    JMenuItem sug = new JMenuItem( new AbstractAction( "Sugiyama" ) {
        public void actionPerformed ( ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {
                JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView(), 6, 0 );
                layout.doLayout();
                
              } } ); } } );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout.JGraph" ).add( sug );

 // JMenuItem tree = new JMenuItem( new AbstractAction( "Tree" ) {
//         public void actionPerformed ( ActionEvent e ) {
//           // Do this in the GUI Event Dispatch thread...
//           SwingUtilities.invokeLater( new Runnable() {
//               public void run() {
//                 JGraphLayout layout = new JGraphLayout( Cytoscape.getCurrentNetworkView() );
//                 layout.doLayout(7, 0);
                
//               } } ); } } );
//     Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout.JGraph" ).add( tree );

  }

}
