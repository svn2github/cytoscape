package cytoscape.util.intr;

/**
 * An iteration over a list of 32 bit integers.
 */
public interface IntIterator
{

  /**
   * Returns true if and only if nextInt() will successfully return a
   * value.
   */
  public boolean hasNext();

  /**
   * Returns the next integer in the iteration.  If hasNext() returns false
   * before nextInt() is called, the behavior of this iteration becomes
   * undefined.
   */
  public int nextInt();

}
