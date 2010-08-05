/*
 File: CreateCloudNetworkAction.java

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

import giny.model.Edge;
import giny.model.Node;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.layout.CyLayouts;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;

/**
 * This is the action is associated with creating a new Network from an existing
 * word tag cloud.
 * @author Layla Oesper
 * @version 1.0
 */

public class CreateCloudNetworkAction extends CytoscapeAction
{

	private static final long serialVersionUID = 2683655996962050569L;
	
	//VARIABLES
	public static String WORD_VAL = "Word_Prob";
	public static String CO_VAL = "CO_Prob";
	public static String INTERACTION_TYPE = "CO";
	private static final char controlChar = '\u001F';
	
	//CONSTRUCTORS
	
	/**
	 * CreateCloudAction constructor.
	 */
	public CreateCloudNetworkAction()
	{
		super("Create Cloud Network");
	}
	
	//METHODS
	
	/**
	 * Method called when a Create Network Cloud action occurs.
	 * 
	 * @param ActionEvent - event created when choosing to create a network
	 * from an existing cloud.
	 */
	public void actionPerformed(ActionEvent ae) 
	{
		//Retrieve the current cloud and relevent information
		CloudParameters curCloud = SemanticSummaryManager.getInstance().getCurCloud();
		HashMap<String, Double> ratios = curCloud.getRatios();
		HashMap<String, Double> pairRatios = curCloud.getPairRatios();
		
		//Create the network
		String newNetworkName = curCloud.getNextNetworkName();
		CyNetwork network = Cytoscape.createNetwork(newNetworkName);
		
		//Create nodes
		for (Iterator<String> iter = ratios.keySet().iterator(); iter.hasNext();)
		{
			String curWord = iter.next();
			Node node = Cytoscape.getCyNode(curWord, true);
			
			network.addNode(node);
			
			//Add attribute to the node
			Double nodeRatio = ratios.get(curWord);
			String attName = newNetworkName + ":" + CreateCloudNetworkAction.WORD_VAL;
			CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
			nodeAttrs.setAttribute(node.getIdentifier(), attName, nodeRatio);
		}
		
		//Create edges
		for (Iterator<String> iter = pairRatios.keySet().iterator(); iter.hasNext();)
		{
			String curEdge = iter.next();
			Double edgeRatio = pairRatios.get(curEdge);
			String[] nodeNames = curEdge.split(Character.toString(controlChar));
			String nodeName1 = nodeNames[0];
			String nodeName2 = nodeNames[1];
			Node node1 = Cytoscape.getCyNode(nodeName1, false);
			Node node2 = Cytoscape.getCyNode(nodeName2, false);
			Double node1Ratio = ratios.get(nodeName1);
			Double node2Ratio = ratios.get(nodeName2);
			Double conditionalRatio = edgeRatio / (node1Ratio * node2Ratio);
			
			//Only create if prob > 1
			if (conditionalRatio > 1)
			{
				Edge edge = (Edge) Cytoscape.getCyEdge(node1, node2, Semantics.INTERACTION, CreateCloudNetworkAction.INTERACTION_TYPE, true);
			
				//Add attribute to the edge
				String attName = newNetworkName + ":" + CreateCloudNetworkAction.CO_VAL;
				CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
				edgeAttrs.setAttribute(edge.getIdentifier(), attName, conditionalRatio);
			
				network.addEdge(edge);
			}
		}
		
		//Visual Style stuff
		CyNetworkView view = Cytoscape.createNetworkView(network);
		
		//make sure that network is registered so that Quickfind works
		Cytoscape.firePropertyChange(cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED, network, view);
		
		VisualMappingManager manager = Cytoscape.getVisualMappingManager();
		CalculatorCatalog catalog = manager.getCalculatorCatalog();
		
		String vs_name = newNetworkName + "WordCloud_style";
		//check to see if the style exists
		VisualStyle vs = catalog.getVisualStyle(vs_name);
		
		if (vs == null)
		{
			WordCloudVisualStyle wc_vs = new WordCloudVisualStyle(vs_name, newNetworkName,curCloud);
			vs = wc_vs.createVisualStyle(network, newNetworkName);
			
			catalog.addVisualStyle(vs);
		}
		
		view.setVisualStyle(vs.getName());
		manager.setVisualStyle(vs);
		view.redrawGraph(true, true);
		
		//Create view
		view.applyLayout(CyLayouts.getLayout("force-directed"));
	}

}
