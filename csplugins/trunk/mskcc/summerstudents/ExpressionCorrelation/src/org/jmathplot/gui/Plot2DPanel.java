package org.jmathplot.gui;

import java.awt.*;
import org.jmathplot.gui.plotObjects.*;
import org.jmathplot.gui.plots.*;
import org.jmathplot.util.*;

public class Plot2DPanel
	extends PlotPanel {

	public Plot2DPanel() {
		super();
	}

	public Plot2DPanel(double[] min, double[] max, int[] axesScales, String[] axesLabels) {
		super(min, max, axesScales, axesLabels);
	}

	public Plot2DPanel(double[][] xy, String name, String type) {
		super(DoubleArray.getColumns(DoubleArray.min(xy),0,1), DoubleArray.getColumns(DoubleArray.max(xy),0,1));
		addPlot(xy, name, type);
	}

	/////////////////////////////////////////////
	//////// base and grid initialisation ///////
	/////////////////////////////////////////////

	public void initBasenGrid(double[] min, double[] max, int[] axesScales, String[] axesLabels) {
		base = new Base2D(min, max, panelSize, axesScales, base.DEFAULT_BORDER);
		grid = new Grid(base, axesLabels);
	}

	public void initBasenGrid(double[] min, double[] max) {
		initBasenGrid(min, max, new int[] {
			Base.LINEAR, Base.LINEAR}
			, new String[] {
			"X", "Y"});
	}

	public void initBasenGrid() {
		initBasenGrid(new double[] {
			0, 0}
			, new double[] {
			1, 1});
	}

	public void setAxesLabels(String Xlabel, String Ylabel) {
		setAxesLabels(new String[] {
			Xlabel, Ylabel});
	}

	public void addPlot(double[][] XY, String name, String type, Color c) {
		Plot newPlot = null;
		if (type.equals("SCATTER")) {
			DoubleArray.checkColumnDimension(XY, 2);
			newPlot = new ScatterPlot(XY, c, name, base);
		} else if (type.equals("LINE")) {
			DoubleArray.checkColumnDimension(XY, 2);
			newPlot = new LinePlot(XY, c, name, base);
		} else if (type.equals("BAR")) {
			DoubleArray.checkColumnDimension(XY, 2);
			newPlot = new BarPlot(XY, c, name, base);
		} else if (type.equals("HISTOGRAM")) {
			DoubleArray.checkColumnDimension(XY, 3);
			newPlot = new HistogramPlot2D( DoubleArray.getColumns(XY,0, 1), DoubleArray.getColumns(XY,2,2),
				  c, name, base);
		} else if (type.equals("BOX")) {
			DoubleArray.checkColumnDimension(XY, 6);
			newPlot = new BoxPlot2D( DoubleArray.getColumns(XY,0, 1), DoubleArray.getColumns(XY,2, 5),
				  c, name, base);
		} else if (type.equals("STAIRCASE")) {
			DoubleArray.checkColumnDimension(XY, 2);
			newPlot = new StaircasePlot(XY, c, name, base);
		} else {
			throw new IllegalArgumentException("Plot type is unknown : " + type);
		}
		addPlot(newPlot);
	}

	public static void main(String[] args) {
		Plot2DPanel p2d = new Plot2DPanel(DoubleArray.random(10, 2), "plot 1", PlotPanel.SCATTER);
		new FrameView(p2d);
		p2d.addPlot(DoubleArray.random(10, 2), "plot 2", PlotPanel.SCATTER);
	}

}