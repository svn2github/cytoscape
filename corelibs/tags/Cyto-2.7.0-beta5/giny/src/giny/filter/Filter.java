package giny.filter;

public interface Filter {

  /**
   * Determine whether the object passes the filter.
   * @return true iff the given Object passes the filter.
   */
  public boolean passesFilter ( Object o );

}
