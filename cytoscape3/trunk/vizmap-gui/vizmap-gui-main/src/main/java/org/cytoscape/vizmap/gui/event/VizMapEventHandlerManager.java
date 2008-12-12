package org.cytoscape.vizmap.gui.event;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.vizmap.gui.editors.EditorFactory;

import cytoscape.Cytoscape;

public class VizMapEventHandlerManager {

	private Map<String, VizMapEventHandler> eventHandlers;

	public VizMapEventHandlerManager() {
		eventHandlers = new HashMap<String, VizMapEventHandler>();
	}

	public void createHandlers() {
		VizMapEventHandler windowEventHandler = new EditorWindowEventHandler();
		eventHandlers.put(EditorFactory.EDITOR_WINDOW_CLOSED,
				windowEventHandler);
		eventHandlers.put(EditorFactory.EDITOR_WINDOW_OPENED,
				windowEventHandler);

		eventHandlers.put(Cytoscape.CYTOSCAPE_INITIALIZED,
				new InitializedEventHandler());

		VizMapEventHandler loadHandler = new DataLoadedEventHandler();
		eventHandlers.put(Cytoscape.VIZMAP_LOADED, loadHandler);
		eventHandlers.put(Cytoscape.SESSION_LOADED, loadHandler);
		
		VizMapEventHandler attrHandler = new AttributeUpdateEventHandler();
		eventHandlers.put(Cytoscape.ATTRIBUTES_CHANGED, attrHandler);
		eventHandlers.put(Cytoscape.NETWORK_LOADED, attrHandler);

		eventHandlers.put("VALUE", new CellEditorEventHandler());

	}

	public VizMapEventHandler getHandler(String name) {
		return eventHandlers.get(name);
	}

}
