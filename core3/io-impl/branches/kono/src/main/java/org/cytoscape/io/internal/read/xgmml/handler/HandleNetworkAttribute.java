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
		} else {
			manager.objectTarget = manager.networkName;
			manager.currentAttributes = manager.network.attrs();
			nextState = attributeValueUtil.handleAttribute(atts,
					manager.currentAttributes);
		}

		if (nextState != ParseState.NONE)
			return nextState;

		return current;
	}
}
