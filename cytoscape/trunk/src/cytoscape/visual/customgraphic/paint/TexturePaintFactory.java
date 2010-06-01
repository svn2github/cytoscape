package cytoscape.visual.customgraphic.paint;

import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import cytoscape.render.stateful.PaintFactory;

public class TexturePaintFactory implements PaintFactory {
	
	private BufferedImage img;
	
	public TexturePaintFactory(final BufferedImage img) {
		this.img = img;
	}

	@Override
	public Paint getPaint(Rectangle2D bound) {
		return new TexturePaint(img, bound);
	}

}
