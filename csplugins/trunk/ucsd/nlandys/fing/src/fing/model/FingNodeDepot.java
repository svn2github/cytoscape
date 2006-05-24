package fing.model;

import giny.model.Node;
import giny.model.RootGraph;

/**
 * Please try to restrain from using this class, or even looking at it.
 * This class was created so that certain legacy applications would have an
 * easier time using this giny.model implementation.  Please use
 * FingRootGraphFactory instead of this class.
 * @see FingRootGraphFactory
 **/
public interface FingNodeDepot
{

  /**
   * This either instantiates a new node or gets one from the recyclery,
   * initializing it with the parameters specified.
   **/
  public Node getNode(RootGraph root, int index, String id);

  /**
   * Recycles a node.  Implementations may choose to do nothing in this
   * method and instantiate a new node in each call to getNode().  This method
   * is simply a hook for Fing to tell the depository "I'm done using this node
   * object -- it's no longer part of a RootGraph".
   **/
  public void recycleNode(Node node);

}
