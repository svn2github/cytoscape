package cytoscape.visual.ui.icon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

import cytoscape.Cytoscape;
import cytoscape.visual.VisualPropertyType;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public class LineTypeIcon extends VisualPropertyIcon {
    private BasicStroke stroke;
    protected Graphics2D g2d;
    
    protected int bottomPadding = 0;
    
    
    public LineTypeIcon() {
        this(null, DEFAULT_ICON_SIZE*3, DEFAULT_ICON_SIZE, "SOLID", (Color)VisualPropertyType.EDGE_COLOR.getDefault(Cytoscape.getVisualMappingManager()
                .getVisualStyle()));
        this.stroke = new BasicStroke((Float) VisualPropertyType.EDGE_LINE_WIDTH.getDefault(Cytoscape.getVisualMappingManager()
                .getVisualStyle()));
    }

    /**
     * Creates a new LineTypeIcon object.
     *
     * @param stroke DOCUMENT ME!
     * @param width DOCUMENT ME!
     * @param height DOCUMENT ME!
     * @param name DOCUMENT ME!
     */
    public LineTypeIcon(BasicStroke stroke, int width, int height, String name) {
        super(null, width, height, name);
        this.stroke = stroke;
    }

    /**
     * Creates a new LineTypeIcon object.
     *
     * @param stroke DOCUMENT ME!
     * @param width DOCUMENT ME!
     * @param height DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param color DOCUMENT ME!
     */
    public LineTypeIcon(BasicStroke stroke, int width, int height, String name,
        Color color) {
        super(null, width, height, name, color);
        this.stroke = stroke;
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     * @param g DOCUMENT ME!
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g2d = (Graphics2D) g;
        g2d.setColor(color);
        // AA on
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        float[] dashDef = null;

        if (stroke.getDashArray() != null)
            dashDef = stroke.getDashArray();

        final BasicStroke lineStroke = new BasicStroke(
                stroke.getLineWidth(),
                BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER,
                10.0f,
                dashDef,
                0.0f);

        g2d.setStroke(lineStroke);
        g2d.draw(
            new Line2D.Double(20, (height + 20) / 2, width, (height + 20) / 2));
    }

}
