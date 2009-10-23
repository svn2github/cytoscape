package org.cytoscape.io.internal.read.xgmml.handler;

import java.util.ArrayList;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class HandleEdgeGraphics extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		// System.out.println("Atts for "+currentEdge.getIdentifier()+": "+printAttributes(atts));
		if (tag.equals("graphics")) {
			if (manager.edgeGraphicsMap.containsKey(manager.currentEdge)) {
				attributeValueUtil.addAttributes(manager.edgeGraphicsMap
						.get(manager.currentEdge), atts);
			} else
				manager.edgeGraphicsMap.put(manager.currentEdge,
						new AttributesImpl(atts));
		} else if (tag.equals("att")) {
			// Handle special edge graphics attributes
			String name = atts.getValue("name");
			if (name != null && name.equals("edgeBend")) {
				manager.handleList = new ArrayList<String>();
				return ParseState.EDGEBEND;
			} else if (name != null
					&& !name.equals("cytoscapeEdgeGraphicsAttributes")) {
				// Add this as a graphics attribute to the end of our list
				((AttributesImpl) manager.edgeGraphicsMap
						.get(manager.currentEdge)).addAttribute("", "", "cy:"
						+ name, "string", atts.getValue("value"));
			}
		}
		return current;
	}
}