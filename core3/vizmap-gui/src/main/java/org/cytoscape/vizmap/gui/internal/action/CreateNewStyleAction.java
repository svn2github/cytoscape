package org.cytoscape.vizmap.gui.internal.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.VisualStyle;
import org.cytoscape.vizmap.calculators.Calculator;
import org.cytoscape.vizmap.gui.DefaultViewPanel;

import cytoscape.Cytoscape;

public class CreateNewStyleAction extends AbstractVizMapperAction {

	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = 3359340478989439229L;

	public CreateNewStyleAction() {
		super();
	}

	public void actionPerformed(ActionEvent e) {
		final String name = vizMapperUtil
				.getStyleName(vizMapperMainPanel, null);

		/*
		 * If name is null, do not create style.
		 */
		if (name == null)
			return;

		// Create the new style
		final VisualStyle newStyle = new VisualStyle(name);
		final List<Calculator> calcs = new ArrayList<Calculator>(vmm
				.getCalculatorCatalog().getCalculators());
		final Calculator dummy = calcs.get(0);
		newStyle.getNodeAppearanceCalculator().setCalculator(dummy);

		final GraphView currentView = cyNetworkManager.getCurrentNetworkView();

		// add it to the catalog
		vmm.getCalculatorCatalog().addVisualStyle(newStyle);
		// Apply the new style
		vmm.setVisualStyle(newStyle);

		vmm.setVisualStyleForView(currentView, newStyle);

		vizMapperMainPanel.getPropertyChangeSupport().firePropertyChange(
				"REMOVE_MAPPING", null, dummy.getVisualProperty());
		// vizMapperMainPanel.removeMapping(dummy.getVisualProperty());

		final JPanel defPanel = defViewEditor.getDefaultView(name);
		final GraphView view = (GraphView) ((DefaultViewPanel) defPanel)
				.getView();
		final Dimension panelSize = vizMapperMainPanel.getDefaultPanel()
				.getSize();

		if (view != null) {
			System.out.println("Creating Default Image for new visual style "
					+ name);
			vizMapperMainPanel.updateDefaultImage(name, view, panelSize);
			vizMapperMainPanel.setDefaultViewImagePanel(vizMapperMainPanel
					.getDefaultImageManager().get(name));
		}

		//vmm.setNetworkView(currentView);
		vizMapperMainPanel.switchVS(name);
	}
}
