/* File: DestinationAttributeSelectionTable.java

 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package csplugins.id.mapping.ui;

import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.TreeSet;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.table.TableColumn;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JButton;

import org.bridgedb.DataSource;



// support different editors for each row in a column
/**
 * Table for selecting which attribute to use for matching nodes
 *
 *
 */
public class DestinationAttributeSelectionTable extends JTable{
    private IDTypeSelectionTableModel model;

    private Set<DataSource> supportedIDType;

    private int rowCount;
    private List<JComboBox> typeComboBoxes;
    private List<String> destinationAttributes;
    private List<JButton> rmvBtns;
    private JButton addBtn;

    public DestinationAttributeSelectionTable(Set<DataSource> idtypes) {
        super();

        supportedIDType = idtypes;

        rowCount = 0;

        destinationAttributes = new Vector();
        typeComboBoxes = new Vector();
        rmvBtns = new Vector();
        addBtn = new JButton("Insert");
        addBtn.setBackground(java.awt.Color.GRAY);

        model = new IDTypeSelectionTableModel();
        setModel(model);

        setRowHeight(25);

        TableCellRenderer renderer = getTableHeader().getDefaultRenderer();
        JLabel label = (JLabel)renderer;
        label.setHorizontalAlignment(JLabel.CENTER);

        final DestinationAttributeSelectionTable this_table = this;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TableColumnModel columnModel = this_table.getColumnModel();
                int column = columnModel.getColumnIndexAtX(e.getX());
                int row    = e.getY() / this_table.getRowHeight();

                if(row >= this_table.getRowCount() || row < 0 ||
                   column >= this_table.getColumnCount() || column < 0)
                  return;

                if (column==2) {
                    if (row < rowCount) {
                        this_table.removeRow(row);
                    } else if (row ==  rowCount) {
                        this_table.addRow();
                    }
                }
                    

            }
        });

        setPreferredColumnWidths(new double[]{0.45,0.45,0.1});

        setColumnEditorAndCellRenderer();
    }

    public void setSupportedIDType(final Set<DataSource> types) {
            supportedIDType = types;
            setColumnEditorAndCellRenderer();
    }

    private String getType(int row) {
        String attr = (String)destinationAttributes.get(row);
        if (attr.length()==0) {
            DataSource type = (DataSource)typeComboBoxes.get(row).getSelectedItem();
            attr = type.getFullName();
        }

        return attr;
    }

    public Map<DataSource, String> getDestinationAttrType() {
        Map<DataSource, String> ret = new HashMap();
        for (int i=0; i<rowCount; i++) {
            String attr = getType(i);
            DataSource type = (DataSource)typeComboBoxes.get(i).getSelectedItem();

            ret.put(type, attr);
        }

        return ret;
    }

    protected void setColumnEditorAndCellRenderer() {
        // type column
        TableColumn column = getColumnModel().getColumn(0);
        RowTableCellEditor rowEditor = new RowTableCellEditor(this);
        for (int ir=0; ir<rowCount; ir++) {
            rowEditor.setEditorAt(ir, new  DefaultCellEditor(typeComboBoxes.get(ir)));
        }
        column.setCellEditor(rowEditor);

        if (supportedIDType.isEmpty()) {
            column.setCellRenderer(new TableCellRenderer() {
                private DefaultTableCellRenderer  defaultRenderer = new DefaultTableCellRenderer();

                //@Override
                public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                       JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                       if (isSelected) {
                               label.setBackground(table.getSelectionBackground());
                               label.setForeground(table.getSelectionForeground());
                       } else {
                               label.setBackground(table.getBackground());
                               label.setForeground(table.getForeground());
                       }
                       label.setToolTipText("Please select a non-empty ID mapping file first.");
                       return label;
                }
            });
        } else {
            column.setCellRenderer(new ComboBoxTableCellRenderer());
        }
        
        column = getColumnModel().getColumn(1);
        column.setCellRenderer(new TableCellRenderer() {
            private DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
            //@Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                            boolean isSelected, boolean hasFocus, int row, int column) {
                if (row==rowCount) {
                    JLabel label = (JLabel) (new DefaultTableCellRenderer()).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setBackground(java.awt.Color.GRAY);
                    label.setForeground(java.awt.Color.GRAY);
                    return label;
                } else {
                    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            }
        });

        column = getColumnModel().getColumn(2);
        column.setCellRenderer(new ButtonRenderer());
    };

    public void addRow() {
        destinationAttributes.add(new String());

        JButton button = new JButton("Remove");
        rmvBtns.add(button);

        JComboBox cc = new  JComboBox(new Vector(supportedIDType));
        typeComboBoxes.add(cc);

        rowCount++;

        setColumnEditorAndCellRenderer();
        fireTableDataChanged();
    }

    public void removeRows(final int[] rows) {
        TreeSet<Integer> setrows = new TreeSet();
        for (int row : rows) {
            setrows.add(row);
        }

        Vector<Integer> vecrows = new Vector(setrows);
        int nsel = vecrows.size();
        for (int i=nsel-1; i>=0; i--) {
            int row = vecrows.get(i);
            if (row<0 || row>rowCount) {
                throw new java.lang.IndexOutOfBoundsException();
            }

//            selected.remove(row);
            destinationAttributes.remove(row);
            typeComboBoxes.remove(row);
            rmvBtns.remove(row);
        }

        rowCount -= nsel;

        setColumnEditorAndCellRenderer();
        fireTableDataChanged();
    }

    public void removeRow(final int row) {
        if (row<0 || row>=rowCount) {
            throw new java.lang.IndexOutOfBoundsException();
        }

        destinationAttributes.remove(row);
        typeComboBoxes.remove(row);
        rmvBtns.remove(row);

        rowCount--;

        setColumnEditorAndCellRenderer();
        fireTableDataChanged();
    }

    void fireTableDataChanged() {
            model.fireTableDataChanged();
    }

    private void setPreferredColumnWidths(double[] percentages) {
        java.awt.Dimension tableDim = this.getPreferredSize();

        double total = 0; 
        for(int i = 0; i < getColumnModel().getColumnCount(); i++)
            total += percentages[i];

        for(int i = 0; i < getColumnModel().getColumnCount(); i++)
        {
            TableColumn column = getColumnModel().getColumn(i);
            column.setPreferredWidth((int) (tableDim.width * (percentages[i] / total)));
        }
   }

    private class IDTypeSelectionTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Target ID Type","New attribute name",""};

        //@Override
        public int getColumnCount() {
            return 3; // select; network; attribute; id types
        }

        //@Override
        public int getRowCount() {
            return rowCount+1;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        //@Override
        public Object getValueAt(int row, int col) {
            if (row<rowCount) {
                switch (col) {
                    case 0:
                        return typeComboBoxes.get(row);
                    case 1:
                        return getType(row);
                    case 2:
                        return rmvBtns.get(row);
                    default:
                        throw new IndexOutOfBoundsException();
                }
            } else if (row==rowCount) {
                switch (col) {
                    case 0:
                    case 1:
                        return null;
                    case 2:
                        return addBtn;
                    default:
                        throw new IndexOutOfBoundsException();
                }
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public Class getColumnClass(int c) {
                return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (row>rowCount) {
                throw new IndexOutOfBoundsException();
            } else if (row==rowCount) {
                return false;
            } else {
                switch (col) {
                    case 0:
                        return !supportedIDType.isEmpty();
                    case 1:
                        return true;
                    case 2:
                        return false;
                    default:
                        throw new IndexOutOfBoundsException();
                }
            }
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col==1 && row<rowCount) {
                destinationAttributes.set(row, (String)value);
                fireTableCellUpdated(row, col);
            }
        }
    }

    // render checkcombobox
    class ComboBoxTableCellRenderer implements TableCellRenderer {
        private DefaultTableCellRenderer defaultRenderer;

        public ComboBoxTableCellRenderer() {
            defaultRenderer = new DefaultTableCellRenderer();
        }

        //@Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
            if (row==rowCount) {
                JLabel label = (JLabel) (new DefaultTableCellRenderer()).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(java.awt.Color.GRAY);
                label.setForeground(java.awt.Color.GRAY);
                return label;
            } else if (row<rowCount) {
                if (!(value instanceof java.awt.Component)) {
                    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                } else {
                    return (java.awt.Component)value;
                }
            } else {
                return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        }
    }

    class ButtonRenderer implements TableCellRenderer {
        private DefaultTableCellRenderer defaultRenderer;

        public ButtonRenderer() {
            defaultRenderer = new DefaultTableCellRenderer();
        }

      public Component getTableCellRendererComponent(JTable table, Object value,
                       boolean isSelected, boolean hasFocus, int row, int column) {
          if (!(value instanceof JButton)) {
              return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
          }

          return (JButton)value;
      }
    }

}

