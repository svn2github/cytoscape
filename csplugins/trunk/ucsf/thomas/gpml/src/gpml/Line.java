package gpml;

import gpml.util.LinAlg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.pathvisio.model.LineStyle;
import org.pathvisio.model.PathwayElement;

import ding.view.DGraphView;

public class Line extends Annotation {
	private static final int HEADSPACE = 30;
	private static final int ARROWHEIGHT = 5;
	private static final int ARROWWIDTH = 10;
	private static final int TBARHEIGHT = 15;
	private static final int TBARWIDTH = 2;
	private static final int LRDIAM = 10;
	private static final int RRDIAM = LRDIAM + 3;
	private static final int RECEPWIDTH = 10;
	private static final int RECEPHEIGHT = 15;
	private static final int LIGANDWIDTH = RECEPWIDTH + 3;
	private static final int LIGANDHEIGHT = RECEPHEIGHT + 3;
	
	public Line(PathwayElement o, DGraphView view) {
		super(o, view);
	}

	protected double getVStartX() { return GpmlImporter.mToV((pwElm.getMStartX())); }
	protected double getVStartY() { return GpmlImporter.mToV((pwElm.getMStartY())); }
	protected double getVEndX() { return GpmlImporter.mToV((pwElm.getMEndX())); }
	protected double getVEndY() { return GpmlImporter.mToV((pwElm.getMEndY())); }
	
	public Line2D getVLine() {
		return new Line2D.Double(getVStartX(), getVStartY(), getVEndX(), getVEndY());
	}
	
	public Shape getVOutline() {
		//TODO: bigger than necessary, just to include the arrowhead / shape at the end
		BasicStroke stroke = new BasicStroke(HEADSPACE);
		Shape outline = stroke.createStrokedShape(getVLine());
		return outline;
	}
	
	public void paint(Graphics g) {
		if(image == null) return;
		
		Graphics2D image2D = image.createGraphics();
		
		Composite origComposite = image2D.getComposite();
		
		Color c = color(pwElm.getColor());
		image2D.setColor(c);
				
		int ls = pwElm.getLineStyle();
		if (ls == LineStyle.SOLID) {
			image2D.setStroke(new BasicStroke());
		}
		else if (ls == LineStyle.DASHED) { 
			image2D.setStroke(new BasicStroke(1, 
									BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 
									10, new float[] {4, 4}, 0));
		}			

		Rectangle b = relativeToBounds(viewportTransform(getVLine())).getBounds();
		Line2D l = getVLine();
		Point2D start = l.getP1();
		Point2D end = l.getP2();
		
		double xdir = end.getX() - start.getX();
		double ydir = end.getY() - start.getY();
		
		double xs = xdir >= 0 ? b.x : b.x + b.width;
		double ys = ydir >= 0 ? b.y : b.y + b.height;
		double xe = xdir >= 0 ? xs + b.width : b.x;
		double ye = ydir >= 0 ? ys + b.height : b.y;
		
		image2D.drawLine ((int)xs, (int)ys, (int)xe, (int)ye);
		
		switch (pwElm.getLineType()) {
			case ARROW:				
				paintArrowHead(image2D, xs, ys, xe, ye, ARROWWIDTH, ARROWHEIGHT);
				break;
			case TBAR:	
				paintTBar(image2D, xs, ys, xe, ye, TBARWIDTH, TBARHEIGHT);
				break;
			case LIGAND_ROUND:	
				paintLRound(image2D, xe, ye, LRDIAM);
				break;
			case RECEPTOR_ROUND:
				paintRRound(image2D, xs, ys, xe, ye, RRDIAM);
				break;
			case RECEPTOR: //TODO: implement receptor
			case RECEPTOR_SQUARE:
				paintReceptor(image2D, xs, ys, xe, ye, RECEPWIDTH, RECEPHEIGHT);
				break;
			case LIGAND_SQUARE:
			{
				paintLigand(image2D, xs, ys, xe, ye, LIGANDWIDTH, LIGANDHEIGHT);
			}
			break;
	}
				
		image2D.setComposite(origComposite);
		((Graphics2D) g).drawImage(image, null, 0, 0);
	}
	
	private double getAngle(double xs, double ys, double xe, double ye) {
		if(xs == xe && ys == ye) return 0; //Unable to determine direction
		Point2D ps = new Point2D.Double(xe - xs, ye - ys);
		Point2D pe = new Point2D.Double(1, 0);
		
		return LinAlg.angle(ps, pe);
	}
	
	private void paintArrowHead(Graphics2D g2d, double xs, double ys, double xe, double ye, double w, double h) {			
		double angle = getAngle(xs, ys, xe, ye);
		int[] xpoints = new int[] { (int)xe, (int)(xe - w), (int)(xe - w) };
		int[] ypoints = new int[] { (int)ye, (int)(ye - h), (int)(ye + h) };
		
		Polygon arrow = new Polygon(xpoints, ypoints, 3);
		AffineTransform f = new AffineTransform();
		f.rotate(-angle, xe, ye);
		Shape rotArrow = f.createTransformedShape(arrow);
		g2d.fill(rotArrow);
	}
	
	private void paintTBar(Graphics2D g2d, double xs, double ys, double xe, double ye, double w, double h) {
		double angle = getAngle(xs, ys, xe, ye);
	
		Rectangle2D bar = new Rectangle2D.Double(xe - w, ye - h/2, w, h);
		AffineTransform f = new AffineTransform();
		f.rotate(-angle, xe, ye);
		Shape rotBar = f.createTransformedShape(bar);
		g2d.fill(rotBar);
	}
	
	private void paintLRound(Graphics2D g2d, double xe, double ye, int d) {	
		g2d.fillOval ((int)xe - d/2, (int)ye - d/2, d, d);
	}
	
	private void paintRRound(Graphics2D g2d, double xs, double ys, double xe, double ye, int d) {
		double angle = Math.toDegrees(getAngle(xs, ys, xe, ye));
		Arc2D arc = new Arc2D.Double((int)xe, (int)ye + d/2, d, d, -90, 90, Arc2D.OPEN);
		AffineTransform f = new AffineTransform();
		f.rotate(-angle, xe, ye);
		Shape rotArc = f.createTransformedShape(arc);
		g2d.draw(rotArc);		
	}
	
	private void paintReceptor(Graphics2D g2d, double xs, double ys, double xe, double ye, double w, double h) {			
		double angle = getAngle(xs, ys, xe, ye);
		
		/* Path2D Only in Java 1.6....
		Path2D rec = new Path2D.Double();
		rec.moveTo(xe + w, ye + h/2);
		rec.lineTo(xe, ye + h/2);
		rec.lineTo(xe, ye - h/2);
		rec.lineTo(xe + w, ye - h/2);
		AffineTransform f = new AffineTransform();
		f.rotate(-angle, xe, ye);
		Shape rotRec = f.createTransformedShape(rec);
		g2d.draw(rotRec);
		*/
		
		AffineTransform f = new AffineTransform();
		f.rotate(-angle, xe, ye);
		
		Shape l = new Line2D.Double(xe + w, ye + h/2, xe, ye + h/2);
		Shape r = f.createTransformedShape(l);
		g2d.draw(r);
		l = new Line2D.Double(xe, ye + h/2, xe, ye - h/2);
		r = f.createTransformedShape(l);
		g2d.draw(r);
		l = new Line2D.Double(xe, ye - h/2, xe + w, ye - h/2);
		r = f.createTransformedShape(l);
		g2d.draw(r);
	}
	
	private void paintLigand(Graphics2D g2d, double xs, double ys, double xe, double ye, double w, double h) {
		double angle = getAngle(xs, ys, xe, ye);
	
		Rectangle2D bar = new Rectangle2D.Double(xe - w, ye - h/2, w, h);
		AffineTransform f = new AffineTransform();
		f.rotate(-angle, xe, ye);
		Shape rotBar = f.createTransformedShape(bar);
		g2d.fill(rotBar);
	}

}
