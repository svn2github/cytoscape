package org.cytoscape.io.internal.read.xgmml.handler;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HandleEdgeAttribute extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {

		manager.attState = current;

		// TODO what if currentEdge is null?
		manager.currentAttributes = manager.currentEdge.attrs();
		ParseState nextState = attributeValueUtil.handleAttribute(atts,
				manager.currentAttributes);
		if (nextState != ParseState.NONE)
			return nextState;
		return current;
	}
}