package org.cytoscape.cpath2.internal.task;

import org.cytoscape.cpath2.internal.CPath2Factory;
import org.cytoscape.cpath2.internal.biopax.view.BioPaxContainer;
import org.cytoscape.cpath2.internal.web_service.CPathResponseFormat;
import org.cytoscape.cpath2.internal.web_service.CPathWebService;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ExecuteGetRecordByCPathIdTaskFactory implements TaskFactory {

	private final CPathWebService webApi;
	private final long[] ids;
	private final CPathResponseFormat format;
	private final String networkTitle;
	private final CyNetwork networkToMerge;
	private final CPath2Factory cPathFactory;
	private final BioPaxContainer bpContainer;

	public ExecuteGetRecordByCPathIdTaskFactory(CPathWebService webApi,
			long[] ids, CPathResponseFormat format, String networkTitle,
			CyNetwork networkToMerge, CPath2Factory cPathFactory, BioPaxContainer bpContainer) {
		this.webApi = webApi;
		this.ids = ids;
		this.format = format;
		this.networkTitle = networkTitle;
		this.networkToMerge = networkToMerge;
		this.cPathFactory = cPathFactory;
		this.bpContainer = bpContainer;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ExecuteGetRecordByCPathId(webApi, ids, format, networkTitle, networkToMerge, cPathFactory, bpContainer));
	}

}
