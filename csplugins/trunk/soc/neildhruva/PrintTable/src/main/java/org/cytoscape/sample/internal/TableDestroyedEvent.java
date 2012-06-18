package org.cytoscape.sample.internal;

import java.util.HashMap;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;

public class TableDestroyedEvent implements NetworkAboutToBeDestroyedListener{

	private MyCytoPanel myCytoPanel;
	private HashMap<String, Object> panelComponentMap;
	
	TableDestroyedEvent(MyCytoPanel myCytoPanel){
		
		this.myCytoPanel = myCytoPanel;
		panelComponentMap = new HashMap<String, Object>();
	}
	
	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent e) {
		
		panelComponentMap = TableAddedEvent.getPanelComponentMap();
		panelComponentMap.remove(e.getNetwork().getDefaultNodeTable().getTitle());
		
		TableAddedEvent.networkDestroyed = true;
	}
}
