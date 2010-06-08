/*
 File: SemanticSummaryManager.java

 Copyright 2010 - The Cytoscape Consortium (www.cytoscape.org)
 
 Code written by: Layla Oesper
 Authors: Layla Oesper, Ruth Isserlin, Daniele Merico
 
 This library is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public License
 along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package cytoscape.csplugins.semanticsummary;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

/**
 * The SemanticSummaryManager class is a singleton class that manages
 * all the parameters involved in using the Semantic Summary Plugin.
 * 
 * @author Layla Oesper
 * @version 1.0
 *
 */

public class SemanticSummaryManager implements PropertyChangeListener 
{
	//VARIABLES
	
	private static SemanticSummaryManager manager = null;
	
	private HashMap<String, SemanticSummaryParameters> cyNetworkList;
	
	//Create only one instance of the input and cloud panels
	private SemanticSummaryInputPanel inputWindow;
	private CloudDisplayPanel cloudWindow;
	
	//Keep track of current network and cloud
	private SemanticSummaryParameters curNetwork;
	private CloudParameters curCloud;
	
	//Default Values for User Input
	private Double defaultNetWeight;
	
	//CONSTRUCTOR
	/**
	 * This is a private constructor that is only called by the getInstance()
	 * method.
	 */
	private SemanticSummaryManager()
	{
		cyNetworkList = new HashMap<String, SemanticSummaryParameters>();
		
		//ADD LISTENERS HERE
		
		//catch network creation/destruction events
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		
		//catch network selection/focus events
		Cytoscape.getDesktop().getNetworkViewManager().getSwingPropertyChangeSupport()
		.addPropertyChangeListener(this);
		
		defaultNetWeight = 1.0;
	}
	
	//METHODS
	/**
	 * Retrieves the instance of the SemanticSummaryManager
	 * @return SemanticSummaryManager - the singular instance
	 */
	public static SemanticSummaryManager getInstance()
	{
		if(manager == null)
			manager = new SemanticSummaryManager();
		return manager;
	}
	
	/**
	 * Property change listener to get network events.
	 * @param event - the PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent event)
	{
		//network destroyed, remove it from our list along with clouds
		if(event.getPropertyName().equals(Cytoscape.NETWORK_DESTROYED))
		{
			networkDestroyed((String) event.getNewValue());
		}
		else if (event.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_DESTROYED))
		{
			setupCurrentNetwork();
		}
		else if (event.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED))
		{
			setupCurrentNetwork();
		}
		else if (event.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED))
		{
			setupCurrentNetwork();
		}
		else if (event.getPropertyName().equals(Cytoscape.NETWORK_TITLE_MODIFIED))
		{
			CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
			String networkID = cyNetwork.getIdentifier();
			if (isSemanticSummary(networkID))
			{
				SemanticSummaryParameters currentParams = getParameters(networkID);
				currentParams.setNetworkName(cyNetwork.getTitle());
			}
		}
	}
	
	/**
	 * Removes the CyNetwork from our list if it has just been destroyed.
	 * @param String - networkID of the destroyed CyNetwork
	 */
	private void networkDestroyed(String networkID)
	{
		//Retrieve parameters and remove if it exists
		if (isSemanticSummary(networkID))
		{
			SemanticSummaryParameters currentParams = getParameters(networkID);
			cyNetworkList.remove(networkID);
			
			//Check if this was the last network displayed
			if (currentParams.equals(curNetwork))
			{
				setupCurrentNetwork();
			}
		}
	}
	
	/**
	 * Register a new network into the manager.
	 * @param CyNetwork - the CyNetwork we are adding.
	 * @param SemanticSummaryParameters - parameters for the network.
	 */
	public void registerNetwork(CyNetwork cyNetwork, SemanticSummaryParameters params)
	{
		if(!cyNetworkList.containsKey(cyNetwork.getIdentifier()))
			cyNetworkList.put(cyNetwork.getIdentifier(), params);
	}
	
	/**
	 * Returns true if the networkID is already contained as a SemanticSummary
	 * @param String - the networkID to check.
	 */
	public boolean isSemanticSummary(String networkID)
	{
		if (cyNetworkList.containsKey(networkID))
			return true;
		else
			return false;
	}
	
	
	/**
	 * Sets up the Manager with the current network.  Clears cloud and
	 * sets user input panel to defaults.
	 */
	public void setupCurrentNetwork()
	{
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();
		
		if(isSemanticSummary(networkID))
			curNetwork = getParameters(networkID);
		else
		{
			SemanticSummaryParameters params = new SemanticSummaryParameters();
			params.setNetworkName(network.getTitle());
			params.setNetworkNodes(network.nodesList());
			params.setNetworkNumNodes(network.getNodeCount());
			SemanticSummaryManager.getInstance().registerNetwork(network, params);
			
			curNetwork = params;
		}
		
		getInputWindow().setNetworkList(curNetwork);
		getInputWindow().setUserDefaults();
		getCloudWindow().clearCloud();
	}
	/**
	 * Returns instance of SemanticSummaryParameters for the networkID
	 * supplied, if it exists.
	 * @param String - networkID to get parameters for
	 * @return SemanticSummaryParameters
	 */
	public SemanticSummaryParameters getParameters(String name)
	{
		if(cyNetworkList.containsKey(name))
			return cyNetworkList.get(name);
		else
			return null;
	}
	
	/**
	 * Returns a reference to the SemanticSummaryInputPanel (WEST)
	 * @return SemanticSummaryInputPanel
	 */
	public SemanticSummaryInputPanel getInputWindow()
	{
		return inputWindow;
	}
	
	/**
	 * Sets reference to the SemanticSummaryInputPanel (WEST)
	 * @param SemanticSummaryInputPanel - reference to panel
	 */
	public void setInputWindow(SemanticSummaryInputPanel inputWindow)
	{
		this.inputWindow = inputWindow;
	}
	
	/**
	 * Returns a reference to the CloudDisplayPanel (SOUTH)
	 * @return CloudDisplayPanel
	 */
	public CloudDisplayPanel getCloudWindow()
	{
		return cloudWindow;
	}
	
	/**
	 * Sets reference to the CloudDisplayPanel (SOUTH)
	 * @param CloudDisplayPanel - reference to panel
	 */
	public void setCloudDisplayWindow(CloudDisplayPanel cloudWindow)
	{
		this.cloudWindow = cloudWindow;
	}
	
	/**
	 * Get the parameters for the current network.
	 * @return SemanticSummaryParameters - the current network
	 */
	public SemanticSummaryParameters getCurNetwork()
	{
		return curNetwork;
	}
	
	/**
	 * Set the current network parameters.
	 * @param SemanticSummaryParameters - the current network.
	 */
	public void setCurNetwork(SemanticSummaryParameters params)
	{
		curNetwork = params;
	}
	
	/**
	 * Get the parameters of the current cloud.
	 * @return CloudParameters - the current cloud
	 */
	public CloudParameters getCurCloud()
	{
		return curCloud;
	}
	
	/**
	 * Sets the current cloud.
	 * @param CloudParameters - the current cloud.
	 */
	public void setCurCloud(CloudParameters params)
	{
		curCloud = params;
	}
	
	public Double getDefaultNetWeight()
	{
		return defaultNetWeight;
	}
	
	
}
