package cytoscape.process;

/**
 * Represents a [lengthy] task that can be stopped.
 **/
public interface Stoppable
{

  /**
   * Guarantees that the task is stopped by the time this method returns.
   * Not sure what meaning and consequence <code>RuntimeException</code>s
   * have; will add documentation when these ideas mature; right now nothing
   * seems to implement this interface.  Maybe some sort
   * of exception could mean that a task could not be stopped?
   **/
  public void stop();

}
