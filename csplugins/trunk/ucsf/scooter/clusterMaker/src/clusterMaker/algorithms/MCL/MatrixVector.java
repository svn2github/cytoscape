package clusterMaker.algorithms.MCL;

import java.lang.Math;

public class MatrixVector {

	private double[] vecArray;
	private double clusteringThresh;
	//index of element in matrix associated with vector
	private int id;
	
	public MatrixVector(double[] array, int index,double clusteringTresh)
	{
		
		vecArray = new double[array.length];
		id = index;
		this.clusteringThresh = clusteringThresh;
		
		for(int i=0; i < array.length; i++)
			vecArray[i] = array[i];
		
		normalize();
	}
	
	//normalize vector. If 0 vector, index should equal 1.
	private void normalize()
	{
		double sum = 0;
		
		for(int i=0; i < vecArray.length; i++)
		{
			if(vecArray[i] < clusteringThresh)
				vecArray[i] = 0;
			else
				sum += vecArray[i];
		}
		
		if(sum == 0)
			vecArray[id] = 1;
		
		else
			for(int i=0; i < vecArray.length; i++)
				vecArray[i] = vecArray[i]/sum;
				
		
		
		
	}
	
	//inflate vector to emphasize difference in edgweights
	public void inflate(double inflationParameter)
	{
		for(int i=0; i < vecArray.length; i++)
		{
			vecArray[i] = Math.pow(vecArray[i],inflationParameter);
		}
		
		normalize();
	}
	
	
	public double getIndex(int index){return vecArray[index];}

	//take dotproduct with array associated with another vector
	public double dotProduct(double[] array)
	{
		double sum = 0;
		
		for(int i=0; i < vecArray.length; i++)
			sum += vecArray[i]*array[i];
		
		return sum;
	}
	
	//Multiply by double array associated with a matrix and return results
	public double[] matrixMultiply(double[][] matrix)
	{
		double[] result = new double[vecArray.length];
		
		for(int i=0; i < vecArray.length; i++)
			result[i] = dotProduct(matrix[i]);		
		
		return result;
	}
	
	//change the vector to equal input array
	public void updateVector(double[] array)
	{
		for(int i=0; i < array.length; i++)
			vecArray[i] = array[i];
	}
}
