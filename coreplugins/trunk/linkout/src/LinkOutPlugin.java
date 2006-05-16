/*$Id$*/
//package csplugins.mskcc.doron;

package linkout;

import cytoscape.*;
import cytoscape.plugin.*;
import ding.view.*;

import javax.swing.*;
import java.awt.*;

/**
 * Linkout plugin for customized url links
 * this is the old implementation of the plugin using previous node context menu
 **
public class LinkOutPlugin
  extends 
    CytoscapePlugin 
  implements
    PropertyChangeListener  {

  public LinkOutPlugin () {
    Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener( CytoscapeDesktop.NETWORK_VIEW_CREATED, this );
    System.out.println("Initialized LinkOutPlugin");
  }

  //Note - This implementation does not work with the cytoscape-2.3
  //see 'TooltipsAndContextMenusForRender' on Cytoscape wiki for future implementation
  //
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

*************************/

/**
 * LinkOut plugin for customized URL links
 * **/
public class LinkOutPlugin
        extends CytoscapePlugin
        implements NodeContextMenuListener {

    public LinkOutPlugin () {

        try{
            ((DGraphView)Cytoscape.getCurrentNetworkView()).addNodeContextMenuListener(this);
        }
        catch (ClassCastException e){
            System.out.println(e.getMessage());
            return;
        }
    }

    public void addNodeContextMenuItems (Point pt, Object nodeView, JPopupMenu menu){
        LinkOut lo= new LinkOut();
        if(menu==null){
            menu=new JPopupMenu();
        }
        menu.add(lo.AddLinks(nodeView));
    }
}

/*
$Log$
Revision 1.2  2006/05/16 22:21:40  betel
Corrections to work with new nodeContextMenuListener

Revision 1.1  2006/05/11 22:42:28  betel
Initial deposit of linkout to pre-coreplugins

Revision 1.2  2006/05/09 22:32:47  betel
New implementation of LinkOutPlugin with new context menu interface and addition of linkout.props

Revision 1.1  2006/05/08 17:15:22  betel
Initial deposit of linkout source code

*/