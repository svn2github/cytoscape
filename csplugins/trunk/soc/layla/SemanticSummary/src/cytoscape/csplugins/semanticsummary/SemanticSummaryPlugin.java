/*
 File: SemanticSummaryPlugin.java

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

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JMenu;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import ding.view.DGraphView;

/**
 * This class defines the Semantic Summary Plugin.
 * It allows a user to create a tag cloud which displays semantic 
 * information from the selected nodes in a given network.
 * 
 * @author Layla Oesper
 * @version 1.0
 */

public class SemanticSummaryPlugin extends CytoscapePlugin 
{
	
	//CONSTRUCTORS
	
	/**
	 * SemanticSummaryPlugin Constructor
	 */
	
	public SemanticSummaryPlugin()
	{
		//New actions for response to menu selections
		SemanticSummaryPluginAction settings = new SemanticSummaryPluginAction();
		CreateCloudAction create = new CreateCloudAction();
		settings.setPreferredMenu("Plugins.Semantic Network Summary");
		create.setPreferredMenu("Plugins.Semantic Network Summary");
	
		//Add to Plugin Menu
		Cytoscape.getDesktop().getCyMenus().addAction(settings);
		Cytoscape.getDesktop().getCyMenus().addAction(create);
		
		//Add to right click menus
		
		//Newly created networks
		SemanticSummaryNetworkListener netListener = 
			new SemanticSummaryNetworkListener();
		
		Cytoscape.getSwingPropertyChangeSupport().
		addPropertyChangeListener(netListener);
		
		//Loaded networks
		Set networkSet = Cytoscape.getNetworkSet();
		
		for (Iterator iter = networkSet.iterator(); iter.hasNext();)
		{
			CyNetwork network = (CyNetwork)iter.next();
			
			SemanticSummaryNodeContextMenuListener nodeMenuListener = 
				new SemanticSummaryNodeContextMenuListener();
			
			((DGraphView)Cytoscape.getNetworkView(network.getIdentifier()))
			.addNodeContextMenuListener(nodeMenuListener);
		}
	}
	

	
	//METHODS
	
	/**
	 * Provides a description of the SemanticSummaryPlugin
	 * @return String that describes the SemanticSummaryPlugin
	 */
	public String describe()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("For every node in the current network, this plugin ");
		sb.append("displays a word cloud semantic summary of the selected ");
		sb.append("cyNode attribute.  The node name is the defuault ");
		sb.append("attribute.");
		return sb.toString();
	}
	
	/**
	 * SaveSessionStateFiles collects all the data stored in the Semantic 
	 * Summary data structures and creates property files for each network 
	 * listing the variables needed to rebuild the Semantic Summary.
	 * 
	 * @param pFileList - pointer to the set of files to be added to the session
	 */
	public void saveSessionStateFiles(List<File> pFileList)
	{
		//TODO
	}
	
	/**
	 * Restore Semantic Summaries
	 * 
	 * @param pStateFileList - list of files associated with the session
	 */
	public void restoreSessionState(List<File> pStateFileList)
	{
		//TODO
	}
	
	
}
