package org.cytoscape.io.internal.read.xgmml.handler;

import org.cytoscape.io.internal.read.xgmml.ObjectType;
import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class HandleListAttribute extends AbstractHandler {
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException {
	    String type = atts.getValue("type");
		ObjectType objType = typeMap.getType(type);
		Object obj = attributeValueUtil.getTypedAttributeValue(objType, atts);

		switch (objType) {
		case BOOLEAN:
		case REAL:
		case INTEGER:
		case STRING:
		    if (manager.listAttrHolder != null)
		        manager.listAttrHolder.add(obj);
		}
		return current;
	}
}