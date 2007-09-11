package oiler;

/**
 * Internal data structure for LinkedListGraph
 *
 * @author Samad Lotia
 */
class LinkedListEdge<E>
{
	public int			edgeIndex;
	public E			edgeObj;
	public int			sourceIndex;
	public int			targetIndex;
	public byte			edgeType;
	public LinkedListEdge<E>	nextIncomingEdge;
	public LinkedListEdge<E>	nextOutgoingEdge;
}
