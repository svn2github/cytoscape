package cytoscape.util.intr;

public final class IntBTree
{

  private final static class Node
  {

    private int deepCount;
    private int splitCount;
    private final int[] splits;

    // Exactly one of { children, values } is null, depending on whether or
    // not this is a leaf node.
    private final Node[] children;
    private final int[] values;

    private Node(int maxBranches, boolean leafNode)
    {
      deepCount = 0;
      splitCount = 0;
      splits = new int[maxBranches - 1];
      if (leafNode) {
        children = new Node[maxBranches];
        values = null; }
      else {
        children = null;
        values = new int[maxBranches]; }
    }

  }

}
