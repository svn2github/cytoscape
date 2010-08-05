package org.idekerlab.PanGIAPlugin;

import org.idekerlab.PanGIAPlugin.util.*;
import org.idekerlab.PanGIAPlugin.data.*;

public class Sandbox
{
	public static void main(String[] args)
	{
		
		double[] v = DoubleVector.rUnif(100);
		for (int i=10;i<20;i++)
			v[i] = v[10];
		
		System.out.println(new DoubleVector(DoubleVector.quantileNorm(v)));
		System.out.println(new DoubleVector(ScalerFactory.getScaler("rank").scale(v,0,1)));
	}
}
