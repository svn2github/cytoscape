package cytoscape.visual.customgraphic;

import java.awt.Color;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import cytoscape.render.stateful.CustomGraphic;
import cytoscape.render.stateful.NodeDetails;

public class URLImageCustomGraphics extends AbstractCyCustomGraphics {

	private static final String DEF_TAG = "bitmap image";
	// Defining padding
	private static final double PAD = 10;
	private static final double R = 28;

	private CustomGraphic cg;

	private BufferedImage originalImage;
	private BufferedImage scaledImage;

	public URLImageCustomGraphics(String url) throws IOException {
		super(url);
		this.tags.add(DEF_TAG);
		createImage(url);
		buildCustomGraphics(originalImage);
	}
	
	public URLImageCustomGraphics(String name, BufferedImage img) {
		super(name);
		this.tags.add(DEF_TAG);
		this.originalImage = img;
		buildCustomGraphics(originalImage);
	}

	private void buildCustomGraphics(BufferedImage targetImg) {
		cgList.clear();
		
		Rectangle2D bound = null;
		Paint paint = null;
		final int imageW = targetImg.getWidth();
		final int imageH = targetImg.getHeight();

		final Shape background = new java.awt.geom.RoundRectangle2D.Double(
				-imageW / 2d - PAD, -imageH / 2d - PAD, imageW + PAD * 2d,
				imageH + PAD * 2d, R, R);
		final Paint backgroundPaint = Color.white;

		bound = new Rectangle2D.Double(-imageW / 2, -imageH / 2, imageW, imageH);
		paint = new TexturePaint(targetImg, bound);

		cg = new CustomGraphic(bound, paint, NodeDetails.ANCHOR_CENTER);
		cgList.add(new CustomGraphic(background, backgroundPaint,
				NodeDetails.ANCHOR_CENTER));
		cgList.add(cg);
	}

	private void createImage(String url) throws IOException {
		if(url == null)
			throw new IllegalStateException("URL string cannot be null.");
		
		final URL imageLocation = new URL(url);
		originalImage = ImageIO.read(imageLocation);
		
		if(originalImage == null)
			throw new IllegalStateException("This is not an image location: " + imageLocation.toString());
		
		System.out.println("######## Image Created: " + originalImage);
	}

	@Override
	public Image getImage() {
		if (scaledImage == null)
			return originalImage;
		else
			return scaledImage;
	}

	@Override
	public Image resizeImage(int width, int height) {
		final Image img = originalImage.getScaledInstance(width, height,
				Image.SCALE_AREA_AVERAGING);
		try {
			scaledImage = ImageUtil.toBufferedImage(img);
		} catch (InterruptedException e) {
			// Could not get scaled one
			e.printStackTrace();
			return originalImage;
		}
		buildCustomGraphics(scaledImage);
		return scaledImage;
	}
	
	public Image resetImage() {
		if(scaledImage != null) {
			scaledImage.flush();
			scaledImage = null;
		}
		buildCustomGraphics(originalImage);
		return originalImage;
	}
}
