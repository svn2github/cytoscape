/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Tree;
import infovis.column.IntColumn;
import infovis.metadata.ValueCategory;
import infovis.tree.Algorithms;
import infovis.tree.DepthFirst;
import infovis.tree.TreeIntColumn;
import infovis.utils.RowIterator;

import java.awt.Color;

/**
 * Computes and maintain a rainbow color according to the leaves of a tree.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class RainbowColumn extends TreeIntColumn implements DepthFirst.Visitor {
    /** Name of the column containing the rainbow. */
    public static final String RAINBOW_COLUMN = "[rainbow]";

    private transient float[]  startValue;
    private transient float[]  endValue;
    private transient int[]    sumDegree;
    private transient int      depth          = 0;

    /**
     * Returns a RainbowColumn associated with the specified tree,
     * creating it if needed. 
     * @param tree the tree
     * @return a RainbowColumn associated with the specified tree,
     * creating it if needed.
     */
    public static RainbowColumn findColumn(Tree tree) {
        RainbowColumn rainbow = getColumn(tree);
        if (rainbow == null) {
            rainbow = new RainbowColumn(tree);
            tree.addColumn(rainbow);
        }
        return rainbow;
    }

    /**
     * Returns a RainbowColumn associated with the specified tree
     * or null if none exists.
     * @param tree the tree
     * @return a RainbowColumn associated with the specified tree
     * or null if none exists.
     */
    public static RainbowColumn getColumn(Tree tree) {
        IntColumn rainbow = IntColumn.getColumn(tree, RAINBOW_COLUMN);
        if (rainbow instanceof RainbowColumn) {
            return (RainbowColumn) rainbow;
        }
        return null;
    }

    protected RainbowColumn(Tree tree) {
        super(RAINBOW_COLUMN, tree);
        ValueCategory.setValueCategory(this, ValueCategory.TYPE_EXPLICIT);
    }

    protected void update() {
        int size = tree.getLastRow() + 1;
        startValue = new float[size];
        endValue = new float[size];
        startValue[Tree.ROOT] = 0;
        endValue[Tree.ROOT] = 1;
        sumDegree = new int[size];
        Algorithms.leafCount(tree, Tree.ROOT, sumDegree);
        DepthFirst.visit(tree, this);
        startValue = null;
        endValue = null;
        sumDegree = null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean preorder(int node) {
        float start = startValue[node];
        float end = endValue[node];
        int color = Color.HSBtoRGB(
                (start + end) / 2,
                1.0f,
                1.0f);
        setExtend(node, color);

        int children = sumDegree[node];
        if (children == 0) {
            return true;
        }
        float delta = (end - start) / children;
        int child = node;
        for (RowIterator iter = tree.childrenIterator(node); iter.hasNext();) {
            child = iter.nextRow();
            startValue[child] = start;
            start += delta * sumDegree[child];
            endValue[child] = start;
        }
        endValue[child] = end; // avoid rounding errors
        depth++;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void postorder(int node) {
        depth--;
    }
}
