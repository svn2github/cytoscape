package org.cytoscape.io.read.internal.xgmml.handler;

import org.cytoscape.io.read.internal.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HandleRDFType extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		manager.RDFType = null;
		return current;
	}
}