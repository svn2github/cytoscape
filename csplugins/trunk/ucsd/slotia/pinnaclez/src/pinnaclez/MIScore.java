package pinnaclez;

import oiler.util.IntHashSet;
import modlab.AbstractScore;
import modlab.Score;

/**
 * Calculates the probability that
 * the vector follows the classification
 * specified by <code>classes[]</code>.
 */
public class MIScore extends AbstractActivityScore
{
	protected final int numBinsX, numBinsY;

	public MIScore(final ExpressionMatrix matrix)
	{
		this(matrix, matrix.experiments.length, numOfUniqueValues(matrix.classes));
	}
	
	public MIScore(final ExpressionMatrix matrix, final int numOfExperiments, final int numOfUniqueClasses)
	{
		super(matrix);
		numBinsX = log2(numOfExperiments) + 1;
		numBinsY = numOfUniqueClasses;
	}

	public double scoreVector(final double[] values, final int[] classes)
	{
		final double[][] hist2 = histogram2(values, classes);
			  
		final double[] rowSum    = new double[numBinsX];
		final double[] columnSum = new double[numBinsY];
		  
		for (int i = 0; i < numBinsX; i++)
			rowSum[i] = 0;
		  
		for (int i = 0; i < numBinsX; i++)
		for (int j = 0; j < numBinsY; j++)
			rowSum[i] = rowSum[i] + hist2[i][j];

		for (int i = 0; i < numBinsY; i++)
		for (int j = 0; j < numBinsX; j++)
			columnSum[i] += hist2[j][i];
		  
		  
		double count = 0;
		double miValue = 0;
		for (int i = 0; i < numBinsX; i++)
		for (int j = 0; j < numBinsY; j++)
		{
			double logf = 0.0;
			if (hist2[i][j] != 0)
				logf = Math.log(hist2[i][j]/rowSum[i]/columnSum[j]);
			count += hist2[i][j];
			miValue += hist2[i][j]*logf;
		}
		  
		miValue = miValue/count;
		miValue = miValue + Math.log(count);
		final double nbias = (numBinsX-1)*(numBinsY-1)/(2*count);
		miValue = miValue - nbias;
		  
		if (miValue<0)
			miValue = 0;
		else if (Double.isNaN(miValue)) //all expression levels are 0
			miValue = -1;
		 
		return miValue;
	}

	/**
	 * @return Math.floor(Math.log(n)/Math.log(2))
	 */
	private static int log2(int n)
	{
		if (n <= 0)
			throw new IllegalArgumentException("n <= 0");

		int c = 0;
		while (n != 1)
		{
			c++;
			n >>= 1;
		}
		return c;
	}

	private final double[][] histogram2(final double[] x, final int[] y)
	{
		double minX = min(x);
		double maxX = max(x);
		final double deltaX = ((maxX - minX) / (x.length-1)) / 2;
		minX -= deltaX;
		maxX += deltaX;

		final double minY = min(y) - 0.1f;
		final double maxY = max(y) + 0.1f;

		final double[][] result = new double[numBinsX][numBinsY];

		for (int i = 0; i < x.length; i++)
		{
			//final int idxX = Math.round((x[i]-minX)/(maxX-minX)*numBinsX + 0.5f);
			//final int idxY = Math.round((y[i]-minY)/(maxY-minY)*numBinsY + 0.5f);

			// This is an optimization: (int) Math.round(x) == (int) (x + 0.5)
			final int idxX = (int) ((x[i]-minX)/(maxX-minX)*numBinsX + 1.0);
			final int idxY = (int) ((y[i]-minY)/(maxY-minY)*numBinsY + 1.0);
			  
			if (idxX >=1 && idxX <= numBinsX && idxY >= 1 && idxY <= numBinsY)
				result[idxX-1][idxY-1]++;
		}
		  
		return result;
	}  
		
	private static final double min(final double[] x)
	{
		double minValue = Double.POSITIVE_INFINITY;
		for (int i=0; i<x.length; i++)
			if (x[i] < minValue) minValue = x[i];
		return minValue;
	}
		
	private static final double max(final double[] x)
	{
		double maxValue = Double.NEGATIVE_INFINITY;
		for (int i=0; i<x.length; i++)
			if (x[i] > maxValue) maxValue = x[i];
		return maxValue;
	}


	private static final int numOfUniqueValues(final int[] x)
	{
		final IntHashSet temp = new IntHashSet();
		for (int i=0; i < x.length; i++)
			temp.add(x[i]);
		return temp.size();
	}
		
	private static final int min(final int[] x)
	{
		int minValue = Integer.MAX_VALUE;
		for (int i=0; i<x.length; i++)
			if (x[i] < minValue) minValue = x[i];
		return minValue;
	}
		
	private static final int max(final int[] x)
	{
		int maxValue = Integer.MIN_VALUE;
		for (int i=0; i<x.length; i++)
			if (x[i] > maxValue) maxValue = x[i];
		return maxValue;
	}
}
