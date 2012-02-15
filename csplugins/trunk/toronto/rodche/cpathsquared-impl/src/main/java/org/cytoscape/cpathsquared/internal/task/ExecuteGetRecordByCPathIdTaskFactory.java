package org.cytoscape.cpathsquared.internal.task;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2WebService;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

import cpath.service.OutputFormat;

public class ExecuteGetRecordByCPathIdTaskFactory implements TaskFactory {

	private final CPath2WebService webApi;
	private final String[] ids;
	private final OutputFormat format;
	private final String networkTitle;
	private final CPath2Factory cPathFactory;
	private final VisualMappingManager mappingManager;

	public ExecuteGetRecordByCPathIdTaskFactory(CPath2WebService webApi,
			String[] ids, OutputFormat format, String networkTitle,
			CPath2Factory cPathFactory, VisualMappingManager mappingManager) {
		this.webApi = webApi;
		this.ids = ids;
		this.format = format;
		this.networkTitle = networkTitle;
		this.cPathFactory = cPathFactory;
		this.mappingManager = mappingManager;
	}


	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new ExecuteGetRecordByCPathId(webApi, ids, format, networkTitle, cPathFactory, mappingManager));
	}

}
