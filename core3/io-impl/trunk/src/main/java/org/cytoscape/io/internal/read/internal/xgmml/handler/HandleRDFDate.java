package org.cytoscape.io.read.internal.xgmml.handler;

import org.cytoscape.io.read.internal.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HandleRDFDate extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		manager.RDFDate = null;
		return current;
	}
}