package cytoscape.graph.layout.impl;

import com.nerius.math.geom.Point3D;
import com.nerius.math.xform.AffineTransform3D;
import com.nerius.math.xform.AxisRotation3D;
import com.nerius.math.xform.Scale3D;
import com.nerius.math.xform.Translation3D;
import cytoscape.graph.layout.algorithm.MutableGraphLayout;
import java.awt.geom.Point2D;

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
      Point2D nodePosition = graph.getNodePosition(i);
      xMin = Math.min(xMin, nodePosition.getX());
      xMax = Math.max(xMax, nodePosition.getX());
      yMin = Math.min(yMin, nodePosition.getY());
      yMax = Math.max(yMax, nodePosition.getY());
    }
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
    scaleFactor = Math.min(scaleFactor,
                           (xRectCenter - 0.0d) / (xRectCenter - xMin));
    scaleFactor = Math.min(scaleFactor,
                           (xRectCenter - graph.getMaxWidth()) /
                           (xRectCenter - xMax));
    scaleFactor = Math.min(scaleFactor,
                           (yRectCenter - 0.0d) / (yRectCenter - yMin));
    scaleFactor = Math.min(scaleFactor,
                           (yRectCenter - graph.getMaxHeight()) /
                           (yRectCenter - yMax));
    final Scale3D scale = new Scale3D(scaleFactor, scaleFactor, 1.0d);
    final AffineTransform3D realTransform =
      toOrig.concatenatePost(rotation.concatenatePost(scale.concatenatePost
                                                      (fromOrig)));
    double[] pointBuff = new double[3];
    for (int i = 0; i < graph.getNumNodes(); i++) {
      if (!graph.isMovableNode(i)) continue;
      Point2D nodePosition = graph.getNodePosition(i);
      pointBuff[0] = nodePosition.getX(); pointBuff[1] = nodePosition.getY();
      realTransform.transformArr(pointBuff);
      graph.setNodePosition
        (i,
         Math.max(0.0d, Math.min(graph.getMaxWidth(), pointBuff[0])),
         Math.max(0.0d, Math.min(graph.getMaxHeight(), pointBuff[1]))); }
  }

}
