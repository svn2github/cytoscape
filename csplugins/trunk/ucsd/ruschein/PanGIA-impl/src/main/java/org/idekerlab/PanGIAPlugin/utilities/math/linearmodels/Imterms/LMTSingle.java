package org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms;


public final class LMTSingle extends LMTerm
{
	final int varIndex;
	
	public LMTSingle(int varIndex)
	{
		this.varIndex = varIndex;
	}
	
	public double evaluate(double[][] data, int row)
	{
		return data[row][varIndex];
	}
	
	public double evaluate(float[][] data, int row)
	{
		return data[row][varIndex];
	}
	
	public double evaluate(byte[][] data, int row)
	{
		return data[row][varIndex];
	}
	
	public String toString()
	{
		return "x"+varIndex;
	}
	
	public int hashCode()
	{
		return varIndex;
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof LMTSingle)
			return ((LMTSingle)other).varIndex==varIndex;
		
		return false;
	}
	
	public int varIndex()
	{
		return varIndex;
	}
}
