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

import csplugins.network.merge.model.AttributeMapping;
import csplugins.network.merge.model.MatchingAttribute;
import csplugins.network.merge.util.AttributeValueCastUtils;
        
import cytoscape.Cytoscape;
import cytoscape.util.CyNetworkNaming;
import cytoscape.data.Semantics;
import cytoscape.data.CyAttributes;

import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;

import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
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
import javax.swing.table.JTableHeader;

/**
 * Table for customizing attribute mapping from original netowrks
 * to resulting network
 * 
 * 
 */
class MergeAttributeTable extends JTable{
    private final String nullAttr = "[DELETE THIS]";
    private MatchingAttribute matchingAttribute;
    private AttributeMapping attributeMapping; //attribute mapping
    private String mergedNetworkName;
    private MergeAttributeTableModel model;
    private boolean isNode;
    private int indexMatchingAttr; // the index of matching attribute in the attribute mapping
                                   // only used when isNode==true
    
    public MergeAttributeTable(final AttributeMapping attributeMapping, final MatchingAttribute matchingAttribute) {
        super();
        isNode = true;
        indexMatchingAttr = -1;
        this.mergedNetworkName = CyNetworkNaming.getSuggestedNetworkTitle("Network.Merged");
        this.attributeMapping = attributeMapping;
        this.matchingAttribute = matchingAttribute;
        model = new MergeAttributeTableModel();
        setModel(model);
        setRowHeight(20);
    }
    
    public MergeAttributeTable(final AttributeMapping attributeMapping) {
        super();        
        this.mergedNetworkName = "Network.Merged";
        this.attributeMapping = attributeMapping;
        model = new MergeAttributeTableModel();
        isNode = false;
        setModel(model);
        setRowHeight(20);
    }

    public String getMergedNetworkName() {
        return mergedNetworkName;
    }

    protected void setColumnEditor() {
        final TreeSet<String> attrset = new TreeSet();
        attrset.addAll(Arrays.asList(attributeMapping.getCyAttributes().getAttributeNames()));

        final Vector<String> attrs = new Vector<String>(attrset);
        attrs.add(nullAttr);

        final int n = attributeMapping.getSizeNetwork();
        for (int i=0; i<n+1; i++) { // for each network
            final JComboBox comboBox = new JComboBox(attrs);
            final TableColumn column = getColumnModel().getColumn(i);
            if (i<n) {
                column.setCellEditor(new DefaultCellEditor(comboBox));
            }

            column.setCellRenderer(new TableCellRenderer() {
                    private DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
                    private ComboBoxTableCellRenderer comboBoxRenderer = new ComboBoxTableCellRenderer(attrs);
                    
                    @Override
                    public Component getTableCellRendererComponent(
                                    JTable table, Object value,
                                    boolean isSelected, boolean hasFocus,
                                    int row, int column) {
                        //final JLabel renderer = (JLabel) defaultRender.getTableCellRendererComponent(table, color, isSelected, hasFocus, row, column);
                        Component renderer;
                        if (row<2||(isNode&&row==2&&column!=table.getColumnCount()-1)) { //TODO do not need this in Cytoscape3
                            JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                            label.setBackground(Color.LIGHT_GRAY);
                            if (isSelected) {
                                label.setForeground(table.getSelectionForeground());
                            } else {
                                label.setForeground(table.getForeground());
                            }
                            if (row==2) {
                                label.setToolTipText("Change this in the matching node table above");
                            } else {
                                label.setToolTipText("Reserved by system");
                            }
                            renderer = label;
                        } else {
                            if (isNode&&row==2) {//&&column==table.getColumnCount()-1
                                JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                                label.setForeground(Color.RED);
                                if (isSelected) {
                                    label.setBackground(table.getSelectionBackground());
                                } else {
                                    label.setBackground(table.getBackground());
                                }
                                label.setToolTipText("CHANGE ME!");
                                renderer = label;
                            } else {
                                if (column<n) {
                                        renderer = comboBoxRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                                } else {
                                        JLabel label = (JLabel) defaultRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                                        label.setToolTipText("Double click to change");
                                        renderer = label;
                                }

                                if (isSelected) {
                                    renderer.setForeground(table.getSelectionForeground());
                                    renderer.setBackground(table.getSelectionBackground());
                                } else {
                                    renderer.setForeground(table.getForeground());
                                    renderer.setBackground(table.getBackground());
                                }
                            }
                        }
                        return renderer;
                    }

                });
        }
        
    }
    
    protected void setMergedNetworkNameTableHeaderListener() {
        getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTableHeader header = (JTableHeader) e.getSource();
                JTable table = header.getTable();
                int columnIndex = header.columnAtPoint(e.getPoint());
                if (columnIndex==attributeMapping.getSizeNetwork()) { // the merged network
                    String title = JOptionPane.showInputDialog(table.getParent(), "Input the title for the merged network");
                    if (title!=null && title.length()!=0) {
                        mergedNetworkName = title;
                        fireTableHeaderChanged();           
                    } else {
                        fireTableHeaderChanged(); //TODO: this is just for remove the duplicate click event
                                                  // there should be better ways                        
                    }
                }
            }            
        });
    }

        
    protected void setCellRender() {
        final int n = attributeMapping.getSizeNetwork();
        if (n==0) return;
        for (int i=0; i<n+1; i++) { // for each network
        final TableColumn column = getColumnModel().getColumn(i);
        
        column.setCellRenderer(new TableCellRenderer() {
            private DefaultTableCellRenderer defaultRender = new DefaultTableCellRenderer();
            @Override
            public Component getTableCellRendererComponent(
                            JTable table, Object color,
                            boolean isSelected, boolean hasFocus,
                            int row, int column) {
                final JLabel renderer = (JLabel) defaultRender.getTableCellRendererComponent(table, color, isSelected, hasFocus, row, column);
                if (row<2||(isNode&&row==2&&column!=table.getColumnCount()-1)) { //TODO do not need this in Cytoscape3
                    renderer.setBackground(Color.LIGHT_GRAY);
                    if (isSelected) {
                        renderer.setForeground(table.getSelectionForeground());
                    } else {
                        renderer.setForeground(table.getForeground());
                    }
                    if (row==2) {
                        renderer.setToolTipText("Change this in the matching node table above");
                    } else {
                        renderer.setToolTipText("Reserved by system");
                    }
                } else {
                    if (isNode&&row==2) {//&&column==table.getColumnCount()-1
                        renderer.setForeground(Color.RED);
                        if (isSelected) {
                            renderer.setBackground(table.getSelectionBackground());
                        } else {
                            renderer.setBackground(table.getBackground());
                        }
                        renderer.setToolTipText("CHANGE ME!");
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
                        } else {
                            renderer.setToolTipText("Double click to change");
                        }
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
        //setCellRender();
        setColumnEditor();
        setMergedNetworkNameTableHeaderListener();
    }
    
    public void updateMatchingAttribute() { 
        if (!isNode) {
            throw new java.lang.UnsupportedOperationException("updateMatchingAttribute is only supported for node table");
        }
        
        boolean update = false;
        if (indexMatchingAttr==-1) {
            indexMatchingAttr = 0;
            String attr_merged = "Matching_Attribute";
            
            //TODO: remove in Cytoscape3
            Map netAttrMap = new HashMap(matchingAttribute.getNetAttrMap());
            Iterator<Map.Entry<String,String>> it = netAttrMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,String> entry = it.next();
                String network = entry.getKey();
                String attr = entry.getValue();
                if (attr.compareTo("ID")==0) {
                    netAttrMap.put(network, Semantics.CANONICAL_NAME);
                }
            }//TODO: remove in Cytoscape3
            
            attributeMapping.addAttributes(netAttrMap, attr_merged, indexMatchingAttr);
            update = true;
        } else {            
            Set<String> networks = matchingAttribute.getNetworkSet();
            Iterator<String> it = networks.iterator();
            while (it.hasNext()) {
                String network = it.next();
                String attr = matchingAttribute.getAttributeForMatching(network);
                if (attr.compareTo("ID")==0) {
                    attr = Semantics.CANONICAL_NAME;
                }
                String old = attributeMapping.setOriginalAttribute(network, attr, indexMatchingAttr);
                if (attr.compareTo(old)!=0) {
                    update=true;
                }
            }
        }
        
        if (update) {
            fireTableStructureChanged();
        }
    }
    
    protected void fireTableHeaderChanged() {
        model.fireTableStructureChanged();
        setCellRender();
        setColumnEditor();
    }

    protected class MergeAttributeTableModel extends AbstractTableModel {
        Vector<String> netNames; // network titles
        Vector<String> netIDs; //network identifiers

        public MergeAttributeTableModel() {
            resetNetworks();
        }

        @Override
        public int getColumnCount() {
            final int n = attributeMapping.getSizeNetwork();
            return n==0?0:n+1;
        }

        @Override
        public int getRowCount() {
            int n = attributeMapping.getSizeMergedAttributes();
            //n = n+1; // +1: add an empty row in the end (TODO: use this one in Cytoscape3.0)
            n = n+3; //TODO REMOVE in Cytoscape3.0
            return attributeMapping.getSizeNetwork()==0?0:n; 
        }

        @Override
        public String getColumnName(final int col) {
            if (col==getColumnCount()-1) {
                return mergedNetworkName;
            } else {
                return netNames.get(col);
            }
        }

        @Override
        public Object getValueAt(final int row, final int col) {
            //final int iAttr = row; //TODO used in Cytoscape3
            
            //TODO remove in Cytoscape3
            if (row==0) {
                return "ID";
            } else if (row==1) {
                return Semantics.CANONICAL_NAME;
            }
            final int iAttr = row - 2;
            //TODO remove in Cytoscape3
            if (row==getRowCount()-1) {
                return null;
            }
            if (col==getColumnCount()-1) {
                return attributeMapping.getMergedAttribute(iAttr);
            } else {
                return attributeMapping.getOriginalAttribute(netIDs.get(col), iAttr);
            }
        }

        @Override
        public Class getColumnClass(final int c) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(final int row, final int col) {
            //TODO remove in Cytoscape3.0
            if (row<2) return false;//TODO remove in Cytoscape3.0
                        
            if (isNode) { // make the matching attribute ineditable
                if (row==2) { //TODO use row==0 in Cytoscape3
                    return col==getColumnCount()-1;
                }
            }
            
            if (row!=getRowCount()-1) return true;
            return (col!=getColumnCount()-1);
        }


        @Override
        public void setValueAt(final Object value, final int row, final int col) {
            if (value==null) return;
            
            final String attr = (String) value;
            final int iAttr = row-2;//TODO remove in Cytoscape3.0
            //final int iAttr = row; //TODO use in Cytoscape3.0
            
            final int n = attributeMapping.getSizeMergedAttributes();
            if (iAttr>n) return; // should not happen

            if (col==getColumnCount()-1) { //column of merged network
                if (iAttr==n) return;
                
                String attr_curr = attributeMapping.getMergedAttribute(iAttr);
                if (attr_curr.compareTo(attr)==0) { //if the same
                    return;
                }
                
                //TODO remove in Cytoscape3.0
                if (attr.compareTo("ID")==0||attr.compareTo(Semantics.CANONICAL_NAME)==0) {
                    JOptionPane.showMessageDialog(getParent(),"Atribute "+attr+" is reserved! Please use another name for this attribute!", "Error: duplicated attribute Name", JOptionPane.ERROR_MESSAGE );
                    return;
                }//TODO remove in Cytoscape3.0
                
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
                    
                    String attr_merged = attr;
                    //TODO remove in Cytoscape3
                    if (attr.compareTo(Semantics.CANONICAL_NAME)==0) {
                        attr_merged = netID+"."+Semantics.CANONICAL_NAME;
                    }//TODO remove in Cytoscape3
                    
                    Map<String,String> map = new HashMap<String,String>();
                    map.put(netID, attr);

                    attributeMapping.addAttributes(map, attr_merged);
                    fireTableDataChanged();
                    return;

                } else {
                    String curr_attr = attributeMapping.getOriginalAttribute(netID, iAttr);                    
                    if (curr_attr!=null && curr_attr.compareTo(attr)==0) {
                        return;
                    }
                    
                    if (attr.compareTo(nullAttr)==0) {
                        if (curr_attr==null) return;
                        //if (attributeMapping.getOriginalAttribute(netID, iAttr)==null) return;
                        attributeMapping.removeOriginalAttribute(netID, iAttr);
                    } else {
                        String mergedAttr = attributeMapping.getMergedAttribute(iAttr);
                        CyAttributes cyAttributes = attributeMapping.getCyAttributes();
                        if (Arrays.asList(cyAttributes.getAttributeNames()).contains(mergedAttr)
                                && !AttributeValueCastUtils.isAttributeTypeConvertable(attr,
                                                                          mergedAttr, 
                                                                          cyAttributes)) {
                            final int ioption = JOptionPane.showConfirmDialog(getParent(),
                                        "Atribute "+attr+" have a type incompatible to the other attributes to be merged. Are you sure to select "+attr+"? ",
                                        "Warning: types are different",
                                        JOptionPane.YES_NO_OPTION );
                                if (ioption==JOptionPane.NO_OPTION) {
                                    return;
                                }
                        }
                                                
                        attributeMapping.setOriginalAttribute(netID, attr, iAttr);// set the attr
                    }
                    fireTableDataChanged();
                    return;
                }       
            }
        }

        @Override
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
                while (index<size && netNames.get(index).compareToIgnoreCase(netName)<0) index++;
                
                netIDs.add(index,netID);
                netNames.add(index,netName);
                size++;
            }
        }
    }

}
