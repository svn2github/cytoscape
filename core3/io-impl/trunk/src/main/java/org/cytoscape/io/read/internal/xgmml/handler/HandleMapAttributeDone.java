package org.cytoscape.io.read.internal.xgmml.handler;

import org.cytoscape.io.read.internal.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HandleMapAttributeDone extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {

		try {
			if (manager.mapAttrHolder != null) {
				manager.currentAttributes.set(manager.currentAttributeID,
						manager.mapAttrHolder);
				manager.mapAttrHolder = null;
			}
		} catch (Exception e) {
			String err = "XGMML attribute handling error for attribute '"
					+ manager.currentAttributeID + "' and object '"
					+ manager.objectTarget + "': " + e.getMessage();
			throw new SAXException(err);
		}
		return current;
	}
}