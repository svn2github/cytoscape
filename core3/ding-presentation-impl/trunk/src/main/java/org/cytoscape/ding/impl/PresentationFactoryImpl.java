
package org.cytoscape.ding.impl;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import org.cytoscape.ding.BirdsEyeView;
import org.cytoscape.ding.GraphView;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.spacial.SpacialIndex2DFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.RootVisualLexicon;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.model.events.NetworkViewChangedListener;
import org.cytoscape.view.model.NodeViewTaskFactory;
import org.cytoscape.view.model.EdgeViewTaskFactory;
import org.cytoscape.view.model.EmptySpaceTaskFactory;
import org.cytoscape.view.presentation.NavigationPresentation;
import org.cytoscape.view.presentation.NetworkRenderer;
import org.cytoscape.view.presentation.PresentationFactory;
import org.cytoscape.work.UndoSupport;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TunableInterceptor;

public class PresentationFactoryImpl implements PresentationFactory, NetworkViewChangedListener {

	private CyDataTableFactory dataTableFactory;
	private CyRootNetworkFactory rootNetworkFactory;
	private SpacialIndex2DFactory spacialFactory;
	private UndoSupport undo;
	private RootVisualLexicon rootLexicon;
	private VisualLexicon dingLexicon;
	private Map<CyNetworkView, DGraphView> viewMap;

	private Map<NodeViewTaskFactory,Map> nodeViewTFs;
	private Map<EdgeViewTaskFactory,Map> edgeViewTFs;
	private Map<EmptySpaceTaskFactory,Map> emptySpaceTFs;

	private TunableInterceptor ti;
	private TaskManager tm;

	public PresentationFactoryImpl(CyDataTableFactory dataTableFactory, 
	                            CyRootNetworkFactory rootNetworkFactory,
								UndoSupport undo, SpacialIndex2DFactory spacialFactory,
								RootVisualLexicon vpc, VisualLexicon dingLexicon, 
								TunableInterceptor ti, TaskManager tm) {
		this.dataTableFactory = dataTableFactory;
		this.rootNetworkFactory = rootNetworkFactory;
		this.spacialFactory = spacialFactory;
		this.undo = undo;
		this.rootLexicon = vpc;
		this.dingLexicon = dingLexicon;
		this.ti = ti;
		this.tm = tm;

		viewMap = new HashMap<CyNetworkView, DGraphView>();
		nodeViewTFs = new HashMap<NodeViewTaskFactory,Map>();
		edgeViewTFs = new HashMap<EdgeViewTaskFactory,Map>();
		emptySpaceTFs = new HashMap<EmptySpaceTaskFactory,Map>();
	}

	/**
	 * 
	 */
	public void addPresentation(Object frame, CyNetworkView view) {
				
		if ( view == null )
			throw new NullPointerException("CyNetworkView is null");
		if ( frame instanceof JComponent ) {
			
			DGraphView dgv = new DGraphView(view,dataTableFactory,rootNetworkFactory,undo,spacialFactory,
					rootLexicon, dingLexicon,nodeViewTFs,edgeViewTFs,emptySpaceTFs,ti,tm);
			viewMap.put(view, dgv);
			view.addViewChangeListener(dgv);
			
			if(frame instanceof JInternalFrame) {	
				JInternalFrame inFrame = (JInternalFrame)frame;
				JDesktopPane desktopPane = inFrame.getDesktopPane();
	
				// TODO - not sure this layered pane bit is optimal
				inFrame.setContentPane( dgv.getContainer(inFrame.getLayeredPane()) );
				dgv.addTransferComponent(desktopPane);
			} else {
				JComponent component = (JComponent) frame;
				component.add(dgv.getComponent());
			}
		} else {
			throw new IllegalArgumentException("frame object is not of type JInternalFrame, which is invalid for this implementation of PresentationFactory");
		}
	}

	public NavigationPresentation addNavigationPresentation(Object targetComponent, Object navBounds) {
		if ( !(targetComponent instanceof JPanel) )
			throw new IllegalArgumentException("targetComponent object is not of type JPanel, which is invalid for this implementation of PresentationFactory");

		if ( !(navBounds instanceof Component) ) 
			throw new IllegalArgumentException("navBounds object is not of type Component, which is invalid for this implementation of PresentationFactory");
		
		JPanel target = (JPanel)targetComponent;

		BirdsEyeView bev = new BirdsEyeView((Component)navBounds,this);	
		target.add( bev );

		return bev;
	}

	
	public void handleEvent(NetworkViewChangedEvent nvce) {
		DGraphView gv = viewMap.get(nvce.getNetworkView());
		if ( gv != null )
			gv.updateView();
	}

	public DGraphView getGraphView(CyNetworkView cnv) {
		return viewMap.get(cnv);
	}

	public void addNodeViewTaskFactory(NodeViewTaskFactory nvtf, Map props) {
		System.out.println("addNodeViewTaskFactory");
		if ( nvtf == null )
			return;

		nodeViewTFs.put(nvtf,props);
	}

	public void removeNodeViewTaskFactory(NodeViewTaskFactory nvtf, Map props) {
		System.out.println("removeNodeViewTaskFactory");
		if ( nvtf == null )
			return;

		nodeViewTFs.remove(nvtf);
	}

	public void addEdgeViewTaskFactory(EdgeViewTaskFactory evtf, Map props) {
		System.out.println("addEdgeViewTaskFactory");
		if ( evtf == null )
			return;

		edgeViewTFs.put(evtf,props);
	}

	public void removeEdgeViewTaskFactory(EdgeViewTaskFactory evtf, Map props) {
		System.out.println("removeEdgeViewTaskFactory");
		if ( evtf == null )
			return;

		edgeViewTFs.remove(evtf);
	}

	public void addEmptySpaceTaskFactory(EmptySpaceTaskFactory evtf, Map props) {
		System.out.println("addEmptySpaceTaskFactory");
		if ( evtf == null )
			return;

		emptySpaceTFs.put(evtf,props);
	}

	public void removeEmptySpaceTaskFactory(EmptySpaceTaskFactory evtf, Map props) {
		System.out.println("removeEmptySpaceTaskFactory");
		if ( evtf == null )
			return;

		emptySpaceTFs.remove(evtf);
	}

	public NetworkRenderer getPresentation(CyNetworkView view) {
		return this.viewMap.get(view);
	}

}
