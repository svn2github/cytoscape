package org.cytoscape.layer.internal.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyNetworkManager;

public class NetworkNameListCellRenderer extends JLabel implements
		ListCellRenderer {

	private CyNetworkManager manager;
	private static final String NETWORK_TITLE = "name";

	public NetworkNameListCellRenderer(CyNetworkManager manager) {
		this.manager = manager;
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		String networkName = "?";
		if (value != null && value instanceof Long) {
			CyNetwork network = manager.getNetwork((Long) value);
			networkName = network.attrs().get(NETWORK_TITLE, String.class);
			this.setText(networkName);
		} else if (value == null) {
			this.setText("Undefined");
		}
		return this;
	}
}
