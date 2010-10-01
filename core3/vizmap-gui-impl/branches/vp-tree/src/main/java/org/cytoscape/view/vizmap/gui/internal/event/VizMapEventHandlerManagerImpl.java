package org.cytoscape.view.vizmap.gui.internal.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.util.HashMap;
import java.util.Map;

import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.event.VizMapEventHandler;
import org.cytoscape.view.vizmap.gui.event.VizMapEventHandlerManager;
import org.cytoscape.view.vizmap.gui.internal.VizMapPropertySheetBuilder;
import org.cytoscape.view.vizmap.gui.internal.VizMapperMainPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.l2fprod.common.propertysheet.PropertySheetPanel;

import cytoscape.Cytoscape;

public class VizMapEventHandlerManagerImpl implements
		VizMapEventHandlerManager, PropertyChangeListener {

	private static final Logger logger = LoggerFactory
			.getLogger(VizMapEventHandlerManagerImpl.class);

	private Map<String, AbstractVizMapEventHandler> eventHandlers;

	private final EditorManager editorManager;
	private final VizMapperMainPanel gui;

	private VizMapPropertySheetBuilder vizMapPropertySheetBuilder;

	public VizMapEventHandlerManagerImpl(final EditorManager editorManager,
			final VizMapPropertySheetBuilder vizMapPropertySheetBuilder,
			final PropertySheetPanel propertySheetPanel, final VizMapperMainPanel gui) {
		this.vizMapPropertySheetBuilder = vizMapPropertySheetBuilder;
		this.editorManager = editorManager;
		this.gui = gui;
		
		registerCellEditorListeners();

		eventHandlers = new HashMap<String, AbstractVizMapEventHandler>();
		createHandlers(propertySheetPanel);
		
		
	}

	private void createHandlers(PropertySheetPanel propertySheetPanel) {
		AbstractVizMapEventHandler windowEventHandler = new EditorWindowEventHandler();
		eventHandlers.put(EditorManager.EDITOR_WINDOW_CLOSED,
				windowEventHandler);
		eventHandlers.put(EditorManager.EDITOR_WINDOW_OPENED,
				windowEventHandler);

		eventHandlers.put(Cytoscape.CYTOSCAPE_INITIALIZED,
				new InitializedEventHandler());

		AbstractVizMapEventHandler loadHandler = new DataLoadedEventHandler();
		eventHandlers.put(Cytoscape.VIZMAP_LOADED, loadHandler);

		// TODO: create session event handler
		// eventHandlers.put(Cytoscape.SESSION_LOADED, loadHandler);

		AbstractVizMapEventHandler attrHandler = new AttributeUpdateEventHandler(
				vizMapPropertySheetBuilder);
		eventHandlers.put(Cytoscape.ATTRIBUTES_CHANGED, attrHandler);
		eventHandlers.put(Cytoscape.NETWORK_LOADED, attrHandler);

		eventHandlers.put("VALUE", new CellEditorEventHandler(
				propertySheetPanel, gui));

	}

	/*
	 * Register listeners for editors.
	 */
	private void registerCellEditorListeners() {
		// FIXME
		for (PropertyEditor p : editorManager.getCellEditors()) {
			p.addPropertyChangeListener(this);

			// if (p instanceof PropertyChangeListener)
			// spcs.addPropertyChangeListener((PropertyChangeListener) p);
		}

		for (PropertyEditor p : editorManager.getAttributeSelectors()) {
			p.addPropertyChangeListener(this);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.cytoscape.vizmap.gui.event.VizMapEventHandlerManager#getHandler(java
	 * .lang.String)
	 */
	public VizMapEventHandler getHandler(String name) {
		return eventHandlers.get(name);
	}

	@Override
	public void propertyChange(PropertyChangeEvent e) {
		logger.debug("###################### VizMap local property change event called: "
				+ e.getPropertyName());

		final String handlerKey = e.getPropertyName();

		// Do nothing if null.
		if (handlerKey == null)
			return;

		final VizMapEventHandler handler = getHandler(handlerKey.toUpperCase());

		logger.debug("###################### Got handler: " + handler
				+ ", Source = " + e.getSource());

		if (handler != null)
			handler.processEvent(e);
	}

}
