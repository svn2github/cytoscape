package cytoscape.render.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;

public final class TryLineDrawingSpeed
{

  public static final void main(String[] args)
  {
    final Frame f = new Frame() {
        public final void paint(Graphics g) {
        }
        public boolean handleEvent(Event evt) {
          if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
          return super.handleEvent(evt); } };
    f.resize(600, 600);
    f.show();
  }

}
