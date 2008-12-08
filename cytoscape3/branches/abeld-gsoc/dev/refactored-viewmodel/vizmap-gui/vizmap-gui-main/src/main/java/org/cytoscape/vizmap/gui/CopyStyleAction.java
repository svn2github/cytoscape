package org.cytoscape.vizmap.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JPanel;

import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.VisualStyle;

import cytoscape.Cytoscape;

public class CopyStyleAction extends AbstractVizMapperAction {

	private static final long serialVersionUID = 1244735696944563618L;

	public void actionPerformed(ActionEvent e) {
		final VisualStyle currentStyle = visualMappingManager.getVisualStyle();
		VisualStyle clone = null;
		final GraphView targetView = Cytoscape.getCurrentNetworkView();

		try {
			clone = (VisualStyle) currentStyle.clone();
		} catch (CloneNotSupportedException exc) {
			System.err.println("Clone not supported exception!");
		}

		final String newName = vizMapperUtil.getStyleName(vizMapperMainPanel, clone);

		if ((newName == null) || (newName.trim().length() == 0)) {
			return;
		}

		clone.setName(newName);

		// add new style to the catalog
		visualMappingManager.getCalculatorCatalog().addVisualStyle(clone);
		visualMappingManager.setVisualStyle(clone);

		final JPanel defPanel = defAppBldr.getDefaultView(newName);
		final GraphView view = (GraphView) ((DefaultViewPanel) defPanel)
				.getView();
		final Dimension panelSize = vizMapperMainPanel.getDefaultPanel().getSize();

		if (view != null) {
			System.out.println("Creating Default Image for new visual style "
					+ newName);
			vizMapperMainPanel.updateDefaultImage(newName, view, panelSize);
			vizMapperMainPanel.setDefaultViewImagePanel(vizMapperMainPanel.getDefaultImageManager().get(newName));
		}

		visualMappingManager.setNetworkView(targetView);
		vizMapperMainPanel.switchVS(newName);
	}

}
