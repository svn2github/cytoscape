package org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms;


/**
 * Provides a linear model term, which is an indicator variable (0,1) indicating a particular value.
 * @author ghannum
 */
public final class LMTSingleIndicator extends LMTerm
{
	final int varIndex;
	final int value;
	
	public LMTSingleIndicator(int varIndex, int value)
	{
		this.varIndex = varIndex;
		this.value = value;
	}
	
	public double evaluate(double[][] data, int row)
	{
		if ((int)data[row][varIndex]==value) return 1;
		else return 0;
	}
	
	public double evaluate(float[][] data, int row)
	{
		if ((int)data[row][varIndex]==value) return 1;
		else return 0;
	}
	
	public double evaluate(byte[][] data, int row)
	{
		if (data[row][varIndex]==value) return 1;
		else return 0;
	}
	
	public String toString()
	{
		return "I(x"+varIndex+"=="+value+")";
	}
	
	public int hashCode()
	{
		return varIndex;
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof LMTSingleIndicator)
			return ((LMTSingleIndicator)other).varIndex==varIndex && ((LMTSingleIndicator)other).value==value;
		
		return false;
	}
	
	public int varIndex()
	{
		return varIndex;
	}
	
	public int value()
	{
		return value;
	}
}
