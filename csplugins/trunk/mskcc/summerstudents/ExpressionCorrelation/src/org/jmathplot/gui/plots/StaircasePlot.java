package org.jmathplot.gui.plots;

import java.awt.*;
import org.jmathplot.gui.plotObjects.*;

public class StaircasePlot
	extends Plot {

	public boolean link = true;

	public StaircasePlot(double[][] XY, Color c, String n, Base b) {
		super(XY, c, n, b);
	}

	public void plot(Graphics comp, Color c) {
		Graphics2D comp2D = (Graphics2D) comp;
		comp2D.setColor(c);
		for (int i = 0; i < datas.length - 1; i++) {
			double[] begin = datas[i];
			double[] end = datas[i+1];
			end[end.length - 1] = datas[i][ end.length - 1];
			drawLine(begin, end, comp2D);
		}

		if (link) {
			for (int i = 1; i < datas.length - 1; i++) {
			double[] begin = datas[i];
			double[] end = datas[i+1];
				begin[begin.length - 1] = datas[i - 1][ begin.length - 1];
				drawLine(begin, end, comp2D);
			}
		}
	}

	public void note(Graphics comp) {
		plot(comp, Color.black);
	}

	public static void main(String[] args) {
//		new FrameView(new Plot2DPanel(DoubleArray.random(10, 2).sort(0), "Staircase Plot 2D", PlotPanel.STAIRCASE));
//		new FrameView(new Plot3DPanel(DoubleArray.random(10, 3).sort(0), "Staircase Plot 3D", PlotPanel.STAIRCASE));
	}

}