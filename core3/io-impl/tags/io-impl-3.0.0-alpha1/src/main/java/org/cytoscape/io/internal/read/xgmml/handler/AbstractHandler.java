package org.cytoscape.io.internal.read.xgmml.handler;

import org.cytoscape.io.internal.read.xgmml.Handler;
import org.cytoscape.io.internal.read.xgmml.ObjectTypeMap;
import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class AbstractHandler implements Handler {

	protected ReadDataManager manager;

	protected AttributeValueUtil attributeValueUtil;
	
	ObjectTypeMap typeMap;

	public AbstractHandler() {

	}

	abstract public ParseState handle(String tag, Attributes atts,
			ParseState current) throws SAXException;

	public void setManager(ReadDataManager manager) {
		this.manager = manager;
	}

	public void setAttributeValueUtil(AttributeValueUtil attributeValueUtil) {
		this.attributeValueUtil = attributeValueUtil;
	}

}
