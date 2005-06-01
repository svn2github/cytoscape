package cytoscape.render.immed;

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

  /**
   * The image that was passed into the constructor.
   */
  public final Image image;

  private Graphics2D g2d = null;

  /**
   * This constructor does not necessarily need to be called from the
   * AWT event handling thread.
   * @param img an off-screen image (an image gotten via the call
   *   java.awt.Component.createImage(int, int)).
   */
  public GraphGraphics(final Image image)
  {
    if (img == null) throw new NullPointerException("image is null");
    this.image = image;
  }

  /**
   * This must be called to let the renderer know when drawing operations
   * are about to be performed.  The sequence of calls will look like this:
   * <blockquote>
   * startFrame()<br />
   * [rendering operation 1]<br />
   * [rendering operation 2]<br />
   * ...<br />
   * finishFrame()<br />
   * </blockquote>
   * Thereafter, startFrame() may be called again to start another frame.<p>
   * This method must be called from the AWT event handling thread.
   * @exception IllegalThreadStateException if the calling thread isn't the
   *   AWT event handling thread.
   */
  public void startFrame()
  {
    if (s_threadDebug && !EventQueue.isDispatchThread())
      throw new IllegalThreadStateException
        ("calling thread is not AWT event dispatcher");
    if (g2d != null) throw new IllegalStateException
                       ("previous frame was not finished");
    g2d = (Graphics2D) image.getGraphics();
  }

  /**
   * Tells the renderer when rendering operations for one frame are done.
   * The renderer then disposes of resources used during the rendering
   * process.<p>
   * This method must be called from the AWT event handling thread.
   */
  public void finishFrame();

  /**
   * Clears image area and makes it transparent.  This is a rendering
   * operation.<p>
   * This method must be called from the AWT event handling thread.
   */
  public void clear();

  public void hiDrawNode(byte shape, double width, double height,
                         double xPos, double yPos, int fillColorRGB,
                         byte borderType, double borderWidth,
                         int borderColorRGB);

  public void loDrawNode(double width, double height, double xPos, double yPos,
                         int fillColorRGB);

}
