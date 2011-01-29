package org.cytoscape.io.internal.read.xgmml.handler;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HandleComplexAttributeDone extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		if (manager.level == 0) {
			// We are done, and have read in all of our attributes
			// System.out.println("Complex attribute "+currentAttributeID+" ComplexMap["+level+"] = "+complexMap[level]);
		} else if (manager.level < manager.numKeys) {
			manager.complexMap[manager.level] = null;
			manager.complexKey[manager.level] = null;
		}
		// Decrement our depth
		manager.level--;
		return current;
	}
}