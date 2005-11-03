package csplugins.contextmenu.yeast;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;
import java.beans.*;

public class YeastPlugin 
  extends 
    CytoscapePlugin 
  implements
    PropertyChangeListener  {

  public YeastPlugin () {
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
  }

  public void propertyChange ( PropertyChangeEvent e ) {

    if ( e.getPropertyName() ==  CytoscapeDesktop.NETWORK_VIEW_CREATED ) {
      CyNetworkView view = ( CyNetworkView )e.getNewValue();
    
      view.addContextMethod( "class phoebe.PNodeView",
                             "csplugins.contextmenu.yeast.NodeAction",
                             "getTitle",
                             new Object[] { view } ,
                             CytoscapeInit.getClassLoader() );
    
    view.addContextMethod( "class phoebe.PNodeView",
                           "csplugins.contextmenu.yeast.NodeAction",
                           "openWebInfo",
                           new Object[] { view } ,
                             CytoscapeInit.getClassLoader() );


    // Add some Edge Context Menus
    view.addContextMethod( "class phoebe.PEdgeView",
                           "csplugins.contextmenu.yeast.EdgeAction",
                           "getTitle",
                           new Object[] { view } ,
                             CytoscapeInit.getClassLoader() );
    }

  }


}
  
