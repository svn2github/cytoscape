package oiler;

/**
 * An interface for edge objects that have weights.
 * This interface provides a means for guaranteeing weighted edges for
 * edge objects.
 *
 * @author Samad Lotia
 */
public interface WeightedEdge extends Comparable<WeightedEdge>
{
	public double weight();
}
