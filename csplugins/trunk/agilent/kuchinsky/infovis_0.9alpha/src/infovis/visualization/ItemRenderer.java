/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Visualization;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

/**
 * A ItemRenderer performs one or several stages of painting and picking
 * for each item of a Visualization.  It adheres to the
 * <code>Composite</code> pattern.
 * 
 * An item pipeline object performs one or several stage for painting a visualization
 * item.  Several pipeline objects are used to perform the whole rendering of one
 * item.  Each pipeline object is ranked so that it can be called in the right order
 * by default.  DefaultVisualization allows for an explicit reordering of these
 * objects. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public interface ItemRenderer {
    /**
     * Returns the name of this pipeline object or <code>null</code> if it
     * should not be seen from the outside (like a container).
     * 
     * @return the name or <code>null</code>
     */
    public abstract String getName();
    
    /**
     * Returns the children of this ItemRenderer at the specified index.
     * 
     * @param index the index.
     * @return the children of this ItemRendered at the specified index.
     */
    public abstract ItemRenderer getRenderer(int index);
    
    /**
     * Returns the number of children <code>ItemRenderer</code>.
     * @return the number of children <code>ItemRenderer</code>.
     */
    public abstract int getRendererCount();
    
    
    public abstract ItemRenderer insertRenderer(int index, ItemRenderer r);
    public abstract ItemRenderer addRenderer(ItemRenderer r);
    public abstract ItemRenderer removeRenderer(int index);
    public abstract ItemRenderer setRenderer(int index, ItemRenderer r);
    
    public abstract Visualization getVisualization();

    public abstract ItemRenderer compile();
    public abstract void install(Graphics2D graphics);
    public abstract void paint(
            Graphics2D graphics,
            int row,
            Shape shape);
    public abstract void uninstall(Graphics2D graphics);
    public abstract boolean pick(
            Rectangle2D hitBox,
            int row,
            Shape shape);
    
    public abstract boolean isPrototype();
    
    public abstract ItemRenderer instantiate(Visualization vis);
}
