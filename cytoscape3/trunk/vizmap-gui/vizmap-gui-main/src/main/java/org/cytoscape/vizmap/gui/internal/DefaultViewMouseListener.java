package org.cytoscape.vizmap.gui.internal;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.GraphView;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.gui.DefaultViewEditor;

public class DefaultViewMouseListener extends MouseAdapter {

	private VisualMappingManager vmm;
	private VizMapperMainPanel vizMapperMainPanel;
	private DefaultViewEditor defViewEditor;

	public DefaultViewMouseListener(VisualMappingManager vmm,
			VizMapperMainPanel panel, DefaultViewEditor defViewEditor) {
		this.vmm = vmm;
		this.vizMapperMainPanel = panel;
		this.defViewEditor = defViewEditor;
	}

	public void mouseClicked(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			final CyNetwork net = vmm.getNetwork();

			if (net == null)
				return;

			final String targetName = vmm.getVisualStyle().getName();
			final Long focus = net.getSUID();

			final DefaultViewPanel panel = (DefaultViewPanel) defViewEditor
					.showEditor(null);
			vizMapperMainPanel.updateDefaultImage(targetName, (GraphView) panel.getView(),
					vizMapperMainPanel.getDefaultViewPanel().getSize());
			vizMapperMainPanel.setDefaultViewImagePanel(vizMapperMainPanel.getDefaultImageManager().get(targetName));

			vmm.setNetworkView(vizMapperMainPanel.getTargetView());
			vmm.setVisualStyle(targetName);

			//cytoscapeDesktop.setFocus(focus);
			// cytoscapeDesktop.repaint();
		}
	}
}