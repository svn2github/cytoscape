package org.cytoscape.io.internal.read.xgmml.handler;

import java.util.Map;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.cytoscape.model.CyNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class HandleNodeAttribute extends AbstractHandler {

    @Override
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
			// It is a bypass attribute...
			name = name.replace(".", "").toLowerCase();
			String value = atts.getValue("value");
            Map<CyNode, Attributes> graphics = manager.getNodeGraphics();
			
			if (!graphics.containsKey(manager.currentNode)) {
			    graphics.put(manager.currentNode, new AttributesImpl());
			}
			
			((AttributesImpl) graphics.get(manager.currentNode)).addAttribute("", "", name, "string", value);
		}

		manager.currentAttributes = manager.currentNode.getCyRow();
        ParseState nextState = attributeValueUtil.handleAttribute(atts, manager.currentAttributes);
		
		if (nextState != ParseState.NONE)
			return nextState;
		
		return current;
	}
}
