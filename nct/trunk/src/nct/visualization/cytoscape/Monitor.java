package nct.visualization.cytoscape;

public abstract class Monitor
{
  protected boolean needToHalt = false;

  public abstract void setPercentCompleted(int percent);
  public void halt()
    { needToHalt = true; }
  public boolean needToHalt()
    { return needToHalt; }
}
