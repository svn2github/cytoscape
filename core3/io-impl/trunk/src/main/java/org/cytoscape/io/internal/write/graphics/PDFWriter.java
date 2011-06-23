package org.cytoscape.io.internal.write.graphics;

import java.awt.Graphics2D;
import java.awt.Image;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.cytoscape.io.write.CyWriter;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.presentation.property.MinimalVisualLexicon;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.DefaultFontMapper;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGraphics2D;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * PDF exporter by the iText library.
 */
public class PDFWriter extends AbstractTask implements CyWriter {

	private static final Logger logger = LoggerFactory.getLogger(PDFWriter.class);

	private boolean exportTextAsFont = true;
	private final Double width;
	private final Double height;
	private final RenderingEngine<?> engine;
	private final OutputStream stream;

	public PDFWriter(final RenderingEngine<?> engine, final OutputStream stream) {
		if (engine == null)
			throw new NullPointerException("Rendering Engine is null.");
		if (stream == null)
			throw new NullPointerException("Stream is null.");

		this.engine = engine;
		this.stream = stream;

		width = engine.getViewModel().getVisualProperty(MinimalVisualLexicon.NETWORK_WIDTH);
		height = engine.getViewModel().getVisualProperty(MinimalVisualLexicon.NETWORK_HEIGHT);

		logger.debug("PDFWriter created.");
	}
	

	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO should be accomplished with renderer properties
		// view.setPrintingTextAsShape(!exportTextAsFont);

		taskMonitor.setProgress(0.0);
		taskMonitor.setStatusMessage("Creating PDF image...");

		Thread.sleep(2000);
		logger.debug("PDF Rendering start");

		final OutputStream os = new BufferedOutputStream(new FileOutputStream("itextTest100000.pdf"));

		final Rectangle pageSize = PageSize.LETTER;
		final Document document = new Document(pageSize);

		logger.debug("Document created: " + document);
		
		final PdfWriter writer = PdfWriter.getInstance(document, os);
		document.open();
		
		final PdfContentByte canvas = writer.getDirectContent();
		logger.debug("CB0 created: " + canvas.getClass());
		
		
		final float pageWidth = pageSize.getWidth();
		final float pageHeight = pageSize.getHeight();
		
		logger.debug("Page W: " + pageWidth + " Page H: " + pageHeight);
		final DefaultFontMapper fontMapper = new DefaultFontMapper();
		logger.debug("FontMapper created = " + fontMapper);
		Graphics2D g = null;
			logger.debug("!!!!! Enter block 2");
			g = canvas.createGraphicsShapes(pageWidth, pageHeight);
			logger.debug("!!!!! G2D created: " + g);
			
			g.dispose();
//		if (exportTextAsFont)
//			g = cb.createGraphics(pageWidth, pageHeight, new DefaultFontMapper());
//		else
//			g = cb.createGraphicsShapes(pageWidth, pageHeight);

		
		
//		double imageScale = Math.min(pageSize.getWidth() / width, pageSize.getHeight() / height);
//		g.scale(imageScale, imageScale);

//		final Image image = engine.createImage(width.intValue(), height.intValue());
//
//		logger.debug("AWT Image created for PDF: " + image);
//		com.itextpdf.text.Image itxImage = com.itextpdf.text.Image.getInstance(image, null);
//		logger.debug("iText Image created for PDF: " + itxImage);
//		canvas.addImage(itxImage, itxImage.getWidth(), 0, 0, itxImage.getHeight(), 0, 0);
//		g.drawImage(image, 0, 0, null);
		

		os.close();
		stream.close();
		document.close();

		logger.debug("PDF rendering finished.");
	}
	
}
