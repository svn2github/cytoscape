package cytoscape.render.export;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

/**
 * The primary purpose of this class is to enable an arbitrary Graphics
 * object to receive rendering calls from a
 * cytoscape.render.immed.GraphGraphics instance.  This, in turn, can be used
 * to render a graph directly to a scalar vector graphic.
 */
public final class ImageImposter extends Image
{

  private final Graphics m_graphics;
  private final int m_width;
  private final int m_height;

  /**
   * The Graphics object that is passed into this constructor is not
   * modified in any way, ever.  The only direct calls made on this object
   * are graphics.create(), which is used in the getGraphics() method
   * of this class.
   */
  public ImageImposter(final Graphics graphics,
                       final int width, final int height)
  {
    if (graphics == null) throw new NullPointerException
                              ("graphics is null");
    if (width <= 0) throw new IllegalArgumentException
                      ("width must be positive");
    if (height <= 0) throw new IllegalArgumentException
                       ("height must be positive");
    m_graphics = graphics;
    m_width = width;
    m_height = height;
  }

  /**
   * Simply returns the width that was passed into the constructor.
   */
  public final int getWidth(final ImageObserver observer)
  {
    return m_width;
  }

  /**
   * Simply returns the height that was passed into the constructor.
   */
  public final int getHeight(final ImageObserver observer)
  {
    return m_height;
  }

  /**
   * This method simply throws an UnsupportedOperationException.
   */
  public final ImageProducer getSource()
  {
    throw new UnsupportedOperationException
      ("this type of image does not store pixel data");
  }

  /**
   * Returns a copy of the Graphics object that was passed into the
   * constructor.  The exact value returned is graphics.create() where
   * graphics is the Graphics object passed to the constructor.
   */
  public final Graphics getGraphics()
  {
    return m_graphics.create();
  }

  /**
   * No properties are defined on this image, ever; this returns
   * UndefinedProperty, always.
   */
  public final Object getProperty(final String name,
                                  final ImageObserver observer)
  {
    return UndefinedProperty;
  }

  /**
   * This is a no-op.
   */
  public final void flush() { }

}
