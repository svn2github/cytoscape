/* File: AttributeBasedIDMappingFilePanel.java

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

import csplugins.id.mapping.IDMapper;
import csplugins.id.mapping.IDMapperFromCyThesaurusService;
import csplugins.id.mapping.model.AttributeBasedIDMappingData;
import csplugins.id.mapping.util.IDMappingDataUtils;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

import cytoscape.data.CyAttributes;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import giny.model.GraphObject;

import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import java.awt.Frame;

/**
 *
 * 
 */
public class AttributeBasedIDMappingPanel extends javax.swing.JPanel {

    /** Creates new form IDMapperTextPanel */
    public AttributeBasedIDMappingPanel(final Frame frame,
                             final AttributeBasedIDMappingDialog parent,
                             final Map<String,Set<String>> selectedNetworkAttribute,
                             final boolean isNode) {
        this.frame = frame;
        this.parent = parent;
        idMapping = parent.getIDMapping();
        this.selectedNetworkAttribute = selectedNetworkAttribute;
        initSrcTypes();
        this.isNode = isNode;
        this.idMapper = new IDMapperFromCyThesaurusService();

        initComponents();
    }

    // initialize selectedNetworkAttributeIDType
    private void initSrcTypes() {
                selectedNetworkAttributeIDType = new HashMap<String,Map<String,Set<String>>>();
                Iterator<Map.Entry<String,Set<String>>> itEntry = selectedNetworkAttribute.entrySet().iterator();
                while (itEntry.hasNext()) {
                        Map.Entry<String,Set<String>> entry = itEntry.next();
                        String netID = entry.getKey();
                        Map<String,Set<String>> mapAttributeIDType = new HashMap<String,Set<String>>();
                        selectedNetworkAttributeIDType.put(netID, mapAttributeIDType);

                        Iterator<String> itAttr = entry.getValue().iterator();
                        while (itAttr.hasNext()) {
                                String attr = itAttr.next();
                                Set<String> types = new HashSet<String>();
                                mapAttributeIDType.put(attr, types);
                        }

                }
       }

    void mappingResourcesChanged() {
        this.idMapper = new IDMapperFromCyThesaurusService();
        idTypeSelectionTable.setSupportedSrcIDType(idMapper.getSupportedSrcIDTypes());

        String[] types = idMapper.getSupportedTgtIDTypes().toArray(new String[0]);
        java.util.Arrays.sort(types);
        toComboBox.setModel(new javax.swing.DefaultComboBoxModel(types));
    }

    // reset selectedNetworkAttributeIDType
//    private void resetSrcTypes(Set<String> supportedSrcIDTypes) {
//                if (supportedSrcIDTypes==null) {
//                        throw new java.lang.NullPointerException();
//                }
//                Iterator<Map<String,Set<String>>> itMapAttrType = selectedNetworkAttributeIDType.values().iterator();
//                while (itMapAttrType.hasNext()) {
//                        Map<String,Set<String>> mapAttrType = itMapAttrType.next();
//                        Iterator<Set<String>> itTypes = mapAttrType.values().iterator();
//                        while (itTypes.hasNext()) {
//                                Set<String> types = itTypes.next();
//                                types.retainAll(supportedSrcIDTypes);
//                        }
//
//                }
//       }

    Map<String,Map<String,Set<String>>> getSrcTypes() {
            return selectedNetworkAttributeIDType;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        idTypePanel = new javax.swing.JPanel();
        idTypeScrollPane = new javax.swing.JScrollPane();
        goButton = new javax.swing.JButton();
        javax.swing.JPanel toPanel = new javax.swing.JPanel();
        toLabel = new javax.swing.JLabel();
        toComboBox = new javax.swing.JComboBox();

        setMinimumSize(new java.awt.Dimension(400, 400));
        setPreferredSize(new java.awt.Dimension(600, 600));
        setLayout(new java.awt.GridBagLayout());

        idTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select ID type(s) for each attribute"));
        idTypePanel.setLayout(new javax.swing.BoxLayout(idTypePanel, javax.swing.BoxLayout.LINE_AXIS));

        idTypeScrollPane.setMinimumSize(new java.awt.Dimension(450, 200));
        idTypeScrollPane.setPreferredSize(new java.awt.Dimension(450, 200));

        idTypeSelectionTable = new IDTypeSelectionTable(frame,null);
        //idTypeSelectionTable.setMinimumSize(new java.awt.Dimension(400, 100));
        //idTypeSelectionTable.setPreferredSize(new java.awt.Dimension(450, 200));
        idTypeSelectionTable.setSupportedSrcIDType(idMapper.getSupportedSrcIDTypes());
        idTypeScrollPane.setViewportView(idTypeSelectionTable);

        idTypePanel.add(idTypeScrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(idTypePanel, gridBagConstraints);

        goButton.setText("Add ID mapping");
        goButton.setEnabled(false);
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(goButton, gridBagConstraints);

        toPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        toLabel.setText("Destination ID Type:");
        toPanel.add(toLabel);

        String[] types = idMapper.getSupportedTgtIDTypes().toArray(new String[0]);
        java.util.Arrays.sort(types);
        toComboBox.setModel(new javax.swing.DefaultComboBoxModel(types));
        toPanel.add(toComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(toPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
            CyAttributes cyAttributes = isNode? Cytoscape.getNodeAttributes():Cytoscape.getEdgeAttributes();
            Set<String> tgtTypes = new HashSet<String>();
            tgtTypes.add(getTgtType());

            final JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setOwner(frame);
            jTaskConfig.displayCloseButton(true);
            jTaskConfig.displayCancelButton(false);
            jTaskConfig.displayStatus(true);
            jTaskConfig.setAutoDispose(true);
            jTaskConfig.displayTimeElapsed(true);

            Iterator<Map.Entry<String,Map<String,Set<String>>>> itEntryNetAttrTypes = selectedNetworkAttributeIDType.entrySet().iterator();
            while (itEntryNetAttrTypes.hasNext()) {
                    Map.Entry<String,Map<String,Set<String>>> entryNetAttrTypes = itEntryNetAttrTypes.next();
                    String netID = entryNetAttrTypes.getKey();
                    CyNetwork network = Cytoscape.getNetwork(netID);

                    // get all identifiers of nodes/edges
                    Set<String> goIDs = new HashSet<String>();
                    Iterator<GraphObject> itGO = isNode? network.nodesIterator():network.edgesIterator();
                    while (itGO.hasNext()) {
                            goIDs.add(itGO.next().getIdentifier());
                    }

                    Map<String,Set<String>> mapAttrTypes = entryNetAttrTypes.getValue();
                    Iterator<Map.Entry<String,Set<String>>> itEntryAttrTypes = mapAttrTypes.entrySet().iterator();
                    while (itEntryAttrTypes.hasNext()) {
                            Map.Entry<String,Set<String>> entryAttrTypes = itEntryAttrTypes.next();
                            String attr = entryAttrTypes.getKey();
                            Set<String> potentialSrcTypes = entryAttrTypes.getValue();

                            ReadIDMappingTask task = new ReadIDMappingTask(idMapping,
                                                                   idMapper,
                                                                   goIDs,
                                                                   cyAttributes,
                                                                   attr,
                                                                   potentialSrcTypes,
                                                                   tgtTypes);
                            // Execute Task in New Thread; pop open JTask Dialog Box.
                            TaskManager.executeTask(task, jTaskConfig);
                            //if (nmTask.isCancelled()) return;
                    }
            }

            //addedFiles.add(textFileTextField.getText());
            
            //updateGoButtonEnable();

//            parent.setOKButtonEnable();
    }//GEN-LAST:event_goButtonActionPerformed


    private String getTgtType() {
            if (toComboBox.getItemCount()==0) {
                    return null;
            }
            
            return (String)toComboBox.getSelectedItem();
    }

    void updateGoButtonEnable() {

        if (getTgtType()==null) {
                goButton.setToolTipText("No target ID type available");
                goButton.setEnabled(false);
                return;
        }

        Iterator<String> itNet = selectedNetworkAttributeIDType.keySet().iterator();
        while (itNet.hasNext()) {
            String network = itNet.next();
            Map<String,Set<String>> mapAttrIDType = selectedNetworkAttributeIDType.get(network);
            Iterator<Set<String>> itTypes = mapAttrIDType.values().iterator();
            while (itTypes.hasNext()) {
                if (itTypes.next().isEmpty()) {
                        goButton.setToolTipText("Select at least one ID type for each attribute");
                        goButton.setEnabled(false);
                        return;
                }
            }
        }


        goButton.setToolTipText(null);
        goButton.setEnabled(true);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton goButton;
    private javax.swing.JPanel idTypePanel;
    private IDTypeSelectionTable idTypeSelectionTable;
    private javax.swing.JScrollPane idTypeScrollPane;
    private javax.swing.JComboBox toComboBox;
    private javax.swing.JLabel toLabel;
    // End of variables declaration//GEN-END:variables

        private Map<String,Set<String>> selectedNetworkAttribute;
        private Map<String,Map<String,Set<String>>> selectedNetworkAttributeIDType;
        private IDMapper idMapper;
        private Frame frame;
        private AttributeBasedIDMappingDialog parent;
        private AttributeBasedIDMappingData idMapping;
        private boolean isNode;
        //private Set<String> addedFiles;
}


