/*
 File: UpdateCloudAction.java

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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
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
 * This is the action associated with updating a Semantic Summary Tag Cloud
 * anywhere in the Semantic Summary Plugin.
 * @author Layla Oesper
 * @version 1.0
 */

public class UpdateCloudAction extends CytoscapeAction
{

//VARIABLES
	
	//CONSTRUCTORS
	
	/**
	 * DeleteCloudAction constructor.
	 */
	public UpdateCloudAction()
	{
		super("Update Cloud");
	}
	
	//METHODS
	
	/**
	 * Method called when an Update Cloud action occurs.
	 * 
	 * @param ActionEvent - event created when choosing Update Cloud.
	 */
	public void actionPerformed(ActionEvent ae)
	{
		//Initialize the Semantic Summary Panels/Bring to front
		SemanticSummaryPluginAction init = new SemanticSummaryPluginAction();
		init.actionPerformed(ae);
		
		//Retrieve current cloud and Network from Manager
		SemanticSummaryParameters networkParams = SemanticSummaryManager.
		getInstance().getCurNetwork();
		CloudParameters cloudParams = SemanticSummaryManager.getInstance().getCurCloud();
		
		//Retrieve current network and view
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		
		//Update network if necessary
		if (networkParams.networkHasChanged(network))
			networkParams.updateParameters(network);
		
		//Get set of selected and compare to saved
		Set<CyNode> nodes = network.getSelectedNodes();
		Boolean isDifferent = checkSelectionChange(cloudParams, nodes);
		
		//If List is changed
		if (isDifferent)
		{
			//Ask to continue or revert
			Component parent = Cytoscape.getDesktop();
			int value = JOptionPane.NO_OPTION;
			Object[] options = { "Continue", "Revert"};
			
			value = JOptionPane.showOptionDialog(parent,
					"Network node selection has changed. " +
					"Continue with current selection or revert to the original?",
					"Node Selection Changed",
					JOptionPane.WARNING_MESSAGE,
					JOptionPane.YES_NO_CANCEL_OPTION,
					null,
					options,
					options[0]);
			
			if (value == JOptionPane.YES_OPTION)
			{
			
				//If no nodes are selected
				if (nodes.size() == 0)
				{
					JOptionPane.showMessageDialog(view.getComponent(), 
					"Please select one or more nodes.");
					return;
				}
			
				//New set of selected nodes
				List<String> nodeNames = new ArrayList<String>();
				for(Iterator<CyNode> iter = nodes.iterator(); iter.hasNext();)
				{
					CyNode curNode = iter.next();
					String curName = curNode.toString();
					nodeNames.add(curName);
				}
			
				cloudParams.setSelectedNodes(nodeNames);
				cloudParams.setSelectedNumNodes(nodeNames.size());
			}//end of updating selection
			else if (value == JOptionPane.NO_OPTION)
			{
				//Update network view with old set of selected nodes
				List<String> selNodeNames = cloudParams.getSelectedNodes();
				Set<CyNode> selNodes = new HashSet<CyNode>();
			
				for(int i = 0; i< selNodeNames.size(); i++)
				{
					String curNodeID = selNodeNames.get(i);
					CyNode curNode = Cytoscape.getCyNode(curNodeID);
					selNodes.add(curNode);
				}
				
				network.unselectAllNodes();
				network.unselectAllEdges();
				network.setSelectedNodeState(selNodes, true);
				
				//Redraw the graph with selected nodes
				view.redrawGraph(false, true);
			}
		}//end if network selection has changed
		
		//Retrieve values from input panel
		cloudParams.retrieveInputVals();
		
		//Update with new information
		cloudParams.calculateFontSizes();
		
		CloudDisplayPanel cloudPanel =
			SemanticSummaryManager.getInstance().getCloudWindow();
		
		cloudPanel.updateCloudDisplay(cloudParams);
	}
	
	/**
	 * Checks whether the set of selected nodes stored in the cloudParameters
	 * is different from the list provided.
	 */
	private boolean checkSelectionChange(CloudParameters params, Set<CyNode> nodes)
	{
		Boolean isChanged = false;
		List<String> oldNames = params.getSelectedNodes();
		
		//Create a hash set to make this call more efficient when check for contains
		HashSet<String> oldNamesHash = new HashSet<String>();
		
		for (int i = 0; i < oldNames.size(); i++)
		{
			String curNode = oldNames.get(i);
			oldNamesHash.add(curNode);
		}
		
		//If lists are different size, they can't be the same
		if (oldNamesHash.size() != nodes.size())
			return true;
		
		else
		{
			//Since they are the same size, just need to check for subset
			for (Iterator<CyNode> iter = nodes.iterator(); iter.hasNext();)
			{
				String nodeName = iter.next().getIdentifier();
				if (!oldNamesHash.contains(nodeName))
				{
					isChanged = true;
					break;
				}
			}
		}
		return isChanged;
	}
}
