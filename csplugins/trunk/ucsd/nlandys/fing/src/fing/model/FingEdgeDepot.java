package fing.model;

/**
 * Please try to restrain from using this class, or even looking at it.
 * This class was created so that certain legacy applications would have an
 * easier time using this giny.model implementation.
 * @deprecated Use FingRootGraphFactory and ignore this class.
 * @see FingRootGraphFactory
 **/
public interface FingEdgeDepot
{

  /**
   * This either instantiates a new edge or gets one from the recyclery.
   **/
  public FingEdge getEdge();

  /**
   * Recycles an edge.  Implementations may choose to do nothing in this
   * method and instantiate a new edge in each call to getEdge().  This method
   * is simply a hook for Fing to tell the depository "I'm done using this edge
   * object -- it's no longer part of a RootGraph".
   **/
  public void recycleEdge(FingEdge node);

}
