package cytoscape.util.intr;

public final class IntBTree
{

  private final static class Node
  {

    private int sliceCount;

    // Exactly one of { values, data } is null, depending on whether or not
    // this is a leaf node.
    private final int[] values;
    private final InnerNodeData data;

    private Node(int maxBranches, boolean leafNode)
    {
      sliceCount = 0;
      if (leafNode) {
        values = new int[maxBranches];
        data = null; }
      else {
        values = null;
        data = new InnerNodeData(maxBranches); }
    }

  }

  private final static class InnerNodeData
  {

    private int deepCount;
    private final int[] splitVals;
    private final Node[] children;

    private InnerNodeData(int maxBranches)
    {
      deepCount = 0;
      splitVals = new int[maxBranches - 1];
      children = new Node[maxBranches];
    }

  }

}
