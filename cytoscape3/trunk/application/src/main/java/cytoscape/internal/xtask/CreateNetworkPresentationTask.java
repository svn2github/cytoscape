/*
 File: CreateNetworkViewTask.java

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
package cytoscape.internal.xtask;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

import cytoscape.CyNetworkManager;

public class CreateNetworkPresentationTask implements Task {

	private final CyNetwork network;
	private final CyNetworkManager netmgr;
	private final CyNetworkViewFactory gvf;

	public CreateNetworkPresentationTask(CyNetwork network,
			CyNetworkViewFactory gvf, CyNetworkManager netmgr) {
		this.network = network;
		this.gvf = gvf;
		this.netmgr = netmgr;
	}

	public void run(TaskMonitor taskMonitor) throws Exception {
		taskMonitor.setStatusMessage("Creating network view ...");
		taskMonitor.setProgress(-1.0);

		try {
			CyNetworkView view = gvf.getNetworkViewFor(network);
			netmgr.addNetworkView(view);
		} catch (Exception e) {
			throw new Exception("Could not create network view for network: "
					+ network.attrs().get("name", String.class), e);
		}

		taskMonitor.setProgress(1.0);
		taskMonitor.setStatusMessage("Network view successfully create for:  "
				+ network.attrs().get("name", String.class));
	}

	public void cancel() {
	}
}
