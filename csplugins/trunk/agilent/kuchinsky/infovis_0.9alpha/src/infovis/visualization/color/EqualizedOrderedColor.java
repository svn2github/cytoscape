/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.color;

import infovis.column.NumberColumn;
import infovis.utils.RowIterator;

import java.awt.Color;

import cern.colt.list.DoubleArrayList;
import cern.jet.stat.quantile.DoubleQuantileFinder;
import cern.jet.stat.quantile.QuantileFinderFactory;

/**
 * Class EqualizedOrderedColor
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class EqualizedOrderedColor extends OrderedColor {
    protected DoubleQuantileFinder finder; 
    protected DoubleArrayList quantiles;
    protected boolean usingQuantiles = true;

    public EqualizedOrderedColor(NumberColumn column, Color[] ramp) {
        super(column, ramp);
    }
    
    public EqualizedOrderedColor(
            NumberColumn column,
            Color start,
            Color end) {
        super(column, start, end);
    }

    public EqualizedOrderedColor(NumberColumn column) {
        this(column, defaultRamp);
    }

    public void update() {
        super.update();
        if (column == null) {
            quantiles = null;
            return;
        }
        finder =
            QuantileFinderFactory.newDoubleQuantileFinder(
                    true, // known_N 
                    column.size(), // N
                    0, // epsilon 
                    0, // delta 
                    65, // quantiles 
                    null); // generator
        int cnt = 0;
        for (RowIterator iter = column.iterator(); iter.hasNext(); ) {
            finder.add(column.getDoubleAt(iter.nextRow()));
            cnt++;
        }
        if (cnt == 0) {
            quantiles = null;
            return;
        }
        quantiles = QuantileFinderFactory.newEquiDepthPhis(64);
        quantiles = finder.quantileElements(quantiles);
    }

    public Color getColor(int row) {
        if (! usingQuantiles) {
            return super.getColor(row);
        }
        if (quantiles == null
                || column.isValueUndefined(row))
            return null;
        double v = column.getDoubleAt(row);
        int index = quantiles.binarySearch(v);
        if (index < 0) {
            index = -index - 1;
        }
        return getCache()[index];
    }

    public DoubleQuantileFinder getFinder() {
        return finder;
    }
    public DoubleArrayList getQuantiles() {
        return quantiles;
    }
    public boolean isUsingQuantiles() {
        return usingQuantiles;
    }
    public void setUsingQuantiles(boolean usingQuantiles) {
        if (this.usingQuantiles == usingQuantiles) return;
        this.usingQuantiles = usingQuantiles;
    }
}