package org.cytoscape.BipartiteVisualiserPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import giny.model.Edge;
import giny.model.Node;
import giny.model.GraphPerspective;
import giny.view.EdgeView;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import ding.view.EdgeContextMenuListener;


public class BipartiteLayoutContextMenuListener
	implements EdgeContextMenuListener
{
	static final String REFERENCE_NETWORK_NAME_ATTRIB =
		"BipartiteVisualiserReferenceNetworkName"; // Also exists in DenovoPGNetworkAlignment!
	private final Map<String, CyNetwork> titleToNetworkMap;

	
	public BipartiteLayoutContextMenuListener() {
		this.titleToNetworkMap = new HashMap<String, CyNetwork>();
	}


	public void addEdgeContextMenuItems(final EdgeView edgeView,
			final JPopupMenu menu)
	{
		if (menu == null)
			return;

		final Edge edge = edgeView.getEdge();

		final Node source = edge.getSource();
		final CyNetwork network1 = (CyNetwork) source.getNestedNetwork();
		if (network1 == null)
			return;

		final Node target = edge.getTarget();
		final CyNetwork network2 = (CyNetwork) target.getNestedNetwork();
		if (network2 == null)
			return;

		final CyNetwork referenceNetwork = getReferenceNetwork(edge);
		if (referenceNetwork != null) {
			final JMenuItem createNestedNetworkSideBySideViewMenuItem =
				new JMenuItem("Create Nested Network Side-by-Side View");
			menu.add(createNestedNetworkSideBySideViewMenuItem);
			return;
		}

		final JMenu createBipartiteViewMenuItem = new JMenu(
				"Create Nested Network Side-by-Side View");
		menu.add(createBipartiteViewMenuItem);

		final JMenuItem selectReferenceNetworkMenuItem =
			new JMenuItem("Select Reference Network");
		selectReferenceNetworkMenuItem.setEnabled(false);
		createBipartiteViewMenuItem.add(selectReferenceNetworkMenuItem);
		createBipartiteViewMenuItem.addSeparator();

		// Parent network is ALWAYS current network view
		final CyNetwork parentNetwork = Cytoscape.getCurrentNetworkView()
				.getNetwork();
		getReferenceNetworkCandidates(parentNetwork);
		final SortedSet<String> sortedTitles = new TreeSet<String>(titleToNetworkMap.keySet());
		for (String networkTitle: sortedTitles) {
			final JMenuItem referenceNetworkCandidate = new JMenuItem(networkTitle);
			referenceNetworkCandidate.addActionListener(new CreateBipartiteViewAction(
					edgeView, titleToNetworkMap.get(networkTitle), network1, network2));
			createBipartiteViewMenuItem.add(referenceNetworkCandidate);
		}
	}

	
	/**
	 * Computes a set of all the networks that are not "parentNetwork" or a nested network of a
	 *          node in "parentNetwork."
	 */
	private void getReferenceNetworkCandidates(final CyNetwork parentNetwork) { 
		// Determine the set of networks that can't possible be candidates for the reference network:
		final Set<CyNetwork> verboten = new HashSet<CyNetwork>();
		verboten.add(parentNetwork);
		@SuppressWarnings("unchecked") final List<Node> nodes = parentNetwork.nodesList();
		for (final Node node : nodes) {
			final GraphPerspective nestedNetwork = node.getNestedNetwork();
			if (nestedNetwork != null)
				verboten.add((CyNetwork)nestedNetwork);
		}

		for (final CyNetwork candidate : Cytoscape.getNetworkSet()) {
			if (!verboten.contains(candidate))
				titleToNetworkMap.put(candidate.getTitle(), candidate);
		}
	}


	private CyNetwork getReferenceNetwork(final Edge edge) {
		final String referenceNetworkTitle =
			Cytoscape.getEdgeAttributes().getStringAttribute(
			        edge.getIdentifier(), REFERENCE_NETWORK_NAME_ATTRIB);
		return getNetworkByTitle(referenceNetworkTitle);
	}


	/**
	 * Returns the first network with title "networkTitle" or null, if there is
	 * no network w/ this title.
	 */
	private CyNetwork getNetworkByTitle(final String networkTitle) {
		for (final CyNetwork network : Cytoscape.getNetworkSet()) {
			if (network.getTitle().equals(networkTitle))
				return network;
		}

		return null;
	}
}
