package org.cytoscape.io.internal.write.graphics;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.view.model.View;
import org.cytoscape.view.presentation.RenderingEngine;
import static org.cytoscape.view.presentation.property.TwoDVisualLexicon.*;

import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;

import javax.imageio.ImageIO;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 */
class BitmapWriter extends AbstractTask implements CyWriter {

	private static final Logger logger = LoggerFactory.getLogger(BitmapWriter.class);

	@Tunable(description="Image scale")
	public BoundedDouble scaleFactor;

	private final File outFile;
	private final RenderingEngine re;
	private final View<?> view;
	private String extension = null; 
	
	public BitmapWriter(View<?> view, RenderingEngine re, File outFile, Set<String> extensions) {
		this.view = view;
		this.re = re;
		this.outFile = outFile;
		setExtension( extensions );
		scaleFactor = new BoundedDouble(0.0,100.0,500.0,false,false);	
	}

	private void setExtension( Set<String> extensions ) {

		for ( String format : ImageIO.getWriterFormatNames() ) {
			for ( String ext : extensions ) {
				if ( format.equals(ext) ) {
					extension = format;
					return;
				}
			}
		}
		throw new IllegalArgumentException("Image format ("+ extensions.toString() +") NOT supported by ImageIO");
	}

	public void run(TaskMonitor tm) throws Exception {
		logger.debug("about to export graphics of type: " + extension + 
		             " and to file: " + outFile.getName()); 

		final double scale = scaleFactor.getValue().doubleValue();

		int width  = (int) (view.getVisualProperty(NETWORK_WIDTH).doubleValue() * scale);
		int height = (int) (view.getVisualProperty(NETWORK_HEIGHT).doubleValue() * scale);

		ImageIO.write(((BufferedImage)re.createImage(width,height)), extension, outFile);
	}

	// TODO use the one from AbstractTask
	public void cancel() {
	}
}
