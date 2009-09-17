package org.cytoscape.io.internal.read.xgmml.handler;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class HandleNodeAttribute extends AbstractHandler {

	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		if (atts == null)
			return current;

		manager.attState = current;
		// System.out.println("Node attribute: "+printAttribute(atts));
		// Is this a graphics override?
		String name = atts.getValue("name");

		// Check for blank attribute (e.g. surrounding a group)
		if (name == null && atts.getValue("value") == null)
			return current;

		if (name.startsWith("node.")) {
			// Yes, add it to our nodeGraphicsMap
			name = atts.getValue("name").substring(5);
			String value = atts.getValue("value");
			if (!manager.nodeGraphicsMap.containsKey(manager.currentNode)) {
				manager.nodeGraphicsMap.put(manager.currentNode,
						new AttributesImpl());
			}
			((AttributesImpl) manager.nodeGraphicsMap.get(manager.currentNode))
					.addAttribute("", "", name, "string", value);
		}

		manager.currentAttributes = manager.currentNode.attrs();
		ParseState nextState = attributeValueUtil.handleAttribute(atts,
				manager.currentAttributes);
		if (nextState != ParseState.NONE)
			return nextState;
		return current;
	}
}
