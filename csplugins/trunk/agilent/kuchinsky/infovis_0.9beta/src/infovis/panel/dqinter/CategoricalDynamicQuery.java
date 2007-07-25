/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.dqinter;

import infovis.Column;
import infovis.column.FilterColumn;
import infovis.column.IntColumn;
import infovis.column.NumberColumn;
import infovis.metadata.ValueCategory;
import infovis.panel.DynamicQuery;
import infovis.utils.Permutation;
import infovis.utils.RowComparator;
import infovis.visualization.color.CategoricalColor;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.text.Format;

import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Dynamic Query for categorical columns.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.11 $
 */
public class CategoricalDynamicQuery extends AbstractDynamicQuery {
    protected IntColumn        catColumn;
    protected FilterColumn     filter;
    protected JList            list;
    protected JScrollPane      scrollPane;
    protected Permutation      permutation;
    protected CategoricalColor colorVisualization;

    /**
     * Constructor.
     * 
     * @param col
     *            the column
     */
    public CategoricalDynamicQuery(NumberColumn col) {
        super(col);
    }

    /**
     * {@inheritDoc}
     */
    public void update() {
        if (list == null)
            return;
        if (catColumn.getMinIndex() == -1) {
            if (list != null) {
                list.setEnabled(false);
            }
            return;
        }
        else if (list != null) {
            list.setEnabled(true);
        }
        DefaultListModel model = (DefaultListModel) list.getModel();
        int min = catColumn.getMin();
        int max = catColumn.getMax();
        int size = max - min + 2;
        if (model.getSize() == size)
            return;
        model.clear();
        Format format = catColumn.getFormat();
        if (format == null) {
            permutation = null;
            model.addElement("[undefined]");
            for (int v = min; v <= max; v++) {
                model.addElement("" + v);
            }
        }
        else {
            final String[] values = new String[size - 1];
            for (int v = min; v <= max; v++) {
                String value = format.format(new Integer(v));
                values[v - min] = value;
            }
            permutation = new Permutation(size-1);
            permutation.sort(new RowComparator() {
                public int compare(int a, int b) {
                    return values[a].compareToIgnoreCase(values[b]);
                }

                public boolean isValueUndefined(int row) {
                    return false;
                }
            });
            model.addElement("[undefined]");
            for (int i = 0; i < values.length; i++) {
                int j = permutation.getDirect(i);
                if (j != -1) {
                    model.addElement(values[j]);
                }
                else {
                    System.out.println("???");
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setColumn(Column column) {
        catColumn = (IntColumn) column;
        super.setColumn(column);
        if (colorVisualization == null) {
            colorVisualization = new CategoricalColor(catColumn);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFiltered(int row) {
        if (column.isValueUndefined(row))
            return list.getSelectionModel().isSelectedIndex(0);
        int v = catColumn.getIntAt(row) - (int) catColumn.getDoubleMin();
        if (permutation != null) {
            v = permutation.getInverse(v);
            if (v == -1)
                return true;
        }
        return list.getSelectionModel().isSelectedIndex(v+1);
    }

    /**
     * {@inheritDoc}
     */
    public JComponent getComponent() {
        if (scrollPane == null) {
            list = new JList();
            list.setModel(new DefaultListModel());
            list.getSelectionModel().addListSelectionListener(
                    new ListSelectionListener() {
                        public void valueChanged(ListSelectionEvent e) {
                            apply();
                        }
                    });
            list.setCellRenderer(new CategoricalCellRenderer());
            scrollPane = new JScrollPane(
                    list,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            update();
        }
        return scrollPane;
    }

    static class ColoredSquare implements Icon {
        Color color;

        public ColoredSquare(Color c) {
            this.color = c;
        }

        public void setColor(Color c) {
            color = c;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color oldColor = g.getColor();
            g.setColor(color);
            g.fill3DRect(x, y, getIconWidth(), getIconHeight(), true);
            g.setColor(oldColor);
        }

        public int getIconWidth() {
            return 12;
        }

        public int getIconHeight() {
            return 12;
        }
    } // End ColoredSquare class

    class CategoricalCellRenderer extends JLabel implements ListCellRenderer {
        ColoredSquare square = new ColoredSquare(Color.BLACK);

        public Component getListCellRendererComponent(JList list, Object value, // value
                                                                                // to
                                                                                // display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) // the list and the cell have the focus
        {
            String s = value.toString();
            setText(s);
            if (index == 0) {
                square.setColor(Color.WHITE);
            }
            else {
                square.setColor(
                        colorVisualization.getColorForValue(
                                permutation.getDirect(index-1)));
            }
            setIcon(square);
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    /**
     * Creator for CategoricalDynamicQuery.
     * 
     * @author Jean-Daniel Fekete
     * @version $Revision: 1.11 $
     */
    public static class Creator implements DynamicQueryFactory.Creator {
        /**
         * {@inheritDoc}
         */
        public DynamicQuery create(Column c, String type) {
            int category = ValueCategory.findValueCategory(c);
            if (c instanceof NumberColumn
                    && category == ValueCategory.TYPE_CATEGORIAL) {
                NumberColumn number = (NumberColumn) c;
                return new CategoricalDynamicQuery(number);
            }
            return null;
        }
    }
}