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
		
		int selection = confirmDelete();
		
		if (selection == JOptionPane.YES_OPTION)
		{
		
			//Delete if cloud is not null
			if (cloudParams != null && 
					cloudParams != SemanticSummaryManager.getInstance().getNullCloudParameters())
			{
				String cloudName = cloudParams.getCloudName();
			
				//Remove cloud from list
				networkParams.getClouds().remove(cloudName);
			
				//Update Current network
				SemanticSummaryManager.getInstance().setupCurrentNetwork();
			
				SemanticSummaryPluginAction init = new SemanticSummaryPluginAction();
				init.loadCloudPanel();
				init.loadInputPanel();
			}
		}
	}
	
	private int confirmDelete()
	{
		//Ask to continue or revert
		Component parent = Cytoscape.getDesktop();
		int value = JOptionPane.NO_OPTION;
		
		value = JOptionPane.showConfirmDialog(parent,"Are you sure you want to permanently delete the selected cloud?", 
				"Delete Cloud",
				JOptionPane.YES_NO_OPTION);
		
		return value;
	}
}
