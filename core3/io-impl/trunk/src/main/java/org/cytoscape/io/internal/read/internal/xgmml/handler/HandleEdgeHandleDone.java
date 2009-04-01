package org.cytoscape.io.read.internal.xgmml.handler;

import org.cytoscape.io.read.internal.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HandleEdgeHandleDone extends AbstractHandler {

	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		if (manager.edgeBendX != null && manager.edgeBendY != null
				&& manager.handleList != null) {
			manager.handleList.add(manager.edgeBendX + "," + manager.edgeBendY);
			manager.edgeBendX = null;
			manager.edgeBendY = null;
		}
		return current;
	}
}