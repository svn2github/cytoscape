package org.cytoscape.cpathsquared.internal.task;

import java.util.Set;

import org.cytoscape.cpathsquared.internal.CPath2WebService;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class ExecuteSearchTaskFactory implements TaskFactory {
	private final CPath2WebService webApi;
	private final String keyword;
	private final Set<String> organism;
	private final Set<String> datasource;
	private ExecuteSearchTask task;
	private ResultHandler result;

	public ExecuteSearchTaskFactory(CPath2WebService webApi, String keyword, Set<String> organism, Set<String> datasource, ResultHandler result) {
		this.webApi = webApi;
		this.keyword = keyword;
		this.organism = organism;
		this.datasource = datasource;
		this.result = result;
	}

	@Override
	public TaskIterator createTaskIterator() {
		task = new ExecuteSearchTask(webApi, keyword, organism, datasource, result);
		return new TaskIterator(task);
	}

	public interface ResultHandler {
		void finished(int matchesFound) throws Exception;
	}
}
