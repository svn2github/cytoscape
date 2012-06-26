package org.cytoscape.cytobridge;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.apache.xmlrpc.XmlRpcException;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.cytobridge.rpc.CytoscapeRPCServer;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.task.create.NewEmptyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;

public class CytoBridgeAction extends AbstractCyAction {

	CytoscapeRPCServer xmlrpcServer = null;
	private CySwingApplication desktopApp;
	
	private NetworkManager myManager;
	
	public CytoBridgeAction(CySwingApplication desktopApp, NetworkManager myManager){
		// Add a menu item -- Plugins->sample03
		super("CytoBridge");
		setPreferredMenu("Apps");

		//ImageIcon icon = new ImageIcon(getClass().getResource("/images/tiger.jpg"));
		//ImageIcon smallIcon = new ImageIcon(getClass().getResource("/images/tiger_small.jpg"));

		// Add image icons on tool-bar and menu item
		//putValue(LARGE_ICON_KEY, icon);
		//putValue(SMALL_ICON, smallIcon);
		
		this.desktopApp = desktopApp;
		this.myManager = myManager;
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {

		System.out.println("Loading CytoscapeRPCPlugin");

        startXmlRpcService(9000, true);
        System.out.println("Started CytoscapeRPC "
                + "XML-RPC service on port " + 9000 + '.');

	}
	
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isInToolBar() {
		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isInMenuBar() {
		return true;
	}
	
	/**
     * Start the XML-RPC service.
     * @param port Port number to listen on.
     * @param localOnly Specifies whether the xmlrpc client should listen to
     * local calls only.
     */
    public final void startXmlRpcService(int port, boolean localOnly) {
        try {
            System.out.println("XML-RPC Port: " + port);
            xmlrpcServer = new CytoscapeRPCServer(port, localOnly);
            xmlrpcServer.startXmlServer(myManager);
        }
        catch (XmlRpcException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
