
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.data.webservice.util;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.webservice.*;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyDataTableUtil;
import org.cytoscape.layout.CyLayouts;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;
import java.util.List;


/**
 *
 */
public class NetworkExpansionMenu implements PropertyChangeListener {
	// Default layout algorithm name in property.
	private static final String LAYOUT_PROP = "expanderDefaultLayout";
	private static final String DEF_LAYOUT = "force-directed";
	private static NetworkExpansionMenu expander;

	static {
		expander = new NetworkExpansionMenu();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param client DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static JMenuItem getExpander(WebServiceClient client) {
		return expander.getMenuItem(client);
	}

	private String defLayout;

	/**
	 * Creates a new NetworkExpansionMenu object.
	 */
	public NetworkExpansionMenu() {
		// Listening to event from core.
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);

		// Set layout algorithm
		defLayout = CytoscapeInit.getProperties().getProperty(LAYOUT_PROP);

		if (defLayout == null)
			defLayout = DEF_LAYOUT;
	}

	private JMenuItem getMenuItem(final WebServiceClient client) {
		final JMenuItem expandMenu = new JMenuItem(new AbstractAction("Get neighbours by ID(s)") {
	private final static long serialVersionUID = 1213748837134204L;
				public void actionPerformed(ActionEvent e) {
					System.out.println("Start expanding network: " + e.getActionCommand());

					final CyWebServiceEvent evt = new CyWebServiceEvent(client.getClientID(),
					                                                    WSEventType.SEARCH_DATABASE,
					                                                    buildStringQuery(),
					                                                    WSEventType.EXPAND_NETWORK);

					SearchTask task = new SearchTask(evt);

					// Configure JTask Dialog Pop-Up Box
					final JTaskConfig jTaskConfig = new JTaskConfig();
					jTaskConfig.setOwner(Cytoscape.getDesktop());
					jTaskConfig.displayCloseButton(true);
					jTaskConfig.displayStatus(true);
					jTaskConfig.setAutoDispose(true);

					// Execute Task in New Thread; pops open JTask Dialog Box.
					TaskManager.executeTask(task, jTaskConfig);
				}
			});

		return expandMenu;
	}

	private String buildStringQuery() {
		final StringBuilder builder = new StringBuilder();
		final List<CyNode> selectedNodes = CyDataTableUtil.getNodesInState(Cytoscape.getCurrentNetwork(),"selected",true);

		for (CyNode node : selectedNodes) {
			builder.append(node.attrs().get("name",String.class) + " ");
		}

		return builder.toString();
	}

	/**
	 *  Catch result from the service.
	 *
	 * @param evt DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		Object resultObj = evt.getNewValue();

		if (evt.getPropertyName().equals(CyWebServiceEvent.WSResponseType.SEARCH_FINISHED.toString())
		    && ((DatabaseSearchResult) resultObj).getNextMove().equals(WSEventType.EXPAND_NETWORK)) {
			System.out.println("Search result from " + evt.getSource() + ", Number of result = "
			                   + evt.getNewValue() + ", Source name = " + evt.getOldValue());

			String[] message = {
			                       ((DatabaseSearchResult) resultObj).getResultSize()
			                       + " interactions found.",
			                       
			"Do you want to add new nodes and edges to " + Cytoscape.getCurrentNetwork().attrs().get("title",String.class)
			                       + "?"
			                   };
			int value = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), message,
			                                          "Expand network", JOptionPane.YES_NO_OPTION);

			if (value == JOptionPane.YES_OPTION) {
				CyWebServiceEvent evt2 = new CyWebServiceEvent(evt.getOldValue().toString(),
				                                               WSEventType.EXPAND_NETWORK,
				                                               ((DatabaseSearchResult) resultObj)
				                                                                                                                                                                  .getResult());

				try {
					WebServiceClientManager.getCyWebServiceEventSupport().fireCyWebServiceEvent(evt2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (evt.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED)
		           && evt.getSource() instanceof NetworkImportWebServiceClient) {
			String[] message = { "Neighbours loaded.", "Do you want to layout the network now?" };
			int value = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), message,
			                                          "Expansion complete",
			                                          JOptionPane.YES_NO_OPTION);

			if (value == JOptionPane.YES_OPTION) {
				CyLayouts.getLayout(defLayout).doLayout(Cytoscape.getCurrentNetworkView());
			}
		} else if (evt.getPropertyName().equals(Cytoscape.PREFERENCES_UPDATED)) {
			defLayout = CytoscapeInit.getProperties().getProperty("expanderDefaultLayout");

			if (defLayout == null) {
				defLayout = "force-directed";
			}
		}
	}

	class SearchTask implements Task {
		private CyWebServiceEvent evt;
		private TaskMonitor taskMonitor;

		public SearchTask(CyWebServiceEvent evt) {
			this.evt = evt;
		}

		public String getTitle() {
			// TODO Auto-generated method stub
			return "Expanding Network";
		}

		public void halt() {
			// TODO Auto-generated method stub
		}

		public void run() {
			taskMonitor.setStatus("Loading neighbours...");
			taskMonitor.setPercentCompleted(-1);

			// this even will load the file
			try {
				WebServiceClientManager.getCyWebServiceEventSupport().fireCyWebServiceEvent(evt);
			} catch (Exception e) {
				taskMonitor.setException(e, "Failed to load neighbours.");

				return;
			}

			taskMonitor.setPercentCompleted(100);

			Cytoscape.getDesktop().setFocus(Cytoscape.getCurrentNetwork().getSUID());

			long curNetID = Cytoscape.getCurrentNetwork().getSUID();

			Cytoscape.getVisualMappingManager()
			         .setVisualStyleForView(Cytoscape.getNetworkView(curNetID),
			                                Cytoscape.getVisualMappingManager().getVisualStyle());
			Cytoscape.redrawGraph(Cytoscape.getNetworkView(curNetID));
		}

		public void setTaskMonitor(TaskMonitor arg0) throws IllegalThreadStateException {
			this.taskMonitor = arg0;
		}
	}
}
