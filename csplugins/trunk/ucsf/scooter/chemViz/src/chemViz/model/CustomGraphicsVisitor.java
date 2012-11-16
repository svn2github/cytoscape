package chemViz.model;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.PaintFactory;
import ding.view.DGraphView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import org.openscience.cdk.annotations.TestClass;
import org.openscience.cdk.annotations.TestMethod;
import org.openscience.cdk.renderer.RendererModel;
import org.openscience.cdk.renderer.elements.ArrowElement;
import org.openscience.cdk.renderer.elements.AtomSymbolElement;
import org.openscience.cdk.renderer.elements.ElementGroup;
import org.openscience.cdk.renderer.elements.GeneralPath;
import org.openscience.cdk.renderer.elements.IRenderingElement;
import org.openscience.cdk.renderer.elements.LineElement;
import org.openscience.cdk.renderer.elements.OvalElement;
import org.openscience.cdk.renderer.elements.PathElement;
import org.openscience.cdk.renderer.elements.RectangleElement;
import org.openscience.cdk.renderer.elements.TextElement;
import org.openscience.cdk.renderer.elements.TextGroupElement;
import org.openscience.cdk.renderer.elements.WedgeLineElement;
import org.openscience.cdk.renderer.elements.path.Type;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.font.IFontManager;
import org.openscience.cdk.renderer.generators.BasicBondGenerator.WedgeWidth;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.ArrowHeadWidth;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.Scale;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator.UseAntiAliasing;
import org.openscience.cdk.renderer.visitor.IDrawVisitor;


/**
 * Implementation of the {@link IDrawVisitor} interface for the AWT
 * widget toolkit, allowing molecules to be rendered with toolkits based on
 * AWT, like the Java reference graphics platform Swing.  This implementation
 * returns a Java Shape rather than directly rendering the content.
 *
 * @cdk.module renderawt
 */

public class CustomGraphicsVisitor implements IDrawVisitor {

	private AWTFontManager fontManager;
	private RendererModel rendererModel;
	private Color currentColor = Color.BLACK;
	private BasicStroke currentStroke = null;
	private List<CustomGraphic> cgList;
	private AffineTransform transform;
	private CyNetworkView networkView;
	private AffineTransform scaleTransform;

	private final Map<Integer, BasicStroke> strokeMap = new HashMap<Integer, BasicStroke>();

	public RendererModel getRendererModel() {
		return rendererModel;
	}

	public Map<Integer, BasicStroke> getStrokeMap() {
		return strokeMap;
	}

	private final Map<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();

	public CustomGraphicsVisitor(CyNetworkView view, double scale) {
		this.fontManager = null;
		this.rendererModel = null;
		this.networkView = view;
		cgList = new ArrayList<CustomGraphic>();
		map.put(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUB);
		currentStroke = new BasicStroke(1);
		strokeMap.put(1, currentStroke);
		this.scaleTransform = AffineTransform.getScaleInstance(scale, scale);
	}

	public void visit(IRenderingElement element) {
		Color savedColor = currentColor;
		if (element instanceof ElementGroup)
			visit((ElementGroup) element);
		else if (element instanceof WedgeLineElement)
			visit((WedgeLineElement) element);
		else if (element instanceof LineElement)
			visit((LineElement) element);
		else if (element instanceof OvalElement)
			visit((OvalElement) element);
		else if (element instanceof TextGroupElement)
			visit((TextGroupElement) element);
		else if (element instanceof AtomSymbolElement)
			visit((AtomSymbolElement) element);
		else if (element instanceof TextElement)
			visit((TextElement) element);
		else if (element instanceof RectangleElement)
			visit((RectangleElement) element);
		else if (element instanceof PathElement)
			visit((PathElement) element);
		else if (element instanceof GeneralPath)
			visit((GeneralPath)element);
		else if (element instanceof ArrowElement)
			visit((ArrowElement) element);
		else
			System.err.println("Visitor method for "
			                    + element.getClass().getName() + " is not implemented");
		currentColor = savedColor;
	}

	public void setFontManager(IFontManager fontManager) {
		this.fontManager = (AWTFontManager) fontManager;
	}

	public void setRendererModel(RendererModel rendererModel) {
		this.rendererModel = rendererModel;
		/*
 		if (rendererModel.hasParameter(UseAntiAliasing.class)) {
			if ((boolean)rendererModel.getParameter(UseAntiAliasing.class).getValue()) {
				graphics.setRendereringHint(RenderingHints.KEY_ANTIALIASING,
				                            RenderingHints.VALUE_ANTIALIAS_ON);
			}
		}
		*/
	}

	public List<CustomGraphic> getCustomGraphics() {
		return cgList;
	}

	public void setTransform(AffineTransform transform) {
		this.transform = transform;
	}

	private void visit(ElementGroup elementGroup) {
		elementGroup.visitChildren(this);
	}

	private void visit(LineElement line) {
		// System.out.println("LineElement");
		int width = (int) (line.width * this.rendererModel.getParameter(Scale.class).getValue());
		if (width < 1) width = 1;
		if (!strokeMap.containsKey(width)) 
			strokeMap.put(width, new BasicStroke(width));
		BasicStroke stroke = strokeMap.get(width);
		double[] start = transform(line.firstPointX, line.firstPointY);
		double[] end = transform(line.secondPointX, line.secondPointY);
		Line2D lineShape = new Line2D.Double(start[0], start[1], end[0], end[1]);
		Shape s = currentStroke.createStrokedShape(lineShape);
		CustomGraphic c = new CustomGraphic(scaleTransform.createTransformedShape(s), new DefaultPaintFactory(currentColor));
		cgList.add(c);
	}

	private void visit(OvalElement oval) {
		// System.out.println("OvalElement");
		double radius = scaleX(oval.radius);
		double diameter = scaleX(oval.radius * 2);
		PaintFactory p = null;

		Ellipse2D e = new Ellipse2D.Double(transformX(oval.xCoord) - radius,
		                                   transformY(oval.yCoord) - radius,
		                                   diameter, diameter);
		Shape s = currentStroke.createStrokedShape(e);
		
		if (oval.fill)
			p = new DefaultPaintFactory(oval.color);
		else
			p = new DefaultPaintFactory(currentColor);

		CustomGraphic c = new CustomGraphic(scaleTransform.createTransformedShape(s), p);
		cgList.add(c);
	}

	private void visit(RectangleElement rectangle) {
		PaintFactory p = null;
		// System.out.println("RectangleElement");
		double[] point1 = transform(rectangle.xCoord, rectangle.yCoord);
		double[] point2 = transform(rectangle.xCoord+rectangle.width, 
		                            rectangle.yCoord+rectangle.height);
		if (rectangle.filled)
			p = new DefaultPaintFactory(rectangle.color);
		else
			p = new DefaultPaintFactory(currentColor);

		Rectangle2D rect = new Rectangle2D.Double(point1[0], point1[1], point2[0]-point1[0], point2[1]-point1[1]);
		Shape s = currentStroke.createStrokedShape(rect);
		CustomGraphic c = new CustomGraphic(scaleTransform.createTransformedShape(s), p);
		cgList.add(c);
	}

	private void visit(PathElement path) {
		System.out.println("PathElement");
	}

	private void visit(GeneralPath path) {
		System.out.println("GeneralPathElement");
	}

	private void visit(AtomSymbolElement atomSymbol) {
		// System.out.println("AtomSymbolElement");
		Font font = fontManager.getFont();
		FontRenderContext frc = getViewFontRenderContext(networkView);
		TextLayout tl = new TextLayout(atomSymbol.text, font, frc);
		Shape textShape = tl.getOutline(null);
		double textWidth = textShape.getBounds2D().getWidth();
		double textHeight = textShape.getBounds2D().getHeight();

		// XXX Check to make sure this is right....
		double textStartX = transformX(atomSymbol.xCoord) - textWidth/2;
		double textStartY = transformY(atomSymbol.yCoord) + textHeight/2;

		AffineTransform trans = new AffineTransform();
		trans.translate(textStartX, textStartY);
		Shape transShape = trans.createTransformedShape(textShape);
		transShape = scaleTransform.createTransformedShape(transShape);
		PaintFactory p = new DefaultPaintFactory(atomSymbol.color);
		CustomGraphic c = new CustomGraphic(transShape, p);
		cgList.add(c);

		// TODO: Handle formal charges...
	}

	private void visit(ArrowElement line) {
		System.out.println("ArrowElement");
	}

	private void visit(WedgeLineElement wedge) {
		System.out.println("WedgeElement");
	}

	private double scaleX(double xCoord) {
		return xCoord*transform.getScaleX();
	}

	private double transformX(double xCoord) {
		return transform(xCoord, 1)[0];
	}

	private double transformY(double yCoord) {
		return transform(1, yCoord)[1];
	}

	private double[] transform(double xCoord, double yCoord) {
		double[] result = new double[2];
		transform.transform( new double[] {xCoord, yCoord}, 0, result, 0, 1);
		return result;
	}

	private FontRenderContext getViewFontRenderContext(CyNetworkView view) {
		if (view == null)
			view = Cytoscape.getCurrentNetworkView();

		Graphics2D g2d = (Graphics2D)((DGraphView)view).getCanvas().getGraphics();
		return g2d.getFontRenderContext();
	}
}
