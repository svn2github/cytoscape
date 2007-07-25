/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.Column;
import infovis.Visualization;
import infovis.column.BooleanColumn;
import infovis.column.FilterColumn;
import infovis.utils.*;
import infovis.visualization.*;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;

import cern.colt.list.IntArrayList;

/**
 * Visualization wrapping a DefaultExcentricLabel.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class ExcentricLabelVisualization extends VisualizationProxy {
    protected ExcentricLabels excentric;
    protected VisualizationInteractor interactor;
    protected JComponent parent;

    public ExcentricLabelVisualization(
        Visualization visualization,
        ExcentricLabels el) {
        super(visualization);
        if (el == null) {
            el = new DefaultExcentricLabels();
        }
        this.excentric = el;
        this.excentric.setVisualization(visualization);
        //setFisheye(visualization.getFisheye());
    }

    public ExcentricLabelVisualization(Visualization visualization) {
        this(visualization, null);
    }
    
    public void setParent(JComponent parent) {
        JComponent old = getParent();
        if (old != parent) {
            uninstall(old);
            install(parent);
            this.parent = parent;
        }
    }
    
    public JComponent getParent() {
        return parent;
    }
    
    protected void uninstall(JComponent parent) {
        if (parent == null || interactor == null) return;
        if (getInteractor() != null) {
            getInteractor().uninstall(parent);
        }
    }

    protected void install(JComponent parent) {
        if (getInteractor() != null) {
            getInteractor().install(parent);
        }        
    }
    
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        if (excentric != null)
            excentric.paint(graphics, bounds);
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
    }

    public void addPropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {

    }
    public LabeledItem createLabelItem(int row) {
        return null;
    }
    public void dispose() {
        setExcentric(null);
    }

    public Visualization findVisualization(Class cls) {
        if (cls.isAssignableFrom(this.getClass())) return this;
        return null;
    }

    public void fireVisualColumnDescriptorChanged(String name) {
    }

    public FilterColumn getFilter() {
        return null;
    }
    
    public ItemRenderer getItemRenderer() {
        return null;
    }
    public Permutation getPermutation() {
        return null;
    }

    public int getRowAtIndex(int index) {
        return -1;
    }
    
    public int getRowIndex(int row) {
        return -1;
    }

    public BooleanColumn getSelection() {
        return null;
    }
    public Shape getShapeAt(int row) {
        return null;
    }
    public Column getVisualColumn(String name) {
        return null;
    }
    public VisualColumnDescriptor getVisualColumnDescriptor(String name) {
        return null;
    }
    public Iterator getVisualColumnIterator() {
        return null;
    }
    public Visualization getVisualization(int index) {
        return null;
    }
    public void invalidate() {
    }
    public void invalidate(Column c) {
    }
    public boolean isFiltered(int row) {
        return false;
    }
    public RowIterator iterator() {
        return null;
    }
    public IntArrayList pickAll(
            Rectangle2D hitBox,
            Rectangle2D bounds,
            IntArrayList pick) {
        return pick;
    }
    public int pickTop(double x, double y, Rectangle2D bounds) {
        return -1;
    }
    public int pickTop(Rectangle2D hitBox, Rectangle2D bounds) {
        return -1;
    }
    public void print(Graphics2D graphics, Rectangle2D bounds) {
        paint(graphics, bounds);
    }
    public void removePropertyChangeListener(PropertyChangeListener l) {
    }
    public void removePropertyChangeListener(
            String propertyName,
            PropertyChangeListener listener) {
    }
    public void setItemRenderer(ItemRenderer ir) {
    }
    public void setPermutation(RowComparator comparator) {
    }
    public void setShapeAt(int row, Shape s) {
    }
    public boolean setVisualColumn(String name, Column column) {
        return false;
    }
    public JComponent getComponent() {
        return getParent();
    }
    
    public Set pickAll(Rectangle2D hitBox, Rectangle2D bounds, Set pick) {
        return pick;
    }
    public short getOrientation() {
        return ORIENTATION_INVALID;
    }
    public void setOrientation(short orientation) {
    }
    /**
     * Returns the excentric.
     * @return ExcentricLabels
     */
    public ExcentricLabels getExcentric() {
        return excentric;
    }
    
    protected void setExcentric(ExcentricLabels el) {
        if (excentric == el)
            return;
        if (excentric != null) {
            excentric.setVisualization(null);
            if (interactor != null)
                interactor.uninstall(getComponent());
        }
        excentric = el;
        if (excentric != null) {
            excentric.setVisualization(this);
            //setFisheye(super.getFisheye());
            if (interactor != null)
                interactor.install(getComponent());
        }
    }
    
    public static ExcentricLabelVisualization find(Visualization vis) {
        return (ExcentricLabelVisualization)vis
            .findVisualization(ExcentricLabelVisualization.class);
    }

    public void setInteractor(VisualizationInteractor inter) {
        if (this.interactor == inter) return;
        if (this.interactor != null) {
            this.interactor.setVisualization(null);
        }
        this.interactor = inter;
        if (this.interactor != null) {
            this.interactor.setVisualization(this);
        }
    }
    
    public VisualizationInteractor getInteractor() {
        return interactor;
    }
    public boolean isEnabled() {
        return excentric.isEnabled();
    }
    public void setEnabled(boolean enabled) {
        excentric.setEnabled(enabled);
    }
}
