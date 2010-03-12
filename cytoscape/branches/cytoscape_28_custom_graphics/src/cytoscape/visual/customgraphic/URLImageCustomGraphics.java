package cytoscape.visual.customgraphic;

import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.NodeDetails;

public class URLImageCustomGraphics implements CyCustomGraphics<CustomGraphic> {

	private final String imageURL;
	private CustomGraphic cg;
	private List<CustomGraphic> cgList;

	public URLImageCustomGraphics(String url) {
		this.imageURL = url;
		cgList = new ArrayList<CustomGraphic>();
	}
	
	private void buildCustomGraphics() {
		BufferedImage image = null;
		try {
			URL imageLocation = new URL(imageURL);
			image = ImageIO.read(imageLocation);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Rectangle2D bound = null;
		Paint paint = null;
		final int imageW = image.getWidth();
		final int imageH = image.getHeight();
		
		bound = new Rectangle2D.Double(-imageW/2, -imageH/2, imageW, imageH);
		paint = new TexturePaint(image, bound);

		cg = new CustomGraphic(bound, paint, NodeDetails.ANCHOR_CENTER);
		cgList.add(cg);
	}

	
	@Override
	public Collection<CustomGraphic> getCustomGraphics() {
		if(cg == null)
			buildCustomGraphics();
		
		return cgList;
	}

	@Override
	public String getDisplayName() {
		return this.imageURL;
	}

}
