package org.cytoscape.io.internal.read.xgmml.handler;

import java.util.HashMap;
import java.util.Map;

import org.cytoscape.io.internal.read.xgmml.ObjectType;
import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * handleComplexAttribute attempts to read an arbitrarily complex attribute map
 * from the XGMML file. For our purposes, a complex attribute map is defined as
 * a HashMap which has as its values Maps. For example, consider a pseudo hash
 * with the following structure:
 * 
 * {"externalref1"}->{"authors"}->{1}->"author1 name";
 * {"externalref1"}->{"authors"}->{2}->"author2 name";
 * {"externalref1"}->{"authors"}->{3}->"author3 name";
 * 
 * where the keys externalref1 and authors are strings, and keys 1, 2, 3 are
 * integers, and the values (author1 name, author2 name, author3 name) are
 * strings, we would have the following attributes written to the xgmml file:
 * 
 * <att type="complex" name="publication references" value="3"> <att
 * type="string" name="externalref1" value="1"> <att type="string"
 * name="authors" value="3"> <att type="int" name="2" value="1"> <att
 * type="string" value="author2 name"/> </att> <att type="int" name="1"
 * value="1"> <att type="string" value="author1 name"/> </att> <att type="int"
 * name="3" value="1"> <att type="string" value="author3 name"/> </att> </att>
 * </att> </att>
 * 
 * Notes: - value attribute property for keys is assigned the number of
 * sub-elements the key references - value attribute property for values is
 * equal to the value - name attribute property for attributes is only set for
 * keys, and the value of this property is the key name. - label attribute
 * property is equal to the data type of the key or value. - name attribute
 * properties are only set for keys
 */
public class HandleComplexAttribute extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		// We can't create the complex attribute until we know what the
		// definition is, but
		// since a complex attribute is really nothing more than a HashMap with
		// a String
		// key and Map values, we can create it on the fly.

		// Get our attributes
		ObjectType type = typeMap.getType(atts.getValue("type"));
		String value = atts.getValue("value");
		// System.out.println("Complex attribute: "+currentAttributeID+" level "+level+" value="+atts.getValue("value"));

		if (manager.level == manager.numKeys) {
			manager.complexMap[manager.level - 1].put(
					manager.complexKey[manager.level - 1], typeMap
							.getTypedValue(type, value));
			manager.valueType = attributeValueUtil.getMultHashMapType(type);
			// See if we've defined the attribute already
			if (Map.class == manager.currentAttributes
					.contains(manager.currentAttributeID)) {
				manager.currentAttributes.getDataTable().createColumn(
						manager.currentAttributeID, Map.class, false);
			}
			// Now define set the attribute
			if (manager.objectTarget != null)
				manager.currentAttributes.set(manager.currentAttributeID,
						typeMap.getTypedValue(type, value)); // ??
		} else if (manager.level == 0) {
			if (manager.complexMap[manager.level] == null) {
				manager.complexMap[manager.level] = new HashMap();
			}
			manager.complexKey[manager.level] = typeMap.getTypedValue(type,
					atts.getValue("name"));
			manager.attributeDefinition[manager.level] = attributeValueUtil
					.getMultHashMapType(type);
		} else {
			if (manager.complexMap[manager.level] == null) {
				manager.complexMap[manager.level] = new HashMap();
			}
			manager.complexMap[manager.level - 1].put(
					manager.complexKey[manager.level - 1],
					manager.complexMap[manager.level]);
			manager.complexKey[manager.level] = typeMap.getTypedValue(type,
					atts.getValue("name"));
			manager.attributeDefinition[manager.level] = attributeValueUtil
					.getMultHashMapType(type);
		}
		manager.level++;

		return current;
	}
}
