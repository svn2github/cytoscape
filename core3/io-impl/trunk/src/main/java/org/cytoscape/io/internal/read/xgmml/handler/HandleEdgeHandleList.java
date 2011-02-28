package org.cytoscape.io.internal.read.xgmml.handler;

import java.util.Map;

import org.cytoscape.io.internal.read.xgmml.ParseState;
import org.cytoscape.model.CyEdge;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class HandleEdgeHandleList extends AbstractHandler {

    public ParseState handle(String tag, Attributes atts, ParseState current) throws SAXException {
        if (manager.handleList != null) {
            String list = "";

            for (int i = 0; i < manager.handleList.size(); i++) {
                if (i != (manager.handleList.size() - 1)) {
                    list += manager.handleList.get(i) + ";";
                } else {
                    list += manager.handleList.get(i);
                }
            }

            Map<CyEdge, Attributes> graphicsMap = manager.getEdgeGraphics();

            // Add this as a graphics attribute to the end of our list
            ((AttributesImpl) graphicsMap.get(manager.currentEdge)).addAttribute("",
                                                                                 "",
                                                                                 "edgeHandleList",
                                                                                 "string",
                                                                                 list);
            manager.handleList = null;
        }
        return current;
    }
}
