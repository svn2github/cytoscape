package org.cytoscape.view.ui.networkpanel.internal;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;

import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupChangeListener;

public class NetworkTreeTableModel extends DefaultTreeTableModel implements
		CyGroupChangeListener {

	public NetworkTreeTableModel() {

	}

	public void groupChanged(CyGroup group, ChangeType change) {
		if (change == CyGroupChangeListener.ChangeType.GROUP_CREATED) {
			groupCreated(group);

		} else if (change == CyGroupChangeListener.ChangeType.GROUP_DELETED) {
			groupRemoved(group);
		} else if (change == CyGroupChangeListener.ChangeType.GROUP_MODIFIED) {
			groupChanged(group);
		} else {
			System.err.println("unsupported change type: " + change);
		}
	}

	private void groupCreated(CyGroup group) {

	}

	private void groupChanged(CyGroup group) {
		// TODO Auto-generated method stub

	}

	private void groupRemoved(CyGroup group) {
		// TODO Auto-generated method stub

	}

}
