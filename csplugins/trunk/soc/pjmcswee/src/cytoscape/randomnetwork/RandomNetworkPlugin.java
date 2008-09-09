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

import cytoscape.randomnetwork.gui.*;
import java.awt.Toolkit;
import cytoscape.plugin.*;
import cytoscape.*;
import cytoscape.util.*;
import java.awt.event.*;
import javax.swing.*;
import cytoscape.visual.*;
import cytoscape.visual.calculators.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;



/**
 *  This class defines the Random Network Plugin.
 *  Which enables users to create random networks either from
 *  scratch or by randomizing existing.  It also allows comparisons
 *  to be made between a round of random networks and an existing network
 *
 * @author Patrick J. McSweeney
 * @version 1.0
 */
public class RandomNetworkPlugin extends CytoscapePlugin  implements PropertyChangeListener 
{

	GenerateRandomAction grAction;
	RandomizeExistingAction reAction;
	CompareRandomAction crAction;
		
	/**	
	*  RandomNetworkPlugin Constructor
	*/
	public RandomNetworkPlugin()	
	{
	
		//Add an item to the plugin menu
		grAction = new GenerateRandomAction();
		reAction = new RandomizeExistingAction();
		crAction = new CompareRandomAction();
		grAction.setPreferredMenu("Plugins.Random Networks");
		reAction.setPreferredMenu("Plugins.Random Networks");
		crAction.setPreferredMenu("Plugins.Random Networks");


		Cytoscape.getDesktop().getCyMenus().addAction(grAction);
		Cytoscape.getDesktop().getCyMenus().addAction(reAction);
		Cytoscape.getDesktop().getCyMenus().addAction(crAction);		
		
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);


		CyNetwork net = Cytoscape.getCurrentNetwork();
		
		if(net == Cytoscape.getNullNetwork())
		{
			reAction.setEnabled(false);
			crAction.setEnabled(false);
		}
		
	}
		
		
	/**
	*
	*/
	  public void propertyChange(PropertyChangeEvent event) 
	  {
	  
		if(event.getPropertyName() == Cytoscape.NETWORK_CREATED) 
		{
			reAction.setEnabled(true);
			crAction.setEnabled(true);
		}


		CyNetwork net = Cytoscape.getCurrentNetwork();
			
		if(net == Cytoscape.getNullNetwork())
		{
			reAction.setEnabled(false);
			crAction.setEnabled(false);
		}
	}

				
	/**
	 *	This class creates the main window for our plugin
	 */
	class RandomNetworkFrame extends JDialog// implements ActionListener
	{
		
		
	
		/**
		 *  The default constructor for this class	
		 */
		public RandomNetworkFrame() { 
			super(Cytoscape.getDesktop(), "     Random Networks");
			initComponents();
		}

		/**
		 *  Initialize the swing components
		 */
		private void initComponents() {
			
		
			//Default is to dispose on close
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		
	
		}
		
		
		private void start(RandomNetworkPanel pPanel)
		{

			MainPanel panel = new MainPanel(pPanel, this);
			
			//add the main pane to our tabbed panel
			getContentPane().add(panel);
		
			//make it visible
			pack();
			setLocationRelativeTo(null); //should center on screen
			//show();

			setVisible(true);
		

		}
	}


	/**
	 *  The action which brings up our dialog
	 */
	class GenerateRandomAction extends CytoscapeAction {

		/**
		 * Default constructor
		 */
		public GenerateRandomAction() {
			super("Generate Random Network");
		}

		/**
	 	 *  When our item is create a new frame
		 */
		public void actionPerformed(ActionEvent ae) {
		
			RandomNetworkFrame frame = new RandomNetworkFrame();
			frame.start(new GenerateRandomPanel(0)); 		
		}
	}
	
	/**
	 *  The action which brings up our dialog
	 */
		class RandomizeExistingAction extends CytoscapeAction {

		/**
		 * Default constructor
		 */
		public RandomizeExistingAction() {
			super("Randomize Existing Network");
		}

		/**
	 	 *  When our item is create a new frame
		 */
		public void actionPerformed(ActionEvent ae) {
		
			RandomNetworkFrame frame = new RandomNetworkFrame();
			frame.start(new RandomizeExistingPanel(0)); 		
		}
	}

/**
	 *  The action which brings up our dialog
	 */
	class CompareRandomAction extends CytoscapeAction {

		/**
		 * Default constructor
		 */
		public CompareRandomAction() {
			super("Compare to Random Network");
		}

		/**
	 	 *  When our item is create a new frame
		 */
		public void actionPerformed(ActionEvent ae) {
		
			RandomNetworkFrame frame = new RandomNetworkFrame();
			frame.start(new RandomComparisonPanel()); 		
		}
	}


}
