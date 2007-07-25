/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis;

import infovis.column.*;
import infovis.utils.*;
import infovis.visualization.*;
import infovis.visualization.magicLens.LabeledComponent;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.JComponent;

import cern.colt.list.IntArrayList;


/**
 * A visualization implements a set of services to visualize
 * a table data structure and allow interaction.
 * 
 * <p>A visualization is responsible for computing the position
 * and shape of graphical marks that will be used to visualize
 * the items of its table.  Some visualizations delegate the
 * computation of this layout to <code>Layout</code> objects.
 * Others compute it directly.
 * 
 * <p>Visualizations also maintains an extensible set of 
 * <emph>visual attributes</emph>.
 * A visual attribute can for example be "color", or "alpha" (transparency); 
 * it specify some part of the visual appearance of each displayed item.
 * A visual attribute can be associated with a table attribute (a column)
 * to specify that the color or size should be computed from a specified 
 * attribute.  
 * 
 * <p>For example, if a table has a "name" column and a "price" column,
 * the name can be associated with the color and the price to the size in
 * a visualization using the following lines:</p>
 * 
 * <pre>
 * visualization.setVisualColumn("color", visualization.getTable().getColumn("name"));
 * visualization.setVisualColumn("size", visualization.getTable().getColumn("price"));
 * </pre>
 * 
 * <p>Visualization also implements <emph>picking</emph> and can maintain an 
 * <code>Interactor</code> object for managing the interaction.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.66 $
 */
public interface Visualization extends LabeledComponent, Orientable {
    /* Name of the visual dimensions managed by the visualization. */

    /** Name of the hover visual column */
    public static final String VISUAL_HOVER = "hover";
    /** Name of the selection visual column */
    public static final String VISUAL_SELECTION = "selection";
    /** Name of the filter visual column */
    public static final String VISUAL_FILTER = "filter";
    /** Name of the label visual column */
    public static final String VISUAL_LABEL = "label";
    /** Name of the color visual column */
    public static final String VISUAL_COLOR = "color";
    /** Name of the size visual column */
    public static final String VISUAL_SIZE = "size";
    /** Name of the alpha visual column */
    public static final String VISUAL_ALPHA = "alpha";
    /** Name of the shape visual column */
    public static final String VISUAL_SHAPE = "#shape";
    
    /** Name of the optional IntColumn managing the permutation. */
    public static final String PERMUTATION_COLUMN = "#permutation";
    /** Name of the optional IntColumn managing the inverse permutation. */
    public static final String INVERSEPERMUTATION_COLUMN = "#inversePermutation";

    /** Name of the property for parent change notification*/
    public static final String PROPERTY_PARENT = "parent";
    /** Name of the property for permutation change notification*/
    public static final String PROPERTY_PERMUTATION = "permutation";
    /** Name of the property for orientation change notification*/
    public static final String PROPERTY_ORIENTATION = "orientation";
    /** Name of the property for interactor change notification*/
    public static final String PROPERTY_INTERACTOR = "interactor";
    /** Name of the property for layout change notification*/
    public static final String PROPERTY_LAYOUT = "layout";
    /** Name of the property for ItermRenderer change notification*/
    public static final String PROPERTY_ITEM_RENDERER = "itemRenderer";
    /** Name of the property for Ruler change notification*/
    public static final String PROPERTY_RULERS= "rulers";
    /** Prefix properties for VisualColumnDescriptor change notification */ 
    public static final String VC_DESCRIPTOR_PROPERTY_PREFIX = "VC_DESCRIPTOR_";
        
    /**
     * Returns the visualization of a specified class from a stack of visualizations or null.
     * @param cls the specified class
     * @return the visualization of a specified class from a stack of visualizations or null.
     */
    public Visualization findVisualization(Class cls);
    
    /**
     * Returns a dependent Visualization used by this Visualization in its stack.
     *   
     * @param index
     * @return a dependent Visualization used by this Visualization in its stack.
     */
    public Visualization getVisualization(int index);
    
    /**
     * Releases all the resources used by the visualization.
     *
     */
    public void dispose();

    /**
     * Returns the Table.
     *
     * @return the Table.
     */
    public Table getTable();
    
    /**
     * Returns the last bounds allocated to this visualization.
     * 
     * @return the last bounds allocated to this visualization.
     */
    public Rectangle2D getBounds();

    /**
     * Returns the owning VisualizationPanel.
     *
     * @return the owning VisualizationPanel.
     */
    public JComponent getParent();

    /**
     * Sets the owning VisualizationPanel.
     *
     * @param parent owning VisualizationPanel.
     */
    public void setParent(JComponent parent);

    // Repaint/recomputeShape management
    /**
     * Invalidates the contents of the Visualization if the column has
     * requested so.  Otherwise, just repaint.
     *
     * @param c the Column triggering the invalidate/repaint.
     */
    public void invalidate(Column c);

    /**
     * Invalidates the contents of the Visualization.
     */
    public void invalidate();

    /**
     * Trigger a repaint on the visualization pane.
     */
    public void repaint();
    
    /**
     * Associate a column to a visual dimension.  For example, the
     * color can be set according to a column containing a date.  For
     * that, you would call the method as: 
     * <code>vis.setVisualColumn("color", dateColumn)</code>
     * 
     * @param name The name of the visual dimension 
     * @param column the column associated with the visual dimension 
     * or <code>null</code>.
     * 
     * @return <code>true</code> is the visualization has been changed.
     */
    public boolean setVisualColumn(String name, Column column);
    
    /**
     * Returns the column associated with a specified visual dimension
     * or <code>null</code>.
     * 
     * @param name the name of the visual dimension
     * @return the column associated with a specified visual dimension
     * or <code>null</code>.
     */
    public Column getVisualColumn(String name);
    
    /**
     * Returns the <code>VisualColumnDescriptor</code> associated
     * with a specified visual dimension.
     *  
     * @param name the name of the visual dimension
     * @return the <code>VisualColumnDescriptor</code> associated
     * with a specified visual dimension.
     */
    public VisualColumnDescriptor getVisualColumnDescriptor(String name);

    /**
     * Returns an <code>Iterator</code> on the names of all the
     * visual columns defined by this visualization.
     *  
     * @return an <code>Iterator</code> on the names of all the
     * visual columns defined by this visualization.
     */
    public Iterator getVisualColumnIterator();
    
    /**
     * Fires property change notifications for the specified Visual
     * Column Descriptor.  The property will be called
     * <code>"VC_DESCRIPTOR_"+name</code> or 
     * <code>Visualization.VC_DESCRIPTOR_PROPERTY_PREFIX+name</code>
     * 
     * @param name the <code>VisualColumnDescriptor</code> name
     */
    public void fireVisualColumnDescriptorChanged(String name);
    
    /**
     * Returns the root {@link ItemRenderer} responsible for
     * rendering the items of this visualization.
     *   
     * @return the root {@link ItemRenderer} responsible for
     * rendering the items managed by this visualization.
     */
    public ItemRenderer getItemRenderer();
    
    /**
     * Sets the root {@link ItemRenderer} responsible for
     * rendering the items of this visualization.
     * 
     * <p>If the specified {@link ItemRenderer} is not instanciated,
     * it will be instantiated by this method.</p>
     * 
     * @param ir the root {@link ItemRenderer} responsible for
     * rendering the items of this visualization.
     * 
     */
    public void setItemRenderer(ItemRenderer ir);

    // Management of managed Columns
    /**
     * Returns the current {@link BooleanColumn} managing the selection
     * of this <code>Visualization</code>.
     * 
     * This is a facade method since the selection visual column can
     * be obtained by
     * <code>VisualSelection.get(vis).getSelectionColumn()</code>.
     *
     * @return the current {@link BooleanColumn} managing the selection
     * of this <code>Visualization</code>
     */
    public BooleanColumn getSelection();

    /**
     * Returns the {@link FilterColumn} managing the dynamic queries
     * of the visualization.

     * This is a facade method since the filter visual column can
     * be obtained by
     * <code>VisualFilter.get(vis).getFilterColumn()</code>.
     *
     * @return the {@link FilterColumn} of the visualization. 
     */
    public FilterColumn getFilter();

    /**
     * Returns <code>true</code> if the row is filtered.
     *
     * This is a facade method since the filter visual column can
     * be obtained by
     * <code>VisualFilter.get(vis).getFilterColumn().isFiltered(row)</code>.
     * 
     * @param row the row.
     *
     * @return <code>true</code> if the row is filtered.
     */
    public boolean isFiltered(int row);

    // Painting    
    /**
     * Returns the preferred dimension of the Visualization or 
     * <code>null</code> if the Visualization can adapt to any dimension.
     * 
     * @return the preferred dimension of the Visualization or
     * <code>null</code> if the Visualization can adapt to any dimension.
     */
    public Dimension getPreferredSize();

    
    /**
     * Recomputes the shapes associated with the rows
     * if the visualization has changed or the bounds have changed.
     *
     * @param bounds the bounding box of the visualization.
     */
    public abstract void validateShapes(Rectangle2D bounds);

    /**
     * Method for painting the visualization.
     *
     * @param graphics the graphics.
     * @param bounds the bounding box of the visualization.
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds);

    /**
     * Method for printing the visualization.
     *
     * @param graphics the graphics.
     * @param bounds the bounding box of the visualization.
     */
    public void print(Graphics2D graphics, Rectangle2D bounds);

    /**
     * Returns the shape of stored for a specified row or null if none is
     * store.
     *
     * @param row the row.
     *
     * @return the shape of stored for a specified row or null if none is
     *         store.
     */
    public Shape getShapeAt(int row);

    /**
     * Associate a shape with a specified row
     *
     * @param row the row.
     * @param s the shape.
     */
    public void setShapeAt(int row, Shape s);
    
    /**
     * Returns a table of Rulers. 
     * 
     * @return a table of Rulers or 
     * <code>null</code> if the visualization doesn't support rulers.
     */
    public Table getRulerTable();

    // Excentric Labels
    
    /**
     * Returns the {@link infovis.visualization.magicLens.LabeledComponent.LabeledItem} for a specified row.
     * 
     * @return  the {@link infovis.visualization.magicLens.LabeledComponent.LabeledItem} for a specified row.
     */
    public LabeledItem createLabelItem(int row);
    
    // Picking
    /**
     * Pick the top item.
     *
     * @param x the X coordinate.
     * @param y the Y coordinate.
     * @param bounds the bounding box of the visualization.
     *
     * @return int the index of the item on top.
     */
    public int pickTop(double x, double y, Rectangle2D bounds);

    /**
     * Pick the top item.
     *
     * @param hitBox the bounds where the top item is searched.
     * @param bounds the total bounds where the visualization is displayed.
     *
     * @return int the index of the item on top.
     */
    public int pickTop(Rectangle2D hitBox, Rectangle2D bounds);

    /**
     * Pick all the items under a rectangle.
     *
     * @param hitBox the bounds where the top item is searched.
     * @param bounds the total bounds where the visualization is displayed.
     * @param pick an {@link IntArrayList} that will contain each row of items
     *        intersecting the hitBox.
     *
     * @return int the index of the item on top.
     */
    public IntArrayList pickAll(
            Rectangle2D hitBox, 
            Rectangle2D bounds,
            IntArrayList pick);

    /**
     * Returns the {@link VisualizationInteractor} associated with
     * this <code>Visualization</code> or <code>null</code> if no
     * interaction is managed.
     * 
     * @return the {@link VisualizationInteractor} associated with
     * this <code>Visualization</code> or <code>null</code> if no
     * interaction is managed.
     */
    public VisualizationInteractor getInteractor();
    
    /**
     * Sets the {@link VisualizationInteractor} associated with
     * this <code>Visualization</code> or <code>null</code> if no
     * interaction is desired.
     * 
     * @param inter the {@link VisualizationInteractor} to set.
     */
    public void setInteractor(VisualizationInteractor inter);

    // Management of permutations
    /**
     * Returns the current permutation.
     *
     * @return the current permutation.
     */
    public Permutation getPermutation();

    /**
     * Sets the permutation using a {@link RowComparator}.
     *
     * @param comparator The comparator used to compute the permutation
     */
    public void setPermutation(RowComparator comparator);

    /**
     * Returns the row at a specified permuted index.
     *
     * @param index the index.
     *
     * @return the row at a specified permuted index.
     */
    public int getRowAtIndex(int index);

    /**
     * Returns the index at a specified permuted row.
     *
     * @param row the row.
     *
     * @return the index at a specified permuted row.
     */
    public int getRowIndex(int row);

    /**
     * Returns a <code>RowIterator</code> taking the permutation into
     * account.
     *
     * @return a <code>RowIterator</code> taking the permutation into
     *         account.
     */
    public RowIterator iterator();

    /**
     * Adds a PropertyChangeListener to the listener list.
     *
     * The listener is registered for all properties.
     * See the fields in PNode and subclasses that start
     * with PROPERTY_ to find out which properties exist.
     * @param l  The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property. See the fields in PNode and subclasses that start
     * with PROPERTY_ to find out which properties are supported.
     * @param propertyName  The name of the property to listen on.
     * @param listener  The PropertyChangeListener to be added
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param l  The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(PropertyChangeListener l);


    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
