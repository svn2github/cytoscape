package org.cytoscape.io.read.internal.xgmml.handler;

import org.cytoscape.io.read.internal.xgmml.ObjectType;
import org.cytoscape.io.read.internal.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HandleMapAttribute extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
		String name = atts.getValue("name");
		ObjectType objType = typeMap.getType(atts.getValue("type"));
		Object obj = attributeValueUtil.getTypedAttributeValue(objType, atts);

		switch (objType) {
		case BOOLEAN:
		case REAL:
		case INTEGER:
		case STRING:
			manager.mapAttrHolder.put(name, obj);
		}

		return current;
	}
}