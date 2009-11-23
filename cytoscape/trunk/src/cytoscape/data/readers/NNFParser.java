package cytoscape.data.readers;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;


/**
 * Parser for NNF files.
 * 
 * @author kono, ruschein
 */
public class NNFParser {
	
	private static final String IMMUTABLE_ID = "immutable_id";
	
	// For performance, these fields will be reused.
	private String[] parts;
	private int length;

	// List of root network plus all nested networks.
	private final List<CyNetwork> networks;

	// Hash map from title to actual network
	private Map<String, CyNetwork> networkMap;


	public NNFParser() {
		networkMap = new HashMap<String, CyNetwork>();
		networks = new ArrayList<CyNetwork>();
	}


	/** Returns the first network with title "networkTitle" or null, if there is no network w/ this title.
	 */
	private CyNetwork getNetworkByTitle(final String networkTitle) {
		Set<CyNetwork> networks = Cytoscape.getNetworkSet();
		for (final CyNetwork network : networks) {
			if (network.getTitle().equals(networkTitle))
				return network;
		}

		return null;
	}
	

	/**
	 * Parse an entry/line in an NNF file.
	 * 
	 * @param line
	 */
	public boolean parse(String line) {
		// Split with white space chars
		parts = line.split("\\s+");
		length = parts.length;

		CyNetwork network = networkMap.get(parts[0]);
		if (network == null) {
			// Reuse existing networks, if possible:
			network = getNetworkByTitle(parts[0]);
			if (network == null) {
				// Create network without view.  View will be created later in class Cytoscape.
				network = Cytoscape.createNetwork(parts[0], /* create_view = */false);
			}

			networkMap.put(parts[0], network);
			networks.add(network);

			// Create node attribute called IMMUTABLE_ID
			Cytoscape.getNetworkAttributes().setAttribute(network.getIdentifier(), IMMUTABLE_ID, network.getIdentifier());
			Cytoscape.getNetworkAttributes().setUserEditable(IMMUTABLE_ID, false);

			// Attempt to nest network within the node with the same name
			final CyNode parent = Cytoscape.getCyNode(parts[0]);
			if (parent != null) {
				parent.setNestedNetwork(network);
			}
		}

		if (length == 2) {
			final CyNode node = Cytoscape.getCyNode(parts[1], true);
			network.addNode(node);
			final CyNetwork nestedNetwork = networkMap.get(parts[1]);
			if (nestedNetwork != null) {
				node.setNestedNetwork(nestedNetwork);
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

			final CyEdge edge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, parts[2], /* create = */true,
								/* directed = */true);
			network.addEdge(edge);
		} else {
			// Invalid number of columns.
			System.out.println("Invalid line found: Length = " + length);
			return false;
		}

		return true;
	}
	

	protected List<CyNetwork> getNetworks() {
		return networks;
	}
}
