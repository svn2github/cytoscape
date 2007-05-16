package SessionExporterPlugin;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Rectangle;

public class Thumbnails
{
	private static int MAX_THUMBNAIL_WIDTH = 200;
	private static int MAX_THUMBNAIL_HEIGHT = 200;

	public static BufferedImage createThumbnail(BufferedImage original)
	{
		if (original == null)
		{
			int imageWidth = MAX_THUMBNAIL_WIDTH;
			int imageHeight = MAX_THUMBNAIL_HEIGHT;
			BufferedImage thumbnail = new BufferedImage(imageWidth, imageHeight,
								    BufferedImage.TYPE_INT_RGB);
			Graphics graphics = thumbnail.getGraphics();
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.fillRect(0, 0, thumbnail.getWidth(), thumbnail.getHeight());
			graphics.setColor(Color.BLACK);
			String message = "Network does not have view";
			int messageWidth = graphics.getFontMetrics().stringWidth(message);
			int messageHeight = graphics.getFont().getSize();
			graphics.drawString(message,
					    (imageWidth - messageWidth) / 2,
					    (imageHeight - messageHeight) / 2);
			return thumbnail;
		}

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
