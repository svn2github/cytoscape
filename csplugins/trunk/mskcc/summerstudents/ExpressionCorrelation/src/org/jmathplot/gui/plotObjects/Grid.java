package org.jmathplot.gui.plotObjects;

/**
 * <p>Copyright : BSD License</p>
 * @author Yann RICHET
 * @version 3.0
 */

import java.awt.*;

public class Grid
	implements Plotable, BaseScalesDependant {

	public static double borderCoeff = 0.1;

	protected Base base;
	protected Axe[] axes;

	public Grid(Base b, String[] as) {
		base = b;
		if (as.length != base.dimension) {
			throw new IllegalArgumentException(
				"String array of axes names must have " + base.dimension +
				" elements.");
		}
		axes = new Axe[base.dimension];
		for (int i = 0; i < base.dimension; i++) {
			axes[i] = new Axe(base, as[i], i);
		}
		updateBase();
	}

	public void setLegend(String[] as) {
		if (as.length != base.dimension) {
			throw new IllegalArgumentException(
				"String array of axes names must have " + base.dimension +
				" elements.");
		}
		for (int i = 0; i < axes.length; i++) {
			axes[i].setName(as[i]);
		}
		updateBase();
	}

	public void setLegend(int i, String as) {
		axes[i].setName(as);
		updateBase();
	}

	public String[] getLegend() {
		String[] array = new String[axes.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = axes[i].getName();
		}
		return array;
	}

	public String getLegend(int i) {
		return axes[i].getName();
	}

	public void setBase(Base b) {
		base = b;
		updateBase();
	}

	public void plot(Graphics comp) {
		for (int i = 0; i < axes.length; i++) {
			axes[i].plot(comp);
		}
	}

	public Axe getAxe(int i) {
		return axes[i];
	}

	/*public void update() {
	 for (int i = 0; i < axes.length; i++) {
	  axes[i].update();
	 }
	  }*/

	public void updateBase() {
		for (int i = 0; i < axes.length; i++) {
			axes[i].updateBase();
		}

	}

}