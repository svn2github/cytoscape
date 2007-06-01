package SessionForWebPlugin;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.Rectangle;

public class Thumbnails
{
	public static BufferedImage createThumbnail(BufferedImage original, SessionExporterSettings settings)
	{
		if (original == null)
		{
			int imageWidth = settings.maxThumbnailWidth;
			int imageHeight = settings.maxThumbnailHeight;
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
			width = settings.maxThumbnailWidth;
			height = original.getHeight() * width / original.getWidth();
		}
		else
		{
			height = settings.maxThumbnailHeight;
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
