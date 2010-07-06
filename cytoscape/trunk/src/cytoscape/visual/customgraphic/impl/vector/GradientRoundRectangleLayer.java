package cytoscape.visual.customgraphic.impl.vector;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.customgraphic.paint.GradientPaintFactory;

public class GradientRoundRectangleLayer extends GradientLayerCustomGraphics {
	
	// Name of this custom graphics.
	private static final String NAME = "Glossy Round Rectangle Layer";
	private int r =20;
	
	public GradientRoundRectangleLayer() {
		super(NAME);
	}
	
	protected void renderImage(Graphics graphics) {
		super.renderImage(graphics);
		
		final Graphics2D g2d = (Graphics2D) graphics;
		// Render
		g2d.setPaint(paintFactory.getPaint(bound.getBounds2D()));
		g2d.fillRoundRect(rendered.getMinX(), rendered.getMinY(), 
				rendered.getWidth(), rendered.getHeight(), r, r);
	}
	
	
	public void update() {
		// First, remove all layers.
		cgList.clear();
		
		r = (int)(Math.min(w.getValue(), h.getValue())*0.3);
		bound = new RoundRectangle2D.Double(-w.getValue() / 2, -h.getValue() / 2,
																	w.getValue(), h.getValue(), r, r);
		paintFactory = new GradientPaintFactory(c1.getValue(), c2.getValue());
		final CustomGraphic cg = new CustomGraphic(bound, paintFactory);
		cgList.add(cg);
	}

}
