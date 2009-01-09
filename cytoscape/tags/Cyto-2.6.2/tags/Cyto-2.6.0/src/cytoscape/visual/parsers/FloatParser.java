package cytoscape.visual.parsers;

/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class FloatParser
    implements ValueParser {
    /**
     *  DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Object parseStringValue(String value) {
        return parseFloat(value);
    }

    /**
     *  DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Float parseFloat(String value) {
        Float f = Float.NaN;

        try {
            f = Float.parseFloat(value);

            return f;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
