package cytoscape.process;

/**
 * A hook for stopping a running process and waiting for it to exit.
 * A process which this hook refers to can only be run once per instance of
 * this interface; once a process is stopped, it cannot magically
 * restart.  This implies that repeated calls to <code>stop()</code> will
 * have the same effect on the [running] process as a single call will.
 **/
public interface Stoppable
{

  /**
   * Guarantees that the process has exited by the time this method returns.
   * If <code>stop()</code> has been called and has returned before an
   * application has started the corresponding process, the process should
   * terminate immediately upon starting.  If a process has been started and
   * has finished executing before <code>stop()</code> is called, a
   * call to <code>stop()</code> should exit immediately.<p>
   * If a framework using this interface chooses to support multiple threads,
   * multiple concurrent calls to <code>stop()</code> should all block until
   * the underlying process finishes.<p>
   * The difference between <code>Haltable.halt()</code> and
   * <code>Stoppable.stop()</code> is that <code>halt()</code> is non-blocking;
   * <code>halt()</code> does not wait for a process to stop before it returns.
   **/
  public void stop();

}
