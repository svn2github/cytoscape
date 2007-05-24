package gpml;


import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.pathvisio.model.PathwayElement;

import ding.view.DGraphView;
import ding.view.ViewportChangeListener;

public class Shape extends Annotation implements ViewportChangeListener {	
	
	public Shape(PathwayElement o, DGraphView view) {
		super(o, view);
	}

	public Rectangle2D.Double getVRectangle() {
		return new Rectangle2D.Double(getVLeft(), getVTop(), getVWidth(), getVHeight());
	}
	
	public java.awt.Shape getVOutline() {
		Rectangle2D.Double r = getVRectangle();
		r.width = r.width + 2;
		r.height = r.height + 2;
		AffineTransform f = new AffineTransform();
		f.rotate(pwElm.getRotation(), getVCenterX(), getVCenterY());
		java.awt.Shape outline = f.createTransformedShape(r);
		return outline;
	}
	
	public Rectangle getUnrotatedBounds() {
		java.awt.Shape outline = viewportTransform(getVOutline());
		Rectangle2D b = outline.getBounds();
		AffineTransform f = new AffineTransform();
		f.rotate(-pwElm.getRotation(), b.getCenterX(), b.getCenterY());
		return f.createTransformedShape(outline).getBounds();
	}
	
	int getVLeft() {
		return (int)GpmlImporter.mToV(pwElm.getMLeft());
	}
	int getVTop() {
		return (int)GpmlImporter.mToV(pwElm.getMTop());
	}
	int getVWidth() {
		return (int)GpmlImporter.mToV(pwElm.getMWidth());
	}
	int getVHeight() {
		return (int)GpmlImporter.mToV(pwElm.getMHeight());
	}
	double getVCenterX() {
		return GpmlImporter.mToV(pwElm.getMCenterX());
	}
	double getVCenterY() {
		return GpmlImporter.mToV(pwElm.getMCenterY());
	}
	
	public void paint(Graphics g) {
		//Relative to yourself
		if(image == null) return;
		
		Graphics2D image2D = image.createGraphics();
		
		Composite origComposite = image2D.getComposite();
		
		//Rectangle b = relativeToBounds(getUnrotatedBounds()).getBounds();
		Rectangle b = relativeToBounds(viewportTransform(getVRectangle())).getBounds();
		
		Color fillcolor = color(pwElm.getColor());
		Color linecolor = color(pwElm.getFillColor());
		
		int sw = (int)getStrokeWidth();
		int x = b.x;
		int y = b.y;
		int w = b.width - sw;
		int h = b.height - sw;
		int cx = x + w/2;
		int cy = y + h/2;
						
		image2D.rotate(pwElm.getRotation(), cx, cy);
		
		switch(pwElm.getShapeType()) {
		case OVAL:
			image2D.setColor(linecolor);
			image2D.drawOval(x, y, w, h);
			if(!pwElm.isTransparent()) {
				image2D.setColor(fillcolor);
				image2D.fillOval(x, y, w, h);
			}
			break;
		case ARC:
			image2D.setColor(linecolor);
			image2D.drawArc(x, y, w, h, 0, -180);
			break;
		case BRACE:
			image2D.setColor(linecolor);
			image2D.drawLine (cx + h/2, cy, cx + w/2 - h/2, cy); //line on the right
			image2D.drawLine (cx - h/2, cy, cx - w/2 + h/2, cy); //line on the left
			image2D.drawArc (cx - w/2, cy, h, h, -180, -90); //arc on the left
			image2D.drawArc (cx - h, cy - h,	h, h, -90, 90); //left arc in the middle
			image2D.drawArc (cx, cy - h, h, h, -90, -90); //right arc in the middle
			image2D.drawArc (cx + w/2 - h, cy, h, h, 0, 90); //arc on the right
			break;
		default:
			image2D.setColor(linecolor);
			image2D.drawRect(x, y, w, h);
			if(!pwElm.isTransparent()) {
				image2D.setColor(fillcolor);
				image2D.fillRect(x, y, w, h);
			}
			break;
		}
		
		image2D.setComposite(origComposite);
		((Graphics2D) g).drawImage(image, null, 0, 0);
	}
}
