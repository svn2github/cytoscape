package cytoscape.render.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

public final class DemonstrateBadLines
{

  public static final void main(String[] args)
  {
    final Frame f = new Frame() {
        public final void paint(Graphics g) {
          Graphics2D g2d = (Graphics2D) g;
          g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
          g2d.setStroke(new BasicStroke(0.5f));
          final double x1 = 22.388;
          final double y1 = 147.847;
          final double x2 = 162.904;
          final double y2 = 72.491;
          g2d.setPaint(Color.red);
          g2d.draw(new Line2D.Double(x1, y1, x2, y2));
          g2d.setPaint(Color.blue);
          g2d.draw(new Line2D.Double(x1 + (x2 - x1) * 0.33333,
                                     y1 + (y2 - y1) * 0.33333,
                                     x2, y2)); }
        public boolean handleEvent(Event evt) {
          if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
          return super.handleEvent(evt); } };
    f.resize(300, 200);
    f.show();
  }

}
