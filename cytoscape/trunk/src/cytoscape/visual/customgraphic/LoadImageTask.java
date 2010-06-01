package cytoscape.visual.customgraphic;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

public class LoadImageTask implements Callable<BufferedImage> {

	private final URL imageURL;

	public LoadImageTask(final URL imageURL) {
		this.imageURL = imageURL;
	}

	@Override
	public BufferedImage call() throws Exception {
		if (imageURL == null)
			throw new IllegalStateException("URL string cannot be null.");

		return ImageIO.read(imageURL);
	}

}
