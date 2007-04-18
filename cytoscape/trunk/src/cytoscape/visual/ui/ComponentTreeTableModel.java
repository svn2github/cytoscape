/*
 * Created on 20.06.2005
 *
 */
package cytoscape.visual.ui;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;


/**
 * A static snapshot of a container hierarchy.
 *
 * NOTE: does not listen to any property changes of contained components - cell
 * updates are arbitrary on repaint only!
 *
 * @author Jeanette Winzenburg
 */
public class ComponentTreeTableModel extends AbstractTreeTableModel {
    /**
     * Creates a new ComponentTreeTableModel object.
     *
     * @param root DOCUMENT ME!
     */
    public ComponentTreeTableModel(Container root) {
        super(root);
        setRoot(root);
    }

    /**
     * DOCUMENT ME!
     *
     * @param root DOCUMENT ME!
     */
    public void setRoot(Container root) {
        if (root == null)
            root = new JXFrame();

        this.root = root;
        fireTreeStructureChanged(
            this,
            new Object[] { root },
            null,
            null);
    }

    // ------------------TreeModel
    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getChild(Object parent, int index) {
        return ((Container) parent).getComponent(index);
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getChildCount(Object parent) {
        return (parent instanceof Container)
        ? ((Container) parent).getComponentCount() : 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getIndexOfChild(Object parent, Object child) {
        Component[] children = ((Container) parent).getComponents();

        for (int i = 0; i < children.length; i++) {
            if (children[i].equals(child))
                return i;
        }

        return -1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String convertValueToText(Object node) {
        String className = node.getClass()
                               .getName();
        int lastDot = className.lastIndexOf(".");
        String lastElement = className.substring(lastDot + 1);

        return lastElement;
    }

    // ------------------ TreeTableModel
    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Class getColumnClass(int column) {
        switch (column) {
        case 0:
            return hierarchicalColumnClass;

        case 1:
            return Point.class;

        case 2:
            return Dimension.class;

        default:
            return Object.class;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnCount() {
        return 3;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "Visual Attribute";

        case 1:
            return "Attribute Name";

        case 2:
            return "Value";

        default:
            return "Column " + column;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getValueAt(Object node, int column) {
        Component comp = (Component) node;

        switch (column) {
        case 0:
            return comp;

        case 1:
            return comp.getLocation();

        case 2:
            return comp.getSize();

        default:
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     * @param node DOCUMENT ME!
     * @param column DOCUMENT ME!
     */
    public void setValueAt(Object value, Object node, int column) {
    }
}
