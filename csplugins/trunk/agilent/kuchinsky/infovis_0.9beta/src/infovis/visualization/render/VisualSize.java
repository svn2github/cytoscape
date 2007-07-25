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

import java.awt.geom.Rectangle2D.Float;

/**
 * Class VisualSize
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */
public class VisualSize extends AbstractVisualColumn {
    public static final String VISUAL = Visualization.VISUAL_SIZE;
    public static double defaultMinSize = 1;
    public static double defaultMaxSize = 50;
    public static double defaultDefaultSize = 5;
    
    protected double minSize;
    protected double maxSize;
    protected double defaultSize;
    protected NumberColumn sizeColumn;
    
    protected boolean rescaling = true;
    protected transient double smin;
    protected transient double smax;
    protected transient double sscale;
    
    protected transient VisualLabel vl;
    
    public static VisualSize get(Visualization vis) {
        return (VisualSize)findNamed(VISUAL, vis);
    }
    
    public VisualSize(ItemRenderer child) {
        this(child, defaultDefaultSize, defaultMinSize, defaultMaxSize);
    }

    public VisualSize(ItemRenderer child, double def, double min, double max) {
        super(VISUAL);
        filter = NotNumberFilter.sharedInstance();
        defaultSize = def;
        minSize = min;
        maxSize = max;
        addRenderer(child);
    }
    
    protected VisualSize(String name) {
        super(name);
    }
    
    public VisualSize() {
        this((ItemRenderer)null);
    }

    public Column getColumn() {
        return sizeColumn;
    }
    
    public boolean isRescaling() {
        return rescaling;
    }
    
    public void setRescaling(boolean rescaling) {
        if (this.rescaling == rescaling) return;
        this.rescaling = rescaling;
        invalidate();
    }
    
    public NumberColumn getSizeColumn() {
        return sizeColumn;
    }
    
    public void setColumn(Column column) {
        if (column == sizeColumn) return;
        super.setColumn(column);
        sizeColumn = (NumberColumn)column;
        if (column != null && isRescaling()) {
            smin = sizeColumn.getDoubleAt(sizeColumn.getMinIndex());
            smax = sizeColumn.getDoubleAt(sizeColumn.getMaxIndex());
            if (smin == smax) {
                sscale = 0;
            }
            else {
                sscale = (maxSize - minSize) / (smax - smin);
            }
        }
        else {
            sscale = 1;
        }
        if (column == null && defaultDefaultSize == 0) {
            vl = VisualLabel.get(visualization);
            if (vl != null) {
                vl.setInvalidate(true);
            }
        }
        else {
            vl = null;
        }
        
        invalidate();
    }

    /**
     * Returns the size associated with the specified row.
     *
     * @param row the row.
     *
     * @return the size associated with the specified row.
     */
    public double getSizeAt(int row) {
        if (sizeColumn == null) {
            return defaultSize;
        }
        if (sizeColumn.isValueUndefined(row)) {
            return 0;
        }
        return (sizeColumn.getDoubleAt(row) - smin) * sscale + minSize;            
    }
    
    public double getWidthAt(int row) {
        if (sizeColumn != null 
                || vl == null 
                || defaultSize != 0) {
            return getSizeAt(row);
        }

        String label = vl.getLabelAt(row);
        if (label == null) {
            return getSizeAt(row);
        }
        return vl.getWidth(label);
    }

    public double getHeightAt(int row) {
        if (sizeColumn != null 
                || vl == null 
                || defaultSize != 0) {
            return getSizeAt(row);
        }
        String label = vl.getLabelAt(row);
        if (label == null) {
            return getSizeAt(row);
        }
        return vl.getHeight(label);
    }
    
    public void setRectSizeAt(int row, Float rect) {
        rect.width = (float)getWidthAt(row);
        rect.height = (float)getHeightAt(row);
    }

    /**
     * Returns the maxSize.
     *
     * @return double
     */
    public double getMaxSize() {
        return maxSize;
    }

    /**
     * Returns the minSize.
     *
     * @return double
     */
    public double getMinSize() {
        return minSize;
    }

    /**
     * Returns the defaultSize.
     *
     * @return double
     */
    public double getDefaultSize() {
        return defaultSize;
    }

    /**
     * Sets the maxSize.
     *
     * @param maxSize The maxSize to set
     */
    public void setMaxSize(double maxSize) {
        this.maxSize = maxSize;
        invalidate();
    }

    /**
     * Sets the minSize.
     *
     * @param minSize The minSize to set
     */
    public void setMinSize(double minSize) {
        this.minSize = minSize;
        invalidate();
    }

    /**
     * Sets the defaultSize.
     *
     * @param defaultSize The defaultSize to set
     */
    public void setDefaultSize(double defaultSize) {
        if (this.defaultSize == defaultSize) return;
        this.defaultSize = defaultSize;
        if (defaultSize == 0 && sizeColumn == null) {
            vl = DefaultVisualLabel.get(getVisualization());
            if (vl != null) {
                vl.setInvalidate(true);
            }
        }
        else if (vl != null) {
            vl.setInvalidate(false);
            vl = null;
        }
        invalidate();
    }

}
