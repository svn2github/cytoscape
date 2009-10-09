package org.genmapp.golayout;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.Semantics;
import cytoscape.ding.CyGraphAllLOD;
import cytoscape.ding.DingNetworkView;
import cytoscape.groups.CyGroup;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.LayoutProperties;
import cytoscape.view.CyDesktopManager;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;

/**
 * PartitionAlgorithm
 */
public class PartitionAlgorithm extends AbstractLayout implements
		PropertyChangeListener {
	double distanceBetweenNodes = 80.0d;
	LayoutProperties layoutProperties = null;

	protected static String layoutName = "force-directed";
	private ArrayList<Object> nodeAttributeValues = new ArrayList();
	// private Object[] layoutNames = null;
	protected static String attributeName = "annotation.GO BIOLOGICAL_PROCESS";
	private HashMap<Object, List<CyNode>> attributeValueNodeMap;
	private List<CyNetworkView> views = new ArrayList<CyNetworkView>();
	private List<CyGroup> groups = new ArrayList<CyGroup>();
	private List<CyNode> unconnectedNodes = new ArrayList<CyNode>();
	protected static int NETWORK_LIMIT_MIN = 5; // min to show network
	protected static int NETWORK_LIMIT_MAX = 200;
	private static final int SUBNETWORK_COUNT_WARNING = 30; // will warn if >
	protected static final String SUBNETWORK_CONNECTIONS = "_subnetworkConnections";
	protected static final String SUBNETWORK_SIZE = "_subnetworkSize";

	/**
	 * Creates a new PartitionAlgorithm object.
	 */
	public PartitionAlgorithm() {
		super();

		// support fit-to-screen when maximizing tiled subnetworks
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
				.addPropertyChangeListener(
						CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
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
		// nothing here... see GOLayout
	}

	/**
	 * Returns the short-hand name of this algorithm NOTE: is related to the
	 * menu item order
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
		return "Partition Only";
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
	 * Sets the attribute to use for the weights
	 * 
	 * @param value
	 *            the name of the attribute
	 */
	public void setLayoutAttribute(String value) {
		attributeName = value;
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

	public void buildSubnetworkOverview(CyNetwork net) {

		CyNetwork overview_network = Cytoscape.createNetwork(new int[0],
				new int[0], "Overview", net);
		CyNetworkView overview_view = Cytoscape.getNetworkView(overview_network
				.getIdentifier());

		CyAttributes nAttributes = Cytoscape.getNodeAttributes();
		CyAttributes eAttributes = Cytoscape.getEdgeAttributes();

		int[] edges = net.getEdgeIndicesArray();

		for (int edgeInt : edges) {
			int nodeInt1 = Cytoscape.getRootGraph().getEdgeSourceIndex(edgeInt);
			int nodeInt2 = Cytoscape.getRootGraph().getEdgeTargetIndex(edgeInt);
			String node1 = net.getNode(nodeInt1).getIdentifier();
			String node2 = net.getNode(nodeInt2).getIdentifier();
			if (nAttributes.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST) {
				List<Object> nodePartitionList1 = nAttributes.getListAttribute(
						node1, attributeName);
				List<Object> nodePartitionList2 = nAttributes.getListAttribute(
						node2, attributeName);

				for (Object np1 : nodePartitionList1) {
					for (Object np2 : nodePartitionList2) {
						// skip if same partition
						if (np1.toString().equalsIgnoreCase(np2.toString()))
							continue;
						// create nodes and edges
						CyNode cn1 = Cytoscape.getCyNode(np1.toString(), true);
						CyNode cn2 = Cytoscape.getCyNode(np2.toString(), true);
						CyEdge ce = Cytoscape.getCyEdge(cn1, cn2,
								Semantics.INTERACTION, "subnetworkInteraction",
								true);
						overview_network.addNode(cn1);
						overview_network.addNode(cn2);
						overview_network.addEdge(ce);
						if (null != eAttributes.getDoubleAttribute(ce
								.getIdentifier(), SUBNETWORK_CONNECTIONS)) {
							eAttributes
									.setAttribute(ce.getIdentifier(),
											SUBNETWORK_CONNECTIONS,
											eAttributes.getDoubleAttribute(ce
													.getIdentifier(),
													SUBNETWORK_CONNECTIONS) + 1.0);

						} else { // first pass; thus create attribute
							//use double so you can passThrough to Edge Line Width
							eAttributes.setAttribute(ce.getIdentifier(), SUBNETWORK_CONNECTIONS, 1.0);
						}
					}
				}

			} else { // TODO: fix assumption of String!

				String nodeRegion1 = nAttributes.getStringAttribute(node1,
						attributeName);
				String nodeRegion2 = nAttributes.getStringAttribute(node2,
						attributeName);
			}
		}

		Iterator<CyNode> nodeIt = overview_network.nodesIterator();
		while (nodeIt.hasNext()) {
			String nodeId = nodeIt.next().getIdentifier();
			nAttributes.setAttribute(nodeId, SUBNETWORK_SIZE,
					attributeValueNodeMap.get(nodeId).size());
		}

		Cytoscape.getVisualMappingManager().setVisualStyle(
				PartitionNetworkVisualStyleFactory.attributeName);
		CyLayoutAlgorithm layout = CyLayouts.getLayout("degree-circle");
		layout.doLayout(overview_view);
		views.add(0, overview_view);
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
		// System.out.println("Got nodes for attributeValue: " + attributeValue
		// + " = " + nodes.size());
		if (nodes == null) {
			return;
		}

		CyNetwork new_network = Cytoscape.createNetwork(nodes, current_network
				.getConnectingEdges(new ArrayList(nodes)),
		// CyNetworkNaming.getSuggestedSubnetworkTitle(current_network),
				attributeValue, // for network title
				current_network, (nodes.size() >= NETWORK_LIMIT_MIN)
						&& nodes.size() <= NETWORK_LIMIT_MAX);
		// optional create network view

		CyNetworkView new_view = Cytoscape.getNetworkView(new_network
				.getIdentifier());

		if (new_view == Cytoscape.getNullNetworkView()) {
			return;
		}

		views.add(new_view);

		// listen for window maximize or restore.
		Cytoscape.getDesktop().getNetworkViewManager().getInternalFrame(
				new_view).addPropertyChangeListener(
				JInternalFrame.IS_MAXIMUM_PROPERTY, this);

		// apply layout
		if (current_network_view != Cytoscape.getNullNetworkView()) {

			// CyLayoutAlgorithm layout =
			// CyLayouts.getLayout("force-directed");
			// System.out.println("Layout: " + new_view.getTitle() +" :: "+
			// layoutName);

			CyLayoutAlgorithm layout = CyLayouts.getLayout(layoutName);
			layout.doLayout(new_view);

		}

		// set graphics level of detail
		((DingNetworkView) new_view).setGraphLOD(new CyGraphAllLOD());

		if (PartitionNetworkVisualStyleFactory.attributeName != null) {
			Cytoscape.getVisualMappingManager().setVisualStyle(
					PartitionNetworkVisualStyleFactory.attributeName);
		}
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
					// Cytoscape.setCurrentNetworkView(view.getIdentifier());
					view.fitContent();
				}

			}
		});
	}

	/**
	 * The layout protocol...
	 */
	public void construct() {

		// if "(none)" was selected in setting, then skip partitioning
		if (null == attributeName) {

			// create visual style if set
			if (PartitionNetworkVisualStyleFactory.attributeName != null) {
				// if a non-null attribute has been selection for node coloring
				// and if not running PartitionOnly
				GOLayout.createVisualStyle(Cytoscape.getCurrentNetworkView());
			}

			// pass on to CellAlgorithm
			CyLayoutAlgorithm layout = CyLayouts.getLayout(layoutName);
			layout.doLayout(Cytoscape.getCurrentNetworkView());
		} else {

			// Our node map needs to be reset in case we have a new network
			attributeValueNodeMap = new HashMap<Object, List<CyNode>>();

			taskMonitor.setStatus("Partitioning the network by "
					+ attributeName);
			taskMonitor.setPercentCompleted(1);

			// Reset partition groups
			// TODO: CyGroup bug: can't get rid of groups created in previous
			// session!
			// for (CyGroup cg : CyGroupManager.getGroupList()) {
			// CyGroupManager.removeGroup(cg);
			// }

			nodeAttributeValues = setupNodeAttributeValues();
			// warn before building more than 100 subnetworks;
			int response = JOptionPane.YES_OPTION;
			if (nodeAttributeValues.size() > SUBNETWORK_COUNT_WARNING) {
				// TODO: add dialog to continue
				response = JOptionPane
						.showConfirmDialog(
								(java.awt.Window) taskMonitor,
								"Building over "
										+ SUBNETWORK_COUNT_WARNING
										+ " subnetworks may take a while. Are you sure you want to proceed?",
								"Warning", JOptionPane.YES_NO_OPTION);
			}
			if (JOptionPane.YES_OPTION == response) {

				populateNodes(attributeName);

				if (PartitionNetworkVisualStyleFactory.attributeName != null) {
					// if a non-null attribute has been selection for node
					// coloring
					// and if not running Floorplan Only
					GOLayout.createVisualStyle(Cytoscape
							.getCurrentNetworkView());
				}

				Set<Object> attributeValues = attributeValueNodeMap.keySet();
				CyNetwork net = Cytoscape.getCurrentNetwork();
				CyNetworkView view = Cytoscape.getNetworkView(net
						.getIdentifier());

				int nbrProcesses = attributeValues.size();
				int count = 0;

				//		
				for (Object val : attributeValues) {
					count++;
					taskMonitor.setPercentCompleted((100 * count)
							/ nbrProcesses);
					taskMonitor.setStatus("building subnetwork for " + val);
					buildSubNetwork(net, val.toString());
				}
				buildSubnetworkOverview(net);
				tileNetworkViews(); // tile and fit content in each view

			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Possible solution for large label drawing bug on tile view
		if (evt.getPropertyName().equals(JInternalFrame.IS_MAXIMUM_PROPERTY)) {
			CyNetworkView cnv = Cytoscape.getCurrentNetworkView();
			cnv.fitContent();
			cnv.setZoom(cnv.getZoom() * 0.9);
		}
	}

}
