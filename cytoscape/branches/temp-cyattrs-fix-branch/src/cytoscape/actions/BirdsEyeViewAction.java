package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.event.*;
import javax.swing.event.*;

import java.awt.Dimension;
import cytoscape.view.CyNetworkView;
import phoebe.event.BirdsEyeView;
import cytoscape.dialogs.GraphObjectSelection;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.giny.PhoebeNetworkView;
import java.beans.*;

import edu.umd.cs.piccolo.PLayer;

public class BirdsEyeViewAction 
  extends 
    CytoscapeAction 
  implements 
    PropertyChangeListener {

  BirdsEyeView bev;
  boolean on = false;

  public BirdsEyeViewAction () {
    super("Toggle Overview");
    setPreferredMenu( "Visualization" );
  }

  public void propertyChange ( PropertyChangeEvent e ) {
    if ( e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED || e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUS ) {
      bev.disconnect();
      try {
        bev.connect(  ( ( PhoebeNetworkView )Cytoscape.getCurrentNetworkView() ).getCanvas(), new PLayer[] { ( ( PhoebeNetworkView )Cytoscape.getCurrentNetworkView() ).getCanvas().getLayer() } );
        bev.updateFromViewed();
      } catch ( Exception ex ) {
         // no newly focused network
      }
    }

    if ( e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_DESTROYED ) {
       bev.disconnect();
       try {
         bev.connect(  ( ( PhoebeNetworkView )Cytoscape.getCurrentNetworkView() ).getCanvas(), new PLayer[] { ( ( PhoebeNetworkView )Cytoscape.getCurrentNetworkView() ).getCanvas().getLayer() } );
         bev.updateFromViewed();
       } catch ( Exception ex ) {
         // no newly focused network
       }
    }


  }

  public void actionPerformed (ActionEvent e) {

    if ( !on ) {
      bev = new BirdsEyeView();
      bev.connect(  ( ( PhoebeNetworkView )Cytoscape.getCurrentNetworkView() ).getCanvas(), new PLayer[] { ( ( PhoebeNetworkView )Cytoscape.getCurrentNetworkView() ).getCanvas().getLayer() } );
      
      bev.setMinimumSize( new Dimension( 180, 180 ) );
      bev.setSize( new Dimension( 180, 180 ) );
      Cytoscape.getDesktop().getNetworkPanel().setNavigator( bev );
      Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( this );
      bev.updateFromViewed();
      on = true;
    } else {
      if ( bev != null ) {
        bev.disconnect();
        bev = null;
      }
      Cytoscape.getDesktop().getNetworkPanel().setNavigator(  Cytoscape.getDesktop().getNetworkPanel().getNavigatorPanel() );
      Cytoscape.getDesktop().getSwingPropertyChangeSupport().removePropertyChangeListener( this );
      on = false;
    }
  }

}
