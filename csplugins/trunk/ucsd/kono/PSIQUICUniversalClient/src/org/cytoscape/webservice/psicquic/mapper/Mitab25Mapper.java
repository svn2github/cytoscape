package org.cytoscape.webservice.psicquic.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

/**
 * Map minimal set of information from MITAB25.
 * 
 * @author kono
 * 
 */
public class Mitab25Mapper {

	// Separator for multiple entries.
	private static final String SEPARATOR = "\\|";

	private static final String ATTR_PREFIX = "PSI-MI-25.";

	final Set<CyNode> nodes;
	final Set<CyEdge> edges;

	final CyAttributes nodeAttr;
	final CyAttributes edgeAttr;
	final CyAttributes networkAttr;

	// Reg.Ex for parsing entry
	private final static Pattern miPttr = Pattern.compile("MI:\\d{4}");
	private final static Pattern miNamePttr = Pattern.compile("\\(.+\\)");

	private static final String TAB = "\t";
	private static final String INTERACTION = "interaction";

	// Attr Names
	private static final String DETECTION_METHOD = ATTR_PREFIX
			+ "interaction detection method";
	private static final String INTERACTION_TYPE = ATTR_PREFIX
			+ "interaction type";
	private static final String SOURCE_DB = ATTR_PREFIX + "source database";
	private static final String INTERACTION_ID = ATTR_PREFIX + "Interaction ID";

	// Stable IDs which maybe used for mapping later
	private static final String UNIPROT = "uniprotkb";
	private static final String ENTREZ_GENE = "entrezgene/locuslink";
	private static final String ENTREZ_GENE_SYN = "entrez gene/locuslink";

	private Matcher matcher;

	public Mitab25Mapper() {

		nodes = new HashSet<CyNode>();
		edges = new HashSet<CyEdge>();

		nodeAttr = Cytoscape.getNodeAttributes();
		edgeAttr = Cytoscape.getEdgeAttributes();
		networkAttr = Cytoscape.getNetworkAttributes();
	}

	public CyNetwork map(String mitab, String networkName) {

		// Read the long string of MITAB
		String[] lines = mitab.split("\n");

		String[] entry;
		String[] sourceID;
		String[] targetID;

		String[] detectionMethods;
		CyNode source;
		CyNode target;
		CyEdge e;

		String[] sourceDB;
		String[] interactionID;
		String[] interactionType;

		for (String line : lines) {
			entry = line.split(TAB);
			if (entry == null || entry.length < 12)
				continue;

			sourceID = entry[0].split(SEPARATOR);
			targetID = entry[1].split(SEPARATOR);

			source = Cytoscape.getCyNode(sourceID[0], true);
			target = Cytoscape.getCyNode(targetID[0], true);
			nodes.add(source);
			nodes.add(target);

			// Aliases
			setAliases(nodeAttr, source.getIdentifier(), entry[0]
					.split(SEPARATOR));
			setAliases(nodeAttr, target.getIdentifier(), entry[1]
					.split(SEPARATOR));
			setAliases(nodeAttr, source.getIdentifier(), entry[2]
					.split(SEPARATOR));
			setAliases(nodeAttr, target.getIdentifier(), entry[3]
					.split(SEPARATOR));
			setAliases(nodeAttr, source.getIdentifier(), entry[4]
					.split(SEPARATOR));
			setAliases(nodeAttr, target.getIdentifier(), entry[5]
					.split(SEPARATOR));

			// Tax ID (pick first one only)
			setTaxID(nodeAttr, source.getIdentifier(), entry[9]
					.split(SEPARATOR)[0]);
			setTaxID(nodeAttr, target.getIdentifier(), entry[10]
					.split(SEPARATOR)[0]);

			sourceDB = entry[12].split(SEPARATOR);
			interactionID = entry[13].split(SEPARATOR);

			detectionMethods = entry[6].split(SEPARATOR);
			interactionType = entry[11].split(SEPARATOR);
			e = Cytoscape.getCyEdge(source, target, INTERACTION,
					interactionID[0], true);
			edges.add(e);

			setEdgeListAttribute(edgeAttr, e.getIdentifier(), interactionType,
					INTERACTION_TYPE);
			setEdgeListAttribute(edgeAttr, e.getIdentifier(), detectionMethods,
					DETECTION_METHOD);
			setEdgeListAttribute(edgeAttr, e.getIdentifier(), sourceDB,
					SOURCE_DB);

			edgeAttr.setAttribute(e.getIdentifier(), INTERACTION_ID,
					interactionID[0]);

			setPublication(edgeAttr, e.getIdentifier(), entry[8]
					.split(SEPARATOR), entry[7].split(SEPARATOR));

		}

		// Create top attribues for important keys
		List<String> currentAttr;
		for (CyNode node : nodes) {
			currentAttr = nodeAttr.getListAttribute(node.getIdentifier(),
					ATTR_PREFIX + UNIPROT);
			if (currentAttr != null && currentAttr.size() != 0) {
				nodeAttr.setAttribute(node.getIdentifier(), ATTR_PREFIX
						+ UNIPROT + ".top", currentAttr.get(0));
			}
			currentAttr = nodeAttr.getListAttribute(node.getIdentifier(),
					ATTR_PREFIX + ENTREZ_GENE);
			if (currentAttr != null && currentAttr.size() != 0) {
				nodeAttr.setAttribute(node.getIdentifier(), ATTR_PREFIX
						+ ENTREZ_GENE + ".top", currentAttr.get(0));
			}
			
			currentAttr = nodeAttr.getListAttribute(node.getIdentifier(),
					ATTR_PREFIX + ENTREZ_GENE_SYN);
			if (currentAttr != null && currentAttr.size() != 0) {
				nodeAttr.setAttribute(node.getIdentifier(), ATTR_PREFIX
						+ ENTREZ_GENE + ".top", currentAttr.get(0));
			}
		}

		if (edges.size() != 0) {
			final CyNetwork network = Cytoscape.createNetwork(nodes, edges,
					networkName, null);

			nodes.clear();
			edges.clear();
			return network;
		} else {
			return null;
		}
	}

	private void setNetworkAttr(final CyNetwork net) {

	}

	private void setTaxID(CyAttributes attr, String id, String value) {
		String[] buf = value.split(":", 2);
		String attrName;
		String taxonName;
		if (buf != null && buf.length == 2) {
			attrName = ATTR_PREFIX + buf[0];

			matcher = miNamePttr.matcher(buf[1]);
			if (matcher.find()) {
				taxonName = matcher.group();
				attr.setAttribute(id, attrName, buf[1].split("\\(")[0]);
				attr.setAttribute(id, attrName + ".name", taxonName.substring(
						1, taxonName.length() - 1));
			} else {
				attr.setAttribute(id, attrName, buf[1]);
			}
		}
	}

	private void setPublication(CyAttributes attr, String id, String[] pubID,
			String[] authors) {
		String key = null;
		String[] temp;

		for (String val : pubID) {
			temp = val.split(":", 2);
			if (temp == null || temp.length < 2)
				continue;

			key = ATTR_PREFIX + temp[0];
			listAttrMapper(attr, key, id, temp[1]);
		}

		for (String val : authors) {
			key = ATTR_PREFIX + "author";
			listAttrMapper(attr, key, id, val);
		}
	}

	private void setAliases(CyAttributes attr, String id, String[] entry) {
		String key = null;
		String[] temp;
		String value;

		for (String val : entry) {
			temp = val.split(":", 2);
			if (temp == null || temp.length < 2)
				continue;

			key = ATTR_PREFIX + temp[0];
			value = temp[1].replaceAll("\\(.+\\)", "");
			listAttrMapper(attr, key, id, value);
		}
	}

	private void setEdgeListAttribute(CyAttributes attr, String id,
			String[] entry, String key) {

		String value;
		String name;

		for (String val : entry) {
			value = trimPSITerm(val);
			name = trimPSIName(val);

			listAttrMapper(attr, key, id, value);
			listAttrMapper(attr, key + ".name", id, name);
		}
	}

	private void listAttrMapper(CyAttributes attr, String attrName, String id,
			String value) {
		List currentAttr;

		currentAttr = attr.getListAttribute(id, attrName);
		if (currentAttr == null) {
			currentAttr = new ArrayList<String>();
			currentAttr.add(value);
			attr.setListAttribute(id, attrName, currentAttr);
		} else if (currentAttr.contains(value) == false) {
			currentAttr.add(value);
			attr.setListAttribute(id, attrName, currentAttr);
		}
	}

	private String trimPSITerm(String original) {
		String miID = null;

		matcher = miPttr.matcher(original);

		if (matcher.find()) {
			miID = matcher.group();
		} else {
			miID = "-";
		}

		return miID;
	}

	private String trimPSIName(String original) {
		String miName = null;

		matcher = miNamePttr.matcher(original);

		if (matcher.find()) {
			miName = matcher.group();
			miName = miName.substring(1, miName.length() - 1);
		} else {
			miName = "-";
		}

		return miName;
	}

}
