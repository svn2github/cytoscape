package org.genmapp.golayout;

import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.ding.CyGraphAllLOD;
import cytoscape.ding.DingNetworkView;
import cytoscape.groups.CyGroup;
import cytoscape.groups.CyGroupManager;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.view.CyDesktopManager;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

/**
 * PartitionAlgorithm
 */
public class PartitionAlgorithm extends AbstractLayout implements
		PropertyChangeListener, WindowListener, WindowStateListener {
	double distanceBetweenNodes = 80.0d;
	LayoutProperties layoutProperties = null;

	private String layoutName = null;
	private ArrayList<Object> nodeAttributeValues = new ArrayList();
	private static final String attributeName = "annotation.GO BIOLOGICAL_PROCESS";
	private HashMap<Object, List<CyNode>> attributeValueNodeMap = new HashMap<Object, List<CyNode>>();
	private List<CyNetworkView> views = new ArrayList<CyNetworkView>();
	private List<CyGroup> groups = new ArrayList<CyGroup>();
	private List<CyNode> unconnectedNodes = new ArrayList<CyNode>();
	private int networkViewThreshhold = 5; // necessary size to show network

	// view in the tiling

	/**
	 * Creates a new PartitionAlgorithm object.
	 */
	public PartitionAlgorithm() {
		super();

		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);

		layoutProperties = new LayoutProperties(getName());
		layoutProperties.add(new Tunable("layoutName", "Layout to perform",
				Tunable.STRING, "grid"));
		// We've now set all of our tunables, so we can read the property
		// file now and adjust as appropriate
		layoutProperties.initializeProperties();

		// Finally, update everything. We need to do this to update
		// any of our values based on what we read from the property file
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
		Tunable t2 = layoutProperties.get("layoutName");
		if ((t2 != null) && (t2.valueChanged() || force))
			this.layoutName = t2.getValue().toString();
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
		return "partition";
	}

	/**
	 * Returns the user-visible name of this layout
	 * 
	 * @return user visible name
	 */
	public String toString() {
		return "Partition";
	}

	/**
	 * Return true if we support performing our layout on a limited set of nodes
	 * 
	 * @return true if we support selected-only layout
	 */
	public boolean supportsSelectedOnly() {
		return false;
	}

	/**
	 * Returns the types of node attributes supported by this algorithm.
	 * 
	 * @return the list of supported attribute types, or null if node attributes
	 *         are not supported
	 */
	public byte[] supportsNodeAttributes() {
		return null;
	}

	/**
	 * Returns the types of edge attributes supported by this algorithm.
	 * 
	 * @return the list of supported attribute types, or null if edge attributes
	 *         are not supported
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
	 */
	public ArrayList<Object> setupNodeAttributeValues() {
		CyAttributes attribs = Cytoscape.getNodeAttributes();
		Map attrMap = CyAttributesUtils.getAttribute(attributeName, attribs);
		Collection values = attrMap.values();
		ArrayList<Object> uniqueValueList = new ArrayList<Object>();

		// key will be a List attribute value, so we need to pull out individual
		// list items
		if (attribs.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST) {
			for (Object o : values) {
				List oList = (List) o;
				for (int j = 0; j < oList.size(); j++) {
					Object jObj = oList.get(j);
					if (jObj != null) {
						if (!uniqueValueList.contains(jObj)) {
							uniqueValueList.add(jObj);
						}
					}
				}
			}
		}

		return uniqueValueList;
	}

	public void buildMetaNodeView(CyNetwork net) {
		// create an 'uber' view of the network as group nodes
		CyNetwork group_network = Cytoscape.createNetwork(net.nodesList(), net
				.edgesList(), "overView", net);
		CyNetworkView group_view = Cytoscape.getNetworkView(group_network
				.getIdentifier());

		for (Object val : attributeValueNodeMap.keySet()) {
			List<CyNode> memberNodes = attributeValueNodeMap.get(val);
			CyGroup group = CyGroupManager.createGroup(val.toString(),
					memberNodes, null);
			groups.add(group);
		}
		// group unconnected nodes
		CyGroup group = CyGroupManager.createGroup("unConnected",
				unconnectedNodes, null);
		groups.add(group);

		// now loop through all groups and set state to 'collapsed'
		for (CyGroup g : groups) {
			g.setState(2);
			CyGroupManager.setGroupViewer(g, "metaNode", group_view, true); // set
			// this
			// false
			// later
			// for
			// efficiency
		}

		CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
		layout.doLayout(group_view);
		Cytoscape.getVisualMappingManager().setVisualStyle(
				PartitionNetworkVisualStyleFactory.PartitionNetwork_VS);

	}

	public void populateNodes(String attributeName) {

		CyAttributes attribs = Cytoscape.getNodeAttributes();
		Iterator<CyNode> it = Cytoscape.getCurrentNetwork().nodesIterator();
		List<CyNode> selectedNodes = null;
		List<CyNode> unassignedNodes = Cytoscape.getCurrentNetwork()
				.nodesList();

		boolean valueFound = false;

		while (it.hasNext()) {

			valueFound = false;
			CyNode node = it.next();

			// assign unconnected nodes to a special category and move on
			int[] edges = Cytoscape.getCurrentNetwork()
					.getAdjacentEdgeIndicesArray(node.getRootGraphIndex(),
							true, true, true);
			if (edges.length <= 0) {
				unconnectedNodes.add(node);
				continue;
			}

			String val = null;
			String terms[] = new String[1];
			// add support for parsing List type attributes
			if (attribs.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST) {
				List valList = attribs.getListAttribute(node.getIdentifier(),
						attributeName);
				// System.out.println ("Got values for node: " + node + " = " +
				// valList);
				// iterate through all elements in the list
				if (valList != null && valList.size() > 0) {
					terms = new String[valList.size()];
					for (int i = 0; i < valList.size(); i++) {
						Object o = valList.get(i);
						terms[i] = o.toString();
					}
				}
				val = join(terms);
			} else {
				String valCheck = attribs.getStringAttribute(node
						.getIdentifier(), attributeName);
				if (valCheck != null && !valCheck.equals("")) {
					val = valCheck;
				}
			}

			if ((!(val == null) && (!val.equals("null")) && (val.length() > 0))) {

				for (Object o : nodeAttributeValues) {
					// System.out.println ("checking node value " + val +
					// " against " + o.toString());
					if (val.indexOf(o.toString()) >= 0) {
						selectedNodes = attributeValueNodeMap.get(o);
						if (selectedNodes == null) {
							selectedNodes = new ArrayList<CyNode>();
						}
						if (!selectedNodes.contains(node)) {
							selectedNodes.add(node);
							attributeValueNodeMap.put(o.toString(),
									selectedNodes);
							valueFound = true;
						}
						// System.out.println ("selected nodes for value: " +
						// o.toString() + " = " +
						// selectedNodes);
					}
				}
			}
			if (!valueFound)
			// put this node in 'unassigned' category
			// but do we need to treat separately the case where there is a
			// value not in the template
			// from the case where there is no value?
			{
				selectedNodes = attributeValueNodeMap.get("unassigned");
				if (selectedNodes == null) {
					selectedNodes = new ArrayList<CyNode>();
				}
				if (!selectedNodes.contains(node)) {
					selectedNodes.add(node);
					attributeValueNodeMap.put("unassigned", selectedNodes);
					valueFound = true;
				}
			}
		}
	}

	private String join(String values[]) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			buf.append(values[i]);
			if (i < values.length - 1) {
				buf.append(", ");
			}
		}
		return buf.toString();
	}

	/**
	 * build a subnetwork for selected nodes leverages from
	 * cytoscape.actions.NewWindowSelectedNodesOnlyAction
	 * 
	 * @param current_network
	 */
	public void buildSubNetwork(CyNetwork current_network, String attributeValue) {

		CyNetworkView current_network_view = null;

		if (Cytoscape.viewExists(current_network.getIdentifier())) {
			current_network_view = Cytoscape.getNetworkView(current_network
					.getIdentifier());
		} // end of if ()

		List nodes = attributeValueNodeMap.get(attributeValue);
		System.out.println("Got nodes for attributeValue: " + attributeValue
				+ " = " + nodes.size());
		if (nodes == null) {
			return;
		}

		CyNetwork new_network = Cytoscape.createNetwork(nodes, current_network
				.getConnectingEdges(new ArrayList(nodes)),
		// CyNetworkNaming.getSuggestedSubnetworkTitle(current_network),
				attributeValue, // for network title
				current_network, (nodes.size() >= networkViewThreshhold)
						&& nodes.size() <= 200); // optional
		// create
		// network
		// view

		// if (new_network.nodesList().size() >= networkViewThreshhold &&
		// new_network.nodesList().size() <= 200) {

		CyNetworkView new_view = Cytoscape.getNetworkView(new_network
				.getIdentifier());

		if (new_view == Cytoscape.getNullNetworkView()) {
			return;
		}

		views.add(new_view);

		// listen for window maximize or restore.
		// ((JFrame)
		// Cytoscape.getDesktop().getNetworkViewManager().getInternalFrame
		// (new_view).getContentPane()).addWindowListener(this);

		// apply layout
		if (current_network_view != Cytoscape.getNullNetworkView()) {

			// CyLayoutAlgorithm layout =
			// CyLayouts.getLayout("force-directed");
			System.out.println("Layout: " + new_view.getTitle());
			CyLayoutAlgorithm layout = CyLayouts.getLayout(this.layoutName);
			layout.doLayout(new_view);

		}

		// set graphics level of detail
		((DingNetworkView) new_view).setGraphLOD(new CyGraphAllLOD());

		Cytoscape.getVisualMappingManager().setVisualStyle(
				PartitionNetworkVisualStyleFactory.PartitionNetwork_VS);

	}

	// }

	/**
	 * layout the subnetwork views in a grid
	 */
	public void tileNetworkViews() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CyDesktopManager.arrangeFrames(CyDesktopManager.Arrange.GRID);
				// finally loop through the network views and fitContent
				for (CyNetworkView view : views) {
					Cytoscape.setCurrentNetworkView(view.getIdentifier());
					view.fitContent();
				}

			}
		});
	}

	/**
	 * The layout protocol...
	 */
	public void construct() {
		nodeAttributeValues = setupNodeAttributeValues();
		populateNodes(attributeName);

		GOLayout.createVisualStyle(Cytoscape.getCurrentNetworkView());

		Set<Object> attributeValues = attributeValueNodeMap.keySet();
		CyNetwork net = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getNetworkView(net.getIdentifier());

		//		
		for (Object val : attributeValues) {
			buildSubNetwork(net, val.toString());
		}

		buildMetaNodeView(net);
		tileNetworkViews(); // tile and fit content in each view

	}

	/**
	 * @return the layoutName
	 */
	public String getLayoutName() {
		return layoutName;
	}

	/**
	 * @param layoutName
	 *            the layoutName to set
	 */
	public void setLayoutName(String layoutName) {
		this.layoutName = layoutName;
	}

	public void windowStateChanged(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("WindowListener method called: windowChanged.");
	}

	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowClosing(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("WindowListener method called: windowDeiconified.");
	}

	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		System.out.println("WindowListener method called: windowIconified.");

	}

	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Possible solution for large label drawing bug on tile view
//		if (evt.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
//
//			for (CyNetworkView nv : views) {
//				nv.redrawGraph(true, true);
//			}
//		}
	}

}
