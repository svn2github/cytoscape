package clusterMaker.algorithms.numeric;

public class PrimitiveMedianSummarizer implements PrimitiveSummarizer {
	@Override
	public double summarize(double[] a) {
		return Numeric.median(a);
	}
}
