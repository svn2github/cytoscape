package cytoscape.visual;

import java.awt.Stroke;


/**
 * Define a line. This will be used as edge or node border.
 *
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public class Line {
    /**
     * DOCUMENT ME!
     */
    public static final Line DEFAULT_LINE = new Line(LineStyle.SOLID, 1.0f);

    // Define line type (stroke).
    private LineStyle type;

    // Width of this line.
    private Float width;

    /**
     * Creates a new Line object.
     *
     * @param type DOCUMENT ME!
     * @param width DOCUMENT ME!
     */
    public Line(LineStyle type, Float width) {
        this.type = type;
        this.width = width;
    }

    /**
     * DOCUMENT ME!
     *
     * @param width DOCUMENT ME!
     */
    public void setWidth(final float width) {
        this.width = width;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    public void setType(final LineStyle type) {
        this.type = type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public float getWidth() {
        return width;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public LineStyle getType() {
        return type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Stroke getStroke() {
        return type.getStroke(width);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return type.name();
    }

    /**
     * DOCUMENT ME!
     *
     * @param text DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Line parseLineText(String text) {
        String lttext = text.trim();
        lttext = lttext.replaceAll("_", "");

        if (lttext.equalsIgnoreCase("dashed1"))
            return new Line(LineStyle.LONG_DASH, 1.0f);
        else if (lttext.equalsIgnoreCase("dashed2"))
            return new Line(LineStyle.LONG_DASH, 2.0f);
        else if (lttext.equalsIgnoreCase("dashed3"))
            return new Line(LineStyle.LONG_DASH, 3.0f);
        else if (lttext.equalsIgnoreCase("dashed4"))
            return new Line(LineStyle.LONG_DASH, 4.0f);
        else if (lttext.equalsIgnoreCase("dashed5"))
            return new Line(LineStyle.LONG_DASH, 5.0f);
        else if (lttext.equalsIgnoreCase("line1"))
            return Line.DEFAULT_LINE;
        else if (lttext.equalsIgnoreCase("line2"))
            return new Line(LineStyle.SOLID, 2.0f);
        else if (lttext.equalsIgnoreCase("line3"))
            return new Line(LineStyle.SOLID, 3.0f);
        else if (lttext.equalsIgnoreCase("line4"))
            return new Line(LineStyle.SOLID, 4.0f);
        else if (lttext.equalsIgnoreCase("line5"))
            return new Line(LineStyle.SOLID, 5.0f);
        else if (lttext.equalsIgnoreCase("line6"))
            return new Line(LineStyle.SOLID, 6.0f);
        else if (lttext.equalsIgnoreCase("line7"))
            return new Line(LineStyle.SOLID, 7.0f);
        else if (lttext.equalsIgnoreCase(LineStyle.LONG_DASH.name()))
            return new Line(LineStyle.LONG_DASH, 1.0f);
        else if (lttext.equalsIgnoreCase(LineStyle.SOLID.name()))
            return new Line(LineStyle.SOLID, 1.0f);
        else
            return Line.DEFAULT_LINE;
    }

	public LineType getLineType() {
		if ( type == LineStyle.SOLID && width == 1.0f )
			return LineType.LINE_1;
		else if ( type == LineStyle.SOLID && width == 2.0f )
			return LineType.LINE_2;
		else if ( type == LineStyle.SOLID && width == 3.0f )
			return LineType.LINE_3;
		else if ( type == LineStyle.SOLID && width == 4.0f )
			return LineType.LINE_4;
		else if ( type == LineStyle.SOLID && width == 5.0f )
			return LineType.LINE_5;
		else if ( type == LineStyle.SOLID && width == 6.0f )
			return LineType.LINE_6;
		else if ( type == LineStyle.SOLID && width == 7.0f )
			return LineType.LINE_7;
		else if ( type == LineStyle.LONG_DASH && width == 1.0f )
			return LineType.DASHED_1;
		else if ( type == LineStyle.LONG_DASH && width == 2.0f )
			return LineType.DASHED_2;
		else if ( type == LineStyle.LONG_DASH && width == 3.0f )
			return LineType.DASHED_3;
		else if ( type == LineStyle.LONG_DASH && width == 4.0f )
			return LineType.DASHED_4;
		else if ( type == LineStyle.LONG_DASH && width == 5.0f )
			return LineType.DASHED_5;
		else
			return LineType.LINE_1;
	}
}
