package clusterMaker.algorithms.numeric;

public class MedianSummarizer implements Summarizer {
	@Override
	public Double summarize(Double[] a) {
		return Numeric.median(a);
	}
}
