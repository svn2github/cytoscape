package gpml;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import data.gpml.GmmlDataObject;
import ding.view.ViewportChangeListener;

public class Shape extends JComponent implements ViewportChangeListener {
	GmmlDataObject gdata;
	
	public Shape(GmmlDataObject o) {
		gdata = o;
	}
	public void paint(Graphics g) {
		int vx = (int)GpmlImporter.mToV(gdata.getMLeft());
		int vy = (int)GpmlImporter.mToV(gdata.getMTop());
		int vw = (int)GpmlImporter.mToV(gdata.getMWidth());
		int vh = (int)GpmlImporter.mToV(gdata.getMHeight());
		
		switch(gdata.getShapeType()) {
		case OVAL:
			g.drawOval(vx, vy, vw, vh);
			break;
		case RECTANGLE:
			g.drawRect(vx, vy, vw, vh);
			break;
		}
	}
	
	public void viewportChanged(int w, int h, double newXCenter, double newYCenter, double newScaleFactor) {
		// TODO Auto-generated method stub
		
	}
}
