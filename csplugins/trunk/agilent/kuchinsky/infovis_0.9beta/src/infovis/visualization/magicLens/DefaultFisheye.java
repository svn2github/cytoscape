/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.utils.StrokedPath;

import java.awt.Shape;
import java.awt.geom.*;

/**
 * Fisheyes manage space deformation to maintain focus+context views by applying
 * a space deformation. See Sheelagh Carpendale's PhD for full details.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class DefaultFisheye extends DefaultMagicLens implements Fisheye {
    public static final String PROPERTY_DISTANCE = "distance";
    public static final String PROPERTY_METRIC = "metric";
    public static final String PROPERTY_LENS_SHAPE = "lensShape";
    public static final String PROPERTY_FOCUS_RADIUS = "focusRadius";
    public static final String PROPERTY_TOLERANCE = "tolerance";
    public static final String PROPERTY_TRANSFORMING_X = "transformingX";
    public static final String PROPERTY_TRANSFORMING_Y = "transformingY";

    /** constant value for setDistanceMetric to use a L1 distance */
    public static final short DISTANCE_L1 = 0;

    /** constant value for setDistanceMetric to use a L2 distance */
    public static final short DISTANCE_L2 = 1;

    /** constant value for setDistanceMetric to use a L infinity distance */
    public static final short DISTANCE_LINF = 2;

    /** constant value for setLensType to use a gaussian lens types */
    public static final short LENS_GAUSSIAN = 0;

    /** constant value for setLensType to use a cosine lens types */
    public static final short LENS_COSINE = 1;

    /** constant value for setLensType to use a hemisphere lens types */
    public static final short LENS_HEMISPHERE = 2;

    /** constant value for setLensType to use a linear lens types */
    public static final short LENS_LINEAR = 3;

    /** constant value for setLensType to use an inverse cosine lens types */
    public static final short LENS_INVERSE_COSINE = 4;

    /** The virtual camera height is 10.0f */
    public static final float REFERENCE_HEIGHT = 10.0f;

    /** The virtual viewplane is located at this distance from the camera */
    public static final float DISTANCE_VIEW_PLANE = 1.0f;

    protected transient Line2D.Double tmpLine = new Line2D.Double();
    protected float focusRadius;
    protected float focusHeight;
    protected float tolerance = 1;
    protected short distanceMetric;
    protected Metric metric;
    protected short lensType;
    protected LensProfile lensProfile;
    protected boolean transformingX = true;
    protected boolean transformingY = true;

    /**
     * Constructor for Fisheyes.
     */
    public DefaultFisheye() {
        this(100, 0, 9);
    }

    /**
     * Creates a new Fisheye object.
     * 
     * @param lensRadius
     *            the lens radius.
     * @param focusRadius
     *            the focus radios
     * @param focalHeight
     *            the focal heigt (0 &lt;= 9)
     */
    public DefaultFisheye(float lensRadius, float focusRadius,
            float focalHeight) {
        setLensRadius(lensRadius);
        setFocusRadius(focusRadius);
        setFocusHeight(focalHeight);
        setDistanceMetric(DISTANCE_L2);
        setLensType(LENS_LINEAR);
    }

    /**
     * Returns true of point is transformed.
     * 
     * @param x
     *            X coordinate
     * @param y
     *            Y coordinate
     * 
     * @return true of point is transformed.
     */
    public boolean isTransformed(float x, float y) {
        return isEnabled()
                && metric.compare(lensRadius, x - lensX, y - lensY) > 0;
    }

    /**
     * Returns true of point is transformed.
     * 
     * @param x
     *            X coordinate
     * @param y
     *            Y coordinate
     * 
     * @return true of point is transformed.
     */
    public boolean isTransformed(double x, double y) {
        return isEnabled() && isTransformed((float) x, (float) y);
    }

    /**
     * Returns true of point is transformed.
     * 
     * @param p
     *            the point
     * 
     * @return true of point is transformed.
     */
    public boolean isTransformed(Point2D p) {
        return isEnabled() && isTransformed(p.getX(), p.getY());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param bounds
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean isTransformed(Rectangle2D bounds) {
        return isEnabled() && bounds.intersects(getBounds());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param s
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean isTransformed(Shape s) {
        return isEnabled() && s.intersects(getBounds());
    }

    /**
     * DOCUMENT ME!
     * 
     * @param s
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Shape transform(Shape s) {
        if (!isTransformed(s))
            return s;

        GeneralPath p = new GeneralPath();
        float[] coords = { 0, 0, 0, 0, 0, 0 };
        float first_x = 0;
        float first_y = 0;
        float prev_x = 0;
        float prev_y = 0;
        for (PathIterator iter = s.getPathIterator(null, tolerance
                / getMaximumScale()); !iter.isDone(); iter.next()) {
            switch (iter.currentSegment(coords)) {
            case PathIterator.SEG_MOVETO: {
                prev_x = coords[0];
                prev_y = coords[1];
                first_x = prev_x;
                first_y = prev_y;
                transform(coords, 1);
                p.moveTo(coords[0], coords[1]);
                break;
            }
            case PathIterator.SEG_LINETO: {
                float x = coords[0];
                float y = coords[1];
                subdivide(prev_x, prev_y, x, y, p);
                prev_x = x;
                prev_y = y;
                break;
            }
            case PathIterator.SEG_CLOSE: {
                subdivide(prev_x, prev_y, first_x, first_y, p);
                p.closePath();
                break;
            }
            }
        }
        return p;
    }

    void addTransformed(float x, float y, GeneralPath p) {
        float scale = getScale(x, y);
        float tx;
        float ty;
        if (scale == 1) {
            return;
        }
        tx = transformX(x, scale);
        ty = transformY(y, scale);
        p.lineTo(tx, ty);
    }

    static float dist2(float dx, float dy) {
        return dx * dx + dy * dy;
    }

    /**
     * Subdivide a line segment already clipped.
     * 
     * @param x1
     *            X coordinate of first point
     * @param y1
     *            Y coordinate of first point
     * @param x2
     *            X coordinate of second point
     * @param y2
     *            Y coordinate of second point
     * @param p
     *            GeneralPath to fill
     */
    public void subdivideSegment(float x1, float y1, float tx1,
            float ty1, float x2, float y2, float tx2, float ty2,
            GeneralPath p, int depth) {
        if (depth > 20)
            throw new RuntimeException("too deep in subdivideSegment");
        if (x1 == x2 && y1 == y2) {
            p.lineTo(tx2, ty2);
            return;
        }
        if (lensRadius <= focusRadius) {
            p.lineTo(tx1, ty1);
            p.lineTo(tx2, ty2);
            p.lineTo(x2, y2);
            return;
        }

        float xm = (x1 + x2) / 2;
        float ym = (y1 + y2) / 2;
        float scale = getScale(xm, ym);
        float txm = transformX(xm, scale);
        float tym = transformY(ym, scale);

        // subdivide in small pieces to avoid losing the compression portion
        float maxLen = (lensRadius - focusRadius) / 3;
        if ((dist2(x2 - x1, y2 - y1) > maxLen * maxLen)
                || (dist2(txm - (tx1 + tx2) / 2, tym - (ty1 + ty2) / 2) > tolerance)) {
            subdivideSegment(x1, y1, tx1, ty1, xm, ym, txm, tym, p,
                    depth + 1);
            subdivideSegment(xm, ym, txm, tym, x2, y2, tx2, ty2, p,
                    depth + 1);
        } else {
            p.lineTo(tx2, ty2);
        }
    }

    /**
     * Subdivide a line segment.
     * 
     * @param x1
     *            X coordinate of first point
     * @param y1
     *            Y coordinate of first point
     * @param x2
     *            X coordinate of second point
     * @param y2
     *            Y coordinate of second point
     * @param p
     *            GeneralPath to fill
     */
    public void subdivide(float x1, float y1, float x2, float y2,
            GeneralPath p) {

        tmpLine.setLine(x1, y1, x2, y2);
        Line2D.Double l = StrokedPath.clip(tmpLine, bounds);
        if (l == null) {
            //            System.out.println(
            //                "clip"+bounds+" with " +
            //                x1 + ", " +
            //                y1 + ", " +
            //                x2 + ", " +
            //                y2 +
            //                " == null");
            p.lineTo(x2, y2);
            return;
        }

        if (l.x1 != x1 || l.y1 != y1) {
            x1 = (float) l.x1;
            y1 = (float) l.y1;
            p.lineTo(x1, y1);
        }
        float scale = getScale(x1, y1);
        float tx1 = transformX(x1, scale);
        float ty1 = transformY(y1, scale);
        scale = getScale((float) l.x2, (float) l.y2);
        float tx2 = transformX((float) l.x2, scale);
        float ty2 = transformY((float) l.y2, scale);

        subdivideSegment(x1, y1, tx1, ty1, (float) l.x2, (float) l.y2,
                tx2, ty2, p, 0);
        if (l.x2 != x2 || l.y2 != y2) {
            p.lineTo(x2, y2);
        }
    }

    /**
     * Returns the height of a specified point.
     * 
     * @param x
     *            X coordinate of the point
     * @param y
     *            Y coordinate of the point
     * 
     * @return the height of the specified point.
     */
    public float pointHeight(float x, float y) {
        return height(distance(x, y));
    }

    /**
     * Returns the distance of the specified point from the focus.
     * 
     * @param x
     *            X coordinate of the point
     * @param y
     *            Y coordinate of the point
     * 
     * @return the distance of the specified point from the focus.
     */
    public float distance(float x, float y) {
        if (!transformingX) {
            return metric.distance(0, y - lensY);
        } else if (!transformingY) {
            return metric.distance(x - lensX, 0);
        }
        return metric.distance(x - lensX, y - lensY);
    }

    /**
     * Returns the height at the specified distance from the focus
     * 
     * @param dist
     *            the distance
     * 
     * @return the height at the specified distance from the focus
     */
    public float height(float dist) {
        if (focusHeight == 0) {
            return 0;
        }

        float realFocus = focusRadius / getMaximumScale();
        if (dist > lensRadius) {
            return 0;
        } else if (dist <= realFocus) {
            return focusHeight;
        } else {
            float t = (dist - realFocus) / (lensRadius - realFocus);
            return Math.min(focusHeight * lens(t), focusHeight);
        }
    }

    /**
     * Returns the height at the specified normalized distance from the focus
     * 
     * @param t
     *            the normalized distance from the focus
     * 
     * @return the height at the specified normalized distance from the focus
     */
    public float lens(float t) {
        return lensProfile.profile(t);
    }

    /**
     * Returns the focusRadius.
     * 
     * @return float
     */
    public float getFocusRadius() {
        return focusRadius;
    }

    public void setLensX(float x) {
        if (bounds != null) {
            bounds.x = x - focusRadius;
        }
        super.setLensX(x);
    }

    /**
     * Sets the lens Y.
     * 
     * @param y
     *            The lensY to set
     */
    public void setLensY(float y) {
        if (bounds != null) {
            bounds.y = y - focusRadius;
        }
        super.setLensY(y);
    }

    /**
     * Sets the focusRadius.
     * 
     * @param radius
     *            The focusRadius to set
     */
    public void setFocusRadius(float radius) {
        if (this.focusRadius == radius)
            return;
        if (this.lensRadius >= radius) {
            float old = this.focusRadius;
            this.focusRadius = radius;
            firePropertyChange(PROPERTY_FOCUS_RADIUS, old, radius);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param focus
     *            DOCUMENT ME!
     * @param lens
     *            DOCUMENT ME!
     */
    public void setRadii(float focus, float lens) {
        if (lens < focus) {
            focus = lens;
        }
        setFocusRadius(focus);
        setLensRadius(lens);
    }

    /**
     * Returns the focal height.
     * 
     * @return float
     */
    public float getFocusHeight() {
        return focusHeight;
    }

    /**
     * Sets the focal height.
     * 
     * @param focalHeight
     *            The focal height to set
     */
    public void setFocusHeight(float focalHeight) {
        if (focalHeight < 0) {
            focalHeight = 0;
        } else if (focalHeight > 9) {
            focalHeight = 9;
        }
        if (this.focusHeight == focalHeight)
            return;
        firePropertyChange(PROPERTY_FOCUS_HEIGHT, this.focusHeight,
                focalHeight);

        this.focusHeight = focalHeight;
    }

    /**
     * Change the maximum scale
     * 
     * @param scale
     *            the new maximum scale
     */
    public void setMaximumScale(float scale) {
        if (scale == 0) {
            setFocusHeight(0);
        } else {
            setFocusHeight(REFERENCE_HEIGHT - (REFERENCE_HEIGHT / scale));
        }
    }

    /**
     * Returns the maximum scale
     * 
     * @return the maximum scale
     */
    public float getMaximumScale() {
        return REFERENCE_HEIGHT / (REFERENCE_HEIGHT - focusHeight);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param x
     *            DOCUMENT ME!
     * @param y
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public float getScale(float x, float y) {
        if (! isEnabled()) {
            return 1;
        }
        float height = pointHeight(x, y);
        return REFERENCE_HEIGHT / (REFERENCE_HEIGHT - height);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param x
     *            DOCUMENT ME!
     * @param scale
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public float transformX(float x, float scale) {
        if (isEnabled() && transformingX)
            return (x - lensX) * scale + lensX;
        return x;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param y
     *            DOCUMENT ME!
     * @param scale
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public float transformY(float y, float scale) {
        if (isEnabled() && transformingY)
            return (y - lensY) * scale + lensY;
        return y;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param coords
     *            DOCUMENT ME!
     */
    public void transform(float[] coords, int npoints) {
        if (!isEnabled()) return;
        for (int i = 0; i < npoints; i++) {
            float scale = getScale(coords[2 * i], coords[2 * i + 1]);
            if (scale != 1) {
                coords[2 * i] = transformX(coords[2 * i], scale);
                coords[2 * i + 1] = transformY(coords[2 * i + 1], scale);
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param src
     *            DOCUMENT ME!
     * @param dst
     *            DOCUMENT ME!
     */
    public void transform(Point2D src, Point2D dst) {
        float scale = getScale((float) src.getX(), (float) src.getY());
        if (scale != 1) {
            dst.setLocation(transformX((float) src.getX(), scale),
                    transformY((float) src.getY(), scale));
        } else if (dst != src) {
            dst.setLocation(src);
        }
    }

    public Point2D transform(Point2D src) {
        float scale = getScale((float) src.getX(), (float) src.getY());
        if (scale == 1)
            return src;
        Point2D dst = new Point2D.Double();
        transform(src, dst);
        return dst;
    }

    /**
     * Returns the distanceMetric.
     * 
     * @return short the distanceMetric
     */
    public short getDistanceMetric() {
        return distanceMetric;
    }

    /**
     * Returns the lensType.
     * 
     * @return short
     */
    public short getLensType() {
        return lensType;
    }

    /**
     * Sets the distanceMetric.
     * 
     * @param distanceMetrics
     *            The distanceMetric to set
     */
    public void setDistanceMetric(short distanceMetrics) {
        if (this.distanceMetric == distanceMetrics)
            return;
        firePropertyChange(PROPERTY_METRIC, this.distanceMetric,
                distanceMetrics);
        this.distanceMetric = distanceMetrics;

        switch (distanceMetrics) {
        case DISTANCE_L1:
            metric = new DistanceL1();
            break;
        case DISTANCE_L2:
            metric = new DistanceL2();
            break;
        case DISTANCE_LINF:
            metric = new DistanceLInf();
            break;
        }
    }

    /**
     * Sets the lensType.
     * 
     * @param lensType
     *            The lensType to set
     */
    public void setLensType(short lensType) {
        this.lensType = lensType;

        switch (lensType) {
        case LENS_GAUSSIAN:
            lensProfile = new ProfileGuassian();
            break;
        case LENS_COSINE:
            lensProfile = new ProfileCos();
            break;
        case LENS_HEMISPHERE:
            lensProfile = new ProfileCos();
            break;
        case LENS_INVERSE_COSINE:
            lensProfile = new ProfileInverse(new ProfileCos());
            break;
        case LENS_LINEAR:
            lensProfile = new ProfileLinear();
            break;
        }
    }

    public interface Metric {
        public float distance(float dx, float dy);

        public int compare(float dist, float dx, float dy);
    }

    public interface LensProfile {
        public float profile(float t);
    }

    public static class ProfileCos implements LensProfile {
        public float profile(float t) {
            return (float) Math.cos(t * Math.PI / 2);
        }
    }

    static class ProfileGuassian implements LensProfile {
        private static final double RO = 0.1;

        //private static final double DENOM = 1 / (RO * Math.sqrt(2 * Math.PI));

        public float profile(float t) {
            return (float) Math.exp((-t * t) / RO);
        }
    }

    static class ProfileOneMinusSin implements LensProfile {
        public float profile(float t) {
            return 1 - (float) Math.sin(t);
        }
    }

    static class ProfileLinear implements LensProfile {
        public float profile(float t) {
            return 1 - t;
        }
    }

    static class ProfileInverse implements LensProfile {
        LensProfile profile;

        public ProfileInverse(LensProfile profile) {
            this.profile = profile;
        }

        public float profile(float t) {
            return 1 - profile.profile(1 - t);
        }
    }

    static class DistanceL1 implements Metric {
        public float distance(float dx, float dy) {
            return Math.abs(dx) + Math.abs(dy);
        }

        public int compare(float dist, float dx, float dy) {
            float d = dist - distance(dx, dy);
            if (d < 0)
                return -1;
            else if (d == 0)
                return 0;
            else
                return 1;
        }

    }

    static class DistanceL2 implements Metric {
        public float distance(float dx, float dy) {

            return (float) Math.sqrt((dx * dx) + (dy * dy));
        }

        public int compare(float dist, float dx, float dy) {
            float d = dist * dist - (dx * dx) + (dy * dy);
            if (d < 0)
                return -1;
            else if (d == 0)
                return 0;
            else
                return 1;
        }
    }

    static class DistanceLInf implements Metric {
        public float distance(float dx, float dy) {
            return Math.max(Math.abs(dx), Math.abs(dy));
        }

        public int compare(float dist, float dx, float dy) {
            float d = dist - distance(dx, dy);
            if (d < 0)
                return -1;
            else if (d == 0)
                return 0;
            else
                return 1;
        }
    }

    /**
     * Returns the tolerance.
     * 
     * @return float
     */
    public float getTolerance() {
        return tolerance;
    }

    /**
     * Sets the tolerance.
     * 
     * @param tolerance
     *            The tolerance to set
     */
    public void setTolerance(float tolerance) {
        if (tolerance < 1)
            tolerance = 1;
        if (this.tolerance == tolerance)
            return;
        firePropertyChange(PROPERTY_TOLERANCE, this.tolerance,
                tolerance);
        this.tolerance = tolerance;
    }

    public boolean isTransformingX() {
        return transformingX;
    }

    public void setTransformingX(boolean transformingX) {
        if (this.transformingX == transformingX)
            return;
        firePropertyChange(PROPERTY_TRANSFORMING_X, this.transformingX,
                transformingX);
        this.transformingX = transformingX;
    }

    public boolean isTransformingY() {
        return transformingY;
    }

    public void setTransformingY(boolean transformingY) {
        if (this.transformingY == transformingY)
            return;
        firePropertyChange(PROPERTY_TRANSFORMING_Y, this.transformingY,
                transformingY);
        this.transformingY = transformingY;
    }
}