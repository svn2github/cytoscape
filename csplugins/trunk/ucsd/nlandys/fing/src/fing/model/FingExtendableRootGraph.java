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

  public FingExtendableRootGraph(FingNodeDepot nodeDepot,
                                 FingEdgeDepot edgeDepot)
  {
    super(nodeDepot, edgeDepot);
  }

}
