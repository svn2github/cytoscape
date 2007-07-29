package cytoscape.bubbleRouter;

import giny.model.GraphObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;

/**
 * A set of Misc. Utility Methods for Accesssing CyAttribute data.
 * 
 * @author Ethan Cerami
 */
public class BRCyAttributesUtil {

	/**
	 * Regardless of attribute type, this method will always return attribute
	 * values as an array of String objects. For example, given an attribute of
	 * type: CyAttributes.TYPE_INTEGER with a single value = 25, this method
	 * will return an array of size 1 = ["25"]. The method will return null if
	 * no attribute value is found, or if the attribute is of type
	 * {@link CyAttributes#TYPE_COMPLEX}.
	 * 
	 * @param attributes
	 *            CyAttributes Object.
	 * @param graphObjectId
	 *            Graph Object ID.
	 * @param attributeKey
	 *            Attribute Key.
	 * @return array of String Objects or null.
	 */
	public static String[] getAttributeValues(CyAttributes attributes,
			String graphObjectId, String attributeKey) {
		String terms[] = new String[1];
		if (attributeKey.equals(Semantics.CANONICAL_NAME)) {
			terms[0] = graphObjectId;
		} else {
			boolean hasAttribute = attributes.hasAttribute(graphObjectId,
					attributeKey);
			if (hasAttribute) {
				// Convert all types to String array.
				byte type = attributes.getType(attributeKey);
				if (type == CyAttributes.TYPE_BOOLEAN) {
					terms[0] = attributes.getBooleanAttribute(graphObjectId,
							attributeKey).toString();
				} else if (type == CyAttributes.TYPE_INTEGER) {
					terms[0] = attributes.getIntegerAttribute(graphObjectId,
							attributeKey).toString();
				} else if (type == CyAttributes.TYPE_FLOATING) {
					terms[0] = attributes.getDoubleAttribute(graphObjectId,
							attributeKey).toString();
				} else if (type == CyAttributes.TYPE_STRING) {
					terms[0] = attributes.getStringAttribute(graphObjectId,
							attributeKey);
				} else if (type == CyAttributes.TYPE_SIMPLE_LIST) {
					List list = attributes.getListAttribute(graphObjectId,
							attributeKey);

					// Iterate through all elements in the list
					if (list != null && list.size() > 0) {
						terms = new String[list.size()];
						for (int i = 0; i < list.size(); i++) {
							Object o = list.get(i);
							terms[i] = o.toString();
						}
					}
				} else if (type == CyAttributes.TYPE_SIMPLE_MAP) {
					Map map = attributes.getMapAttribute(graphObjectId,
							attributeKey);

					// Iterate through all values in the map
					if (map != null && map.size() > 0) {
						terms = new String[map.size()];
						Iterator mapIterator = map.values().iterator();
						int index = 0;
						while (mapIterator.hasNext()) {
							Object o = mapIterator.next();
							terms[index++] = o.toString();
						}
					}
				} else if (type == CyAttributes.TYPE_COMPLEX) {
					return null;
				}
			} else {
				return null;
			}
		}
		// Remove all new line chars
		for (int i = 0; i < terms.length; i++) {
			terms[i] = terms[i].replaceAll("\n", " ");
		}
		return terms;
	}

	/**
	 * Method returns the first X distinct attribute values.
	 * 
	 * @param iterator
	 *            Iterator of nodes or edges.
	 * @param attributes
	 *            Node or Edge Attributes.
	 * @param attributeKey
	 *            Attribute Key.
	 * @param numDistinctValues
	 *            Number of Distinct Values.
	 * @return Array of Distinct Value Strings.
	 */
	public static String[] getDistinctAttributeValues(Iterator iterator,
			CyAttributes attributes, String attributeKey, int numDistinctValues) {
		HashSet<Object> set = new HashSet<Object>();
		int counter = 0;
		while (iterator.hasNext() && counter < numDistinctValues) {
			GraphObject graphObject = (GraphObject) iterator.next();
			String values[] = BRCyAttributesUtil.getAttributeValues(attributes,
					graphObject.getIdentifier(), attributeKey);
			if (values != null && values.length > 0) {
				String singleStr = join(values);
				if (!set.contains(singleStr)) {
					set.add(singleStr);
					counter++;
				}
			}
		}
		if (set.size() > 0) {
			return (String[]) set.toArray(new String[0]);
		} else {
			return null;
		}
	}

	/**
	 * Joins a list of Strings with ,
	 * 
	 * @param values
	 *            Array of String Objects.
	 * @return One string with each value separate by a comma.
	 */
	private static String join(String values[]) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < values.length; i++) {
			buf.append(values[i]);
			if (i < values.length - 1) {
				buf.append(", ");
			}
		}
		return buf.toString();
	}
}