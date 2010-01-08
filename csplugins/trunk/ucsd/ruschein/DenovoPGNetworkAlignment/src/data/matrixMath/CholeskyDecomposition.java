package data.matrixMath;

import data.*;

public class CholeskyDecomposition
{
	private double[][] L;
	
	public CholeskyDecomposition(DoubleMatrix a)
	{
		decompose(a);
	}
	
	private void decompose(DoubleMatrix a)
	{
		L = new double[a.numRows()][];
		
		for (int i=0;i<a.numRows();i++)
			L[i] = new double[i+1];
		
		L[0][0] = Math.sqrt(a.get(0, 0));
		
		for (int i=1;i<a.numRows();i++)
		{
			for (int j=0;j<L[i].length-1;j++)
			{
				double sum = 0;
				
				for (int k=0;k<j;k++)
					sum += L[i][k]*L[j][k];
					
				L[i][j] = (a.get(i, j)-sum)/L[j][j];
			}
			
			double sum=0;
			for (int k=0;k<i;k++)
				sum += L[i][k]*L[i][k];
				
			
			if (a.get(i, i)<sum)
			{
				System.err.println("Error CholeskyDecomposition.decompose(DoubleMatrix): Sqrt(-#) encountered. Entry matrix is poorly conditioned.");
			}
			else L[i][i] = Math.sqrt(a.get(i, i)-sum);
		}
				
	}
	
	public DoubleVector solve(DoubleVector b)
	{
		DoubleVector out = b.clone();
		
		for (int j=0;j<L.length-1;j++)
			for (int i=j+1;i<L.length;i++)
				out.set(i,out.get(i)-L[i][j]/L[j][j]*out.get(j));
		
		for (int i=0;i<out.size();i++)
			out.set(i,out.get(i)/L[i][i]);
		
		for (int i=L.length-1;i>=1;i--)
			for (int j=i-1;j>=0;j--)
				out.set(j,out.get(j)-L[i][j]/L[i][i]*out.get(i));
		
		for (int i=0;i<out.size();i++)
			out.set(i,out.get(i)/L[i][i]);
		
		return out;
	}
}
