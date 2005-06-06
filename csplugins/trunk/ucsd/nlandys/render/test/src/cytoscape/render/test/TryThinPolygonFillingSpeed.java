package cytoscape.render.test;

import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.util.Random;

public final class TryThinPolygonFillingSpeed
{

  private final static int FLAG_ANTIALIAS = 1;
  private final static int FLAG_TRANSFORM = 2;
  private final static int FLAG_DOUBLE_BUFFER = 4;

  public static final void main(String[] args)
  {
    final int imgW = 600;
    final int imgH = 600;
    final float[] extents;
    final int flags;

    {
      int N = Integer.parseInt(args[0]);
      if (args.length > 1) flags = Integer.parseInt(args[1]);
      else flags = 0;
      extents = new float[N * 8];
      int inx = 0;
      Random r = new Random();
      double lineWidth = 2.0d;
      while (inx < N) {
        int nonnegative = 0x7fffffff & r.nextInt();
        double x0 =
          ((double) nonnegative) / ((double) 0x7fffffff) * ((double) imgW);
        nonnegative = 0x7fffffff & r.nextInt();
        double y0 =
          ((double) nonnegative) / ((double) 0x7fffffff) * ((double) imgH);
        nonnegative = 0x7fffffff & r.nextInt();
        double x1 =
          ((double) nonnegative) / ((double) 0x7fffffff) * ((double) imgW);
        nonnegative = 0x7fffffff & r.nextInt();
        double y1 =
          ((double) nonnegative) / ((double) 0x7fffffff) * ((double) imgH);
        double xOff = x0 - x1;
        double yOff = y0 - y1;
        double len = Math.sqrt(xOff * xOff + yOff * yOff);
        extents[inx * 8] = (float) (x0 + yOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 1] = (float) (y0 - xOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 2] = (float) (x0 - yOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 3] = (float) (y0 + xOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 4] = (float) (x1 - yOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 5] = (float) (y1 + xOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 6] = (float) (x1 + yOff / (len * 2.0d) * lineWidth);
        extents[inx * 8 + 7] = (float) (y1 - xOff / (len * 2.0d) * lineWidth);
        inx++; }
    }

    final GeneralPath poly = new GeneralPath();
    final Frame f = new Frame() {
        public final void paint(Graphics g) {
          Graphics2D g2 = (Graphics2D) g;
          if ((flags & FLAG_ANTIALIAS) != 0)
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
          g.setColor(Color.black);
          for (int i = 0; i < extents.length; i += 8) {
            poly.reset();
            poly.moveTo(extents[i], extents[i + 1]);
            poly.lineTo(extents[i + 2], extents[i + 3]);
            poly.lineTo(extents[i + 4], extents[i + 5]);
            poly.lineTo(extents[i + 6], extents[i + 7]);
            poly.closePath();
            g2.fill(poly); }
          //repaint();
        }
        public boolean handleEvent(Event evt) {
          if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
          return super.handleEvent(evt); } };
    f.resize(600, 600);
    f.show();
  }

}
