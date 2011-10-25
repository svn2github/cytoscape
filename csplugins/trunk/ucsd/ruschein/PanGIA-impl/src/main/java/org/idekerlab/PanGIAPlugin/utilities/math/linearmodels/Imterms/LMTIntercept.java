package org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms;

public final class LMTIntercept extends LMTerm
{
	public double evaluate(double[][] data, int row)
	{
		return 1;
	}
	
	public double evaluate(float[][] data, int row)
	{
		return 1;
	}
	
	public double evaluate(byte[][] data, int row)
	{
		return 1;
	}
	
	public String toString()
	{
		return "1";
	}
	
	public int hashCode()
	{
		return -1;
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof LMTIntercept) return true;
		else return false;
	}
}
