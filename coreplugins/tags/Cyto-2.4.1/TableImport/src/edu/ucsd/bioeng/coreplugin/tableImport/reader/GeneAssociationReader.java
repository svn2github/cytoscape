package edu.ucsd.bioeng.coreplugin.tableImport.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
import giny.model.Node;

import static edu.ucsd.bioeng.coreplugin.tableImport.reader.TextFileDelimiters.*;

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
 * <p>
 * Since Cytoscape do not use Annotation class any more, CyAtteibutes imported
 * by this reader will be tagged with a special hidden attribute called
 * "isAnnotation." This is a boolean value to distinguish these values from
 * other regular attributes.
 * </p>
 * 
 * @version 0.8
 * @since Cytoscape 2.4
 * @author Keiichiro Ono
 * 
 */
public class GeneAssociationReader implements TextTableReader {
	
	private static final String GO_PREFIX = "GO";
	private static final String ANNOTATION_PREFIX = "annotation";

	private static final String GA_DELIMITER = TAB.toString();

	private static final String ID = "ID";
	private static final int EXPECTED_COL_COUNT = GeneAssociationTags.values().length;

	/*
	 * Default key columns
	 */
	private static final int DB_OBJ_ID = 1;
	private static final int KEY = 2;
	private static final int OBJ_NAME = 9;
	private static final int SYNONYM = 10;
	private static final int GOID = 4;
	

	private static final String TAXON_RESOURCE_FILE = "/cytoscape/resources/tax_report.txt";

	private InputStream is;
	private final String keyAttributeName;

	private Aliases nodeAliases;
	private Map<String, List<String>> attr2id;

	private CyAttributes nodeAttributes;
	private GeneOntology geneOntology;
	private HashMap speciesMap;

	private boolean importAll = false;

	/**
	 * Constructor.
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
	public GeneAssociationReader(final String ontologyName, final URL url,
			final String keyAttributeName) throws IOException {
		this(ontologyName, URLUtil.getInputStream(url), keyAttributeName, false);
	}

	public GeneAssociationReader(final String ontologyName, final URL url,
			final String keyAttributeName, final boolean importAll)
			throws IOException {
		this(ontologyName, URLUtil.getInputStream(url), keyAttributeName,
				importAll);
	}

	public GeneAssociationReader(final String ontologyName,
			final InputStream is, final String keyAttributeName,
			final boolean importAll) throws IOException {

		this.importAll = importAll;
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

		BufferedReader bufRd = new BufferedReader(new InputStreamReader(is));
		String line = null;
		String[] parts;

		int global = 0;

		while ((line = bufRd.readLine()) != null) {
			global++;
			parts = line.split(GA_DELIMITER);
			if (parts.length == EXPECTED_COL_COUNT) {
				parseGA(parts);
			}
		}

		if (is != null) {
			is.close();
			is = null;
		}

		if (bufRd != null) {
			bufRd.close();
			bufRd = null;
		}
	}

	/**
	 * 
	 * @param key
	 * @param synoString
	 * @return
	 */
	private String setAlias(final String key, final String objName,
			final String synoString, final String dbSpecificId) {

		final String[] synos = synoString.split(PIPE.toString());
		final String[] objNames;
		final Set<String> idSet = new TreeSet<String>();

		if (objName != null && objName.length() != 0) {
			String[] tempObj = objName.split(":");
			if (tempObj.length != 0) {
				objNames = tempObj[0].split(",");
				for (String name : objNames) {
					idSet.add(name);
				}
			}
		}

		idSet.add(key);
		
		if(dbSpecificId != null && dbSpecificId.length() != 0) {
			idSet.add(dbSpecificId);
		}

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

		if (geneOntology.getAspect(entries[GOID]) == null) {
			return;
		}
		
		/*
		 * Create attribute name based on GO Aspect (a.k.a. Name Space)
		 */
		final String attributeName = ANNOTATION_PREFIX + "." + GO_PREFIX + " "
				+ geneOntology.getAspect(entries[GOID]).name();

		/*
		 * If importAll option is selected, just import everything even if the
		 * node does not exists in the memmory.
		 */
		if (importAll) {
			// Add all aliases
			if (entries[SYNONYM] != null && entries[SYNONYM].length() != 0) {
				final String[] alias = entries[SYNONYM].split(PIPE.toString());
				nodeAliases.add(entries[KEY], Arrays.asList(alias));
			}
			mapEntry(entries, entries[KEY], attributeName);
		} else {

			/*
			 * Case 1: use node ID as the key
			 */
			if (keyAttributeName.equals(ID)) {
				String newKey = setAlias(entries[KEY], entries[OBJ_NAME],
						entries[SYNONYM], entries[DB_OBJ_ID]);
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
	}

	private void mapEntry(final String[] entries, final String key, final String attributeName) {

		String fullName = null;

		for (int i = 0; i < GeneAssociationTags.values().length; i++) {
			GeneAssociationTags tag = GeneAssociationTags.values()[i];

			switch (tag) {
			case GO_ID:
				Set<String> goTermSet = new TreeSet<String>();
				if (nodeAttributes.getListAttribute(key, attributeName) != null) {

					goTermSet.addAll(nodeAttributes.getListAttribute(key,
							attributeName));
				}

				if ((fullName = geneOntology.getGOTerm(entries[i])
						.getFullName()) != null) {
					goTermSet.add(fullName);
				}

				nodeAttributes.setListAttribute(key, attributeName,
						new ArrayList(goTermSet));
				break;
			case TAXON:
				nodeAttributes.setAttribute(key, ANNOTATION_PREFIX + "." + tag.toString(),
						(String) speciesMap.get(entries[i].split(":")[1]));
				break;
			case EVIDENCE:
				Map<String, String> evidences = nodeAttributes.getMapAttribute(
						key, ANNOTATION_PREFIX + "." + tag.toString());
				if (evidences == null) {
					evidences = new HashMap<String, String>();
				}
				evidences.put(entries[GOID], entries[i]);
				nodeAttributes.setMapAttribute(key, ANNOTATION_PREFIX + "." + tag.toString(), evidences);
				break;
			case DB_REFERENCE:
				Map<String, String> references = nodeAttributes
						.getMapAttribute(key, ANNOTATION_PREFIX + "." + tag.toString());
				if (references == null) {
					references = new HashMap<String, String>();
				}
				references.put(entries[GOID], entries[i]);
				nodeAttributes.setMapAttribute(key, ANNOTATION_PREFIX + "." + tag.toString(), references);
				break;
			case DB_OBJECT_SYMBOL:
			case ASPECT:
			case DB_OBJECT_SYNONYM:
				// Ignore these lines
				break;
			default:
				nodeAttributes.setAttribute(key, ANNOTATION_PREFIX + "." + tag.toString(), entries[i]);
				break;
			}
		}
	}

	/**
	 * Return column names as List onject.
	 */
	public List getColumnNames() {

		final List<String> colNames = new ArrayList<String>();
		for (GeneAssociationTags tag : GeneAssociationTags.values()) {
			colNames.add(tag.toString());
		}
		return colNames;
	}

	public String getReport() {
		// TODO Auto-generated method stub
		final StringBuffer sb = new StringBuffer();

		return sb.toString();
	}
}