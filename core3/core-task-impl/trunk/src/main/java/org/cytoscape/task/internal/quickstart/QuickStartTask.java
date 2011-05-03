/*
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

package org.cytoscape.task.internal.quickstart;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.task.internal.quickstart.subnetworkbuilder.SubnetworkBuilderUtil;
import org.cytoscape.task.internal.quickstart.subnetworkbuilder.SubnetworkBuilderTask;

public class QuickStartTask extends AbstractTask {

	private static final String LOAD_NETWORK = "Load a Network";
	private static final String LOAD_TABLE = "Load an Attribute Table";
	private static final String LOAD_SUBNETWORK = "Create subnetwork from interactome";
	
	// @Tunable(description="What would you like to do today?\nCytoscape requires at the minimum a network to do anything.  A network and attributes makes life much better.  If you don't have a network, that's OK, we'll help you find one.")
	@Tunable(description = "What would you like to do today?")
	public ListSingleSelection<String> selection = new ListSingleSelection<String>(
			LOAD_NETWORK, LOAD_TABLE, LOAD_SUBNETWORK);

	private final QuickStartState state;
	private final ImportTaskUtil importTaskUtil;
	
	private final CyNetworkManager networkManager;
	private final SubnetworkBuilderUtil subnetworkUtil;
	
	
	public QuickStartTask(final QuickStartState state, final ImportTaskUtil importTaskUtil, 
			final CyNetworkManager networkManager, final SubnetworkBuilderUtil subnetworkUtil) {
		super();
		this.state = state;
		this.importTaskUtil = importTaskUtil;
		this.networkManager = networkManager;
		this.subnetworkUtil = subnetworkUtil;
	}

	public void run(TaskMonitor e) {
		String selected = selection.getSelectedValue();
		if (selected == LOAD_NETWORK) {
			insertTasksAfterCurrentTask(new LoadNetworkTask(state, importTaskUtil));
		} else if (selected == LOAD_TABLE)
			insertTasksAfterCurrentTask(new LoadTableTask(state, importTaskUtil));
		 else if (selected == LOAD_SUBNETWORK) {
			 insertTasksAfterCurrentTask(new SubnetworkBuilderTask(networkManager, subnetworkUtil));	 
		}
	}
}
