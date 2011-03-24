package org.cytoscape.cpath2.internal.task;

import org.cytoscape.cpath2.internal.web_service.CPathWebService;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ExecutePhysicalEntitySearchTaskFactory implements TaskFactory {
	private final CPathWebService webApi;
	private final String keyword;
	private final int ncbiTaxonomyId;
	private ExecutePhysicalEntitySearch task;

	public ExecutePhysicalEntitySearchTaskFactory(CPathWebService webApi, String keyword, int ncbiTaxonomyId) {
		this.webApi = webApi;
		this.keyword = keyword;
		this.ncbiTaxonomyId = ncbiTaxonomyId;
	}

	@Override
	public TaskIterator getTaskIterator() {
		task = new ExecutePhysicalEntitySearch(webApi, keyword, ncbiTaxonomyId);
		return new TaskIterator(task);
	}

	public int getNumMatchesFound() {
		return task.getNumMatchesFound();
	}
}
