package cytoscape.graph.layout.impl;

import com.nerius.math.geom.Point3D;
import com.nerius.math.xform.AffineTransform3D;
import com.nerius.math.xform.AxisRotation3D;
import com.nerius.math.xform.Scale3D;
import com.nerius.math.xform.Translation3D;
import cytoscape.graph.layout.algorithm.MutableGraphLayout;

public final class RotationLayouter
{

  // No constructor.
  private RotationLayouter() {}

  public static void rotateGraph(MutableGraphLayout graph,
                                 double radians)
  {
    double xMin = Double.MAX_VALUE; double xMax = Double.MIN_VALUE;
    double yMin = Double.MAX_VALUE; double yMax = Double.MIN_VALUE;
    for (int i = 0; i < graph.getNumNodes(); i++)
    {
      if (!graph.isMovableNode(i)) continue;
      double nodeXPosition = graph.getNodePosition(i, true);
      double nodeYPosition = graph.getNodePosition(i, false);
      xMin = Math.min(xMin, nodeXPosition);
      xMax = Math.max(xMax, nodeXPosition);
      yMin = Math.min(yMin, nodeYPosition);
      yMax = Math.max(yMax, nodeYPosition);
    }
    if (xMax < 0) return; // No nodes are movable.
    final double xRectCenter = (xMin + xMax) / 2.0d;
    final double yRectCenter = (yMin + yMax) / 2.0d;
    final Translation3D toOrig =
      new Translation3D(-xRectCenter, -yRectCenter, 0.0d);
    final AxisRotation3D rotation =
      new AxisRotation3D(AxisRotation3D.Z_AXIS, radians);
    final Translation3D fromOrig =
      new Translation3D(xRectCenter, yRectCenter, 0.0d);
    AffineTransform3D tentativeTransform =
      toOrig.concatenatePost(rotation.concatenatePost(fromOrig));
    Point3D[] boundaryPoints = new Point3D[] {
      new Point3D(xMin, yMin, 0.0d),
      new Point3D(xMin, yMax, 0.0d),
      new Point3D(xMax, yMax, 0.0d),
      new Point3D(xMax, yMin, 0.0d) };
    xMin = Double.MAX_VALUE; xMax = Double.MIN_VALUE;
    yMin = Double.MAX_VALUE; yMax = Double.MIN_VALUE;
    for (int i = 0; i < boundaryPoints.length; i++) {
      Point3D p = tentativeTransform.transform(boundaryPoints[i]);
      xMin = Math.min(xMin, p.x); xMax = Math.max(xMax, p.x);
      yMin = Math.min(yMin, p.y); yMax = Math.max(yMax, p.y); }
    double scaleFactor = 1.0d;
    if (xMin < 0.0d)
      scaleFactor = (xRectCenter - 0.0d) / (xRectCenter - xMin);
    if (xMax > graph.getMaxWidth())
      scaleFactor = Math.min(scaleFactor,
                             (xRectCenter - graph.getMaxWidth()) /
                             (xRectCenter - xMax));
    if (yMin < 0.0d)
      scaleFactor = Math.min(scaleFactor,
                             (yRectCenter - 0.0d) / (yRectCenter - yMin));
    if (yMax > graph.getMaxHeight())
      scaleFactor = Math.min(scaleFactor,
                             (yRectCenter - graph.getMaxHeight()) /
                             (yRectCenter - yMax));
    final Scale3D scale = new Scale3D(scaleFactor, scaleFactor, 1.0d);

    // Finally, we've found the transform we're going to use to move
    // nodes.  Everything up to now was just a calculation to arrive at
    // a suitable realTransform.
    final AffineTransform3D realTransform =
      toOrig.concatenatePost(rotation.concatenatePost(scale.concatenatePost
                                                      (fromOrig)));

    final double[] pointBuff = new double[3];
    for (int i = 0; i < graph.getNumNodes(); i++)
    {
      if (!graph.isMovableNode(i)) continue;
      pointBuff[0] = graph.getNodePosition(i, true);
      pointBuff[1] = graph.getNodePosition(i, false);
      realTransform.transformArr(pointBuff);
      // If we later find that, because of floating-point rounding errors,
      // some moved points fall outside of boundary, then we should adjust
      // our scale transform to make it shrink the graph by epsilon more.
      graph.setNodePosition(i, pointBuff[0], pointBuff[1]);
    }
  }

}
