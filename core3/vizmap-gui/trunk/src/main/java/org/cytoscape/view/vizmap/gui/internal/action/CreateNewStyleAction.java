package org.cytoscape.view.vizmap.gui.internal.action;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.VizMapGUI;
import org.cytoscape.view.vizmap.gui.internal.util.VizMapperUtil;

import cytoscape.CyNetworkManager;

public class CreateNewStyleAction extends AbstractVizMapperAction {

	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = 3359340478989439229L;

	private VizMapperUtil vizMapperUtil;

	
	public CreateNewStyleAction(VisualMappingManager vmm, VizMapperUtil vizMapperUtil, CyNetworkManager cyNetworkManager) {
		super();
		this.vizMapperUtil = vizMapperUtil;
		this.vmm = vmm;
		this.cyNetworkManager = cyNetworkManager;
	}

	public void actionPerformed(ActionEvent e) {
		final String title = vizMapperUtil.getStyleName(null,
				null);
//FIXME
		/*
		 * If name is null, do not create style.
		 */
		if (title == null)
			return;

		// Create the new style
		final VisualStyle newStyle = vmm.createVisualStyle(title);
		final CyNetworkView currentView = cyNetworkManager
				.getCurrentNetworkView();
		
		// Set selected style
//		this.vizMapperMainPanel.setSelectedVisualStyle(newStyle);
		if(currentView != null)
			vmm.setVisualStyle(newStyle, currentView);

//		final Component defPanel = defViewEditor.getDefaultView(newStyle);
//		final CyNetworkView view = (CyNetworkView) ((DefaultViewPanel) defPanel)
//				.getView();
//		final Dimension panelSize = vizMapperMainPanel.getDefaultPanel()
//				.getSize();
//
//		if (view != null) {
//			System.out.println("Creating Default Image for new visual style "
//					+ title);
//			vizMapperMainPanel.updateDefaultImage(newStyle, view, panelSize);
//			vizMapperMainPanel.setDefaultViewImagePanel(vizMapperMainPanel
//					.getDefaultImageManager().get(newStyle));
//		}
//
//		vizMapperMainPanel.switchVS(newStyle);
	}
}
