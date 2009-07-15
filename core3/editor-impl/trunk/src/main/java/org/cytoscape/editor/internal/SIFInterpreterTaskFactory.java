
package org.cytoscape.editor.internal;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.work.Task;

public class SIFInterpreterTaskFactory extends AbstractNetworkViewTaskFactory {

	public Task getTask() {
		return new SIFInterpreterTask( view );
	}
}

