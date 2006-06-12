/*$Id$*/


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
        extends CytoscapePlugin  {

    public LinkOutPlugin () {

        try{

            //Create a Network create event listener
            LinkOutNetworkListener m_listener=new LinkOutNetworkListener();
            Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(m_listener);

            // Create a new ContextMenuListener and register with the CURRENT network.
            // This deals with cases where networks are loaded before the plugins. For example, when running Cytoscape
            // from the command line.
            // Note -  plugins should be loaded before any network - See CytoscapeInit.
            DGraphView currentNetwork=((DGraphView)Cytoscape.getCurrentNetworkView());
            if(currentNetwork!=null){
                LinkOutContextMenuListener menu_listener=new LinkOutContextMenuListener();

                currentNetwork.addNodeContextMenuListener(menu_listener);
            }

        }
        catch (ClassCastException e){
            System.out.println(e.getMessage());
            return;
        }
    }

}

/*
$Log$
Revision 1.4  2006/06/12 19:27:44  betel
Fixes to bug reports 346-links to missing labels, 637-linkout fix for command line mode

Revision 1.3  2006/05/19 21:51:29  betel
New implementation of LinkOut with network-view listener

Revision 1.1  2006/05/11 22:42:28  betel
Initial deposit of linkout to pre-coreplugins

Revision 1.2  2006/05/09 22:32:47  betel
New implementation of LinkOutPlugin with new context menu interface and addition of linkout.props

Revision 1.1  2006/05/08 17:15:22  betel
Initial deposit of linkout source code

*/