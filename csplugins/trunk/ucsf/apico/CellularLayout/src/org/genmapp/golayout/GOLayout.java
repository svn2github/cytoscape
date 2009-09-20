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
	
	/**
	 * The constructor registers our layout algorithm. The CyLayouts mechanism
	 * will worry about how to get it in the right menu, etc.
	 */
	public GOLayout() {

		CyLayouts.addLayout(new GOLayoutAlgorithm(), "GO Layout");
		CyLayouts.addLayout(new PartitionAlgorithm(), "GO Layout");
		CyLayouts.addLayout(new CellAlgorithm(), "GO Layout");
	}
	
	public static void createVisualStyle(CyNetworkView view){
		PartitionNetworkVisualStyleFactory.createVisualStyle(view);

	}

	public class GOLayoutAlgorithm extends AbstractLayout {
//		double distanceBetweenNodes = 80.0d;
		LayoutProperties layoutProperties = null;

		/**
		 * Creates a new CellularLayoutAlgorithm object.
		 */
		public GOLayoutAlgorithm() {
			super();
			layoutProperties = new LayoutProperties(getName());
			layoutProperties
			.add(new Tunable("attributePartition",
					"The attribute to use for partitioning",
					Tunable.NODEATTRIBUTE, PartitionAlgorithm.attributeName,
					(Object) getInitialAttributeList(), (Object) null, 0));
			layoutProperties
			.add(new Tunable("attributeLayout",
					"The attribute to use for the layout",
					Tunable.NODEATTRIBUTE, CellAlgorithm.attributeName,
					(Object) getInitialAttributeList(), (Object) null, 0));
			layoutProperties
			.add(new Tunable("attributeNodeColor",
					"The attribute to use for node color",
					Tunable.NODEATTRIBUTE, PartitionNetworkVisualStyleFactory.attributeName,
					(Object) getInitialAttributeList(), (Object) null, 0));


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
			Tunable t = layoutProperties.get("attributePartition");
			if ((t != null) && (t.valueChanged() || force)) {
				String newValue = (String) t.getValue();
				PartitionAlgorithm.attributeName = newValue;
			}

			t = layoutProperties.get("attributeLayout");
			if ((t != null) && (t.valueChanged() || force)) {
				String newValue = (String) t.getValue();
				CellAlgorithm.attributeName = newValue;
			}
			
			t = layoutProperties.get("attributeNodeColor");
			if ((t != null) && (t.valueChanged() || force)) {
				String newValue = (String) t.getValue();
				PartitionNetworkVisualStyleFactory.attributeName = newValue;
			}
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
		 * NOTE: is related to the menu item order
		 * 
		 * @return short-hand name
		 */
		public String getName() {
			return "0-golayout";
		}

		/**
		 * Returns the user-visible name of this layout
		 * 
		 * @return user visible name
		 */
		public String toString() {
			return "GO Layout";
		}

		/**
		* Gets the Task Title.
		*
		* @return human readable task title.
		*/
		public String getTitle() {
			return new String("GO Layout");
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
			PartitionAlgorithm.layoutName = "cell-layout";
			CyLayoutAlgorithm layout = CyLayouts.getLayout("partition");
			layout.doLayout(Cytoscape.getCurrentNetworkView(), taskMonitor);
		}
	}
}
