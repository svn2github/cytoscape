package cytoscape.visual.customgraphic;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
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

	private static final Color DEF_COLOR = new Color(0, 30, 190, 100);

	private static final String NAME = "Gradient Round Rectangle";
	
	private Image defImage;
	private static final String DEF_IMG_URL = "http://www.iconarchive.com/icons/iconshock/general-business/64/music-icon.png";

	private float w;
	private float h;
	
	private ImageIcon DEF_ICON = new ImageIcon(Cytoscape.class.getResource("images/ximian/stock_dialog-warning-32.png"));

	public GradientRectangleCustomGraphics() {
		super(NAME);

		this.h = 60;
		this.w = 150;
		
		buildCustomGraphics();
	}

	private void buildCustomGraphics() {
		cgList.clear();
		final GradientPaint gradient = new GradientPaint(w/2, 0, Color.LIGHT_GRAY,
				w/2, h/2, Color.white);
		final RoundRectangle2D bound = new RoundRectangle2D.Double(-w / 2, -h / 2, w, h, 20, 20);
		final CustomGraphic cg = new CustomGraphic(bound, gradient, NodeDetails.ANCHOR_CENTER);
		cgList.add(cg);
	}


	@Override
	public Image getImage() {
		return DEF_ICON.getImage();
	}

	@Override
	public Image resizeImage(int width, int height) {
		this.w = width;
		this.h = height;
		buildCustomGraphics();
		
		return DEF_ICON.getImage();
	}

}
