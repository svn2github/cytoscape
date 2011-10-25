package org.idekerlab.PanGIAPlugin.utilities.math.linearmodels;

import java.util.*;

import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTInteraction;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTIntercept;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTSingle;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTSingleIndicator;
import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTerm;


import org.idekerlab.PanGIAPlugin.utilities.math.svd.SVD;
import org.idekerlab.PanGIAPlugin.utilities.math.*;
import org.idekerlab.PanGIAPlugin.data.*;

public abstract class AbstractSingleLinearModelD extends AbstractLinearModelD
{
	protected DoubleVector coefficients;
	protected final double[] y;
	protected float SSE=Float.NaN;
	
	public AbstractSingleLinearModelD(List<LMTerm> terms, byte[][] x, double[] y)
	{
		super(terms,x);
		this.y = y;
	}
	
	public AbstractSingleLinearModelD(List<LMTerm> terms, float[][] x, double[] y)
	{
		super(terms,x);
		this.y = y;
	}
	
	public AbstractSingleLinearModelD(List<LMTerm> terms, double[][] x, double[] y)
	{
		super(terms,x);
		this.y = y;
	}
	
	/**
	 * Note: For efficiency, this returns the reference to y. Do not modify it.
	 */
	public double[] y()
	{
		return y;
	}
	
	public int ysize()
	{
		return y.length;
	}
	
	public int df()
	{
		return x.length-terms.size(); 
	}
	
	public int numCols()
	{
		return x[0].length;
	}
	
	public double SSE()
	{
		if (!Float.isNaN(this.SSE)) return this.SSE;
		
		float sse = 0;
		
		for (int i=0;i<y.length;i++)
		{
			double diff = y[i]-yhat(i);
			sse += diff*diff;
		}
		
		this.SSE = sse;
		
		return sse;
	}
	
	public String toString()
	{
		String out = "y=";
		
		if (coefficients==null)
			for (int i=0;i<terms.size();i++)
			{
				out+="b"+i+"*"+terms.get(i);
				if (i!=terms.size()-1) out+=" + ";
			}
		else for (int i=0;i<terms.size();i++)
		{
			out+=String.format("%.3f*"+terms.get(i),coefficients.get(i));
			if (i!=terms.size()-1) out+=" + ";
		}
		
		return out;
	}
	
	public LogisticModelD getSubmodel()
	{
		double[][] newX = this.evaluateX();
		
		List<LMTerm> newTerms = new ArrayList<LMTerm>(newX[0].length);
		for (int i=0;i<newX[0].length;i++)
			newTerms.add(new LMTSingle(i));
		
		return new LogisticModelD(newTerms,newX,y);
	}
	
	
	/***
	 * Produces a model with a reduced number of eigenterms
	 * The number of eigenterms is based on the percentVariance (0-1) and will never be more than maxPC 
	 */
	public LogisticModelD deriveEigenMarkerLogisticSubmodel(double percentVariance, int maxVec)
	{	
		//Determine the eigenterms
		double[][] newX = SVD.eigenVectors(evaluateX(), percentVariance, maxVec);
		
		if (newX.length==0) System.out.println(evaluateX()[0].length+", "+percentVariance+", "+maxVec);
				
		List<LMTerm> eigenTerms = new ArrayList<LMTerm>(newX[0].length);
		for (int i=0;i<newX[0].length;i++)
			eigenTerms.add(new LMTSingle(i));
		
		return new LogisticModelD(eigenTerms,newX,y);
	}
	
	public LogisticModelD deriveEigenMarkerLogisticSubmodel_MultiThreaded(double percentVariance, int maxVec)
	{	
		//Determine the eigenterms
		double[][] newX = SVD.eigenVectors_MultiThreaded(evaluateX(), percentVariance, maxVec);
				
		List<LMTerm> eigenTerms = new ArrayList<LMTerm>(newX[0].length);
		for (int i=0;i<newX[0].length;i++)
			eigenTerms.add(new LMTSingle(i));
		
		return new LogisticModelD(eigenTerms,newX,y);
	}
	
	public void printCoefficients()
	{
		this.coefficients.print();
	}
	
	public double chiSq()
	{
		double chisq = 0;
		
		for (int i=0;i<y.length;i++)
		{
			double ome = y[i]-yhat(i);
			chisq += (ome*ome)/yhat(i);
		}
		
		return chisq;
	}
	
	public DoubleVector coefficients()
	{
		return coefficients;
	}
}
