package org.jmathplot.gui.plots;

import java.awt.*;
import org.jmathplot.gui.*;
import org.jmathplot.gui.plotObjects.*;
import org.jmathplot.util.*;

public class LinePlot
	extends Plot {

	public LinePlot(double[][] XY, Color c, String n, Base b) {
		super(XY, c, n, b);
	}

	public void plot(Graphics comp, Color c) {
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.setColor(c);
		for (int i = 0; i < datas.length - 1; i++) {
			drawLine(datas[i], datas[i+1], comp2D);
		}
	}

	public void note(Graphics comp) {
		plot(comp, Color.black);
	}

	public static void main(String[] args) {
		new FrameView(new Plot2DPanel(DoubleArray.random(10, 2), "Line Plot 2D", PlotPanel.LINE));
		new FrameView(new Plot3DPanel(DoubleArray.random(10, 3), "Line Plot 3D", PlotPanel.LINE));
	}

}