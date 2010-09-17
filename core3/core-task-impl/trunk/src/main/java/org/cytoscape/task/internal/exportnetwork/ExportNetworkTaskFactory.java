package org.cytoscape.task.internal.exportnetwork;

import org.cytoscape.io.write.CyNetworkWriter;
import org.cytoscape.io.write.CyNetworkWriterManager;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

public class ExportNetworkTaskFactory extends AbstractNetworkTaskFactory {

	private CyNetworkWriterManager writerManager;

	public ExportNetworkTaskFactory(CyNetworkWriterManager writerManager) {
		this.writerManager = writerManager;
	}
	
	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new CyNetworkWriter(writerManager, net));
	}

}
