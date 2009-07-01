/*
 File: PopupActionListener.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape.internal.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskManager;

import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.CyNetworkNaming;
import cytoscape.xtask.CreateNetworkPresentationTaskFactory;

/**
 * This class listens for actions from the popup menu, it is responsible for
 * performing actions related to destroying and creating views, and destroying
 * the network.
 */
class PopupActionListener implements ActionListener {
	/**
	 * Constants for JMenuItem labels
	 */
	public static final String DESTROY_VIEW = "Destroy View";

	/**
	 *
	 */
	public static final String CREATE_VIEW = "Create View";

	public static final String CREATE_VIEW_BY = "Create View by...";

	/**
	 *
	 */
	public static final String DESTROY_NETWORK = "Destroy Network";

	/**
	 *
	 */
	public static final String EDIT_TITLE = "Edit Network Title";

	/**
	 * This is the network which originated the mouse-click event (more
	 * appropriately, the network associated with the ID associated with the row
	 * associated with the JTable that originated the popup event
	 */
	protected CyNetwork cyNetwork;
	private NetworkPanel panel;
	private CyNetworkManager netmgr;
	private TaskManager taskManager;
	private CreateNetworkPresentationTaskFactory viewFactory;
	private CyNetworkNaming naming;

	private Map<String, Task> actionMap;
	
	public PopupActionListener(NetworkPanel panel, CyNetworkManager netmgr,
			TaskManager taskManager, CreateNetworkPresentationTaskFactory viewFactory,
			CyNetworkNaming naming) {
		this.panel = panel;
		this.netmgr = netmgr;
		this.taskManager = taskManager;
		this.viewFactory = viewFactory;
		this.naming = naming;
		
		actionMap = new HashMap<String, Task>();
	}
	
	private void createActionMap() {
		//TODO: replace the following if statement.
	}

	/**
	 * Based on the action event, destroy or create a view, or destroy a network
	 */
	public void actionPerformed(ActionEvent ae) {
		final String label = ((JMenuItem) ae.getSource()).getText();

		// Figure out the appropriate action
		if (label == DESTROY_VIEW) {
			long vid = cyNetwork.getSUID();
			if (netmgr.viewExists(vid))
				netmgr.destroyNetworkView(netmgr.getNetworkView(vid));
		} else if (label == CREATE_VIEW) {
			Task t = viewFactory.getCreateNetworkPresentationTask(cyNetwork);
			taskManager.execute(t);
		} else if (label == DESTROY_NETWORK) {
			netmgr.destroyNetwork(cyNetwork);
		} else if (label == EDIT_TITLE) {
			JOptionPane.showMessageDialog(panel, "Changing names is not yet supported - we need to make these actions TaskFactories!", "ERROR", JOptionPane.ERROR_MESSAGE);	
			//naming.editNetworkTitle(cyNetwork, panel, netmgr);
			//panel.updateTitle(cyNetwork);
			// TODO we might consider firing an event here to let others know
			// of the title change.
		} else {
			// throw an exception here?
			System.err.println("Unexpected network panel popup option");
		}
	}

	/**
	 * Right before the popup menu is displayed, this function is called so we
	 * know which network the user is clicking on to call for the popup menu
	 */
	public void setActiveNetwork(final CyNetwork cyNetwork) {
		this.cyNetwork = cyNetwork;
	}
	
	
}
