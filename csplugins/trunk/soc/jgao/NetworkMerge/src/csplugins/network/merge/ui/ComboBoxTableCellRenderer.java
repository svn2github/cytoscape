/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csplugins.network.merge.ui;

import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

class ComboBoxTableCellRenderer extends JComboBox implements TableCellRenderer {

    public ComboBoxTableCellRenderer(Object[] items) {
            super(items);
    }

    public ComboBoxTableCellRenderer(Vector items) {
            super(items);
    }

    @Override
    public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
            this.setSelectedItem(value);
        if (isSelected) {

             setForeground(table.getSelectionForeground());
             setBackground(table.getSelectionBackground());

        } else
        {
             setForeground(table.getForeground());
             setBackground(table.getBackground());

        }
        return this;
    }
}
