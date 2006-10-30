package cytoscape.data.readers;

import giny.model.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.TextTableReader.ObjectType;
import cytoscape.data.synonyms.Aliases;

import static cytoscape.data.readers.TextFileDelimiters.*;

/**
 * Parameter object for text table <---> CyAttributes mapping.<br>
 * <p>
 *  This object will be used by all attribute readers.
 * </p>
 * 
 * @since Cytoscape 2.4
 * @version 0.9
 * @author Keiichiro Ono
 * 
 */
public class AttributeMappingParameters implements MappingParameter {

	public static final String ID = "ID";
	private static final String DEF_LIST_DELIMITER = PIPE.toString();
	private static final String DEF_DELIMITER = TAB.toString();
	
	private final ObjectType objectType;
	private final int keyIndex;
	private final List<Integer> aliasIndex;
	private String[] attributeNames;
	private byte[] attributeTypes;
	private final String mappingAttribute;

	private List<String> delimiters;
	private String listDelimiter;

	private boolean[] importFlag;
	
	private Map<String, List<String>> attr2id;
	
	private CyAttributes attributes;
	private Aliases existingAliases;

	public AttributeMappingParameters(final ObjectType objectType,
			final List<String> delimiters, final String listDelimiter,
			final int keyIndex, final String mappingAttribute,
			final List<Integer> aliasIndex, final String[] attributeNames,
			byte[] attributeTypes, boolean[] importFlag) throws Exception {

		if(attributeNames == null) {
			throw new Exception("attributeNames should not be null.");
		}
		
		/*
		 * Error check: Key column number should be smaller than actual number
		 * of columns in the text table.
		 */
		if (attributeNames.length < keyIndex) {
			throw new IOException("Key is out of range.");
		}
		
		/*
		 * These calues should not be null!
		 */
		this.objectType = objectType;
		this.keyIndex = keyIndex;
		this.attributeNames = attributeNames;
		
		
		/*
		 * If attribute mapping is null, use ID for mapping.
		 */
		if(mappingAttribute == null) {
			this.mappingAttribute = ID;
		} else {
			this.mappingAttribute = mappingAttribute;
		}
		
		/*
		 * If delimiter is not available, use default value (TAB)
		 */
		if(delimiters == null) {
			this.delimiters = new ArrayList<String>();
			this.delimiters.add(DEF_DELIMITER);
		} else {
			this.delimiters = delimiters;
		}

		/*
		 * If list delimiter is null, use default "|"
		 */
		if (listDelimiter == null) {
			this.listDelimiter = DEF_LIST_DELIMITER;
		} else {
			this.listDelimiter = listDelimiter;
		}

		if(aliasIndex == null) {
			this.aliasIndex = new ArrayList<Integer>();
		} else {
			this.aliasIndex = aliasIndex;
		}

		
		/*
		 * If not specified, import everything as String attributes.
		 */
		if (attributeTypes == null) {
			this.attributeTypes = new byte[attributeNames.length];
			for (int i = 0; i < attributeNames.length; i++) {
				this.attributeTypes[i] = CyAttributes.TYPE_STRING;
			}
		} else {
			this.attributeTypes = attributeTypes;
		}
		
		/*
		 * If not specified, import everything.
		 */
		if (importFlag == null) {
			this.importFlag = new boolean[attributeNames.length];
			for (int i=0; i < this.importFlag.length; i++) {
				this.importFlag[i] = true;
			}
		} else {
			this.importFlag = importFlag;
		}


		final Iterator it;
		switch (objectType) {
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
			buildAttribute2IDMap(it);
		}
		
	}
	
	public Aliases getAlias() {
		return existingAliases;
	}
	
	public CyAttributes getAttributes() {
		return attributes;
	}

	public List<Integer> getAliasIndexList() {

		return aliasIndex;
	}

	public String[] getAttributeNames() {
		// TODO Auto-generated method stub
		return attributeNames;
	}

	public byte[] getAttributeTypes() {
		// TODO Auto-generated method stub
		return attributeTypes;
	}

	public boolean[] getImportFlag() {
		// TODO Auto-generated method stub
		return importFlag;
	}

	public int getKeyIndex() {
		// TODO Auto-generated method stub
		return keyIndex;
	}

	public String getListDelimiter() {
		// TODO Auto-generated method stub
		return listDelimiter;
	}

	public String getMappingAttribute() {
		return mappingAttribute;
	}

	public ObjectType getObjectType() {
		// TODO Auto-generated method stub
		return objectType;
	}

	public List<String> getDelimiters() {
		return delimiters;
	}
	
	public String getDelimiterRegEx() {
		StringBuffer delimiterBuffer = new StringBuffer();
		delimiterBuffer.append("[");
		for (String delimiter : delimiters) {
			delimiterBuffer.append(delimiter);
		}
		delimiterBuffer.append("]");
		return delimiterBuffer.toString();
	}
	
	public List<String> toID(String attributeValue) {
		return attr2id.get(attributeValue);
	}
	
	public Map<String, List<String>> getAttributeToIDMap() {
		return attr2id;
	}
	/**
	 * Building hashmap for attribute <--> object ID mapping.
	 * 
	 */
	private void buildAttribute2IDMap(Iterator it) {

		attr2id = new HashMap<String, List<String>>();

		String objectID = null;

		String attributeValue = null;
		List<String> objIdList = null;

		while (it.hasNext()) {
			switch (objectType) {
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

	public int getColumnCount() {
		// TODO Auto-generated method stub
		return attributeNames.length;
	}

}
