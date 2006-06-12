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
     * Register a LinkOutContextMenuListener for all new DGraphView objects (i.e. new network instances)
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
 *Revision 1.2  2006/06/12 19:27:44  betel
 *Fixes to bug reports 346-links to missing labels, 637-linkout fix for command line mode
 *
/*Revision 1.1  2006/05/19 21:51:29  betel
/*New implementation of LinkOut with network-view listener
/**/