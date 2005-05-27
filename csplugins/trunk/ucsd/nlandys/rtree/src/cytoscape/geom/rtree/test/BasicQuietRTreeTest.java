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
    double[] extentsArr = new double[4];
    for (int i = 0; i < 3; i++)
      if (!tree.exists(i, extentsArr, 0))
        throw new IllegalStateException("entry " + i + " does not exist");
    if (tree.exists(3, extentsArr, 0))
      throw new IllegalStateException("entry 3 exits");
    if (extentsArr[0] != 0.5 || extentsArr[1] != 1.0 ||
        extentsArr[2] != 1.5 || extentsArr[3] != 2.0)
      throw new IllegalStateException("entry's extents don't match");
  }

}
