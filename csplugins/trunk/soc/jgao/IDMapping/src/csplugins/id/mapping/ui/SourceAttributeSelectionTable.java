/* File: SourceAttributeSelectionTable.java

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
import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.table.TableColumn;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.JButton;

import org.bridgedb.DataSource;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;


// support different editors for each row in a column
/**
 * Table for selecting which attribute to use for matching nodes
 *
 *
 */
public class SourceAttributeSelectionTable extends JTable{
    private IDTypeSelectionTableModel model;

    private Set<DataSource> supportedIDType;

    private List<JComboBox> networkComboBoxes;
    private List<JComboBox> attributeComboBoxes;
    private List<CheckComboBox> typeComboBoxes;
    private List<JButton> rmvBtns;
    private JButton addBtn;

    private int rowCount;

    public SourceAttributeSelectionTable(Set<DataSource> idtypes) {
        super();

        supportedIDType = idtypes;

        networkComboBoxes = new Vector();
        attributeComboBoxes = new Vector();
        typeComboBoxes = new Vector();
        rmvBtns = new Vector();
        addBtn = new JButton("Insert");
        addBtn.setBackground(java.awt.Color.GRAY);

        rowCount = 0;

        model = new IDTypeSelectionTableModel();
        setModel(model);

        setRowHeight(25);

        TableCellRenderer renderer = getTableHeader().getDefaultRenderer();
        JLabel label = (JLabel)renderer;
        label.setHorizontalAlignment(JLabel.CENTER);

        final SourceAttributeSelectionTable this_table = this;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TableColumnModel columnModel = this_table.getColumnModel();
                int column = columnModel.getColumnIndexAtX(e.getX());
                int row    = e.getY() / this_table.getRowHeight();

                if(row >= this_table.getRowCount() || row < 0 ||
                   column >= this_table.getColumnCount() || column < 0)
                  return;

                if (column==3) {
                    if (row<rowCount) {
                        this_table.removeRow(row);
                    } else if (row==rowCount) {
                        this_table.addRow();
                    }
                }
            }
        });

        setPreferredColumnWidths(new double[]{0.25,0.25,0.4,0.1});

        setColumnEditorAndCellRenderer();
    }

    public void setSupportedIDType(final Set<DataSource> types) {
            supportedIDType = types;
            setColumnEditorAndCellRenderer();
    }

    public Map<CyNetwork,Map<String,Set<DataSource>>> getSourceNetAttrType() {
        Map<CyNetwork,Map<String,Set<DataSource>>> ret = new HashMap();
        for (int i=0; i<rowCount; i++) {
            CyNetwork net = ((CyNetworkWrapper)networkComboBoxes.get(i).getSelectedItem()).network();
            String attr = (String)attributeComboBoxes.get(i).getSelectedItem();
            Set<DataSource> types = typeComboBoxes.get(i).getSelectedItem();

            Map<String,Set<DataSource>> map = ret.get(net);
            if (map==null) {
                map = new HashMap();
                ret.put(net, map);
            }

            map.put(attr, types);
        }

        return ret;
    }

    protected void setColumnEditorAndCellRenderer() {
        // network column
        TableColumn column = getColumnModel().getColumn(0);
        RowTableCellEditor rowEditor = new RowTableCellEditor(this);

        for (int ir=0; ir<rowCount; ir++) {
            rowEditor.setEditorAt(ir, new  DefaultCellEditor(networkComboBoxes.get(ir)));
        }
        column.setCellEditor(rowEditor);
        column.setCellRenderer(new ComboBoxTableCellRenderer());

        // attribute column
        column = getColumnModel().getColumn(1);
        rowEditor = new RowTableCellEditor(this);

        for (int ir=0; ir<rowCount; ir++) {
            rowEditor.setEditorAt(ir, new  DefaultCellEditor(attributeComboBoxes.get(ir)));
        }
        column.setCellEditor(rowEditor);
        column.setCellRenderer(new ComboBoxTableCellRenderer());

        // type column
        column = getColumnModel().getColumn(2);
        rowEditor = new RowTableCellEditor(this);

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

        column = getColumnModel().getColumn(3);
        column.setCellRenderer(new ButtonRenderer());
    }

    public void addRow() {

        Vector<CyNetworkWrapper> networks = new Vector();
        for (CyNetwork net : Cytoscape.getNetworkSet()) {
            networks.add(new CyNetworkWrapper(net));
        }

        networkComboBoxes.add(new JComboBox(networks));

        Vector<String> attrs = new Vector<String>();
        //TODO remove in Cytoscape3
        attrs.add("ID");
        //TODO: modify if local attribute implemented
        attrs.addAll(Arrays.asList(Cytoscape.getNodeAttributes().getAttributeNames()));

        attributeComboBoxes.add(new JComboBox(attrs));

        CheckComboBox cc = new  CheckComboBox(supportedIDType);
        typeComboBoxes.add(cc);

        JButton button = new JButton("Remove");
        rmvBtns.add(button);

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

            networkComboBoxes.remove(row);
            attributeComboBoxes.remove(row);
            typeComboBoxes.remove(row);
            rmvBtns.remove(row);
        }

        rowCount -= nsel;

        setColumnEditorAndCellRenderer();
        fireTableDataChanged();
    }

    public void removeRow(final int row) {
        int n = networkComboBoxes.size();
        if (row<0 || row>n) {
            throw new java.lang.IndexOutOfBoundsException();
        }

        networkComboBoxes.remove(row);
        attributeComboBoxes.remove(row);
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
        private final String[] columnNames = {"Network","Attribute","Source ID Type(s)",""};

        //@Override
        public int getColumnCount() {
            return 4; // select; network; attribute; id types
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
                        return networkComboBoxes.get(row);
                    case 1:
                        return attributeComboBoxes.get(row);
                    case 2:
                        return typeComboBoxes.get(row);
                    case 3:
                        return rmvBtns.get(row);
                    default:
                        throw new java.lang.IndexOutOfBoundsException();
                }
            } else if (row==rowCount) {
                switch (col) {
                    case 0:
                    case 1:
                    case 2:
                        return null;
                    case 3:
                        return addBtn;
                    default:
                        throw new java.lang.IndexOutOfBoundsException();
                }
            } else {
                throw new java.lang.IndexOutOfBoundsException();
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            if (row<rowCount) {
                switch (col) {
                    case 0:
                    case 1:
                        return true;
                    case 2:
                        return !supportedIDType.isEmpty();
                    case 3:
                        return false;
                    default:
                        throw new IndexOutOfBoundsException();
                }
            } else if (row==rowCount) {
                return false;
            } else {
                throw new java.lang.IndexOutOfBoundsException();
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
                if (!(value instanceof JComboBox)) {
                    return defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                } else {
                    return (JComboBox)value;
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


class CyNetworkWrapper {
    private CyNetwork network;

    public CyNetworkWrapper(CyNetwork network) {
        this.network = network;
    }

    @Override
    public String toString() {
        return network.getTitle();
    }

    public CyNetwork network() {
        return network;
    }

    @Override
    public int hashCode() {
        return network.getIdentifier().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CyNetworkWrapper)) {
            return false;
        }

        CyNetwork other = ((CyNetworkWrapper)o).network();
        return network.getIdentifier().equals(other.getIdentifier());
    }
}
