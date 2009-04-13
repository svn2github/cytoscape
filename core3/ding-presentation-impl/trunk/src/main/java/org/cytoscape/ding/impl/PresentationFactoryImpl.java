
package org.cytoscape.ding.impl;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualPropertyCatalog;
import org.cytoscape.view.model.events.NetworkViewChangedListener;
import org.cytoscape.view.model.events.NetworkViewChangedEvent;
import org.cytoscape.view.presentation.PresentationFactory;
import org.cytoscape.view.presentation.NavigationPresentation;
import org.cytoscape.ding.impl.DGraphView;
import org.cytoscape.ding.GraphViewFactory;
import org.cytoscape.ding.GraphView;
import org.cytoscape.ding.BirdsEyeView;
import org.cytoscape.spacial.SpacialIndex2DFactory;
import org.cytoscape.work.UndoSupport;
import javax.swing.JInternalFrame;
import javax.swing.JDesktopPane;
import javax.swing.JPanel;
import java.awt.Component;
import java.util.Map;
import java.util.HashMap;

public class PresentationFactoryImpl implements PresentationFactory, NetworkViewChangedListener {

	private CyDataTableFactory dataTableFactory;
	private CyRootNetworkFactory rootNetworkFactory;
	private SpacialIndex2DFactory spacialFactory;
	private UndoSupport undo;
	private VisualPropertyCatalog vpc;
	private Map<CyNetworkView, GraphView> viewMap;

	public PresentationFactoryImpl(CyDataTableFactory dataTableFactory, 
	                            CyRootNetworkFactory rootNetworkFactory,
								UndoSupport undo, SpacialIndex2DFactory spacialFactory,
								VisualPropertyCatalog vpc) {
		this.dataTableFactory = dataTableFactory;
		this.rootNetworkFactory = rootNetworkFactory;
		this.spacialFactory = spacialFactory;
		this.undo = undo;
		this.vpc = vpc;

		viewMap = new HashMap<CyNetworkView, GraphView>();
	}

	public void addPresentation(Object frame, CyNetworkView view) {
		if ( view == null )
			throw new NullPointerException("CyNetworkView is null");
		if ( frame instanceof JInternalFrame ) {
			JInternalFrame inFrame = (JInternalFrame)frame;
			JDesktopPane desktopPane = inFrame.getDesktopPane();

			DGraphView dgv = new DGraphView(view,dataTableFactory,rootNetworkFactory,undo,spacialFactory,vpc);
			viewMap.put(view, dgv);

			// TODO - not sure this layered pane bit is optimal
			inFrame.setContentPane( dgv.getContainer(inFrame.getLayeredPane()) );
			dgv.addTransferComponent(desktopPane);

			view.addViewChangeListener(dgv);

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
		GraphView gv = viewMap.get(nvce.getNetworkView());
		if ( gv != null )
			gv.updateView();
	}

	public GraphView getGraphView(CyNetworkView cnv) {
		return viewMap.get(cnv);
	}
}
