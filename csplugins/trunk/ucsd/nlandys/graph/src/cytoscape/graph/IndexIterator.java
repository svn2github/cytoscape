package cytoscape.graph;

import java.util.NoSuchElementException;

/**
 * An iterator over a set of indices.
 **/
public interface IndexIterator
{
 
  /**
   * Returns the next index in the iteration.
   * @return the next index in the iteration.
   * @exception NoSUchElementException if the iteration has no more
   *   indices.
   **/
  public int next();

  /**
   * Returns an integer I such that <code>next()</code> will successfully
   * return a value no more and no less that I times.  Negative values are
   * never returned.
   **/
  public int numRemaining();

}
