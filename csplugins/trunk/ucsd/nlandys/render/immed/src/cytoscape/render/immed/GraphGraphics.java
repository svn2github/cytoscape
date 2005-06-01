package cytoscape.render.immed;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * This is functional programming at it's finest [sarcasm].
 * The purpose of this class is to make the proper calls on a Graphics2D
 * object to efficiently render nodes, labels, and edges.
 */
public final class GraphGraphics
{

  public static final byte SHAPE_DIAMOND = 0;
  public static final byte SHAPE_ELLIPSE = 1;
  public static final byte SHAPE_HEXAGON = 2;
  public static final byte SHAPE_OCTAGON = 3;
  public static final byte SHAPE_PARALLELOGRAM = 4;
  public static final byte SHAPE_RECTANGLE = 5;
  public static final byte SHAPE_TRIANGLE = 6;

  public static final byte BORDER_DASHED = 0;
  public static final byte BORDER_NONE = 1;
  public static final byte BORDER_SOLID = 2;

  private static final boolean s_threadDebug = true;
  private static final Color s_transparent = new Color(0, 0, 0, 0);

  /**
   * The image that was passed into the constructor.
   */
  public final Image image;

  private Graphics2D g2d;

  /**
   * This constructor needs to be called from the AWT event handling thread.
   * @param img an off-screen image (an image gotten via the call
   *   java.awt.Component.createImage(int, int)).
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   */
  public GraphGraphics(final Image image)
  {
    this.image = image;
    this.clear();
  }

  /**
   * Clears image area and makes it transparent.  This is a rendering
   * operation.  It is healthy to call this method right before starting
   * to render a new picture.  Don't try to be clever in not calling this
   * method.<p>
   * This method must be called from the AWT event handling thread.
   */
  public void clear()
  {
    if (s_threadDebug && !EventQueue.isDispatchThread())
      throw new IllegalStateException
        ("calling thread is not AWT event dispatcher");
    g2d = (Graphics2D) image.getGraphics();
    g2d.setBackground(s_transparent);
    g2d.clearRect(0, 0, image.getWidth(null), image.getHeight(null));
  }

  public void drawNodeFull(byte shape, double width, double height,
                           double xPos, double yPos, int fillColorRGB,
                           byte borderType, double borderWidth,
                           int borderColorRGB)
  {
  }

  public void drawNodeLow(double width, double height, double xPos,
                          double yPos, int fillColorRGB)
  {
  }

}
