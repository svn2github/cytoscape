/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.*;
import infovis.column.*;
import infovis.panel.dqinter.DynamicQueryFactory;
import infovis.utils.*;
import infovis.visualization.VisualColumnDescriptor;

import java.awt.Dimension;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * Panel managing the Dynamic Query controls on the visualized table.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.31 $
 */
public class DynamicQueryPanel
    extends AbstractControlPanel
    implements TableModelListener {
    protected Table table;
    protected HashMap columnDynamicQuery = new HashMap();
    protected FilterColumn filterColumn;
    protected ColumnFilter filter;
    protected JButton hideFilteredButton;
    protected JButton hideSelectedButton;
    protected JButton hideUnselectedButton;
    protected JButton showAllButton;

    /**
     * Constructor for DynamicQueryPanel.
     *
     * @param table the table.
     */
    public DynamicQueryPanel(Visualization vis, Table table) {
        this(
            vis,
            table,
            FilterColumn.findColumn(table, Table.FILTER_COLUMN));
    }

    /**
     * Creates a new DynamicQueryPanel object.
     *
     * @param table the Table to filter.
     * @param filterColumn the FilterColumn.
     */
    public DynamicQueryPanel(
        Visualization vis,
        Table table,
        FilterColumn filterColumn) {
        this(vis, table, filterColumn, null);
    }

    /**
     * Creates a new DynamicQueryPanel object.
     *
     * @param table the Table to filter.
     * @param filterColumn the FilterColumn.
     * @param filter the ColumnFilter.
     */
    public DynamicQueryPanel(
        Visualization vis,
        Table table,
        FilterColumn filterColumn,
        ColumnFilter filter) {
        super(vis);
        this.table = table;
        this.filterColumn = filterColumn;
        this.filter = filter;
        Box panel = Box.createHorizontalBox();

        hideFilteredButton = new JButton("Filtered");
        hideFilteredButton.addActionListener(this);
        panel.add(hideFilteredButton);
        hideSelectedButton = new JButton("Selected");
        hideSelectedButton.addActionListener(this);
        panel.add(hideSelectedButton);
        hideUnselectedButton = new JButton("Unselected");
        hideUnselectedButton.addActionListener(this);
        panel.add(hideUnselectedButton);
        panel.setBorder(BorderFactory.createTitledBorder("Hide"));
        add(panel);
        showAllButton = new JButton("Show All");
        showAllButton.addActionListener(this);
        showAllButton.setAlignmentX(CENTER_ALIGNMENT);
        add(showAllButton);

        addMissingControls();
    }
    
    public void dispose() {
        Object []columns = columnDynamicQuery.values().toArray();
        for (int i = 0; i < columns.length; i++) {
            Column c = (Column)columns[i];
            removeControl(c);
        }
    }
    
    public Permutation getPermutation() {
        Permutation perm = getVisualization().getPermutation();
        if (perm == null) {
            return new Permutation(getVisualization().iterator());
        }
        else {
            return perm;
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == hideFilteredButton) {
            Permutation perm = getPermutation();
            perm.filter((RowFilter)getVisualization().getFilter());
            getVisualization().setPermutation(perm);
        }
        else if (e.getSource() == hideSelectedButton) {
            Permutation perm = getPermutation();
            perm.filter(new InverseRowFilter(getVisualization().getSelection()));
            getVisualization().setPermutation(perm);
        }
        else if (e.getSource() == hideUnselectedButton) {
            Permutation perm = getPermutation();
            perm.filter((RowFilter)getVisualization().getSelection());
            getVisualization().setPermutation(perm);
        }
        else if (e.getSource() == showAllButton) {
            getVisualization().setPermutation(null);
        }
        else
            super.actionPerformed(e);
    }

    /**
     * Creates a default control for the specified column.
     *
     * @param c the column.
     */
    public void createDefaultControl(Column c) {
        final DynamicQuery dq = DynamicQueryFactory.createDQ(c);
        if (dq == null)
            return;
        JComponent rs = dq.getComponent();
        //setMaximumSize(rs);
        setTitleBorder(rs, c.getName());
        rs.setEnabled(true);
        add(rs);
        columnDynamicQuery.put(c, dq);
        dq.setFilterColumn(filterColumn);
        final JPopupMenu popup = new JPopupMenu("Column "+c.getName());
        addPopupActions(popup, dq);
        rs.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }
            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(),
                               e.getX(), e.getY());
                }
            }
        });
    }
    
    public static class SelectVisualColumnAction extends AbstractAction {
        protected String visual;
        protected DynamicQuery dq;
        protected Visualization visualization;
        
        public SelectVisualColumnAction(String visual, DynamicQuery dq, Visualization vis) {
            super(visual);
            this.visual = visual;
            this.dq = dq;
            this.visualization = vis;
        }
        
        public void actionPerformed(ActionEvent e) {
            Column column = dq.getColumn();
            visualization.setVisualColumn(visual, column);
        }
    }

    public void addPopupActions(JPopupMenu popup, final DynamicQuery dq) {
        JMenu visualMenu = new JMenu("Visual for ...");
        for (Iterator iter = getVisualization().getVisualColumnIterator();
            iter.hasNext(); ) {
            String visual = (String)iter.next();
            VisualColumnDescriptor vc = getVisualization().getVisualColumnDescriptor(visual);
            SelectVisualColumnAction action = new SelectVisualColumnAction(visual, dq, getVisualization());
            ColumnFilter filter = vc.getFilter();
            if (filter != null && filter.filter(dq.getColumn())) {
                action.setEnabled(false);
            }
            else {
                action.setEnabled(true);
            }
            visualMenu.add(action);
        }
        popup.add(visualMenu);
        popup.add(new AbstractAction("Show Values") {
            public void actionPerformed(ActionEvent e) {
                Column c = dq.getColumn();
                if (c instanceof BasicColumn) {
                    final BasicColumn col = (BasicColumn) c;
                    final IntIntSortedMap map = col.computeValueMap();
                    DefaultListModel model = new DefaultListModel();
                    for (RowIterator iter = map.keyIterator();
                        iter.hasNext(); ) {
                        int k = iter.nextRow();
                        model.addElement(col.getValueAt(k)+ " ("+map.get(k)+"x)");
                    }
                    JList list = new JList(model);
                    JScrollPane pane = new JScrollPane(list);
                    pane.setPreferredSize(new Dimension(300, 500));
                    JFrame frame = new JFrame("Value list of column '"+col.getName()+"'");
                    frame.getContentPane().add(pane);
                    frame.pack();
                    frame.setVisible(true);
                }
            }
        });
    }
    /**
     * Removes the control associated with a specified column.
     *
     * @param c the column.
     */
    public void removeControl(Column c) {
        DynamicQuery dq = (DynamicQuery) columnDynamicQuery.remove(c);
        removeDynamicQuery(dq);
    }

    protected void removeDynamicQuery(DynamicQuery dq) {
        if (dq != null) {
            getFilterColumn().removeDynamicQuery(dq);
            remove(dq.getComponent());
            dq = null; // should dispose it.
        }
    }

    public boolean filter(Column c) {
        if (filter != null)
            return filter.filter(c);
        return false;
    }

    /**
     * Adds Dynamic Query controls for all columns in the table unless one already
     * exists.
     */
    public void addMissingControls() {
        Set knownColumn = new TreeSet(columnDynamicQuery.keySet());
        for (int col = 0; col < table.getColumnCount(); col++) {
            Column c = table.getColumnAt(col);
            if (filter(c))
                continue;
            if (!knownColumn.remove(c)) {
                createDefaultControl(c);
            }
        }
        for (Iterator iter = knownColumn.iterator(); iter.hasNext();) {
            Column c = (Column) iter.next();
            removeControl(c);
        }
    }

    /**
     * @see javax.swing.event.TableModelListener#tableChanged(TableModelEvent)
     */
    public void tableChanged(TableModelEvent e) {
        addMissingControls();
    }

    // Filter management
    public int columnOffset(Column column) {
        int offset = table.indexOf(column);
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("column is not in table");
        }
        return offset;
    }

    /**
     * Returns the filterColumn.
     * @return FilterColumn
     */
    public FilterColumn getFilterColumn() {
        return filterColumn;
    }

    /**
     * Sets the filterColumn.
     * @param filterColumn The filterColumn to set
     */
    public void setFilterColumn(FilterColumn filterColumn) {
        this.filterColumn = filterColumn;
    }

    /**
     * Returns the dynamic query associated with a specified column.
     *
     * @param c the column.
     *
     * @return the dynamic query associated with a specified column or null if
     *  no dynamic query exist for this column.
     */
    public DynamicQuery getColumnDynamicQuery(Column c) {
        return (DynamicQuery) columnDynamicQuery.get(c);
    }
    /**
     * Returns the filter.
     * @return ColumnFilter
     */
    public ColumnFilter getFilter() {
        return filter;
    }

    /**
     * Sets the filter.
     * @param filter The filter to set
     */
    public void setFilter(ColumnFilter filter) {
        this.filter = filter;
        addMissingControls();
    }

}
