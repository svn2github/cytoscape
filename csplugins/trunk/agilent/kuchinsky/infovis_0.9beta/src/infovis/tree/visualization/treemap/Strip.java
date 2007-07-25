/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization.treemap;


/**
 * Strip Treemap algorithm.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 * 
 * @infovis.factory TreemapFactory Strip
 */
public class Strip extends Squarified {
    /** Instance. */
    public static final Strip instance = new Strip();
    
    /**
     * Constructor.
     *
     */
    public Strip() {
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return "Strip";
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isVertical(
        float xmin,
        float ymin,
        float xmax,
        float ymax,
        int node) {
        return false;
    }

}
