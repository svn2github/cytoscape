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
	public static ComplexRegressionResult complexRegress(SFNetwork net, List<SNodeModule> complexList, boolean abs, int nullScore)
	{
		int n = net.numEdges();
		
		//Build within complex network
		BooleanHashNetwork wnet = new BooleanHashNetwork(false,false,100000);
		for (SNodeModule c : complexList)
			wnet.addAll(c.asNetwork());
				
		List<String> nodes = new ArrayList<String>(net.getNodes());
		
		double[] rawx = new double[n];
		double[] rawy = new double[n];
		double[][] x = new double[n+2][1];
		double[] y = new double[n+2];
		
		x[0][0] = nullScore;
		x[1][0] = nullScore;
		y[0] = 1;
		y[1] = 0;
		
		int absentHits = 0;
		int absentMisses = 0;
		
		List<SEdge> edgeList = new ArrayList<SEdge>(n);
		
		int index = 2;
		for (int i=0;i<nodes.size()-1;i++)
		{
			String n1 = nodes.get(i);
			for (int j=i+1;j<nodes.size();j++)
			{
				boolean within = wnet.contains(n1, nodes.get(j));
				
				float val = net.edgeValue(n1, nodes.get(j));
				
				if (Double.isNaN(val))
				{
					if (within) absentHits++;
					else absentMisses++;
				}else
				{
					x[index][0] = (abs) ? Math.abs(val) : val;
					y[index] = (within) ? 1 : 0;
					rawx[index-2] = val;
					rawy[index-2] = y[index];
					edgeList.add(new UndirectedSEdge(n1,nodes.get(j)));
					index++;
				}
			}
		}
		
		if (absentHits==0 || absentMisses==0)
		{
			throw new java.lang.AssertionError("Absent hits = "+absentHits+",  Absent misses = "+absentMisses);
		}
		
		double[] weights = new double[y.length];
		weights[0] = absentHits;
		weights[1] = absentMisses;
		
		if (weights[0]==0) weights[0] = 1e-6;
		if (weights[1]==0) weights[1] = 1e-6;
		
		for (int i=2;i<weights.length;i++)
			weights[i] = 1;
		
		List<LMTerm> terms = new ArrayList<LMTerm>(n);
		terms.add(new LMTSingle(0));
		terms.add(new LMTIntercept());
		
		LogisticModelD lm = new LogisticModelD(terms,x,y);
		lm.regress(100,1e-8,weights);
		
		double background = wnet.numEdges()/(double)(nodes.size()*(nodes.size()-1)/2);
		double logBackground = Math.log(background);
		
		System.out.println("Background: "+background);
		System.out.println("Training coefficients: "+lm.coefficients());
		System.out.println("Absent hits: "+absentHits+", Absent misses:"+absentMisses);
		//System.out.println("Fit F statistic: "+lm.FANOVA(new DoubleVector(y).SST())+", df1=1, df2="+(y.length-2));
		
		FloatHashNetwork out = new FloatHashNetwork(false,false,n);
						
		for (int j=2;j<n+2;j++)
		{
			double prob = lm.yhat(j);
			
			if (prob==1) prob = 1-1/(double)n;
			if (prob==0) prob = 1/(double)n;
			
			//prob = Math.log(prob)-Math.log(1-prob);
			prob = Math.log(prob)-logBackground;
			
			/*
			prob = prob / (1-prob);
			
			if (Float.isInfinite((float)prob))
			{
				prob = 1-1/(double)n;
				prob = prob / (1-prob);
			}*/
			
			if (prob>0) out.add(edgeList.get(j-2).getI1(),edgeList.get(j-2).getI2(),(float)prob);
		}
		
		return new ComplexRegressionResult(out,rawx,rawy,lm.coefficients().get(0),lm.coefficients().get(1),absentHits,absentMisses,background);
	}
}
