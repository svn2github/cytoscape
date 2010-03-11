package cytoscape.visual.customgraphic;

import giny.view.NodeView;

import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.NodeDetails;
import ding.view.DNodeView;

public class URLImageCustomGraphics implements CyCustomGraphics {

	protected BufferedImage image;

	public URLImageCustomGraphics(String url) {

		try {
			URL imageLocation = new URL(url);
			image = ImageIO.read(imageLocation);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void applyGraphics(NodeView nv) {
		if (!(nv instanceof DNodeView))
			return;

		final DNodeView dv = (DNodeView) nv;
		while (dv.getNumCustomGraphics() != 0) {
			CustomGraphic custom = dv.customGraphicIterator().next();
			dv.removeCustomGraphic(custom);
		}

		Rectangle2D bound = null;
		Paint paint = null;

		bound = new Rectangle2D.Double(0, 0, image.getWidth(), image
				.getHeight());
		paint = new TexturePaint(image, bound);

		dv.addCustomGraphic(new CustomGraphic(bound, paint,
				NodeDetails.ANCHOR_CENTER));

	}

}
