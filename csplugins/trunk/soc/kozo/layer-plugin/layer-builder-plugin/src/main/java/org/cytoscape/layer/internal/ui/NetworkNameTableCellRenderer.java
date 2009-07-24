package org.cytoscape.layer.internal.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyNetworkManager;

public class NetworkNameTableCellRenderer extends JLabel implements
		TableCellRenderer {

	private CyNetworkManager manager;
	private static final String NETWORK_TITLE = "name";

	public NetworkNameTableCellRenderer(CyNetworkManager manager) {
		this.manager = manager;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		String networkName = "?";

		if (value != null && value instanceof Long) {
			CyNetwork network = manager.getNetwork((Long) value);

			// Get title here!
			networkName = network.attrs().get(NETWORK_TITLE, String.class);

			this.setText(networkName);

		} else if(value == null) {
			this.setText("Undefined");
		}

		return this;
	}

}
