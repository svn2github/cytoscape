package org.jmathplot.gui.plotObjects;

import java.awt.*;

public class Line implements Plotable {

	private Coord[] extrem = new Coord[2];
	private Color color;


	public Line(Coord c1,Coord c2,Color col) {
		extrem[0] = c1;
		extrem[1] = c2;
		color = col;
	}

	public void plot(Graphics comp) {
		Graphics2D comp2D = (Graphics2D)comp;
		comp2D.setColor(color);
		comp2D.drawLine(extrem[0].getScreenCoordCopy()[0],extrem[0].getScreenCoordCopy()[1],extrem[1].getScreenCoordCopy()[0],extrem[1].getScreenCoordCopy()[1]);
	}

}