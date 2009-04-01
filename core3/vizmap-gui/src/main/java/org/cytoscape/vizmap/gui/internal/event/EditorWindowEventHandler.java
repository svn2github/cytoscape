package org.cytoscape.vizmap.gui.internal.event;

import java.beans.PropertyChangeEvent;

import javax.annotation.Resource;

import org.cytoscape.viewmodel.VisualProperty;
import org.cytoscape.vizmap.gui.editors.EditorFactory;
import org.cytoscape.vizmap.gui.internal.EditorWindowManager;

public class EditorWindowEventHandler extends AbstractVizMapEventHandler {
	
	@Resource
	EditorWindowManager editorWindowManager;

	@Override
	public void processEvent(PropertyChangeEvent e) {
		editorWindowManager.manageWindow(e.getPropertyName(), (VisualProperty) e.getNewValue(),
				e.getSource());

		if (e.getPropertyName().equals(EditorFactory.EDITOR_WINDOW_CLOSED))
			editorWindowManager.removeEditorWindow((VisualProperty) e.getNewValue());
	}


}
