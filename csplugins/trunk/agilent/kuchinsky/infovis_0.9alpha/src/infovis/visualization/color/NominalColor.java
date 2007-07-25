/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.color;

import infovis.Column;
import infovis.utils.RowIterator;
import infovis.visualization.ColorVisualization;

import java.awt.Color;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;


/**
 * Color nominal columns.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.11 $
 */
public class NominalColor extends ColorVisualization {
    private int[]  category;
    transient Color[] cache;
    int fixedCategories = 0;
    HashMap categories = new HashMap();

    /**
     * Creates a new NominalColor object.
     *
     * @param column the Column,
     * @param numcategories number of categories.
     */
    public NominalColor(Column column, int numcategories) {
        super(column);
        computeCategories(numcategories, 0, 1, 1);
    }
    
    public NominalColor(Column column) {
        this(column, 30);
    }

    /**
     * DOCUMENT ME!
     *
     * @param numcategories DOCUMENT ME!
     * @param startHue DOCUMENT ME!
     * @param saturation DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public void computeCategories(int numcategories, float startHue,
                                  float saturation, float value) {
        if (colorColumn == null) return;
        if (category == null || category.length != numcategories) {
            category = new int[numcategories];
            cache = new Color[numcategories];
            for (int i = 0; i < numcategories; i++) {
                category[i] = Color.HSBtoRGB((float)i / numcategories+startHue, saturation, value);
            }
        }
        computeHash();
    }
    
    public int getColorIndex(int row) {
        if (colorColumn == null) return -1;
        if (colorColumn.isValueUndefined(row))
            return -1;
        Object v = categories.get(colorColumn.getValueAt(row));
        if (v == null) return -1;
        int index = ((Integer)v).intValue();
        return index % category.length;        
    }

    /**
     * @see infovis.visualization.ColorVisualization#getColorValue(int)
     */
    public int getColorValue(int row) {
        int index = getColorIndex(row);
        if (index == -1) return -1;
        return category[index % category.length];
    }
    
    public Color getColor(int row) {
        int index = getColorIndex(row);
        if (index == -1) return null;
        Color c = cache[index];
        if (c == null) {
            c = new Color(getColorValue(row));
            cache[index] = c;
        }
        
        return c;
    }
    
    public void computeHash() {
        if (colorColumn == null) return;
        //TODO try to keep previous categories
        for (RowIterator iter = colorColumn.iterator(); iter.hasNext(); ) {
            int row = iter.nextRow();
            String value = colorColumn.getValueAt(row);
            if (! categories.containsKey(value)) {
                categories.put(value, new Integer(categories.size()));
            }
        }
    }
    
    /**
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        computeCategories(30, 0, 1, 1);
    }
}
