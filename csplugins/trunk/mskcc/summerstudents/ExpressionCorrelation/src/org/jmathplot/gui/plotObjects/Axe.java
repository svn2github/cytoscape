package org.jmathplot.gui.plotObjects;

import java.awt.*;

/**


 * <p>Copyright : BSD License</p>

 * @author Yann RICHET
 * @version 1.0
 */

public class Axe
	implements Plotable, BaseScalesDependant {

	public int linear_slicing;
	public double label_offset;

	protected int index;
	protected Base base;

	protected double[] linesSlicing;
	protected double[] labelsSlicing;

	protected String name;

	protected Coord origin;
	protected Coord end;

	protected Line darkLine;
	protected Line[][] lightLines;
	protected Label darkLabel;
	protected Label[] lightLabels;

	public Axe(Base b, String aS, int i) {
		base = b;
		name = aS;
		index = i;
		linear_slicing = 10;
		label_offset = 0.05;
		updateBase();
	}

	public void setName(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	private void setOriginEnd() {
		origin = new RelativeCoord(base.getCoords()[0].getPlotCoordCopy(), base);
		end = new RelativeCoord(base.getCoords()[index + 1].getPlotCoordCopy(), base);
	}

	private void setSlicing() {

		// slicing initialisation
		if (base.getAxeScale(index) == Base.LOG) {
			int numPow10 = (int) Math.rint( (Math.log(base.getMaxBounds()[index] /
				       base.getMinBounds()[index]) /
				       Math.log(10)));
			double minPow10 = Math.rint(Math.log(base.getMinBounds()[index]) / Math.log(10));

			linesSlicing = new double[numPow10 * 9 + 1];
			labelsSlicing = new double[numPow10 + 1];

			//set slicing for labels : 0.1 , 1 , 10 , 100 , 1000
			for (int i = 0; i < numPow10 + 1; i++) {
				labelsSlicing[i] = Math.pow(10, i + minPow10);
			}
			//set slicing for labels : 0.1 , 0.2 , ... , 0.9 , 1 , 2 , ... , 9 , 10 , 20 , ...
			for (int i = 0; i < numPow10; i++) {
				for (int j = 0; j < 10; j++) {
					linesSlicing[i * 9 + j] = Math.pow(10, i + minPow10) * (j + 1);
				}
			}
		} else if (base.getAxeScale(index) == Base.LINEAR) {
			linesSlicing = new double[linear_slicing + 1];
			labelsSlicing = new double[linear_slicing + 1];

			double min = base.getMinBounds()[index];

			double pitch = (base.getCoords()[index + 1].getPlotCoordCopy()[index] -
				       base.getCoords()[0].getPlotCoordCopy()[index]) / (linear_slicing);

			for (int i = 0; i < linear_slicing + 1; i++) {
				//lines and labels slicing are the same
				linesSlicing[i] = min + i * pitch;
				labelsSlicing[i] = min + i * pitch;
			}
		}

//    System.out.println("\nAxe " + index + " lines :");
//    for (int i = 0; i < linesSlicing.length; i++) {
//      System.out.print(" " + linesSlicing[i]);
//    }
//    System.out.println("\nAxe " + index + " labels :");
//    for (int i = 0; i < labelsSlicing.length; i++) {
//      System.out.print(" " + labelsSlicing[i]);
//    }
//    System.out.println();

	}

	public void plot(Graphics comp) {
		for (int i = 0; i < lightLines.length; i++) {
			//j = 0 overwrites a darkLine of another Axe : so I begin to j = 1.
			for (int j = 1; j < lightLines[i].length; j++) {
				lightLines[i][j].plot(comp);
			}
		}
		for (int i = 0; i < lightLabels.length; i++) {
			lightLabels[i].plot(comp);
		}
		darkLine.plot(comp);
		darkLabel.plot(comp);
	}

	private void setLightLabels() {

		//offset of lightLabels
		double[] labelOffset = new double[base.dimension];
		for (int i = 0; i < base.getDimension(); i++) {
			if (i != index) {
				labelOffset[i] = -label_offset;
			}
		}

		//local variables initialisation
		int decimal = 0;
		String lab;

		lightLabels = new Label[labelsSlicing.length];

		for (int i = 0; i < lightLabels.length; i++) {

			RelativeCoord labelCoord = BaseLabel.buildRelativeCoord(labelOffset, base);
			labelCoord.setPlotCoord(labelsSlicing[i], index);

			if (base.getAxeScale(index) == Base.LINEAR) {
				decimal = - (int) (Math.log(base.getPrecisionUnit()[index] / 100) / Math.log(10));
			} else if (base.getAxeScale(index) == Base.LOG) {
				decimal = - (int) (Math.floor(Math.log(labelsSlicing[i]) / Math.log(10)));
			}

			lab = new String(Label.approx(labelCoord.getPlotCoordCopy()[index], decimal) + "");

			lightLabels[i] = new Label(lab, labelCoord, Color.darkGray);
		}

	}

	private void setLightLines() {
		lightLines = new Line[base.getDimension() - 1][linesSlicing.length];

		//local variables initialisation
		RelativeCoord origin_tmp, end_tmp;
		int i2 = 0;

		for (int i = 0; i < base.getDimension() - 1; i++) {
			if (i2 == index) {
				i2++;
			}
			for (int j = 0; j < lightLines[i].length; j++) {
				double[] originCoord_tmp = origin.getPlotCoordCopy();
				double[] endCoord_tmp = base.getCoords()[i2 + 1].getPlotCoordCopy();
				originCoord_tmp[index] = linesSlicing[j];
				endCoord_tmp[index] = linesSlicing[j];

				origin_tmp = new RelativeCoord(originCoord_tmp, base);
				end_tmp = new RelativeCoord(endCoord_tmp, base);

				lightLines[i][j] = new Line(origin_tmp, end_tmp, Color.green);
			}
			i2++;
		}
	}

	private void setDarkLines() {
		darkLine = new Line(origin, end, Color.black);
	}

	private void setDarkLabels() {
		darkLabel = new Label(name, end, Color.black);
		darkLabel.setCorner(0, 0);
	}

	public void updateBase() {
		setOriginEnd();
		setSlicing();

		setDarkLines();
		setDarkLabels();
		setLightLines();
		setLightLabels();
	}

}