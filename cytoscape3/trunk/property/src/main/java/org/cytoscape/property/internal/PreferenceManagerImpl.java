package org.cytoscape.property.internal;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.property.PropertyManager;

public class PreferenceManagerImpl implements PropertyManager {
	
	private Map<String, Object> preferences;
	

	public PreferenceManagerImpl() {
		preferences = new HashMap<String, Object>();
	}
	
	public void setPreferences(Map<String, Object> preferences) {
		this.preferences = preferences;
	}
	
	public void addProperties(Object preference) {
		// TODO Auto-generated method stub

	}

	public Object getProperties(String preferenceName) {
		// TODO Auto-generated method stub
		return null;
	}

	public void store() {
		// TODO Auto-generated method stub

	}

	public void store(OutputStream os) {
		// TODO Auto-generated method stub

	}

}
