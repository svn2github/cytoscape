package csplugins.layout;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;

import csplugins.layout.jgraphaddons.*;

import java.awt.event.*;
import javax.swing.*;

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
                 RadialTreeLayoutAlgorithm rad = new RadialTreeLayoutAlgorithm( Cytoscape.getCurrentNetworkView() );
                 rad.doLayout();
               } } ); } } );


    JMenuItem circle = new JMenuItem( new AbstractAction( "Circle" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 CircleGraphLayout layout = new CircleGraphLayout( Cytoscape.getCurrentNetworkView() );
                 layout.doLayout();
               } } ); } } );


     JMenuItem spring = new JMenuItem( new AbstractAction( "Spring" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 SpringEmbeddedLayoutAlgorithm layout = new SpringEmbeddedLayoutAlgorithm( Cytoscape.getCurrentNetworkView() );
                 layout.doLayout();
               } } ); } } );
    

    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout" ).add( radial );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout" ).add( circle );
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout" ).add( spring );
  }


}


