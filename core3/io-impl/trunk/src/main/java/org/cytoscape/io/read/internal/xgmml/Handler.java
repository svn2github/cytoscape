package org.cytoscape.io.read.internal.xgmml;

import org.cytoscape.io.read.internal.xgmml.handler.AttributeValueUtil;
import org.cytoscape.io.read.internal.xgmml.handler.ReadDataManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public interface Handler {
	
	public ParseState handle(String tag, Attributes atts, ParseState current)
			throws SAXException;
	
	public void setManager(ReadDataManager manager);
	
	public void setAttributeValueUtil(AttributeValueUtil attributeValueUtil);

}