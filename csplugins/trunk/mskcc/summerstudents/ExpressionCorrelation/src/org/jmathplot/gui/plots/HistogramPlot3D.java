package org.jmathplot.gui.plots;

import java.awt.*;
import org.jmathplot.gui.plotObjects.*;
import org.jmathplot.util.*;

public class HistogramPlot3D
	extends Plot {

	private double[][] widths;

	public HistogramPlot3D(double[][] XY, double[][] W, Color c, String n, Base b) {
		super(XY, c, n, b);
		widths = W;
		double[] datasMin = DoubleArray.min(datas);
		double[] widthsMin = DoubleArray.min(widths);
		double[] datasMax = DoubleArray.max(datas);
		double[] widthsMax = DoubleArray.max(widths);
		base.includeInBounds(new double[] {
			datasMin[0] - widthsMax[0] / 2,datasMin[1] - widthsMax[1] / 2, 0});
		base.includeInBounds(new double[] {
			datasMax[0] + widthsMax[0] / 2,datasMax[1] + widthsMax[1] / 2, 0});
	}

	public void plot(Graphics comp, Color c) {
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.setColor(c);
		for (int i = 0; i < datas.length; i++) {
			double[] topNW = new double[] {datas[i][0] - widths[i][0] / 2, datas[i][1] - widths[i][1] / 2, datas[i][2]};
			double[] topNE = new double[] {datas[i][0] + widths[i][0] / 2, datas[i][1] - widths[i][1] / 2, datas[i][2]};
			double[] topSW = new double[] {datas[i][0] - widths[i][0] / 2, datas[i][1] + widths[i][1] / 2, datas[i][2]};
			double[] topSE = new double[] {datas[i][0] + widths[i][0] / 2, datas[i][1] + widths[i][1] / 2, datas[i][2]};
			double[] bottomNW = new double[] {datas[i][0] - widths[i][0] / 2, datas[i][1] - widths[i][1] / 2, 0};
			double[] bottomNE = new double[] {datas[i][0] + widths[i][0] / 2, datas[i][1] - widths[i][1] / 2, 0};
			double[] bottomSW = new double[] {datas[i][0] - widths[i][0] / 2, datas[i][1] + widths[i][1] / 2, 0};
			double[] bottomSE = new double[] {datas[i][0] + widths[i][0] / 2, datas[i][1] + widths[i][1] / 2, 0};

			drawLine(topNW, topNE, comp2D);
			drawLine(topNE, topSE, comp2D);
			drawLine(topSE, topSW, comp2D);
			drawLine(topSW, topNW, comp2D);

			drawLine(bottomNW, bottomNE, comp2D);
			drawLine(bottomNE, bottomSE, comp2D);
			drawLine(bottomSE, bottomSW, comp2D);
			drawLine(bottomSW, bottomNW, comp2D);

			drawLine(bottomNW, topNW, comp2D);
			drawLine(bottomNE, topNE, comp2D);
			drawLine(bottomSE, topSE, comp2D);
			drawLine(bottomSW, topSW, comp2D);
		}
	}

	public void note(Graphics comp) {
		plot(comp, Color.black);
	}

}