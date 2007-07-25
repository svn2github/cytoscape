/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import java.awt.geom.Point2D;

import javax.swing.SwingConstants;

/**
 * Interface Orientable
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public interface Orientable extends SwingConstants {
    public static final short ORIENTATION_INVALID = -1;
    /** Orientation towards center */
    public static final short ORIENTATION_CENTER = CENTER;
    /** Orientation towards north */
    public static final short ORIENTATION_NORTH = NORTH;
    /** Orientation towards south*/
    public static final short ORIENTATION_SOUTH = SOUTH;
    /** Orientation towards east */
    public static final short ORIENTATION_EAST = EAST;
    /** Orientation towards west */
    public static final short ORIENTATION_WEST = WEST;
    
    public static final float SQRT2 = (float)Math.sqrt(2);

    static final String name[] = { 
            "invalid", 
            "center", 
            "north",
            "north east",
            "east",
            "south east",
            "south",
            "south west",
            "west",
            "north west"};
    static final Point2D.Float direction[] = {
            new Point2D.Float(0, 0), // no direction
            new Point2D.Float(0, 1), 
            new Point2D.Float(SQRT2, SQRT2), 
            new Point2D.Float(1, 0),
            new Point2D.Float(SQRT2, -SQRT2), 
            new Point2D.Float(0, -1),
            new Point2D.Float(-SQRT2, -SQRT2),
            new Point2D.Float(-1, 0),
            new Point2D.Float(-SQRT2, SQRT2),
            };

    
    public short getOrientation();
    public void setOrientation(short orientation);

}
