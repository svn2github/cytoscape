/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization.treemap;


import infovis.column.NumberColumn;
import infovis.utils.IntStack;
import infovis.visualization.Orientable;

/**
 * Class TreemapStrip
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class TreemapStrip extends IntStack implements Orientable {
    protected double length;
    protected double scale;
    protected short orientation = ORIENTATION_INVALID;
    protected NumberColumn column;

    protected double width;
    protected double surface;
    protected double min_area;
    protected double max_area;
    protected double worst;

    TreemapStrip(NumberColumn column, double length, double scale) {
        this.length = length;
        this.column = column;
        this.scale = scale;
    }

    public short getOrientation() {
        return orientation;
    }

    public void setOrientation(short orientation) {
        this.orientation = orientation;
    }

    public void clear() {
        super.clear();
        width = 0;
        surface = 0;
        min_area = 0;
        max_area = 0;
        worst = 0;
    }

    public void updateMinMaxArea() {
        if (max_area == 0 || min_area == 0) {
            min_area = Double.MAX_VALUE;
            for (int i = 0; i < size(); i++) {
                double area = getSurfaceAt(get(i));
                if (area == 0)
                    continue;
                if (area < min_area)
                    min_area = area;
                if (area > max_area)
                    max_area = area;
            }
            if (min_area > max_area)
                min_area = max_area;
        }
    }

    public double getMinArea() {
        updateMinMaxArea();
        return min_area;
    }

    public double getMaxArea() {
        updateMinMaxArea();
        return max_area;
    }

    protected void init(double s) {
        surface = s;
        width = surface / length;
        min_area = surface;
        max_area = surface;
        worst = Math.max(length / width, width / length);
    }

    public void add(int row) {
        double area = getSurfaceAt(row);
        if (area == 0)
            return;
        super.add(row);
        if (surface == 0 && area != 0) {
            init(area);
        }
        else if (area != 0) {
            surface += area;
            double s2 = area * area;
            double w2 = length * length;

            updateMinMaxArea();            
            min_area = (area < min_area) ? area : min_area;
            max_area = (area > max_area) ? area : max_area;

            worst =
                Math.max((w2 * max_area) / s2, s2 / (w2 * min_area));
        }
    }
    
    public int pop() {
        int row = super.pop();
        double area = getSurfaceAt(row);
        surface -= area;
        min_area = 0;
        max_area = 0;
        return row;
    }

    public double computeWorstAdding(int row) {
        double area = getSurfaceAt(row);
        if (area == 0 || size() == 0) {
            return worst;
        }
        double s = this.surface + area;
        if (s == 0)
            return worst;
        double s2 = s * s;
        double w2 = length * length;
        double cur_min_area = (area < min_area) ? area : min_area;
        double cur_max_area = (area > max_area) ? area : max_area;

        double cur_worst =
            Math.max(
                (w2 * cur_max_area) / s2,
                s2 / (w2 * cur_min_area));
        return cur_worst;
    }

    public boolean isWorstAdding(int row) {
        return computeWorstAdding(row) > worst;
    }

    public boolean maybeAdd(int row) {
        if (isWorstAdding(row)) {
            return false;
        }
        else {
            add(row);
            return true;
        }
    }
    
    public double getSurfaceAt(int row) {
        return column.getDoubleAt(row) * scale;
    }
    
    public double getWidth() {
        return surface / length; 
    }

    public double getWorst() {
        return worst;
    }

}
