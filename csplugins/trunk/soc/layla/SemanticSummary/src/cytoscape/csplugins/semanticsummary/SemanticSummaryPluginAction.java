/*
 File: SemanticSummaryPluginAction.java

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

import javax.swing.SwingConstants;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;


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
		this.loadInputPanel();
		this.loadCloudPanel();
	}
	
	/**
	 * Loads the InputPanel or brings it into the forefront.
	 */
	public void loadInputPanel()
	{
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
			cytoPanel.add("Semantic Summary", inputWindow);
			
			//Move to front of display
			index = cytoPanel.indexOfComponent(inputWindow);
			cytoPanel.setSelectedIndex(index);
		}//end if not loaded
		
		else
		{
			//Add panel to display
			cytoPanel.add("Semantic Summary",inputWindow);
			
			//Move to front of display
			index = cytoPanel.indexOfComponent(inputWindow);
			cytoPanel.setSelectedIndex(index);
		}//end else
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
			cytoPanel.add("Semantic Summary Cloud", cloudWindow);
			
			//Move to front of display
			index = cytoPanel.indexOfComponent(cloudWindow);
			cytoPanel.setSelectedIndex(index);
		}//end if not loaded
		
		else
		{
			//Add panel to display
			cytoPanel.add("Semantic Summary Cloud",cloudWindow);
			
			//Move to front of display
			index = cytoPanel.indexOfComponent(cloudWindow);
			cytoPanel.setSelectedIndex(index);
		}//end else
	}//end loadCloudPanel() method
	
}
