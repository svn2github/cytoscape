package cytoscape.visual.customgraphic;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Image;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;

import cytoscape.Cytoscape;
import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.NodeDetails;

/**
 * Proof of concept code. Generate images dynamically from attributes.
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
	
	private static ImageIcon DEF_ICON = new ImageIcon(Cytoscape.class.getResource("images/ximian/stock_dialog-warning-32.png"));

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
		this.tags.add("vector image");
		
		update();
	}

	
	@Override
	public void update() {
		cgList.clear();
		final GradientPaint gradient = new GradientPaint(w.getValue()/2, 0, c2.getValue(),
				w.getValue()/2, h.getValue()/2, c1.getValue());
		final RoundRectangle2D bound = new RoundRectangle2D.Double(-w.getValue() / 2, -h.getValue() / 2,
																	w.getValue(), h.getValue(), 20, 20);
		final CustomGraphic cg = new CustomGraphic(bound, gradient, NodeDetails.ANCHOR_CENTER);
		cgList.add(cg);
	}


	@Override
	public Image getImage() {
		return DEF_ICON.getImage();
	}

	@Override
	public Image resizeImage(int width, int height) {
		this.w.setValue((float)width);
		this.h.setValue((float)height);
		update();
		
		return DEF_ICON.getImage();
	}

}
