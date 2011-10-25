package org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms;

public final class LMTInteraction extends LMTerm
{
	final int varIndex1;
	final int varIndex2;
	
	public LMTInteraction(int varIndex1, int varIndex2)
	{
		this.varIndex1 = varIndex1;
		this.varIndex2 = varIndex2;
	}
	
	public double evaluate(float[][] data, int row)
	{
		return data[row][varIndex1]*data[row][varIndex2];
	}
	
	public double evaluate(double[][] data, int row)
	{
		return data[row][varIndex1]*data[row][varIndex2];
	}
	
	public double evaluate(byte[][] data, int row)
	{
		return data[row][varIndex1]*data[row][varIndex2];
	}
	
	public String toString()
	{
		return "x"+varIndex1+":x"+varIndex2;
	}
	
	public int hashCode()
	{
		return varIndex1+varIndex2;
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof LMTInteraction)
		{
			LMTInteraction o = (LMTInteraction)other;
			if ((o.varIndex1==varIndex1 && o.varIndex2==varIndex2) || (o.varIndex1==varIndex2 && o.varIndex2==varIndex1)) return true;
		}
		
		return false;
	}
	
	public int varIndex1()
	{
		return varIndex1;
	}
	
	public int varIndex2()
	{
		return varIndex2;
	}
}
