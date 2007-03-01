package cytoscape.visual.ui.icon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;


/**
 * Icon for arrow shape.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 */
public class ArrowIcon extends VisualPropertyIcon {
    private static final Stroke EDGE_STROKE = new BasicStroke(9.0f,
            BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);

    /**
     * Creates a new ArrowIcon object.
     *
     * @param shape
     *            DOCUMENT ME!
     * @param width
     *            DOCUMENT ME!
     * @param height
     *            DOCUMENT ME!
     * @param name
     *            DOCUMENT ME!
     */
    public ArrowIcon(Shape shape, int width, int height, String name) {
        super(shape, width, height, name);
    }

    /**
     * Creates a new ArrowIcon object.
     *
     * @param shape DOCUMENT ME!
     * @param width DOCUMENT ME!
     * @param height DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param color DOCUMENT ME!
     */
    public ArrowIcon(Shape shape, int width, int height, String name,
        Color color) {
        super(shape, width, height, name, color);
    }

    /**
     * Draw icon using Java2D.
     *
     * @param c
     *            DOCUMENT ME!
     * @param g
     *            DOCUMENT ME!
     * @param x
     *            DOCUMENT ME!
     * @param y
     *            DOCUMENT ME!
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        final Graphics2D g2d = (Graphics2D) g;

        // Turn AA on
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(color);

        /*
         * If shape is not defined, treat as no-head.
         */
        if (shape == null) {
            g2d.setStroke(EDGE_STROKE);
            g2d.drawLine(10, (height + 20) / 2, (int) (width * 0.95),
                (height + 20) / 2);

            return;
        }

        final AffineTransform af = new AffineTransform();

        g2d.setStroke(new BasicStroke(2.0f));

        final Rectangle2D bound = shape.getBounds2D();
        final double minx = bound.getMinX();
        final double miny = bound.getMinY();

        Shape newShape = shape;

        /*
         * Adjust position if it is NOT in first quadrant.
         */
        if (minx < 0) {
            af.setToTranslation(
                Math.abs(minx),
                0);
            newShape = af.createTransformedShape(newShape);
        }

        if (miny < 0) {
            af.setToTranslation(
                0,
                Math.abs(miny));
            newShape = af.createTransformedShape(newShape);
        }

        final double shapeWidth = newShape.getBounds2D()
                                          .getWidth();
        final double shapeHeight = newShape.getBounds2D()
                                           .getHeight();

        final double originalXYRatio = shapeWidth / shapeHeight;

        final double xRatio = (width / 3) / shapeWidth;
        final double yRatio = height / shapeHeight;
        af.setToScale(xRatio * originalXYRatio, yRatio);
        newShape = af.createTransformedShape(newShape);

        af.setToTranslation((width * 0.8) -
            newShape.getBounds2D().getCenterX(),
            ((height + 20) / 2) - newShape.getBounds2D().getCenterY());
        newShape = af.createTransformedShape(newShape);

        g2d.fill(newShape);

        /*
         * Finally, draw an edge (line) to the arrow head.
         */
        g2d.setStroke(EDGE_STROKE);

        if (newShape.getBounds2D()
                        .getWidth() > 5)
            g2d.drawLine(10, (height + 20) / 2,
                (int) (newShape.getBounds2D().getCenterX()), (height + 20) / 2);
        else
            g2d.drawLine(10, (height + 20) / 2,
                (int) (newShape.getBounds2D().getMinX()), (height + 20) / 2);
    }
}
