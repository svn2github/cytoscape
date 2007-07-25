/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import java.awt.*;
import java.awt.Container;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

/**
 * Row headers for the DetailTable: shows the name of the attributes
 * visualized in the row of a scrollable pane so that it is always
 * visible when scrolling to see the details of the selection.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class TableRowHeader extends JList {
    private JTable table;
    private ArrayList labels;

    public static boolean isRowHeaderVisible(JTable table) {
        Container p = table.getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                JViewport rowHeaderViewPort = scrollPane.getRowHeader();
                if (rowHeaderViewPort != null)
                    return rowHeaderViewPort.getView() != null;
            }
        }
        return false;
    }

    /**
     * Creates row header for table with row number (starting with 1) displayed
     */

    public static void removeRowHeader(JTable table) {
        Container p = table.getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                scrollPane.setRowHeader(null);
            }
        }
    }

    /**
     * Creates row header for table with row number (starting with 1) displayed
     */
    public static void setRowHeader(JTable table, ArrayList labels) {
        Container p = table.getParent();
        if (p instanceof JViewport) {
            Container gp = p.getParent();
            if (gp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) gp;
                scrollPane.setRowHeaderView(new TableRowHeader(table, labels));
            }
        }
    }

    public TableRowHeader(JTable table, ArrayList labels) {
        super(new TableRowHeaderModel(table));
        this.table = table;
        this.labels = labels;
        setFixedCellHeight(table.getRowHeight());
        setFixedCellWidth(preferredHeaderWidth());
        setCellRenderer(new RowHeaderRenderer());
        setSelectionModel(table.getSelectionModel());
    }

    /***************************************************************************
     * * Returns the bounds of the specified range of items in JList
     * coordinates. Returns null if index isn't valid. * *
     * 
     * @param index0
     *            the index of the first JList cell in the range *
     * @param index1
     *            the index of the last JList cell in the range *
     * @return the bounds of the indexed cells in pixels
     */
    public Rectangle getCellBounds(int index0, int index1) {
        Rectangle rect0 = table.getCellRect(index0, 0, true);
        Rectangle rect1 = table.getCellRect(index1, 0, true);
        int y, height;
        if (rect0.y < rect1.y) {
            y = rect0.y;
            height = rect1.y + rect1.height - y;
        } else {
            y = rect1.y;
            height = rect0.y + rect0.height - y;
        }
        return new Rectangle(0, y, getFixedCellWidth(), height);
    }
    // assume that row header width should be big enough to display row number
    // Integer.MAX_VALUE completely

    private int preferredHeaderWidth() {
        JLabel longestRowLabel;
        if (labels == null || labels.size() == 0) {
            longestRowLabel = new JLabel("65356");
        }
        else {
            String longest = "1000000/1000000";
            for (int i = 0; i < labels.size(); i++) {
                String s = labels.get(i).toString();
                if (s.length() > longest.length()) {
                    longest = s;
                }
            }
            longestRowLabel = new JLabel(longest);
        }
        JTableHeader header = table.getTableHeader();
        longestRowLabel.setBorder(header.getBorder());
        //UIManager.getBorder("TableHeader.cellBorder"));
        longestRowLabel.setHorizontalAlignment(JLabel.CENTER);
        longestRowLabel.setFont(header.getFont());
        return longestRowLabel.getPreferredSize().width;
    }
    
    public String getLabel(int i) {
        if (labels == null || labels.size() <= i) {
            return String.valueOf(i+1);
        }
        else {
            return labels.get(i).toString();
        }
    }

    public static class TableRowHeaderModel extends AbstractListModel {
        private JTable table;

        public TableRowHeaderModel(JTable table) {
            this.table = table;
        }

        public int getSize() {
            return table.getRowCount();
        }

        public Object getElementAt(int index) {
            return null;
        }
    }

    public class RowHeaderRenderer extends JLabel implements
            ListCellRenderer {
        private Border selectedBorder;

        private Border normalBorder;

        private Font selectedFont;

        private Font normalFont;

        RowHeaderRenderer() {
            normalBorder = UIManager
                    .getBorder("TableHeader.cellBorder");
            selectedBorder = BorderFactory.createRaisedBevelBorder();
            final JTableHeader header = table.getTableHeader();
            normalFont = header.getFont();
            selectedFont = normalFont.deriveFont(normalFont.getStyle()
                    | Font.BOLD);
            setForeground(header.getForeground());
            setBackground(header.getBackground());
            setOpaque(true);
            setHorizontalAlignment(CENTER);
        }

        public Component getListCellRendererComponent(JList list,
                Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            if (table.getSelectionModel().isSelectedIndex(index)) {
                setFont(selectedFont);
                setBorder(selectedBorder);
            } else {
                setFont(normalFont);
                setBorder(normalBorder);
            }
            String label = getLabel(index);
            setText(label);
            return this;
        }
    }
}

