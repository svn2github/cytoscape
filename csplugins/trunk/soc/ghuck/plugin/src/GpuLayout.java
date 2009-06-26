/**************************************************************************************
Copyright (C) Apeksha Godiyal, 2008
Copyright (C) Gerardo Huck, 2009


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

See licence.h for more information.
**************************************************************************************/


import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;

import giny.model.GraphPerspective;
import giny.model.Node;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.Iterator;








/**
 * This plugin provides a GPU assited graph layout utility by calling CUDA C++ code
 */
public class GpuLayout extends CytoscapePlugin {
	
    private int coarseGraphSize         = 50;
    private int interpolationIterations = 50;
    private int levelConvergence        = 2;
    private int edgeLen                 = 5;
    private int initialNoIterations     = 300;

    private int groupcount = 2;

    private LayoutProperties layoutProperties;

	
	/**
	 * 
	 */
	public GpuLayout() {
	    //  Show message on screen    
	    String message = "GPU Graph Layout Loaded!\n" 
		           +  coarseGraphSize + "\n" 
		           + interpolationIterations + "\n"  
		           + levelConvergence + "\n"         
		           + edgeLen + "\n"                  
                           + initialNoIterations; 
	    // Use the CytoscapeDesktop as parent for a Swing dialog
	    JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);

	    // Add Layout to menu
	    CyLayouts.addLayout(new ForceDirected(), "GPU Assisted Layout");
	}
	
	class ForceDirected extends AbstractLayout{
		/**
		 * Creates a new layout object.
		 */
		public ForceDirected() {
			super();
			layoutProperties = new LayoutProperties(getName());
			initialize_properties();
		}
		
		protected void initialize_properties() {
			layoutProperties.add(new Tunable("", "Coarse Graph Size"           , Tunable.INTEGER, new Integer(45) ));
			layoutProperties.add(new Tunable("", "Interpolation Iterations"    , Tunable.INTEGER, new Integer(50) ));
			layoutProperties.add(new Tunable("", "Level Convergence"           , Tunable.INTEGER, new Integer(2)  ));
			layoutProperties.add(new Tunable("", "Ideal Edge Length"           , Tunable.INTEGER, new Integer(5)  ));
			layoutProperties.add(new Tunable("", "Initial Number of Iterations", Tunable.INTEGER, new Integer(300)));


			layoutProperties.initializeProperties();

			updateSettings(true);
		}

		/**
		 *  DOCUMENT ME!
		 */
		public void updateSettings() {
			updateSettings(false);
		}

		/**
		 *  DOCUMENT ME!
		 *
		 * @param force DOCUMENT ME!
		 */
		public void updateSettings(boolean force) {
			layoutProperties.updateValues();
			Tunable t;

			// Get coarseGraphSize
			t = layoutProperties.get("Coarse Graph Size");
			if ((t != null) && (t.valueChanged() || force))
			    coarseGraphSize = ((Integer) t.getValue()).intValue();

			// Get interpolationIterations
			t = layoutProperties.get("Interpolation Iterations");
			if ((t != null) && (t.valueChanged() || force))
			    interpolationIterations = ((Integer) t.getValue()).intValue();

			// Get levelConvergence
			t = layoutProperties.get("Level Convergence");
			if ((t != null) && (t.valueChanged() || force))
			    levelConvergence = ((Integer) t.getValue()).intValue();

			// Get edgeLen
			t = layoutProperties.get("Ideal Edge Length");
			if ((t != null) && (t.valueChanged() || force))
			    edgeLen = ((Integer) t.getValue()).intValue();

			// Get initialNoIterations
			t = layoutProperties.get("Initial Number of Iterations");
			if ((t != null) && (t.valueChanged() || force))
			    initialNoIterations = ((Integer) t.getValue()).intValue();
	
			//  Show message on screen    
			String message = "Preferences updated\n" 
			    +  coarseGraphSize + "\n" 
			    + interpolationIterations + "\n"  
			    + levelConvergence + "\n"         
			    + edgeLen + "\n"                  
			    + initialNoIterations; 
			// Use the CytoscapeDesktop as parent for a Swing dialog
			JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);
			
		}

		/**
		 * Get the settings panel for this layout
		 */
		public JPanel getSettingsPanel() {
			JPanel panel = new JPanel(new GridLayout(0, 5));
			panel.add(layoutProperties.getTunablePanel());

			return panel;
		}

		
		/**
		 *  DOCUMENT ME!
		 */
		public void revertSettings() {
			layoutProperties.revertProperties();
		}

		public LayoutProperties getSettings() {
			return layoutProperties;
		}

		/**
		 *  DOCUMENT ME!
		 */
		public void construct() {
			taskMonitor.setStatus("Initializing");
			initialize(); // Calls initialize_local

	
			System.out.println("do layout here: groupcount = " + groupcount);

			if (groupcount<2) {
				return;
			}

			// Get the group center X
			double[] group_center_x = new double[groupcount];
			
			for (int i=0; i<groupcount; i++) {
				group_center_x[i] = i* maxwidth/(groupcount*2); 
			}
			
			double group_width = (maxwidth/groupcount)*0.6/2;
						
			Iterator<Node> it = network.nodesIterator();
			
			int group_id = 0;
			
			while (it.hasNext()) {
				if (canceled)
					return;

				group_id = (int) Math.round((groupcount-1)*Math.random());	
				
				double x = group_center_x[group_id] + (Math.random()-0.5)*group_width;
				
				Node node = (Node) it.next();
			
				//System.out.println(group_id);
				
				networkView.getNodeView(node).setXPosition(x);
			}
		}

		private double maxwidth = 5000.0;

		/**
		 * getName is used to construct property strings
		 * for this layout.
		 */
		public  String getName() {
			return "Force Directed GPU Layout";
		}

		/**
		 * toString is used to get the user-visible name
		 * of the layout
		 */
		public  String toString(){
			return "Force Directed Layout";
		}
	}
}








