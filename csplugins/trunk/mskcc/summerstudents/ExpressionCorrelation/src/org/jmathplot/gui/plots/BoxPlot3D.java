package org.jmathplot.gui.plots;

import java.awt.*;
import org.jmathplot.gui.*;
import org.jmathplot.gui.plotObjects.*;
import org.jmathplot.util.*;

public class BoxPlot3D
	extends Plot {

	private double[][] widths;

	public BoxPlot3D(double[][] XY, double[][] W, Color c, String n, Base b) {
		super(XY, c, n, b);
		widths = W;
		double[] datasMin = DoubleArray.min(datas);
		double[] widthsMin = DoubleArray.min(widths);
		double[] datasMax = DoubleArray.max(datas);
		double[] widthsMax = DoubleArray.max(widths);
		base.includeInBounds(new double[] {
			datasMin[0] - widthsMax[0], datasMin[1] - widthsMax[2], datasMin[2] - widthsMax[4]});
		base.includeInBounds(new double[] {
			datasMax[0] + widthsMax[1], datasMax[1] - widthsMax[3], datasMax[2] - widthsMax[5]});
	}

	public void plot(Graphics comp, Color c) {
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.setColor(c);
		for (int i = 0; i < datas.length; i++) {
			double[] Xmin = {datas[i][0] - widths[i][0], datas[i][1], datas[i][2]};
			double[] Xmax = {datas[i][0] + widths[i][1], datas[i][1], datas[i][2]};
			double[] Ymin = {datas[i][0], datas[i][1] - widths[i][2], datas[i][2]};
			double[] Ymax = {datas[i][0], datas[i][1] + widths[i][3], datas[i][2]};
			double[] Zmin = {datas[i][0], datas[i][1], datas[i][2] - widths[i][4]};
			double[] Zmax = {datas[i][0], datas[i][1], datas[i][2] + widths[i][5]};
			double[] Center = {datas[i][0], datas[i][1], datas[i][2]};

			drawLine(Xmin, Xmax, comp2D);
			drawLine(Ymin, Ymax, comp2D);
			drawLine(Zmin, Zmax, comp2D);
			drawDot(Center, comp2D);
		}
	}

	public void note(Graphics comp) {
		plot(comp, Color.black);
//		Graphics2D comp2D = (Graphics2D)comp;
//		comp2D.setColor(Color.black);
//		RelativeCoord c = new RelativeCoord(datas.mean().getRowArrayCopy(0),base);
//		comp2D.drawString(name,c.getScreenCoordCopy()[0],c.getScreenCoordCopy()[1]);
	}

	public static void main(String[] args) {
		double[][] X = DoubleArray.random(10, 9);
		new FrameView(new Plot3DPanel(X, "Box Plot 3D", PlotPanel.BOX));
	}
}