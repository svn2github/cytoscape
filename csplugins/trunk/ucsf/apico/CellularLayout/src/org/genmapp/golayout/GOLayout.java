package org.genmapp.golayout;

import java.awt.GridLayout;

import javax.swing.JPanel;

import cytoscape.Cytoscape;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;

public class GOLayout extends CytoscapePlugin {
	
	PartitionAlgorithm pa = new PartitionAlgorithm();
	
	/**
	 * The constructor registers our layout algorithm. The CyLayouts mechanism
	 * will worry about how to get it in the right menu, etc.
	 */
	public GOLayout() {

		CyLayouts.addLayout(new CellAlgorithm(), "GO Layout");
		CyLayouts.addLayout(new GOLayoutAlgorithm(), "GO Layout");
		CyLayouts.addLayout(pa, "GO Layout");
	}
	
	public static void createVisualStyle(CyNetworkView view){
		PartitionNetworkVisualStyleFactory.createVisualStyle(view);

	}

	public class GOLayoutAlgorithm extends AbstractLayout {
		double distanceBetweenNodes = 80.0d;
		LayoutProperties layoutProperties = null;

		/**
		 * Creates a new CellularLayoutAlgorithm object.
		 */
		public GOLayoutAlgorithm() {
			super();
			layoutProperties = new LayoutProperties(getName());
			layoutProperties.add(new Tunable("nodeSpacing",
					"Spacing between nodes", Tunable.DOUBLE, new Double(80.0)));

			/*
			 * We've now set all of our tunables, so we can read the property
			 * file now and adjust as appropriate
			 */
			layoutProperties.initializeProperties();

			/*
			 * Finally, update everything. We need to do this to update any of
			 * our values based on what we read from the property file
			 */
			updateSettings(true);

		}

		/**
		 * External interface to update our settings
		 */
		public void updateSettings() {
			updateSettings(true);
		}

		/**
		 * Signals that we want to update our internal settings
		 * 
		 * @param force
		 *            force the settings to be updated, if true
		 */
		public void updateSettings(boolean force) {
			layoutProperties.updateValues();
			Tunable t = layoutProperties.get("nodeSpacing");
			if ((t != null) && (t.valueChanged() || force))
				distanceBetweenNodes = ((Double) t.getValue()).doubleValue();
		}

		/**
		 * Reverts our settings back to the original.
		 */
		public void revertSettings() {
			layoutProperties.revertProperties();
		}

		public LayoutProperties getSettings() {
			return layoutProperties;
		}

		/**
		 * Returns the short-hand name of this algorithm
		 * 
		 * @return short-hand name
		 */
		public String getName() {
			return "go-layout";
		}

		/**
		 * Returns the user-visible name of this layout
		 * 
		 * @return user visible name
		 */
		public String toString() {
			return "Run All";
		}

		/**
		* Gets the Task Title.
		*
		* @return human readable task title.
		*/
		public String getTitle() {
			return new String("GO Layout: Do All");
		}
		
		/**
		 * Return true if we support performing our layout on a limited set of
		 * nodes
		 * 
		 * @return true if we support selected-only layout
		 */
		public boolean supportsSelectedOnly() {
			return false;
		}

		/**
		 * Returns the types of node attributes supported by this algorithm.
		 * 
		 * @return the list of supported attribute types, or null if node
		 *         attributes are not supported
		 */
		public byte[] supportsNodeAttributes() {
			return null;
		}

		/**
		 * Returns the types of edge attributes supported by this algorithm.
		 * 
		 * @return the list of supported attribute types, or null if edge
		 *         attributes are not supported
		 */
		public byte[] supportsEdgeAttributes() {
			return null;
		}

		/**
		 * Returns a JPanel to be used as part of the Settings dialog for this
		 * layout algorithm.
		 * 
		 */
		public JPanel getSettingsPanel() {
			JPanel panel = new JPanel(new GridLayout(0, 1));
			panel.add(layoutProperties.getTunablePanel());

			return panel;
		}

		/**
		 * The layout protocol...
		 */
		public void construct() {
		    pa.setLayoutName("cell-layout");
			CyLayoutAlgorithm layout = CyLayouts.getLayout("partition");
			layout.doLayout(Cytoscape.getCurrentNetworkView(), taskMonitor);
		}
	}
}
