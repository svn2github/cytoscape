package org.jmathplot.gui.plots;

import java.awt.*;
import org.jmathplot.gui.plotObjects.*;

public abstract class Plot implements Plotable, Noteable {

	public int dot_radius = 2;

	protected String name;
	protected Base base;
	protected double[][] datas;
	protected Color color;
	protected boolean noteEachCoord = false;

	public Plot(double[][] d,Color c,String n,Base b) {
		name = n;
		datas = d;
		base = b;
		color = c;
		base = b;
	}

	public void setNoteEachCoord(boolean nec) {
		noteEachCoord = nec;
	}

	public void setDatas(double[][] d) {
		datas = d;
	}

	public double[][] getDatas() {
		return datas;
	}

	public void setName(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	public void setColor(Color c) {
		color = c;
	}

	public RelativeCoord[] getCoords() {
		RelativeCoord[] coords = new RelativeCoord[datas.length];
		for (int i =0; i < datas.length; i++) {
			coords[i] = new RelativeCoord(datas[i],base);
		}
		return coords;
	}

	public boolean tryNote(int[] screenCoord) {
		RelativeCoord[] coords = getCoords();
		for (int i = 0; i < coords.length; i++) {
			if (coords[i].tryNote(screenCoord)) {
				return true;
			}
		}
		return false;
	}

	public boolean tryNote(int[] screenCoord,Graphics comp) {
		RelativeCoord[] coords = getCoords();
		if (noteEachCoord) {
			for (int i = 0; i < coords.length; i++) {
				if (coords[i].tryNote(screenCoord,comp)) {
					return true;
				}
			}
			return false;
		} else {
			for (int i = 0; i < coords.length; i++) {
				if (coords[i].tryNote(screenCoord)) {
					note(comp);
					comp.setColor(Color.black);
					comp.drawString(name,screenCoord[0],screenCoord[1]);
					return true;
				}
			}
			return false;
		}
	}

	public abstract void note(Graphics comp);
	public abstract void plot(Graphics comp,Color c);

	public void plot(Graphics comp) {
		plot(comp,color);
	}

	protected void drawLine(double[] pC1,double[] pC2,Graphics2D comp2D) {
		RelativeCoord c1 = new RelativeCoord(pC1,base);
		RelativeCoord c2 = new RelativeCoord(pC2,base);
		drawLine(c1,c2,comp2D);
	}

	protected void drawLargeLine(double[] pC1,double[] pC2,Graphics2D comp2D) {
		RelativeCoord c1 = new RelativeCoord(pC1,base);
		RelativeCoord c2 = new RelativeCoord(pC2,base);
		drawLargeLine(c1,c2,comp2D);
	}

	protected void drawDot(double[] pC,Graphics2D comp2D) {
		RelativeCoord c = new RelativeCoord(pC,base);
		drawDot(c,dot_radius,comp2D);
	}

	protected void drawDot(double[] pC,int radius,Graphics2D comp2D) {
		RelativeCoord c = new RelativeCoord(pC,base);
		drawDot(c,radius,comp2D);
	}

	protected void drawPloygon(double[][] pC,Graphics2D comp2D) {
		RelativeCoord[] c = new RelativeCoord[pC.length];
		for (int i = 0; i < pC.length; i++) {
			c[i] = new RelativeCoord(pC[i],base);
		}
		drawPloygon(c,comp2D);
	}

	protected void fillPloygon(double[][] pC,Graphics2D comp2D) {
		RelativeCoord[] c = new RelativeCoord[pC.length];
		for (int i = 0; i < pC.length; i++) {
			c[i] = new RelativeCoord(pC[i],base);
		}
		fillPloygon(c,comp2D);
	}

	protected void drawLargeLine(Coord c1,Coord c2,Graphics2D comp2D) {
		comp2D.drawLine(c1.getScreenCoordCopy()[0]+1,c1.getScreenCoordCopy()[1],c2.getScreenCoordCopy()[0]+1,c2.getScreenCoordCopy()[1]);
		comp2D.drawLine(c1.getScreenCoordCopy()[0]-1,c1.getScreenCoordCopy()[1],c2.getScreenCoordCopy()[0]-1,c2.getScreenCoordCopy()[1]);
		comp2D.drawLine(c1.getScreenCoordCopy()[0],c1.getScreenCoordCopy()[1]+1,c2.getScreenCoordCopy()[0],c2.getScreenCoordCopy()[1]+1);
		comp2D.drawLine(c1.getScreenCoordCopy()[0],c1.getScreenCoordCopy()[1]-1,c2.getScreenCoordCopy()[0],c2.getScreenCoordCopy()[1]-1);
	}

	protected void drawLine(Coord c1,Coord c2,Graphics2D comp2D) {
		comp2D.drawLine(c1.getScreenCoordCopy()[0],c1.getScreenCoordCopy()[1],c2.getScreenCoordCopy()[0],c2.getScreenCoordCopy()[1]);
	}

	protected void drawDot(Coord c,int radius,Graphics2D comp2D) {
		comp2D.fillOval(c.getScreenCoordCopy()[0]-radius,c.getScreenCoordCopy()[1]-radius,2*radius,2*radius);
	}

	protected void drawPloygon(Coord[] c,Graphics2D comp2D) {
		int[] x = new int[c.length];
		for (int i = 0; i < c.length; i++) {
			x[i] = c[i].getScreenCoordCopy()[0];
		}
		int[] y = new int[c.length];
		for (int i = 0; i < c.length; i++) {

			y[i] = c[i].getScreenCoordCopy()[1];
		}
		comp2D.drawPolygon(x,y,x.length);
	}

	protected void fillPloygon(Coord[] c,Graphics2D comp2D) {
		int[] x = new int[c.length];
		for (int i = 0; i < c.length; i++) {
			x[i] = c[i].getScreenCoordCopy()[0];
		}
		int[] y = new int[c.length];
		for (int i = 0; i < c.length; i++) {
			y[i] = c[i].getScreenCoordCopy()[1];
		}
		comp2D.fillPolygon(x,y,x.length);
	}

}