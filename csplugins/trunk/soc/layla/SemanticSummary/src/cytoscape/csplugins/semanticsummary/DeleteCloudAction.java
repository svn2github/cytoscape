/*
 File: DeleteCloudAction.java

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

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

/**
 * This is the action associated with deleting a Semantic Summary Tag Cloud
 * anywhere in the Semantic Summary Plugin.
 * @author Layla Oesper
 * @version 1.0
 */

public class DeleteCloudAction extends CytoscapeAction
{
	//VARIABLES
	
	//CONSTRUCTORS
	
	/**
	 * DeleteCloudAction constructor.
	 */
	public DeleteCloudAction()
	{
		super("Delete Cloud");
	}
	
	//METHODS
	
	/**
	 * Method called when a Delete Cloud action occurs.
	 * 
	 * @param ActionEvent - event created when choosing Delete Cloud.
	 */
	public void actionPerformed(ActionEvent ae)
	{
		//Retrieve current cloud and Network from Manager
		SemanticSummaryParameters networkParams = SemanticSummaryManager.
		getInstance().getCurNetwork();
		CloudParameters cloudParams = SemanticSummaryManager.getInstance().getCurCloud();
		
		//Delete if cloud is not null
		if (cloudParams != null)
		{
			String cloudName = cloudParams.getCloudName();
			
			//Remove cloud from list
			networkParams.getClouds().remove(cloudName);
			
			//Update Current network
			SemanticSummaryManager.getInstance().setupCurrentNetwork();
			
			//Clear Selected Nodes
			//CyNetwork network = Cytoscape.getCurrentNetwork();
			//network.unselectAllNodes();
			//network.unselectAllEdges();
			
			//Redraw the graph with selected nodes
			//CyNetworkView view = Cytoscape.getCurrentNetworkView();
			//view.redrawGraph(false, true);
			
			SemanticSummaryPluginAction init = new SemanticSummaryPluginAction();
			init.loadCloudPanel();
			init.loadInputPanel();
		}
	}
}
