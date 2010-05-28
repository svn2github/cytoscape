package org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms;

import java.util.*;

public final class LMTInteractionIndicatorUnordered extends LMTerm
{
	final int varIndex1;
	final int varIndex2;
	final int value1;
	final int value2;
	
	public LMTInteractionIndicatorUnordered(int varIndex1, int varIndex2, int value1, int value2)
	{
		this.varIndex1 = varIndex1;
		this.varIndex2 = varIndex2;
		this.value1 = value1;
		this.value2 = value2;
	}
	
	public double evaluate(float[][] data, int row)
	{
		if (((int)data[row][varIndex1]==value1 && (int)data[row][varIndex2]==value2) ||  ((int)data[row][varIndex1]==value2 && (int)data[row][varIndex2]==value1)) return 1;
		else return 0;
	}
	
	public double evaluate(byte[][] data, int row)
	{
		if ((data[row][varIndex1]==value1 && data[row][varIndex2]==value2) || (data[row][varIndex1]==value2 && data[row][varIndex2]==value1)) return 1;
		else return 0;
	}
	
	public double evaluate(double[][] data, int row)
	{
		if (((int)data[row][varIndex1]==value1 && (int)data[row][varIndex2]==value2) || ((int)data[row][varIndex1]==value2 && (int)data[row][varIndex2]==value1)) return 1;
		else return 0;
	}
	
	public String toString()
	{
		return "I(x"+varIndex1+"x,"+varIndex2+"x=="+value1+","+value2+")";
	}
	
	public int hashCode()
	{
		return varIndex1+varIndex2+value1+value2;
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof LMTInteractionIndicatorUnordered)
		{
			LMTInteractionIndicatorUnordered o = (LMTInteractionIndicatorUnordered)other;
			
			if (varIndex1!=o.varIndex1 && varIndex1!=o.varIndex2) return false;
			if (varIndex2!=o.varIndex1 && varIndex2!=o.varIndex2) return false;
			if (value1!=o.value1 && value1!=o.value2) return false;
			if (value2!=o.value1 && value2!=o.value2) return false;
		}else return false;
		
		return true;
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
