package org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms;

import java.util.*;

public final class LMTInteractionIndicator extends LMTerm
{
	final int varIndex1;
	final int varIndex2;
	final int value1;
	final int value2;
	
	public LMTInteractionIndicator(int varIndex1, int varIndex2, int value1, int value2)
	{
		this.varIndex1 = varIndex1;
		this.varIndex2 = varIndex2;
		this.value1 = value1;
		this.value2 = value2;
	}
	
	public double evaluate(float[][] data, int row)
	{
		if ((int)data[row][varIndex1]==value1 && (int)data[row][varIndex2]==value2) return 1;
		else return 0;
	}
	
	public double evaluate(byte[][] data, int row)
	{
		if (data[row][varIndex1]==value1 && data[row][varIndex2]==value2) return 1;
		else return 0;
	}
	
	public double evaluate(double[][] data, int row)
	{
		if ((int)data[row][varIndex1]==value1 && (int)data[row][varIndex2]==value2) return 1;
		else return 0;
	}
	
	public String toString()
	{
		return "I(x"+varIndex1+"=="+value1+"&&x"+varIndex2+"=="+value2+")";
	}
	
	public int hashCode()
	{
		return varIndex1+varIndex2+value1+value2;
	}
	
	public boolean equals(Object other)
	{
		if (other instanceof LMTInteractionIndicator)
		{
			LMTInteractionIndicator o = (LMTInteractionIndicator)other;
			if ((o.varIndex1==varIndex1 && o.varIndex2==varIndex2 && o.value1==value1 && o.value2==value2) || (o.varIndex1==varIndex2 && o.varIndex2==varIndex1 && o.value1==value2 && o.value2==value1)) return true;
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
	
	public static List<LMTInteractionIndicator> generateIndicators(int i1, int i2, int numStates)
	{
		List<LMTInteractionIndicator> out = new ArrayList<LMTInteractionIndicator>(numStates*numStates-1);
		
		int nsm1 = numStates-1; 
		
		for (int i=0;i<numStates;i++)
			for (int j=0;j<numStates;j++)
				if (i!=nsm1 || j!=nsm1)out.add(new LMTInteractionIndicator(i1,i2,i,j));
		
		return out;
	}
}
