package org.cytoscape.view.vizmap.gui.internal.action;

import java.awt.event.ActionEvent;

public class CreateLegendAction extends AbstractVizMapperAction {

	public CreateLegendAction() {
		super();
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 707566797144402515L;

	public void actionPerformed(ActionEvent e) {

		LegendDialog ld = new LegendDialog(menuItem, vizMapperMainPanel
				.getSelectedVisualStyle());
		ld.setLocationRelativeTo(menuItem);
		ld.setVisible(true);
	}
}
