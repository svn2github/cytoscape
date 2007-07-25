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
import infovis.table.TableProxy;
import infovis.utils.*;
import infovis.visualization.magicLens.ExcentricItem;
import infovis.visualization.magicLens.Fisheye;
import infovis.visualization.render.*;
import infovis.visualization.ruler.RulerTable;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.event.*;

import org.apache.log4j.Logger;
import cern.colt.list.IntArrayList;

/**
 * Base class for concrete visualizations.
 * 
 * <p>This class implements a set of services useful for all the
 * concrete visualizations.
 * 
 * <p>A <code>DefaultVisualization</code> needs a <code>Table</code>.  It
 * implements a visualization technique and manages the rendering and
 * interaction with the items.
 * 
 * <p>It interfaces with four subsystems:
 * <ol>
 * <li>The Layout subsystem,
 * <li>The Item Renderer subsystem,
 * <li>The Interaction subsystem,
 * <li>The Rulers subsystem.
 * </ol>
 * 
 * <p>By default, the <code>Layout</code> is specific to the concrete class.
 * The {@link #getLayout()} method returns the used {@link infovis.visualization.Layout}
 * object or <code>null</code> if the visualization doesn't need one.  
 * For example, {@link infovis.table.visualization.ScatterPlotVisualization}
 * has no <code>Layout</code> associated whereas {@link infovis.tree.visualization.TreemapVisualization}
 * uses one.  Specifying the <code>Layout</code> is visualization specific.
 * 
 * <p>Item Renderers are used to render each visualized items.  They form a tree described in
 * {@link infovis.visualization.ItemRenderer}.  This tree can be specified in the constructor of a
 * visualization or is created through an {@link infovis.visualization.render.ItemRendererFactory}.
 * 
 * <p>Item Renderers passed to a visualization can be prototypes or instances (see
 * {@link infovis.visualization.ItemRenderer#isPrototype()}.  When a prototype is passed, it is instantiated
 * by the visualization.  Otherwise, it is used as it is.
 * 
 * <p>The interaction subsystem is meant to associate a
 * {@link infovis.visualization.VisualizationInteractor} to a visualization.  These objects manage the standard
 * event management of the visualizations.  When no <code>VisualInteractor</code> is associated with the visualization,
 * then it has no interaction (at least through this mechanism).
 *
 * <p>The Rulers subsystem provides a mechanism to associate tick marks, rulers and labels to visualizations.
 * Each visualization has to maintain this association when it makes sense.  Scatter plots use it to define the axes
 * rulers.  The method {@link #getRulerTable()} returns a <code>Table</code> containing the items to visualize
 * as rulers, as described in {@link infovis.visualization.ruler.RulerTable}.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.77 $
 */

public class DefaultVisualization extends TableProxy
    implements Visualization, ChangeListener {
    /** The Panel parent of this visualization */
    protected JComponent                 parent;
    /** The permutation of rows used by this visualization. */
    protected Permutation                permutation;
    /** The orientation */
    protected short                      orientation    = ORIENTATION_SOUTH;
    /** The root ItemRenderer */
    protected ItemRenderer               itemRenderer;
    /** The interactor for this visualization */
    protected VisualizationInteractor    interactor;
    /** the Shape column */
    protected ShapeColumn                shapes;
    /** The list of rulers */
    protected RulerTable                 rulers;

    private Map                          visualColumns  = new TreeMap();
    private SwingPropertyChangeSupport   changeSupport;
    private transient boolean            shapesUpdated;
    private transient Rectangle2D.Double bounds         = new Rectangle2D.Double();
    private transient boolean            itemsInstalled = false;
    
    private static final Logger          logger         = 
        Logger.getLogger(DefaultVisualization.class);
    
    /**
     * Creates a new Visualization for a specified table.
     *
     * @param table The table.
     */
    public DefaultVisualization(Table table) {
        this(table, null);
    }
    
    /**
     * Creates a new DefaultVisualization for a specified
     * <code>Table</code> using the specified
     * <code>ItemRenderer</code> for rendering.
     * 
     * @param table the Table
     * @param ir the ItemRenderer or <code>null</code>
     */
    public DefaultVisualization(
        Table table,
        ItemRenderer ir) {
        super(table);
        shapes = new ShapeColumn(VISUAL_SHAPE);
        declareVisualColumns(ir);
    }

    /**
     * Declares the visual columns of this visualization.
     */
    protected void declareVisualColumns(ItemRenderer ir) {
        if (ir == null) {
            ir = ItemRendererFactory.createItemRenderer(this);            
        }
        setItemRenderer(ir);
        DefaultVisualColumn vc = new DefaultVisualColumn(
                VISUAL_SHAPE, false);
        vc.setColumn(shapes);
        putVisualColumn(vc);
    }

    /**
     * {@inheritDoc}
     */
    public Visualization getVisualization(int index) {
        return null;
    }
    
    //TODO declare in Visualization interface
    /**
     * Returns the bounds of a specified visualization,
     * given the bounds of the current visualization.
     * 
     * @param index the visualization index
     * @param bounds the bounds of the current visualization.
     * @return the bounds of a specified visualization
     */
    public Rectangle2D getVisualizationBounds(
            int index, 
            Rectangle2D.Float bounds) {
        Visualization vis = getVisualization(index);
        if (vis == null) {
            return bounds;
        }
        else {
            return vis.getBounds();
        }
    }

    /**
     * Returns the sub-visualization of the specified class
     * or null.
     * 
     * @param cls the visualization class to find
     * @return the sub-visualization of the specified class
     * or null
     */
    public Visualization findVisualization(Class cls) {
        if (cls.isAssignableFrom(getClass()))
            return this;
        int i = 0;
        for (Visualization sub = getVisualization(i++);
            sub != null;
            sub = getVisualization(i++)) {
            Visualization ret = sub.findVisualization(cls);
            if (ret != null)
                return ret;
        }
        return null;
    }

    protected void putVisualColumn(VisualColumnDescriptor vc) {
        String name = vc.getName();
        if (visualColumns.containsKey(name))
            throw new RuntimeException(
                "visual column " + name + " already declared");
        visualColumns.put(name, vc);
        addManagedColumn(vc.getColumn());
    }

    protected void putVisualColumn(VisualColumnProxy vc) {
        visualColumns.put(vc.getName(), vc);
    }
    
    protected void removeVisualColumn(String name) {
        VisualColumnDescriptor vc = 
            (VisualColumnDescriptor)visualColumns.remove(name);
        if  (vc != null) {
            removeManagedColumn(vc.getColumn());
            firePropertyChange(VC_DESCRIPTOR_PROPERTY_PREFIX+name, vc, null);            
        }
    }
    
    /**
     * Triggers a notification from a changed ItemRenderer given its name.
     * 
     * @param name the name of the ItemRenderer
     */
    public void fireVisualColumnDescriptorChanged(String name) {
        if (itemRenderer == null) return; // ignore during the initialization
        VisualColumnDescriptor vc = getVisualColumnDescriptor(name);
        if (vc == null) {
            String msg = "visual column " + name + " not declared";
            logger.error(msg);
            throw new RuntimeException(msg);
        }
        if (vc.isInvalidate()) {
            invalidate();
        }
        else {
            Column c = vc.getColumn();
            if (c != null && c instanceof BasicColumn) {
                BasicColumn col = (BasicColumn) c;
                int row = col.getLastModifiedRow();
                if (row >= 0) {
                    repaint(row);
                }
                else {
                    repaint();
                }
            }
            else
                repaint();
        }
        firePropertyChange(VC_DESCRIPTOR_PROPERTY_PREFIX+name, null, vc);
    }

    /**
     * {@inheritDoc}
     */
    public VisualColumnDescriptor getVisualColumnDescriptor(String name) {
        VisualColumnDescriptor vc =
            (VisualColumnDescriptor) visualColumns.get(name);
        return vc;
    }
    
    /**
     * {@inheritDoc}
     */
    public Iterator getVisualColumnIterator() {
        return visualColumns.keySet().iterator();
    }

    /**
     * Returns <code>true</code> if modifying this column triggers a
     * recomputation of the visualization.
     *
     * @param c the Column.
     *
     * @return <code>true</code> if modifying this column triggers a
     *         recomputation of the visualization.
     */
    public boolean isInvalidateColumn(Column c) {
        if (c == null) return false;
        for (Iterator iter = visualColumns.values().iterator(); iter.hasNext(); ) {
            VisualColumnDescriptor vc = (VisualColumnDescriptor)iter.next();
            if (vc.getColumn() == c && vc.isInvalidate()) {
                return true;
            }
        }
        return false;
    }
    
    protected void removeManagedColumn(Column oldC) {
        if (oldC == null) return;
        oldC.removeChangeListener(this);
    }
    
    protected void addManagedColumn(Column newC) {
        if (newC == null) return;
        newC.addChangeListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public boolean setVisualColumn(String name, Column newC) {
        VisualColumnDescriptor vc = getVisualColumnDescriptor(name);
        if (vc == null) {
            throw new RuntimeException("invalid visual column " + name);
        }
        Column oldC = vc.getColumn();
        if (oldC == newC) {
            return false;
        }

        vc.setColumn(newC);
        addManagedColumn(newC);
        removeManagedColumn(oldC);

        firePropertyChange(name, oldC, newC);
        return true;
    }

    /** 
     * {@inheritDoc}
     */
    public Column getVisualColumn(String name) {
        VisualColumnDescriptor vc = getVisualColumnDescriptor(name);
        if (vc == null)
            return null;
        return vc.getColumn();
    }

    /**
     * {@inheritDoc}
     */
    public void setItemRenderer(ItemRenderer root) {
        if (this.itemRenderer == root) return;
        ItemRenderer old = itemRenderer;
        itemRenderer = null;
        if (root.isPrototype()) {
            root = root.instantiate(this);
        }
        itemRenderer = root;
        unregisterRenderers(old);
        registerRenderers(root, old, false);
        firePropertyChange(PROPERTY_ITEM_RENDERER, old, root);
    }
    
    protected void unregisterRenderers(ItemRenderer old) {
        if (old == null) return;
        for (int i = 0; i < old.getRendererCount(); i++) {
            unregisterRenderers(old.getRenderer(i));
        }
        if (old instanceof VisualColumnDescriptor) {
            VisualColumnDescriptor vc = (VisualColumnDescriptor) old;
            removeVisualColumn(vc.getName());
        }
    }
    
    protected void registerRenderers(
            ItemRenderer ir,
            ItemRenderer old,
            boolean invalidate) {
        for (int i = 0; i < ir.getRendererCount(); i++) {
            registerRenderers(
                    ir.getRenderer(i),
                    old, 
                    invalidate || ir instanceof LayoutVisual);
        }
        if (ir instanceof VisualColumnDescriptor) {
            VisualColumnDescriptor vc = (VisualColumnDescriptor) ir;
            vc.setInvalidate(invalidate);
            putVisualColumn(vc);
            ItemRenderer oldir = AbstractItemRenderer.findNamed(vc.getName(), old);
            if (oldir != null && oldir instanceof VisualColumnDescriptor) {
                VisualColumnDescriptor oldvc = (VisualColumnDescriptor)oldir;
                vc.setFilter(oldvc.getFilter());
                vc.setInvalidate(oldvc.isInvalidate());
                vc.setColumn(oldvc.getColumn()); // triggers fireVisualColumnDescriptorChanged
                addManagedColumn(vc.getColumn());
            }
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public ItemRenderer getItemRenderer() {
        return itemRenderer;
    }
    
    /**
     * Returns an ItemRenderer, given its name.
     * 
     * @param name the name
     * 
     * @return an ItemRenderer, given its name.
     */
    public ItemRenderer getItemRenderer(String name) {
        return getItemRenderer(name, itemRenderer);
    }
    
    /**
     * Returns an ItemRenderer given its name and its root ItemRenderer.
     * @param name the name
     * @param root the root
     * 
     * @return an ItemRenderer
     */
    public static ItemRenderer getItemRenderer(String name, ItemRenderer root) {
        if (name.equals(root.getName())) {
            return root;
        }
        for (int i = 0; i < root.getRendererCount(); i++) {
            ItemRenderer r = getItemRenderer(name, root.getRenderer(i));
            if (r != null) {
                return r;
            }
        }
        return null;
    }

    /**
     * Releases all the resources used by the visualization.
     *
     */
    public void dispose() {
        for (Iterator iter = visualColumns.entrySet().iterator();
            iter.hasNext();
            ) {
            Map.Entry entry = (Map.Entry) iter.next();
            VisualColumnDescriptor vc =
                (VisualColumnDescriptor) entry.getValue();
            vc.setColumn(null);
        }
        getShapes().clear();
        int i = 0;
        for (Visualization v = getVisualization(i++); v != null;
        v = getVisualization(i++)) {
            v.dispose();
        }
        //managedColumns.clear();
    }

    /**
     * Returns the Table.
     *
     * @return the Table.
     */
    public Table getTable() {
        return table;
    }

    /**
     * {@inheritDoc}
     */
    public Rectangle2D getBounds() {
        return bounds;
    }

    /**
     * Returns the owning VisualizationPanel.
     *
     * @return the owning VisualizationPanel.
     */
    public JComponent getParent() {
        return parent;
    }

    /**
     * Sets the owning VisualizationPanel.
     *
     * @param parent owning VisualizationPanel.
     */
    public void setParent(JComponent parent) {
        if (this.parent == parent) return;
        JComponent old = this.parent;
        if (this.parent != null) {
            uninstall(this.parent);
        }
        this.parent = parent;
        if (this.parent != null) {
            install(this.parent);
        }
        firePropertyChange(PROPERTY_PARENT, old, parent);
        int i = 0;
        for (Visualization sub = getVisualization(i++);
            sub != null;
            sub = getVisualization(i++)) {
            sub.setParent(parent);
        }
        invalidate();
    }

    /**
     * Installs the visualization into its Swing component.
     *
     * @param parent the Swing parent component.
     */
    protected void install(JComponent parent) {
        if (getInteractor() != null) {
            getInteractor().install(parent);
        }
    }

    /**
     * Desinstalls the visualization from its Swing component.
     *
     * @param parent the Swing parent component.
     */
    protected void uninstall(JComponent parent) {
        if (getInteractor() != null) {
            getInteractor().uninstall(parent);
        }
    }

    // Repaint/recomputeShape management
    /**
     * Invalidates the contents of the Visualization if the column has
     * requested so.  Otherwise, just repaint.
     *
     * @param c the Column triggering the invalidate/repaint.
     */
    public void invalidate(Column c) {
        if (isInvalidateColumn(c)) {
            invalidate();
        }
        else {
            if (c != null && c instanceof BasicColumn) {
                BasicColumn col = (BasicColumn) c;
                int row = col.getLastModifiedRow();
                if (row >= 0) {
                    repaint(row);
                    return;
                }
            }
            repaint();            
        }
    }
    
    /**
     * Returns the bounds around the visual representation of
     * a specified row.
     * @param row the row
     * @return the visual bounds
     */
    public Rectangle2D getShapeBoundsAt(int row) {
        // TODO modify ItemRenderer to support getTransformedShape
        // since stored shape don't take into account
        // borders, labels and fisheyes
        Shape shape = getShapeAt(row);
        if (shape != null)
            return shape.getBounds2D();
        return null;
    }

    /**
     * Invalidates the contents of the Visualization.
     */
    public void invalidate() {
        if (! shapesUpdated) return;
        shapesUpdated = false;
        if (getParent() != null) {
            if (getParent() instanceof JComponent) {
                JComponent jc = (JComponent)getParent();
                jc.revalidate();
            }
            else {
                getParent().invalidate();
            }
        }
        if (getLayout() != null) {
            getLayout().invalidate(this);
        }
        repaint();
    }
    
    /**
     * Returns true if the visualization is already
     * invalited.
     * @return true if the visualization is already
     * invalited.
     */
    public boolean isInvalidated() {
        return !shapesUpdated;
    }

    /**
     * Trigger a repaint on the visualization pane.
     */
    public void repaint() {
        if (parent != null) {
            if (bounds != null && ! bounds.isEmpty())
                parent.repaint(bounds.getBounds());
            else
                parent.repaint();
            
        }
    }
    
    /**
     * Triggers a repaint for the specified row.
     * @param row the row
     */
    public void repaint(int row) {
        Rectangle2D r = getShapeBoundsAt(row);
        if (r == null) 
            repaint();
        else if (parent != null) {
            parent.repaint(r.getBounds());            
        }

        
    }
    // Management of managed Columns
    /**
     * Returns the current ListSelectionModel of this pickable.
     *
     * @return the current ListSelectionModel of this pickable.
     */
    public BooleanColumn getSelection() {
        VisualSelection vs = VisualSelection.get(this);
        if (vs != null) {
            return vs.getSelection();
        }
        else {
            return null;
        }
    }

    /**
     * Returns the filter.
     *
     * @return FilterColumn
     */
    public FilterColumn getFilter() {
        VisualFilter vf = VisualFilter.get(this);
        return vf == null ? null : vf.getFilterColumn();
    }

    /**
     * Returns <code>true</code> if the row is filtered.
     *
     * @param row the row.
     *
     * @return <code>true</code> if the row is filtered.
     */
    public boolean isFiltered(int row) {
        VisualFilter vf = VisualFilter.get(this);
        if (vf == null) return false;
        return vf.isFiltered(row);
    }

    /**
     * Returns the orientation.
     * @return short
     */
    public short getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation.
     * @param orientation The orientation to set
     */
    public void setOrientation(short orientation) {
        if (this.orientation == orientation) return;
        short old = this.orientation;
        this.orientation = orientation;
        invalidate();
        firePropertyChange(PROPERTY_ORIENTATION, old, orientation);
    }


    // Painting
    
    /**
     * Draw the shape of a specified row onto the specified graphics.
     *
     * @param graphics the graphics.
     * @param row the row.
     */
    public void paintItem(Graphics2D graphics, int row) {
        Shape s = getShapeAt(row);
        if (s != null)
            itemRenderer.paint(graphics, row, s);
    }

    /**
     * Computes the shapes associated with the rows, and store them with
     * setShapeAt.
     * 
     * <p>This method should not be overriden lightly.  It
     * install and desintalls several pieces carfuly.  Implementing
     * the {@link infovis.visualization.Layout} interface is the
     * standard way of defining a new visualization algorithm.
     *
     * @param bounds the bounding box of the visualization.
     */
    public final void computeShapes(Rectangle2D bounds) {
        Layout l = getLayout();
        if (l == null) return;
        if (! itemsInstalled) {
            Table t = getRulerTable();
            try {
                if (t != null) {
                    t.disableNotify();
                }
                getShapes().disableNotify();
                //shapes.clear();
                shapes.setSize(table.getLastRow()+1);
                itemRenderer.install(null);
                itemsInstalled = true;
                getLayout().computeShapes(bounds, this);
            }
            catch(Exception e) {
                logger.error("In ComputeShapes",e);
            }
            finally {
                itemsInstalled = false;
                itemRenderer.uninstall(null);
                getShapes().enableNotify();
                if (t != null) {
                    t.enableNotify();
                    firePropertyChange(PROPERTY_RULERS, null, t);
                }
            }
        }
        else {
            getLayout().computeShapes(bounds, this);
        }
    }
    /**
     * Returns the layout object managed by this visualization or
     * null if the visualization computes the layout by itself.
     * 
     * @return the layout object managed by this visualization or null.
     */
    public Layout getLayout() {
        return null;
    }

    /**
     * Method for painting the visualization.
     *
     * @param graphics the graphics.
     * @param bounds the bounding box of the visualization.
     */
    public void paint(Graphics2D graphics, Rectangle2D bounds) {
        try {
            validateShapes(bounds);
            if (! itemsInstalled) {
                itemsInstalled = true;
                itemRenderer.install(graphics);
            }
            paintItems(graphics, bounds);
        }
        catch(Exception e) {
            logger.error("In Paint",e);
        }
        finally {
            itemsInstalled = false;
            itemRenderer.uninstall(graphics);
        }
    }
    
    /**
     * Paints the sub-visualizations.
     * @param graphics the graphics.
     * @param bounds the bounding box of the visualization.
     */
    public void paintVisualizations(
            Graphics2D graphics, 
            Rectangle2D bounds) {
        Rectangle2D.Float vb = RectPool.allocateRect();
        try {
            int i = 0;
        for (Visualization sub = getVisualization(i); 
            sub != null; 
            sub = getVisualization(++i)) {
            vb.setRect(bounds);
            getVisualizationBounds(i, vb);
            sub.paint(graphics, vb);
        }
        }
        finally {
            RectPool.freeRect(vb);
        }        
    }
    
    /**
     * Prints the visualization on the specified graphics.
     * 
     * @param graphics the graphics.
     * @param bounds the bounding box of the visualization.
     */
    public void print(Graphics2D graphics, Rectangle2D bounds) {
        paint(graphics, bounds);
    }

    /**
     * Method for filtering and painting the items.
     *
     * @param graphics the graphics.
     * @param bounds the bounding box of the visualization.
     */
    public void paintItems(Graphics2D graphics, Rectangle2D bounds) {
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int row = iter.nextRow();
            paintItem(graphics, row);
        }
    }

    /**
     * Checks whether the shapes should be recomputed and call
     * updateShapes then.
     *
     * @param bounds the Visualization bounds.
     */
    public void validateShapes(Rectangle2D bounds) {
        if (!(shapesUpdated && this.bounds.equals(bounds))) {
            try {
                getShapes().disableNotify();
                this.bounds.setRect(bounds);
                computeShapes(bounds);
            }
            finally {
                getShapes().enableNotify();
                shapesUpdated = true;
            }
        }
    }

    /**
     * Returns the ObjectColumn containing the shapes.
     *
     * @return the ObjectColumn containing the shapes.
     */
    public ShapeColumn getShapes() {
        return shapes;
    }
    
    /**
     * Returns the shape of stored for a specified row or null if none is
     * store.
     *
     * @param row the row.
     *
     * @return the shape of stored for a specified row or null if none is
     *         store.
     */
    public Shape getShapeAt(int row) {
        return shapes.get(row);
    }
    
    /**
     * Returns the rectangle at a specified row.
     * @param row the row
     * @return the rectangle or null.
     */
    public Rectangle2D.Float getRectAt(int row) {
        return shapes.getRect(row);
    }
    
    /**
     * Returns a rectangle for the specified row,
     * allocating it if needed.
     * @param row the row
     * @return a rectangle for the specified row,
     * allocating it if needed.
     */
    public Rectangle2D.Float findRectAt(int row) {
        return shapes.findRect(row);
    }
    
    /**
     * Returns the rectangle at the specified row
     * to the rectangle pool.
     * 
     * @param row the row of the rectangle. 
     */
    public void freeRectAt(int row) {
        shapes.freeRect(row);
    }

    /**
     * Associate a shape with a specified row.
     *
     * @param row the row.
     * @param s the shape.
     */
    public void setShapeAt(int row, Shape s) {
        shapes.setExtend(row, s);
    }

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
    public int pickTop(double x, double y, Rectangle2D bounds) {
        Rectangle2D.Float hitBox = RectPool.allocateRect();
        hitBox.setFrame(x, y, 1, 1);
        try {
            return pickTop(hitBox, bounds);
        }
        finally {
            RectPool.freeRect(hitBox);
        }
    }

    /**
     * Pick the top item.
     *
     * @param hitBox the bounds where the top item is searched.
     * @param bounds the total bounds where the visualization is displayed.
     *
     * @return int the index of the item on top.
     */
    public int pickTop(Rectangle2D hitBox, Rectangle2D bounds) {
        validateShapes(bounds);
        for (RowIterator iter = reverseIterator(); iter.hasNext();) {
            int row = iter.nextRow();
            Shape s = getShapeAt(row);
            if (pickItem(hitBox, bounds, s, row))
                return row;
        }
        return -1;
    }
    
    /**
     * Pick all the items under a rectangle.
     *
     * @param hitBox the bounds where the top item is searched.
     * @param bounds the total bounds where the visualization is displayed.
     * @param pick an IntArrayList that will contain each row of items
     *        intersecting the hitBox.
     *
     * @return int the index of the item on top.
     */
    public IntArrayList pickAll(
        Rectangle2D hitBox,
        Rectangle2D bounds,
        IntArrayList pick) {
        validateShapes(bounds);
        if (pick == null)
            pick = new IntArrayList();
        else
            pick.clear();
        for (RowIterator iter = iterator(); iter.hasNext();) {
            int row = iter.nextRow();
            Shape s = getShapeAt(row);
            if (pickItem(hitBox, bounds, s, row)) {
                pick.add(row);
            }
        }
        return pick;
    }

    /**
     * Returns true of an item is picked by the specified
     * bounds.
     * 
     * @param hitBox the bounds of the pick
     * @param bounds the bounds of the visualization
     * @param s the item shape
     * @param row the item row
     * @return true of an item is picked by the specified
     * bounds.
     */
    public boolean pickItem(
        Rectangle2D hitBox,
        Rectangle2D bounds,
        Shape s,
        int row) {
        if (s == null) {
            return false;
        }
        return itemRenderer.pick(hitBox, row, s);
    }

    // Management of permutations

    /**
     * Returns the permutation.
     *
     * @return Permutation
     */
    public Permutation getPermutation() {
        return permutation;
    }

    /**
     * Sets the permutation.
     *
     * @param perm The permutation to set
     */
    public void setPermutation(Permutation perm) {
        if (perm == this.permutation) return;
        assert(perm == null || perm.getMaxIndex() <= getLastRow());
        Permutation old = permutation;
        if (permutation != null) {
            permutation.removeChangeListener(this);
        }
        permutation = perm;
        if (permutation != null) {
            permutation.addChangeListener(this);
        }
        invalidate();
        firePropertyChange(PROPERTY_PERMUTATION, old, permutation);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getRowCount() {
        if (permutation == null) {
            return super.getRowCount();
        }
        else {
            return permutation.size();
        }
    }

    /**
     * Returns the row at a specified permuted index.
     *
     * @param index the index.
     *
     * @return the row at a specified permuted index.
     */
    public int getRowAtIndex(int index) {
        if (permutation == null)
            return index;
        return permutation.getDirect(index);
    }

    /**
     * Returns the index at a specified permuted row.
     *
     * @param row the row.
     *
     * @return the index at a specified permuted row.
     */
    public int getRowIndex(int row) {
        if (permutation == null)
            return row;
        return permutation.getInverse(row);
    }

    /**
     * Returns a <code>RowIterator</code> taking the permutation into
     * account.
     *
     * @return a <code>RowIterator</code> taking the permutation into
     *         account.
     */
    public RowIterator iterator() {
        if (permutation == null)
            return super.iterator();
        else
            return permutation.iterator();
    }
    
    /**
     * {@inheritDoc}
     */
    public RowIterator reverseIterator() {
        if (permutation == null)
            return super.reverseIterator();
        else
            return permutation.reverseIterator();
    }
    
    /**
     * Computes the indexes of the min and max values of a 
     * specified column taking into account the current permutation.
     * @param c the column
     * @return a IntPair containing the indexes of hthe min and max values
     * of the column taking into account the current permutation. 
     */
    public IntPair computeMinMax(Column c) {
        if (permutation == null) {
            return new IntPair(c.getMinIndex(), c.getMaxIndex());
        }
        return Algorithms.computeMinMax(c, iterator());
    }

    /**
     * Computes the indexes of the min and max values of a 
     * specified column taking into account the current permutation
     * and filter.
     * @param c the column
     * @return a IntPair containing the indexes of hthe min and max values
     * of the column taking into account the current permutation
     * and filter.
     */
    public IntPair computeMinMaxFiltered(Column c) {
        return Algorithms.computeMinMax(
                c, 
                new FilteredRowIterator(iterator(), getFilter()));
    }

    
    // interface ChangeListener
    /**
     * {@inheritDoc}
     */
    public void stateChanged(ChangeEvent e) {
        if (e == null) return;
        if (e.getSource() instanceof Column) {
            Column column = (Column) e.getSource();
            invalidate(column);
        }
        else if (e.getSource() == permutation) {
            assert(permutation.getMaxIndex() <= getLastRow());
            invalidate();
            firePropertyChange(PROPERTY_PERMUTATION, null, permutation);
        }
        else
            invalidate(null);
    }

    /**
     * {@inheritDoc}
     */
    public void tableChanged(TableModelEvent e) {
        if (e.getSource() == table 
                && permutation != null
                && e.getColumn() == TableModelEvent.ALL_COLUMNS
                && e.getType() == TableModelEvent.UPDATE) {
            // Should be a new row filtered no unfiltered
            // check for changes in the dynamic table
            for (int start = e.getFirstRow(); 
                start <= e.getLastRow(); start++) {
                if (table.isRowValid(start)) {
                    permutation.filter(start);
                }
            }
        }
        super.tableChanged(e);
    }
    
    /**
     * Returns the installed Fisheye or <code>null</code>.
     * 
     * @return the installed Fisheye or <code>null</code>.
     */
    public Fisheye getFisheye() {
        VisualFisheye fir = VisualFisheye.get(this);
        if (fir == null) {
            return null;
        }
        return fir.getFisheye();
    }
    
    /**
     * Sets the fisheyes.
     * @param fisheye The fisheyes to set
     */
    public void setFisheye(Fisheye fisheye) {
        VisualFisheye fir = VisualFisheye.get(this);
        if (fir == null) {
            logger.error("Trying to set the Fisheye when no FisheyeItemRenderer is installed");
            return;
        }
        fir.setFisheye(fisheye);
    }

    // Excentric labeling
    /**
     * {@inheritDoc}
     */
    public LabeledItem createLabelItem(int row) {
        return new ExcentricItem(this, row);
    }

    /**
     * {@inheritDoc}
     */
    public Set pickAll(
        Rectangle2D hitBox,
        Rectangle2D bounds,
        Set pick) {
        validateShapes(bounds);

        IntArrayList intPick = null;
        intPick = pickAll(hitBox, bounds, intPick);

        if (pick == null)
            pick = new HashSet(intPick.size());

        int i;
        for (i = 0; i < intPick.size(); i++) {
            int row = intPick.get(i);
            pick.add(createLabelItem(row));
        }
        i = 0;
        Rectangle2D.Float vb = RectPool.allocateRect();
        try {
        for (Visualization sub = getVisualization(i);
            sub != null;
            sub = getVisualization(++i)) {
            vb.setRect(bounds);
            getVisualizationBounds(i, vb);
            sub.pickAll(hitBox, vb, pick);
        }
        }
        finally {
            RectPool.freeRect(vb);
        }
        return pick;
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        return getParent();
    }
    
    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (l == null) return;
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(l);
    }
    
    /**
     * {@inheritDoc}
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null) return;
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (l == null) return;
        if (changeSupport == null) return;
        changeSupport.removePropertyChangeListener(l);
    }
    
    /**
     * {@inheritDoc}
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null) return;
        if (changeSupport == null) return;

        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Fires a property change given a property name, the old and new values
     * of the property.
     * 
     * @param property the property name
     * @param oldV the old value
     * @param newV the new value
     */
    public void firePropertyChange(String property, Object oldV, Object newV) {
        if (changeSupport == null) return;
        changeSupport.firePropertyChange(property, oldV, newV);
    }
    
    /**
     * Fires a property change given a property name, the old and new values
     * of the property.
     * 
     * @param property the property name
     * @param oldV the old value
     * @param newV the new value
     */
    public void firePropertyChange(String property, int oldV, int newV) {
        if (changeSupport == null) return;
        changeSupport.firePropertyChange(property, new Integer(oldV), new Integer(newV));
    }
    
    /**
     * Fires a property change given a property name, the old and new values
     * of the property.
     * 
     * @param property the property name
     * @param oldV the old value
     * @param newV the new value
     */
    public void firePropertyChange(String property, boolean oldV, boolean newV) {
        if (changeSupport == null) return;
        changeSupport.firePropertyChange(property, new Boolean(oldV), new Boolean(newV));
    }
    
    /**
     * {@inheritDoc}
     */
    public Dimension getPreferredSize() {
        if (getLayout() != null) {
            return getLayout().getPreferredSize(this);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public VisualizationInteractor getInteractor() {
        return interactor;
    }

    /**
     * {@inheritDoc}
     */
    public void setInteractor(VisualizationInteractor interactor) {
        if (this.interactor == interactor) return;
        VisualizationInteractor old = this.interactor;
        if (this.interactor != null) {
            this.interactor.setVisualization(null);
        }
        this.interactor = interactor;
        if (this.interactor != null) {
            this.interactor.setVisualization(this);
        }
        firePropertyChange(PROPERTY_INTERACTOR, old, interactor);
    }
    
    /**
     * {@inheritDoc}
     */
    public Table getRulerTable() {
        return rulers;
    }
    
    protected void clearRulers() {
        if (rulers != null) {
            rulers.clear();
        }
    }
}
