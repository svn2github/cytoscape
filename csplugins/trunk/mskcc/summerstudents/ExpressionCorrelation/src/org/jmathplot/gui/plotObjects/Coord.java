package org.jmathplot.gui.plotObjects;

import org.jmathplot.io.*;

public abstract class Coord implements CommandLinePrintable {

	protected int[] screenCoord;
	protected double[] plotCoord;

	public int[] getScreenCoordCopy() {
		return new int[] {screenCoord[0],screenCoord[1]};
	}

	public void setScreenCoord(int[] newScreenCoord) {
		screenCoord[0] = newScreenCoord[0];
		screenCoord[1] = newScreenCoord[1];
	}

	public double[] getPlotCoordCopy() {
		double[] pC = new double[plotCoord.length];
		for (int i = 0; i < plotCoord.length; i++) {
			pC[i] = plotCoord[i];
		}
		return pC;
	}

	public void setPlotCoord(double[] newPlotCoord) {
		for (int i = 0; i < plotCoord.length; i++) {
			plotCoord[i] = newPlotCoord[i];
		}
	}

	public String toString() {
		StringBuffer s = new StringBuffer("(");
		for (int i = 0; i < plotCoord.length-1;i++) {
			s.append(this.getPlotCoordCopy()[i]).append(",");
		}
		s.append(this.getPlotCoordCopy()[plotCoord.length-1]).append(")");
		return(s.toString());
	}

	public void toCommandLine(String title) {
		StringBuffer s = new StringBuffer(title).append(" : (");
		for (int i = 0; i < plotCoord.length-1;i++) {
			s.append(this.getPlotCoordCopy()[i]).append(",");
		}
		s.append(this.getPlotCoordCopy()[plotCoord.length-1]).append(") -> ["+getScreenCoordCopy()[0]+","+getScreenCoordCopy()[1]+"]");
		System.out.println(s.toString());
	}

}