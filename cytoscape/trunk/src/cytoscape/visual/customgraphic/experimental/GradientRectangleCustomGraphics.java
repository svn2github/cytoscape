package cytoscape.visual.customgraphic.experimental;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.PaintFactory;
import cytoscape.visual.customgraphic.AbstractCyCustomGraphics;
import cytoscape.visual.customgraphic.CustomGraphicsPropertyImpl;
import cytoscape.visual.customgraphic.paint.GradientPaintFactory;

/**
 * Proof of concept code to generate Custom Graphics dynamically as vector graphics.
 * 
 * @author kono
 * 
 */
public class GradientRectangleCustomGraphics extends AbstractCyCustomGraphics {

	private static final String NAME = "Gradient Round Rectangle";
	
	private static final String WIDTH = "Width";
	private static final String HEIGHT = "Height";
	private static final String COLOR1 = "Color 1";
	private static final String COLOR2 = "Color 2";
	
	private final CustomGraphicsProperty<Float> w;
	private final CustomGraphicsProperty<Float> h;
	private final CustomGraphicsProperty<Color> c1;
	private final CustomGraphicsProperty<Color> c2;
	
	private Image rendered;
	
	public GradientRectangleCustomGraphics() {
		super(NAME);
		w = new CustomGraphicsPropertyImpl<Float>(60f);
		h = new CustomGraphicsPropertyImpl<Float>(150f);
		c1 = new CustomGraphicsPropertyImpl<Color>(Color.white);
		c2 = new CustomGraphicsPropertyImpl<Color>(Color.darkGray);
		
		this.props.put(WIDTH, w);
		this.props.put(HEIGHT, h);
		this.props.put(COLOR1, c1);
		this.props.put(COLOR2, c2);
		this.tags.add("vector image, gradient");
		rendered = new RenderedGradientBox(w.getValue().intValue(), h.getValue().intValue(), BufferedImage.TYPE_INT_ARGB);
		update();
	}

	
	@Override
	public void update() {
		// First, remove all layers.
		cgList.clear();
		
		final PaintFactory paintFactory = new GradientPaintFactory(c1.getValue(), c2.getValue());
		final RoundRectangle2D bound = new RoundRectangle2D.Double(-w.getValue() / 2, -h.getValue() / 2,
																	w.getValue(), h.getValue(), 20, 20);
		final CustomGraphic cg = new CustomGraphic(bound, paintFactory);
		cgList.add(cg);
	}

	public Image getImage() {
		return rendered;
	}
	
	@Override
	public Image resizeImage(int width, int height) {
		this.w.setValue((float)width);
		this.h.setValue((float)height);
		update();
		
		return rendered;
	}
	
	class RenderedGradientBox extends BufferedImage {

		public RenderedGradientBox(int width, int height, int imageType) {
			super(width, height, imageType);
			// TODO Auto-generated constructor stub
		}
		
		public void update() {
			Graphics2D g = (Graphics2D) this.getGraphics();
			g.setPaint(Color.red);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
		
	}
}
