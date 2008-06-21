/* File: MatchNodeTable.java

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

import csplugins.network.merge.NetworkMerge;
import csplugins.network.merge.MatchingAttribute;

import cytoscape.Cytoscape;

import java.util.Vector;
import java.util.Iterator;
import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.DefaultCellEditor;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JComboBox;

/**
 * Table for selecting which attribute to use for matching nodes
 * 
 * @author JGao
 */
class MatchNodeTable extends JTable{ 
    private MatchingAttribute matchingAttribute;
    private MatchNodeTableModel model;

    public MatchNodeTable(MatchingAttribute matchingAttribute) {
        super();
        this.matchingAttribute = matchingAttribute;
        model = new MatchNodeTableModel();
        setModel(model);
    }
    
    public void setColumnEditor() {
        int n = matchingAttribute.size();
        for (int i=0; i<n; i++) {
            Vector<String> attrs = new Vector<String>();
            attrs.add(NetworkMerge.ID);
            // TODO: modify if local attribute implemented
            attrs.addAll(Arrays.asList(Cytoscape.getNodeAttributes().getAttributeNames()));
            JComboBox comboBox = new JComboBox(attrs);
            TableColumn column = getColumnModel().getColumn(i);
            column.setCellEditor(new DefaultCellEditor(comboBox));

            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setToolTipText("Click for combo box");
            column.setCellRenderer(renderer);
        }
    }

    public void fireTableStructureChanged() {
        model.fireTableStructureChanged();
        setColumnEditor();
    }

    private class MatchNodeTableModel extends AbstractTableModel {
        Vector<String> netNames;
        Vector<String> netIDs;

        public MatchNodeTableModel() {
            resetNetworks();
        }

        public int getColumnCount() {
            return matchingAttribute.size();
        }

        public int getRowCount() {
            int n = matchingAttribute.size();
            return n==0?0:1;
        }

        public String getColumnName(int col) {
            return netNames.get(col);
        }

        public Object getValueAt(int row, int col) {
            return matchingAttribute.getAttributeForMatching(netIDs.get(col));
        }

        public Class getColumnClass(int c) {
            //return JComboBox.class;
            return String.class;
        }

        /*
         * 
         * 
         */
        public boolean isCellEditable(int row, int col) {
            return true;
        }

        /* TODO
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            if (value!=null);
            matchingAttribute.putAttributeForMatching(netIDs.get(col), (String)value);
            fireTableDataChanged();
        }

        public void fireTableStructureChanged() {
            resetNetworks();
            super.fireTableStructureChanged();
        }
            
        private void resetNetworks() {
            netNames = new Vector<String>();
            netIDs = new Vector<String>();
            int size=0;
            Iterator<String> it = matchingAttribute.getNetworkSet().iterator();
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
