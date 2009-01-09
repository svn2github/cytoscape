/*
 * Created on Sep 13, 2005
 *
 */
package cytoscape;

import java.beans.PropertyChangeEvent;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import java.util.HashMap;
import java.beans.PropertyChangeListener;

/**
 * 
 * CytoscapeModifiedNetworkManager manages the modified state settings for the networks and listens for 
 * PropertyChangeEvents.  This enables functionality such as prompting the user to save modified
 * networks when exiting Cytoscape.
 * 
 * @author Allan Kuchinsky
 * @version 1.0
 *
 * 
 *
 */
public class CytoscapeModifiedNetworkManager  implements PropertyChangeListener {

	
	public static final String MODIFIED = "Modified";
	public static final String CLEAN = "Clean";
	private static HashMap networkStateMap = new HashMap();
	
	/**
	 * 
	 */
	public CytoscapeModifiedNetworkManager()  {
		super();

		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						Cytoscape.NETWORK_MODIFIED, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						Cytoscape.NETWORK_SAVED, this);
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
		.addPropertyChangeListener(
				Cytoscape.NETWORK_CREATED, this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
	}

    /**
     * 
     */
	public void propertyChange(PropertyChangeEvent e) {

		//		System.out.println ("Property changed: " + e.getPropertyName());
		//		System.out.println ("Old value = " + e.getOldValue());
		//		System.out.println ("New value = " + e.getNewValue());

			if (e.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)) {
				CyNetwork net = (CyNetwork) e.getNewValue();
				if (net instanceof CyNetwork) {
				setModified(net, MODIFIED);
				}
			} else if (e.getPropertyName().equals(Cytoscape.NETWORK_SAVED)) {
			    // MLC 09/19/05 BEGIN:
			    // CyNetwork net = (CyNetwork) e.getNewValue();
			    CyNetwork net = (CyNetwork)(((Object[]) e.getNewValue())[0]);
			    // MLC 09/19/05 END.
				if (net instanceof CyNetwork) {
					setModified(net, CLEAN);
				}
			}
		}
	
	/**
	 * 
	 * @param net
	 * @return
	 */
	public static boolean isModified(CyNetwork net)
	{
		Object modObj = networkStateMap.get(net);
		if (modObj == null)   // no network in table, so it can't be modified
		{
			return false;
		}
		else if (modObj.toString().equals(MODIFIED))
		{
			return true;
		}
		else
		{
			return false;
		}		
	}
	
	/**
	 * set the state of the network
	 * @param net
	 * @param state values supported in this version: CLEAN, MODIFIED
	 */
	public static void setModified (CyNetwork net, String state)
	{
		networkStateMap.put(net, state);
	}
	
	
}
