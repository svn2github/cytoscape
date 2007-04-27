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

import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;

import cytoscape.visual.ArrowShape;
import cytoscape.visual.LineTypeDef;
import cytoscape.visual.NodeShape;

import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.NodeShape;
import cytoscape.Cytoscape;


/**
 * Icon for node shapes.
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 *
 */
public class NodeIcon extends VisualPropertyIcon {
	
	protected Shape newShape;
	protected Graphics2D g2d;

	public NodeIcon() {
		this(((NodeShape)(VisualPropertyType.NODE_SHAPE.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle()))).getShape(),
             DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE, 
             ((NodeShape)(VisualPropertyType.NODE_SHAPE.getDefault(Cytoscape.getVisualMappingManager().getVisualStyle()))).getShapeName(),
			 DEFAULT_ICON_COLOR);
	}

    /** 
     * Creates a new NodeShapeIcon object.
     *
     * @param shape
     * @param width
     * @param height
     * @param name
     */
    public NodeIcon(Shape shape, int width, int height, String name) {
        this(shape, width, height, name, DEFAULT_ICON_COLOR);
    }

    /**
     * Creates a new NodeShapeIcon object.
     *
     * @param shape DOCUMENT ME!
     * @param width DOCUMENT ME!
     * @param height DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param color DOCUMENT ME!
     */
    public NodeIcon(Shape shape, int width, int height, String name, Color color ) {
        super(shape, width, height, name, color);

        adjustShape();
    }


    /**
     * DOCUMENT ME!
     *
     * @param width DOCUMENT ME!
     */
    public void setIconWidth(int width) {
        super.setIconWidth(width);
        adjustShape();
    }

    /**
     * DOCUMENT ME!
     *
     * @param height DOCUMENT ME!
     */
    public void setIconHeight(int height) {
        super.setIconHeight(height);
        adjustShape();
    }

    private void adjustShape() {
        final double shapeWidth = shape.getBounds2D().getWidth();
        final double shapeHeight = shape.getBounds2D().getHeight();

        final double xRatio = width / shapeWidth;
        final double yRatio = height / shapeHeight;

        final AffineTransform af = new AffineTransform();

        final Rectangle2D bound = shape.getBounds2D();
        final double minx = bound.getMinX();
        final double miny = bound.getMinY();

        if (minx < 0) {
            af.setToTranslation( Math.abs(minx), 0);
            shape = af.createTransformedShape(shape);
        }

        if (miny < 0) {
            af.setToTranslation( 0, Math.abs(miny));
            shape = af.createTransformedShape(shape);
        }

        af.setToScale(xRatio, yRatio);
        shape = af.createTransformedShape(shape);
    }

    /**
     * Draw icon using Java2D.
     *
     * @param c DOCUMENT ME!
     * @param g DOCUMENT ME!
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     */
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g2d = (Graphics2D) g;

        final AffineTransform af = new AffineTransform();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		                     RenderingHints.VALUE_ANTIALIAS_ON);

        newShape = shape;
     
        af.setToTranslation(pad, (c.getHeight() - newShape.getBounds2D().getHeight()) / 2);
        newShape = af.createTransformedShape(newShape);

        g2d.setColor(color);
		g2d.setStroke(new BasicStroke(2.0f));
		g2d.draw(newShape);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public NodeIcon clone() {
        final NodeIcon cloned = new NodeIcon(shape, width, height, name, color);

        return cloned;
    }

}
