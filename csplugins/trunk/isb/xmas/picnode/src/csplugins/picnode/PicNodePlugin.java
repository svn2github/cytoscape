package csplugins.picnode;


import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;
import java.beans.*;
public class PicNodePlugin 
  extends CytoscapePlugin 
  implements
    PropertyChangeListener{

  public PicNodePlugin () {
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
  }

 

 public void propertyChange ( PropertyChangeEvent e ) {

    if ( e.getPropertyName() ==  CytoscapeDesktop.NETWORK_VIEW_CREATED ) {
      CyNetworkView view = ( CyNetworkView )e.getNewValue();

      System.out.println( "Adding context method to: "+view );

      view.addContextMethod( "class edu.umd.cs.piccolo.PNode",
                             "csplugins.picnode.PicNodeAction",
                             "setImage",
                             new Object[] { view } ,
                             JarLoader.getLoader() );

    }
  }

}
