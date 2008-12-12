package org.cytoscape.vizmap.gui.event;

import java.beans.PropertyChangeEvent;

import javax.annotation.Resource;

import org.cytoscape.vizmap.VisualPropertyType;
import org.cytoscape.vizmap.gui.editors.EditorFactory;
import org.cytoscape.vizmap.gui.internal.EditorWindowManager;

public class EditorWindowEventHandler extends VizMapEventHandler {
	
	@Resource
	EditorWindowManager editorWindowManager;

	@Override
	public void processEvent(PropertyChangeEvent e) {
		editorWindowManager.manageWindow(e.getPropertyName(), (VisualPropertyType) e.getNewValue(),
				e.getSource());

		if (e.getPropertyName().equals(EditorFactory.EDITOR_WINDOW_CLOSED))
			editorWindowManager.removeEditorWindow((VisualPropertyType) e.getNewValue());
	}


}
