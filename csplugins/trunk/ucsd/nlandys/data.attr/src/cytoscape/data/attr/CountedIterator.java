package cytoscape.data.attr;

import java.util.Iterator;

/**
 * A java.util.Iterator with knowledge of how many elements are remaining.
 */
public interface CountedIterator extends Iterator
{

  /**
   * Returns a non-negative integer I such that next() will successfully
   * return a value no more and no less than I times.
   */
  public int numRemaining();

}
