package cytoscape.data.readers;

import giny.model.Edge;
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
import java.util.TreeMap;
import java.util.TreeSet;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.synonyms.Aliases;
import cytoscape.util.URLUtil;

/**
 * Basic text table reader for attributes.<br>
 * 
 * <p>
 * based on the given parameters, map the text table to CyAttributes.
 * </p>
 * 
 * @author kono
 * 
 */
public class DefaultAttributeTableReader implements TextTableReader {

	/**
	 * Lines begin with this charactor will be considered as comment lines.
	 */
	private static final String COMMENT_CHAR = "!";

	/**
	 * Reserved words
	 */
	private static final String ID = "ID";
	private static final String DEF_LIST_DELIMITER = "\\|";
	private static final String DEF_DELIMITER = TextFileDelimiters.TAB.toString();
	private static final int DEF_KEY_COLUMN = 0;
	
	private final URL source;
	private final ObjectType objType;
	private final int key;
	private final List<Integer> aliasColumns;
	private String[] columnNames;
	private byte[] attributeTypes;
	private final String mappingAttribute;

	private Map<String, List<String>> attr2id;

	private List<String> delimiters;
	private String listDelimiter;

	private CyAttributes attributes;
	private Aliases existingAliases;

	private boolean[] importFlag;

	public DefaultAttributeTableReader(final URL source, final ObjectType objectType,
			final List<String> delimiters) {
		this(source, objectType, delimiters, DEF_LIST_DELIMITER,
				DEF_KEY_COLUMN, null, null, null, null, null);
	}

	public DefaultAttributeTableReader(final URL source, final ObjectType objectType,
			final List<String> delimiters, final int key,
			final String[] columnNames) {
		this(source, objectType, delimiters, DEF_LIST_DELIMITER,
				DEF_KEY_COLUMN, null, null, columnNames, null, null);
	}

	/**
	 * Constructor with full options.<br>
	 * 
	 * @param source
	 *            Source file URL (can be remote or local)
	 * @param objectType
	 * @param delimiter
	 * @param listDelimiter
	 * @param key
	 * @param aliases
	 * @param columnNames
	 * @param toBeImported
	 */
	public DefaultAttributeTableReader(final URL source, final ObjectType objectType,
			final List<String> delimiters, final String listDelimiter,
			final int key, final String mappingAttribute,
			final List<Integer> aliases, final String[] columnNames,
			final byte[] attributeTypes, final boolean[] toBeImported) {
		this.source = source;
		this.objType = objectType;

		if(mappingAttribute != null) {
			this.mappingAttribute = mappingAttribute;
		} else {
			this.mappingAttribute = ID;
		}
		if(delimiters == null) {
			this.delimiters = new ArrayList<String>();
			delimiters.add(DEF_DELIMITER);
		}
		this.delimiters = delimiters;

		if (listDelimiter == null) {
			this.listDelimiter = DEF_LIST_DELIMITER;
		} else {
			this.listDelimiter = listDelimiter;
		}

		if(aliases != null) {
			this.aliasColumns = aliases;
		} else {
			this.aliasColumns = new ArrayList<Integer>();
		}

		this.attributeTypes = attributeTypes;

		this.key = key;

		this.columnNames = columnNames;
		this.importFlag = toBeImported;

		final Iterator it;
		switch (objType) {
		case NODE:
			attributes = Cytoscape.getNodeAttributes();
			existingAliases = Cytoscape.getOntologyServer().getNodeAliases();
			it = Cytoscape.getRootGraph().nodesIterator();
			break;
		case EDGE:
			attributes = Cytoscape.getEdgeAttributes();
			existingAliases = Cytoscape.getOntologyServer().getEdgeAliases();
			it = Cytoscape.getRootGraph().edgesIterator();
			break;
		case NETWORK:
			attributes = Cytoscape.getNetworkAttributes();
			existingAliases = Cytoscape.getOntologyServer().getNetworkAliases();
			it = Cytoscape.getNetworkSet().iterator();
			break;
		default:
			attributes = null;
			it = null;
		}

		if (this.mappingAttribute != null && !this.mappingAttribute.equals(ID)) {
			buildMap(it);
		}
	}

	
	public List getColumnNames() {
		List<String> colNamesList = new ArrayList<String>();
		for (String name : columnNames) {
			colNamesList.add(name);
		}
		return colNamesList;
	}

	private String getDelimiter() {
		StringBuffer delimiterBuffer = new StringBuffer();
		delimiterBuffer.append("[");
		for (String delimiter : delimiters) {
			delimiterBuffer.append(delimiter);
		}
		delimiterBuffer.append("]");
		return delimiterBuffer.toString();
	}

	/**
	 * Read table from the data source.
	 */
	public void readTable() throws IOException {

		InputStream is = URLUtil.getInputStream(source);

		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(
				is));
		String line;

		if (columnNames == null) {
			while ((line = bufRd.readLine()) != null) {
				if (!line.startsWith("!") && line.trim().length() != 0) {
					System.out.println(line);
					columnNames = line.split(getDelimiter());
					break;
				}
			}
		}

		/*
		 * Error check: Key column number should be smaller than actual number
		 * of columns in the text table.
		 */
		if (columnNames.length < key) {
			throw new IOException("Key is out of range.");
		}

		/*
		 * If not specified, import everything as String attributes.
		 */
		if (attributeTypes == null) {
			attributeTypes = new byte[columnNames.length];
			for (int i = 0; i < columnNames.length; i++) {
				attributeTypes[i] = CyAttributes.TYPE_STRING;
			}
		}

		if (importFlag == null) {
			importFlag = new boolean[columnNames.length];

			for (int i = 0; i < importFlag.length; i++) {
				importFlag[i] = true;
			}
		}

		/*
		 * Read & extract one line at a time. The line can be Tab delimited,
		 */
		while ((line = bufRd.readLine()) != null) {
			/*
			 * Ignore Empty & Commnet lines.
			 */
			if (!line.startsWith(COMMENT_CHAR) && line.trim().length() > 0) {
				parseEntry(line);
			}

		}
		is.close();
		bufRd.close();
	}

	private void parseEntry(String line) {
		/*
		 * Split the line and extract values
		 */
		final String[] parts = line.split(getDelimiter());
		final String primaryKey = parts[key].trim();

		/*
		 * Set aliases In this case, "aliases" means alias entries in the TEXT
		 * TABLE, not the ones returned by Cytoscape.getNodeAliases()
		 * 
		 * The variable aliasSet has non-redundant set of object names.
		 */
		final Set<String> aliasSet = new TreeSet<String>();
		if (aliasColumns.size() != 0) {
			/*
			 * Alias column exists. Extract those keys.
			 */
			String aliasCell = null;
			for (int aliasColumn : aliasColumns) {
				aliasCell = parts[aliasColumn];
				// System.out.print("Aliasing: " + primaryKey + "Delimiter = " +
				// listDelimiter);
				if (aliasCell != null && aliasCell.trim().length() != 0) {
					final String[] aliasStrings = aliasCell
							.split(listDelimiter);
					// System.out.println("\t num aliases = " +
					// aliasStrings.length);
					for (int i = 0; i < aliasStrings.length; i++) {
						aliasSet.add(aliasStrings[i].trim());

					}
				}
			}
		}
		
		aliasSet.add(primaryKey);

		/*
		 * Case 1: use node ID as the key
		 */
		if (mappingAttribute.equals(ID)) {
			transfer2cyattributes(primaryKey, aliasSet, parts);	
		} else {
			/*
			 * Case 2: use an attribute as the key.
			 */
			
			List<String> objectIDs = null;
			for (String id : aliasSet) {
				if (attr2id.containsKey(id)) {
					objectIDs = attr2id.get(id);

					for (String objectID : objectIDs) {
						existingAliases.add(objectID, new ArrayList<String>(aliasSet));
					}
					break;
				}
			}
			if (objectIDs != null) {
				for (String key : objectIDs) {
					transfer2cyattributes(key, aliasSet, parts);
				}
			}
		}

	}

	private void transfer2cyattributes(String primaryKey, Set<String> aliasSet,
			String[] parts) {

		String altKey = null;
		/*
		 * Search the key
		 */
		switch (objType) {
		case NODE:

			Node node = Cytoscape.getCyNode(primaryKey);
			if (node == null) {
				for (String alias : aliasSet) {
					node = Cytoscape.getCyNode(alias);
					if (node != null) {
						altKey = alias;
						break;
					}
				}
				if (node == null) {
					return;
				}
			} else {
				break;
			}

			break;
		case EDGE:
			Edge edge = Cytoscape.getRootGraph().getEdge(primaryKey);
			if (edge == null) {
				for (String alias : aliasSet) {
					edge = Cytoscape.getRootGraph().getEdge(alias);
					if (edge != null) {
						altKey = alias;
						break;
					}
				}
				if (edge == null) {
					return;
				}
			} else {
				break;
			}

			break;
		case NETWORK:
			/*
			 * This is a special case: Since network IDs are only integers and
			 * not always the same, we need to use title instead of ID.
			 */
			Set<CyNetwork> networkSet = Cytoscape.getNetworkSet();
			Map<String, String> titleMap = new TreeMap<String, String>();
			for (CyNetwork net : networkSet) {
				titleMap.put(net.getTitle(), net.getIdentifier());
			}

			String targetNetworkID = null;
			if (titleMap.containsKey(primaryKey)) {
				targetNetworkID = titleMap.get(primaryKey);
				break;
			}

			if (targetNetworkID == null) {
				for (String alias : aliasSet) {
					if (titleMap.containsKey(alias)) {
						targetNetworkID = titleMap.get(alias);
						break;
					}
				}
			}
			if (targetNetworkID == null) {
				/*
				 * Network not found: just ignore this line.
				 */
				return;
			}
			break;
		default:
			attributes = null;
		}

		/*
		 * Now, transfer entries into CyAttributes.
		 */
		for (int i = 0; i < parts.length; i++) {
			if (i != key && !aliasColumns.contains(i) && importFlag[i]) {
				/*
				 * Frist, check the node exists or not with the primary key
				 */
				if (altKey == null) {
					attributes.setAttribute(primaryKey, columnNames[i],
							parts[i].trim());
				} else {
					attributes.setAttribute(altKey, columnNames[i], parts[i]
							.trim());
				}
			}
		}
		
		/*
		 * Finally, add aliases
		 */
		if(altKey == null) {
			existingAliases.add(primaryKey, new ArrayList<String>(aliasSet));
		} else {
			existingAliases.add(altKey, new ArrayList<String>(aliasSet));
		}
	}
	
	/**
	 * Building hashmap for attribute <--> object ID mapping.
	 * 
	 */
	private void buildMap(Iterator it) {

		attr2id = new HashMap<String, List<String>>();

		String objectID = null;

		String attributeValue = null;
		List<String> objIdList = null;

		while (it.hasNext()) {
			switch (objType) {
			case NODE:
				Node node = (Node) it.next();
				objectID = node.getIdentifier();
				attributeValue = attributes.getStringAttribute(objectID,
						mappingAttribute);
				break;
			case EDGE:
				break;
			case NETWORK:
				break;
			default:

			}

			if (attributeValue != null) {
				if (attr2id.containsKey(attributeValue)) {
					objIdList = (List<String>) attr2id.get(attributeValue);
				} else {
					objIdList = new ArrayList<String>();
				}
				objIdList.add(objectID);
				attr2id.put(attributeValue, objIdList);
			}
		}

	}

}
