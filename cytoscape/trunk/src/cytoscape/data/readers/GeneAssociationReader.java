package cytoscape.data.readers;

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
import cytoscape.data.synonyms.Aliases;
import cytoscape.util.BioDataServerUtil;
import cytoscape.util.URLUtil;

/**
 * 
 * Gene Association file reader.<br>
 * This is a special reader only for valid GA files.<br>
 * <p>
 * Gene Association file is an annotation file for Gene Ontology. For more
 * detail, please visit the following web site:
 * 
 * </p>
 * 
 * @version 0.6
 * @since Cytoscape 2.4
 * @author kono
 * 
 */
public class GeneAssociationReader implements TextTableReader {

	private String targetOntologyName;

	private final InputStream is;
	private final TextFileDelimiters type;
	private final String keyAttributeName;

	private Aliases nodeAliases;
	private Map<String, List<String>> attr2id;

	private CyAttributes nodeAttributes;
	private GeneOntology ontology;
	private HashMap speciesMap;

	private static final String ID = "ID";
	private static final int EXPECTED_COL_COUNT = 15;
	/*
	 * Default key columns
	 */
	private static final int KEY = 2;
	private static final int SYNONYM = 10;
	private static final int ASPECT = 8;
	private static final int GOID = 4;

	private static final String TAXON_RESOURCE_FILE = "/cytoscape/resources/tax_report.txt";

	public enum GOAspect {
		BIOLOGICAL_PROCESS("P"), CELLULAR_COMPONENT("C"), MOLECULAR_FUNCTION(
				"F");

		private String aspect;

		private GOAspect(String aspect) {
			this.aspect = aspect;
		}

		public String toString() {
			return aspect;
		}

	}

	private static final String[] COLUMN_NAMES = { "DB", "DB_Object_ID",
			"DB_Object_Symbol", "Qualifier", "GO ID", "DB:Reference",
			"Evidence", "With (or) From", "Aspect", "DB_Object_Name",
			"DB_Object_Synonym", "DB_Object_Type", "taxon", "Date",
			"Assigned_by" };

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
			final boolean isColumnName, final TextFileDelimiters type,
			String keyAttributeName) throws IOException {
		this(ontologyName, URLUtil.getInputStream(url), isColumnName, type,
				keyAttributeName);
	}

	public GeneAssociationReader(String ontologyName, final InputStream is,
			final boolean isColumnName, final TextFileDelimiters type,
			final String keyAttributeName) throws IOException {
		this.is = is;
		this.type = type;
		this.keyAttributeName = keyAttributeName;
		this.targetOntologyName = ontologyName;

		this.nodeAliases = Cytoscape.getOntologyServer().getNodeAliases();

		if (!Cytoscape.getOntologyServer().getOntologyNames().contains(
				targetOntologyName)) {
			throw new IOException("Cannot find ontology.");
		}

		nodeAttributes = Cytoscape.getNodeAttributes();
		this.ontology = (GeneOntology) Cytoscape.getOntologyServer()
				.getOntologies().get(targetOntologyName);

		URL taxURL = getClass().getResource(TAXON_RESOURCE_FILE);

		BufferedReader taxonFileReader = new BufferedReader(
				new InputStreamReader(taxURL.openStream()));
		BioDataServerUtil bdsu = new BioDataServerUtil();
		speciesMap = bdsu.getTaxonMap(taxonFileReader);

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

		for (String key : attr2id.keySet()) {
			System.out.println("### Attr Key = " + key);
		}
	}

	public void readTable() throws IOException {

		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(
				is));
		String line = null;
		String[] parts;

		while ((line = bufRd.readLine()) != null) {
			parts = line.split(type.toString());
			// If this line is valid, copy entries to CyAttributes
			if (parts.length == EXPECTED_COL_COUNT) {
				transfer2cyAttributes(parts);
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
	private String setSynonyms(String key, String synoString) {

		final String[] synos = synoString.split("\\|");
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

	private List<String> setMultipleSynonyms(String key, String synoString) {

		final String[] synos = synoString.split("\\|");
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
	private void transfer2cyAttributes(String[] entries) {
		/*
		 * Create attribute name based on GO Aspect
		 */
		final String attributeName = makeAttributeName(entries[ASPECT]);

		/*
		 * Case 1: use node ID as the key
		 */
		if (keyAttributeName.equals("ID")) {
			String newKey = setSynonyms(entries[KEY], entries[SYNONYM]);
			if (newKey != null) {
				parseEntry(entries, newKey, attributeName);
			}
		} else {
			/*
			 * Case 2: use an attribute as the key.
			 */
			List<String> keys = setMultipleSynonyms(entries[KEY],
					entries[SYNONYM]);
			if (keys != null) {
				for (String key : keys) {
					parseEntry(entries, key, attributeName);
				}
			}
		}
	}

	private void parseEntry(String[] entries, String key, String attributeName) {
		final String dbAndId = entries[0] + ":" + entries[1];
		nodeAttributes.setAttribute(key, "DB Name and ID", dbAndId);

		String fullName = null;

		for (int i = 0; i < COLUMN_NAMES.length; i++) {
			if (COLUMN_NAMES[i].equals("GO ID")) {

				Set<String> goTermSet = new TreeSet<String>();
				if (nodeAttributes.getAttributeList(key, attributeName) != null) {

					goTermSet.addAll(nodeAttributes.getAttributeList(key,
							attributeName));

				}
				fullName = ontology.getGOTerm(entries[i]).getFullName();
				if (fullName != null) {
					goTermSet.add(fullName);
				}

				nodeAttributes.setAttributeList(key, attributeName,
						new ArrayList(goTermSet));
			} else if (COLUMN_NAMES[i].equals("DB_Object_Name")
					|| COLUMN_NAMES[i].equals("DB_Object_Type")) {
				nodeAttributes.setAttribute(key, COLUMN_NAMES[i], entries[i]);
			} else if (COLUMN_NAMES[i].equals("taxon")) {
				nodeAttributes.setAttribute(key, COLUMN_NAMES[i],
						(String) speciesMap.get(entries[i].split(":")[1]));
			} else if (COLUMN_NAMES[i].equals("Evidence")) {
				Map<String, String> evidences = nodeAttributes.getAttributeMap(
						key, "Evidence");
				if (evidences == null) {
					evidences = new HashMap<String, String>();
				}
				evidences.put(entries[GOID], entries[i]);
				nodeAttributes.setAttributeMap(key, "Evidence", evidences);
			} else if (COLUMN_NAMES[i].equals("DB:Reference")) {
				Map<String, String> references = nodeAttributes
						.getAttributeMap(key, "DB:Reference");
				if (references == null) {
					references = new HashMap<String, String>();
				}
				references.put(entries[GOID], entries[i]);
				nodeAttributes.setAttributeMap(key, "DB:Reference", references);
			}
		}
	}

	private String makeAttributeName(String aspect) {
		String attributeName = null;
		if (aspect.equalsIgnoreCase(GOAspect.BIOLOGICAL_PROCESS.toString())) {
			attributeName = "GO " + GOAspect.BIOLOGICAL_PROCESS.name();
		} else if (aspect.equalsIgnoreCase(GOAspect.CELLULAR_COMPONENT
				.toString())) {
			attributeName = "GO " + GOAspect.CELLULAR_COMPONENT.name();
		} else if (aspect.equalsIgnoreCase(GOAspect.MOLECULAR_FUNCTION
				.toString())) {
			attributeName = "GO " + GOAspect.MOLECULAR_FUNCTION.name();
		}
		return attributeName;
	}

	/**
	 * Return column names as List onject.
	 */
	public List getColumnNames() {

		List colNames = new ArrayList();
		for (String name : COLUMN_NAMES) {
			colNames.add(name);
		}
		return colNames;
	}
}