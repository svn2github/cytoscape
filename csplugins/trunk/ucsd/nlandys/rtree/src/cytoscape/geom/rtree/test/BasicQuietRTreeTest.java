package cytoscape.geom.rtree.test;

import cytoscape.geom.rtree.RTree;

public class BasicQuietRTreeTest
{

  public static void main(String[] args)
  {
    RTree tree = new RTree(3);
    tree.insert(0, 0.0, 0.0, 1.0, 1.0);
    tree.insert(1, 2.0, 2.0, 3.0, 3.0);
    tree.insert(2, 0.5, 1.0, 1.5, 2.0);
    // Still before any split.
  }

}
