package csplugins.metabolic;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;
import java.beans.*;

import javax.swing.*;
import java.util.*;

import giny.model.*;

public class MetabolicPlugin 
  extends 
    CytoscapePlugin
  implements
    PropertyChangeListener {

  public MetabolicPlugin() {
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
  
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins").add( new JMenuItem ( new AbstractAction( "View Reactions" ) {
        public void actionPerformed ( java.awt.event.ActionEvent e ) {
          // Do this in the GUI Event Dispatch thread...
          SwingUtilities.invokeLater( new Runnable() {
              public void run() {

                CyNetwork network = Cytoscape.getCurrentNetwork();
                for ( Iterator i = network.nodesIterator(); i.hasNext(); ) {
                  Node node = ( Node )i.next();
                  if ( node instanceof Reaction ) {
                    Reaction reaction = ( Reaction )node;
                    System.out.println( "Reaction: "+reaction );
                    System.out.println( "  Modifiers: " );
                    for ( Iterator m = reaction.getModifierList().iterator(); m.hasNext(); ) {
                      System.out.println( "    M: "+m.next() );
                    }
                    System.out.println( "  Reactants: " );
                    for ( Iterator m = reaction.getReactantList().iterator(); m.hasNext(); ) {
                      System.out.println( "    R: "+m.next() );
                    }
                    System.out.println( "  Products: " );
                    for ( Iterator m = reaction.getProductList().iterator(); m.hasNext(); ) {
                      System.out.println( "    P: "+m.next() );
                    }


                  }
                }
              }
                
            } ); } } ) );




  }

  public void propertyChange ( PropertyChangeEvent e ) {

    if ( e.getPropertyName() ==  CytoscapeDesktop.NETWORK_VIEW_CREATED ) {
      CyNetworkView view = ( CyNetworkView )e.getNewValue();

      System.out.println( "Adding context method to: "+view );

      view.addContextMethod( "class phoebe.PNodeView",
                             "csplugins.metabolic.MetabolicAction",
                             "shadows",
                             new Object[] { view } ,
                             JarLoader.getLoader() );

    }
  }

}
