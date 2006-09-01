package cytoscape.render.test;

import cytoscape.render.immed.GraphGraphics;

public final class TestEdgeIntersectionComputationPerformance
{

  public final static void main(String[] args)
  {
    final byte nodeShape =
      args.length > 0 ? Byte.parseByte(args[0]) :
      GraphGraphics.SHAPE_RECTANGLE;
    final float offset =
      args.length > 1 ? Float.parseFloat(args[1]) : 0.0f;
    final float nodeYMin = 0.0f;
    final float nodeYMax = 1.0f;
    float nodeXMin = -1000.0f;
    float nodeXMax = -999.2f;
    final float ptX = 0.0f;
    final float ptY = 1000.0f;
    final GraphGraphics gg = new GraphGraphics(null, false);
    final float[] xsectBuff = new float[2];
    final long timeBegin = System.currentTimeMillis();
    final int numIntersections = 1000;
    for (int i = 0; i < numIntersections; i++) {
      final boolean result = gg.computeEdgeIntersection
        (nodeShape,
         nodeXMin, nodeYMin, nodeXMax, nodeYMax,
         offset, ptX, ptY, xsectBuff);
      nodeXMin += 2.0f;
      nodeXMax += 2.0f; }
    final long timeEnd = System.currentTimeMillis();
    System.out.println(numIntersections + " intersections took " +
                       (timeEnd - timeBegin) + " milliseconds");
  }

}
