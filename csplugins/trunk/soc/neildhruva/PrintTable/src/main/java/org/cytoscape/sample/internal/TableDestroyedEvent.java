package org.cytoscape.sample.internal;

import java.util.HashMap;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

public class TableDestroyedEvent implements NetworkAboutToBeDestroyedListener{

	private HashMap<String, Object> panelComponentMap;
	
	TableDestroyedEvent(){
		
		panelComponentMap = new HashMap<String, Object>();
	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		
		panelComponentMap = TableAddedEvent.getPanelComponentMap();
		panelComponentMap.remove(e.getNetwork().getDefaultNodeTable().getTitle());
	}

}
