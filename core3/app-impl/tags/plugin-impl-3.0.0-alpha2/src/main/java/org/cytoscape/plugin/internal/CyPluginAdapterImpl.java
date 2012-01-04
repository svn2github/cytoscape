
package org.cytoscape.plugin.internal;

import org.cytoscape.plugin.CyPluginAdapter;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.session.CySessionManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.layout.CyLayouts;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyleFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.cytoscape.work.swing.GUITaskManager;
import org.cytoscape.io.write.CyNetworkViewWriterManager;
import org.cytoscape.io.write.CySessionWriterManager;
//import org.cytoscape.io.write.CyTableWriterManager;
import org.cytoscape.io.write.CyPropertyWriterManager;
import org.cytoscape.io.write.PresentationWriterManager;
import org.cytoscape.io.read.CyNetworkViewReaderManager;
import org.cytoscape.io.read.CySessionReaderManager;
import org.cytoscape.io.read.CyTableReaderManager;
import org.cytoscape.io.read.CyPropertyReaderManager;
import org.cytoscape.property.CyProperty;
import org.cytoscape.service.util.CyServiceRegistrar;

import java.util.Properties;


/**
 * An implementation of CyPluginAdapter
 */
public class CyPluginAdapterImpl implements CyPluginAdapter {

	//
	// Since there are a lot of fields, keep them
	// in alphabetical order to maintain sanity.
	// Always make the field name same as the class
	// name, but with a lower case first letter.
	//
	// NOTE that grep and sort are very useful!
	//
	private final CyApplicationManager cyApplicationManager;
	private final CyEventHelper cyEventHelper;
	private final CyLayouts cyLayouts;
	private final CyNetworkFactory cyNetworkFactory;
	private final CyNetworkManager cyNetworkManager;
	private final CyNetworkViewFactory cyNetworkViewFactory;
	private final CyNetworkViewManager cyNetworkViewManager;
	private final CyNetworkViewReaderManager cyNetworkViewReaderManager;
	private final CyNetworkViewWriterManager cyNetworkViewWriterManager;
	private final CyProperty<Properties> cyProperty;
	private final CyPropertyReaderManager cyPropertyReaderManager;
	private final CyPropertyWriterManager cyPropertyWriterManager;
	private final CyRootNetworkFactory cyRootNetworkFactory;
	private final CyServiceRegistrar cyServiceRegistrar;
	private final CySessionManager cySessionManager;
	private final CySessionReaderManager cySessionReaderManager;
	private final CySessionWriterManager cySessionWriterManager;
	private final CySwingApplication cySwingApplication;
	private final CyTableFactory cyTableFactory;
	private final CyTableManager cyTableManager;
	private final CyTableReaderManager cyTableReaderManager;
//	private final CyTableWriterManager cyTableWriterManager;
	private final GUITaskManager guiTaskManager;
	private final PresentationWriterManager presentationWriterManager;
	private final RenderingEngineManager renderingEngineManager;
	private final TaskManager taskManager;
	private final UndoSupport undoSupport;
	private final VisualMappingManager visualMappingManager;
	private final VisualStyleFactory visualStyleFactory;

	//
	// Since this is implementation code, there shouldn't be a
	// a problem adding new arguments as needed.  Therefore, to
	// maintain sanity, keep the arguments in alphabetical order.
	//
	CyPluginAdapterImpl( final CyApplicationManager cyApplicationManager,
	                     final CyEventHelper cyEventHelper,
	                     final CyLayouts cyLayouts,
	                     final CyNetworkFactory cyNetworkFactory,
	                     final CyNetworkManager cyNetworkManager,
	                     final CyNetworkViewFactory cyNetworkViewFactory,
	                     final CyNetworkViewManager cyNetworkViewManager,
	                     final CyNetworkViewReaderManager cyNetworkViewReaderManager,
	                     final CyNetworkViewWriterManager cyNetworkViewWriterManager,
	                     final CyProperty<Properties> cyProperty,
	                     final CyPropertyReaderManager cyPropertyReaderManager,
	                     final CyPropertyWriterManager cyPropertyWriterManager,
	                     final CyRootNetworkFactory cyRootNetworkFactory,
	                     final CyServiceRegistrar cyServiceRegistrar,
	                     final CySessionManager cySessionManager,
	                     final CySessionReaderManager cySessionReaderManager,
	                     final CySessionWriterManager cySessionWriterManager,
	                     final CySwingApplication cySwingApplication,
	                     final CyTableFactory cyTableFactory,
	                     final CyTableManager cyTableManager,
	                     final CyTableReaderManager cyTableReaderManager,
//	                     final CyTableWriterManager cyTableWriterManager,
	                     final GUITaskManager guiTaskManager,
	                     final PresentationWriterManager presentationWriterManager,
	                     final RenderingEngineManager renderingEngineManager,
	                     final TaskManager taskManager,
	                     final UndoSupport undoSupport,
	                     final VisualMappingManager visualMappingManager,
	                     final VisualStyleFactory visualStyleFactory
					    )
	{
		this.cyApplicationManager = cyApplicationManager;
		this.cyEventHelper = cyEventHelper;
		this.cyLayouts = cyLayouts;
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyNetworkViewManager = cyNetworkViewManager;
		this.cyNetworkViewReaderManager = cyNetworkViewReaderManager;
		this.cyNetworkViewWriterManager = cyNetworkViewWriterManager;
		this.cyProperty = cyProperty;
		this.cyPropertyReaderManager = cyPropertyReaderManager;
		this.cyPropertyWriterManager = cyPropertyWriterManager;
		this.cyRootNetworkFactory = cyRootNetworkFactory;
		this.cyServiceRegistrar = cyServiceRegistrar;
		this.cySessionManager = cySessionManager;
		this.cySessionReaderManager = cySessionReaderManager;
		this.cySessionWriterManager = cySessionWriterManager;
		this.cySwingApplication = cySwingApplication;
		this.cyTableFactory = cyTableFactory;
		this.cyTableManager = cyTableManager;
		this.cyTableReaderManager = cyTableReaderManager;
//		this.cyTableWriterManager = cyTableWriterManager;
		this.guiTaskManager = guiTaskManager;
		this.presentationWriterManager = presentationWriterManager;
		this.renderingEngineManager = renderingEngineManager;
		this.taskManager = taskManager;
		this.undoSupport = undoSupport;
		this.visualMappingManager = visualMappingManager;
		this.visualStyleFactory = visualStyleFactory;
	}

	//
	// May as well keep the methods alphabetical too!
	// 
	public CyApplicationManager getCyApplicationManager() { return cyApplicationManager; }
	public CyEventHelper getCyEventHelper() { return cyEventHelper; } 
	public CyLayouts getCyLayouts() { return cyLayouts; } 
	public CyNetworkFactory getCyNetworkFactory() { return cyNetworkFactory; }
	public CyNetworkManager getCyNetworkManager() { return cyNetworkManager; } 
	public CyNetworkViewFactory getCyNetworkViewFactory() { return cyNetworkViewFactory; }
	public CyNetworkViewManager getCyNetworkViewManager() { return cyNetworkViewManager; }
	public CyNetworkViewReaderManager getCyNetworkViewReaderManager() { return cyNetworkViewReaderManager; }
	public CyNetworkViewWriterManager getCyNetworkViewWriterManager() { return cyNetworkViewWriterManager; }
	public CyProperty<Properties> getCoreProperties() { return cyProperty; }
	public CyPropertyReaderManager getCyPropertyReaderManager() { return cyPropertyReaderManager; }
	public CyPropertyWriterManager getCyPropertyWriterManager() { return cyPropertyWriterManager; }
	public CyRootNetworkFactory getCyRootNetworkFactory() { return cyRootNetworkFactory; } 
	public CyServiceRegistrar getCyServiceRegistrar() { return cyServiceRegistrar; }
	public CySessionManager getCySessionManager() { return cySessionManager; } 
	public CySessionReaderManager getCySessionReaderManager() { return cySessionReaderManager; }
	public CySessionWriterManager getCySessionWriterManager() { return cySessionWriterManager; }
	public CySwingApplication getCySwingApplication() { return cySwingApplication; }
	public CyTableFactory getCyTableFactory() { return cyTableFactory; } 
	public CyTableManager getCyTableManager() { return cyTableManager; }
	public CyTableReaderManager getCyTableReaderManager() { return cyTableReaderManager; }
//	public CyTableWriterManager getCyTableWriterManager() { return cyTableWriterManager; }
	public GUITaskManager getGUITaskManager() { return guiTaskManager; }
	public PresentationWriterManager getPresentationWriterManager() { return presentationWriterManager; }
	public RenderingEngineManager getRenderingEngineManager() { return renderingEngineManager; }
	public TaskManager getTaskManager() { return taskManager; }
	public UndoSupport getUndoSupport() { return undoSupport; }
	public VisualMappingManager getVisualMappingManager() { return visualMappingManager; }
	public VisualStyleFactory getVisualStyleFactory() { return visualStyleFactory; }
}
