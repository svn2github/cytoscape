package csplugins.layout.Jung;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;
import cytoscape.layout.*;
import cytoscape.view.*;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class JungLayoutPlugin extends CytoscapePlugin {

  public JungLayoutPlugin () {
    initialize();
  }

  protected void initialize () {

    

    JMenuItem isom = new JMenuItem( new AbstractAction( "ISOM" ) {
         public void actionPerformed ( ActionEvent e ) {
           // Do this in the GUI Event Dispatch thread...
           SwingUtilities.invokeLater( new Runnable() {
               public void run() {
                 final SwingWorker worker = new SwingWorker(){
                     public Object construct(){
                       LayoutAlgorithm layout = new JungISOMLayout( Cytoscape.getCurrentNetworkView() );
                       Cytoscape.getCurrentNetworkView().applyLayout( layout );
                       return null;
                     }
                   };
                 worker.start();
               } } ); } } );



     
     Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Layout" ).add( isom );
  }


}


