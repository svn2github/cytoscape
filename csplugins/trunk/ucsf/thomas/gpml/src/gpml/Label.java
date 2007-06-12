package gpml;

import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.pathvisio.model.PathwayElement;

import ding.view.DGraphView;

public class Label extends Annotation {

	public Label(PathwayElement o, DGraphView view) {
		super(o, view);
	}

	public Shape getVOutline() {
//		FontMetrics fm = getFontMetrics(getVFont());
//		Rectangle2D r = fm.getStringBounds(pwElm.getTextLabel(), 
//				image != null ? image.createGraphics() : getGraphics());
//		return new Rectangle2D.Double(getVLeft(), getVTop(), r.getWidth(), r.getHeight());
		return new Rectangle(getVLeft(), getVTop(), getVWidth(), getVHeight());
	}
	
	private Font getVFont() {
		int style = pwElm.isBold() ? Font.BOLD : Font.PLAIN;
		style |= pwElm.isItalic() ? Font.ITALIC : Font.PLAIN;
		return new Font(pwElm.getFontName(), style, (int)GpmlImporter.mToV(pwElm.getMFontSize() * scaleFactor));
	}

	public void paint(Graphics g) {
		if(image == null) return;
		
		Graphics2D image2D = image.createGraphics();
				
		Composite origComposite = image2D.getComposite();

		Rectangle b = getBounds();
		image2D.setFont(getVFont());
		image2D.setColor(color(pwElm.getColor()));
		
		image2D.drawString(pwElm.getTextLabel(), 0, b.height / 2);
		image2D.drawRect(0, 0, b.width - 1, b.height - 1);
		
		image2D.setComposite(origComposite);
		((Graphics2D) g).drawImage(image, null, 0, 0);
	}
	
	double scaleFactor = 1;
	
	public void viewportChanged(int w, int h, double newXCenter, double newYCenter, double newScaleFactor) {
		scaleFactor = newScaleFactor;
		super.viewportChanged(w, h, newXCenter, newYCenter, newScaleFactor);
		
	}
}
