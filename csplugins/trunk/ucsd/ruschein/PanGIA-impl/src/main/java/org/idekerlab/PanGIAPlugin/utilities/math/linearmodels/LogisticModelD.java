package org.idekerlab.PanGIAPlugin.utilities.math.linearmodels;

import java.util.*;

import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.*;

public class LogisticModelD extends AbstractSingleLinearModelD
{
	
	
	public LogisticModelD(List<LMTerm> terms, double[][] x, double[] y)
	{
		super(terms,x,y);
	}
	
	public LogisticModelD(List<LMTerm> terms, float[][] x, double[] y)
	{
		super(terms,x,y);
	}
	
	public LogisticModelD(List<LMTerm> terms, byte[][] x, double[] y)
	{
		super(terms,x,y);
	}
	
	public void regress()
	{
		this.coefficients = LogisticRegression.logisticRegression(this.evaluateX(), y,25,1e-8,false);
		if (this.coefficients==null) System.out.println("Did not converge: "+this);
	}
	
	public void regress(int maxit)
	{
		this.coefficients = LogisticRegression.logisticRegression(this.evaluateX(), y,maxit,1e-8,false);
		if (this.coefficients==null) System.out.println("Did not converge: "+this);
	}
	
	public void regressSilent()
	{
		this.coefficients = LogisticRegression.logisticRegression(this.evaluateX(), y,25,1e-8,true);
	}
	
	public void regressSilent(int maxit)
	{
		this.coefficients = LogisticRegression.logisticRegression(this.evaluateX(), y,maxit,1e-8,true);
	}
	
	public void regress(int maxit, double epsilon)
	{
		this.coefficients = LogisticRegression.logisticRegression(this.evaluateX(), y,maxit,epsilon,false);
	}
	
	public void regress(int maxit, double epsilon, double[] weights)
	{
		this.coefficients = LogisticRegression.logisticRegression(this.evaluateX(), y,weights,maxit,epsilon,false);
	}
	
	
	public double yhat(int i)
	{
		double e = 0;
		
		for (int t=0;t<terms.size();t++)
			e+=coefficients.get(t)*terms.get(t).evaluate(x, i);
		
		e = Math.exp(e);
		
		return e/(1+e);
	}
	
	public double[] yhat()
	{
		double[] out = new double[y.length];
		
		for (int i=0;i<y.length;i++)
			out[i] = yhat(i);
		
		return out;
	}
	
	public double FANOVA(double SST)
	{
		int df1 = terms.size()-1;
		int df2 = y.length-terms.size();
		
		double SSE = this.SSE();
		
		return (SST-SSE)/SSE*df2/df1;
		
		//IN R, anova(lm1,lm2) does, F = (sst-sse)/sse*df2/df1  yes! sst-sse = ssb 
		//df1 = terms.size()-1
		//df2 = y.length-df1
	}
	
	/**
	 * Determines the F-statistic of fit for this model as compared to another model.
	 * Assumes both models are fit to the same data.
	 * @return F statistic
	 */
	public double compareFitF(LogisticModelD other)
	{
		double SSE1 = this.SSE();
		double SSREG = other.SSE()-SSE1;
		
		int df1 = terms.size()-other.numTerms();
		int df2 = y.length-terms.size();
		
		return SSREG/SSE1*df2/df1;
	}
	
		
	/**
	 * Performs a test of variance to determine if the variance of this model is less than the intercept model.
	 * @param SST = y.SSE()
	 * @return F statistic
	 */
	public double varTestF(double SST)
	{
		int df1 = this.df();
		int df0 = x.length-1;
		
		return this.SSE()/SST*df0/df1;
	}
	
	public double logLikelyhood()
	{
		double yhat0 = yhat(0);
		double L = Math.log(Math.pow(yhat0,y[0])*Math.pow(1-yhat0,1-y[0]));
		
		for (int i=1;i<y.length;i++)
		{
			double yhati = yhat(i);
			L+=Math.log(Math.pow(yhati,y[i])*Math.pow(1-yhati,1-y[i]));
		}
		
		return L;
	}
	
	public double logLikelyhoodBinary()
	{
		double yhat0 = yhat(0);
		double L = y[0]==1 ? Math.log(yhat0) : Math.log(1-yhat0);
		
		for (int i=1;i<y.length;i++)
			L+= y[i]==1 ? Math.log(yhat(i)) : Math.log(1-yhat(i));
		
		return L;
	}
	
}
