package org.cytoscape.ding.impl;


import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.spacial.SpacialIndex2DFactory;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.events.UpdateNetworkPresentationEvent;
import org.cytoscape.view.model.events.UpdateNetworkPresentationEventListener;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.undo.UndoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * RenderingEngineFactory for Navigation.
 * 
 * @author kono
 * 
 */
public class DingNavigationRenderingEngineFactory implements
		RenderingEngineFactory<CyNetwork>, UpdateNetworkPresentationEventListener
{
	private static final Logger logger = LoggerFactory.getLogger(DingNavigationRenderingEngineFactory.class);

	
	private final RenderingEngineManager renderingEngineManager;
	
	private CyTableFactory dataTableFactory;
	private CyRootNetworkFactory rootNetworkFactory;
	private SpacialIndex2DFactory spacialFactory;
	private UndoSupport undo;
	private VisualLexicon dingLexicon;
	private CyServiceRegistrar registrar;

	private Map<CyNetworkView, DGraphView> viewMap;

	private Map<NodeViewTaskFactory, Map> nodeViewTFs;
	private Map<EdgeViewTaskFactory, Map> edgeViewTFs;
	private Map<NetworkViewTaskFactory, Map> emptySpaceTFs;

	private TaskManager tm;
	private CyTableManager tableMgr;
	
	private final CyApplicationManager appManager;

	public DingNavigationRenderingEngineFactory(
			CyTableFactory dataTableFactory,
			CyRootNetworkFactory rootNetworkFactory, UndoSupport undo,
			SpacialIndex2DFactory spacialFactory,
			VisualLexicon dingLexicon, TaskManager tm,
			CyServiceRegistrar registrar, CyTableManager tableMgr, RenderingEngineManager renderingEngineManager,
			CyApplicationManager appManager
	) {

		this.dataTableFactory = dataTableFactory;
		this.rootNetworkFactory = rootNetworkFactory;
		this.spacialFactory = spacialFactory;
		this.undo = undo;
		this.dingLexicon = dingLexicon;
		this.tm = tm;
		this.registrar = registrar;
		this.tableMgr = tableMgr;
		this.renderingEngineManager = renderingEngineManager;
		this.appManager = appManager;

		viewMap = new HashMap<CyNetworkView, DGraphView>();
		nodeViewTFs = new HashMap<NodeViewTaskFactory, Map>();
		edgeViewTFs = new HashMap<EdgeViewTaskFactory, Map>();
		emptySpaceTFs = new HashMap<NetworkViewTaskFactory, Map>();
	}
	
	
	@Override public RenderingEngine<CyNetwork> getInstance(final Object visualizationContainer, final View<CyNetwork> view) {

		if (visualizationContainer == null)
			throw new IllegalArgumentException(
					"Visualization container is null.  This should be an JComponent for this rendering engine.");
		if (view == null)
			throw new IllegalArgumentException(
					"View Model is null.");

		if (!(visualizationContainer instanceof JComponent)
				|| !(view instanceof CyNetworkView))
			throw new IllegalArgumentException(
					"Visualization Container object is not of type Component, "
							+ "which is invalid for this implementation of PresentationFactory");
		
		final JComponent container = (JComponent) visualizationContainer;

		final RenderingEngine<CyNetwork> engine = appManager.getCurrentRenderingEngine();
		
		logger.info("!!!! DGV created for navigation: View ID = " + view.getSUID());


		final BirdsEyeView bev = new BirdsEyeView(container, (DGraphView) engine);
		
		container.setLayout(new BorderLayout());
		container.add(bev, BorderLayout.CENTER);
		
		this.renderingEngineManager.addRenderingEngine(bev);
		return bev;

	}

	
	/**
	 * Catch the events from view model layer.
	 * 
	 */
	@Override
	public void handleEvent(UpdateNetworkPresentationEvent nvce) {
		DGraphView gv = viewMap.get(nvce.getSource());
		if (gv != null)
			gv.updateView();
	}


	@Override
	public VisualLexicon getVisualLexicon() {
		
		return dingLexicon;
	}

}
