package cytoscape.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import cytoscape.Cytoscape;


/** This class manages images that represent nested networks.  This "management" includes creation, updating and destruction of such images as well
 *  as updating network views when any of their nodes nested networks have changed.
 */
public class NestedNetworkImageManager implements PropertyChangeListener {

	private NestedNetworkImageManager() throws IOException {
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	public void propertyChange(final PropertyChangeEvent evt) {		
		if (evt.getPropertyName().equals(Cytoscape.NESTED_NETWORK_CREATED)) {
			
		} else if (evt.getPropertyName().equals(Cytoscape.NESTED_NETWORK_DESTROYED)) {
			
		} else if (Cytoscape.NETWORK_MODIFIED.equals(evt.getPropertyName())) {

		}
	}
}
