package cytoscape.data.attr;

import java.util.Enumeration;

/**
 * A java.util.Enumeration with knowledge of how many elements are in the
 * enumeration.
 */
public interface CountedEnumeration extends Enumeration
{

  /**
   * Returns a non-negative integer I such that nextElement() will successfully
   * return a value no more and no less than I times.
   */
  public int numElementsRemaining();

}
