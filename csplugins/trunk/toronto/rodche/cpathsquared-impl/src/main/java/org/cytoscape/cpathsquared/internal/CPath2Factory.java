package org.cytoscape.cpathsquared.internal;

import java.util.List;

import javax.swing.JPanel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.cpathsquared.internal.view.BinarySifVisualStyleFactory;
import org.cytoscape.cpathsquared.internal.view.DownloadDetails;
import org.cytoscape.cpathsquared.internal.view.ResultsModel;
import org.cytoscape.cpathsquared.internal.view.SearchHitsPanel;
import org.cytoscape.cpathsquared.internal.view.DetailsPanel;
import org.cytoscape.cpathsquared.internal.view.SearchQueryPanel;
import org.cytoscape.cpathsquared.internal.view.SearchResultsPanel;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;

import cpath.service.jaxb.SearchHit;

// TODO: This is a "God" object.  Probably shouldn't exist, but it's better than having to
//       propagate all of the injected dependencies throughout all the implementation classes.
//       Lesser of two evils.
public final class CPath2Factory {
	private static CySwingApplication application;
	private static TaskManager taskManager;
	private static OpenBrowser openBrowser;
	private static CyNetworkManager networkManager;
	private static CyApplicationManager applicationManager;
	private static CyNetworkViewManager networkViewManager;
	private static CyNetworkReaderManager networkViewReaderManager;
	private static CyNetworkNaming naming;
	private static CyNetworkFactory networkFactory;
	private static CyLayoutAlgorithmManager layoutManager;
	private static UndoSupport undoSupport;
	private static BinarySifVisualStyleFactory binarySifVisualStyleUtil;
	private static VisualMappingManager mappingManager;
	
	// non-instantiable static factory class
	private CPath2Factory() {
		throw new AssertionError();
	}
	
	public static void init(CySwingApplication app, TaskManager tm, OpenBrowser ob, 
			CyNetworkManager nm, CyApplicationManager am, CyNetworkViewManager nvm, 
			CyNetworkReaderManager nvrm, CyNetworkNaming nn, CyNetworkFactory nf, 
			CyLayoutAlgorithmManager lam, UndoSupport us, 
			BinarySifVisualStyleFactory bsvsf, VisualMappingManager mm) 
	{
		application = app;
		taskManager = tm;
		openBrowser = ob;
		networkManager = nm;
		applicationManager = am;
		networkViewManager = nvm;
		networkViewReaderManager = nvrm;
		naming = nn;
		layoutManager = lam;
		networkFactory = nf;
		undoSupport = us;
		binarySifVisualStyleUtil = bsvsf;
		mappingManager = mm;
	}
	
	/**
	 * Creates a new universal task factory
	 * (can contain one or more different tasks)
	 * 
	 * @return
	 */
	public static TaskFactory newTaskFactory(final Task... tasks) {
		return new TaskFactory() {
			@Override
			public TaskIterator createTaskIterator() {
				return new TaskIterator(tasks);
			}
		};
	}
	
	public static SearchQueryPanel createSearchQueryPanel() {
		return new SearchQueryPanel();
	}

	public static OpenBrowser getOpenBrowser() {
		return openBrowser;
	}

	public static CySwingApplication getCySwingApplication() {
		return application;
	}

	public static TaskManager getTaskManager() {
		return taskManager;
	}

	public static DownloadDetails createDownloadDetails(List<SearchHit> passedRecordList) {
		return new DownloadDetails(passedRecordList);
	}

	public static JPanel createFilterPanel(ResultsModel model) {
		return new SearchHitsPanel(model);
	}
	
	public static DetailsPanel createDetailsPanel(SearchResultsPanel searchHitsPanel) {
		return new DetailsPanel(searchHitsPanel);
	}

	public static CyNetworkManager getNetworkManager() {
		return networkManager;
	}

	public static CyApplicationManager getCyApplicationManager() {
		return applicationManager;
	}

	public static CyNetworkViewManager getCyNetworkViewManager() {
		return networkViewManager;
	}

	public static CyNetworkReaderManager getCyNetworkViewReaderManager() {
		return networkViewReaderManager;
	}

	public static CyNetworkNaming getCyNetworkNaming() {
		return naming;
	}

	public static CyNetworkFactory getCyNetworkFactory() {
		return networkFactory;
	}

	public static UndoSupport getUndoSupport() {
		return undoSupport;
	}

	public static CyNetworkManager getCyNetworkManager() {
		return networkManager;
	}

	public static CyLayoutAlgorithmManager getCyLayoutAlgorithmManager() {
		return layoutManager;
	}
	
	public static BinarySifVisualStyleFactory getBinarySifVisualStyleUtil() {
		return binarySifVisualStyleUtil;
	}

	public static CySwingApplication getApplication() {
		return application;
	}

	public static CyApplicationManager getApplicationManager() {
		return applicationManager;
	}

	public static CyNetworkViewManager getNetworkViewManager() {
		return networkViewManager;
	}

	public static CyNetworkReaderManager getNetworkViewReaderManager() {
		return networkViewReaderManager;
	}

	public static CyNetworkNaming getNaming() {
		return naming;
	}

	public static CyNetworkFactory getNetworkFactory() {
		return networkFactory;
	}

	public static CyLayoutAlgorithmManager getLayoutManager() {
		return layoutManager;
	}

	public static VisualMappingManager getMappingManager() {
		return mappingManager;
	}

}
