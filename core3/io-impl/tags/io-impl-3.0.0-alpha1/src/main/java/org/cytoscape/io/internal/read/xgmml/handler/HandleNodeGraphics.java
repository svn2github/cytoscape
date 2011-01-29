package org.cytoscape.io.internal.read.xgmml.handler;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * handleNodeGraphics builds the objects that will remember the node graphic
 * information until we do the actual layout. Unfortunately, the way the readers
 * work, we can't apply the graphics information until we do the actual layout.
 */
public class HandleNodeGraphics extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		if (tag.equals("graphics")) {
			if (manager.nodeGraphicsMap.containsKey(manager.currentNode))
				attributeValueUtil.addAttributes(manager.nodeGraphicsMap
						.get(manager.currentNode), atts);
			else
				manager.nodeGraphicsMap.put(manager.currentNode,
						new AttributesImpl(atts));
		} else if (tag.equals("att")) {
			// Handle special node graphics attributes
			String name = atts.getValue("name");
			if (name != null && !name.equals("cytoscapeNodeGraphicsAttributes")) {
				// Add this as a graphics attribute to the end of our list
				((AttributesImpl) manager.nodeGraphicsMap
						.get(manager.currentNode)).addAttribute("", "", "cy:"
						+ name, "string", atts.getValue("value"));
			}
		}
		return current;
	}
}
