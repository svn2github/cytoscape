package org.jmathplot.gui.plotObjects;



public class Base3D extends Base {

	protected double theta;
	protected double phi;

	public Base3D(double[] Xmi,double[] Xma,int[] dimension,int[] scales,double borderCoeff) {
		super(Xmi,Xma,dimension,scales,borderCoeff);
		theta = Math.PI/4;
		phi = Math.PI/4;
		setBaseCoords();
	}

	protected double[] baseCoordsScreenProjectionRatio(double[] xyz) {
		double factor = 1.7;
		double[] sC = new double[2];
		sC[0] = 0.5
					+ (Math.cos(theta)*((xyz[1]-(Xmax[1]+Xmin[1])/2)/(Xmax[1]-Xmin[1]))
					- Math.sin(theta)*((xyz[0]-(Xmax[0]+Xmin[0])/2)/(Xmax[0]-Xmin[0])))/factor;
		sC[1] = 0.5
					+ (Math.cos(phi)*((xyz[2]-(Xmax[2]+Xmin[2])/2)/(Xmax[2]-Xmin[2]))
					- Math.sin(phi)*Math.cos(theta)*((xyz[0]-(Xmax[0]+Xmin[0])/2)/(Xmax[0]-Xmin[0]))
					- Math.sin(phi)*Math.sin(theta)*((xyz[1]-(Xmax[1]+Xmin[1])/2)/(Xmax[1]-Xmin[1])))/factor;
		//System.out.println("Theta = " + theta + " Phi = " + phi);
		//System.out.println("(" + xyz[0] +"," + xyz[1] +"," + xyz[2] + ") -> (" + sC[0] + "," + sC[1] + ")");
		return sC;
	}

	public void rotate(int[] screenTranslation,int[] dimension,double borderCoeff) {
		theta = theta - ((double)screenTranslation[0])/100;
		phi = phi + ((double)screenTranslation[1])/100;
		setBaseCoords();
	}

}