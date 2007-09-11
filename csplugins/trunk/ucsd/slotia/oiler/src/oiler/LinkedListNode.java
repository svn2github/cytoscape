package oiler;

import oiler.util.IntIterator;

/**
 * Internal data structure for LinkedListGraph
 *
 * @author Samad Lotia
 */

class LinkedListNode<N,E>
{
	public N			nodeObj;
	public int			directedIncomingCount;
	public int			directedOutgoingCount;
	public int			undirectedCount;
	public LinkedListEdge<E>	firstDirectedIncomingEdge;
	public LinkedListEdge<E>	firstDirectedOutgoingEdge;
	public LinkedListEdge<E>	firstUndirectedIncomingEdge;
	public LinkedListEdge<E>	firstUndirectedOutgoingEdge;
}
