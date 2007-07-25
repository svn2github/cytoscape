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
 * Class Orientation
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class Orientation implements Orientable {
    private short orientation;

    public Orientation() {
        this(ORIENTATION_SOUTH);
    }

    public Orientation(short orientation) {
        setOrientation(orientation);
    }

    public short getOrientation() {
        return orientation;
    }

    public void setOrientation(short orientation) {
        this.orientation = orientation;
    }

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

    public static boolean isHorizontal(short orientation) {
        return orientation == ORIENTATION_EAST
                || orientation == ORIENTATION_WEST;
    }

    public static boolean isVertical(short orientation) {
        return orientation == ORIENTATION_NORTH
                || orientation == ORIENTATION_SOUTH;

    }

    static String toString(short orientation) {
        int o = orientation + 1;
        if (o < 0 || o > name.length)
            return "invalid";
        return name[o];
    }

    static Point2D getDirection(short orientation) {
        return direction[orientation + 1];
    }
}