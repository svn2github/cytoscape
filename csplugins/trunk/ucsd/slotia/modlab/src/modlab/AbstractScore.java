package modlab;

import oiler.Graph;

/**
 * A convenience class for <code>Score</code> implementations.
 * <p>See <code>Score</code> for more details.</p>
 */
public abstract class AbstractScore<N,E> implements Score<N,E>
{
	public double scoreNode(Graph<N,E> network, int node)
	{
		throw new UnsupportedOperationException();
	}

	public double scoreEdge(Graph<N,E> network, int edge)
	{
		throw new UnsupportedOperationException();
	}

	public double scoreModule(Graph<N,E> module)
	{
		throw new UnsupportedOperationException();
	}
}
