package cytoscape.process;

/**
 * Represents a [lengthy] task that can be stopped.
 **/
public interface Stoppable
{

  /**
   * Guarantees that the task is stopped by the time this method returns.
   * Not sure what meaning and consequence a thrown
   * <code>RuntimeException</code> has;
   * will add documentation when these ideas mature.  Maybe some sort
   * of exception could mean that a task could not be stopped?<p>
   * The difference between <code>Haltable.halt()</code> and
   * <code>Stoppable.stop()</code> is that <code>halt()</code> is non-blocking;
   * <code>halt()</code> does not wait for a process to stop before it returns.
   **/
  public void stop();

}
