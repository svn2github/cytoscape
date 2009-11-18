package cytoscape.data.readers;

import java.util.HashMap;
import java.util.Map;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;

public class NNFParser {
	// For performance, these fields will be reused.
	private String[] parts;
	private int length;

	// Parent network of all graph objects in the file
	private CyNetwork rootNetwork;

	private String rootNetworkTitle;
	// Hash map from title to actual network
	private Map<String, CyNetwork> networkMap;

	public NNFParser() {
		networkMap = new HashMap<String, CyNetwork>();
	}

	/**
	 * Parse an entry in NNF file.
	 * 
	 * @param rootNetwork
	 * @param line
	 */
	public boolean parse(String line) {
		System.out.println("Current Line: " + line);

		// Split with white space chars
		parts = line.split("\\s+");
		length = parts.length;

		// Create root network if necessary.
		if (networkMap.size() == 0) {
			// This is the first non-empty line.
			rootNetworkTitle = parts[0];
			rootNetwork = Cytoscape.createNetwork(rootNetworkTitle);
			rootNetwork.setTitle(rootNetworkTitle);
			networkMap.put(rootNetworkTitle, rootNetwork);
		}

		CyNetwork network = networkMap.get(parts[0]);
		if (network == null) {
			network = Cytoscape.createNetwork(parts[0]);
			network.setTitle(parts[0]);
			networkMap.put(parts[0], network);
		}

		if (length == 2) {
			final CyNode node = Cytoscape.getCyNode(parts[1], true);
			if (network != null) {
				network.addNode(node);
				final CyNetwork nestedNetwork = networkMap.get(parts[1]);
				if (nestedNetwork != null) {
					node.setNestedNetwork(nestedNetwork);
				}
			}

		} else if (length == 4) {
			final CyNode source = Cytoscape.getCyNode(parts[1], true);
			network.addNode(source);
			CyNetwork nestedNetwork = networkMap.get(parts[1]);
			if (nestedNetwork != null) {
				source.setNestedNetwork(nestedNetwork);
			}

			final CyNode target = Cytoscape.getCyNode(parts[3], true);
			network.addNode(target);
			nestedNetwork = networkMap.get(parts[3]);
			if (nestedNetwork != null) {
				target.setNestedNetwork(nestedNetwork);
			}

			final CyEdge edge = Cytoscape.getCyEdge(source, target,
					Semantics.INTERACTION, parts[2], true);
			network.addEdge(edge);
		} else {
			// Invalid number of columns.
			System.out.println("Invalid line found: Length = " + length);
			return false;
		}

		return true;
	}
	
	protected CyNetwork getRootNetwork() {
		return rootNetwork;
	}
}
