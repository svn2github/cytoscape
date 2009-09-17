package org.cytoscape.io.internal.write.graphics;

import org.cytoscape.view.model.CyNetworkView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Bitmap exporter by the ImageIO class.
 * @author Samad Lotia
 */
public class BitmapExporter implements Exporter
{
	private String extension;
	private double scale;

	public BitmapExporter(String extension, double scale)
	{
		this.extension = extension;
		this.scale = scale;

		boolean match = false;
		String[] formats = ImageIO.getWriterFormatNames();
		for (int i = 0; i < formats.length; i++)
		{
			if (formats[i].equals(extension))
			{
				match = true;
				break;
			}
		}
		if (!match)
			throw new IllegalArgumentException("Format " + extension + " is not supported by the ImageIO class");
	}

	public void export(CyNetworkView view, FileOutputStream stream) throws IOException
	{
		// TODO update with presentation code
	/*
		// TODO NEED RENDERER
		int width  = (int) (view.getComponent().getWidth() * scale);
		// TODO NEED RENDERER
		int height = (int) (view.getComponent().getHeight() * scale);

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.scale(scale, scale);
		// TODO NEED RENDERER
		view.printNoImposter(g);
		g.dispose();
		
		ImageIO.write(image, extension, stream);
		*/
	}
}
