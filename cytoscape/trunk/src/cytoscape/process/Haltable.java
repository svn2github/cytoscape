package cytoscape.process;

/**
 * A hook for requesting that a running process be halted.
 * A process which this hook refers to can only be run once per instance of
 * this interface; once a process is stopped, it cannot magically
 * restart.  This implies that repeated calls to <code>halt()</code> will
 * have the same effect on the [running] process as a single call will.
 **/
public interface Haltable
{

  /**
   * Requests that a running process be aborted.  <code>halt()</code> does
   * not block; it returns quickly; it is likely that <code>halt()</code>
   * will return before an underlying running process exits.
   * If <code>halt()</code> is called before an underlying process is started,
   * that underlying process should abort immediately if it is ever started.
   * If an underlying process has been started and has finished executing
   * before <code>halt()</code> is called, <code>halt()</code> will have no
   * effect.
   */
  public void halt();

}
