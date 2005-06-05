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
          xform.setToScale(1000.0d, 1000.0d);
          g2d.transform(xform);
          g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
          g2d.setStroke(new BasicStroke(0.002f));
          g2d.setPaint(Color.red);
          g2d.draw(new Ellipse2D.Double(0.0145d, 0.0384d, 0.1901d, 0.0597d)); }
        public boolean handleEvent(Event evt) {
          if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
          return super.handleEvent(evt); } };
    f.resize(300, 200);
    f.show();
  }

}
