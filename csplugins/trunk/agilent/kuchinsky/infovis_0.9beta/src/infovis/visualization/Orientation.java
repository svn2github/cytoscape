/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import java.awt.geom.Point2D;

/**
 * Simple implementation of interface Orientable with
 * conveninent static methods.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class Orientation implements Orientable {
    private short orientation;

    /**
     * Create an Orientation to the SOUTH.
     *
     */
    public Orientation() {
        this(ORIENTATION_SOUTH);
    }

    /**
     * Creates an orientation by specifying its initial value.
     * @param orientation the initial value
     */
    public Orientation(short orientation) {
        setOrientation(orientation);
    }

    /**
     * Returns the orientation. 
     * 
     * @return the orientation. 
     */
    public short getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation.
     * @param orientation the new orientation set
     */
    public void setOrientation(short orientation) {
        this.orientation = orientation;
    }

    /**
     * Returns the closest orientation to a speficied direction.
     * 
     * @param dx the x component of the orientation
     * @param dy the y component of the orientation
     * @return the corresponding orientation value
     */
    public static short directionOrientation(double dx, double dy) {
        if (dx == 0 && dy == 0)
            return ORIENTATION_INVALID;
        if (dy >= 0) {
            if (dx >= 0) {
                if (dx >= dy)
                    return ORIENTATION_EAST;
                else
                    return ORIENTATION_SOUTH;
            } else { // dx < 0
                if (-dx >= dy)
                    return ORIENTATION_WEST;
                else
                    return ORIENTATION_SOUTH;
            }
        } else { // dy < 0
            if (dx >= 0) {
                if (dx >= -dy)
                    return ORIENTATION_EAST;
                else
                    return ORIENTATION_NORTH;
            } else { // dx < 0
                if (-dx >= -dy)
                    return ORIENTATION_WEST;
                else
                    return ORIENTATION_NORTH;
            }
        }
    }

    /**
     * Returns the inverse of a specified orientation.
     * 
     * @param orientation the orientation
     * @return the inverse of the orientation.
     */
    public static short inverse(short orientation) {
        switch (orientation) {
        case ORIENTATION_EAST:
            return ORIENTATION_WEST;
        case ORIENTATION_WEST:
            return ORIENTATION_EAST;
        case ORIENTATION_NORTH:
            return ORIENTATION_SOUTH;
        case ORIENTATION_SOUTH:
            return ORIENTATION_NORTH;
        case ORIENTATION_CENTER:
            return ORIENTATION_CENTER;
        }
        return ORIENTATION_INVALID;
    }

    /**
     * Returns the orientation turned by 90 degrees.
     * 
     * @param orientation the orientation
     * @return the orientation turned by 90 degrees.
     */
    public static short turn90(short orientation) {
        switch (orientation) {
        case ORIENTATION_EAST:
            return ORIENTATION_SOUTH;
        case ORIENTATION_WEST:
            return ORIENTATION_NORTH;
        case ORIENTATION_NORTH:
            return ORIENTATION_EAST;
        case ORIENTATION_SOUTH:
            return ORIENTATION_WEST;
        case ORIENTATION_CENTER:
            return ORIENTATION_CENTER;
        }
        return ORIENTATION_INVALID;
    }

    /**
     * Return true if the orientation is horizontal.
     * @param orientation the orientation
     * @return true if the orientation is horizontal.
     */
    public static boolean isHorizontal(short orientation) {
        return orientation == ORIENTATION_EAST
                || orientation == ORIENTATION_WEST;
    }

    /**
     * Return true if the orientation is vertical.
     * @param orientation the orientation
     * @return true if the orientation is vertial.
     */
    public static boolean isVertical(short orientation) {
        return orientation == ORIENTATION_NORTH
                || orientation == ORIENTATION_SOUTH;

    }

    /**
     * Returns a printable representation for a specified orientation.
     * 
     * @param orientation the orientatio
     * @return a printable representation for the orientation.
     */
    public static String toString(short orientation) {
        int o = orientation + 1;
        if (o < 0 || o > NAME.length)
            return "invalid";
        return NAME[o];
    }

    /**
     * Returns a direction vector for a specified orientation.
     * 
     * @param orientation the orientation
     * @return the direction vector in a Point2D
     */
    public static Point2D getDirection(short orientation) {
        return DIRECTION[orientation + 1];
    }
}