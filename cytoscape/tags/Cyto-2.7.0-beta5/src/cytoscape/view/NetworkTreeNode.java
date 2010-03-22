package cytoscape.view;

import javax.swing.tree.DefaultMutableTreeNode;

public class NetworkTreeNode extends DefaultMutableTreeNode {
	
	private static final long serialVersionUID = -1504239724666254584L;
	
	private String network_uid;

	public NetworkTreeNode(Object userobj, String id) {
		super(userobj.toString());
		network_uid = id;
	}

	protected void setNetworkID(String id) {
		network_uid = id;
	}

	protected String getNetworkID() {
		return network_uid;
	}
}