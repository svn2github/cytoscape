package fing.model;

/**
 * Please try to restrain from using this class, or even looking at it.
 * This class was created so that certain legacy applications would have an
 * easier time using this giny.model implementation.
 * @deprecated Use FingRootGraphFactory and ignore this class.
 * @see FingRootGraphFactory
 **/
public interface FingNodeDepot
{

  /**
   * This method tells the Fing implementation whether or not nodes
   * are recyclable.  If Node [FingNode] objects are recyclable, then
   * recycleNode(FingNode) will be called by the internal Fing implementation,
   * otherwise not.
   **/
  public boolean isRecyclery();

  /**
   * This either instantiates a new node or gets one from the recylery.
   **/
  public FingNode getNode();

  /**
   * Recycles a node -- only called if this FingNodeDepot.isRecyclery().
   **/
  public void recycleNode(FingNode node);

}
