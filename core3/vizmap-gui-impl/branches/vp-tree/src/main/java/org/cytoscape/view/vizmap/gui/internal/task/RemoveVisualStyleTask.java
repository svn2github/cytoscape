package org.cytoscape.view.vizmap.gui.internal.task;

import javax.swing.JOptionPane;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.view.vizmap.gui.internal.VizMapPropertySheetBuilder;
import org.cytoscape.view.vizmap.gui.internal.VizMapperMainPanel;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class RemoveVisualStyleTask extends AbstractTask {

	private final VisualMappingManager vmm;
	private final VizMapperMainPanel vizMapperMainPanel;
	private final CyNetworkManager cyNetworkManager;
	private final VizMapPropertySheetBuilder vizMapPropertySheetBuilder;

	public RemoveVisualStyleTask(final VisualMappingManager vmm,
			final VizMapperMainPanel vizMapperMainPanel,
			final CyNetworkManager cyNetworkManager,
			final VizMapPropertySheetBuilder vizMapPropertySheetBuilder) {
		this.vmm = vmm;
		this.vizMapperMainPanel = vizMapperMainPanel;
		this.cyNetworkManager = cyNetworkManager;
		this.vizMapPropertySheetBuilder = vizMapPropertySheetBuilder;
	}

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {

		final VisualStyle currentStyle = this.vizMapperMainPanel
				.getSelectedVisualStyle();

		if (currentStyle.equals(vizMapperMainPanel.getDefaultVisualStyle())) {
			JOptionPane.showMessageDialog(vizMapperMainPanel,
					"You cannot delete default style.",
					"Cannot remove defalut style!", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// make sure the user really wants to do this
		final String styleName = currentStyle.getTitle();
		final String checkString = "Are you sure you want to permanently delete"
				+ " the visual style '" + styleName + "'?";
		int ich = JOptionPane.showConfirmDialog(vizMapperMainPanel,
				checkString, "Confirm Delete Style", JOptionPane.YES_NO_OPTION);

		if (ich == JOptionPane.YES_OPTION) {

			vmm.removeVisualStyle(currentStyle);
			vizMapperMainPanel.getDefaultImageManager().remove(currentStyle);
			vizMapPropertySheetBuilder.getPropertyMap().remove(currentStyle);
			
			// Switch to the default style
			final VisualStyle defaultStyle = vizMapperMainPanel.getDefaultVisualStyle();

			vizMapperMainPanel.switchVS(defaultStyle);
			// Apply to the current view
			final CyNetworkView view = cyNetworkManager.getCurrentNetworkView();
			if (view != null)
				vmm.setVisualStyle(defaultStyle, view);
		}

	}

}
