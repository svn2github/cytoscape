package cytoscape.visual.customgraphic.impl.vector;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.visual.customgraphic.paint.GradientPaintFactory;

public class GradientOvalLayer extends GradientLayerCustomGraphics {
	
	// Name of this custom graphics.
	private static final String NAME = "Glossy Oval Layer";

	public GradientOvalLayer() {
		super(NAME);
		// TODO Auto-generated constructor stub
	}
	
	protected void renderImage(Graphics graphics) {
		super.renderImage(graphics);
		
		final Graphics2D g2d = (Graphics2D) graphics;
		// Render
		g2d.setPaint(paintFactory.getPaint(bound.getBounds2D()));
		g2d.fillOval(rendered.getMinX(), rendered.getMinY(), 
				rendered.getWidth(), rendered.getHeight());
	}
	
	
	public void update() {
		// First, remove all layers.
		cgList.clear();
		
		bound = new Ellipse2D.Double(-w.getValue() / 2, -h.getValue() / 2,
																	w.getValue(), h.getValue());
		paintFactory = new GradientPaintFactory(c1.getValue(), c2.getValue());
		final CustomGraphic cg = new CustomGraphic(bound, paintFactory);
		cgList.add(cg);
	}

}
