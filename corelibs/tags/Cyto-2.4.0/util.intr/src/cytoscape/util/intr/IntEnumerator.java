package cytoscape.util.intr;

/**
 * An enumeration over a list of 32 bit integers.
 */
public interface IntEnumerator
{

  /**
   * Returns a non-negative integer I such that nextInt() will successfully
   * return a value no more and no less than I times.
   */
  public int numRemaining();

  /**
   * Returns the next integer in the enumeration.
   * If numRemaining() returns a non-positive quantity before
   * nextInt() is called, the behavior of this enumeration becomes undefined.
   */
  public int nextInt();

}
