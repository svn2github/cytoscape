package org.cytoscape.cpathsquared.internal;

import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.biopax.BioPaxContainer;
import org.cytoscape.biopax.MapBioPaxToCytoscapeFactory;
import org.cytoscape.biopax.NetworkListener;
import org.cytoscape.cpath.service.jaxb.SearchHitType;
import org.cytoscape.cpathsquared.internal.mapping.MapCPathToCytoscape;
import org.cytoscape.cpathsquared.internal.task.ExecuteGetRecordByCPathIdTaskFactory;
import org.cytoscape.cpathsquared.internal.util.BinarySifVisualStyleUtil;
import org.cytoscape.cpathsquared.internal.util.NetworkUtil;
import org.cytoscape.cpathsquared.internal.view.DownloadDetails;
import org.cytoscape.cpathsquared.internal.view.InteractionBundleModel;
import org.cytoscape.cpathsquared.internal.view.InteractionBundlePanel;
import org.cytoscape.cpathsquared.internal.view.PathwayTableModel;
import org.cytoscape.cpathsquared.internal.view.PhysicalEntityDetailsPanel;
import org.cytoscape.cpathsquared.internal.view.SearchBoxPanel;
import org.cytoscape.cpathsquared.internal.view.SearchDetailsPanel;
import org.cytoscape.cpathsquared.internal.view.SearchHitsPanel;
import org.cytoscape.cpathsquared.internal.web_service.CPathResponseFormat;
import org.cytoscape.cpathsquared.internal.web_service.CPathWebService;
import org.cytoscape.io.read.CyNetworkReaderManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;

// TODO: This is a "God" object.  Probably shouldn't exist, but it's better than having to
//       propagate all of the injected dependencies throughout all the implementation classes.
//       Lesser of two evils.
public class CPath2Factory {
	private final CySwingApplication application;
	private final TaskManager taskManager;
	private final OpenBrowser openBrowser;
	private final CyNetworkManager networkManager;
	private final CyApplicationManager applicationManager;
	private final CyNetworkViewManager networkViewManager;
	private final CyNetworkReaderManager networkViewReaderManager;
	private final CyNetworkNaming naming;
	private final CyNetworkFactory networkFactory;
	private final CyLayoutAlgorithmManager layoutManager;
	private final UndoSupport undoSupport;
	private final BioPaxContainer bpContainer;
	private final MapBioPaxToCytoscapeFactory mapperFactory;
	private final NetworkListener networkListener;
	private final BinarySifVisualStyleUtil binarySifVisualStyleUtil;
	private final VisualMappingManager mappingManager;
	
	public CPath2Factory(CySwingApplication application, TaskManager taskManager, OpenBrowser openBrowser, CyNetworkManager networkManager, CyApplicationManager applicationManager, CyNetworkViewManager networkViewManager, CyNetworkReaderManager networkViewReaderManager, CyNetworkNaming naming, CyNetworkFactory networkFactory, CyLayoutAlgorithmManager layouts, UndoSupport undoSupport, BioPaxContainer bpContainer, MapBioPaxToCytoscapeFactory mapperFactory, NetworkListener networkListener, BinarySifVisualStyleUtil binarySifVisualStyleUtil, VisualMappingManager mappingManager) {
		this.application = application;
		this.taskManager = taskManager;
		this.openBrowser = openBrowser;
		this.networkManager = networkManager;
		this.applicationManager = applicationManager;
		this.networkViewManager = networkViewManager;
		this.networkViewReaderManager = networkViewReaderManager;
		this.naming = naming;
		this.layoutManager = layouts;
		this.networkFactory = networkFactory;
		this.undoSupport = undoSupport;
		this.bpContainer = bpContainer;
		this.mapperFactory = mapperFactory;
		this.networkListener = networkListener;
		this.binarySifVisualStyleUtil = binarySifVisualStyleUtil;
		this.mappingManager = mappingManager;
	}
	
	public ExecuteGetRecordByCPathIdTaskFactory createExecuteGetRecordByCPathIdTaskFactory(CPathWebService webApi, String[] ids, CPathResponseFormat format, String networkTitle, CyNetwork networkToMerge) {
		return new ExecuteGetRecordByCPathIdTaskFactory(webApi, ids, format, networkTitle, networkToMerge, this, bpContainer, mapperFactory, networkListener, mappingManager);
	}

	public ExecuteGetRecordByCPathIdTaskFactory createExecuteGetRecordByCPathIdTaskFactory(
			CPathWebService webApi, String[] ids, CPathResponseFormat format, String title) {
		return new ExecuteGetRecordByCPathIdTaskFactory(webApi, ids, format, title, null, this, bpContainer, mapperFactory, networkListener, mappingManager);
	}

	public SearchBoxPanel createSearchBoxPanel(CPathWebService webApi) {
		return new SearchBoxPanel(webApi, this);
	}

	public OpenBrowser getOpenBrowser() {
		return openBrowser;
	}

	public SearchHitsPanel createSearchHitsPanel(
			InteractionBundleModel interactionBundleModel,
			PathwayTableModel pathwayTableModel, CPathWebService webApi) {
		return new SearchHitsPanel(interactionBundleModel, pathwayTableModel, webApi, this);
	}

	public CySwingApplication getCySwingApplication() {
		return application;
	}

	public TaskManager getTaskManager() {
		return taskManager;
	}

	public DownloadDetails createDownloadDetails(List<SearchHitType> passedRecordList, String physicalEntityName) {
		return new DownloadDetails(passedRecordList, physicalEntityName, this);
	}

	public JPanel createInteractionBundlePanel(InteractionBundleModel interactionBundleModel) {
		return new InteractionBundlePanel(interactionBundleModel, this);
	}

	public InteractionBundlePanel createInteractionBundlePanel(
			InteractionBundleModel interactionBundleModel, CyNetwork network,
			JDialog dialog) {
		return new InteractionBundlePanel(interactionBundleModel, network, dialog, this);
	}

	public PhysicalEntityDetailsPanel createPhysicalEntityDetailsPanel(SearchHitsPanel searchHitsPanel) {
		return new PhysicalEntityDetailsPanel(searchHitsPanel, this);
	}

	public SearchDetailsPanel createSearchDetailsPanel(
			InteractionBundleModel interactionBundleModel,
			PathwayTableModel pathwayTableModel) {
		return new SearchDetailsPanel(interactionBundleModel, pathwayTableModel, this);
	}

	public CyNetworkManager getNetworkManager() {
		return networkManager;
	}

	public CyApplicationManager getCyApplicationManager() {
		return applicationManager;
	}

	public CyNetworkViewManager getCyNetworkViewManager() {
		return networkViewManager;
	}

	public CyNetworkReaderManager getCyNetworkViewReaderManager() {
		return networkViewReaderManager;
	}

	public CyNetworkNaming getCyNetworkNaming() {
		return naming;
	}

	public Thread createNetworkUtil(String cpathRequest, CyNetwork network, boolean merging) {
		return new NetworkUtil(cpathRequest, network, merging, this);
	}

	public MapCPathToCytoscape createMapCPathToCytoscape() {
		return new MapCPathToCytoscape(this);
	}

	public CyNetworkFactory getCyNetworkFactory() {
		return networkFactory;
	}

	public UndoSupport getUndoSupport() {
		return undoSupport;
	}

	public CPathNetworkImportTask createCPathNetworkImportTask(String query, CPathWebService client, CPathResponseFormat format) {
		return new CPathNetworkImportTask(query, client, format, this);
	}

	public CyNetworkManager getCyNetworkManager() {
		return networkManager;
	}

	public CyLayoutAlgorithmManager getCyLayoutAlgorithmManager() {
		return layoutManager;
	}
	
	public BinarySifVisualStyleUtil getBinarySifVisualStyleUtil() {
		return binarySifVisualStyleUtil;
	}
}
