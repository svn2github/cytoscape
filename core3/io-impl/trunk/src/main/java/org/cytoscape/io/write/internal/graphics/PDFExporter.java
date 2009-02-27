package org.cytoscape.io.write.internal.graphics;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.cytoscape.view.GraphView;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * PDF exporter by the iText library.
 * @author Samad Lotia
 */
public class PDFExporter implements Exporter
{
	private boolean exportTextAsFont = true;

	public void export(GraphView view, FileOutputStream stream) throws IOException
	{
		// TODO NEED RENDERER
		view.setPrintingTextAsShape(!exportTextAsFont);
		
		Rectangle pageSize = PageSize.LETTER;
		Document document = new Document(pageSize);
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, stream);
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			Graphics2D g = null;
			if ( exportTextAsFont ) {
				g = cb.createGraphics(pageSize.getWidth(), pageSize.getHeight(), new DefaultFontMapper());
			} else {
				g = cb.createGraphicsShapes(pageSize.getWidth(), pageSize.getHeight());
			}
			// TODO NEED RENDERER
			double imageScale = Math.min(pageSize.getWidth()  / ((double) view.getComponent().getWidth()),
			                             pageSize.getHeight() / ((double) view.getComponent().getHeight()));
			g.scale(imageScale, imageScale);
			// TODO NEED RENDERER
			view.print(g);
			g.dispose();
		}
		catch (DocumentException exp)
		{
			throw new IOException(exp.getMessage());
		}

		document.close();
	}
	public void setExportTextAsFont(boolean pExportTextAsFont) {
		exportTextAsFont = pExportTextAsFont;
	}
}
