/* File: MergeAttributeTable.java

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

package csplugins.network.merge.ui;

import csplugins.network.merge.AttributeMapping;
import csplugins.network.merge.MatchingAttribute;
import csplugins.network.merge.NetworkMerge;
        
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;

import java.util.Collections;
import java.util.Vector;
import java.util.Iterator;
import java.util.Arrays;

import java.awt.Component;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 * Table for customizing attribute mapping from original netowrks
 * to resulting network
 * 
 * @author JGao
 */
class MergeAttributeTable extends JTable{
    private final String nullAttr = "[DELETE THIS]";
    private MatchingAttribute matchingAttribute;
    private AttributeMapping attributeMapping; //attribute mapping
    private String mergedNetworkName;
    private MergeAttributeTableModel model;
    private boolean isNode;
    
    public MergeAttributeTable(String mergedNetworkName ,AttributeMapping attributeMapping, MatchingAttribute matchingAttribute) {
        super();
        this.mergedNetworkName = mergedNetworkName;
        this.attributeMapping = attributeMapping;
        isNode = true;
        model = new MergeAttributeTableModel();
        setModel(model);
    }
    
    public MergeAttributeTable(String mergedNetworkName ,AttributeMapping attributeMapping) {
        super();        
        this.mergedNetworkName = mergedNetworkName;
        this.attributeMapping = attributeMapping;
        model = new MergeAttributeTableModel();
        isNode = false;
        setModel(model);
    }


    public void setMergedNetworkName(String mergedNetworkName) {
        this.mergedNetworkName = mergedNetworkName;
        fireTableStructureChanged();
    }

    public void setColumnEditor() {
        int n = attributeMapping.getSizeNetwork();
        for (int i=0; i<n; i++) { // for each network
            Vector<String> attrs = new Vector();
            //attrs.add(NetworkMerge.ID);
            attrs.addAll(Arrays.asList(Cytoscape.getNodeAttributes().getAttributeNames()));            
            attrs.add(nullAttr);
            JComboBox comboBox = new JComboBox(attrs);
            TableColumn column = getColumnModel().getColumn(i);
            column.setCellEditor(new DefaultCellEditor(comboBox));
        }
    }

        
    public void setCellRender() {
        int n = attributeMapping.getSizeNetwork();
        if (n==0) return;
        for (int i=0; i<n+1; i++) { // for each network
        TableColumn column = getColumnModel().getColumn(i);
        
        column.setCellRenderer(new TableCellRenderer() {
            private DefaultTableCellRenderer defaultRender = new DefaultTableCellRenderer();
            public Component getTableCellRendererComponent(
                            JTable table, Object color,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
                JLabel renderer = (JLabel) defaultRender.getTableCellRendererComponent(table, color, isSelected, hasFocus, row, column);
                if (row<2) {
                    renderer.setBackground(Color.LIGHT_GRAY);
                    if (isSelected) {
                        renderer.setForeground(table.getSelectionForeground());
                    } else {
                        renderer.setForeground(table.getForeground());
                    }
                } else {
                    if (isSelected) {
                        renderer.setForeground(table.getSelectionForeground());
                        renderer.setBackground(table.getSelectionBackground());
                    } else {
                        renderer.setForeground(table.getForeground());
                        renderer.setBackground(table.getBackground());
                    }
                    if (column<attributeMapping.getSizeNetwork()) {
                        renderer.setToolTipText("Click for combo box");
                    }
                }
                return renderer;
            }

        });
        }
    }
    
    public void fireTableStructureChanged() {
        //pack();
        model.fireTableStructureChanged();
        setCellRender();
        setColumnEditor();
    }

    private class MergeAttributeTableModel extends AbstractTableModel {
        Vector<String> netNames; // network titles
        Vector<String> netIDs; //network identifiers

        public MergeAttributeTableModel() {
            resetNetworks();
        }

        public int getColumnCount() {
            int n = attributeMapping.getSizeNetwork();
            return n==0?0:n+1;
        }

        public int getRowCount() {
            int n = attributeMapping.getSizeMergedAttributes();
            //n = n+1; // +1: add an empty row in the end (TODO: use this one in Cytoscape3.0)
            n = n+3; // TODO REMOVE in Cytoscape3.0
            return attributeMapping.getSizeNetwork()==0?0:n; 
        }

        public String getColumnName(int col) {
            if (col==getColumnCount()-1) {
                return mergedNetworkName;
            } else {
                return netNames.get(col);
            }
        }

        public Object getValueAt(int row, int col) {
            int iAttr;
            // TODO remove in Cytoscape3.0
            if (row==0) {
                return NetworkMerge.ID;
            } else if (row==1) {
                return Semantics.CANONICAL_NAME;
            } else {
                iAttr = row-2;
            }// TODO remove in Cytoscape3.0
            
            //iAttr=row; //TODO use this one in Cytoscape3.0
            
            if (col==getColumnCount()-1) {
                return attributeMapping.getMergedAttribute(iAttr);
            } else {
                return attributeMapping.getOriginalAttribute(netIDs.get(col), iAttr);
            }
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return String.class;
        }

        /*
         * 
         * 
         */
        public boolean isCellEditable(int row, int col) {
            // TODO remove in Cytoscape3.0
            if (row<2) return false;// TODO remove in Cytoscape3.0
            
            if (row!=getRowCount()-1) return true;
            return (col!=getColumnCount()-1);
        }

        /* 
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            if (value==null) return;
            
            String attr = (String) value;
            int iAttr = row-2;// TODO remove in Cytoscape3.0
            //int iAttr = row; // TODO use in Cytoscape3.0
            
            //if (attr==null) return; // click on the last row, but not selected
            int n = attributeMapping.getSizeMergedAttributes();
            if (iAttr>n) return; // should not happen

            if (col==getColumnCount()-1) { //column of merged network
                if (iAttr==n) return;
                if (attributeMapping.getMergedAttribute(iAttr).compareTo(attr)==0) { //if the same
                    return;
                }
                
                // TODO remove in Cytoscape3.0
                if (attr.compareTo(NetworkMerge.ID)==0||attr.compareTo(Semantics.CANONICAL_NAME)==0) {
                    JOptionPane.showMessageDialog(getParent(),"Atribute "+attr+" is reserved! Please use another name for this attribute!", "Error: duplicated attribute Name", JOptionPane.ERROR_MESSAGE );
                    return;
                }// TODO remove in Cytoscape3.0
                
                if (attr.length()==0) {
                    JOptionPane.showMessageDialog(getParent(),"Please use a non-empty name for the attribute!", "Error: empty attribute Name", JOptionPane.ERROR_MESSAGE );
                    return;
                } 
                
                if (attributeMapping.containsMergedAttributes(attr)) {
                    JOptionPane.showMessageDialog(getParent(),"Atribute "+attr+" is already exist! Please use another name for this attribute!", "Error: duplicated attribute Name", JOptionPane.ERROR_MESSAGE );
                    return;
                }  
                attributeMapping.setMergedAttribute(iAttr, attr);
                fireTableDataChanged();
                return;
            } else { //column of original network
                String netID = netIDs.get(col);
                if (iAttr==n) { // the last row
                    if (attr.compareTo(nullAttr)==0) return;

                    attributeMapping.addNewAttribute(netID, attr);
                    fireTableDataChanged();
                    return;

                } else {
                    //if (attributeMapping.getOriginalAttribute(netID, iAttr).compareTo(attr)==0) {
                    //    return;
                    //}
                    if (attr.compareTo(nullAttr)==0) {
                        //if (attributeMapping.getOriginalAttribute(netID, iAttr)==null) return;
                        attributeMapping.removeOriginalAttribute(netID, iAttr);
                    } else {
                        //if (attributeMapping.getOriginalAttribute(netID, iAttr).compareTo(attr)==0) return;
                        attributeMapping.setOriginalAttribute(netID, attr, iAttr);// set the attr
                    }
                    fireTableDataChanged();
                    return;
                }       
            }
        }

        public void fireTableStructureChanged() {
            resetNetworks();
            super.fireTableStructureChanged();
        }

        private void resetNetworks() {
            netNames = new Vector<String>();
            netIDs = new Vector<String>();
            int size=0;
            Iterator<String> it = attributeMapping.getNetworkSet().iterator();
            while (it.hasNext()) {
               String netID = it.next();
                String netName = Cytoscape.getNetwork(netID).getTitle();
                int index = 0;
                while (index<size && netNames.get(index).compareTo(netName)<0) index++;
                
                netIDs.add(index,netID);
                netNames.add(index,netName);
                size++;
            }
        }
    }

}
