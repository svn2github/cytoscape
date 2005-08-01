package cytoscape.render.test;

import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Ellipse2D;

public final class TryBezierDisk
{

  public static final void main(String[] args)
  {
    final Frame f = new Frame() {
        public final void paint(Graphics g) {
          Graphics2D g2d = (Graphics2D) g;
          g2d.setBackground(Color.white);
          g2d.clearRect(0, 0, 400, 400);
          g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                               RenderingHints.VALUE_ANTIALIAS_ON);
          g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
          g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                               RenderingHints.VALUE_FRACTIONALMETRICS_ON);
          g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                               RenderingHints.VALUE_STROKE_PURE);
          GeneralPath path = new GeneralPath();
          double a = 4.0d * (Math.sqrt(2.0d) - 1.0d) / 3.0d;
          path.moveTo(200, 100);
          path.curveTo((float) (200.0d - 100.0d * a), 100,
                       100, (float) (200.0d - 100.0d * a),
                       100, 200);
          path.curveTo(100, (float) (200.0d + 100.0d * a),
                       (float) (200.0d - 100.0d * a), 300,
                       200, 300);
          path.quadTo(300, 300, 300, 200);
          path.quadTo(300, 100, 200, 100);
          g2d.setColor(Color.black);
          g2d.fill(path);
          Ellipse2D rect = new Ellipse2D.Float(100, 100, 200, 200);
          g2d.setColor(Color.red);
          g2d.fill(rect); }
        public boolean handleEvent(Event evt) {
          if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
          return super.handleEvent(evt); } };
    f.resize(400, 400);
    f.show();
  }

}
