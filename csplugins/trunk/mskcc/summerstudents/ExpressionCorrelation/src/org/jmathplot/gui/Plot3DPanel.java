package org.jmathplot.gui;


import java.awt.*;
import java.awt.event.*;
import org.jmathplot.gui.plotObjects.*;
import org.jmathplot.gui.plots.*;
import org.jmathplot.util.*;

public class Plot3DPanel
	extends PlotPanel {

	public final static int ROTATION = 2;

	public Plot3DPanel() {
		super();
	}

	public Plot3DPanel(double[] min, double[] max, int[] axesScales, String[] axesLabels) {
		super(min, max, axesScales, axesLabels);
	}

	public Plot3DPanel(double[][] xy, String name, String type) {
		super(DoubleArray.getColumns(DoubleArray.min(xy),0,2), DoubleArray.getColumns(DoubleArray.max(xy),0,2));
		addPlot(xy, name, type);
	}

	/////////////////////////////////////////////
	//////// base and grid initialisation ///////
	/////////////////////////////////////////////

	public void initBasenGrid(double[] min, double[] max, int[] axesScales, String[] axesLabels) {
		base = new Base3D(min, max, panelSize, axesScales, base.DEFAULT_BORDER);
		grid = new Grid(base, axesLabels);
	}

	public void initBasenGrid(double[] min, double[] max) {
		initBasenGrid(min, max, new int[] {
			Base.LINEAR, Base.LINEAR, Base.LINEAR}
			, new String[] {
			"X", "Y", "Z"});
	}

	public void initBasenGrid() {
		initBasenGrid(new double[] {
			0, 0, 0}
			, new double[] {
			1, 1, 1});
	}

	public void setAxesLabels(String Xlabel, String Ylabel, String Zlabel) {
		setAxesLabels(new String[] {
			Xlabel, Ylabel, Zlabel});
	}

	public void addPlot(double[][] XY, String name, String type, Color c) {
		Plot newPlot = null;
		if (type.equals("SCATTER")) {
			DoubleArray.checkColumnDimension(XY, 3);
			newPlot = new ScatterPlot(XY, c, name, base);
		} else if (type.equals("LINE")) {
			DoubleArray.checkColumnDimension(XY, 3);
			newPlot = new LinePlot(XY, c, name, base);
		} else if (type.equals("BAR")) {
			DoubleArray.checkColumnDimension(XY, 3);
			newPlot = new BarPlot(XY, c, name, base);
		} else if (type.equals("HISTOGRAM")) {
			DoubleArray.checkColumnDimension(XY, 5);
			newPlot = new HistogramPlot3D(DoubleArray.getColumns(XY,0, 2), DoubleArray.getColumns(XY,3, 4), c,
				  name, base);
		} else if (type.equals("BOX")) {
			DoubleArray.checkColumnDimension(XY, 9);
			newPlot = new BoxPlot3D(DoubleArray.getColumns(XY,0, 2), DoubleArray.getColumns(XY,3, 8), c, name,
				  base);
		} else if (type.equals("STAIRCASE")) {
			DoubleArray.checkColumnDimension(XY, 3);
			newPlot = new StaircasePlot(XY, c, name, base);
		} else {
			throw new IllegalArgumentException("Plot type is unknown : " + type);
		}
		addPlot(newPlot);
	}

	public void mouseDragged(MouseEvent e) {
		mouseCurent[0] = e.getX();
		mouseCurent[1] = e.getY();
		e.consume();
		int[] t;
		switch (ActionMode) {
			case TRANSLATION:
				t = new int[] {mouseCurent[0] - mouseClick[0], mouseCurent[1] - mouseClick[1]};
				base.translate(t);
				mouseClick[0] = mouseCurent[0];
				mouseClick[1] = mouseCurent[1];
				break;
			case ROTATION:
				t = new int[] {mouseCurent[0] - mouseClick[0], mouseCurent[1] - mouseClick[1]};
				( (Base3D) base).rotate(t, panelSize, base.getBorder());
				mouseClick[0] = mouseCurent[0];
				mouseClick[1] = mouseCurent[1];
				break;
		}
		repaint();
	}
}