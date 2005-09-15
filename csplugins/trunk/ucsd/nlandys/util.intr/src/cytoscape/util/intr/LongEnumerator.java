package cytoscape.util.intr;

/**
 * An enumeration over a list of 64 bit integers.
 */
public interface LongEnumerator
{

  /**
   * Returns a non-negative integer I such that nextLong() will successfully
   * return a value no more and no less than I times.
   */
  public int numRemaining();

  /**
   * Returns the next 64 bit integer in the enumeration.
   * If numRemaining() returns a non-positive quantity before
   * nextLong() is called, the behavior of this enumeration becomes undefined.
   */
  public long nextLong();

}
