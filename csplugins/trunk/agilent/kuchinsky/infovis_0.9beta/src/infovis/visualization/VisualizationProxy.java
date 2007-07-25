/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.*;
import infovis.column.BooleanColumn;
import infovis.column.FilterColumn;
import infovis.utils.Permutation;
import infovis.utils.RowIterator;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;

import cern.colt.list.IntArrayList;


/**
 * Proxy of a Visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.32 $
 */
public class VisualizationProxy implements Visualization {
    protected Visualization visualization;
    
    /**
     * Constructor.
     * @param visualization the visualization
     */
    public VisualizationProxy(Visualization visualization) {
        setVisualization(visualization);
    }
    
    /**
     * Returns the visualization.
     * @return the visualization
     */
    public Visualization getVisualization() {
        return visualization;
    }
    
    /**
     * Sets the visualization.
     * @param vis the visualization
     */
    public void setVisualization(Visualization vis) {
        this.visualization = vis;
    }

    /**
     * {@inheritDoc}
     */
    public Visualization findVisualization(Class cls) {
        if (cls.isAssignableFrom(getClass()))
            return this;
        return visualization.findVisualization(cls);
    }
    
    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization(int index) {
        if (index == 0)
            return visualization;
        return null;
    }
    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        visualization.addPropertyChangeListener(l);
    }
    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        visualization.addPropertyChangeListener(propertyName, listener);
    }
    /**
     * {@inheritDoc}
     */
    public LabeledItem createLabelItem(int row) {
        return visualization.createLabelItem(row);
    }
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        visualization.dispose();
    }
    /**
     * {@inheritDoc}
     */
    public Rectangle2D getBounds() {
        return visualization.getBounds();
    }
    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return visualization.getComponent();
    }
    /**
     * {@inheritDoc}
     */
    public FilterColumn getFilter() {
        return visualization.getFilter();
    }
    /**
     * {@inheritDoc}
     */
    public VisualizationInteractor getInteractor() {
        return visualization.getInteractor();
    }
    /**
     * {@inheritDoc}
     */
    public ItemRenderer getItemRenderer() {
        return visualization.getItemRenderer();
    }
    /**
     * {@inheritDoc}
     */
    public short getOrientation() {
        return visualization.getOrientation();
    }
    /**
     * {@inheritDoc}
     */
    public JComponent getParent() {
        return visualization.getParent();
    }
    /**
     * {@inheritDoc}
     */
    public Permutation getPermutation() {
        return visualization.getPermutation();
    }
    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize() {
        return visualization.getPreferredSize();
    }
    /**
     * {@inheritDoc}
     */
    public int getRowAtIndex(int index) {
        return visualization.getRowAtIndex(index);
    }
    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        return visualization.getRowCount();
    }
    /**
     * {@inheritDoc}
     */
    public int getRowIndex(int row) {
        return visualization.getRowIndex(row);
    }
    /**
     * {@inheritDoc}
     */
    public BooleanColumn getSelection() {
        return visualization.getSelection();
    }
    /**
     * {@inheritDoc}
     */
    public Shape getShapeAt(int row) {
        return visualization.getShapeAt(row);
    }
    /**
     * {@inheritDoc}
     */
    public Table getTable() {
        return visualization.getTable();
    }
    /**
     * {@inheritDoc}
     */
    public Column getVisualColumn(String name) {
        return visualization.getVisualColumn(name);
    }
    /**
     * {@inheritDoc}
     */
    public Iterator getVisualColumnIterator() {
        return visualization.getVisualColumnIterator();
    }
    /**
     * {@inheritDoc}
     */
    public VisualColumnDescriptor getVisualColumnDescriptor(String name) {
        return visualization.getVisualColumnDescriptor(name);
    }
    /**
     * {@inheritDoc}
     */
    public void fireVisualColumnDescriptorChanged(String name) {
        visualization.fireVisualColumnDescriptorChanged(name);
    }
    /**
     * {@inheritDoc}
     */
    public void invalidate() {
        visualization.invalidate();
    }
    /**
     * {@inheritDoc}
     */
    public void invalidate(Column c) {
        visualization.invalidate(c);
    }
    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int row) {
        return visualization.isFiltered(row);
    }
    /**
     * {@inheritDoc}
     */
    public RowIterator iterator() {
        return visualization.iterator();
    }
    /**
     * {@inheritDoc}
     */
    public RowIterator reverseIterator() {
        return visualization.reverseIterator();
    }
    /**
     * {@inheritDoc}
     */
    public void validateShapes(Rectangle2D bounds) {
        visualization.validateShapes(bounds);
    }
    /**
     * {@inheritDoc}
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        visualization.paint(graphics, bounds);
    }
    /**
     * {@inheritDoc}
     */
    public void print(Graphics2D graphics, Rectangle2D bounds) {
        visualization.print(graphics, bounds);
    }
    /**
     * {@inheritDoc}
     */
    public IntArrayList pickAll(Rectangle2D hitBox, Rectangle2D bounds,
            IntArrayList pick) {
        return visualization.pickAll(hitBox, bounds, pick);
        }
    /**
     * {@inheritDoc}
     */
    public Set pickAll(Rectangle2D hitBox, Rectangle2D bounds, Set pick) {
        return visualization.pickAll(hitBox, bounds, pick);
    }
    /**
     * {@inheritDoc}
     */
    public int pickTop(double x, double y, Rectangle2D bounds) {
        return visualization.pickTop(x, y, bounds);
    }
    /**
     * {@inheritDoc}
     */
    public int pickTop(Rectangle2D hitBox, Rectangle2D bounds) {
        return visualization.pickTop(hitBox, bounds);
    }
    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        visualization.removePropertyChangeListener(l);
    }
    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        visualization.removePropertyChangeListener(
                propertyName,
                listener);
    }
    /**
     * {@inheritDoc}
     */
    public void repaint() {
        visualization.repaint();
    }
    /**
     * {@inheritDoc}
     */
    public void setPermutation(Permutation perm) {
        visualization.setPermutation(perm);
    }
    /**
     * {@inheritDoc}
     */
    public void setInteractor(VisualizationInteractor inter) {
        visualization.setInteractor(inter);
    }
    /**
     * {@inheritDoc}
     */
    public void setItemRenderer(ItemRenderer ir) {
        visualization.setItemRenderer(ir);
    }
    /**
     * {@inheritDoc}
     */
    public void setOrientation(short orientation) {
        visualization.setOrientation(orientation);
    }
    /**
     * {@inheritDoc}
     */
    public void setParent(JComponent parent) {
        visualization.setParent(parent);
    }
    /**
     * {@inheritDoc}
     */
    public void setShapeAt(int row, Shape s) {
        visualization.setShapeAt(row, s);
    }
    /**
     * {@inheritDoc}
     */
    public boolean setVisualColumn(String name, Column column) {
        return visualization.setVisualColumn(name, column);
    }
    /**
     * {@inheritDoc}
     */
    public Table getRulerTable() {
        return visualization.getRulerTable();
    }
}
