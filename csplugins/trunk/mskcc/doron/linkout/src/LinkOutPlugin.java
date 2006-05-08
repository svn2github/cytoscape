/*$Id*/
package csplugins.mskcc.doron;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;
import java.beans.*;

/**
 * Linkout plugin for customized url links
 **/
public class LinkOutPlugin
  extends 
    CytoscapePlugin 
  implements
    PropertyChangeListener  {

  public LinkOutPlugin () {
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
    System.out.println("Initialized LinkOutPlugin");
  }

  /*TODO - This implementation does not work with the cytoscape-2.3
  *see 'TooltipsAndContextMenusForRender' RFC for future implementation
  **/
  public void propertyChange ( PropertyChangeEvent e ) {

    if ( e.getPropertyName() ==  CytoscapeDesktop.NETWORK_VIEW_CREATED ) {
      CyNetworkView view = ( CyNetworkView )e.getNewValue();


	//Add LinkOut Menu
	//TODO- check the bool return value 
	view.addContextMethod("class phoebe.PNodeView",//phoebe class is part of the GINY graph library
							 // the package name
							"csplugins.mskcc.doron.LinkOut",
                            "AddLinks", //method name
							new Object[] {view}, // arguments
							CytoscapeInit.getClassLoader() ); // the class load

    }

  }

}

/*
$Log$
Revision 1.1  2006/05/08 17:15:22  betel
Initial deposit of linkout source code

*/