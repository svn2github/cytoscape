/*$Id$*/
package linkout;

import cytoscape.*;

import cytoscape.plugin.*;

import ding.view.*;

import java.util.*;


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
public class LinkOutPlugin extends CytoscapePlugin {
	/**
	 * Creates a new LinkOutPlugin object.
	 */
	public LinkOutPlugin() {
		try {
			//Create a Network create event listener
			LinkOutNetworkListener m_listener = new LinkOutNetworkListener();
			Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(m_listener);

			// Create a new ContextMenuListener and register with the pre-loaded networks.
			// Cases where networks are loaded before the plugins. For example, when running Cytoscape
			// from the command line.
			//Todo - To be tested
			Set networkSet = Cytoscape.getNetworkSet();

			for (Iterator it = networkSet.iterator(); it.hasNext();) {
				CyNetwork cyNetwork = (CyNetwork) it.next();

				LinkOutNodeContextMenuListener nodeMenuListener = new LinkOutNodeContextMenuListener();
				((DGraphView) Cytoscape.getNetworkView(cyNetwork.getIdentifier()))
				                                                              .addNodeContextMenuListener(nodeMenuListener);

				LinkOutEdgeContextMenuListener edgeMenuListener = new LinkOutEdgeContextMenuListener();
				((DGraphView) Cytoscape.getNetworkView(cyNetwork.getIdentifier()))
				                                                                  .addEdgeContextMenuListener(edgeMenuListener);
			}

			/*
			            DGraphView currentNetwork=((DGraphView)Cytoscape.getCurrentNetworkView());

			            if(currentNetwork!=null){

			                LinkOutNodeContextMenuListener nodeMenuListener=new LinkOutNodeContextMenuListener();
			                currentNetwork.addNodeContextMenuListener(nodeMenuListener);

			                LinkOutEdgeContextMenuListener edgeMenuListener=new LinkOutEdgeContextMenuListener();
			                currentNetwork.addEdgeContextMenuListener(edgeMenuListener);

			            }
			*/
		} catch (ClassCastException e) {
			cytoscape.logger.CyLogger.getLogger(LinkOutPlugin.class).error(e.getMessage());

			return;
		}
	}
}
/*
$Log: LinkOutPlugin.java,v $
Revision 1.1  2006/06/14 18:12:46  mes
updated project to actually compile and work with ant

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
