package org.jmathplot.gui.plots;

import java.awt.*;
import org.jmathplot.gui.*;
import org.jmathplot.gui.plotObjects.*;
import org.jmathplot.util.*;

public class BarPlot
	extends Plot {

	public BarPlot(double[][] XY, Color c, String n, Base b) {
		super(XY, c, n, b);
	}

	public void plot(Graphics comp, Color c) {
		RelativeCoord[] coords = getCoords();
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.setColor(c);
		for (int i = 0; i < datas.length; i++) {
			drawLine(coords[i], coords[i].projection(datas[0].length - 1),
				comp2D);
			drawDot(datas[i], comp2D);
		}
	}

	public void note(Graphics comp) {
		plot(comp, Color.black);
	}

	public static void main(String[] args) {
		new FrameView(new Plot2DPanel(DoubleArray.random(10, 2), "Bar Plot 2D",
			PlotPanel.BAR));
		new FrameView(new Plot3DPanel(DoubleArray.random(10, 3), "Bar Plot 3D",
			PlotPanel.BAR));
	}

}