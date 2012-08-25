package clusterMaker.algorithms.numeric;

/**
 * Summarizer interface for the Double type.
 * Doubly ugly because Java's Generic does not support primitive types.
 * @author djh.shih
 *
 */
public interface PrimitiveSummarizer {
	double summarize(double[] a);
}
