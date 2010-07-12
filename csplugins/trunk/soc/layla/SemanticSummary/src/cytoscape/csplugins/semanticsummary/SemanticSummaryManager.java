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
import java.util.Iterator;
import java.util.Set;

import javax.swing.JList;
import javax.swing.ListSelectionModel;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.attr.MultiHashMapListener;
import cytoscape.view.CytoscapeDesktop;

/**
 * The SemanticSummaryManager class is a singleton class that manages
 * all the parameters involved in using the Semantic Summary Plugin.
 * 
 * @author Layla Oesper
 * @version 1.0
 *
 */

public class SemanticSummaryManager implements PropertyChangeListener, MultiHashMapListener 
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
	
	
	//Null Values for params
	private SemanticSummaryParameters nullSemanticSummary;
	private CloudParameters nullCloudParameters;
	
	//CONSTRUCTOR
	/**
	 * This is a private constructor that is only called by the getInstance()
	 * method.
	 */
	private SemanticSummaryManager()
	{
		cyNetworkList = new HashMap<String, SemanticSummaryParameters>();
		nullSemanticSummary = new SemanticSummaryParameters();
		nullSemanticSummary.setNetworkName("No Network Loaded");
		nullCloudParameters = new CloudParameters();
		nullCloudParameters.setCloudName("Null Cloud");
		nullCloudParameters.setNetworkParams(nullSemanticSummary);
		
		//catch network creation/destruction events
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		
		//catch network selection/focus events
		Cytoscape.getDesktop().getNetworkViewManager().getSwingPropertyChangeSupport()
		.addPropertyChangeListener(this);
		
		//catch attribute changes
		Cytoscape.getNodeAttributes().getMultiHashMap().addDataListener(this);
		
		curNetwork = nullSemanticSummary;
		curCloud = nullCloudParameters;
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
		else if (event.getPropertyName().equals(Cytoscape.NETWORK_CREATED))
		{
			setupCurrentNetwork();
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
				
				//Update Input Panel 
				SemanticSummaryManager.getInstance().getInputWindow().
				getNetworkLabel().setText(currentParams.getNetworkName());
			}
		}
		else if (event.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED))
		{
			inputWindow.refreshAttributeCMB();
		}
		
		else if (event.getPropertyName().equals(Cytoscape.NETWORK_MODIFIED))
		{
			networkModified();
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
			cyNetworkList.remove(networkID);
		}
	}
	
	/**
	 * Updates any current Network parameters that the network has changed, and 
	 * notifies clouds that they need to be recomputed.
	 *@param String - networkID of the modified CyNetwork
	 */
	private void networkModified()
	{
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();
		
		//Retrieve parameters and mark modified
		if (isSemanticSummary(networkID))
		{
			SemanticSummaryParameters params = this.getParameters(networkID);
			params.updateParameters(network);
		}
	}
	
	/*
	 * Register a new network into the manager.
	 * @param CyNetwork - the CyNetwork we are adding.
	 * @param SemanticSummaryParameters - parameters for the network.
	 */
	public void registerNetwork(CyNetwork cyNetwork, SemanticSummaryParameters params)
	{
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

		//Null current network
		if (network.equals(Cytoscape.getNullNetwork()))
		{
			curNetwork = nullSemanticSummary;
			curCloud = nullCloudParameters;
		}
		
		//Already Registered
		else if(isSemanticSummary(networkID))
			curNetwork = getParameters(networkID);
		
		//Need to create new
		else
		{
			SemanticSummaryParameters params = new SemanticSummaryParameters();
			params.updateParameters(network);

			SemanticSummaryManager.getInstance().registerNetwork(network, params);
			
			curNetwork = params;
		}
		
		//Update cloud list and update attributes
		getInputWindow().setNetworkList(curNetwork);
		getCloudWindow().clearCloud();
		getInputWindow().setUserDefaults();
		getInputWindow().refreshAttributeCMB();
		getInputWindow().refreshRemovalCMB();
		
		getInputWindow().loadCurrentCloud(curCloud);

	}
	
	/**
	 * Refreshes the current network list to be up to date.  Called whenever
	 * the tab in the control panel changes.
	 */
	public void refreshCurrentNetworkList()
	{
		CyNetwork network = Cytoscape.getCurrentNetwork();
		String networkID = network.getIdentifier();
		String newNetworkName = network.getTitle();
		String oldNetworkName = curNetwork.getNetworkName();
		JList cloudList = getInputWindow().getCloudList();
		Object selected = cloudList.getSelectedValue();
		String CloudName = curCloud.getCloudName();
		
		
		//Null current network
		if (network.equals(Cytoscape.getNullNetwork()))
		{
			curNetwork = nullSemanticSummary;
			curCloud = nullCloudParameters;
		}
		
		//Already Registered
		else if(isSemanticSummary(networkID))
			curNetwork = getParameters(networkID);
		
		//Need to create new
		else
		{
			SemanticSummaryParameters params = new SemanticSummaryParameters();
			params.updateParameters(network);
			SemanticSummaryManager.getInstance().registerNetwork(network, params);
			curNetwork = params;
		}
		
		//Update cloud list and update attributes
		getInputWindow().setNetworkList(curNetwork);//Clear cur cloud
		
		//If network has not changed, keep the same row highlighted
		if (newNetworkName.equals(oldNetworkName) && (selected != null))
		{
			//Turn off listener while doing work
			ListSelectionModel listSelectionModel = cloudList.getSelectionModel();
			CloudListSelectionHandler handler = getInputWindow().getCloudListSelectionHandler();
			listSelectionModel.removeListSelectionListener(handler);
			
			//Reset selected Value
			String selectedValue = (String)selected;
			getInputWindow().getCloudList().setSelectedValue(selectedValue, true);
			
			//Turn listener back on
			listSelectionModel.addListSelectionListener(handler);
			
			//Ensure current cloud has not changed
			if (curNetwork.containsCloud(CloudName))
				curCloud = curNetwork.getCloud(CloudName);
		}
		else if (!newNetworkName.equals(oldNetworkName))
		{
			getInputWindow().setUserDefaults();
			getInputWindow().refreshAttributeCMB();
			getCloudWindow().clearCloud();
			
		}
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
	 * Setup the nullCloudParameters for the manager now that it is initialized.
	 */
	public void setupNullCloudParams()
	{
		nullCloudParameters = new CloudParameters();
		nullCloudParameters.setCloudName("Null Cloud");
		curCloud = nullCloudParameters;
	}
	
	
	/**
	 * Returns the hashmap of all the SemanticSummaryParameters.
	 * @return HashMap of all the SemanticSummaryParameters for all networks.
	 */
	public HashMap<String, SemanticSummaryParameters> getCyNetworkList()
	{
		return cyNetworkList;
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
	
	public SemanticSummaryParameters getNullSemanticSummary()
	{
		return nullSemanticSummary;
	}
	
	public CloudParameters getNullCloudParameters()
	{
		return nullCloudParameters;
	}

	public void allAttributeValuesRemoved(String objectKey, String attributeName) {
		// TODO Auto-generated method stub
		
	}

	public void attributeValueAssigned(String objectKey, String attributeName, Object[] keyIntoValue,
			Object oldAttributeValue, Object newAttributeValue) {

		CyNode curNode = Cytoscape.getCyNode(objectKey, false);
		if (curNode != null)
		{
			HashMap<String, SemanticSummaryParameters> networks = 
				SemanticSummaryManager.getInstance().getCyNetworkList();
			
			Set<String> names = networks.keySet();
			for (Iterator<String> iter = names.iterator(); iter.hasNext();)
			{
				String curNetwork = iter.next();
				SemanticSummaryParameters params = networks.get(curNetwork);
				String networkName = params.getNetworkName();
				CyNetwork cyNetwork = Cytoscape.getNetwork(networkName);
				
				if (cyNetwork.containsNode(curNode))
				{
					params.networkChanged();
				}
			}
		}
		
	}

	public void attributeValueRemoved(String objectKey, String attributeName, Object[] keyIntoValue,
			Object attributeValue) {
		// TODO Auto-generated method stub
		
	}
	
}
