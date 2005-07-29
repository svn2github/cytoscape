package cytoscape.geom.rtree;

import cytoscape.util.intr.IntEnumerator;

public interface RTreeEntryEnumerator extends IntEnumerator
{

  public void nextMBR(float[] mbr);

}
