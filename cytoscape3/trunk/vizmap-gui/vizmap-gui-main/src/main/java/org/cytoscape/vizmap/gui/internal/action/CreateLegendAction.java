package org.cytoscape.vizmap.gui.internal.action;

import java.awt.event.ActionEvent;


import cytoscape.util.SwingWorker;

public class CreateLegendAction extends AbstractVizMapperAction {

	public CreateLegendAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 707566797144402515L;

	public void actionPerformed(ActionEvent e) {
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				LegendDialog ld = new LegendDialog(menuItem,
						vmm.getVisualStyle());
				ld.setLocationRelativeTo(menuItem);
				ld.setVisible(true);

				return null;
			}
		};

		worker.start();
	}
}
