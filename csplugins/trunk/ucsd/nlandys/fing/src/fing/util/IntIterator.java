package fing.util;

public interface IntIterator
{

  /**
   * Returns the number of times that successive calls to nextInt() can
   * be made successfully.
   */
  public int numRemaining();

  /**
   * If numRemaining() returns a non-positive quantity before
   * nextInt() is called, the behavior of nextInt() is undefined.
   */
  public int nextInt();

}
