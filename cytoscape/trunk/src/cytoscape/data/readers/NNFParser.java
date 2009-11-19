package cytoscape.data.readers;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

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


	/**
	 * Parse an entry/line in an NNF file.
	 * 
	 * @param line
	 */
	public boolean parse(String line) {
		System.out.println("Current Line: " + line);

		// Split with white space chars
		parts = line.split("\\s+");
		length = parts.length;

		CyNetwork network = networkMap.get(parts[0]);
		if (network == null) {
			network = Cytoscape.createNetwork(parts[0]);
			network.setTitle(parts[0]);
			networkMap.put(parts[0], network);
			networks.add(network);
			CyNode parent = Cytoscape.getCyNode(parts[0]);
			if (parent != null)
				parent.setNestedNetwork(network);
		}

		if (length == 2) {
			final CyNode node = Cytoscape.getCyNode(parts[1], true);
			network.addNode(node);

		} else if (length == 4) {
			final CyNode source = Cytoscape.getCyNode(parts[1], true);
			network.addNode(source);

			final CyNode target = Cytoscape.getCyNode(parts[3], true);
			network.addNode(target);

			final CyEdge edge = Cytoscape.getCyEdge(source, target, Semantics.INTERACTION, parts[2], true);
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
