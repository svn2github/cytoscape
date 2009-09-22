/*
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

import csplugins.id.mapping.util.DataSourceWrapper;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

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
import javax.swing.JScrollPane;



// support different editors for each row in a column
/**
 * Table for selecting which attribute to use for matching nodes
 *
 *
 */
public class TargetAttributeSelectionTable extends JTable{
    private IDTypeSelectionTableModel model;

    private Set<DataSourceWrapper> supportedIDType;

    private int rowCount;
    private List<JComboBox> idTypeComboBoxes;
    private List<String> destinationAttributes;
    private List<JComboBox> oneIDOnlyComboBoxes;
    private List<JButton> rmvBtns;
    private JButton addBtn;

    private final String headerIDType = "Target ID Type";
    private final String headerAttrName = "Target New Attribute";
    private final String headerOneIDOnly = "All target ID(s) or first only?";
    private final String headerBtn = " ";

    private final String oneIDOnly = "Keep the first target ID only";
    private final String multiIDs = "Keep all target IDs";

    private java.awt.Color defBgColor = (new JScrollPane()).getBackground();

    public TargetAttributeSelectionTable() {
        super();

        supportedIDType = new HashSet();

        rowCount = 0;

        destinationAttributes = new Vector();
        idTypeComboBoxes = new Vector();
        oneIDOnlyComboBoxes = new Vector();
        rmvBtns = new Vector();
        addBtn = new JButton("Insert");
        addBtn.setBackground(defBgColor);

        this.setGridColor(defBgColor);

        model = new IDTypeSelectionTableModel();
        setModel(model);

        setRowHeight(25);

        TableCellRenderer renderer = getTableHeader().getDefaultRenderer();
        JLabel label = (JLabel)renderer;
        label.setHorizontalAlignment(JLabel.CENTER);

        final TargetAttributeSelectionTable this_table = this;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TableColumnModel columnModel = this_table.getColumnModel();
                int column = columnModel.getColumnIndexAtX(e.getX());
                int row    = e.getY() / this_table.getRowHeight();

                if(row >= this_table.getRowCount() || row < 0 ||
                   column >= this_table.getColumnCount() || column < 0)
                  return;

                String colName = this_table.getColumnName(column);
                if (colName.compareTo(headerBtn)==0) {
                    if (row < rowCount) {
                        this_table.removeRow(row);
                    } else if (row ==  rowCount) {
                        this_table.addRow();
                    }
                }
            }
        });

        setPreferredColumnWidths(new double[]{0.3,0.3,0.3,0.1});

        setColumnEditorAndCellRenderer();
    }

    public void setSupportedIDType(final Set<String> types, final Set<String> attrs) {
        if (types==null && attrs==null) {
            throw new NullPointerException();
        }

        List<DataSourceWrapper> oldDss = this.getTgtIDTypes();

        supportedIDType = new LinkedHashSet();
        if (types!=null) {
            for (String type : new TreeSet<String>(types)) {
                supportedIDType.add(DataSourceWrapper.getInstance(type,
                        DataSourceWrapper.DsAttr.DATASOURCE));
            }
        }

        if (attrs!=null && !attrs.isEmpty()) {
            if (!supportedIDType.isEmpty()) {
                supportedIDType.add(null);
            }

            for (String attr : new TreeSet<String>(attrs)) {
                supportedIDType.add(DataSourceWrapper.getInstance(attr,
                        DataSourceWrapper.DsAttr.ATTRIBUTE));
            }
        }

        idTypeComboBoxes.clear();
        for (int i=0; i<rowCount; i++) {
            JComboBox cb = new JComboBox(new Vector(supportedIDType));
            if (oldDss!=null) {
                DataSourceWrapper old = oldDss.get(i);
                if (supportedIDType.contains(old)) {
                    cb.setSelectedItem(old);
                }
            }
            cb.addActionListener(new SeparatorComboListener(cb));
            cb.setRenderer(new SeparatorComboBoxRenderer());

            idTypeComboBoxes.add(cb);
        }

        model.fireTableStructureChanged();
        setColumnEditorAndCellRenderer();
    }

    private String getAttrName(int row) {
        if (supportedIDType.isEmpty()) {
            return null;
        }

        List<String> names = getTgtAttrNames();

        return names.get(row);
    }

    public List<DataSourceWrapper> getTgtIDTypes() {
        if (supportedIDType.isEmpty()) {
            return null;
        }

        List<DataSourceWrapper> ret = new ArrayList();
        for (int i=0; i<rowCount; i++) {
            DataSourceWrapper dsw = (DataSourceWrapper)idTypeComboBoxes.get(i)
                        .getSelectedItem();
            ret.add(dsw);
        }
        return ret;
    }

    public List<Boolean> getIdOrAttr() {
        if (supportedIDType.isEmpty()) {
            return null;
        }

        List<Boolean> ret = new ArrayList();
        for (int i=0; i<rowCount; i++) {
            DataSourceWrapper dsw = (DataSourceWrapper)idTypeComboBoxes.get(i)
                        .getSelectedItem();
            ret.add(dsw.getDsAttr()==DataSourceWrapper.DsAttr.DATASOURCE);
        }
        return ret;
    }

    public List<String> getTgtAttrNames() {
        Set<String> usedName = new HashSet();
        usedName.add("ID"); //TODO: remove Cy3
        usedName.addAll(Arrays.asList(Cytoscape.getNodeAttributes()
                    .getAttributeNames()));
        usedName.addAll(destinationAttributes);

        List<String> ret = new ArrayList();
        for (int row=0; row<rowCount; row++) {
            String attr = destinationAttributes.get(row);
            if (attr.length()==0) {
                DataSourceWrapper dsw = (DataSourceWrapper)idTypeComboBoxes
                            .get(row).getSelectedItem();
                attr = dsw.toString();

                if (usedName.contains(attr)) {
                    int num = 1;
                    while (usedName.contains(attr+"."+num)) {
                        num ++;
                    }
                    attr += "."+num;
                }

                usedName.add(attr);
            }

            ret.add(attr);
        }
        return ret;
    }

    public List<Byte> getTgtAttrTypes() {
        List<Byte> ret = new ArrayList();
        for (int i=0; i<rowCount; i++) {
            boolean oneId = oneIDOnly.compareTo((String)oneIDOnlyComboBoxes
                        .get(i).getSelectedItem())==0;
            byte attrType = oneId ?
                CyAttributes.TYPE_STRING : CyAttributes.TYPE_SIMPLE_LIST;

            ret.add(attrType);
        }
        return ret;
    }

    public Map<String, DataSourceWrapper> getMapAttrNameIDType() {
        if (supportedIDType.isEmpty()) {
            return null;
        }
        
        Map<String, DataSourceWrapper> ret = new HashMap();
        for (int i=0; i<rowCount; i++) {
            DataSourceWrapper dsw = (DataSourceWrapper)idTypeComboBoxes.get(i)
                        .getSelectedItem();
                String name = getAttrName(i);
                ret.put(name, dsw);
        }
        return ret;
    }

    // cytoscape attr
    public Map<String,Byte> getMapAttrNameAttrType() {
        Map<String,Byte> ret = new HashMap();
        for (int i=0; i<rowCount; i++) {
            String name = getAttrName(i);
            boolean oneId = oneIDOnly.compareTo((String)oneIDOnlyComboBoxes
                        .get(i).getSelectedItem())==0;
            byte attrType = oneId ?
                CyAttributes.TYPE_STRING : CyAttributes.TYPE_SIMPLE_LIST;
            
            ret.put(name, attrType);
        }
        return ret;
    }

    public void resetDestinationAttributeNames() {
        java.util.Collections.fill(destinationAttributes, new String());
        this.repaint();
    }

    protected void setColumnEditorAndCellRenderer() {
        // type column
        TableColumnModel colModel = getColumnModel();

        // ID type
        int iCol = colModel.getColumnIndex(headerIDType);
        TableColumn column = colModel.getColumn(iCol);
        RowTableCellEditor rowEditor = new RowTableCellEditor(this);
        for (int ir=0; ir<rowCount; ir++) {
            rowEditor.setEditorAt(ir,
                        new  DefaultCellEditor(idTypeComboBoxes.get(ir)));
        }
        column.setCellEditor(rowEditor);

        if (supportedIDType.isEmpty()) {
            column.setCellRenderer(new TableCellRenderer() {
                private DefaultTableCellRenderer  defaultRenderer
                            = new DefaultTableCellRenderer();
                
                //@Override
                public java.awt.Component getTableCellRendererComponent(
                            JTable table, Object value, boolean isSelected,
                            boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) defaultRenderer
                                .getTableCellRendererComponent(table, value,
                                isSelected, hasFocus, row, column);
                    if (row<rowCount) {
                       if (isSelected) {
                           label.setBackground(table.getSelectionBackground());
                           label.setForeground(table.getSelectionForeground());
                       } else {
                           label.setBackground(table.getBackground());
                           label.setForeground(table.getForeground());
                       }
                       label.setText("Select at least one SOURCE ID type.");
                       label.setToolTipText("Please select a non-empty ID " +
                                   "mapping source first and select at least one" +
                                   " source ID type.");
                       return label;
                    } else if (row==rowCount) {
                        label.setBackground(defBgColor);
                        label.setForeground(defBgColor);
                        label.setText("");
                        return label;
                    } else {
                        return defaultRenderer.getTableCellRendererComponent(
                                    table, value, isSelected, hasFocus, row,
                                    column);
                    }
                }
            });
        } else {
            column.setCellRenderer(new ComponentTableCellRenderer());
        }

        // headerAttrName
        iCol = colModel.getColumnIndex(headerAttrName);
        column = colModel.getColumn(iCol);
        column.setCellRenderer(new TableCellRenderer() {
            private DefaultTableCellRenderer defaultRenderer
                        = new DefaultTableCellRenderer();
            //@Override
            public java.awt.Component getTableCellRendererComponent(
                        JTable table, Object value, boolean isSelected,
                        boolean hasFocus, int row, int column) {
                if (row==rowCount) {
                    JLabel label = (JLabel) (new DefaultTableCellRenderer()).
                                getTableCellRendererComponent(table, value,
                                isSelected, hasFocus, row, column);
                    label.setBackground(defBgColor);
                    label.setForeground(defBgColor);
                    return label;
                } else {
                    return defaultRenderer.getTableCellRendererComponent(table,
                                value, isSelected, hasFocus, row, column);
                }
            }
        });

        // one id only
        iCol = colModel.getColumnIndex(headerOneIDOnly);
        column = colModel.getColumn(iCol);
        rowEditor = new RowTableCellEditor(this);
        for (int ir=0; ir<rowCount; ir++) {
            rowEditor.setEditorAt(ir,
                        new  DefaultCellEditor(oneIDOnlyComboBoxes.get(ir)));
        }
        column.setCellEditor(rowEditor);

        column.setCellRenderer(new ComponentTableCellRenderer());

        // button
        iCol = colModel.getColumnIndex(headerBtn);
        column = getColumnModel().getColumn(iCol);
        column.setCellRenderer(new ButtonRenderer());
    };

    public void addRow() {
        destinationAttributes.add(new String());

        JButton button = new JButton("Remove");
        rmvBtns.add(button);

        JComboBox cb = new JComboBox(new Vector(supportedIDType));
        cb.addActionListener(new SeparatorComboListener(cb));
        cb.setRenderer(new SeparatorComboBoxRenderer());
        idTypeComboBoxes.add(cb);

        cb = new JComboBox(new String[]{multiIDs, oneIDOnly});
        oneIDOnlyComboBoxes.add(cb);

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

            destinationAttributes.remove(row);
            idTypeComboBoxes.remove(row);
            oneIDOnlyComboBoxes.remove(row);
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
        idTypeComboBoxes.remove(row);
        oneIDOnlyComboBoxes.remove(row);
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
        private final String[] columnNames = {headerIDType,headerAttrName,headerOneIDOnly,headerBtn};

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
            String colName = getColumnName(col);            

            if (row<rowCount) {
                if (colName.compareTo(headerIDType)==0) {
                    return idTypeComboBoxes.get(row);
                }

                if (colName.compareTo(headerAttrName)==0) {
                    return getAttrName(row);
                }

                if (colName.compareTo(headerOneIDOnly)==0) {
                    return oneIDOnlyComboBoxes.get(row);
                }

                if (colName.compareTo(headerBtn)==0) {
                    return rmvBtns.get(row);
                }

                throw new IndexOutOfBoundsException();
            } else if (row==rowCount) {
                if (colName.compareTo(headerBtn)==0) {
                    return addBtn;
                }

                return null;
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
                String colName = getColumnName(col);
                if (colName.compareTo(headerIDType)==0) {
                    return !supportedIDType.isEmpty();
                }

                if (colName.compareTo(headerAttrName)==0) {
                    return true;
                }

                if (colName.compareTo(headerOneIDOnly)==0) {
                   return true;
                }

                if (colName.compareTo(headerBtn)==0) {
                    return false;
                }

                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (row>=rowCount) return;

            String colName = getColumnName(col);
            if (colName.compareTo(headerAttrName)==0) {
                Set<String> usedName = new HashSet();
                usedName.add("ID"); //TODO remove in Cy3
                usedName.addAll(Arrays.asList(Cytoscape.getNodeAttributes().getAttributeNames()));
                usedName.addAll(destinationAttributes);
                String str = (String)value;
                if (usedName.contains(str)) {
                    int num = 1;
                    while (usedName.contains(str+"."+num)) {
                        num ++;
                    }
                    destinationAttributes.set(row, str+"."+num);
                } else {
                    destinationAttributes.set(row, str);
                }

                this.fireTableDataChanged();
            } else if (colName.compareTo(headerIDType)==0) {
                this.fireTableDataChanged();
            }
        }
    }

    // render combobox
    class ComponentTableCellRenderer implements TableCellRenderer {
        private DefaultTableCellRenderer defaultRenderer;

        public ComponentTableCellRenderer() {
            defaultRenderer = new DefaultTableCellRenderer();
        }

        //@Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column) {
            if (row==rowCount) {
                JLabel label = (JLabel) (new DefaultTableCellRenderer()).getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(table.getParent().getBackground());
                label.setForeground(table.getParent().getBackground());
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

    class SeparatorComboBoxRenderer extends JLabel implements javax.swing.ListCellRenderer {
        javax.swing.JSeparator separator;

        public SeparatorComboBoxRenderer() {
            setOpaque(true);
            setBorder(new javax.swing.border.EmptyBorder(1, 1, 1, 1));
            separator = new javax.swing.JSeparator(javax.swing.JSeparator.HORIZONTAL);
        }

        public Component getListCellRendererComponent(javax.swing.JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
            if (value==null) {
                return separator;
            }
            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setFont(list.getFont());
            setText(value.toString());
            return this;
        }
    }

    class SeparatorComboListener implements java.awt.event.ActionListener {
        JComboBox combo;

        int currentIndex;

        SeparatorComboListener(JComboBox combo) {
            this.combo = combo;
            //combo.setSelectedIndex(0);
            currentIndex = combo.getSelectedIndex();
        }

        public void actionPerformed(java.awt.event.ActionEvent e) {
            DataSourceWrapper tempItem = (DataSourceWrapper) combo.getSelectedItem();
            if (tempItem==null) {
                if (currentIndex==-1)
                    currentIndex=0;
                combo.setSelectedItem(combo.getItemAt(currentIndex));
            } else {
                currentIndex = combo.getSelectedIndex();
            }
        }
    }

}

