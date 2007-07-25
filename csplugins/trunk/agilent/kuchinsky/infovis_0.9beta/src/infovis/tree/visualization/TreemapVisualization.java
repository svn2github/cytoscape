/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Tree;
import infovis.column.NumberColumn;
import infovis.tree.visualization.treemap.Squarified;
import infovis.tree.visualization.treemap.Treemap;
import infovis.visualization.Layout;

import java.awt.Dimension;

/**
 * Visualization of a Tree using a Treemap layout.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.48 $

 *  * @infovis.factory VisualizationFactory "Treemap" infovis.Tree
 */
public class TreemapVisualization extends TreeVisualization {
    /** The treemap algorithm */
    protected Treemap treemap;

    /**
     * Creates a new TreemapVisualization object.
     *
     * @param tree the Tree
     * @param treemap the Treemap Layout algorithm.
     */
    public TreemapVisualization(Tree tree, Treemap treemap) {
        super(tree);

        if (treemap == null) {
            treemap = Squarified.instance;
        }
        setLayout(treemap);
//        ColumnFilter filter =
//            new AdditiveAggregation.NonAdditiveFilter(tree);
//        getVisualColumnDescriptor(VISUAL_SIZE).setFilter(filter);
    }

    /**
     * Creates a new TreemapVisualization object.
     * 
     * @param tree
     *            the Tree
     */
    public TreemapVisualization(Tree tree) {
        this(tree, null);
    }

    /**
     * Returns the Treemap.
     *
     * @return Treemap
     */
    public Treemap getTreemap() {
        return treemap;
    }

    /**
     * Sets the treemap.
     *
     * @param treemap The treemap to set
     */
    public void setLayout(Treemap treemap) {
        this.treemap = treemap;
        invalidate();
    }
    
    /**
     * {@inheritDoc}
     */
    public Layout getLayout() {
        return treemap;
    }

    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize() {
        NumberColumn weightColumn = getWeightColumn();
        double min = weightColumn.getDoubleMin();
        double max = weightColumn.getDoubleMax();
        if (min != 0) {
            // Assume we have a square ratio for items
            // and reserve 3 pixels for the smalest item edge
            double side = Math.sqrt(max / min) * 3;
            if (side > 100 && side < 6000) {
                return new Dimension((int)side, (int)side);
            }
            else if (side >= 6000){
                return new Dimension(6000, 6000);
            }
        }
        return super.getPreferredSize();
    }
    
}
