
package org.cytoscape.ding.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

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

public class DingRenderingEngineFactory implements RenderingEngineFactory<CyNetwork>, NetworkViewChangedListener {
	
	private static final Logger logger = LoggerFactory.getLogger(DingRenderingEngineFactory.class);

	private CyDataTableFactory dataTableFactory;
	private CyRootNetworkFactory rootNetworkFactory;
	private SpacialIndex2DFactory spacialFactory;
	private UndoSupport undo;
	private RootVisualLexicon rootLexicon;
	private VisualLexicon dingLexicon;
	private CyServiceRegistrar registrar;
	
	private Map<CyNetworkView, DGraphView> viewMap;

	private Map<NodeViewTaskFactory,Map> nodeViewTFs;
	private Map<EdgeViewTaskFactory,Map> edgeViewTFs;
	private Map<NetworkViewTaskFactory,Map> emptySpaceTFs;

	private TunableInterceptor ti;
	private TaskManager tm;
	private final CyTableManager tableMgr;

	
	public DingRenderingEngineFactory(CyDataTableFactory dataTableFactory, 
	                            CyRootNetworkFactory rootNetworkFactory,
								UndoSupport undo, SpacialIndex2DFactory spacialFactory,
								RootVisualLexicon vpc, VisualLexicon dingLexicon, 
								TunableInterceptor ti, TaskManager tm,
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
		nodeViewTFs = new HashMap<NodeViewTaskFactory,Map>();
		edgeViewTFs = new HashMap<EdgeViewTaskFactory,Map>();
		emptySpaceTFs = new HashMap<NetworkViewTaskFactory,Map>();
	}

	/**
	 * Render given view model by Ding rendering engine.
	 * 
	 */
	public RenderingEngine<CyNetwork> render(Object presentationContainer, View<CyNetwork> view) {
				
		if ( view == null )
			throw new IllegalArgumentException("Cannot create presentation for null view model.");
		
		if ( view instanceof CyNetworkView == false )
			throw new IllegalArgumentException("Ding accepts CyNetworkView only.");
		
		final CyNetworkView targetView = (CyNetworkView) view;
		DGraphView dgv = null;
		if ( presentationContainer instanceof JComponent ) {
			
			logger.debug("Start rendering presentation by Ding: " + targetView.getSUID());
			dgv = new DGraphView(targetView, dataTableFactory,rootNetworkFactory,undo,spacialFactory,
					rootLexicon, dingLexicon,nodeViewTFs,edgeViewTFs,emptySpaceTFs,ti,tm, registrar,tableMgr);
			logger.info("DGraphView created as a presentation for view model: " + targetView.getSUID());
			viewMap.put(targetView, dgv);
			
			if(presentationContainer instanceof JInternalFrame) {	
				JInternalFrame inFrame = (JInternalFrame)presentationContainer;
				JDesktopPane desktopPane = inFrame.getDesktopPane();
	
				// TODO - not sure this layered pane bit is optimal
				inFrame.setContentPane( dgv.getContainer(inFrame.getLayeredPane()) );
				dgv.addTransferComponent(desktopPane);
			} else {
				JComponent component = (JComponent) presentationContainer;
				component.add(dgv.getComponent());
			}
			
		} else {
			throw new IllegalArgumentException("frame object is not of type JInternalFrame, which is invalid for this implementation of PresentationFactory");
		}

		registrar.registerAllServices(dgv, new Properties());
		registrar.registerAllServices(new AddDeleteHandler(dgv), new Properties());
		
		return dgv;
	}

	
	@Override
	public void handleEvent(NetworkViewChangedEvent nvce) {
		DGraphView gv = viewMap.get(nvce.getSource());
		if ( gv != null )
			gv.updateView();
	}

	public DGraphView getGraphView(CyNetworkView cnv) {
		return viewMap.get(cnv);
	}

	public void addNodeViewTaskFactory(NodeViewTaskFactory nvtf, Map props) {
		if ( nvtf == null )
			return;

		nodeViewTFs.put(nvtf,props);
	}

	public void removeNodeViewTaskFactory(NodeViewTaskFactory nvtf, Map props) {
		if ( nvtf == null )
			return;

		nodeViewTFs.remove(nvtf);
	}

	public void addEdgeViewTaskFactory(EdgeViewTaskFactory evtf, Map props) {
		if ( evtf == null )
			return;

		edgeViewTFs.put(evtf,props);
	}

	public void removeEdgeViewTaskFactory(EdgeViewTaskFactory evtf, Map props) {
		if ( evtf == null )
			return;

		edgeViewTFs.remove(evtf);
	}

	public void addNetworkViewTaskFactory(NetworkViewTaskFactory evtf, Map props) {
		if ( evtf == null )
			return;

		emptySpaceTFs.put(evtf,props);
	}

	public void removeNetworkViewTaskFactory(NetworkViewTaskFactory evtf, Map props) {
		if ( evtf == null )
			return;

		emptySpaceTFs.remove(evtf);
	}
}
