package cytoscape;

//import cytoscape.render.immed.GraphGraphics;
import java.awt.*;
import java.awt.geom.*;

import org.cytoscape.view.DependentVisualPropertyCallback;
import org.cytoscape.view.DiscreteVisualProperty;
import org.cytoscape.view.NodeView;
import org.cytoscape.view.EdgeView;
import org.cytoscape.view.VisualProperty;
import org.cytoscape.view.VisualPropertyCatalog;
import org.cytoscape.view.VisualPropertyIcon;
import org.cytoscape.view.renderers.NodeRenderer;
import org.cytoscape.vizmap.LabelPosition;
import org.cytoscape.vizmap.icon.ArrowIcon;
import org.cytoscape.vizmap.icon.LineTypeIcon;
import org.cytoscape.vizmap.icon.NodeIcon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.UIManager;

import cytoscape.render.immed.GraphGraphics;
import cytoscape.render.stateful.NodeDetails;

public class ShapeRenderer implements NodeRenderer {
	public static final int CUSTOM_SHAPE_MAX_VERTICES = 100;
	private final double[] m_polyCoords = // I need this for extra precision.
		new double[2 * CUSTOM_SHAPE_MAX_VERTICES];
	public static final byte SHAPE_RECTANGLE = 0;
	public static final byte SHAPE_DIAMOND = 1;
	public static final byte SHAPE_ELLIPSE = 2;
	public static final byte SHAPE_HEXAGON = 3;
	public static final byte SHAPE_OCTAGON = 4;
	public static final byte SHAPE_PARALLELOGRAM = 5;
	public static final byte SHAPE_ROUNDED_RECTANGLE = 6;
	public static final byte SHAPE_TRIANGLE = 7;
	public static final byte SHAPE_VEE = 8;
	private static final byte s_last_shape = SHAPE_VEE;
	
	/*
	 * A constant for controlling how cubic Bezier curves are drawn; This
	 * particular constant results in elliptical-looking curves.
	 */
	private static final double CURVE_ELLIPTICAL = (4.0d * (Math.sqrt(2.0d) - 1.0d)) / 3.0d;
	
	private final GeneralPath m_path2d = new GeneralPath();
	private final GeneralPath m_path2dPrime = new GeneralPath();
	private final Ellipse2D.Double m_ellp2d = new Ellipse2D.Double();
	
	private final double[] m_ptsBuff = new double[4];
	private int m_polyNumPoints; // Used with m_polyCoords.

	private Set<VisualProperty> visualProperties;
	private String name;
	
	public ShapeRenderer(String name){
		m_path2dPrime.setWindingRule(GeneralPath.WIND_EVEN_ODD);
		this.name = name;
		if (visualProperties == null){
			populateListOfVisualProperties();
		}
	}
	
	/** Returns user-friendly name */
	public String name(){
		return this.name;
	}

	/**
	 * Draw a preview image on canvas at given place (using some default NodeDetails that the renderer can make up)
	 */
	public void generatePreview(Graphics2D graphics, float[] position){
		// TODO
	}
	// DEFINE VISUALPROPERTIES:
	/** return array of Integer-s containing first,...last with step size increment */
	private static Object[] range(int first, int last, int increment){
		ArrayList result = new ArrayList();
		for (int i = first; i<=last; i+=increment){
			result.add(Integer.valueOf(i));
		}
		return result.toArray();
	}

	public static Map<Object, VisualPropertyIcon> getNodeIconSet(Object [] values, Map<Byte, Shape>shapes) {
		Map<Object, VisualPropertyIcon> nodeShapeIcons = new HashMap<Object, VisualPropertyIcon>();
		for (int i = 0; i < values.length; i++) {
			Integer value = (Integer) values[i];
			Shape shape = shapes.get(new Byte(value.byteValue()));
			
			nodeShapeIcons.put(shape, new NodeIcon(shape));
			
		}
		return nodeShapeIcons;
	}
	private void populateListOfVisualProperties(){
		visualProperties = new HashSet<VisualProperty>();
	
		visualProperties.add(new LegacyVisualProperty("NODE_FILL_COLOR", Color.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_BORDER_COLOR", Color.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_OPACITY", Number.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_BORDER_OPACITY", Number.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_LABEL_OPACITY", Number.class, true));

		Object [] range = range(0, 8, 1); 
		Map<Object, VisualPropertyIcon> iconSet = getNodeIconSet(range, GraphGraphics.getNodeShapes()); 
		visualProperties.add( new DiscreteVisualProperty("NODE_SHAPE", Integer.class, true, range, iconSet));
	
		visualProperties.add( new LegacyVisualProperty("NODE_SIZE", Number.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_WIDTH", Number.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_HEIGHT", Number.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_LABEL", String.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_FONT_FACE", Font.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_FONT_SIZE", Number.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_LABEL_COLOR", Color.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_TOOLTIP", String.class, true));
		visualProperties.add( new LegacyVisualProperty("NODE_LABEL_POSITION", LabelPosition.class, true));
		
		
		range = new Object[]{Boolean.TRUE, Boolean.FALSE};
		
		Map<Object, VisualPropertyIcon> icons = new HashMap<Object, VisualPropertyIcon>();
		icons.put(Boolean.TRUE, (VisualPropertyIcon) new NodeIcon("true"));
		icons.put(Boolean.FALSE, (VisualPropertyIcon) new NodeIcon("false"));
		visualProperties.add( new DiscreteVisualProperty("NODE_SIZE_LOCKED", Boolean.class, true, range, icons,
				new DependentVisualPropertyCallback(){
					public Set<VisualProperty> changed(Collection<NodeView> nodeviews, Collection<EdgeView> edgeviews, Collection<VisualProperty> current_vps){
						boolean hasTrue = false;
						boolean hasFalse = false;
						for (NodeView nv: nodeviews){
							HashMap<String, Object> map = nv.getVisualAttributes();
							System.out.println("visualAttributes:"+map);
							Boolean b = (Boolean) map.get("NODE_SIZE_LOCKED");
							if (b == null){
								System.out.println("value for NODE_SIZE_LOCKED not found, forcing default 'true' value:"+b);
								b = Boolean.TRUE;
							}
							if (b.booleanValue()){
								hasTrue = true;
							} else {
								hasFalse = true;
							}
						}
						Set <VisualProperty> toRemove = new HashSet<VisualProperty>();
						if (! hasTrue){
							toRemove.add(VisualPropertyCatalog.getVisualProperty("NODE_SIZE"));
						}
						if (! hasFalse){
							toRemove.add(VisualPropertyCatalog.getVisualProperty("NODE_WIDTH"));
							toRemove.add(VisualPropertyCatalog.getVisualProperty("NODE_HEIGHT"));
						}
						return toRemove;
					}
			}));
		
	}
	/**
	 * Return a list of visual attributes this renderer can use
	 */
	public Collection<VisualProperty> supportedVisualAttributes(){
		return new HashSet<VisualProperty>(visualProperties);
	}

	/**
	 * Draws a node with medium to high detail, depending on parameters
	 * specified. The xMin, yMin, xMax, and yMax parameters specify the extents
	 * of the node shape (in the node coordinate system), including the border
	 * width. That is, the drawn border won't extend beyond the extents
	 * specified.
	 * <p>
	 * There is an imposed constraint on borderWidth which, using the
	 * implemented algorithms, prevents strange-looking borders. The constraint
	 * is that borderWidth may not exceed the minimum of the node width and node
	 * height divided by six. In addition, for custom node shapes, this
	 * requirement may be more constrained, depending on the kinks in the custom
	 * node shape.
	 * <p>
	 * There is a constraint that only applies to SHAPE_ROUNDED_RECTANGLE which
	 * imposes that the maximum of the width and height be strictly less than
	 * twice the minimum of the width and height of the node.
	 * <p>
	 * This method will not work unless clear() has been called at least once
	 * previously.
	 * 
	 * @param nodeShape
	 *            the shape of the node to draw (one of the SHAPE_* constants or
	 *            a custom node shape).
	 * @param xMin
	 *            an extent of the node shape to draw, in node coordinate space;
	 *            the drawn shape will theoretically contain a point that lies
	 *            on this X coordinate.
	 * @param yMin
	 *            an extent of the node shape to draw, in node coordinate space;
	 *            the drawn shape will theoretically contain a point that lies
	 *            on this Y coordinate.
	 * @param xMax
	 *            an extent of the node shape to draw, in node coordinate space;
	 *            the drawn shape will theoretically contain a point that lies
	 *            on this X coordinate.
	 * @param yMax
	 *            an extent of the node shape to draw, in node coordinate space;
	 *            the drawn shape will theoretically contain a point that lies
	 *            on this Y coordinate.
	 * @param fillPaint
	 *            the paint to use when drawing the node area minus the border
	 *            (the "interior" of the node).
	 * @param borderWidth
	 *            the border width, in node coordinate space; if this value is
	 *            zero, the rendering engine skips over the process of rendering
	 *            the border, which gives a significant performance boost.
	 * @param borderPaint
	 *            if borderWidth is not zero, this paint is used for rendering
	 *            the node border; otherwise, this parameter is ignored (and may
	 *            be null).
	 * @exception IllegalArgumentException
	 *                if xMin is not less than xMax or if yMin is not less than
	 *                yMax, if borderWidth is negative or is greater than
	 *                Math.min(xMax - xMin, yMax - yMin) / 6 (for custom node
	 *                shapes borderWidth may be even more limited, depending on
	 *                the specific shape), if nodeShape is
	 *                SHAPE_ROUNDED_RECTANGLE and the condition max(width,
	 *                height) < 2 * min(width, height) does not hold, or if
	 *                nodeShape is neither one of the SHAPE_* constants nor a
	 *                previously defined custom node shape.
	 */
	public void render(Graphics2D m_g2d, NodeDetails nodeDetails, float[] floatBuff1, int node, NodeView nodeView) {
		System.out.println("rendering by: "+name);
		
		// TODO Auto-generated method stub
		byte nodeShape = nodeDetails.shape(node);
		float xMin = floatBuff1[0]; 
		float yMin =  floatBuff1[1];
		float xMax = floatBuff1[2];
		float yMax = floatBuff1[3];
		
		HashMap <String, Object> attrs = nodeView.getVisualAttributes();
		Paint fillPaint; 
		if (nodeView.isSelected()) {
			fillPaint = (Paint) attrs.get("selectedPaint");
		} else {
			fillPaint = (Paint) attrs.get("unselectedPaint");
		}
		float borderWidth = ((Double)attrs.get("borderWidth")).floatValue();
		Paint borderPaint = (Paint) attrs.get("borderPaint");
		
		if (borderWidth == 0.0f) {
			m_g2d.setPaint(fillPaint);
			m_g2d.fill(getShape(nodeShape, xMin, yMin, xMax, yMax));
		} else { // There is a border.
			m_path2dPrime.reset();
			m_path2dPrime.append(getShape(nodeShape, xMin, yMin, xMax, yMax),
					false); // Make a copy, essentially.

			final Shape innerShape;

			if (nodeShape == SHAPE_ELLIPSE) {
				// TODO: Compute a more accurate inner area for ellipse +
				// border.
				innerShape = getShape(SHAPE_ELLIPSE, ((double) xMin)
						+ borderWidth, ((double) yMin) + borderWidth,
						((double) xMax) - borderWidth, ((double) yMax)
								- borderWidth);
			} else if (nodeShape == SHAPE_ROUNDED_RECTANGLE) {
				computeRoundedRectangle(((double) xMin) + borderWidth,
						((double) yMin) + borderWidth, ((double) xMax)
								- borderWidth, ((double) yMax) - borderWidth,
						(Math.max(((double) xMax) - xMin, ((double) yMax)
								- yMin) / 4.0d)
								- borderWidth, m_path2d);
				innerShape = m_path2d;
			} else {
				// A general [possibly non-convex] polygon with certain
				// restrictions: no two consecutive line segments can be
				// parallel,
				// each line segment must have nonzero length, the polygon
				// cannot
				// self-intersect, and the polygon must be clockwise
				// in the node coordinate system.
				m_path2d.reset();

				final double xNot = m_polyCoords[0];
				final double yNot = m_polyCoords[1];
				final double xOne = m_polyCoords[2];
				final double yOne = m_polyCoords[3];
				double xPrev = xNot;
				double yPrev = yNot;
				double xCurr = xOne;
				double yCurr = yOne;
				double xNext = m_polyCoords[4];
				double yNext = m_polyCoords[5];
				computeInnerPoint(m_ptsBuff, xPrev, yPrev, xCurr, yCurr, xNext,
						yNext, borderWidth);
				m_path2d.moveTo((float) m_ptsBuff[0], (float) m_ptsBuff[1]);

				int i = 6;

				while (true) {
					if (i == (m_polyNumPoints * 2)) {
						computeInnerPoint(m_ptsBuff, xCurr, yCurr, xNext,
								yNext, xNot, yNot, borderWidth);
						m_path2d.lineTo((float) m_ptsBuff[0],
								(float) m_ptsBuff[1]);
						computeInnerPoint(m_ptsBuff, xNext, yNext, xNot, yNot,
								xOne, yOne, borderWidth);
						m_path2d.lineTo((float) m_ptsBuff[0],
								(float) m_ptsBuff[1]);
						m_path2d.closePath();

						break;
					} else {
						xPrev = xCurr;
						yPrev = yCurr;
						xCurr = xNext;
						yCurr = yNext;
						xNext = m_polyCoords[i++];
						yNext = m_polyCoords[i++];
						computeInnerPoint(m_ptsBuff, xPrev, yPrev, xCurr,
								yCurr, xNext, yNext, borderWidth);
						m_path2d.lineTo((float) m_ptsBuff[0],
								(float) m_ptsBuff[1]);
					}
				}

				innerShape = m_path2d;
			}

			m_g2d.setPaint(fillPaint);
			m_g2d.fill(innerShape);

			// Render the border such that it does not overlap with the fill
			// region because translucent colors may be used. Don't do
			// things differently for opaque and translucent colors for the
			// sake of consistency.
			
			m_path2dPrime.append(innerShape, false);
			m_g2d.setPaint(borderPaint);
			m_g2d.fill(m_path2dPrime);
			
		}
	}
	
	/*
	 * This method has the side effect of setting m_ellp2d or m_path2d; if
	 * m_path2d is set (every case but the ellipse and rounded rectangle), then
	 * m_polyCoords and m_polyNumPoints are also set.
	 */
	private final Shape getShape(final byte nodeShape, final double xMin,
			final double yMin, final double xMax, final double yMax) {
		switch (nodeShape) {
		case SHAPE_ELLIPSE:
			m_ellp2d.setFrame(xMin, yMin, xMax - xMin, yMax - yMin);

			return m_ellp2d;

		case SHAPE_RECTANGLE:
			m_polyNumPoints = 4;
			m_polyCoords[0] = xMin;
			m_polyCoords[1] = yMin;
			m_polyCoords[2] = xMax;
			m_polyCoords[3] = yMin;
			m_polyCoords[4] = xMax;
			m_polyCoords[5] = yMax;
			m_polyCoords[6] = xMin;
			m_polyCoords[7] = yMax;

			break;

		case SHAPE_DIAMOND:
			m_polyNumPoints = 4;
			m_polyCoords[0] = (xMin + xMax) / 2.0d;
			m_polyCoords[1] = yMin;
			m_polyCoords[2] = xMax;
			m_polyCoords[3] = (yMin + yMax) / 2.0d;
			m_polyCoords[4] = (xMin + xMax) / 2.0d;
			m_polyCoords[5] = yMax;
			m_polyCoords[6] = xMin;
			m_polyCoords[7] = (yMin + yMax) / 2.0d;

			break;

		case SHAPE_HEXAGON:
			m_polyNumPoints = 6;
			m_polyCoords[0] = ((2.0d * xMin) + xMax) / 3.0d;
			m_polyCoords[1] = yMin;
			m_polyCoords[2] = ((2.0d * xMax) + xMin) / 3.0d;
			m_polyCoords[3] = yMin;
			m_polyCoords[4] = xMax;
			m_polyCoords[5] = (yMin + yMax) / 2.0d;
			m_polyCoords[6] = ((2.0d * xMax) + xMin) / 3.0d;
			m_polyCoords[7] = yMax;
			m_polyCoords[8] = ((2.0d * xMin) + xMax) / 3.0d;
			m_polyCoords[9] = yMax;
			m_polyCoords[10] = xMin;
			m_polyCoords[11] = (yMin + yMax) / 2.0d;

			break;

		case SHAPE_OCTAGON:
			m_polyNumPoints = 8;
			m_polyCoords[0] = ((2.0d * xMin) + xMax) / 3.0d;
			m_polyCoords[1] = yMin;
			m_polyCoords[2] = ((2.0d * xMax) + xMin) / 3.0d;
			m_polyCoords[3] = yMin;
			m_polyCoords[4] = xMax;
			m_polyCoords[5] = ((2.0d * yMin) + yMax) / 3.0d;
			m_polyCoords[6] = xMax;
			m_polyCoords[7] = ((2.0d * yMax) + yMin) / 3.0d;
			m_polyCoords[8] = ((2.0d * xMax) + xMin) / 3.0d;
			m_polyCoords[9] = yMax;
			m_polyCoords[10] = ((2.0d * xMin) + xMax) / 3.0d;
			m_polyCoords[11] = yMax;
			m_polyCoords[12] = xMin;
			m_polyCoords[13] = ((2.0d * yMax) + yMin) / 3.0d;
			m_polyCoords[14] = xMin;
			m_polyCoords[15] = ((2.0d * yMin) + yMax) / 3.0d;

			break;

		case SHAPE_PARALLELOGRAM:
			m_polyNumPoints = 4;
			m_polyCoords[0] = xMin;
			m_polyCoords[1] = yMin;
			m_polyCoords[2] = ((2.0d * xMax) + xMin) / 3.0d;
			m_polyCoords[3] = yMin;
			m_polyCoords[4] = xMax;
			m_polyCoords[5] = yMax;
			m_polyCoords[6] = ((2.0d * xMin) + xMax) / 3.0d;
			m_polyCoords[7] = yMax;

			break;

		case SHAPE_ROUNDED_RECTANGLE:
			// A condition that must be satisfied (pertaining to radius) is that
			// max(width, height) <= 2 * min(width, height).
			computeRoundedRectangle(xMin, yMin, xMax, yMax, Math.max(xMax
					- xMin, yMax - yMin) / 4.0d, m_path2d);

			return m_path2d;

		case SHAPE_TRIANGLE:
			m_polyNumPoints = 3;
			m_polyCoords[0] = (xMin + xMax) / 2.0d;
			m_polyCoords[1] = yMin;
			m_polyCoords[2] = xMax;
			m_polyCoords[3] = yMax;
			m_polyCoords[4] = xMin;
			m_polyCoords[5] = yMax;

			break;

		case SHAPE_VEE:
			m_polyNumPoints = 4;
			m_polyCoords[0] = xMin;
			m_polyCoords[1] = yMin;
			m_polyCoords[2] = (xMin + xMax) / 2.0d;
			m_polyCoords[3] = ((2.0d * yMin) + yMax) / 3.0d;
			m_polyCoords[4] = xMax;
			m_polyCoords[5] = yMin;
			m_polyCoords[6] = (xMin + xMax) / 2.0d;
			m_polyCoords[7] = yMax;

			break;

		}

		m_path2d.reset();

		m_path2d.moveTo((float) m_polyCoords[0], (float) m_polyCoords[1]);

		for (int i = 2; i < (m_polyNumPoints * 2);)
			m_path2d.lineTo((float) m_polyCoords[i++],
					(float) m_polyCoords[i++]);

		m_path2d.closePath();

		return m_path2d;
	}
	
	private final static void computeRoundedRectangle(final double xMin,
			final double yMin, final double xMax, final double yMax,
			final double radius, final GeneralPath path2d) {
		path2d.reset();
		path2d.moveTo((float) (xMax - radius), (float) yMin);
		path2d.curveTo((float) (((CURVE_ELLIPTICAL - 1.0d) * radius) + xMax),
				(float) yMin, (float) xMax,
				(float) (((1.0d - CURVE_ELLIPTICAL) * radius) + yMin),
				(float) xMax, (float) (radius + yMin));
		path2d.lineTo((float) xMax, (float) (yMax - radius));
		path2d.curveTo((float) xMax,
				(float) (((CURVE_ELLIPTICAL - 1.0d) * radius) + yMax),
				(float) (((CURVE_ELLIPTICAL - 1.0d) * radius) + xMax),
				(float) yMax, (float) (xMax - radius), (float) yMax);
		path2d.lineTo((float) (radius + xMin), (float) yMax);
		path2d.curveTo((float) (((1.0d - CURVE_ELLIPTICAL) * radius) + xMin),
				(float) yMax, (float) xMin,
				(float) (((CURVE_ELLIPTICAL - 1.0d) * radius) + yMax),
				(float) xMin, (float) (yMax - radius));
		path2d.lineTo((float) xMin, (float) (radius + yMin));
		path2d.curveTo((float) xMin,
				(float) (((1.0d - CURVE_ELLIPTICAL) * radius) + yMin),
				(float) (((1.0d - CURVE_ELLIPTICAL) * radius) + xMin),
				(float) yMin, (float) (radius + xMin), (float) yMin);
		path2d.closePath();
	}
	/*
	 * This method is used to construct an inner shape for node border.
	 * output[0] is the x return value and output[1] is the y return value. The
	 * line prev->curr cannot be parallel to curr->next.
	 */
	private final static void computeInnerPoint(final double[] output,
			final double xPrev, final double yPrev, final double xCurr,
			final double yCurr, final double xNext, final double yNext,
			final double borderWidth) {
		final double segX1 = xCurr - xPrev;
		final double segY1 = yCurr - yPrev;
		final double segLength1 = Math.sqrt((segX1 * segX1) + (segY1 * segY1));
		final double segX2 = xNext - xCurr;
		final double segY2 = yNext - yCurr;
		final double segLength2 = Math.sqrt((segX2 * segX2) + (segY2 * segY2));
		final double segX2Normal = segX2 / segLength2;
		final double segY2Normal = segY2 / segLength2;
		final double xNextPrime = (segX2Normal * segLength1) + xPrev;
		final double yNextPrime = (segY2Normal * segLength1) + yPrev;
		final double segPrimeX = xNextPrime - xCurr;
		final double segPrimeY = yNextPrime - yCurr;
		final double distancePrimeToSeg1 = (((segX1 * yNextPrime)
				- (segY1 * xNextPrime) + (xPrev * yCurr)) - (xCurr * yPrev))
				/ segLength1;
		final double multFactor = borderWidth / distancePrimeToSeg1;
		output[0] = (multFactor * segPrimeX) + xCurr;
		output[1] = (multFactor * segPrimeY) + yCurr;
	}

}
