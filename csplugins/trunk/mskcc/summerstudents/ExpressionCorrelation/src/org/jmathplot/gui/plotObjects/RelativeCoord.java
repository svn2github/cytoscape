package org.jmathplot.gui.plotObjects;

import java.awt.*;

public class RelativeCoord extends Coord implements Noteable {

	protected Base base;
	protected boolean isNoteable = true;

	public RelativeCoord(double[] pC,Base b) {
		base = b;
		plotCoord = pC;
		updateScreenCoord();
	}

	public static RelativeCoord barycenter(RelativeCoord A,double p1,Coord B,double p2) {
		double[] pC = new double[A.getPlotCoordCopy().length];
		for (int i = 0; i < A.getPlotCoordCopy().length; i++) {
			pC[i] = (A.getPlotCoordCopy()[i]*p1 + B.getPlotCoordCopy()[i]*p2)/(p1+p2);
		}
		return new RelativeCoord(pC,A.getBase());
	}

	/*public static RelativeCoord vector(RelativeCoord A,Coord B) {
		double[] pC = new double[A.getPlotCoordCopy().length];
		for (int i = 0; i < A.getPlotCoordCopy().length; i++) {
			pC[i] = B.getPlotCoordCopy()[i] - A.getPlotCoordCopy()[i];
		}
		return new RelativeCoord(pC,A.getBase());
	}*/

	public int[] getScreenCoordCopy() {
		updateScreenCoord();
		return super.getScreenCoordCopy();
	}

	public Base getBase() {
		return base;
	}

	public void setBase(Base b) {
		base = b;
	}

	public void setPlotCoord(double[] pC) {
		plotCoord = pC;
		updateScreenCoord();
	}

	public void setPlotCoord(double pCi, int i) {
		plotCoord[i] = pCi;
		updateScreenCoord();
	}

	public RelativeCoord projection(int dimension) {
		double[] pC = this.getPlotCoordCopy();
		pC[dimension] = base.getCoords()[0].getPlotCoordCopy()[dimension];
		return new RelativeCoord(pC,this.getBase());
	}

	public RelativeCoord addVector(double[] v) {
		double[] end = this.getPlotCoordCopy();
		for (int i = 0; i < end.length; i++) {
			end[i] += v[i];
		}
		return new RelativeCoord(end,base);
	}

	public void addVectorEquals(double[] v) {
		for (int i = 0; i < v.length; i++) {
			plotCoord[i] += v[i];
		}
		updateScreenCoord();
	}

	public void addVectorEquals(double vi, int i) {
		plotCoord[i] += vi;
		updateScreenCoord();
	}


	public String toString() {
		StringBuffer s = new StringBuffer("(");
		for (int i = 0; i < plotCoord.length-1;i++) {
			String coordStr = new String(""+approx(this.getPlotCoordCopy()[i],getPower(base.getPrecisionUnit()[i])));//(base.getScales()[i] == Base.LOG) ? (new String("10^"+approx(this.getPlotCoordCopy()[i],getPower(base.getPrecisionUnit()[i])))) : (new String(""+approx(this.getPlotCoordCopy()[i],getPower(base.getPrecisionUnit()[i]))));
			s.append(coordStr).append(",");
		}
		String coordStr = new String(""+approx(this.getPlotCoordCopy()[plotCoord.length-1],getPower(base.getPrecisionUnit()[plotCoord.length-1])));//(base.getScales()[plotCoord.length-1] == Base.LOG) ? (new String("10^"+approx(this.getPlotCoordCopy()[plotCoord.length-1],getPower(base.getPrecisionUnit()[plotCoord.length-1])))) : (new String(""+approx(this.getPlotCoordCopy()[plotCoord.length-1],getPower(base.getPrecisionUnit()[plotCoord.length-1]))));
		s.append(coordStr).append(")");
		return(s.toString());
	}
	public void setNotable(boolean b) {
		isNoteable = b;
	}

	public boolean tryNote(int[] sC) {
		if ((sC[0]>screenCoord[0]-5)&&(sC[0]<screenCoord[0]+5)&&(sC[1]>screenCoord[1]-5)&&(sC[1]<screenCoord[1]+5))
			return true;
		else return false;
	}

	public boolean tryNote(int[] sC,Graphics comp) {
		if (isNoteable&&tryNote(sC)) {
			note(comp);
			return true;
		}
		return false;
	}

	public void note(Graphics comp) {
		Graphics2D comp2D = (Graphics2D)comp;
		comp2D.setColor(Color.black);
		comp2D.drawString(toString(),screenCoord[0],screenCoord[1]);
		for (int i = 0; i < base.getCoords().length-1; i++) {
			Coord p = projection(i);
			comp2D.drawLine(this.getScreenCoordCopy()[0],this.getScreenCoordCopy()[1],p.getScreenCoordCopy()[0],p.getScreenCoordCopy()[1]);
		}
	}

	private void updateScreenCoord() {
		screenCoord = base.screenProjection(plotCoord);
	}

	private static int getPower(double precisionUnit) {
		return -(int)(Math.log(precisionUnit/10000)/Math.log(10));
	}

	public static double approx(double val,int decimal) {
//		double timesEn = val*Math.pow(10,decimal);
//		if (Math.rint(timesEn) == timesEn) {
//			return val;
//		} else {
		//to limit precision loss, you need to separate cases where decimal<0 and >0
		//if you don't you'll find this : approx(10000.0,-4) => 10000.00000001
		if (decimal<0) {
			return Math.rint(val/Math.pow(10,-decimal))*Math.pow(10,-decimal);
		} else  {
			return Math.rint(val*Math.pow(10,decimal))/Math.pow(10,decimal);
		}
//		}
	}


	public void checkSameRelativesBases(RelativeCoord B) {
		if (base!=B.base)
			throw new IllegalArgumentException("Using two Coordinates, their bases must be the same");
	}


}