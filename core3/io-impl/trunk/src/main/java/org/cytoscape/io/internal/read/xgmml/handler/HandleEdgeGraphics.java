package org.cytoscape.io.internal.read.xgmml.handler;

import java.util.ArrayList;
import java.util.Map;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class HandleEdgeGraphics extends AbstractHandler {

    public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
        Map<CyEdge, Attributes> graphicsMap = manager.getEdgeGraphics();

        if (tag.equals("graphics")) {
            if (graphicsMap.containsKey(manager.currentEdge)) {
                attributeValueUtil.addAttributes(graphicsMap.get(manager.currentEdge), atts);
            } else
                graphicsMap.put(manager.currentEdge, new AttributesImpl(atts));
        } else if (tag.equals("att")) {
            // Handle special edge graphics attributes
            String name = atts.getValue("name");
            if (name != null && name.equals("edgeBend")) {
                manager.handleList = new ArrayList<String>();
                return ParseState.EDGEBEND;
            } else if (name != null && !name.equals("cytoscapeEdgeGraphicsAttributes")) {
                // Add this as a graphics attribute to the end of our list
                ((AttributesImpl) graphicsMap.get(manager.currentEdge)).addAttribute("",
                                                                                     "",
                                                                                     "cy:" + name,
                                                                                     "string",
                                                                                     atts.getValue("value"));
            }
        }
        return current;
    }
}
