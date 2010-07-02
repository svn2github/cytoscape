package cytoscape.visual.customgraphic.experimental;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

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
	
	PaintFactory paintFactory;
	RoundRectangle2D bound;
	
	private final CustomGraphicsProperty<Float> w;
	private final CustomGraphicsProperty<Float> h;
	private final CustomGraphicsProperty<Color> c1;
	private final CustomGraphicsProperty<Color> c2;
	
	private BufferedImage rendered;
	
	public GradientRectangleCustomGraphics() {
		super(NAME);
		w = new CustomGraphicsPropertyImpl<Float>(80f);
		h = new CustomGraphicsPropertyImpl<Float>(35f);
		
		final Color tGray = new Color(200, 200, 200, 100);
		final Color tBlack = new Color(0, 0, 0, 100);
		
		c1 = new CustomGraphicsPropertyImpl<Color>(tGray);
		c2 = new CustomGraphicsPropertyImpl<Color>(tBlack);
		
		
		
		this.props.put(WIDTH, w);
		this.props.put(HEIGHT, h);
		this.props.put(COLOR1, c1);
		this.props.put(COLOR2, c2);
		this.tags.add("vector image, gradient");
		rendered = new BufferedImage(w.getValue().intValue(), 
				h.getValue().intValue(), BufferedImage.TYPE_INT_ARGB);
		update();
		render(rendered.getGraphics());
	}

	
	private void render(Graphics graphics) {
		final Graphics2D g2d = (Graphics2D) graphics;
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, 
				RenderingHints.VALUE_RENDER_QUALITY );
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON );
		g2d.setPaint(paintFactory.getPaint(bound.getBounds2D()));
		g2d.fillRoundRect(rendered.getMinX(), rendered.getMinY(), 
				rendered.getWidth(), rendered.getHeight(), 20, 20);
		g2d.setColor(Color.DARK_GRAY);
		g2d.drawRoundRect(rendered.getMinX(), rendered.getMinY(), 
				rendered.getWidth(), rendered.getHeight(), 20, 20);
	}


	@Override
	public void update() {
		// First, remove all layers.
		cgList.clear();
		
		paintFactory = new GradientPaintFactory(c1.getValue(), c2.getValue());
		bound = new RoundRectangle2D.Double(-w.getValue() / 2, -h.getValue() / 2,
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
		render(rendered.getGraphics());
		return rendered;
	} 
}
