package org.cytoscape.view.vizmap.gui.internal.event;

import java.beans.PropertyChangeEvent;

import javax.annotation.Resource;

import org.cytoscape.view.vizmap.gui.editor.EditorManager;
import org.cytoscape.view.vizmap.gui.internal.EditorWindowManager;
import org.cytoscape.viewmodel.VisualProperty;

public class EditorWindowEventHandler extends AbstractVizMapEventHandler {
	
	@Resource
	EditorWindowManager editorWindowManager;

	@Override
	public void processEvent(PropertyChangeEvent e) {
		editorWindowManager.manageWindow(e.getPropertyName(), (VisualProperty) e.getNewValue(),
				e.getSource());

		if (e.getPropertyName().equals(EditorManager.EDITOR_WINDOW_CLOSED))
			editorWindowManager.removeEditorWindow((VisualProperty) e.getNewValue());
	}


}
