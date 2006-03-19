package ding.view;

public interface ViewportChangeListener
{

  /**
   * This gets fired upon graph redraw when zoom is changed or the graph
   * center is changed.
   */
  public void viewportChanged(double newXCenter, double newYCenter,
                              double newScaleFactor);

}
