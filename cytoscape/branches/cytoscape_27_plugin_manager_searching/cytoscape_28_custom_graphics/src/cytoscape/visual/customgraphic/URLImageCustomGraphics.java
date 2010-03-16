package cytoscape.visual.customgraphic;

import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
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

	// Defining padding
	private static final double PAD = 10;
	private static final double R = 28;
	
	private final String imageURL;
	private CustomGraphic cg;
	private List<CustomGraphic> cgList;
	
	private BufferedImage originalImage;

	public URLImageCustomGraphics(String url) {
		this.imageURL = url;
		cgList = new ArrayList<CustomGraphic>();
	}
	
	private void buildCustomGraphics() {
		if(originalImage == null)
			createImage();
		
		Rectangle2D bound = null;
		Paint paint = null;
		final int imageW = originalImage.getWidth();
		final int imageH = originalImage.getHeight();
		
		final Shape background = new java.awt.geom.RoundRectangle2D.Double(-imageW/2d-PAD, -imageH/2d-PAD, imageW+PAD*2d, imageH+PAD*2d, R, R);
		final Paint backgroundPaint = Color.white;
		
		bound = new Rectangle2D.Double(-imageW/2, -imageH/2, imageW, imageH);
		paint = new TexturePaint(originalImage, bound);

		cg = new CustomGraphic(bound, paint, NodeDetails.ANCHOR_CENTER);
		cgList.add(new CustomGraphic(background, backgroundPaint, NodeDetails.ANCHOR_CENTER));
		cgList.add(cg);
		
	}
	
	private void createImage() {
		try {
			URL imageLocation = new URL(imageURL);
			originalImage = ImageIO.read(imageLocation);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

	@Override
	public Image getImage() {
		if(originalImage == null)
			createImage();
		
		return originalImage;
	}

}
