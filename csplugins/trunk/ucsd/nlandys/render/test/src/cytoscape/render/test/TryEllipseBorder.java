package cytoscape.render.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public final class TryEllipseBorder
{

  public static final void main(String[] args)
  {
    final Frame f = new Frame() {
        public final void paint(Graphics g) {
          Graphics2D g2d = (Graphics2D) g;
          AffineTransform xform = new AffineTransform();
          final double scaleFactor = 1000.0d; // If you set this to 100.0, the
                                              // ellipse will become smooth.
          xform.setToScale(scaleFactor, scaleFactor);
          g2d.transform(xform);
          g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
          g2d.setStroke(new BasicStroke((float) (2.0d / scaleFactor)));
          g2d.setPaint(Color.red);
          g2d.draw(new Ellipse2D.Double
                   (14.5d / scaleFactor, 38.4d / scaleFactor,
                    190.1d / scaleFactor, 59.7d / scaleFactor)); }
        public boolean handleEvent(Event evt) {
          if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
          return super.handleEvent(evt); } };
    f.resize(230, 120);
    f.show();
  }

}
