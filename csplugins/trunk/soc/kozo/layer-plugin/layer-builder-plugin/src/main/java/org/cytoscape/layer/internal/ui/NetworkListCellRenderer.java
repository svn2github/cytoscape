package org.cytoscape.layer.internal.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyNetworkManager;

public class NetworkListCellRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = 2917055467421736292L;

	private CyNetworkManager manager;
	private static final String NETWORK_TITLE = "name";

	public NetworkListCellRenderer(CyNetworkManager manager) {
		super();
		this.manager = manager;
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		String networkName = "Undefined";

		if (value != null && value instanceof Long) {
			CyNetwork network = manager.getNetwork((Long) value);
			networkName = network.attrs().get(NETWORK_TITLE, String.class);
		}

		this.setText(networkName);

		return this;
	}

}
