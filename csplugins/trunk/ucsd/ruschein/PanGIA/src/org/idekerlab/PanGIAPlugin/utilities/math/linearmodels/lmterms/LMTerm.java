package org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms;

public abstract class LMTerm
{
	public abstract double evaluate(float[][] data, int row);
	public abstract double evaluate(double[][] data, int row);
	public abstract double evaluate(byte[][] data, int row);
}
