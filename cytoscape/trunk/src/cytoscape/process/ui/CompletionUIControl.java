package cytoscape.process.ui;

public interface CompletionUIControl
{

  /**
   * This is a hook to set the percent completed in a progress bar UI.
   *
   * @param value represents percent completed of a task - must
   *   be in the range <nobr><code>[0.0, 1.0]</code></nobr>.
   * @exception IllegalArgumentException if <code>percent</code> is not in
   *   the interval <nobr><code>[0.0, 1.0]</code></nobr>.
   **/
  public void setPercentCompleted(double percent);

}
