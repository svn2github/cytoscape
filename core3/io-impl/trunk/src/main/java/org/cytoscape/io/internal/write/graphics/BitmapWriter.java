package org.cytoscape.io.internal.write.graphics;

import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NETWORK_HEIGHT;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.NETWORK_WIDTH;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Set;

import javax.imageio.ImageIO;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.BoundedInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class BitmapWriter extends AbstractTask implements CyWriter {

	private static final Logger logger = LoggerFactory
			.getLogger(BitmapWriter.class);
	
	private static final int MAX_SIZE = 50000;

	@Tunable(description = "Image scale")
	public BoundedDouble scaleFactor;
	
	@Tunable(description = "Original Width (px)")
	public BoundedInteger width;
	
	@Tunable(description = "Original Height (px)")
	public BoundedInteger height;

	private final OutputStream outStream;
	private final RenderingEngine<?> re;
	private String extension = null;

	public BitmapWriter(final RenderingEngine<?> re, OutputStream outStream,
			Set<String> extensions) {
		this.re = re;
		this.outStream = outStream;
		setExtension(extensions);
		scaleFactor = new BoundedDouble(0.0, 1.0, 50.0, false, false);
		
		final int w = (int) (re.getViewModel()
				.getVisualProperty(NETWORK_WIDTH).doubleValue());
		final int h = (int) (re.getViewModel()
				.getVisualProperty(NETWORK_HEIGHT).doubleValue());
		
		width = new BoundedInteger(1, w, MAX_SIZE, false, false);
		height = new BoundedInteger(1, h, MAX_SIZE, false, false);

	}

	private void setExtension(Set<String> extensions) {

		for (String format : ImageIO.getWriterFormatNames()) {
			for (String ext : extensions) {
				if (format.equals(ext)) {
					extension = format;
					return;
				}
			}
		}
		throw new IllegalArgumentException("Image format ("
				+ extensions.toString() + ") NOT supported by ImageIO");
	}

	public void run(TaskMonitor tm) throws Exception {
		final double scale = scaleFactor.getValue().doubleValue();
		final int finalW = ((Number)(width.getValue()*scale)).intValue();
		final int finalH = ((Number)(height.getValue()*scale)).intValue();

		ImageIO.write(((BufferedImage) re.createImage(finalW, finalH)),
				extension, outStream);
	}
}
