/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Column;
import infovis.Visualization;
import infovis.column.NumberColumn;
import infovis.column.filter.NotNumberFilter;
import infovis.visualization.ItemRenderer;

import java.awt.*;

/**
 * Choose the transpareny of items.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class VisualAlpha extends AbstractVisualColumn {
    public static final String VISUAL = Visualization.VISUAL_ALPHA;
    public static final int RULE = AlphaComposite.SRC_OVER;
    protected double defaultAlpha = 1.0;
    protected NumberColumn alphaColumn;
    protected double amin;
    protected double amax;
    protected double scale;
    protected static final AlphaComposite[] CACHE = new AlphaComposite[65];
    
    static {
        for (int i = 0; i < 65; i++) {
            CACHE[i] = AlphaComposite.getInstance(RULE, i/64.0f);
        }
    }

    public static VisualAlpha get(Visualization vis) {
        return (VisualAlpha)findNamed(VISUAL, vis);
    }
    
    public VisualAlpha(ItemRenderer child) {
        super(VISUAL);
        addRenderer(child);
    }
    
    public VisualAlpha(ItemRenderer c1, ItemRenderer c2) {
        super(VISUAL);
        addRenderer(c1);
        addRenderer(c2);
    }

    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto, Visualization vis) {
        super.instantiateChildren(proto, vis);
        filter = NotNumberFilter.sharedInstance();
        return this;
    }

    
    public Column getColumn() {
        return alphaColumn;
    }
    
    public void setColumn(Column column) {
        if (this.alphaColumn == column) return;
        super.setColumn(column);
        alphaColumn = (NumberColumn)column;
        invalidate();
    }
    
    public NumberColumn getAlphaColumn() {
        return alphaColumn;
    }

    public void install(Graphics2D graphics) {
        if (alphaColumn == null 
                || alphaColumn.getMinIndex() == -1) {
            scale = 0;
        }
        else {
            amin = alphaColumn.getDoubleAt(alphaColumn.getMinIndex());
            amax = alphaColumn.getDoubleAt(alphaColumn.getMaxIndex());
            if (amin == amax)
                scale = 0;
            else
                scale = 1.0 / (amax - amin);
        }
        super.install(graphics);
    }

    public double getAlphaAt(int row) {
        if (scale == 0)
            return defaultAlpha;
        else
            return (alphaColumn.getDoubleAt(row) - amin) * scale;
    }

    public double getDefaultAlpha() {
        return defaultAlpha;
    }

    public void setDefaultAlpha(double alpha) {
        defaultAlpha = alpha;
        invalidate();
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        if (scale == 0 && defaultAlpha == 1.0) {
            super.paint(graphics, row, shape);
            return;
        }
        Composite saved = graphics.getComposite();
        try {
            graphics.setComposite(
                    CACHE[(int)(getAlphaAt(row)*64.99)]);
            super.paint(graphics, row, shape);
        } finally {
            graphics.setComposite(saved);
        }
    }
    
    public ItemRenderer compile() {
        if (alphaColumn != null) {
            return this;
        }
        if (defaultAlpha == 0) {
            return null;
        }
        if (getRendererCount() == 0) {
            return null;
        }
        if (defaultAlpha != 1) {
            return this;
        }
        // behave just like a group
        return super.compileGroup();
    }
}
