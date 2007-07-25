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
 * A <code>ItemRenderer</code> performs one or several stages of painting and picking
 * for each item of a Visualization.  It adheres to the <code>Composite</code> Design Pattern.
 * 
 * <p>An item renderer object performs one or several stage for painting a visualization
 * item.  Several pipeline objects are used to perform the whole rendering of one
 * item.  Item renderers form a tree.  For example, when each visualized objects should be
 * assigned a color and filled, the tree is simply a list of the following form:
 * <code>ItemRenderer ir = new VisualColor(new Fill());</code>
 * 
 * <p>Default renderers are more complex.  They can also render a stroke outside each
 * item and a label over them such as:
 * <pre>
ItemRenderer ir = new VisualGroup(
    new VisualColor(new Fill()),
    new VisualSelect(new Stroke()),
    new VisualLabel());
 * </pre>
 * 
 * <p>Item Renderers can be created directly and set to a {@link infovis.Visualization}, either
 * at construction time or later.  However, they are more often created through the 
 * {@link infovis.visualization.render.ItemRendererFactory}.  They most often derive from two
 * classes: {@link infovis.visualization.render.DefaultFillingItemRenderer} or
 * {@link infovis.visualization.render.DefaultStrokingItemRenderer}.  Looking at these classes 
 * should help understand how to specify or change the default ones.
 * 
 * <p>Most classes implementing the <code>ItemRenderer</code> also implement the
 * {@link infovis.visualization.VisualColumnDescriptor} interface.  When a tree of 
 * <code>ItemRenderer</code>s is associated with a visualization, all the ones also implementing
 * the <code>VisualColumnDescriptor</code> interface are registered as visual columns.
 * 
 * <p>Item Renderers have a name (see {@link #getName()}), and should implement the
 * {@link #install(Graphics2D)},
 * {@link #paint(Graphics2D,int,Shape)},
 * {@link #uninstall(Graphics2D)} and
 * {@link #pick(Rectangle2D,int,Shape)} methods.
 * 
 * <p><code>install</code> is called by the visualization before any rendering is done.  It is used
 * to initialize the parameters of the renderer that depends on the current settings of the
 * visualization or of the environment.
 * 
 * <p><code>uninstall</code> is called by the visualization after all the rendering is done.  It is
 * used to release any resource allocated during the rendering.
 * 
 * <p><code>paint</code> is called when rendering each item in turn.
 * 
 * <p><code>pick</code> is called for picking.
 * 
 * <p>Each of these methods are called on each items of the tree so their implementation should
 * usually propagate the operations to the subtree.  The
 * {@link infovis.visualization.render.AbstractItemRenderer} base class implements the propagation
 * along the tree. 
 * 
 * <p><code>ItemRenderer</code>s can also come into two flavors: prototypes and instances.  Prototypes
 * are meant to be cloned to create instances.  When a tree of <code>ItemRenderer</code>s does not reference
 * a <code>Visualization</code>, they are prototypes whereas those referencing a <code>Visualization</code>
 * are instances (there are currently two exceptions though).
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
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
