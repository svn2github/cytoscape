package org.cytoscape.sample.internal;

import java.io.Serializable;
import java.util.HashMap;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

public class TableDestroyedEvent implements NetworkAboutToBeDestroyedListener{

	private HashMap<String, Serializable> panelComponentMap;
	
	TableDestroyedEvent(){
		
		panelComponentMap = new HashMap<String, Serializable>();
	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		
		panelComponentMap = TableAddedEvent.getPanelComponentMap();
		panelComponentMap.remove(e.getNetwork().getDefaultNodeTable().getTitle());
	}

}
