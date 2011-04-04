package org.cytoscape.internal.shutdown;


import java.io.File;

import org.cytoscape.io.write.CyPropertyWriterManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;


final class PropertyWriterFactory implements TaskFactory {
	private final CyPropertyWriterManager writerManager;
	private final CyProperty property;
	private final File outputFile;

	PropertyWriterFactory(final CyPropertyWriterManager writerManager, final CyProperty property,
                              final File outputFile)
	{
		this.writerManager = writerManager;
		this.property      = property;
		this.outputFile    = outputFile;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new PropertyWriter(writerManager, property, outputFile));
	}
}