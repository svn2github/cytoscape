package clusterMaker.algorithms.numeric;

public class PrimitiveMeanSummarizer implements PrimitiveSummarizer {
	@Override
	public double summarize(double[] a) {
		return Numeric.mean(a);
	}
}
