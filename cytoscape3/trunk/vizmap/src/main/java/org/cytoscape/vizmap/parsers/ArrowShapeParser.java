package org.cytoscape.vizmap.parsers;

import org.cytoscape.vizmap.ArrowShape;
import org.cytoscape.vizmap.ValueParser;

public class ArrowShapeParser implements ValueParser {
	
	/**
     *  DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object parseStringValue(String value) {
        return parseArrowShape(value); 
    }

    /**
     *  DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public ArrowShape parseArrowShape(String value) {
        return ArrowShape.parseArrowText(value); 
    }
}
