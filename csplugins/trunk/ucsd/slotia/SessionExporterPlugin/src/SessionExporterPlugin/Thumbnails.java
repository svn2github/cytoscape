package SessionExporterPlugin;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;

public class Thumbnails
{
	private static int MAX_THUMBNAIL_WIDTH = 200;
	private static int MAX_THUMBNAIL_HEIGHT = 200;

	public static BufferedImage createThumbnail(BufferedImage original)
	{
		int width, height;
		if (original.getWidth() > original.getHeight())
		{
			width = MAX_THUMBNAIL_WIDTH;
			height = original.getHeight() * width / original.getWidth();
		}
		else
		{
			height = MAX_THUMBNAIL_HEIGHT;
			width = original.getWidth() * height / original.getHeight();
		}

		BufferedImage thumbnail = new BufferedImage(width, height, original.getType());
		Graphics2D graphics = thumbnail.createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					  RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics.setBackground(Color.WHITE);
		graphics.drawImage(original, 0, 0, width, height, null);

		return thumbnail;
	}
}
