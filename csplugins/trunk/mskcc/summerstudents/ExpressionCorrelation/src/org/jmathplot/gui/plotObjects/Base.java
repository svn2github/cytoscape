package org.jmathplot.gui.plotObjects;

import org.jmathplot.io.*;

public abstract class Base
	implements CommandLinePrintable {

	public final static int LINEAR = 0;
	public final static int LOG = 1;

	public static double DEFAULT_BORDER = 0.15;

	protected Coord[] baseCoords;
	protected double[] precisionUnit;

	protected double[] Xmin;
	protected double[] Xmax;

	protected int[] panelSize;
	protected int dimension;
	protected double borderCoeff;

	protected int[] axesScales;

	public Base(Coord[] b) {
		baseCoords = b;
	}

	public Base(double[] Xmi, double[] Xma, int[] dim, int[] scales, double bC) {
		dimension = Xmi.length;
		axesScales = scales;
		panelSize = dim;
		borderCoeff = bC;
		//setPrecisionUnit(Xmi,Xma);
		initBounds(Xmi.length);
		setnRoundBounds(Xmi, Xma);
		setBaseCoords();
	}

	/////////////////////////////////////////////
	//////// set/get variables //////////////////
	/////////////////////////////////////////////

	public void setPanelSize(int[] dim) {
		panelSize = dim;
	}

	public void setAxesScales(int[] scales) {
		axesScales = scales;
		setnRoundBounds(Xmin, Xmax);
		setBaseCoords();
	}

	public void setAxesScales(int i, int scale) {
		axesScales[i] = scale;
		setnRoundBounds(Xmin, Xmax);
		setBaseCoords();
	}

	/*	protected void setPrecisionUnit(double[] Xmi,double[] Xma) {
	  precisionUnit = new double[Xmi.length];
	  for (int i = 0; i < precisionUnit.length; i++) {
	   setPrecisionUnit(Xmi[i],Xma[i], i);
	  }
	 }*/

	private void setPrecisionUnit(int i, double Xmi, double Xma) {
		if (Xma - Xmi > 0) {
			precisionUnit[i] = Math.pow(10, Math.rint(Math.log(Xma - Xmi) / Math.log(10)));
		} else {
			precisionUnit[i] = 1;
		}
	}

	public double getBorder() {
		return borderCoeff;
	}

	public Coord[] getCoords() {
		return baseCoords;
	}

	public int getDimension() {
		return dimension;
	}

	public int[] getAxesScales() {
		return axesScales;
	}

	public int getAxeScale(int i) {
		return axesScales[i];
	}

	public double[] getMinBounds() {
		return Xmin;
	}

	public double[] getMaxBounds() {
		return Xmax;
	}

	public double[] getPrecisionUnit() {
		return precisionUnit;
	}

	/////////////////////////////////////////////
	//////// bounds methods /////////////////////
	/////////////////////////////////////////////

	private void initBounds(int i) {
		Xmin = new double[i];
		Xmax = new double[i];
	}

	private void setBounds(int i, double Xmi, double Xma) {
		if ( (Xmi < 0) && (axesScales[i] == Base.LOG)) {
			throw new IllegalArgumentException("Error while bounding dimension " + (i + 1) + " : bounds [" +
				Xmi + "," +
				Xma + "] are incompatible with Logarithm scale.");
		}
		if (Xmi == Xma) {
			Xmi = Xma-1;
		}
		if (Xmi > Xma) {
			throw new IllegalArgumentException("Error while bounding dimension " + (i + 1) + " : min " +
				Xmi +
				" must be < to max " + Xma);
		}
		Xmin[i] = Xmi;
		Xmax[i] = Xma;
	}

	private void setBounds(double[] Xmi, double[] Xma) {
		for (int i = 0; i < Xmi.length; i++) {
			setBounds(i, Xmi[i], Xma[i]);
		}
	}

	public void setFixedBounds(int i, double Xmi, double Xma) {
		setPrecisionUnit(i, Xmi, Xma);
		setBounds(i, Xmi, Xma);
	}

	public void setFixedBounds(double[] Xmi, double[] Xma) {
		for (int i = 0; i < Xmi.length; i++) {
			setFixedBounds(i, Xmi[i], Xma[i]);
		}
	}

	public void setnRoundBounds(int i, double Xmi, double Xma) {
		setPrecisionUnit(i, Xmi, Xma);
		if (axesScales[i] == Base.LOG) {
			setBounds(i, Math.pow(10, Math.floor(Math.log(Xmi) / Math.log(10))), Math.pow(10,
				Math.ceil(Math.log(Xma) / Math.log(10))));
		} else if (axesScales[i] == Base.LINEAR) {
			setBounds(i, precisionUnit[i] * (Math.floor(Xmi / precisionUnit[i])),
				precisionUnit[i] * (Math.ceil(Xma / precisionUnit[i])));
		}
	}

	public void setnRoundBounds(double[] Xmi, double[] Xma) {
		precisionUnit = new double[Xmi.length];
		for (int i = 0; i < Xmi.length; i++) {
			setnRoundBounds(i, Xmi[i], Xma[i]);
		}
	}

	public void includeInBounds(int dim, double XY) {
		double[] Xmi = new double[Xmin.length];
		for (int i = 0; i < Xmin.length; i++) {
			if (i == dim) {
				Xmi[i] = Math.min(XY, Xmin[i]);
			} else {
				Xmi[i] = Xmin[i];
			}
		}
		double[] Xma = new double[Xmax.length];
		for (int i = 0; i < Xmax.length; i++) {
			if (i == dim) {
				Xma[i] = Math.max(XY, Xmax[i]);
			} else {
				Xma[i] = Xmax[i];
			}
		}
		setnRoundBounds(Xmi, Xma);
		setBaseCoords();
	}

	public void includeInBounds(double[] XY) {
		double[] Xmi = new double[Xmin.length];
		for (int i = 0; i < Xmin.length; i++) {
			Xmi[i] = Math.min(XY[i], Xmin[i]);
		}
		double[] Xma = new double[Xmax.length];
		for (int i = 0; i < Xmax.length; i++) {
			Xma[i] = Math.max(XY[i], Xmax[i]);
		}
		setnRoundBounds(Xmi, Xma);
		setBaseCoords();
	}

	/////////////////////////////////////////////
	//////// move methods ///////////////////////
	/////////////////////////////////////////////

	public void translate(int[] screenTranslation) {
		for (int i = 0; i < baseCoords.length; i++) {
			baseCoords[i].setScreenCoord(new int[] {
				baseCoords[i].getScreenCoordCopy()[0] + screenTranslation[0],
				baseCoords[i].getScreenCoordCopy()[1] + screenTranslation[1]});
		}
	}

	public void dilate(int[] screenOrigin, double[] screenRatio) {
		for (int i = 0; i < baseCoords.length; i++) {
			baseCoords[i].setScreenCoord(new int[] {
				(int) ( (baseCoords[i].getScreenCoordCopy()[0] - screenOrigin[0]) / screenRatio[0]),
				(int) ( (baseCoords[i].getScreenCoordCopy()[1] - screenOrigin[1]) / screenRatio[1])});
		}
	}

	/////////////////////////////////////////////
	//////// update methods /////////////////////
	/////////////////////////////////////////////

	public void updateScreenCoord() {
		double[] ratio;
		int[] sC;
		for (int i = 0; i < baseCoords.length; i++) {
			ratio = baseCoordsScreenProjectionRatio(baseCoords[i].getPlotCoordCopy());
			sC = new int[] { (int) (panelSize[0] * (borderCoeff + (1 - 2 * borderCoeff) * ratio[0])),
			     (int) (panelSize[1] - panelSize[1] * (borderCoeff + (1 - 2 * borderCoeff) * ratio[1]))};
			baseCoords[i] = new AbsoluteCoord(baseCoords[i].getPlotCoordCopy(), sC);
		}
	}

	public void setBaseCoords() {
		baseCoords = new AbsoluteCoord[Xmin.length + 1];

		for (int i = 0; i < Xmin.length + 1; i++) {
			double[] pC = (double[]) (Xmin.clone());
			if (i > 0) {
				pC[i - 1] = Xmax[i - 1];
			}
			double[] ratio = baseCoordsScreenProjectionRatio(pC);
			int[] sC = new int[] { (int) (panelSize[0] * (borderCoeff + (1 - 2 * borderCoeff) * ratio[0])),
				   (int) (panelSize[1] - panelSize[1] * (borderCoeff + (1 - 2 * borderCoeff) * ratio[1]))};
			baseCoords[i] = new AbsoluteCoord(pC, sC);
		}
	}

	/////////////////////////////////////////////
	//////// projection method //////////////////
	/////////////////////////////////////////////

	public int[] screenProjection(double[] pC) {
		double[] sC = new double[2];
		sC[0] = baseCoords[0].getScreenCoordCopy()[0];
		sC[1] = baseCoords[0].getScreenCoordCopy()[1];
		for (int i = 0; i < baseCoords[0].getPlotCoordCopy().length; i++) {
			if (axesScales[i] == LOG) {
				sC[0] += ( (Math.log(pC[i]) -
					Math.log(baseCoords[0].getPlotCoordCopy()[i])) / (Math.log(baseCoords[i +
					1].getPlotCoordCopy()[i]) -
					Math.log(baseCoords[0].getPlotCoordCopy()[i]))) * (baseCoords[i +
					1].getScreenCoordCopy()[0] - baseCoords[0].getScreenCoordCopy()[0]);
				sC[1] += ( (Math.log(pC[i]) -
					Math.log(baseCoords[0].getPlotCoordCopy()[i])) / (Math.log(baseCoords[i +
					1].getPlotCoordCopy()[i]) -
					Math.log(baseCoords[0].getPlotCoordCopy()[i]))) * (baseCoords[i +
					1].getScreenCoordCopy()[1] - baseCoords[0].getScreenCoordCopy()[1]);
			} else if (axesScales[i] == LINEAR) {
				sC[0] += ( (pC[i] - baseCoords[0].getPlotCoordCopy()[i]) / (baseCoords[i +
					1].getPlotCoordCopy()[i] -
					baseCoords[0].getPlotCoordCopy()[i])) * (baseCoords[i +
					1].getScreenCoordCopy()[0] -
					baseCoords[0].getScreenCoordCopy()[0]);
				sC[1] += ( (pC[i] - baseCoords[0].getPlotCoordCopy()[i]) / (baseCoords[i +
					1].getPlotCoordCopy()[i] -
					baseCoords[0].getPlotCoordCopy()[i])) * (baseCoords[i +
					1].getScreenCoordCopy()[1] -
					baseCoords[0].getScreenCoordCopy()[1]);
			}
		}
		return new int[] {
			(int) sC[0], (int) sC[1]};
	}

	protected abstract double[] baseCoordsScreenProjectionRatio(double[] xyz);

	/////////////////////////////////////////////
	//////// other public methods ///////////////
	/////////////////////////////////////////////

	public RelativeCoord getGravCenter() {
		double[] g = baseCoords[0].getPlotCoordCopy();
		for (int i = 0; i < baseCoords.length - 1; i++) {
			g[i] = g[i] + baseCoords[i].getPlotCoordCopy()[i] / 2;
		}
		return new RelativeCoord(g, this);
	}

	public boolean authorizedLogScale(int i) {
		System.out.println("Xmin[" + i + "] = " + Xmin[i]);
		if (Xmin[i] > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void toCommandLine(String title) {
		System.out.println(title + " : ");
		for (int i = 0; i < baseCoords.length; i++) {
			baseCoords[i].toCommandLine("point " + i);
		}
	}
}