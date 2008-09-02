package cytoscape.data.webservice.util;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.logger.CyLogger;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.DatabaseSearchResult;
import cytoscape.data.webservice.NetworkImportWebServiceClient;
import cytoscape.data.webservice.WebServiceClient;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.layout.CyLayouts;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

public class NetworkExpansionMenu implements PropertyChangeListener {
	
	// Default layout algorithm name in property.
	private static final String LAYOUT_PROP = "expanderDefaultLayout";
	private static final String DEF_LAYOUT = "force-directed";
	
	private static NetworkExpansionMenu expander;
	
	static {
		expander = new NetworkExpansionMenu();
	}
	
	public static JMenuItem getExpander(WebServiceClient client) {
		return expander.getMenuItem(client);
		
	}

	private String defLayout;
	
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
				public void actionPerformed(ActionEvent e) {
					CyLogger.getLogger().info("Start expanding network: " + e.getActionCommand());

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
		final Set<Node> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();

		for (Node node : selectedNodes) {
			builder.append(node.getIdentifier() + " ");
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
			CyLogger.getLogger().info("Search result from " + evt.getSource() + ", Number of result = "
			                   + evt.getNewValue() + ", Source name = " + evt.getOldValue());

			String[] message = {
			                       ((DatabaseSearchResult) resultObj).getResultSize()
			                       + " interactions found.",
			                       
			"Do you want to add new nodes and edges to " + Cytoscape.getCurrentNetwork().getTitle()
			                       + "?"
			                   };
			int value = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), message,
			                                          "Expand network", JOptionPane.YES_NO_OPTION);

			if (value == JOptionPane.YES_OPTION) {
				
				CyWebServiceEvent evt2 = new CyWebServiceEvent(evt.getOldValue().toString(),
				                                               WSEventType.EXPAND_NETWORK,
				                                               ((DatabaseSearchResult) resultObj).getResult());

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
				CyLayouts.getLayout(defLayout).doLayout();
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

			Cytoscape.getDesktop().setFocus(Cytoscape.getCurrentNetwork().getIdentifier());

			String curNetID = Cytoscape.getCurrentNetwork().getIdentifier();

			Cytoscape.getNetworkView(curNetID)
			         .setVisualStyle(Cytoscape.getVisualMappingManager().getVisualStyle().getName());
			Cytoscape.getNetworkView(curNetID).redrawGraph(false, true);
		}

		public void setTaskMonitor(TaskMonitor arg0) throws IllegalThreadStateException {
			this.taskMonitor = arg0;
		}
	}
}
