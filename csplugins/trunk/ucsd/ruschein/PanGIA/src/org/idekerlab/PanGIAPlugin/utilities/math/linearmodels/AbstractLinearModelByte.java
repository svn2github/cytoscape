package org.idekerlab.PanGIAPlugin.utilities.math.linearmodels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.idekerlab.PanGIAPlugin.utilities.math.linearmodels.lmterms.LMTerm;


public abstract class AbstractLinearModelByte extends AbstractLinearModel
{
	protected final byte[][] x; //individuals x markers
	
	public abstract int df();
	public abstract double SSE();
	public abstract void regress();
	public abstract double FANOVA(double SST);
	public abstract void printCoefficients();
	public abstract double yhat(int i);
	public abstract int ysize();
	
	public AbstractLinearModelByte(List<LMTerm> terms, byte[][] x)
	{
		super(terms);
		this.x = x;
	}
	
	public boolean areRedundant(LMTerm term1, LMTerm term2)
	{
		for (int i=0;i<x.length;i++)
			if (term1.evaluate(x, i)!=term2.evaluate(x, i)) return false;
		
		return true;
	}
	
	/**
	 * Compute the term evaluation matrix X.
	 */
	public double[][] evaluateX()
	{
		double[][] X = new double[this.x.length][terms.size()];
		
		for (int i=0;i<this.x.length;i++)
			for (int j=0;j<terms.size();j++)
				X[i][j] = terms.get(j).evaluate(x,i);
		
		return X;
	}
	
	/**
	 * Note: For efficiency, this returns the actual datamatrix x.
	 */
	public byte[][] x()
	{
		return x;
	}
	
	
	public void removeRedundantTerms()
	{
		//Identify redundancies
		Map<LMTerm,Set<LMTerm>> rMap = new HashMap<LMTerm,Set<LMTerm>>();
		
		List<LMTerm> keepers = new ArrayList<LMTerm>(terms.size());
		
		for (int i=0;i<terms.size()-1;i++)
			for (int j=i+1;j<terms.size();j++)
				if (this.areRedundant(terms.get(i),terms.get(j)))
				{
					Set<LMTerm> r = rMap.get(terms.get(i));
					
					if (r==null)
					{
						r = new HashSet<LMTerm>();
						r.add(terms.get(j));
						rMap.put(terms.get(i), r);
					} else r.add(terms.get(j));
					
					keepers.add(terms.get(i));
				}
		
		//Identify terms to remove
		Set<LMTerm> redundant = new HashSet<LMTerm>(rMap.size()*2);
		
		
		for (LMTerm term : keepers)
		{
			redundant.addAll(determineRedundant(rMap.get(term),rMap));
			rMap.remove(term);
		}
		
		this.terms.removeAll(redundant);
	}
	
	private Set<LMTerm> determineRedundant(Set<LMTerm> toBeRemoved, Map<LMTerm,Set<LMTerm>> rMap)
	{
		Set<LMTerm> redundant = new HashSet<LMTerm>(toBeRemoved.size()*2);
		
		for (LMTerm t : toBeRemoved)
		{
			redundant.add(t);
					
			Set<LMTerm> rSet = rMap.get(t);
			if (rSet!=null)
			{
				redundant.addAll(determineRedundant(rMap.get(t),rMap));
				rMap.remove(t);
			}
		}
		
		return redundant;
	}
	
	
}
