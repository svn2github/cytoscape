package org.cytoscape.view.vizmap.gui.internal.action;

import java.awt.event.ActionEvent;

import org.cytoscape.view.vizmap.VisualStyle;

public class RenameStyleAction extends AbstractVizMapperAction {

	private static final long serialVersionUID = -3823557783901332855L;

	public RenameStyleAction() {
		super();
	}

	public void actionPerformed(ActionEvent e) {

		final VisualStyle selectedStyle = this.vizMapperMainPanel.getSelectedVisualStyle();

		final String name = vizMapperUtil.getStyleName(vizMapperMainPanel,
				selectedStyle);

		// Ignore if user does not enter new name.
		if (name == null)
			return;

		vizMapperMainPanel.setLastVSName(name);

		// Rename the selected style
		selectedStyle.setTitle(name);
		
		// Update the combo box.
		vizMapperMainPanel.setSelectedVisualStyle(selectedStyle);

	}
}
