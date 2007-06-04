package gpml;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import org.pathvisio.model.PathwayElement;

import ding.view.DGraphView;

public class Label extends Annotation {

	public Label(PathwayElement o, DGraphView view) {
		super(o, view);
	}

	public Shape getVOutline() {
//		FontMetrics fm = getFontMetrics(getVFont());
//		LineMetrics lm = fm.getStringBounds(pwElm.getTextLabel(), 
//				image != null ? image.createGraphics() : getGraphics());
		return new Rectangle(getVLeft(), getVTop(), getVWidth(), getVHeight());
	}
	
	private Font getVFont() {
		int style = pwElm.isBold() ? Font.BOLD : Font.PLAIN;
		style |= pwElm.isItalic() ? Font.ITALIC : Font.PLAIN;
		return new Font(pwElm.getFontName(), style, (int)GpmlImporter.mToV(pwElm.getMFontSize()));
	}

	public void paint(Graphics g) {
		if(image == null) return;
		
		Graphics2D image2D = image.createGraphics();
				
		Composite origComposite = image2D.getComposite();

		Rectangle b = getBounds();
		
		image2D.setFont(getVFont()); //TODO: scale font
		image2D.setColor(color(pwElm.getColor()));
		
		image2D.drawString(pwElm.getTextLabel(), 0, - b.height / 2);
		image2D.drawRect(b.x, b.y, b.width, b.height);
		
		image2D.setComposite(origComposite);
		((Graphics2D) g).drawImage(image, null, 0, 0);
	}
}
