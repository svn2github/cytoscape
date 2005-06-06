package cytoscape.render.test;

import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.Random;

public final class TryLineDrawingSpeed
{

  public static final void main(String[] args)
  {
    final int imgW = 600;
    final int imgH = 600;
    final int[] extents;

    {
      int N = Integer.parseInt(args[0]);
      extents = new int[N * 4];
      int inx = 0;
      Random r = new Random();
      while (inx < N) {
        int nonnegative = 0x7fffffff & r.nextInt();
        int x0 = nonnegative % imgW;
        nonnegative = 0x7fffffff & r.nextInt();
        int y0 = nonnegative % imgH;
        nonnegative = 0x7fffffff & r.nextInt();
        int x1 = nonnegative % imgW;
        nonnegative = 0x7fffffff & r.nextInt();
        int y1 = nonnegative % imgH;
        extents[inx * 4] = x0;
        extents[(inx * 4) + 1] = y0;
        extents[(inx * 4) + 2] = x1;
        extents[(inx * 4) + 3] = y1;
        inx++; }
    }

    final Frame f = new Frame() {
        public final void paint(Graphics g) {
          g.setColor(Color.black);
          for (int i = 0; i < extents.length;) {
            g.drawLine(extents[i++], extents[i++],
                       extents[i++], extents[i++]); }
          repaint(); }
        public boolean handleEvent(Event evt) {
          if (evt.id == Event.WINDOW_DESTROY) System.exit(0);
          return super.handleEvent(evt); } };
    f.resize(600, 600);
    f.show();
  }

}
