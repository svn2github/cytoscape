/* File: IDTypeSelectionTable.java

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

import cytoscape.Cytoscape;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Table for selecting which attribute to use for matching nodes
 * 
 * 
 */
public class IDTypeSelectionTable extends JTable{
    private IDTypeSelectionTableModel model;
    private Frame frame;
    private AttributeBasedIDMappingFilePanel parent;

    private Set<String> supportedSrcIDType;

    private List<String[]> listNetIDTitleAttr;

    public IDTypeSelectionTable(final Frame frame,
                                final AttributeBasedIDMappingFilePanel parent) {
        super();

        this.frame = frame;
        this.parent = parent;

        initNetworks();

        supportedSrcIDType = new HashSet<String>();

        model = new IDTypeSelectionTableModel();
        setModel(model);
        setRowHeight(20);

        addMouseClickListener();

    }

    void setSupportedSrcIDType(final Set<String> types) {
            supportedSrcIDType = types;
    }

        private void initNetworks() {
            Map<String,Map<String,Set<String>>> selectedNetworkAttributeIDType = parent.getSrcTypes();

            List<String> netTitles = new Vector<String>();
            List<String> netIDs = new Vector<String>();
            int size=0;
            Iterator<String> it = selectedNetworkAttributeIDType.keySet().iterator();
            while (it.hasNext()) {
                String netID = it.next();
                String netName = Cytoscape.getNetwork(netID).getTitle();
                int index = 0;
                while (index<size && netTitles.get(index).compareToIgnoreCase(netName)<0) index++;

                netIDs.add(index,netID);
                netTitles.add(index,netName);
                size++;
            }

            listNetIDTitleAttr = new Vector<String[]>();

            int n = netIDs.size();
            for (int i=0; i<n; i++) {
                    String id = netIDs.get(i);
                    Iterator<String> itAttr = selectedNetworkAttributeIDType.get(id).keySet().iterator();
                    while (itAttr.hasNext()) {
                            String attr = itAttr.next();
                            listNetIDTitleAttr.add(new String[]{id,netTitles.get(i),attr});
                    }
            }
       }

    private void addMouseClickListener() {
            addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                         IDTypeSelectionTable source = (IDTypeSelectionTable)e.getSource();
                         int column = source.getSelectedColumn();
                         if (column==2) {
                            int row = source.getSelectedRow();
                            String netID = listNetIDTitleAttr.get(row)[0];
                            String attr = listNetIDTitleAttr.get(row)[2];
                            Map<String,Set<String>> mapAttrType = parent.getSrcTypes().get(netID);
                            IDTypeSelectionDialog dialog = new IDTypeSelectionDialog(frame,
                                                                                     true,
                                                                                     supportedSrcIDType,
                                                                                     mapAttrType.get(attr));
                            dialog.setLocationRelativeTo(source.getParent());
                            dialog.setVisible(true);
                            if (!dialog.isCancelled()) {
                                mapAttrType.put(attr, dialog.getSelectedIDTypes());
                                source.fireTableDataChanged();
                                parent.updateGoButtonEnable();
                            }
                         }
                    }
                });
    }

    void fireTableDataChanged() {
            model.fireTableDataChanged();
    }

    private class IDTypeSelectionTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Network","Attribute","ID Type(s)"};
        //private List<String> netNames;
        //private List<String> netIDs;

        //public IDTypeSelectionTableModel() {
            //setNetworks();
       // }

        @Override
        public int getColumnCount() {
            return getRowCount()==0?0:3; // network; attribute; id types
        }

        @Override
        public int getRowCount() {
            return listNetIDTitleAttr.size();
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public String getValueAt(int row, int col) {
            String[] strs = listNetIDTitleAttr.get(row);
            String netID = strs[0];
            String netTitle = strs[1];
            String attr = strs[2];
            switch (col) {
                case 0:
                    return netTitle;
                case 1:
                    return attr;
                case 2:
                    return parent.getSrcTypes().get(netID).get(attr).toString();
                default:
                    throw new java.lang.IndexOutOfBoundsException();
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

    }

}
