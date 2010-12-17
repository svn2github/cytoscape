package org.cytoscape.ding.impl.customgraphics;

import java.util.Properties;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class RestoreImageTaskFactory implements TaskFactory {
	
	private final Properties props;
	private final CustomGraphicsManagerImpl manager;
	
	RestoreImageTaskFactory(final Properties props, final CustomGraphicsManagerImpl manager) {
		this.manager = manager;
		this.props = props;
	}

	@Override
	public TaskIterator getTaskIterator() {
		
		return new TaskIterator(new RestoreImageTask(props, manager));
	}

}
