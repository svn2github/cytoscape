package org.cytoscape.task.internal.loaddatatable;

import java.util.Properties;

import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.io.CyIOFactoryManager;
import org.cytoscape.io.read.CyDataTableReaderFactory;
import org.cytoscape.task.CyDataTableMapperTaskFactory;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.model.CyDataTableFactory;

public class LoadAttrsDataTableTaskFactoryImpl implements TaskFactory {
	CyIOFactoryManager<CyDataTableReaderFactory> iomanager;
	CyDataTableMapperTaskFactory mapperFactory;
	CyNetworkManager manager;
	CyDataTableFactory tableFactory;
	String objType;

	public LoadAttrsDataTableTaskFactoryImpl(
		CyIOFactoryManager<CyDataTableReaderFactory> iomanager,
		CyDataTableMapperTaskFactory mapperFactory,
		CyNetworkManager manager,
		CyDataTableFactory tableFactory,
		String objType)
	{
		this.iomanager = iomanager;
		this.mapperFactory = mapperFactory;
		this.manager = manager;
		this.tableFactory = tableFactory;
		this.objType = objType;
	}

	public Task getTask() {
		return new LoadAttrsDataTableTask(iomanager, mapperFactory, manager, tableFactory, objType);
	}
}
