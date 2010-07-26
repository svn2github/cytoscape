/*
 File: SemanticSummaryPluginAction.java

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

import javax.swing.JList;
import javax.swing.SwingConstants;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;


/**
 * This class defines the Semantic Summary Plugin Action.
 * This action is associated with what happens when a user selects
 * the Semantic Summary option from the Plugins menu, or right click menu.
 * 
 * @author Layla Oesper
 * @version 1.0
 */

public class SemanticSummaryPluginAction extends CytoscapeAction
{
	//VARIABLES
	
	
	//CONSTRUCTORS
	
	/**
	 * SemanticSummaryPluginAction constructor
	 * 
	 */
	public SemanticSummaryPluginAction()
	{
		super("Settings");
	}
	
	//METHODS
	
	/**
	 * Method called when Semantic Summary is chosen from Plugins menu. Loads
	 * SemanticSummaryPanel, CloudDisplayPanel and initializes Manager object.
	 * 
	 * @param ActionEvent - event created when choosing Semantic Summary from
	 * the Plugins menu.
	 */
	public void actionPerformed(ActionEvent ae)
	{
		//Create Null Cloud in Manager
		CloudParameters nullCloud = SemanticSummaryManager.getInstance().getNullCloudParameters();
		if (nullCloud == null)
		{
			SemanticSummaryManager.getInstance().setupNullCloudParams();
		}
		
		
		boolean loaded = this.loadInputPanel();
		this.loadCloudPanel();
		
		if (!loaded)
			SemanticSummaryManager.getInstance().setupCurrentNetwork();
	}
	
	/**
	 * Loads the InputPanel or brings it into the forefront.  Returns false
	 * if this is the first time that the input panel has been loaded.
	 */
	public boolean loadInputPanel()
	{
		boolean loaded = false;
		
		int index = 0;
		
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.WEST);
		
		//Check if panel already exists
		SemanticSummaryInputPanel inputWindow = SemanticSummaryManager.getInstance().getInputWindow();
		
		if(inputWindow == null)
		{
			inputWindow = new SemanticSummaryInputPanel();
			
			//Set input window in the manager
			SemanticSummaryManager.getInstance().setInputWindow(inputWindow);
			
			//Add panel to display
			cytoPanel.add("WordCloud", inputWindow);
			
			//Move to front of display
			index = cytoPanel.indexOfComponent(inputWindow);
			cytoPanel.setSelectedIndex(index);
			
			//Create Listener for when this panel moves to the front
			cytoPanel.addCytoPanelListener(new InputPanelSelectListener(index));
			
		}//end if not loaded
		
		else
		{
			//Add panel to display
			cytoPanel.add("WordCloud",inputWindow);
			
			//Move to front of display
			index = cytoPanel.indexOfComponent(inputWindow);
			cytoPanel.setSelectedIndex(index);
			loaded = true;
		}//end else
		
		return loaded;
	}//end loadInputPanel() method
	
	
	
	/**
	 * Loads the CloudPanel or brings it into the forefront.
	 */
	public void loadCloudPanel()
	{
		int index = 0;
		
		CytoscapeDesktop desktop = Cytoscape.getDesktop();
		CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.SOUTH);
		
		//Check if panel already exists
		CloudDisplayPanel cloudWindow = SemanticSummaryManager.getInstance().getCloudWindow();
		
		if(cloudWindow == null)
		{
			
			cloudWindow = new CloudDisplayPanel();
			
			//Set input window in the manager
			SemanticSummaryManager.getInstance().setCloudDisplayWindow(cloudWindow);
			
			//Add panel to display
			cytoPanel.add("WordCloud Display", cloudWindow);
			
			//Move to front of display
			index = cytoPanel.indexOfComponent(cloudWindow);
			cytoPanel.setSelectedIndex(index);
		}//end if not loaded
		
		else
		{
			//Add panel to display
			cytoPanel.add("WordCloud Display",cloudWindow);
			
			//Move to front of display
			index = cytoPanel.indexOfComponent(cloudWindow);
			cytoPanel.setSelectedIndex(index);
		}//end else
	}//end loadCloudPanel() method
	
	public class InputPanelSelectListener implements CytoPanelListener
	{
		public InputPanelSelectListener(int index)
		{
		}
		public void onComponentSelected(int ComponentIndex)
		{
			SemanticSummaryManager.getInstance().refreshCurrentNetworkList();
		}
		public void onComponentAdded(int arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onComponentRemoved(int arg0) {
			// TODO Auto-generated method stub
			
		}
		public void onStateChange(CytoPanelState arg0) {
			// TODO Auto-generated method stub
			
		}
	}
}


