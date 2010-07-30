package org.idekerlab.PanGIAPlugin;

import org.idekerlab.PanGIAPlugin.networks.*;
import org.idekerlab.PanGIAPlugin.networks.SEdge;
import org.idekerlab.PanGIAPlugin.networks.SFEdge;
import org.idekerlab.PanGIAPlugin.networks.hashNetworks.*;
import java.util.*;

import org.idekerlab.PanGIAPlugin.data.DoubleMatrix;
import org.idekerlab.PanGIAPlugin.data.DoubleVector;

import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.LogisticModelD;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTIntercept;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTSingle;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTerm;



public class ComplexRegression
{
	public static ComplexRegressionResult complexRegress(SFNetwork net, List<SNodeModule> complexList, boolean abs)
	{
		int n = net.numEdges();
		
		double[][] x = new double[n][2];
		double[] y = new double[n];
		
		List<SEdge> edgeList = new ArrayList<SEdge>(n);
		
		int i=0;
		for (SFEdge e : net.edgeIterator())
		{
			x[i][0] = (abs) ? Math.abs(e.value()) : e.value();
			x[i][1] = e.value();
			
			y[i] = 0;
			for (SNodeModule c : complexList)
				if (c.contains(e.getI1()) && c.contains(e.getI2()))
				{
					y[i] = 1;
					break;
				}
							
			edgeList.add(e);
			
			i++;
		}
		
		List<LMTerm> terms = new ArrayList<LMTerm>(n);
		terms.add(new LMTSingle(0));
		terms.add(new LMTIntercept());
		
		LogisticModelD lm = new LogisticModelD(terms,x,y);
		lm.regress(100);
		
		System.out.println("Training coefficients: "+lm.coefficients());
		//System.out.println("Fit F statistic: "+lm.FANOVA(new DoubleVector(y).SST())+", df1=1, df2="+(y.length-2));
		
		FloatHashNetwork out = new FloatHashNetwork(false,false,n);
		
		for (int j=0;j<n;j++)
		{
			double prob = lm.yhat(j);
			
			if (prob==1) prob = 1-1/(double)n;
			prob = prob / (1-prob);
			
			if (Float.isInfinite((float)prob))
			{
				prob = 1-1/(double)n;
				prob = prob / (1-prob);
			}
			
			out.add(edgeList.get(j).getI1(),edgeList.get(j).getI2(),(float)prob);
		}
		
		return new ComplexRegressionResult(out,DoubleMatrix.getCol(x, 1),y,lm.coefficients().get(0),lm.coefficients().get(1));
	}
}
