package org.cytoscape.layer.internal.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.session.CyNetworkManager;

/**
 * The Renderer class for JTable.
 * 
 * @author kozo
 * 
 */

public class NetworkTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -7451409548228446195L;

	private CyNetworkManager manager;
	private static final String NETWORK_TITLE = "name";

	public NetworkTableCellRenderer(CyNetworkManager manager) {
		super();
		this.manager = manager;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		String networkName = "Undefined";

		if (value != null && value instanceof Long) {
			CyNetwork network = manager.getNetwork((Long) value);
			networkName = network.attrs().get(NETWORK_TITLE, String.class);
			this.setText(networkName);
		}

		this.setText(networkName);

		return this;
	}
}
