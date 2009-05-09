package org.cytoscape.view.vizmap.gui.internal.action;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.DefaultViewPanel;
import org.cytoscape.view.vizmap.gui.VizMapGUI;

public class CreateNewStyleAction extends AbstractVizMapperAction {

	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = 3359340478989439229L;

	public CreateNewStyleAction(VizMapGUI vizMapGUI) {
		super();
	}

	public void actionPerformed(ActionEvent e) {
		final String title = vizMapperUtil.getStyleName(vizMapperMainPanel,
				null);

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
		this.vizMapperMainPanel.setSelectedVisualStyle(newStyle);
		vmm.setVisualStyle(newStyle, currentView);

		final Component defPanel = defViewEditor.getDefaultView(newStyle);
		final CyNetworkView view = (CyNetworkView) ((DefaultViewPanel) defPanel)
				.getView();
		final Dimension panelSize = vizMapperMainPanel.getDefaultPanel()
				.getSize();

		if (view != null) {
			System.out.println("Creating Default Image for new visual style "
					+ title);
			vizMapperMainPanel.updateDefaultImage(newStyle, view, panelSize);
			vizMapperMainPanel.setDefaultViewImagePanel(vizMapperMainPanel
					.getDefaultImageManager().get(newStyle));
		}

		// vmm.setNetworkView(currentView);
		vizMapperMainPanel.switchVS(newStyle);
	}
}
