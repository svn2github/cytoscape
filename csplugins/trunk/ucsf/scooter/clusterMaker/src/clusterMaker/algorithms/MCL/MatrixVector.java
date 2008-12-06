package clusterMaker.algorithms.MCL;

import java.lang.Math;

public class MatrixVector {

	protected double[] vecArray;
	protected double clusteringThresh;
	//index of element in matrix associated with vector
	protected int id;
	
	public MatrixVector(double[] array, int index, double clusteringTresh)
	{
		
		id = index;
		this.clusteringThresh = clusteringThresh;
		
		this.vecArray = (double[])array.clone();
		
		normalize();
	}

	public MatrixVector(MatrixVector v) {
		id = v.id;
		this.clusteringThresh = v.clusteringThresh;
		this.vecArray = (double[])v.vecArray.clone();
	}

	public double[] toArray() {
		return (double[])vecArray.clone();
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
	
	public double getIndex(int index){
		return vecArray[index];
	}

	//take dotproduct with array associated with another vector
	public double dotProduct(MatrixVector vec)
	{
		double sum = 0;
		
		for(int i=0; i < vecArray.length; i++)
			sum += vecArray[i]*vec.vecArray[i];
		
		return sum;
	}
	
	//Multiply by double array associated with a matrix and return results
	public double[] matrixMultiply(MatrixVector[] vectors)
	{
		double[] result = new double[vecArray.length];
		
		for(int i=0; i < vecArray.length; i++)
			result[i] = dotProduct(vectors[i]);		
		
		return result;
	}
	
	//change the vector to equal input array
	public void updateVector(double[] array)
	{
		this.vecArray = array.clone();
	}
}
