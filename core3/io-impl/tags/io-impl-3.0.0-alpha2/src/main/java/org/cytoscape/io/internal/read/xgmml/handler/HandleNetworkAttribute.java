package org.cytoscape.io.internal.read.xgmml.handler;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HandleNetworkAttribute extends AbstractHandler {

	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		manager.attState = current;
		ParseState nextState = current;
		// Look for "special" network attributes
		if (attributeValueUtil.getAttributeValue(atts, "backgroundColor") != null) {
			manager.backgroundColor = attributeValueUtil.getAttributeValue(
					atts, "backgroundColor");
		} else if (attributeValueUtil.getAttributeValue(atts, "GRAPH_VIEW_ZOOM") != null) {
		    manager.graphZoom = attributeValueUtil.getAttributeValue(atts, "GRAPH_VIEW_ZOOM");
		} else if (attributeValueUtil.getAttributeValue(atts, "GRAPH_VIEW_CENTER_X") != null) {
		    manager.graphCenterX = attributeValueUtil.getAttributeValue(atts, "GRAPH_VIEW_CENTER_X");
		} else if (attributeValueUtil.getAttributeValue(atts, "GRAPH_VIEW_CENTER_Y") != null) {
		    manager.graphCenterY = attributeValueUtil.getAttributeValue(atts, "GRAPH_VIEW_CENTER_Y");
		} else {
			manager.objectTarget = manager.networkName;
			manager.currentAttributes = manager.network.getCyRow();
			nextState = attributeValueUtil.handleAttribute(atts,
					manager.currentAttributes);
		}

		if (nextState != ParseState.NONE)
			return nextState;

		return current;
	}
}
