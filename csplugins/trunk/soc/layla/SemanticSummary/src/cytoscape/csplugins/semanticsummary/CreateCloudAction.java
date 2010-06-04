/*
 File: CreateCloudAction.java

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

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
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
			params.setNetworkName(networkID);
			params.setNetworkNodes(network.nodesList());
			params.setNetworkNumNodes(network.getNodeCount());
			SemanticSummaryManager.getInstance().registerNetwork(network, params);
		}
		
		//Create CloudParameters
		CloudParameters cloudParams = new CloudParameters();
		cloudParams.setCloudName(networkID);
		cloudParams.setCloudName(params.getNextCloudName()); 
		cloudParams.setNetworkParams(params);
		
		//TODO - CHOICE HERE
		//cloudParams.setSelectedNodes(view.getSelectedNodes());
		cloudParams.setSelectedNodes(network.getSelectedNodes());
		
		cloudParams.setSelectedNumNodes(network.getNodeCount());
		
		//Add to list of clouds
		params.addCloud(cloudParams.getCloudName(), cloudParams);
		
		//Retrieve values from input panel
		cloudParams.retrieveInputVals();
		
		cloudParams.updateRatios();
		cloudParams.calculateFontSizes();
		
		CloudDisplayPanel cloudPanel =
			SemanticSummaryManager.getInstance().getCloudWindow();
		
		cloudPanel.updateCloudDisplay(cloudParams);
		
		//TODO - Finish this
			
	}
}
