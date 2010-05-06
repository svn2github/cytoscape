package org.idekerlab.ModFindPlugin;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

import cytoscape.Cytoscape;

/**
 * Listening to the network creation event and override the visual style.
 * 
 * @author kono
 *
 */
public class VisualStyleObserver implements PropertyChangeListener {

	private enum NetworkType {
		OVERVIEW, MODULE;
	}
	
	private final Map<String, NetworkType> managedNetworkMap;
	
	VisualStyleObserver() {
		managedNetworkMap = new HashMap<String, NetworkType>();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("#### Observer got EVENT: " + evt.getPropertyName());
	}

}
