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
  

  public BirdsEyeViewAction () {
    super("Overview");
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


    bev = new BirdsEyeView();
    bev.connect(  ( ( PhoebeNetworkView )Cytoscape.getCurrentNetworkView() ).getCanvas(), new PLayer[] { ( ( PhoebeNetworkView )Cytoscape.getCurrentNetworkView() ).getCanvas().getLayer() } );
      
    //bev.setMinimumSize( new Dimension( 180, 180 ) );
    //bev.setMaximumSize( new Dimension( 180, 180 ) );
    //bev.setPreferredSize( new Dimension( 180, 180 ) );
    bev.setSize( Cytoscape.getDesktop().getNetworkPanel().getNavigatorPanel().getSize( null ) );
    Cytoscape.getDesktop().getNetworkPanel().getNavigatorPanel().add( bev );
    //Cytoscape.getDesktop().getNetworkPanel().getNavigatorPanel().add( new JLabel( "Birds Eye View " ) ); 
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( this );
    bev.updateFromViewed();
	
  //   JFrame dialog = new JFrame("Navigator");
//     final PGraphView pview = (PGraphView)Cytoscape.getCurrentNetworkView();
    
//      dialog.getContentPane().add( pview.getBirdsEyeView() );
    
//      dialog.addWindowListener(new WindowAdapter() {
//          public void windowClosing(WindowEvent we) {
           
//           ( (phoebe.event.BirdsEyeView)pview.getBirdsEyeView() ).disconnect();
//          }
//        });
     
//      dialog.pack();
//      dialog.setSize(new Dimension ( 200, 200));
//      dialog.setVisible( true );
     
  }

}
