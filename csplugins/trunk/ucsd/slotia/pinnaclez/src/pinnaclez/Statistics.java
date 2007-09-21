package pinnaclez;

import cern.jet.stat.Gamma;

/**
 * A dumping ground for all statistical calculations.
 * @author Samad Lotia
 */
public class Statistics
{
	protected Statistics() {}

	/**
	 * Calculates sum of values.
	 * Formula in TeX: Given $x_i$, where $1 \le i \le N$, sum = $\sum^N_{i=1}=x_i$
	 */
	public static double sum(final double[] values)
	{
		double sum = 0.0f;
		for (int i = 0; i < values.length; i++)
			sum += values[i];
		return sum;
	}

	/**
	 * Calculates logarithmic sum of values.
	 * Formula in TeX: Given $x_i$, where $1 \le i \le N$, logsum = $\sum^N_{i=1}=\ln(x_i)$
	 */
	public static final double logsum(final double[] values)
	{
		double logsum = 0.0;
		for (int i = 0; i < values.length; i++)
			logsum += Math.log(values[i]);
		return logsum;
	}

	/**
	 * Calculates squaed sum of values.
	 * Formula in TeX: Given $x_i$, where $1 \le i \le N$, squaresum = $\sum^N_{i=1}=x_i^2$
	 */
	public static final double squaresum(final double[] values)
	{
		double squaresum = 0.0;
		for (int i = 0; i < values.length; i++)
			squaresum += (values[i] * values[i]);
		return squaresum;
	}

	/**
	 * Calculates the k parameter of a gamma distribution using maximum
	 * likelihood estimation.
	 * This calculation is from http://en.wikipedia.org/wiki/Gamma_distribution
	 * Formula in TeX: Given sum, logsum, and N:
	 * $s=\ln(\frac{sum}{N})-\frac{logsum}{N}$
	 * $\hat{k} \approx \frac{3-s+\sqrt{(s-3)^2+24s}}{12s}$
	 * @param sum The sum of the values in the distribution.
	 * @param logsum The logarithmic sum of the values in the distribution.
	 * @param N The number of values in the distribution.
	 */
	public static final double gammaKParamByMLE(final double sum, final double logsum, final int N)
	{
		final double s = Math.log(sum/N) - logsum/N;
		return 3.0-s + Math.sqrt((s-3.0)*(s-3.0) + 24.0*s) / (12.0*s);
	}

	/**
	 * Calculates the theta parameter of a gamma distribution using maximum
	 * likelihood estimation.
	 * This calculation is from http://en.wikipedia.org/wiki/Gamma_distribution
	 * Formula in TeX: Given sum, k, and N:
	 * $\hat{\theta}=\frac{sum}{kN}$
	 * @param sum The sum of the values in the distribution.
	 * @param k The k parameter of the gamma distribution.
	 * @param N The number of values in the distribution.
	 */
	public static final double gammaThetaParamByMLE(final double sum, final double k, final int N)
	{
		return sum/(k*N);
	}
	/**
	 * Calculates the k parameter of a gamma distribution using
	 * the method of moments.
	 * This calculation is from http://en.wikipedia.org/wiki/Gamma_distribution
	 * @param sum The sum of the values in the distribution.
	 * @param squaresum The squared sum of the values in the distribution.
	 * @param N The number of values in the distribution.
	 */
	public static final double gammaKParamByMoment(final double sum, final double squaresum, final int N)
	{
		final double m1 = sum / N;
		final double m2 = squaresum / N;
		return (m1 * m1) / (m2 - (m1 * m1));
	}

	/**
	 * Calculates the theta parameter of a gamma distribution using
	 * the method of moments.
	 * This calculation is from http://en.wikipedia.org/wiki/Gamma_distribution
	 * @param sum The sum of the values in the distribution.
	 * @param squaresum The squared sum of the values in the distribution.
	 * @param N The number of values in the distribution.
	 */
	public static final double gammaThetaParamByMoment(final double sum, final double squaresum, final int N)
	{
		final double m1 = sum / N;
		final double m2 = squaresum / N;
		return (m2 - (m1 * m1)) / m1;
	}

	/**
	 * Calculates gamma cumulative distribution function.
	 * This calculation is from http://en.wikipedia.org/wiki/Gamma_distribution
	 * Formula in TeX: Given x, k, and theta:
	 * $F(x;k,\theta)=\frac{\gamma(k,x/\theta)}{\Gamma(k)}$
	 */
	public static final double gammaCDF(final double x, final double k, final double theta)
	{
		if (Double.isInfinite(x))
			return 1.0f;
		if (Double.isInfinite(k) || Double.isInfinite(theta))
			return 0.0f;

		return Gamma.incompleteGamma(k, x / theta);
	}

	/**
	 * Estimates gamma distribution parameters from <code>sample</code>
	 * and returns the CDF based on these parameters and <code>value</code>.
	 * If <code>sample</code> contains any zeros, this will use the
	 * moment method to estimate parameters. Otherwise, it will use
	 * the MLE method.
	 */
	public static final double gammaCDF(final double value, final double[] sample)
	{
		final double sum = sum(sample);
		final double logsum = logsum(sample);
		if (Double.isNaN(logsum) || Double.isInfinite(logsum))
		{
			// Probably zeros in sample, switch to moments method
			final double squaresum = squaresum(sample);
			final double k = gammaKParamByMoment(sum, squaresum, sample.length);
			final double theta = gammaThetaParamByMoment(sum, squaresum, sample.length);
			return gammaCDF(value, k, theta);
		}
		else
		{
			// Use MLE, the default method
			final double k = gammaKParamByMLE(sum, logsum, sample.length);
			final double theta = gammaThetaParamByMLE(sum, k, sample.length);
			return gammaCDF(value, k, theta);
		}
	}

	/**
	 * Calculates the standard deviation.
	 */
	public static final double stddev(final double mean, final double[] values)
	{
		double stddev = 0.0f;
		for (int i = 0; i < values.length; i++)
		{
			final double delta = mean - values[i];
			stddev += delta * delta;
		}
		stddev /= (values.length - 1);
		return Math.sqrt(stddev);
	}

	public static final double SQRT_2 = Math.sqrt(2.0);

	/**
	 * Calculates gamma cumulative distribution function.
	 * This calculation is from http://en.wikipedia.org/wiki/Normal_distribution
	 * Formula in TeX:
	 * $F(x;\mu,\sigma)=\frac{1}{2}(1+erf(\frac{x-\mu}{\sigma \sqrt{2}}))$
	 */
	public static final double normalCDF(final double x, final double mean, final double stddev)
	{
		// Calculate the error function, where z is the parameter of the error function
		if (stddev == 0.0 || Double.isInfinite(mean) || Double.isInfinite(x))
			return Double.POSITIVE_INFINITY;
		if (Double.isInfinite(stddev))
			return 0.0;
		final double z = (x - mean) / (SQRT_2 * stddev);
		final double result = 0.5 + 0.5 * erf(z);

		// Cap the result from erf
		//   A SIDE NOTE: If erf() returns anything less than -1,
		//   cdf() should return 0; if erf returns anything larger
		//   than 1, cdf() should return 1.
		//   Proof:
		//     1. 0 <= cdf <= 1
		//     2. cdf = .5 * (1 + erf)
		//     3. 0 <= .5 * (1 + erf) <= 1
		//     4. -1 <= erf <= 1
		//   The current behavior is not like this.
		//   If cdf < 0 or cdf > 1, it will return 1.
		//   I checked this function against MATLAB, and it works how MATLAB does.
		//   Perhaps I'm wrong, and this needs to be checked.
		return (result < 0.0 || result > 1.0 ? 1.0 : result);
	}

	/**
	 * Estimates normal distribution parameters from <code>sample</code>
	 * and returns the CDF based on these parameters and <code>value</code>.
	 */
	public static final double normalCDF(final double value, final double[] sample)
	{
		if (value > 0.0 && Double.isInfinite(value))
			return 1.0;
		if (value < 0.0 && Double.isInfinite(value))
			return 0.0;
		final double mean = sum(sample) / sample.length;
		final double stddev = stddev(mean, sample);
		return normalCDF(value, mean, stddev);
	}

	public static final double SQRT_PI = (double) Math.sqrt(Math.PI);
	
	/**
	 * Calculates the error function.
	 * This calculation is from http://en.wikipedia.org/wiki/Error_function
	 * Formula in TeX:
	 * $erf(x) \approx \frac{2}{\sqrt{\pi}}(x-\frac{x^3}{3}-\frac{x^5}{10}-\frac{x^7}{42}-\frac{x^9}{216}+\frac{x^11}{1320})$
	 */
	public static final double erf(final double x)
	{
		final double x2  = x  * x;
		final double x3  = x  * x2;
		final double x5  = x3 * x2;
		final double x7  = x5 * x2;
		final double x9  = x7 * x2;
		final double x11 = x9 * x2;
		return 2.0 * (x - x3/3.0 + x5/10.0 - x7/42.0 + x9/216.0 - x11/1320.0) / SQRT_PI;
	}

	/**
	 * Calculates the Z score of a value in a normal distribution.
	 * @param value The value to calculate the Z score for
	 * @param mean The mean of the normal distribution
	 * @param stddev The standard deviation of the normal distribution
	 */
	public static final double zScore(final double value, final double mean, final double stddev)
	{
		return (value - mean) / stddev;
	}

	/**
	 * Calculates an empirical value for <code>x</code> in
	 * a sorted list of values of <code>sortedValues</code>.
	 * <i>WARNING:</i> <code>sortedValues</code> MUST BE SORTED
	 * IN ASCENDING ORDER!
	 * It can be sorted with the <code>java.utilArrays.sort()</code>
	 * method. If it is not sorted, incorrect values will be calculated.
	 *
	 * TODO: Since the array is sorted, there is a better
	 * way to do this through the quicksort algorithm.
	 */
	public static final double calculateEmpiricalPValue(final double x, final double[] sortedValues)
	{
		int i;
		for (i = 0; i < sortedValues.length; i++)
			if (sortedValues[i] > x)
				break;
		return ((double) sortedValues.length - i) / (sortedValues.length);
	}
}
