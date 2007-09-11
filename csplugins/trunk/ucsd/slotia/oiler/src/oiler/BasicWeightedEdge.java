package oiler;

/**
 * A minimal implementation of <code>WeightedEdge</code>.
 *
 * @author Samad Lotia
 */
public class BasicWeightedEdge implements WeightedEdge
{
	protected double weight;

	public BasicWeightedEdge(final double weight)
	{
		this.weight = weight;
	}

	public final double weight()
	{
		return weight;
	}

	public final String toString()
	{
		return Double.toString(weight);
	}

	public final boolean equals(final Object that)
	{
		if (this == that)
			return true;
		if (!(that instanceof WeightedEdge))
			return false;
		WeightedEdge thatEdge = (WeightedEdge) that;
		return (Double.compare(weight, thatEdge.weight()) == 0);
	}

	public final int compareTo(final WeightedEdge that)
	{
		return Double.compare(this.weight, that.weight());
	}
}
