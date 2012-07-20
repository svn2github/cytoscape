package org.cytoscape.cytobridge;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.apache.xmlrpc.XmlRpcException;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.cytobridge.json.MyJSON;
import org.cytoscape.cytobridge.rpc.CytoscapeRPCServer;

public class CytoBridgeAction extends AbstractCyAction {

	CytoscapeRPCServer xmlrpcServer = null;
	private CySwingApplication desktopApp;
	
	private boolean started = false;
	
	private NetworkManager myManager;
	
	private Thread serverThread;
	
	public CytoBridgeAction(CySwingApplication desktopApp, NetworkManager myManager){
		// Add a menu item -- Plugins->sample03
		super("CytoBridge");
		setPreferredMenu("Apps");

		ImageIcon icon = new ImageIcon(getClass().getResource("/images/bridge.png"));
		ImageIcon smallIcon = new ImageIcon(getClass().getResource("/images/bridge_small.png"));
		
		// Add image icons on tool-bar and menu item
		putValue(LARGE_ICON_KEY, icon);
		putValue(SMALL_ICON, smallIcon);
		
		this.desktopApp = desktopApp;
		this.myManager = myManager;
	}
	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {

		if (!started) {
			ImageIcon icon = new ImageIcon(getClass().getResource("/images/bridge2.png"));
	
			// Add image icons on tool-bar and menu item
			putValue(LARGE_ICON_KEY, icon);
			
			/*System.out.println("Loading CytoscapeRPCPlugin");
	
	        startXmlRpcService(9000, true);
	        System.out.println("Started CytoscapeRPC "
	                + "XML-RPC service on port " + 9000 + '.');
	                */
			if (serverThread == null) {
				startJSONService();
			}
			started = true;
		} else {
			ImageIcon icon = new ImageIcon(getClass().getResource("/images/bridge.png"));
			
			// Add image icons on tool-bar and menu item
			putValue(LARGE_ICON_KEY, icon);
			started = false;
		}

	}
	
	
	/**
	 * Make sure the plugin shows up in the Cytoscape toolbar.
	 * @return  True, since it should show up.
	 */
	public boolean isInToolBar() {
		return true;
	}

	/**
	 * Make sure the plugin shows up in the Cytoscape menubar.
	 * @return  True, since it should show up.
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
    
    public final void startJSONService() {
        MyJSON jsonServer = new MyJSON(myManager);
        serverThread = new Thread(jsonServer);
        serverThread.start();
    }
    
    public final void stopJSONService() {
    	serverThread.stop();
    }
}
