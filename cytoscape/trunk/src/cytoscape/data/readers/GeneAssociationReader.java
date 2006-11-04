package cytoscape.data.readers;

import static cytoscape.data.readers.TextFileDelimiters.PIPE;
import static cytoscape.data.readers.TextFileDelimiters.TAB;
import giny.model.Node;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.ontology.GeneOntology;
import cytoscape.data.ontology.Ontology;
import cytoscape.data.synonyms.Aliases;
import cytoscape.util.BioDataServerUtil;
import cytoscape.util.URLUtil;

/**
 * <p>
 * Gene Association (GA) file reader.<br>
 * This is a special reader only for valid GA files.<br>
 * </p>
 * 
 * <p>
 * Gene Association file is an annotation file for Gene Ontology. A valid GA
 * file is:<br>
 * <ul>
 * <li>Should have 15 columns</li>
 * <li>Tab delimited</li>
 * <li>Key is column3, GO term is column5, and alias is column11.</li>
 * </ul>
 * <br>
 * This reader accepts only valid GA file.<br>
 * For more detail, please visit the following web site: <href
 * a="http://www.geneontology.org/GO.current.annotations.shtml"/>
 * </p>
 * 
 * @version 0.8
 * @since Cytoscape 2.4
 * @author Keiichiro Ono
 * 
 */
public class GeneAssociationReader implements TextTableReader {

	private static final String GA_DELIMITER = TAB.toString();

	private static final String ID = "ID";
	private static final int EXPECTED_COL_COUNT = GeneAssociationTags.values().length;

	/*
	 * Default key columns
	 */
	private static final int KEY = 2;
	private static final int SYNONYM = 10;
	private static final int GOID = 4;

	private static final String TAXON_RESOURCE_FILE = "/cytoscape/resources/tax_report.txt";

	private final InputStream is;
	private final String keyAttributeName;

	private Aliases nodeAliases;
	private Map<String, List<String>> attr2id;

	private CyAttributes nodeAttributes;
	private GeneOntology geneOntology;
	private HashMap speciesMap;

	/**
	 * Constructor.
	 * 
	 * 
	 * 
	 * @param ontologyName
	 *            Name of Ontology which is associated with this annotation
	 *            file.
	 * @param url
	 *            URL of the source file. This can be local or remote. Supports
	 *            compressed and flat text files.
	 * @param isColumnName
	 * @param type
	 * @throws IOException
	 * @throws NoOntologyException
	 * @throws
	 */
	public GeneAssociationReader(String ontologyName, final URL url,
			String keyAttributeName) throws IOException {
		this(ontologyName, URLUtil.getInputStream(url), keyAttributeName);
	}

	public GeneAssociationReader(String ontologyName, final InputStream is,
			final String keyAttributeName) throws IOException {

		this.is = is;
		this.keyAttributeName = keyAttributeName;

		// GA file is only for nodes!
		this.nodeAliases = Cytoscape.getOntologyServer().getNodeAliases();
		this.nodeAttributes = Cytoscape.getNodeAttributes();

		final Ontology testOntology = Cytoscape.getOntologyServer()
				.getOntologies().get(ontologyName);

		/*
		 * Ontology type should be GO.
		 */
		if (testOntology.getClass() == GeneOntology.class) {
			this.geneOntology = (GeneOntology) testOntology;
		} else {
			throw new IOException("Given ontology is not GO.");
		}

		final BufferedReader taxonFileReader = new BufferedReader(
				new InputStreamReader(getClass().getResource(
						TAXON_RESOURCE_FILE).openStream()));
		final BioDataServerUtil bdsu = new BioDataServerUtil();
		this.speciesMap = bdsu.getTaxonMap(taxonFileReader);

		taxonFileReader.close();

		if (this.keyAttributeName != null && !this.keyAttributeName.equals(ID)) {
			buildMap();
		}
	}

	private void buildMap() {

		attr2id = new HashMap<String, List<String>>();

		Iterator it = Cytoscape.getRootGraph().nodesIterator();

		String nodeID = null;
		Node node = null;
		String attributeValue = null;
		List<String> nodeIdList = null;

		while (it.hasNext()) {
			node = (Node) it.next();
			nodeID = node.getIdentifier();
			attributeValue = nodeAttributes.getStringAttribute(nodeID,
					keyAttributeName);

			if (attributeValue != null) {
				if (attr2id.containsKey(attributeValue)) {
					nodeIdList = attr2id.get(attributeValue);
				} else {
					nodeIdList = new ArrayList<String>();
				}
				nodeIdList.add(nodeID);
				attr2id.put(attributeValue, nodeIdList);
			}
		}
	}

	public void readTable() throws IOException {

		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(
				is));
		String line = null;
		String[] parts;

		while ((line = bufRd.readLine()) != null) {
			parts = line.split(GA_DELIMITER);
			if (parts.length == EXPECTED_COL_COUNT) {
				parseGA(parts);
			}
		}
		is.close();
		bufRd.close();
	}

	/**
	 * 
	 * @param key
	 * @param synoString
	 * @return
	 */
	private String setAlias(final String key, final String synoString) {

		final String[] synos = synoString.split(PIPE.toString());
		final Set<String> idSet = new TreeSet<String>();
		idSet.add(key);

		/*
		 * Build a Set of node names which includes all aliases and id.
		 */
		for (String synonym : synos) {
			idSet.add(synonym);
		}

		for (String id : idSet) {

			if (Cytoscape.getCyNode(id) != null) {
				if (idSet.size() != 1) {
					idSet.remove(id);
				}
				nodeAliases.add(id, new ArrayList<String>(idSet));
				return id;
			}
		}
		return null;
	}

	private List<String> setMultipleAliases(String key, String synoString) {

		final String[] synos = synoString.split(PIPE.toString());
		final Set<String> idSet = new TreeSet<String>();
		idSet.add(key);
		for (String synonym : synos) {
			idSet.add(synonym);
		}

		for (String id : idSet) {
			if (attr2id.containsKey(id)) {
				List<String> nodeIDs = attr2id.get(id);

				for (String nodeID : nodeIDs) {
					nodeAliases.add(nodeID, new ArrayList<String>(idSet));
				}
				return nodeIDs;
			}
		}
		return null;
	}

	/**
	 * All fields are saved as Strings.
	 * 
	 */
	private void parseGA(String[] entries) {
		/*
		 * Create attribute name based on GO Aspect (a.k.a. Name Space)
		 */
		final String attributeName = "GO "
				+ geneOntology.getAspect(entries[GOID]).name();

		/*
		 * Case 1: use node ID as the key
		 */
		if (keyAttributeName.equals(ID)) {
			String newKey = setAlias(entries[KEY], entries[SYNONYM]);
			if (newKey != null) {
				mapEntry(entries, newKey, attributeName);
			}
		} else {
			/*
			 * Case 2: use an attribute as the key.
			 */
			List<String> keys = setMultipleAliases(entries[KEY],
					entries[SYNONYM]);
			if (keys != null) {
				for (String key : keys) {
					mapEntry(entries, key, attributeName);
				}
			}
		}
	}

	private void mapEntry(String[] entries, String key, String attributeName) {

		String fullName = null;

		for (int i = 0; i < GeneAssociationTags.values().length; i++) {
			GeneAssociationTags tag = GeneAssociationTags.values()[i];

			switch (tag) {
			case GO_ID:
				Set<String> goTermSet = new TreeSet<String>();
				if (nodeAttributes.getAttributeList(key, attributeName) != null) {

					goTermSet.addAll(nodeAttributes.getAttributeList(key,
							attributeName));
				}

				if ((fullName = geneOntology.getGOTerm(entries[i])
						.getFullName()) != null) {
					goTermSet.add(fullName);
				}

				nodeAttributes.setAttributeList(key, attributeName,
						new ArrayList(goTermSet));
				break;
			case TAXON:
				nodeAttributes.setAttribute(key, tag.toString(),
						(String) speciesMap.get(entries[i].split(":")[1]));
				break;
			case EVIDENCE:
				Map<String, String> evidences = nodeAttributes.getAttributeMap(
						key, tag.toString());
				if (evidences == null) {
					evidences = new HashMap<String, String>();
				}
				evidences.put(entries[GOID], entries[i]);
				nodeAttributes.setAttributeMap(key, tag.toString(), evidences);
				break;
			case DB_REFERENCE:
				Map<String, String> references = nodeAttributes
						.getAttributeMap(key, tag.toString());
				if (references == null) {
					references = new HashMap<String, String>();
				}
				references.put(entries[GOID], entries[i]);
				nodeAttributes.setAttributeMap(key, tag.toString(), references);
				break;
			case DB_OBJECT_SYMBOL:
			case ASPECT:
			case DB_OBJECT_SYNONYM:
				// Ignore these lines
				break;
			default:
				nodeAttributes.setAttribute(key, tag.toString(), entries[i]);
				break;
			}
		}
	}

	
	/**
	 * Return column names as List onject.
	 */
	public List getColumnNames() {

		List<String> colNames = new ArrayList<String>();
		for (GeneAssociationTags tag : GeneAssociationTags.values()) {
			colNames.add(tag.toString());
		}
		return colNames;
	}
}