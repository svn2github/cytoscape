package org.cytoscape.view.vizmap.gui.internal.event;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.vizmap.gui.editors.EditorFactory;
import org.cytoscape.view.vizmap.gui.event.VizMapEventHandler;
import org.cytoscape.view.vizmap.gui.event.VizMapEventHandlerManager;
import org.cytoscape.view.vizmap.gui.internal.VizMapPropertySheetBuilder;

import cytoscape.Cytoscape;

public class VizMapEventHandlerManagerImpl implements VizMapEventHandlerManager {

	private Map<String, AbstractVizMapEventHandler> eventHandlers;

	private VizMapPropertySheetBuilder vizMapPropertySheetBuilder;
	
	public VizMapEventHandlerManagerImpl(VizMapPropertySheetBuilder vizMapPropertySheetBuilder) {
		this.vizMapPropertySheetBuilder = vizMapPropertySheetBuilder;
		
		eventHandlers = new HashMap<String, AbstractVizMapEventHandler>();
		createHandlers();
	}

	private void createHandlers() {
		AbstractVizMapEventHandler windowEventHandler = new EditorWindowEventHandler();
		eventHandlers.put(EditorFactory.EDITOR_WINDOW_CLOSED,
				windowEventHandler);
		eventHandlers.put(EditorFactory.EDITOR_WINDOW_OPENED,
				windowEventHandler);

		eventHandlers.put(Cytoscape.CYTOSCAPE_INITIALIZED,
				new InitializedEventHandler());

		AbstractVizMapEventHandler loadHandler = new DataLoadedEventHandler();
		eventHandlers.put(Cytoscape.VIZMAP_LOADED, loadHandler);
		eventHandlers.put(Cytoscape.SESSION_LOADED, loadHandler);
		
		AbstractVizMapEventHandler attrHandler = new AttributeUpdateEventHandler(vizMapPropertySheetBuilder);
		eventHandlers.put(Cytoscape.ATTRIBUTES_CHANGED, attrHandler);
		eventHandlers.put(Cytoscape.NETWORK_LOADED, attrHandler);

		eventHandlers.put("VALUE", new CellEditorEventHandler());

	}

	/* (non-Javadoc)
	 * @see org.cytoscape.vizmap.gui.event.VizMapEventHandlerManager#getHandler(java.lang.String)
	 */
	public VizMapEventHandler getHandler(String name) {
		return eventHandlers.get(name);
	}

}
