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
import infovis.tree.*;
import infovis.utils.RowIterator;

import java.awt.Color;

/**
 * Class RainbowTreeColor
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class RainbowColumn extends TreeIntColumn
    implements DepthFirst.Visitor {
    public final static String RAINBOW_COLUMN = "[rainbow]"; 
    private transient float[] startValue;
    private transient float[] endValue;
    private transient int[] sumDegree;
    private transient int depth = 0;

    public static RainbowColumn findColumn(Tree tree) {
        RainbowColumn rainbow = getColumn(tree);
        if (rainbow == null) {
            rainbow = new RainbowColumn(tree);
            tree.addColumn(rainbow);
        }
        return rainbow;
    }
    
    public static RainbowColumn getColumn(Tree tree) {
        IntColumn rainbow = IntColumn.getColumn(tree, RAINBOW_COLUMN);
        if (rainbow instanceof RainbowColumn) {
            return (RainbowColumn)rainbow;
        }
        return null;
    }
    
    public RainbowColumn(Tree tree) {
        super(RAINBOW_COLUMN, tree);
        ValueCategory.setValueCategory(this, ValueCategory.TYPE_EXPLICIT);
    }
    
    public void update() {
        startValue = new float[tree.getNodeCount()];
        endValue = new float[tree.getNodeCount()];
        startValue[Tree.ROOT] = 0;
        endValue[Tree.ROOT] = 1;
        sumDegree = new int[tree.getNodeCount()];
        Algorithms.leafCount(tree, Tree.ROOT, sumDegree);
        DepthFirst.visit(tree, this);
        startValue = null;
        endValue = null;
        sumDegree = null;
    }
    
    public boolean preorder(int node) {
        float start = startValue[node];
        float end = endValue[node];
        int color = Color.HSBtoRGB(
                (start+end)/2,
                1.0f, //(maxDepth - depth) / maxDepth,
                1.0f);
//        int color = Colors.LCHtoRGB(
//                100,
//                1,
//              (start+end)/2*360);
        setExtend(node, color);

        int children = sumDegree[node];
        if (children == 0) {
            return true;
        }
        float delta = (end - start) / children;
        int child = node;
        for (RowIterator iter = tree.childrenIterator(node); iter.hasNext(); ) {
            child = iter.nextRow();
            startValue[child] = start;
            start += delta * sumDegree[child];
            endValue[child] = start;
        }
        endValue[child] = end; // avoid rounding errors
        depth++;
        return true;
    }
    
    public void inorder(int node) {
    }
    
    public void postorder(int node) {
        depth--;
    }
}
