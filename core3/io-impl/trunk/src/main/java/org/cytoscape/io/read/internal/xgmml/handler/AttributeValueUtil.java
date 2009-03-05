package org.cytoscape.io.read.internal.xgmml.handler;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.io.read.internal.xgmml.MetadataEntries;
import org.cytoscape.io.read.internal.xgmml.MetadataParser;
import org.cytoscape.io.read.internal.xgmml.ObjectType;
import org.cytoscape.io.read.internal.xgmml.ObjectTypeMap;
import org.cytoscape.io.read.internal.xgmml.ParseState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.AttributesImpl;

public class AttributeValueUtil {

	private static final String ATTR_NAME = "name";
	private static final String ATTR_LABEL = "label";
	private static final String ATTR_VALUE = "value";

	
	private Locator locator;

	// Should be injected
	private ReadDataManager manager;
	private ObjectTypeMap typeMap;
	
	public void setTypeMap(ObjectTypeMap typeMap) {
		this.typeMap = typeMap;
	}
	
	public void setManager(ReadDataManager manager) {
		this.manager = manager;
	}

	public void setLocator(Locator locator) {
		this.locator = locator;
	}

	/********************************************************************
	 * Routines to handle attributes
	 *******************************************************************/

	/**
	 * Return the string attribute value for the attribute indicated by "key".
	 * If no such attribute exists, return null. In particular, this routine
	 * looks for an attribute with a <b>name</b> or <b>label</b> of <i>key</i>
	 * and returns the <b>value</b> of that attribute.
	 * 
	 * @param atts
	 *            the attributes
	 * @param key
	 *            the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	public String getAttributeValue(Attributes atts, String key) {

		String name = atts.getValue(ATTR_NAME);

		if (name == null)
			name = atts.getValue(ATTR_LABEL);

		if (name != null && name.equals(key))
			return atts.getValue(ATTR_VALUE);
		else
			return null;
	}

	/**
	 * Return the double attribute value for the attribute indicated by "key".
	 * If no such attribute exists, return null. In particular, this routine
	 * looks for an attribute with a <b>name</b> or <b>label</b> of <i>key</i>
	 * and returns the <b>value</b> of that attribute.
	 * 
	 * @param atts
	 *            the attributes
	 * @param key
	 *            the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	public double getDoubleAttributeValue(Attributes atts, String key)
			throws SAXParseException {

		final String attributeValue = getAttributeValue(atts, key);

		if (attributeValue == null)
			return 0.0;

		try {
			return Double.parseDouble(attributeValue);
		} catch (NumberFormatException e) {
			throw new SAXParseException("Unable to convert '" + attributeValue
					+ "' to a DOUBLE", locator);
		}
	}

	/**
	 * Return the Color attribute value for the attribute indicated by "key". If
	 * no such attribute exists, return null. In particular, this routine looks
	 * for an attribute with a <b>name</b> or <b>label</b> of <i>key</i> and
	 * returns the <b>value</b> of that attribute.
	 * 
	 * @param atts
	 *            the attributes
	 * @param key
	 *            the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	public Color getColorAttributeValue(Attributes atts, String key)
			throws SAXParseException {

		final String attribute = getAttributeValue(atts, key);

		if (attribute == null)
			return null;
		try {
			return new Color(Integer.parseInt(attribute.substring(1), 16));
		} catch (NumberFormatException e) {
			throw new SAXParseException("Unable to convert '" + attribute
					+ "' to a color", locator);
		}
	}

	/**
	 * Return the typed attribute value for the passed attribute. In this case,
	 * the caller has already determined that this is the correct attribute and
	 * we just lookup the value. This routine is responsible for type conversion
	 * consistent with the passed argument.
	 * 
	 * @param type
	 *            the ObjectType of the value
	 * @param atts
	 *            the attributes
	 * @return the value of the attribute in the appropriate type
	 */
	public Object getTypedAttributeValue(ObjectType type, Attributes atts)
			throws SAXParseException {

		String value = atts.getValue("value");

		try {
			return typeMap.getTypedValue(type, value);
		} catch (Exception e) {
			throw new SAXParseException("Unable to convert '" + value
					+ "' to type " + type.toString(), locator);
		}
	}

	/**
	 * Return the attribute value for the attribute indicated by "key". If no
	 * such attribute exists, return null.
	 * 
	 * @param atts
	 *            the attributes
	 * @param key
	 *            the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	public String getAttribute(Attributes atts, String key) {
		return atts.getValue(key);
	}

	/**
	 * Return the attribute value for the attribute indicated by "key". If no
	 * such attribute exists, return null.
	 * 
	 * @param atts
	 *            the attributes
	 * @param key
	 *            the specific attribute to get
	 * @param ns
	 *            the namespace for the attribute we're interested in
	 * @return the value for "key" or null if no such attribute exists
	 */
	public String getAttributeNS(Attributes atts, String key, String ns) {
		if (atts.getValue(ns, key) != null)
			return atts.getValue(ns, key);
		else
			return atts.getValue(key);
	}

	/**
	 * Return the integer attribute value for the attribute indicated by "key".
	 * If no such attribute exists, return null.
	 * 
	 * @param atts
	 *            the attributes
	 * @param key
	 *            the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	public int getIntegerAttribute(Attributes atts, String key) {
		String attribute = atts.getValue(key);
		if (attribute == null)
			return 0;
		return (new Integer(attribute)).intValue();
	}

	/**
	 * Return the integer attribute value for the attribute indicated by "key".
	 * If no such attribute exists, return null.
	 * 
	 * @param atts
	 *            the attributes
	 * @param key
	 *            the specific attribute to get
	 * @param ns
	 *            the namespace for the attribute we're interested in
	 * @return the value for "key" or null if no such attribute exists
	 */
	public int getIntegerAttributeNS(Attributes atts, String key, String ns) {
		String attribute = atts.getValue(ns, key);
		if (attribute == null)
			return 0;
		return (new Integer(attribute)).intValue();
	}

	/**
	 * Return the double attribute value for the attribute indicated by "key".
	 * If no such attribute exists, return null.
	 * 
	 * @param atts
	 *            the attributes
	 * @param key
	 *            the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	public double getDoubleAttribute(Attributes atts, String key) {
		String attribute = atts.getValue(key);
		if (attribute == null)
			return 0.0;
		return (new Double(attribute)).doubleValue();
	}

	/**
	 * Return the double attribute value for the attribute indicated by "key".
	 * If no such attribute exists, return null.
	 * 
	 * @param atts
	 *            the attributes
	 * @param key
	 *            the specific attribute to get
	 * @param ns
	 *            the namespace for the attribute we're interested in
	 * @return the value for "key" or null if no such attribute exists
	 */
	public double getDoubleAttributeNS(Attributes atts, String key, String ns) {
		String attribute = atts.getValue(ns, key);
		if (attribute == null)
			return 0;
		return (new Double(attribute)).doubleValue();
	}

	/**
	 * Return the Color attribute value for the attribute indicated by "key". If
	 * no such attribute exists, return null.
	 * 
	 * @param atts
	 *            the attributes
	 * @param key
	 *            the specific attribute to get
	 * @return the value for "key" or null if no such attribute exists
	 */
	public Color getColorAttribute(Attributes atts, String key) {
		String attribute = atts.getValue(key);
		if (attribute == null)
			return null;
		return new Color(Integer.parseInt(attribute.substring(1), 16));
	}

	public ParseState handleAttribute(Attributes atts, CyRow cyAtts)
			throws SAXParseException {
		String name = atts.getValue("name");
		ObjectType objType = typeMap.getType(atts.getValue("type"));
		Object obj = getTypedAttributeValue(objType, atts);

		switch (objType) {
		case BOOLEAN:
			if (obj != null && name != null)
				cyAtts.set(name, (Boolean) obj);
			break;
		case REAL:
			if (obj != null && name != null)
				cyAtts.set(name, (Double) obj);
			break;
		case INTEGER:
			if (obj != null && name != null)
				cyAtts.set(name, (Integer) obj);
			break;
		case STRING:
			if (obj != null && name != null)
				cyAtts.set(name, (String) obj);
			break;

		// We need to be *very* careful. Because we duplicate attributes for
		// each network we write out, we wind up reading and processing each
		// attribute multiple times, once for each network. This isn't a problem
		// for "base" attributes, but is a significant problem for attributes
		// like LIST and MAP where we add to the attribute as we parse. So, we
		// must make sure to clear out any existing values before we parse.
		case LIST:
			manager.currentAttributeID = name;
			if (List.class == cyAtts.contains(name))
				cyAtts.set(name, null);
			return ParseState.LISTATT;
		case MAP:
			manager.currentAttributeID = name;
			if (Map.class == cyAtts.contains(name))
				cyAtts.set(name, null);
			return ParseState.MAPATT;
		case COMPLEX:
			manager.currentAttributeID = name;
			if (Map.class == cyAtts.contains(name)) // assuming complex will
				// become Map
				cyAtts.set(name, null);
			// If this is a complex attribute, we know that the value attribute
			// is an integer
			manager.numKeys = Integer.parseInt(atts.getValue("value"));
			manager.complexMap = new HashMap[manager.numKeys];
			manager.complexKey = new Object[manager.numKeys];
			manager.attributeDefinition = new byte[manager.numKeys];
			manager.level = 0;
			return ParseState.COMPLEXATT;
		}
		return ParseState.NONE;
	}

	public void addAttributes(Attributes attI, Attributes atts) {

		final int attrLength = atts.getLength();

		for (int i = 0; i < attrLength; i++)
			((AttributesImpl) attI).addAttribute(atts.getURI(i), atts
					.getLocalName(i), atts.getQName(i), atts.getType(i), atts
					.getValue(i));
	}

	/**
	 * Given an ObjectType, method returns a MultiHashMapDefinition byte
	 * corresponding to its type.
	 * 
	 * @param objectType
	 *            - the type
	 * 
	 * @return - byte
	 */
	public byte getMultHashMapType(final ObjectType objectType) {
		switch (objectType) {
		case BOOLEAN:
			return (byte) 1;
		case STRING:
			return (byte) 4;
		case INTEGER:
			return (byte) 3;
		case REAL:
			return (byte) 2;
		}

		// outta here
		return -1;
	}

	public CyNode createUniqueNode(String label, String id) throws SAXException {
		if (label != null) {
			if (id == null)
				id = label;
			// System.out.print(" label=\""+label+"\"");
		}
		// OK, now actually create it
		CyNode node = manager.network.addNode();
		node.attrs().set("name", label);
		// System.out.println("Created new node("+label+") id="+node.getRootGraphIndex());

		// Add it our indices
		manager.nodeList.add(node);
		// System.out.println("Adding node "+node.getIdentifier()+"("+id+") to map");
		manager.idMap.put(id, node);
		return node;
	}

	public CyEdge createEdge(CyNode source, CyNode target, String label,
			boolean directed) throws SAXException {
		// OK create it
		CyEdge edge = manager.network.addEdge(source, target, directed);
		edge.attrs().set("name", label);

		manager.edgeList.add(edge);
		return edge;
	}

	public void setMetaData(CyNetwork network) {
		MetadataParser mdp = new MetadataParser(network);
		if (manager.RDFType != null)
			mdp.setMetadata(MetadataEntries.TYPE, manager.RDFType);
		if (manager.RDFDate != null)
			mdp.setMetadata(MetadataEntries.DATE, manager.RDFDate);
		if (manager.RDFTitle != null)
			mdp.setMetadata(MetadataEntries.TITLE, manager.RDFTitle);
		if (manager.RDFDescription != null)
			mdp
					.setMetadata(MetadataEntries.DESCRIPTION,
							manager.RDFDescription);
		if (manager.RDFSource != null)
			mdp.setMetadata(MetadataEntries.SOURCE, manager.RDFSource);
		if (manager.RDFFormat != null)
			mdp.setMetadata(MetadataEntries.FORMAT, manager.RDFFormat);
		if (manager.RDFIdentifier != null)
			mdp.setMetadata(MetadataEntries.IDENTIFIER, manager.RDFIdentifier);
	}

}
