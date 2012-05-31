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
import cytoscape.logger.CyLogger;

/**
 * Map minimal set of information from MITAB25.
 * 
 */
public class Mitab25Mapper {

	private static final CyLogger logger = CyLogger.getLogger();

	public static final String PREDICTED_GENE_NAME = "predicted gene name";
	
	// Separator for multiple entries.
	private static final String SEPARATOR = "\\|";
	private static final String TAB = "\t";
	private static final String DB_ID = "\\:";
	private static final String DESCRIPTION = "(\\S?)";
	private static final String ATTR_PREFIX = "PSIMI25.";

	// PSIMI25 specification contains 15 columns.
	private static final int COLUMN_COUNT = 15;

	final Set<CyNode> nodes;
	final Set<CyEdge> edges;

	final CyAttributes nodeAttr;
	final CyAttributes edgeAttr;
	final CyAttributes networkAttr;

	private static final String INTERACTION = "interaction";

	// Reg.Ex for parsing entry
	private final static Pattern miPttr = Pattern.compile("MI:\\d{4}");
	private final static Pattern miNamePttr = Pattern.compile("\\(.+\\)");

	private final static Pattern lineSplitter = Pattern.compile(TAB);
	private final static Pattern entrySplitter = Pattern.compile(SEPARATOR);
	private final static Pattern dbSplitter = Pattern.compile(DB_ID);
	private final static Pattern descriptionSpliter = Pattern.compile("\\(");
	private final static Pattern ncbiPattern = Pattern.compile("^[A-Za-z].+");
	private final static Pattern uniprotPattern = Pattern.compile("^[a-zA-Z]\\d.+");
	// Attr Names
	private static final String DETECTION_METHOD = ATTR_PREFIX + "interaction detection method";
	private static final String INTERACTION_TYPE = ATTR_PREFIX + "interaction type";
	private static final String SOURCE_DB = ATTR_PREFIX + "source database";
	private static final String INTERACTION_ID = ATTR_PREFIX + "Interaction ID";
	private static final String EDGE_SCORE = ATTR_PREFIX + "Confidence Score";

	// Stable IDs which maybe used for mapping later
	private static final String UNIPROT = "uniprotkb";
	private static final String ENTREZ_GENE = "entrezgene/locuslink";
	private static final String ENTREZ_GENE_SYN = "entrez gene/locuslink";

	private static final String ENTREZ_GENE_ATTR_NAME = ATTR_PREFIX + ENTREZ_GENE;
	private static final String UNIPROT_ATTR_NAME = ATTR_PREFIX + "uniprotkb";
	private static final String CHEBI = "chebi";

	private static final String INTERACTOR_TYPE = ATTR_PREFIX + "interactor type";
	private static final String COMPOUND = "compound";

	private Matcher matcher;

	public Mitab25Mapper() {

		nodes = new HashSet<CyNode>();
		edges = new HashSet<CyEdge>();

		nodeAttr = Cytoscape.getNodeAttributes();
		edgeAttr = Cytoscape.getEdgeAttributes();
		networkAttr = Cytoscape.getNetworkAttributes();
	}

	public CyNetwork map(String mitab, String networkName, CyNetwork parentNetwork) {

		// Read the long string of MITAB
		String[] lines = mitab.split("\n");
		parse(lines);
		lines = null;

		// Create top attribues for important keys
		List<String> currentAttr;
		for (CyNode node : nodes) {
			currentAttr = nodeAttr.getListAttribute(node.getIdentifier(), ATTR_PREFIX + UNIPROT);
			if (currentAttr != null && currentAttr.size() != 0) {
				nodeAttr.setAttribute(node.getIdentifier(), ATTR_PREFIX + UNIPROT + ".top", currentAttr.get(0));
			}
			currentAttr = nodeAttr.getListAttribute(node.getIdentifier(), ATTR_PREFIX + ENTREZ_GENE);
			if (currentAttr != null && currentAttr.size() != 0) {
				nodeAttr.setAttribute(node.getIdentifier(), ATTR_PREFIX + ENTREZ_GENE + ".top", currentAttr.get(0));
			}

			currentAttr = nodeAttr.getListAttribute(node.getIdentifier(), ATTR_PREFIX + ENTREZ_GENE_SYN);
			if (currentAttr != null && currentAttr.size() != 0) {
				nodeAttr.setAttribute(node.getIdentifier(), ATTR_PREFIX + ENTREZ_GENE + ".top", currentAttr.get(0));
			}
		}

		if (edges.size() != 0) {
			final CyNetwork network = Cytoscape.createNetwork(nodes, edges, networkName, parentNetwork);

			nodes.clear();
			edges.clear();
			return network;
		} else {
			return null;
		}
	}

	private void parse(final String[] lines) {
		for (final String line : lines) {
			try {
				parseLine(line);
			} catch (Exception ex) {
				logger.warn("Failed parse line: " + line, ex);
				continue;
			}
		}
	}

	private final void parseLine(final String line) throws Exception {
		final String[] entry = lineSplitter.split(line);

		// Validate entry list.
		if (entry == null || entry.length < COLUMN_COUNT)
			return;

		// Create nodes
		final String[] sourceID = entrySplitter.split(entry[0]);
		final String[] targetID = entrySplitter.split(entry[1]);

		String[] keyIDPparts = dbSplitter.split(sourceID[0]);
		final String firstSourceID = keyIDPparts[1];
		final String firstSourceDB = keyIDPparts[0];

		keyIDPparts = dbSplitter.split(targetID[0]);
		final String firstTargetID = keyIDPparts[1];
		final String firstTargetDB = keyIDPparts[0];

		final CyNode source = Cytoscape.getCyNode(firstSourceID, true);
		final CyNode target = Cytoscape.getCyNode(firstTargetID, true);
		nodeAttr.setAttribute(source.getIdentifier(), ATTR_PREFIX + "primaryKey." + firstSourceDB, firstSourceID);
		nodeAttr.setAttribute(target.getIdentifier(), ATTR_PREFIX + "primaryKey." + firstTargetDB, firstTargetID);
		nodes.add(source);
		nodes.add(target);

		// Set type if not protein
		if (source.getIdentifier().contains(CHEBI))
			nodeAttr.setAttribute(source.getIdentifier(), INTERACTOR_TYPE, COMPOUND);
		if (target.getIdentifier().contains(CHEBI))
			nodeAttr.setAttribute(target.getIdentifier(), INTERACTOR_TYPE, COMPOUND);

		// Aliases
		setAliases(nodeAttr, source.getIdentifier(), sourceID);
		setAliases(nodeAttr, target.getIdentifier(), targetID);
		setAliases(nodeAttr, source.getIdentifier(), entrySplitter.split(entry[2]));
		setAliases(nodeAttr, target.getIdentifier(), entrySplitter.split(entry[3]));
		setAliases(nodeAttr, source.getIdentifier(), entrySplitter.split(entry[4]));
		setAliases(nodeAttr, target.getIdentifier(), entrySplitter.split(entry[5]));

		// Tax ID (pick first one only)
		setTaxID(nodeAttr, source.getIdentifier(), entrySplitter.split(entry[9])[0]);
		setTaxID(nodeAttr, target.getIdentifier(), entrySplitter.split(entry[10])[0]);

		final String[] sourceDB = entrySplitter.split(entry[12]);
		final String[] interactionID = entrySplitter.split(entry[13]);

		final String[] edgeScores = entrySplitter.split(entry[14]);

		final String[] detectionMethods = entrySplitter.split(entry[6]);
		final String[] interactionType = entrySplitter.split(entry[11]);
		final CyEdge e = Cytoscape.getCyEdge(source, target, INTERACTION, interactionID[0], true);
		edges.add(e);

		setEdgeListAttribute(edgeAttr, e.getIdentifier(), interactionType, INTERACTION_TYPE);
		setEdgeListAttribute(edgeAttr, e.getIdentifier(), detectionMethods, DETECTION_METHOD);
		setEdgeListAttribute(edgeAttr, e.getIdentifier(), sourceDB, SOURCE_DB);

		// Map scores
		setEdgeScoreListAttribute(edgeAttr, e.getIdentifier(), edgeScores, EDGE_SCORE);

		edgeAttr.setAttribute(e.getIdentifier(), INTERACTION_ID, interactionID[0]);
		setPublication(edgeAttr, e.getIdentifier(), entrySplitter.split(entry[8]), entrySplitter.split(entry[7]));

		guessHumanReadableName(source);
		guessHumanReadableName(target);
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
				attr.setAttribute(id, attrName + ".name", taxonName.substring(1, taxonName.length() - 1));
			} else {
				attr.setAttribute(id, attrName, buf[1]);
			}
		}
	}

	private void setPublication(CyAttributes attr, String id, String[] pubID, String[] authors) {
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

	private void setEdgeListAttribute(CyAttributes attr, String id, String[] entry, String key) {

		String value;
		String name;

		for (String val : entry) {
			value = trimPSITerm(val);
			name = trimPSIName(val);

			listAttrMapper(attr, key, id, value);
			listAttrMapper(attr, key + ".name", id, name);
		}
	}

	/**
	 * Create edge score attribute as Double
	 * 
	 * @param attr
	 * @param id
	 * @param scores
	 * @param prefix
	 */
	private void setEdgeScoreListAttribute(CyAttributes attr, String id, String[] scores, String prefix) {
		for (final String scoreFullString : scores) {
			final String[] parts = dbSplitter.split(scoreFullString);
			if (parts == null || parts.length != 2)
				continue;

			final String scoreRaw = parts[1];
			final String scoreString = descriptionSpliter.split(scoreRaw)[0];
			final String db = parts[0];

			try {
				final double score = Double.parseDouble(scoreString);
				edgeAttr.setAttribute(id, prefix + "." + db, score);
			} catch (Exception e) {
				continue;
			}
		}
	}

	private void listAttrMapper(CyAttributes attr, String attrName, String id, String value) {
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

	private void guessHumanReadableName(CyNode node) {
		final String id = node.getIdentifier();
		// try NCBI
		final List<String> ncbiList = nodeAttr.getListAttribute(id, ENTREZ_GENE_ATTR_NAME);
		String candidateString = null;
		if (ncbiList != null) {
			for (final String geneID : ncbiList) {
				if (ncbiPattern.matcher(geneID).find()) {
					candidateString = geneID;
					break;
				}
			}
			if (candidateString != null) {
				nodeAttr.setAttribute(id, PREDICTED_GENE_NAME, candidateString);
				return;
			}
		}
		
		// Try Uniprot
		final List<String> uniprotList = nodeAttr.getListAttribute(id, UNIPROT_ATTR_NAME);
		if (uniprotList != null) {
			for (final String geneID : uniprotList) {
				if (uniprotPattern.matcher(geneID).find() == false) {
					candidateString = geneID;
					break;
				}
			}
			if (candidateString != null) {
				nodeAttr.setAttribute(id, PREDICTED_GENE_NAME, candidateString);
				return;
			}
		}

		// TODO: try String
		
		// Give up.  Use primary key
		nodeAttr.setAttribute(id, PREDICTED_GENE_NAME, node.getIdentifier());
	}

}
