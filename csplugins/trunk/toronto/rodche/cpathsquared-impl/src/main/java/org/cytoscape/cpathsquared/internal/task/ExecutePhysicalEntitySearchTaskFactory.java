package org.cytoscape.cpathsquared.internal.task;

import org.cytoscape.cpathsquared.internal.CPathWebService;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ExecutePhysicalEntitySearchTaskFactory implements TaskFactory {
	private final CPathWebService webApi;
	private final String keyword;
	private final int ncbiTaxonomyId;
	private ExecutePhysicalEntitySearchTask task;
	private ResultHandler result;

	public ExecutePhysicalEntitySearchTaskFactory(CPathWebService webApi, String keyword, int ncbiTaxonomyId, ResultHandler result) {
		this.webApi = webApi;
		this.keyword = keyword;
		this.ncbiTaxonomyId = ncbiTaxonomyId;
		this.result = result;
	}

	@Override
	public TaskIterator createTaskIterator() {
		task = new ExecutePhysicalEntitySearchTask(webApi, keyword, ncbiTaxonomyId, result);
		return new TaskIterator(task);
	}

	public interface ResultHandler {
		void finished(int matchesFound) throws Exception;
	}
}
