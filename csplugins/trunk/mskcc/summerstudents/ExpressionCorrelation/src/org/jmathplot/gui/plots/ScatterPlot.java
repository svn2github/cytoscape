package org.jmathplot.gui.plots;

import java.awt.*;
import org.jmathplot.gui.*;
import org.jmathplot.gui.plotObjects.*;
import org.jmathplot.util.*;

public class ScatterPlot
	extends Plot {

	public ScatterPlot(double[][] XY, Color c, String n, Base b) {
		super(XY, c, n, b);
	}

	public void plot(Graphics comp, Color c) {
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.setColor(c);
		for (int i = 0; i < datas.length; i++) {
			drawDot(datas[i], comp2D);
		}
	}

	public void note(Graphics comp) {
		plot(comp, Color.black);
	}

	public static void main(String[] args) {
		new FrameView(new Plot2DPanel(DoubleArray.random(10, 2), "Scatter Plot 2D", PlotPanel.SCATTER));
		new FrameView(new Plot3DPanel(DoubleArray.random(10, 3), "Scatter Plot 3D", PlotPanel.SCATTER));
	}

}