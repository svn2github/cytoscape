package cytoscape.visual.ui.icon;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import cytoscape.visual.VisualPropertyType;


/**
 * Icon for node shapes.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public class NodeIcon extends VisualPropertyIcon {
	
    private VisualPropertyType type = VisualPropertyType.NODE_SHAPE;
    private Object value;
    private static final Color FILL_COLOR = new Color(0, 0, 240, 50);

    /**
     * Creates a new NodeShapeIcon object.
     *
     * @param shape
     * @param width
     * @param height
     * @param name
     */
    public NodeIcon(Shape shape, int width, int height, String name) {
        this(shape, width, height, name, null, null);
    }

    /**
     * Creates a new NodeShapeIcon object.
     *
     * @param shape
     *            DOCUMENT ME!
     * @param width
     *            DOCUMENT ME!
     * @param height
     *            DOCUMENT ME!
     * @param name
     *            DOCUMENT ME!
     * @param color
     *            DOCUMENT ME!
     */
    public NodeIcon(Shape shape, int width, int height, String name,
        Color color, VisualPropertyType type) {
        super(shape, width, height, name, color);

        if (type != null)
            this.type = type;

        adjustShape();
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    public void setPropertyType(VisualPropertyType type) {
        this.type = type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * DOCUMENT ME!
     *
     * @param width DOCUMENT ME!
     */
    public void setIconWidth(int width) {
        super.width = width;
        adjustShape();
    }

    /**
     * DOCUMENT ME!
     *
     * @param height DOCUMENT ME!
     */
    public void setIconHeight(int height) {
        super.height = height;
        adjustShape();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getValue() {
        return this.value;
    }

    private void adjustShape() {
        final double shapeWidth = shape.getBounds2D()
                                       .getWidth();
        final double shapeHeight = shape.getBounds2D()
                                        .getHeight();

        final double xRatio = width / shapeWidth;
        final double yRatio = height / shapeHeight;

        final AffineTransform af = new AffineTransform();

        final Rectangle2D bound = shape.getBounds2D();
        final double minx = bound.getMinX();
        final double miny = bound.getMinY();

        if (minx < 0) {
            af.setToTranslation(
                Math.abs(minx),
                0);
            shape = af.createTransformedShape(shape);
        }

        if (miny < 0) {
            af.setToTranslation(
                0,
                Math.abs(miny));
            shape = af.createTransformedShape(shape);
        }

        af.setToScale(xRatio, yRatio);
        shape = af.createTransformedShape(shape);
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

        final AffineTransform af = new AffineTransform();

        // AA on
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setColor(color);

        Shape newShape = shape;
     
        af.setToTranslation(pad,
            (c.getHeight() - newShape.getBounds2D()
                                     .getHeight()) / 2);
        newShape = af.createTransformedShape(newShape);

        /*
         * Switch based on prop type.
         */
        switch (type) {
        case NODE_SHAPE:
            g2d.setColor(DEF_COLOR);
            g2d.draw(newShape);

            break;

        case NODE_FILL_COLOR:
            g2d.setColor((Color) value);
            g2d.fill(newShape);

            break;

        case NODE_BORDER_COLOR:
            g2d.setColor((Color) value);
            g2d.draw(newShape);

            break;

        case NODE_LABEL_COLOR:
            g2d.setFont(new Font("SansSerif", Font.BOLD, 40));
            g2d.setColor(Color.BLACK);
            g2d.drawString("A", c.getX() + 10,
                (int) (shape.getBounds2D().getMaxY()) + 5);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));

            break;

        case NODE_FONT_FACE:
            //g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString(
                ((Font) value).getFontName(),
                c.getX() + 10,
                (int) (shape.getBounds2D().getCenterY()) + 5);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 40));
            g2d.setColor(new Color(10, 10, 10, 30));
            g2d.drawString("A", c.getX() + 10,
                (int) (shape.getBounds2D().getMaxY()) + 5);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));

            break;

        case NODE_SIZE:
        case NODE_WIDTH:
        case NODE_HEIGHT:
            g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
            g2d.drawString(
                value.toString(),
                c.getX() + 10,
                (int) (shape.getBounds2D().getMaxY()));
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2d.setColor(new Color(10, 10, 10, 30));
            g2d.draw(newShape);

            break;

        case NODE_FONT_SIZE:
            g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
            // final int fontWidth =
            // SwingUtilities.computeStringWidth(g2d.getFontMetrics(), "12");
            g2d.drawString(
                value.toString(),
                c.getX() + 10,
                (int) (shape.getBounds2D().getMaxY()));
            g2d.setFont(new Font("SansSerif", Font.BOLD, 40));
            g2d.setColor(new Color(10, 10, 10, 40));
            g2d.drawString("A", c.getX() + 10,
                (int) (shape.getBounds2D().getMaxY()));
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));

            break;

        case NODE_LINETYPE:
            g2d.setStroke(new BasicStroke(5.0f));
            g2d.drawLine(c.getX() + 10,
                (int) (shape.getBounds()
                            .getCenterY() + 5),
                (int) shape.getBounds2D()
                           .getMaxX() * 2,
                (int) (shape.getBounds().getCenterY()) + 5);

            break;

        case NODE_LINE_WIDTH:
            g2d.setFont(new Font("SansSerif", Font.BOLD, 24));
            // final int fontWidth =
            // SwingUtilities.computeStringWidth(g2d.getFontMetrics(), "12");
            g2d.drawString("8",
                c.getX() + 10 + ((int) shape.getBounds2D()
                                            .getWidth() / 2),
                (int) (shape.getBounds2D().getMaxY()));
            g2d.setColor(new Color(10, 10, 10, 50));
            g2d.setStroke(new BasicStroke(8.0f));
            g2d.drawLine(c.getX() + 10,
                (int) (shape.getBounds()
                            .getCenterY() + 10),
                (int) shape.getBounds2D()
                           .getMaxX() * 2,
                (int) (shape.getBounds().getCenterY()) + 10);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));

            break;

        case NODE_LABEL:
        case NODE_TOOLTIP:
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString(
                value.toString(),
                c.getX() + 10,
                (int) (shape.getBounds2D().getCenterY()) + 5);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));

            break;

        case NODE_LABEL_POSITION:
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
            g2d.drawString("<C,C,c,0,0>", c.getX() + 10,
                (int) (shape.getBounds2D().getCenterY()) + 5);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 14));

            break;

        default:
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.draw(newShape);

            break;
        }
    }

    private void paintShape(Graphics g, int w, int h) {
        final Graphics2D g2d = (Graphics2D) g;

        // AA on
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, w, h);

        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setColor(color);

        Shape newShape = shape;

        g2d.setColor(FILL_COLOR);
        g2d.fill(newShape);
        g2d.setColor(color);
        g2d.draw(newShape);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
//    public Image getImage() {
//        image = new BufferedImage(width + 1, height + 1,
//                BufferedImage.TYPE_INT_RGB);
//
//        Graphics g2 = ((BufferedImage) image).createGraphics();
//        paintShape(g2, width + 1, width + 1);
//
//        return image;
//    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeIcon clone() {
        final NodeIcon cloned = new NodeIcon(shape, width, height, name, color,
                type);

        return cloned;
    }
}
