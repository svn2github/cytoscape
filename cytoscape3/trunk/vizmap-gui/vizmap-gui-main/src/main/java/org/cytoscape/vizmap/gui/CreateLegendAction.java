package org.cytoscape.vizmap.gui;

import java.awt.event.ActionEvent;

import cytoscape.util.SwingWorker;

public class CreateLegendAction extends AbstractVizMapperAction {

	private static final long serialVersionUID = 707566797144402515L;

	public void actionPerformed(ActionEvent e) {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				LegendDialog ld = new LegendDialog(menuItem,
						visualMappingManager.getVisualStyle());
				ld.setLocationRelativeTo(menuItem);
				ld.setVisible(true);

				return null;
			}
		};

		worker.start();
	}
}
