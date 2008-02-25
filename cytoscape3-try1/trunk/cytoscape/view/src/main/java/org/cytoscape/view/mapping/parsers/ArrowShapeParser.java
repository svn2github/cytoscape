package main.java.org.cytoscape.view.mapping.parsers;

import org.cytoscape.application.widget.vizmap.shape.ArrowShape;

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
