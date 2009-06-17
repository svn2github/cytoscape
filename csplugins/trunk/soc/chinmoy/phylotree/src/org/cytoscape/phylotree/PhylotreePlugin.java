package org.cytoscape.phylotree;


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
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.Rectangle;

import java.util.Iterator;
/**
 * 
 */
public class PhylotreePlugin extends CytoscapePlugin {
	
	private int groupcount = 2;
	private LayoutProperties layoutProperties;

	
	/**
	 * 
	 */
	public PhylotreePlugin() {
		CyLayouts.addLayout(new MyLayout(), "PhyloTree Layouts");
	}
	
	class MyLayout extends AbstractLayout{
		/**
		 * Creates a new layout object.
		 */
		public MyLayout() {
			super();
			layoutProperties = new LayoutProperties(getName());
			initialize_properties();
		}
		
		protected void initialize_properties() {
			layoutProperties.add(new Tunable("groupcount",
			                                 "Number of random groups ",
			                                 Tunable.INTEGER, new Integer(2)));

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

			Tunable t = layoutProperties.get("groupcount");

			if ((t != null) && (t.valueChanged() || force))
				groupcount = ((Integer) t.getValue()).intValue();
		}

		/**
		 * Get the settings panel for this layout
		 */
		public JPanel getSettingsPanel() {
			JPanel panel = new JPanel(new GridLayout(0, 1));
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

	
			System.out.println("do layout here: groupcount = "+ groupcount);

			if (groupcount <2) {
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
			return "PhyloTree Layout";
		}

		/**
		 * toString is used to get the user-visible name
		 * of the layout
		 */
		public  String toString(){
			return "PhyloTree Layout";
		}
	}
}
