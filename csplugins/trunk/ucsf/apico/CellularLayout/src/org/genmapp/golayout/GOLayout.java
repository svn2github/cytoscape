package org.genmapp.golayout;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;

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
		CyLayouts.addLayout(new PartitionAlgorithm(), null);
		CyLayouts.addLayout(new CellAlgorithm(), null);
	}

	public static void createVisualStyle(CyNetworkView view) {
		PartitionNetworkVisualStyleFactory.createVisualStyle(view);

	}

	public class GOLayoutAlgorithm extends AbstractLayout {

		protected static final String LAYOUT_NAME = "0-golayout";
		LayoutProperties layoutProperties = null;
		private static final String HELP = "GOLayout Help";

		/**
		 * Creates a new CellularLayoutAlgorithm object.
		 */
		public GOLayoutAlgorithm() {
			super();
			layoutProperties = new LayoutProperties(getName());
			layoutProperties.add(new Tunable("global", "Global Settings",
					Tunable.GROUP, new Integer(3)));
			layoutProperties.add(new Tunable("attributePartition",
					"The attribute to use for partitioning",
					Tunable.NODEATTRIBUTE, PartitionAlgorithm.attributeName,
					(Object) getInitialAttributeList(), (Object) null, 0));
			layoutProperties.add(new Tunable("attributeLayout",
					"The attribute to use for the layout",
					Tunable.NODEATTRIBUTE, CellAlgorithm.attributeName,
					(Object) getInitialAttributeList(), (Object) null, 0));
			layoutProperties.add(new Tunable("attributeNodeColor",
					"The attribute to use for node color",
					Tunable.NODEATTRIBUTE,
					PartitionNetworkVisualStyleFactory.attributeName,
					(Object) getInitialAttributeList(), (Object) null, 0));
			layoutProperties.add(new Tunable("partition", "Partition Settings",
					Tunable.GROUP, new Integer(2)));
			layoutProperties.add(new Tunable("partitionMin",
					"Don't show subnetworks with fewer nodes than",
					Tunable.INTEGER, PartitionAlgorithm.NETWORK_LIMIT_MIN));
			layoutProperties.add(new Tunable("partitionMax",
					"Don't show subnetworks with more nodes than",
					Tunable.INTEGER, PartitionAlgorithm.NETWORK_LIMIT_MAX));
			layoutProperties.add(new Tunable("floorplan", "Floorplan Settings",
					Tunable.GROUP, new Integer(2)));
			layoutProperties.add(new Tunable("nodeSpacing",
					"Spacing between nodes", Tunable.DOUBLE,
					CellAlgorithm.distanceBetweenNodes));
			layoutProperties.add(new Tunable("pruneEdges",
					"Prune cross-region edges?", Tunable.BOOLEAN, false));

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
			
			// Add help menu item
			JMenuItem getHelp = new JMenuItem(HELP);
			getHelp.setToolTipText("Open online help for GOLayout");
			GetHelpListener getHelpListener = new GetHelpListener();
			getHelp.addActionListener(getHelpListener);
			Cytoscape.getDesktop().getCyMenus().getHelpMenu().add(getHelp);

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
				if (newValue.equals("(none)")) {
					PartitionAlgorithm.attributeName = null;
				} else {
					PartitionAlgorithm.attributeName = newValue;
				}
			}

			t = layoutProperties.get("attributeLayout");
			if ((t != null) && (t.valueChanged() || force)) {
				String newValue = (String) t.getValue();
				if (newValue.equals("(none)")) {
					CellAlgorithm.attributeName = null;
				} else {
					CellAlgorithm.attributeName = newValue;
				}
			}

			t = layoutProperties.get("attributeNodeColor");
			if ((t != null) && (t.valueChanged() || force)) {
				String newValue = (String) t.getValue();
				if (newValue.equals("(none)")) {
					PartitionNetworkVisualStyleFactory.attributeName = null;
				} else {
					PartitionNetworkVisualStyleFactory.attributeName = newValue;
				}
			}

			t = layoutProperties.get("partitionMin");
			if ((t != null) && (t.valueChanged() || force))
				PartitionAlgorithm.NETWORK_LIMIT_MIN = ((Integer) t.getValue())
						.intValue();

			t = layoutProperties.get("partitionMax");
			if ((t != null) && (t.valueChanged() || force))
				PartitionAlgorithm.NETWORK_LIMIT_MAX = ((Integer) t.getValue())
						.intValue();

			t = layoutProperties.get("nodeSpacing");
			if ((t != null) && (t.valueChanged() || force))
				CellAlgorithm.distanceBetweenNodes = ((Double) t.getValue())
						.doubleValue();

			t = layoutProperties.get("pruneEdges");
			if ((t != null) && (t.valueChanged() || force))
				CellAlgorithm.pruneEdges = ((Boolean) t.getValue())
						.booleanValue();

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
		 * Returns the short-hand name of this algorithm NOTE: is related to the
		 * menu item order
		 * 
		 * @return short-hand name
		 */
		public String getName() {
			return LAYOUT_NAME;
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
		 * 
		 * Add "(none)" to the list
		 * 
		 * @returns List of our "special" weights
		 */
		public List<String> getInitialAttributeList() {
			ArrayList<String> attList = new ArrayList<String>();
			attList.add("(none)");

			return attList;
		}

		/**
		 * The layout protocol...
		 */
		public void construct() {
			if (null != CellAlgorithm.attributeName) {
				PartitionAlgorithm.layoutName = CellAlgorithm.LAYOUT_NAME;
			}
			CyLayoutAlgorithm layout = CyLayouts.getLayout("partition");
			layout.doLayout(Cytoscape.getCurrentNetworkView(), taskMonitor);
		}
	}
}

/**
 * This class direct a browser to the help manual web page.
 */
class GetHelpListener implements ActionListener {
	private String helpURL = "http://genmapp.org/GOLayout/GOLayout.html";

	public void actionPerformed(ActionEvent ae) {
		cytoscape.util.OpenBrowser.openURL(helpURL);
	}

}

