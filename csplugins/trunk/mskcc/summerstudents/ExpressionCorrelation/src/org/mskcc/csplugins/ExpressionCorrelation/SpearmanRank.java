//
//  SpearmanRank.java
//  
//
//  Created by Shirley Hui on 08/05/07.
//  Copyright 2007 __MyCompanyName__. All rights reserved.
//
package org.mskcc.csplugins.ExpressionCorrelation;

import cern.colt.matrix.*;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.impl.*;
import cern.colt.matrix.doublealgo.*;
import java.util.*;
import cern.jet.math.Functions;
import cern.colt.matrix.doublealgo.Statistic;

public class SpearmanRank {

	private List rankInfoList;
    private DoubleMatrix2D distanceMatrix;

    public SpearmanRank(DoubleMatrix2D data)
	{
        System.out.println("Initializing Spearman Rank...");
        init(data);
	}
	
	// Perform quicksort on 2 d matrix v1, but sort wrt to first column
	private DoubleMatrix2D sort(DoubleMatrix2D v1)
	{
		DoubleMatrix2D view = Sorting.quickSort.sort(v1,0);
		return view;
	}

	private RankInfo rank(double[] x)
	{
		int j = 0;
		int ji;
		int jt;
		double t;
		int n = x.length;
		double r;
		double s = 0.0;
		double[] w = new double[n];
		
		for (int i = 0;i < n;i++)
		{
			w[i] = x[i];
		}
		
		while (j < n)
		{
			if (j == n-1)
				break;
			// not a tie
			if (w[j+1] != w[j] && j < n)
			{
				w[j] = j;
				j = j + 1;
			} else
			{
				for (jt = j+ 1; jt < n && w[jt]==w[j];jt++);
				r = 0.5*(j+jt-1);
				for (ji=j;ji <=(jt-1);ji++) w[ji] = r;
				t = jt-j;
				s = s + t*t*t-1;
				j = jt;
			}
		}
		if (j==n-1) w[n-1] = n-1;
		
		/*for (int i = 0; i < w.length;i++)
		{
			System.out.print( w[i] + ", ");
		}
		System.out.println();
		*/
		return new RankInfo(w,s);
		//return w;
	}
	
	private static DoubleMatrix2D addIndices(DoubleMatrix1D x)
	{
		int n = x.size();
		// Create a 2D matrix.  The first column is v1, the second column is [1..v1.length]
		DenseDoubleMatrix2D v = new DenseDoubleMatrix2D(n,2);
		
		for (int i = 0; i < n; i++) 
		{
			for (int j = 0;j < 2;j++)
			{
				double xValue = x.get(i);
				
				if (j == 0)
				{
					//System.out.print(x.get(i));
					v.setQuick(i, j, xValue);
				}
				else
					v.setQuick(i,j,i);
			}
		}
		
		//DoubleMatrix2D view =Sorting.quickSort.sort(v1,0);
		//System.out.println(view); 
		return v;
	}
	
	private void init(DoubleMatrix2D rdata)
	{
		int n = rdata.rows();
		rankInfoList = new ArrayList();
		for (int i = 0; i < n;i++)
		{
			DoubleMatrix1D x = rdata.viewRow(i);
			DoubleMatrix2D v1 = addIndices(x);
			// Sort v1 wrt to first column 
			DoubleMatrix2D sortedV1 = sort(v1);
			// v1 first column are the sorted data values
			DoubleMatrix1D sortedColV1 = sortedV1.viewColumn(0);
			// v1 second column are the original indices sorted according to first column
			DoubleMatrix1D sortedIndicesV1 = sortedV1.viewColumn(1);
			
			double[] vv2 = sortedColV1.toArray();
			RankInfo rr1 = rank(vv2);
			rr1.setIndices(sortedIndicesV1.toArray());
			
			rankInfoList.add(rr1);
		}

        System.out.println("rankInfoList contains: " + rankInfoList.size() + " number of RankInfos");
        Algebra A = new Algebra();
        distanceMatrix = Statistic.distance(A.transpose(rdata),Statistic.EUCLID);
        System.out.println("The distance matrix is: " + distanceMatrix.rows() + " rows by " + distanceMatrix.columns() + "columns");
    }
	
	/** 
		* calculate the spearman correlation for two vectors d and d2
		**/
	public double corr(int xindex, int yindex)
	{
		if (rankInfoList.size()==0)
			return -2.0;
			
		RankInfo xrank = (RankInfo) rankInfoList.get(xindex);
		RankInfo yrank = (RankInfo) rankInfoList.get(yindex);
		
		double[] r1 = xrank.ranking;
		double sf = xrank.s;
		double[] r1index = xrank.sortedIndices;
		
		double[] r2 = yrank.ranking;
		double sg = yrank.s;
		double[] r2index = yrank.sortedIndices;

        // compute sum of square diffs
        //DoubleMatrix1D d1 = new DenseDoubleMatrix1D(r1);
        //DoubleMatrix1D d2 = new DenseDoubleMatrix1D(r2);

        //double sumOfDiffSquared = d1.aggregate(d2, Functions.plus, Functions.chain(Functions.square,Functions.minus));

        int n = r1.length;
        double d1 = 0.0;
        /*
		for (int j = 0; j <n;j++)
		{
            double diff = r1[(int)r1index[j]]-r2[(int)r2index[j]];
            //double diff = 0.0;
            double diffSquared = diff*diff;
            d1 = d1 + diffSquared; //Math.pow(r1[(int)r1index[j]]-r2[(int)r2index[j]],2);
		} */
		double en = (double)n;
		double en3n = en*en*en-en;
		/*double fac = (1.0-sf/en3n)*(1.0-sg/en3n);
		double p = (1.0 - (6.0/en3n)*(d1+(sf+sg)/12.0))/Math.sqrt(fac);
		*/
        double p = 1.0 - (6.0 * d1)/(en3n-n);
        return p;
	}
	
	private class RankInfo
	{
		double[] ranking;
		double s = 0.0;
		double[] sortedIndices;
		
		public RankInfo(double[] ranking, double s)
		{
			this.ranking = ranking;
			this.s = s;
		}
		
		public void setIndices(double[] sortedIndices)
		{
			this.sortedIndices = sortedIndices;
		}
	}
	
	
}
