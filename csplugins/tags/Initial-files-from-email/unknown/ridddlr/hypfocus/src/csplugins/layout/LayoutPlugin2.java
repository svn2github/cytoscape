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
import phoebe.util.GraphPartition;

public class LayoutPlugin2 extends CytoscapePlugin {

  public LayoutPlugin2 () {
    initialize();
  }

  protected void initialize () {

    


    JMenuItem hypfocus = new JMenuItem( new AbstractAction( "HypFocus" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 final SwingWorker worker = new SwingWorker(){
                     public Object construct(){
                       LayoutAlgorithm layout = new HypFocusLayout( Cytoscape.getCurrentNetworkView() );
                       Cytoscape.getCurrentNetworkView().applyLayout( layout );
                       return null;
                     }
                   };
                 worker.start();
               } } ); } } );

     Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout" ).add( hypfocus );
  }


}


