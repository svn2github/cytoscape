package cytoscape.render.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

public final class TryEllipseBorder
{

  public static final void main(String[] args)
  {
    final Frame f = new Frame() {
        public final void paint(Graphics g) {
          Graphics2D g2d = (Graphics2D) g;
          g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
          g2d.setStroke(new BasicStroke(0.5f));
          g2d.setPaint(Color.red);
          g2d.draw(new Ellipse2D.Double(14.5, 38.4, 190.1, 139.7)); }
        public boolean handleEvent(Event evt) {
          if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
          return super.handleEvent(evt); } };
    f.resize(300, 200);
    f.show();
  }

}
