package org.cytoscape.view.ui.networkpanel.internal;

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;

public class NetworkTreeNode extends AbstractMutableTreeTableNode implements
		NetworkBrowserTreeNode {

	private String networkID;

	private final Object[] data;

	public NetworkTreeNode(Object userobj, String id, int columnSize) {
		super(userobj.toString());
		networkID = id;
		data = new Object[columnSize];
	}

	public void setNetworkID(String id) {
		networkID = id;
	}

	public String getNetworkID() {
		return networkID;
	}

	public TreeObjectType getObjectType() {
		return TreeObjectType.NETWORK;
	}

	public int getColumnCount() {
		return data.length;
	}

	public Object getValueAt(int i) {
		if (i > data.length) {
			return null;
		} else
			return data[i];
	}

	public void setValueAt(Object value, int colIndex) {
		if (value != null && data.length > colIndex)
			data[colIndex] = value;
	}
}