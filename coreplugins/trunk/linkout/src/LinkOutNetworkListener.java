/*$Id$*/
package linkout;

import ding.view.*;
import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import java.beans.*;


/**
 * LinkOutNetworkListener implements PropertyChangeListener for new Network instances
 * When a new cytoscape network view is created it registers the LinkOutContextMenuListener
 * with the new DGraphView.
**/
public class LinkOutNetworkListener implements PropertyChangeListener{

    public void LinkOutNetworkListener(){
        //System.out.println("[LinkOutNetworkListener]: constructor called");
    }

    /**
     * Register a LinkOutContextMenuListener with DGraphView
     * @param evnt PropertyChangeEvent
     */
    public void propertyChange (PropertyChangeEvent evnt){
        if (evnt.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_CREATED){
            //System.out.println("[LinkOutNetworkListener]: propertyChange called");

            LinkOutContextMenuListener menu_listener=new LinkOutContextMenuListener();

            ((DGraphView)Cytoscape.getCurrentNetworkView()).addNodeContextMenuListener(menu_listener);

        }
    }
}

/*$Log$
 *Revision 1.1  2006/05/19 21:51:29  betel
 *New implementation of LinkOut with network-view listener
 **/