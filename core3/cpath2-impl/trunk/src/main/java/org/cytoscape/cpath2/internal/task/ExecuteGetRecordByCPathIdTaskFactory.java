package org.cytoscape.cpath2.internal.task;

import org.cytoscape.cpath2.internal.CPath2Factory;
import org.cytoscape.cpath2.internal.biopax.BioPaxFactory;
import org.cytoscape.cpath2.internal.web_service.CPathResponseFormat;
import org.cytoscape.cpath2.internal.web_service.CPathWebService;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ExecuteGetRecordByCPathIdTaskFactory implements TaskFactory {

	private CPathWebService webApi;
	private long[] ids;
	private CPathResponseFormat format;
	private String networkTitle;
	private CyNetwork networkToMerge;
	private final CPath2Factory cPathFactory;
	private BioPaxFactory bioPaxFactory;

	public ExecuteGetRecordByCPathIdTaskFactory(CPathWebService webApi,
			long[] ids, CPathResponseFormat format, String networkTitle,
			CyNetwork networkToMerge, CPath2Factory cPathFactory, BioPaxFactory bioPaxFactory) {
		this.webApi = webApi;
		this.ids = ids;
		this.format = format;
		this.networkTitle = networkTitle;
		this.networkToMerge = networkToMerge;
		this.cPathFactory = cPathFactory;
		this.bioPaxFactory = bioPaxFactory;
	}

	@Override
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ExecuteGetRecordByCPathId(webApi, ids, format, networkTitle, networkToMerge, cPathFactory, bioPaxFactory));
	}

}
