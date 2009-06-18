
package org.cytoscape.editor.internal;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.EmptySpaceTaskFactory;
import org.cytoscape.work.Task;

public class SIFInterpreterTaskFactory implements EmptySpaceTaskFactory {

	private CyNetworkView view;

	public SIFInterpreterTaskFactory() {
	}

	public void setNetworkView(CyNetworkView view) {
		this.view = view;
	}

	public Task getTask() {
		return new SIFInterpreterTask( view );
	}
}

