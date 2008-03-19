package cytoscape.util.export;

import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Graphics2D;

import cytoscape.Cytoscape;
import org.cytoscape.view.GraphView;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * PDF exporter by the iText library.
 * @author Samad Lotia
 */
public class PDFExporter implements Exporter
{
	public void export(GraphView view, FileOutputStream stream) throws IOException
	{
		Rectangle pageSize = PageSize.LETTER;
		Document document = new Document(pageSize);
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, stream);
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			Graphics2D g = cb.createGraphicsShapes((int) pageSize.getWidth(), (int) pageSize.getHeight());
			double imageScale = Math.min(pageSize.getWidth()  / ((double) view.getComponent().getWidth()),
			                             pageSize.getHeight() / ((double) view.getComponent().getHeight()));
			g.scale(imageScale, imageScale);
			view.print(g);
			g.dispose();
		}
		catch (DocumentException exp)
		{
			throw new IOException(exp.getMessage());
		}

		document.close();
	}
}
