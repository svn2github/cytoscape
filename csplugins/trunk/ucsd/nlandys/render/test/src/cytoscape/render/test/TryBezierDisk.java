package cytoscape.render.test;

import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;

public final class TryBezierDisk
{

  public static final void main(String[] args)
  {
    final Frame f = new Frame() {
        public final void paint(Graphics g) {
          Graphics2D g2d = (Graphics2D) g;
          GeneralPath path = new GeneralPath();
          path.moveTo(200, 100);
          path.quadTo(100, 100, 100, 200);
          path.quadTo(100, 300, 200, 300);
          path.quadTo(300, 300, 300, 200);
          path.quadTo(300, 100, 200, 100);
          g2d.setColor(Color.black);
          g2d.fill(path); }
        public boolean handleEvent(Event evt) {
          if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
          return super.handleEvent(evt); } };
    f.resize(400, 400);
    f.show();
  }

}
