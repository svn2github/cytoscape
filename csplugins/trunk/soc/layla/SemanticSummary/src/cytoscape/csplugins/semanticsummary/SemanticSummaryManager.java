/*
 File: SemanticSummaryManager.java

 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.csplugins.semanticsummary;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import cytoscape.CyNetwork;

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
	
	//CONSTRUCTOR
	/**
	 * This is a private constructor that is only called by the getInstance()
	 * method.
	 */
	private SemanticSummaryManager()
	{
		cyNetworkList = new HashMap<String, SemanticSummaryParameters>();
		
		//TODO
		//Create and add panels in correct locations
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
		//TODO
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
	
	
}
