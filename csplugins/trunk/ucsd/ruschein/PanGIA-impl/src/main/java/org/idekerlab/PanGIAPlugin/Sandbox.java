package org.idekerlab.PanGIAPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.idekerlab.PanGIAPlugin.networks.SEdge;
import org.idekerlab.PanGIAPlugin.networks.SFEdge;
import org.idekerlab.PanGIAPlugin.networks.SNodeModule;
import org.idekerlab.PanGIAPlugin.util.*;
import org.idekerlab.PanGIAPlugin.data.*;
import org.idekerlab.PanGIAPlugin.utilities.math.*;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.LogisticModelD;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTIntercept;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTSingle;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTerm;

public class Sandbox
{
	public static void main(String[] args)
	{
		int n=10000000;
		
		
		Random r = new Random();
		
		double[][] x = new double[n][1];
		double[] y = new double[n];
		
		for (int i=0;i<n;i++)
		{
			x[i][0] = r.nextDouble();
			y[i] = (r.nextBoolean()) ? 1 : 0;
		}
		
		
		List<LMTerm> terms = new ArrayList<LMTerm>(n);
		terms.add(new LMTSingle(0));
		terms.add(new LMTIntercept());
		
		LogisticModelD lm = new LogisticModelD(terms,x,y);
		lm.regress(100);
		
		System.out.println(lm.coefficients());
	}
}
