/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Tree;
import infovis.Visualization;
import infovis.tree.visualization.nodelink.RTLayout;
import infovis.visualization.*;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/**
 * Node Link Diagram Visualization of Trees.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision$
 * 
 * @infovis.factory VisualizationFactory "Tree Node Link" infovis.Tree
 */
public class NodeLinkTreeVisualization extends TreeVisualization
    implements NodeAccessor {
    protected boolean rescale = true;
    protected LinkVisualization linkVisualization;
    protected NodeLinkTreeLayout layout;

    /**
     * Constructor for NodeLinkTreeVisualization.
     *
     * @param tree the <code>Tree</code>
     */
    public NodeLinkTreeVisualization(Tree tree) {
        this(tree, null);
    }

    /**
     * Constructor for NodeLinkTreeVisualization.
     *
     * @param tree the <code>Tree</code>
     * @param ir The <code>ItemRenderer</code> or <code>null</code>
     */
    public NodeLinkTreeVisualization(
        Tree tree,
        ItemRenderer ir) {
        super(tree, ir);
        linkVisualization =
            new LinkVisualization(
                tree,
                this,
                this);
        //linkVisualization.setOrientation(ORIENTATION_INVALID);
    }

    // NodeAccessor interface    
    /**
     * {@inheritDoc}
     */
    public int getStartNode(int link) {
        return getTree().getParent(link);
    }

    /**
     * {@inheritDoc}
     */
    public int getEndNode(int link) {
        return link;
    }
    
    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        if (layout == null) {
            layout = new RTLayout();
        }
        return layout;
    }
    
    /**
     * Ssts the Layout of the visualization.
     * @param layout the layout.
     */
    public void setLayout(NodeLinkTreeLayout layout) {
        if (this.layout == layout) return;
        Layout old = this.layout;
        this.layout = layout;
        invalidate();
        firePropertyChange(PROPERTY_LAYOUT, old, layout);
    }

    /**
     * {@inheritDoc}
     */
    public void paintItems(Graphics2D graphics, Rectangle2D bounds) {
        linkVisualization.paint(graphics, bounds);
        super.paintItems(graphics, bounds);
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        super.dispose();
        linkVisualization.dispose();
    }

    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization(int index) {
        if (index == 0)
            return linkVisualization;
        return null;
    }

    /**
     * Returns the LinkVisualization of this NodeLinkTreevisualization.
     * @return the LinkVisualization of this NodeLinkTreevisualization.
     */
    public LinkVisualization getLinkVisualization() {
        return linkVisualization;
    }
    

}
