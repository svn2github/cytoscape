
package org.cytoscape.ding.impl;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyDataTableFactory;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.VisualPropertyCatalog;
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

public class PresentationFactoryImpl implements PresentationFactory {

	private CyDataTableFactory dataTableFactory;
	private CyRootNetworkFactory rootNetworkFactory;
	private SpacialIndex2DFactory spacialFactory;
	private UndoSupport undo;
	private NetworkViewChangedListenerImpl nvcli;
	private VisualPropertyCatalog vpc;

	public PresentationFactoryImpl(CyDataTableFactory dataTableFactory, 
	                            CyRootNetworkFactory rootNetworkFactory,
								UndoSupport undo, SpacialIndex2DFactory spacialFactory,
								NetworkViewChangedListenerImpl nvcli,
								VisualPropertyCatalog vpc) {
		this.dataTableFactory = dataTableFactory;
		this.rootNetworkFactory = rootNetworkFactory;
		this.spacialFactory = spacialFactory;
		this.undo = undo;
		this.nvcli = nvcli;
		this.vpc = vpc;
	}

	public void addPresentation(Object frame, CyNetworkView view) {
		if ( view == null )
			throw new NullPointerException("CyNetworkView is null");
		if ( frame instanceof JInternalFrame ) {
			JInternalFrame inFrame = (JInternalFrame)frame;
			JDesktopPane desktopPane = inFrame.getDesktopPane();

			DGraphView dgv = new DGraphView(view,dataTableFactory,rootNetworkFactory,undo,spacialFactory,vpc);
			nvcli.addGraphView( view, dgv );

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

		BirdsEyeView bev = new BirdsEyeView((Component)navBounds);	
		target.add( bev );

		return bev;
	}
}
