// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2009 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package bioCycPlugin.webservices;

import bioCycPlugin.BioCycPlugin;
import bioCycPlugin.commands.QueryHandler;
import bioCycPlugin.model.Database;
import bioCycPlugin.model.Pathway;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.data.webservice.CyWebServiceEvent;
import cytoscape.data.webservice.CyWebServiceEvent.WSEventType;
import cytoscape.data.webservice.CyWebServiceEventListener;
import cytoscape.data.webservice.CyWebServiceException;
import cytoscape.data.webservice.NetworkImportWebServiceClient;
import cytoscape.data.webservice.WebServiceClientImplWithGUI;
import cytoscape.data.webservice.WebServiceClientManager;
import cytoscape.data.webservice.WebServiceClientManager.ClientType;
import cytoscape.logger.CyLogger;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.ModulePropertiesImpl;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * WebserviceClient implementation, for accessing the
 * BioCyc webservice in a standard way from within Cytoscape.
 */
public class BioCycClient extends WebServiceClientImplWithGUI<BioCycRESTClient, BioCycClientGui> implements NetworkImportWebServiceClient {
	private static final String DISPLAY_NAME = "BioCyc Web Service Client";
	private static final String CLIENT_ID = "biocyc";

	private CyLogger logger;

	private BioCycRESTClient stub;

	public BioCycClient(CyLogger logger) {
		super(CLIENT_ID, DISPLAY_NAME,
				new ClientType[] { ClientType.NETWORK },
				null, null, null
		);
		this.logger = logger;
		getStub();
		setGUI(new BioCycClientGui(this, logger));
		props = new ModulePropertiesImpl(CLIENT_ID, "biocyc");
	}

	private String prevURL = null;

	public BioCycRESTClient getStub() {
		String urlString = BioCycPlugin.getBaseUrl();
		if(stub == null || prevURL == null || !prevURL.equals(urlString)) {
			stub = new BioCycRESTClient(urlString, logger);
			setClientStub(stub);
			prevURL = urlString;

			if(gui != null) gui.resetDatabases();
		}
		return stub;
	}

	public String getDescription() {
		String description = "<html><body><b>BioCyc</b> is one of several repositories using the Pathway Tools software (";
		description += "<a href=\"http://bioinformatics.ai.sri.com/ptools\">http://bioinformatics.ai.sri.com/ptools</a>) ";
		description += "for pathway information on a variety of species.  This plugin supports any of the repositories ";
		description += "running Pathway Tools release <b>14.5</b> or later</body></html>";
		return description;
	}

	/**
	 * Check if a working connection to the wikipathways
	 * web service is available.
	 */
	public boolean isConnected() {
		try {
			//Try to list databases, if fails then we're probably
			//not connected
			listDatabases();
			return true;
		} catch (RemoteException e) {
			return false;
		}
	}

	public void executeService(CyWebServiceEvent e)
	throws CyWebServiceException {
		if(CLIENT_ID.equals(e.getSource())) {
			switch(e.getEventType()) {
			case IMPORT_NETWORK:
				logger.info("Importing " + e.getParameter());
				GetPathwayParameters parms = (GetPathwayParameters)e.getParameter();
				getStub().loadNetwork(parms.id, parms.db);
				break;
			case SEARCH_DATABASE:
				logger.info("Searching " + e.getParameter());
				search((FindPathwaysByTextParameters)e.getParameter());
				break;
			}
		}
	}

	private void search(FindPathwaysByTextParameters request) throws CyWebServiceException {
		SearchTask task = new SearchTask(request);
		JTaskConfig config = new JTaskConfig();
		config.displayCancelButton(false);
		config.setModal(true);
		TaskManager.executeTask(task, config);
	}

	protected List<Database> listDatabases() throws RemoteException {
		return getStub().listDatabases();
	}

	public VisualStyle getDefaultVisualStyle() {
		return null; //TODO
	}

	public static class FindPathwaysByTextParameters {
		public String query;
		public String db = null;
	}

	public static class GetPathwayParameters {
		public String id;
		public String db = null;
	}

	class SearchTask implements Task, CyWebServiceEventListener {

		FindPathwaysByTextParameters query;
		TaskMonitor monitor;

		public SearchTask(FindPathwaysByTextParameters query) {
			this.query = query;
			WebServiceClientManager.getCyWebServiceEventSupport()
			.addCyWebServiceEventListener(this);
		}

		public String getTitle() {
			return "Searching...";
		}

		public void run() {
			try {
				List<Pathway> result = getStub().findPathwaysByText(query.query, query.db);
				gui.setResults(result);
				if(result == null || result.size() == 0) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							JOptionPane.showMessageDialog(
									gui, "The search didn't return any results",
									"No results", JOptionPane.INFORMATION_MESSAGE
							);
						}
					});

				}
			} catch (final Exception e) {
				logger.error("Error while searching", e);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						JOptionPane.showMessageDialog(
								gui, "Error: " + e.getMessage() + ". See log for details",
								"Error", JOptionPane.ERROR_MESSAGE
						);
					}
				});
			}
		}

		public void halt() {
		}

		public void setTaskMonitor(TaskMonitor m)
		throws IllegalThreadStateException {
			this.monitor = m;
		}

		public void executeService(CyWebServiceEvent event)
		throws CyWebServiceException {
		}
	}

}
