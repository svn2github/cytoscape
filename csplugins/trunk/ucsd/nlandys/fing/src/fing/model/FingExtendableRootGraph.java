package fing.model;

/**
 * Please try to restrain from using this class.  This class was created so
 * that certain legacy applications would have an easier time using this
 * giny.model implementation.
 * @deprecated Use FingRootGraphFactory instead.
 * @see FingRootGraphFactory
 **/
public class FingExtendableRootGraph extends FRootGraph
{

  public FingExtendableRootGraph(FingNodeDepot nodeDepot) { }

//   /**
//    * This method is here so that subclasses can gain control of the exact
//    * Node and Edge object that are used by this graph system.
//    **/
//   protected Node createNode(int nodeInx)
//   {
//   }

//   /**
//    * This method is here so that subclasses can gain control of the exact
//    * Node and Edge object that are used by this graph system.
//    **/
//   protected Edge createEdge(int edgeInx)
//   {
//   }

}
