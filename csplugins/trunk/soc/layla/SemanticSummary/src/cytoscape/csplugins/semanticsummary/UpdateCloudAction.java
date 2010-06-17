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

import java.awt.event.ActionEvent;
import java.util.ArrayList;
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
		
		//If no nodes are selected
		if (network.getSelectedNodes().size() == 0)
		{
			JOptionPane.showMessageDialog(view.getComponent(), 
					"Please select one or more nodes.");
			return;
		}
		
		//New set of selected nodes
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

		//Retrieve values from input panel
		cloudParams.retrieveInputVals();
		
		//Update with new information
		cloudParams.calculateFontSizes();
		
		CloudDisplayPanel cloudPanel =
			SemanticSummaryManager.getInstance().getCloudWindow();
		
		cloudPanel.updateCloudDisplay(cloudParams);
		
	}
}
