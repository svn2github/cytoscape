/* File: RandomNetworkPlugin.java

 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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

package cytoscape.randomnetwork;

import javax.swing.JOptionPane;
import java.util.*;
import giny.model.*;
import giny.view.*;
import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.*;
import cytoscape.util.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import javax.swing.*;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import java.awt.event.ActionEvent;



/*
	RandomNetworkPlugin is the topmost class
*/
public class RandomNetworkPlugin extends CytoscapePlugin {

	/*	
		RandomNetworkPlugin Constructor
	*/
	public RandomNetworkPlugin() {
	
		//Add an item to the plugin menu
		GenerateRandomAction action = new GenerateRandomAction();
		action.setPreferredMenu("Plugins");
		Cytoscape.getDesktop().getCyMenus().addAction(action);
		
		//Get the VisualMappingManager
		VisualMappingManager vmm = Cytoscape.getVisualMappingManager();

		//Get the listing of visualStyles
		CalculatorCatalog catalog = vmm.getCalculatorCatalog();

		//Get the default visualStyle
		VisualStyle defaultStyle = catalog.getVisualStyle("default");

		//Create a new visualStyle, from the default style
		VisualStyle newStyle = new VisualStyle(defaultStyle);
		
		//Set the name for this visualStyle
		newStyle.setName("random network");
		
		//Get the NodeAppearenceCalculator for this style
		NodeAppearanceCalculator nodeAC = newStyle.getNodeAppearanceCalculator();
		
		//Remove the node Label mapping
		nodeAC.removeCalculator(cytoscape.visual.VisualPropertyType.NODE_LABEL);

		//Add this visual Style to the list of options
		catalog.addVisualStyle(newStyle);
	}
				
	
	/*
	 *	This class creates the main window for our plugin
	 */
	class RandomNetworkDialog extends JDialog implements ActionListener
	{
		//Main Tabbed Pane for our Dialog
		javax.swing.JTabbedPane mainPane;

		//The panel used for generating random networks
		GenerateRandomPanel generateRandomPanel;
		javax.swing.JPanel verifyRandomPanel;

		javax.swing.JPanel randomizePane;
		javax.swing.JPanel compareRandomPane;
	
	
		/*
		 *  The default constructor for this class	
		 */
		public RandomNetworkDialog(java.awt.Frame parent) {
			super(parent, true);
			initComponents();
			pack();
		}

		/*
		 * Initialize the swing components
		 */
		private void initComponents() {
			
			//Initialize the tabbed panel
			mainPane = new JTabbedPane();
			//Create the Panel
			generateRandomPanel = new GenerateRandomPanel(0);
			verifyRandomPanel = new GenerateRandomPanel(1);
			randomizePane = new JPanel();
			compareRandomPane = new JPanel();
			
			//Default is to dispose on close
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			
			//Add the three tabs for our application
			mainPane.addTab("Generate Random Network", generateRandomPanel);
			mainPane.addTab("Verify Random Network", verifyRandomPanel);
			mainPane.addTab("Randomize Existing Network", randomizePane);
			mainPane.addTab("Compare to Random Network", compareRandomPane);

			//add the main pane to our tabbed panel
			add(mainPane);
		}
		
		/*
		 * On ActionEvent
		 */
		public void actionPerformed(ActionEvent e) {
			//for now do nothing, we may be able to remove this
		}
	}


	/*
	 *  The action which brings up our dialog
	 */
	class GenerateRandomAction extends CytoscapeAction {

		/*
		 * Default constructor
		 */
		public GenerateRandomAction() {
			super("Random Network Plugin");
		}

		/*
	 	 *  When our item is selected run this function
		 */
		public void actionPerformed(ActionEvent ae) {
		
			//Create our dialog
			RandomNetworkDialog dialog = new RandomNetworkDialog(Cytoscape.getDesktop());
			dialog.pack();
			dialog.setLocationRelativeTo(Cytoscape.getDesktop());
			dialog.show();

		}
	}

}
