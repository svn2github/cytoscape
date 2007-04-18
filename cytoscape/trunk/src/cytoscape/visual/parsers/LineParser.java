package cytoscape.visual.parsers;

import cytoscape.visual.Line;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class LineParser
    implements ValueParser {
    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object parseStringValue(String value) {
        return parseLine(value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Line parseLine(String value) {
        return Line.parseLineText(value);
    }
}
