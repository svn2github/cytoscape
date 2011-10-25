package org.idekerlab.PanGIAPlugin.utilities.math.svd;

import java.util.List;

import org.idekerlab.PanGIAPlugin.data.DoubleMatrix;
import org.idekerlab.PanGIAPlugin.data.DoubleVector;

public class SVD
{
	protected double[][] U;
	protected double[] S;
	protected double[][] V;
	
	public SVD(double[][] U, double[] S, double[][] V)
	{
		this.U = U;
		this.S = S;
		this.V = V;
	}
	
	public double[][] U()
	{
		return U;
	}
	
	public double[] S()
	{
		return S;
	}
	
	public double[][] V()
	{
		return V;
	}
	
	protected static double normalize(double[] vec)
	{
		double norm2 = 0;
		for (int i=0;i<vec.length;i++)
			norm2 += vec[i]*vec[i];
		norm2 = Math.sqrt(norm2);
		
		for (int i=0;i<vec.length;i++)
			vec[i] = vec[i]/norm2;
		
		return norm2;
	}
	
	public static SVD averageSVD(List<SVD> svdlist)
	{
		SVD svd0 = svdlist.get(0);
		
		double[][] U = DoubleMatrix.copy(svd0.U);
		double[][] V = (svd0.V==null) ? null : DoubleMatrix.copy(svd0.V);
		double[] S = DoubleVector.copy(svd0.S);
		
		for (int si=1;si<svdlist.size();si++)
		{
			SVD svdi = svdlist.get(si);
			
			double[][] Ui = svdi.U();
			double[][] Vi = svdi.V();
			double[] Si = svdi.S();
			
			for (int i=0;i<U.length;i++)
				for (int j=0;j<U[0].length;j++)
					U[i][j] += Ui[i][j];
			
			if (V!=null)
				for (int i=0;i<V.length;i++)
					for (int j=0;j<V[0].length;j++)
						V[i][j] += Vi[i][j];
			
			for (int i=0;i<S.length;i++)
				S[i] += Si[i];
		}
		
		int count = svdlist.size();
		
		for (int i=0;i<U.length;i++)
			for (int j=0;j<U[0].length;j++)
				U[i][j] /= count;
		
		if (V!=null)
			for (int i=0;i<V.length;i++)
				for (int j=0;j<V[0].length;j++)
					V[i][j] /= count;
		
		for (int i=0;i<S.length;i++)
			S[i] /= count;
		
		return new SVD(U,S,V);
	}
	
	public static double[][] eigenVectors(double[][] X, double percentVariance, int maxPC)
	{
		//DoubleMatrix.centerCols(X);
		
		return eigenVec(new LinpackSVD(X,false),percentVariance,maxPC);
	}
	
	public static double[][] eigenVectors_MultiThreaded(double[][] X, double percentVariance, int maxPC)
	{
		//DoubleMatrix.centerCols(X);
		
		return eigenVec(new LinpackSVD(X,false,true),percentVariance,maxPC);
	}
	
	private static double[][] eigenVec(SVD svd, double percentVariance, int maxPC)
	{
		double[] svals = svd.S();
		
		double target = DoubleVector.sum(svals)*percentVariance;
		
		int numPC=Math.min(maxPC, svals.length);
		double cumsum = 0;
		for (int i=0;i<numPC;i++)
		{
			cumsum+=svals[i];
			if (cumsum>=target)
			{
				numPC = i+1;
				break;
			}
		}
		
		//Build output
		double[][] U = svd.U();
		
		double[][] newx = new double[U.length][numPC];
		for (int i=0;i<U.length;i++)
			for (int j=0;j<numPC;j++)
				newx[i][j] = U[i][j];
		
		return newx;
	}
}
