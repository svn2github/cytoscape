package org.cytoscape.BipartiteVisualiserPlugin;

import giny.view.EdgeView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cytoscape.BipartiteVisualiserPlugin.duallayout.LayoutTask;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

public class CreateBipartiteViewAction implements ActionListener {

	private final EdgeView edgeView;

	private final CyNetwork parentNetwork;
	private final CyNetwork network1;
	private final CyNetwork network2;

	public CreateBipartiteViewAction(final EdgeView edgeView,
			final CyNetwork parentNetwork, final CyNetwork network1,
			final CyNetwork network2) {
		this.edgeView = edgeView;

		this.parentNetwork = parentNetwork;
		this.network1 = network1;
		this.network2 = network2;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("====== Do dual Layout for "
				+ edgeView.getEdge().getIdentifier());

		// Start layout engine

		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.displayTimeElapsed(true);
		jTaskConfig.displayTimeRemaining(false);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.setModal(true);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		//TaskManager.executeTask(new LayoutTask(), jTaskConfig);
	}

}
