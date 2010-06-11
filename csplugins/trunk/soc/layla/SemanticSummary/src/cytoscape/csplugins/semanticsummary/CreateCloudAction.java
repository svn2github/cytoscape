/*
 File: CreateCloudAction.java

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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

/**
 * This is the action associated with creating a new Semantic Summary Tag Cloud
 * anywhere in the Semantic Summary Plugin.  This includes from the Plugin menu,
 * right click on a node, and from the Semantic Summary Input Panel.
 * @author Layla Oesper
 * @version 1.0
 */

public class CreateCloudAction extends CytoscapeAction
{
	//VARIABLES
	
	//CONSTRUCTORS
	
	/**
	 * CreateCloudAction constructor.
	 */
	public CreateCloudAction()
	{
		super("Create Cloud");
	}
	
	//METHODS
	
	/**
	 * Method called when a Create Cloud action occurs.
	 * 
	 * @param ActionEvent - event created when choosing Create Cloud from 
	 * any of its various locations.
	 */
	public void actionPerformed(ActionEvent ae)
	{
		//Initialize the Semantic Summary Panels
		SemanticSummaryPluginAction init = new SemanticSummaryPluginAction();
		init.actionPerformed(ae);
		
		//Retrieve current network and view
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		
		//Unable to proceed if either of these is null
		//TODO - This never happens since network and view will be special
		//empty CyNetwork(View)
		if (network == null || view == null)
		{return;}
		
		//If no nodes are selected
		if (view.getSelectedNodes().size() == 0)
		{
			JOptionPane.showMessageDialog(view.getComponent(), 
					"Please select one or more nodes.");
			return;
		}
		
		//Check if network is already in our list
		SemanticSummaryParameters params;
		String networkID = network.getIdentifier();
		
		//Get SemanticSummaryParameters or Register if necessary
		if(SemanticSummaryManager.getInstance().isSemanticSummary(networkID))
			params = SemanticSummaryManager.getInstance().getParameters(networkID);
		else
		{
			params = new SemanticSummaryParameters();
			params.setNetworkName(network.getTitle());
			
			//Get list of node ID's
			List<CyNode> nodes = network.nodesList();
			List<String> nodeNames = new ArrayList<String>();
			for(Iterator<CyNode> iter = nodes.iterator(); iter.hasNext();)
			{
				CyNode curNode = iter.next();
				String curName = curNode.toString();
				nodeNames.add(curName);
			}
			params.setNetworkNodes(nodeNames);
			SemanticSummaryManager.getInstance().registerNetwork(network, params);
		}
		
		//Create CloudParameters
		CloudParameters cloudParams = new CloudParameters();
		cloudParams.setCloudName(params.getNextCloudName()); 
		cloudParams.setNetworkParams(params);
		cloudParams.setNetworkNumNodes(network.getNodeCount());
		
		//Get list of node ID's
		Set<CyNode> nodes = network.getSelectedNodes();
		List<String> nodeNames = new ArrayList<String>();
		for(Iterator<CyNode> iter = nodes.iterator(); iter.hasNext();)
		{
			CyNode curNode = iter.next();
			String curName = curNode.toString();
			nodeNames.add(curName);
		}
		
		cloudParams.setSelectedNodes(nodeNames);
		cloudParams.setSelectedNumNodes(nodeNames.size());
		
		//Add to list of clouds
		params.addCloud(cloudParams.getCloudName(), cloudParams);
		
		//Retrieve values from input panel
		cloudParams.retrieveInputVals();
		
		cloudParams.updateRatios();
		cloudParams.calculateFontSizes();
		
		CloudDisplayPanel cloudPanel =
			SemanticSummaryManager.getInstance().getCloudWindow();
		
		cloudPanel.updateCloudDisplay(cloudParams);
		
		//Update list of clouds
		SemanticSummaryInputPanel inputPanel = 
			SemanticSummaryManager.getInstance().getInputWindow();
		
		//inputPanel.setNetworkList(params);
		inputPanel.addNewCloud(cloudParams);
		
			
	}
}
