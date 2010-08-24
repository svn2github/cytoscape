package org.cytoscape.ding.impl;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.spacial.SpacialIndex2DFactory;
import org.cytoscape.task.EdgeViewTaskFactory;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.RootVisualLexicon;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedListener;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.RenderingEngineFactory;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;
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
		RenderingEngineFactory<CyNetwork>, NetworkViewChangedListener {
	
	private static final Logger logger = LoggerFactory.getLogger(DingNavigationRenderingEngineFactory.class);

	private CyDataTableFactory dataTableFactory;
	private CyRootNetworkFactory rootNetworkFactory;
	private SpacialIndex2DFactory spacialFactory;
	private UndoSupport undo;
	private RootVisualLexicon rootLexicon;
	private VisualLexicon dingLexicon;
	private CyServiceRegistrar registrar;

	private Map<CyNetworkView, DGraphView> viewMap;

	private Map<NodeViewTaskFactory, Map> nodeViewTFs;
	private Map<EdgeViewTaskFactory, Map> edgeViewTFs;
	private Map<NetworkViewTaskFactory, Map> emptySpaceTFs;

	private TunableInterceptor ti;
	private TaskManager tm;
	private CyTableManager tableMgr;

	public DingNavigationRenderingEngineFactory(
			CyDataTableFactory dataTableFactory,
			CyRootNetworkFactory rootNetworkFactory, UndoSupport undo,
			SpacialIndex2DFactory spacialFactory, RootVisualLexicon vpc,
			VisualLexicon dingLexicon, TunableInterceptor ti, TaskManager tm,
			CyServiceRegistrar registrar, CyTableManager tableMgr) {
		this.dataTableFactory = dataTableFactory;
		this.rootNetworkFactory = rootNetworkFactory;
		this.spacialFactory = spacialFactory;
		this.undo = undo;
		this.rootLexicon = vpc;
		this.dingLexicon = dingLexicon;
		this.ti = ti;
		this.tm = tm;
		this.registrar = registrar;
		this.tableMgr = tableMgr;

		viewMap = new HashMap<CyNetworkView, DGraphView>();
		nodeViewTFs = new HashMap<NodeViewTaskFactory, Map>();
		edgeViewTFs = new HashMap<EdgeViewTaskFactory, Map>();
		emptySpaceTFs = new HashMap<NetworkViewTaskFactory, Map>();
	}
	
	
	public RenderingEngine<CyNetwork> render(final Object visualizationContainer, final View<CyNetwork> view) {

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

		final DGraphView dgv = new DGraphView((CyNetworkView) view,
				dataTableFactory, rootNetworkFactory, undo, spacialFactory,
				rootLexicon, dingLexicon, nodeViewTFs, edgeViewTFs,
				emptySpaceTFs, ti, tm, registrar, tableMgr);
		
		logger.info("DGV created for navigation: View ID = " + view.getSUID());

		JPanel target = new JPanel();
		BirdsEyeView bev = new BirdsEyeView((Component) visualizationContainer,
				dgv);
		target.add(bev);

		return new DingNavigationRenderingEngine(dgv);

	}

	public void handleEvent(NetworkViewChangedEvent nvce) {
		DGraphView gv = viewMap.get(nvce.getSource());
		if (gv != null)
			gv.updateView();
	}

}
