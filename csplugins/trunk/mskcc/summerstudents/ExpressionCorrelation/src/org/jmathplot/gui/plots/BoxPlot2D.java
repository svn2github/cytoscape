package org.jmathplot.gui.plots;

import java.awt.*;
import org.jmathplot.gui.*;
import org.jmathplot.gui.plotObjects.*;
import org.jmathplot.util.*;

public class BoxPlot2D
	extends Plot {

	private double[][] widths;

	public BoxPlot2D(double[][] XY, double[][] W, Color c, String n, Base b) {
		super(XY, c, n, b);
		widths = W;
		double[] datasMin = DoubleArray.min(datas);
		double[] widthsMin = DoubleArray.min(widths);
		double[] datasMax = DoubleArray.max(datas);
		double[] widthsMax = DoubleArray.max(widths);
		base.includeInBounds(new double[] {
			datasMin[0] - widthsMax[0], datasMin[1] - widthsMax[2]});
		base.includeInBounds(new double[] {
			datasMax[0] + widthsMax[1], datasMax[1] - widthsMax[3]});
	}

	public void plot(Graphics comp, Color c) {
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.setColor(c);
		for (int i = 0; i < datas.length; i++) {
			double[] Xmin = {
					datas[i][0] - widths[i][0], datas[i][1]};
			double[] Xmax = {
					datas[i][0] + widths[i][1], datas[i][1]};
			double[] Ymin = {
					datas[i][0], datas[i][1] - widths[i][2]};
			double[] Ymax = {
					datas[i][0], datas[i][1] + widths[i][3]};
			double[] Center = {
					  datas[i][0], datas[i][1]};

			drawLine(Xmin, Xmax, comp2D);
			drawLine(Ymin, Ymax, comp2D);
			drawDot(Center, comp2D);
		}
	}

	public void note(Graphics comp) {
		plot(comp, Color.black);
	}

	public static void main(String[] args) {
		double[][] X = DoubleArray.random(10, 6);
		new FrameView(new Plot2DPanel(X, "Box Plot 2D", PlotPanel.BOX));
	}

}