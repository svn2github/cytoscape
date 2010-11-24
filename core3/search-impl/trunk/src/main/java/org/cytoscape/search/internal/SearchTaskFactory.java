package org.cytoscape.search.internal;

import org.cytoscape.model.CyTableManager;
import org.cytoscape.task.AbstractNetworkViewTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.view.model.CyNetworkView;

public class SearchTaskFactory extends AbstractNetworkViewTaskFactory implements TaskFactory {
	private  CyTableManager tableMgr;
	private EnhancedSearch searchMgr;
	private String query;
	
	public SearchTaskFactory(CyNetworkView view, EnhancedSearch searchMgr, CyTableManager tableMgr, String query) {
		this.view = view;
		this.searchMgr = searchMgr;
		this.query = query;
	}

	public TaskIterator getTaskIterator() {
		return new TaskIterator(new IndexAndSearchTask(view, searchMgr, tableMgr, query));
	}
}
