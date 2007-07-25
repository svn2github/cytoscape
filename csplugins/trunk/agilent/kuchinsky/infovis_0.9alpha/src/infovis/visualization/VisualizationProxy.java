/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.*;
import infovis.column.*;
import infovis.utils.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JComponent;

import cern.colt.list.IntArrayList;


/**
 * Class VisualizationProxy
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.30 $
 */
public class VisualizationProxy implements Visualization {
    protected Visualization visualization;
    
    public VisualizationProxy(Visualization visualization) {
        setVisualization(visualization);
    }
    
    public Visualization getVisualization() {
        return visualization;
    }
    
    public void setVisualization(Visualization vis) {
        this.visualization = vis;
    }

    public Visualization findVisualization(Class cls) {
        if (cls.isAssignableFrom(getClass()))
            return this;
        return visualization.findVisualization(cls);
    }
    
    public Visualization getVisualization(int index) {
        if (index == 0)
            return visualization;
        return null;
    }
    public void addPropertyChangeListener(PropertyChangeListener l) {
        visualization.addPropertyChangeListener(l);
    }
    public void addPropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        visualization.addPropertyChangeListener(propertyName, listener);
    }
    public LabeledItem createLabelItem(int row) {
        return visualization.createLabelItem(row);
    }
    public void dispose() {
        visualization.dispose();
    }
    public Rectangle2D getBounds() {
        return visualization.getBounds();
    }
    public JComponent getComponent() {
        return visualization.getComponent();
    }
    public FilterColumn getFilter() {
        return visualization.getFilter();
    }
    public VisualizationInteractor getInteractor() {
        return visualization.getInteractor();
    }
    public ItemRenderer getItemRenderer() {
        return visualization.getItemRenderer();
    }
    public short getOrientation() {
        return visualization.getOrientation();
    }
    public JComponent getParent() {
        return visualization.getParent();
    }
    public Permutation getPermutation() {
        return visualization.getPermutation();
    }
    public Dimension getPreferredSize() {
        return visualization.getPreferredSize();
    }
    public int getRowAtIndex(int index) {
        return visualization.getRowAtIndex(index);
    }
    public int getRowIndex(int row) {
        return visualization.getRowIndex(row);
    }
    public BooleanColumn getSelection() {
        return visualization.getSelection();
    }
    public Shape getShapeAt(int row) {
        return visualization.getShapeAt(row);
    }
    public Table getTable() {
        return visualization.getTable();
    }
    public Column getVisualColumn(String name) {
        return visualization.getVisualColumn(name);
    }
    public Iterator getVisualColumnIterator() {
        return visualization.getVisualColumnIterator();
    }
    public VisualColumnDescriptor getVisualColumnDescriptor(String name) {
        return visualization.getVisualColumnDescriptor(name);
    }
    public void fireVisualColumnDescriptorChanged(String name) {
        visualization.fireVisualColumnDescriptorChanged(name);
    }
    public void invalidate() {
        visualization.invalidate();
    }
    public void invalidate(Column c) {
        visualization.invalidate(c);
    }
    public boolean isFiltered(int row) {
        return visualization.isFiltered(row);
    }
    public RowIterator iterator() {
        return visualization.iterator();
    }
    public void validateShapes(Rectangle2D bounds) {
        visualization.validateShapes(bounds);
    }
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        visualization.paint(graphics, bounds);
    }
    public void print(Graphics2D graphics, Rectangle2D bounds) {
        visualization.print(graphics, bounds);
    }
    public IntArrayList pickAll(Rectangle2D hitBox, Rectangle2D bounds,
            IntArrayList pick) {
        return visualization.pickAll(hitBox, bounds, pick);
    }
    public Set pickAll(Rectangle2D hitBox, Rectangle2D bounds, Set pick) {
        return visualization.pickAll(hitBox, bounds, pick);
    }
    public int pickTop(double x, double y, Rectangle2D bounds) {
        return visualization.pickTop(x, y, bounds);
    }
    public int pickTop(Rectangle2D hitBox, Rectangle2D bounds) {
        return visualization.pickTop(hitBox, bounds);
    }
    public void removePropertyChangeListener(PropertyChangeListener l) {
        visualization.removePropertyChangeListener(l);
    }
    public void removePropertyChangeListener(String propertyName,
            PropertyChangeListener listener) {
        visualization.removePropertyChangeListener(
                propertyName,
                listener);
    }
    public void repaint() {
        visualization.repaint();
    }
    public void setPermutation(RowComparator comparator) {
        visualization.setPermutation(comparator);
    }
    public void setInteractor(VisualizationInteractor inter) {
        visualization.setInteractor(inter);
    }
    public void setItemRenderer(ItemRenderer ir) {
        visualization.setItemRenderer(ir);
    }
    public void setOrientation(short orientation) {
        visualization.setOrientation(orientation);
    }
    public void setParent(JComponent parent) {
        visualization.setParent(parent);
    }
    public void setShapeAt(int row, Shape s) {
        visualization.setShapeAt(row, s);
    }
    public boolean setVisualColumn(String name, Column column) {
        return visualization.setVisualColumn(name, column);
    }
    
//    public Ruler getRuler(int i) {
//        return visualization.getRuler(i);
//    }
    
    public Table getRulerTable() {
        return visualization.getRulerTable();
    }
}
