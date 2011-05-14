package org.cytoscape.io.internal.read.xgmml.handler;

import java.util.Map;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.cytoscape.model.CyEdge;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class HandleEdgeAttribute extends AbstractHandler {

    public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
        manager.attState = current;

        // Is this a graphics override?
        String name = atts.getValue("name");

        // Check for blank attribute
        if (name == null && atts.getValue("value") == null) return current;

        if (name.startsWith("edge.")) {
            // It is a bypass attribute...
            name = name.replace(".", "").toLowerCase();
            String value = atts.getValue("value");
            Map<CyEdge, Attributes> graphics = manager.getEdgeGraphics();

            if (!graphics.containsKey(manager.currentEdge)) {
                graphics.put(manager.currentEdge, new AttributesImpl());
            }

            ((AttributesImpl) graphics.get(manager.currentEdge)).addAttribute("", "", name, "string", value);
        }

        // TODO what if currentEdge is null?
        manager.currentAttributes = manager.currentEdge.getCyRow();
        ParseState nextState = attributeValueUtil.handleAttribute(atts, manager.currentAttributes);
        
        if (nextState != ParseState.NONE)
            return nextState;
        
        return current;
    }
}
