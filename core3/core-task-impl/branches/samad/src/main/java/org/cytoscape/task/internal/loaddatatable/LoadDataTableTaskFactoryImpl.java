package org.cytoscape.task.internal.loaddatatable;

import java.util.Properties;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.io.read.CyDataTableReaderFactory;
import org.cytoscape.task.CyDataTableMapperTaskFactory;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.model.CyDataTableFactory;

public class LoadDataTableTaskFactoryImpl implements TaskFactory {
	CyDataTableReaderFactory readerFactory;
	CyDataTableMapperTaskFactory mapperFactory;
	CyNetworkManager manager;
	CyDataTableFactory tableFactory;

	public LoadDataTableTaskFactoryImpl(
		CyDataTableReaderFactory readerFactory,
		CyDataTableMapperTaskFactory mapperFactory,
		CyNetworkManager manager,
		CyDataTableFactory tableFactory)
	{
		this.readerFactory = readerFactory;
		this.mapperFactory = mapperFactory;
		this.manager = manager;
		this.tableFactory = tableFactory;
	}

	public Task getTask() {
		return new LoadDataTableTask(readerFactory, mapperFactory, manager, tableFactory);
	}
}
