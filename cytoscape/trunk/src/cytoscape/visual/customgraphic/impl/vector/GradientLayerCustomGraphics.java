package cytoscape.visual.customgraphic.impl.vector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;

import cytoscape.render.stateful.PaintFactory;
import cytoscape.visual.customgraphic.AbstractCyCustomGraphics;
import cytoscape.visual.customgraphic.CustomGraphicsPropertyImpl;

/**
 * Proof of concept code to generate Custom Graphics dynamically as vector graphics.
 * 
 * @author kono
 * 
 */
public abstract class GradientLayerCustomGraphics extends AbstractCyCustomGraphics implements VectorCustomGraphics {

	// Paint fot this graphics
	protected PaintFactory paintFactory;
	
	// Bound of this graphics
	protected Shape bound;
	
	protected static final String WIDTH = "Width";
	protected static final String HEIGHT = "Height";
	protected static final String COLOR1 = "Color 1";
	protected static final String COLOR2 = "Color 2";
	
	protected final CustomGraphicsProperty<Float> w;
	protected final CustomGraphicsProperty<Float> h;
	
	protected final CustomGraphicsProperty<Color> c1;
	protected final CustomGraphicsProperty<Color> c2;
	
	// Pre-Rendered image for icon.
	protected BufferedImage rendered;
	
	private static final Color transparentWhite = new Color(255, 255, 255, 120);
	private static final Color transparentBlack = new Color(100, 100, 100, 120);
	
	private static final float DEF_W = 100;
	private static final float DEF_H = 100;
	
	
	public GradientLayerCustomGraphics(final String name) {
		super(name);
		w = new CustomGraphicsPropertyImpl<Float>(DEF_W);
		h = new CustomGraphicsPropertyImpl<Float>(DEF_H);
		
		c1 = new CustomGraphicsPropertyImpl<Color>(transparentWhite);
		c2 = new CustomGraphicsPropertyImpl<Color>(transparentBlack);
		
		this.props.put(WIDTH, w);
		this.props.put(HEIGHT, h);
		this.props.put(COLOR1, c1);
		this.props.put(COLOR2, c2);
		this.tags.add("vector image, gradient");
		rendered = new BufferedImage(w.getValue().intValue(), 
				h.getValue().intValue(), BufferedImage.TYPE_INT_ARGB);
		update();
		renderImage(rendered.getGraphics());
	}

	
	protected void renderImage(Graphics graphics) {
		rendered.flush();
		final Graphics2D g2d = (Graphics2D) graphics;
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
				RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON );
	}
	

	public Image getImage() {
		return rendered;
	}
	
	@Override
	public Image resizeImage(int width, int height) {
		this.w.setValue((float)width);
		this.h.setValue((float)height);
		update();
		renderImage(rendered.getGraphics());
		return rendered;
	} 
	
	public void update(int x, int y, int width, int height) {
		
	}
}
