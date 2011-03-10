package org.cytoscape.tableimport.internal;


import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.GUITaskManager;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.tableimport.internal.util.CytoscapeServices;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.util.swing.FileUtil;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.session.CyNetworkNaming;

public class ImportNetworkTableTaskFactory implements TaskFactory {
	private final static long serialVersionUID = 1205339869460898L;
	
	/**
	 * Creates a new ImportAttributeTableTaskFactory object.
	 */ 
	public ImportNetworkTableTaskFactory(CySwingApplication desktop,CyApplicationManager appMgr,
			CyNetworkManager netMgr,
			CyProperty<Bookmarks> bookmarksProp, BookmarksUtil bookmarksUtil,
			GUITaskManager guiTaskManagerServiceRef, CyProperty cytoscapePropertiesServiceRef,
			CyTableManager tblMgr, FileUtil fileUtilService, OpenBrowser openBrowserService,
			CyLayouts cyLayoutsServiceRef, CyNetworkViewFactory cyNetworkViewFactoryServiceRef,
			CyNetworkFactory cyNetworkFactoryServiceRef,
		    CyNetworkViewManager networkViewManager,CyNetworkNaming cyNetworkNaming) 

	{
		CytoscapeServices.desktop = desktop;
		CytoscapeServices.bookmarksUtil = bookmarksUtil;
		CytoscapeServices.cytoscapePropertiesServiceRef= cytoscapePropertiesServiceRef;
		CytoscapeServices.guiTaskManagerServiceRef = guiTaskManagerServiceRef;
		CytoscapeServices.tblMgr =tblMgr;
		CytoscapeServices.theBookmarks = bookmarksProp.getProperties();
		CytoscapeServices.openBrowser = openBrowserService;
		CytoscapeServices.fileUtil = fileUtilService;
		CytoscapeServices.appMgr = appMgr;
		CytoscapeServices.netMgr = netMgr;
		
		CytoscapeServices.cyLayoutsServiceRef= cyLayoutsServiceRef;
		CytoscapeServices.cyNetworkViewFactoryServiceRef = cyNetworkViewFactoryServiceRef;
		CytoscapeServices.cyNetworkFactoryServiceRef = cyNetworkFactoryServiceRef;
		CytoscapeServices.networkViewManager = networkViewManager;
		CytoscapeServices.cyNetworkNaming = cyNetworkNaming;
	}
	
	
	public TaskIterator getTaskIterator() {
		return new TaskIterator(new ImportNetworkTableTask());
	} 	
}

