package org.jmathplot.gui.plots;

import java.awt.*;
import org.jmathplot.gui.plotObjects.*;
import org.jmathplot.util.*;

public class HistogramPlot2D
	extends Plot {

	private double[][] widths;

	public HistogramPlot2D(double[][] XY, double[][] W, Color c, String n, Base b) {
		super(XY, c, n, b);
		widths = W;
		double[] datasMin = DoubleArray.min(datas);
		double[] widthsMin = DoubleArray.min(widths);
		double[] datasMax = DoubleArray.max(datas);
		double[] widthsMax = DoubleArray.max(widths);
		base.includeInBounds(new double[] {
			datasMin[0] - widthsMax[0] / 2, 0});
		base.includeInBounds(new double[] {
			datasMax[0] + widthsMax[0] / 2, 0});
	}

	public void plot(Graphics comp, Color c) {
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.setColor(c);
		for (int i = 0; i < datas.length; i++) {
			double[] topLeft = {datas[i][0] - widths[i][0] / 2, datas[i][1]};
			double[] topRight = {datas[i][0] + widths[i][0] / 2, datas[i][1]};
			double[] bottomLeft = {datas[i][0] - widths[i][0] / 2, 0};
			double[] bottomRight = {datas[i][0] + widths[i][0] / 2, 0};

			drawLine(bottomLeft, topLeft, comp2D);
			drawLine(topLeft, topRight, comp2D);
			drawLine(topRight, bottomRight, comp2D);
			drawLine(bottomRight, bottomLeft, comp2D);
		}
	}

	public void note(Graphics comp) {
		plot(comp, Color.black);
	}

}