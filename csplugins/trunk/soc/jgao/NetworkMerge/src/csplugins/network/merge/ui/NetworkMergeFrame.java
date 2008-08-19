/* File: NetworkMergeFrame.java

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
import csplugins.network.merge.AttributeBasedNetworkMerge;
import csplugins.network.merge.model.AttributeMappingImpl;
import csplugins.network.merge.model.MatchingAttributeImpl;
import csplugins.network.merge.model.AttributeMapping;
import csplugins.network.merge.model.MatchingAttribute;
import csplugins.network.merge.NetworkMerge.Operation;
import csplugins.network.merge.conflict.AttributeConflictHandler;
//import csplugins.network.merge.conflict.IDMappingAttributeConflictHandler;
import csplugins.network.merge.conflict.DefaultAttributeConflictHandler;
import csplugins.network.merge.conflict.AttributeConflictManager;
import csplugins.network.merge.conflict.AttributeConflictCollector;
import csplugins.network.merge.conflict.AttributeConflictCollectorImpl;
import csplugins.network.merge.util.AttributeValueMatcher;
import csplugins.network.merge.util.DefaultAttributeValueMatcher;
import csplugins.network.merge.util.IDMappingAttributeValueMatcher;
import csplugins.network.merge.util.AttributeMerger;
import csplugins.network.merge.util.DefaultAttributeMerger;
import csplugins.network.merge.util.IDMappingAttributeMerger;
import csplugins.id.mapping.ui.IDMappingPreviewDialog;
import csplugins.id.mapping.model.AttributeBasedIDMappingData;

import cytoscape.data.Semantics;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.GraphSetUtils;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;
import cytoscape.util.CyNetworkNaming;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import java.util.List;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Insets;
import java.awt.GridLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.ListCellRenderer;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.BoxLayout;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 *
 * Main frame for advance network merge
 */
public class NetworkMergeFrame extends javax.swing.JFrame {

    /** Creates new form NetworkMergeFrame */
    public NetworkMergeFrame() {
        frame = this;
        idMapping = null;
        matchingAttribute = new MatchingAttributeImpl(Cytoscape.getNodeAttributes());
        nodeAttributeMapping = new AttributeMappingImpl(Cytoscape.getNodeAttributes());
        edgeAttributeMapping = new AttributeMappingImpl(Cytoscape.getEdgeAttributes());

        collapsiblePanel = new CollapsiblePanel("Advance Options");

        collapsiblePanel.addCollapeListener(new ResizeCollapeListener(this));

        initComponents();
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

                javax.swing.JPanel operationPanel = new javax.swing.JPanel();
                javax.swing.JLabel operationLabel = new javax.swing.JLabel();
                operationComboBox = new javax.swing.JComboBox();
                operationIcon = new javax.swing.JLabel();
                javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
                javax.swing.JPanel selectNetworkPanel = new javax.swing.JPanel();
                javax.swing.JScrollPane unselectedNetworkScrollPane = new javax.swing.JScrollPane();
                unselectedNetworkData = new NetworkListModel();
                unselectedNetworkList = new javax.swing.JList(unselectedNetworkData);
                javax.swing.JPanel udButtonPanel = new javax.swing.JPanel();
                rightButton = new javax.swing.JButton();
                leftButton = new javax.swing.JButton();
                javax.swing.JScrollPane selectedNetworkScrollPane = new javax.swing.JScrollPane();
                selectedNetworkData = new NetworkListModel();
                selectedNetworkList = new javax.swing.JList(selectedNetworkData);
                collapsiblePanelAgent = collapsiblePanel;
                advancedPanel = new javax.swing.JPanel();
                javax.swing.JSeparator jSeparator2 = new javax.swing.JSeparator();
                attributePanel = new javax.swing.JPanel();
                matchNodeTable = new MatchNodeTable(matchingAttribute);
                attributeScrollPane = new javax.swing.JScrollPane();
                javax.swing.JPanel idMappingPanel = new javax.swing.JPanel();
                importIDMappingButton = new javax.swing.JButton();
                viewIDMappingButton = new javax.swing.JButton();
                javax.swing.JSeparator jSeparator3 = new javax.swing.JSeparator();
                javax.swing.JPanel mergeAttributePanel = new javax.swing.JPanel();
                javax.swing.JTabbedPane mergeAttributeTabbedPane = new javax.swing.JTabbedPane();
                javax.swing.JPanel mergeNodeAttributePanel = new javax.swing.JPanel();
                javax.swing.JScrollPane mergeNodeAttributeScrollPane = new javax.swing.JScrollPane();
                javax.swing.JPanel mergeEdgeAttributePanel = new javax.swing.JPanel();
                javax.swing.JScrollPane mergeEdgeAttributeScrollPane = new javax.swing.JScrollPane();
                javax.swing.JSeparator jSeparator4 = new javax.swing.JSeparator();
                javax.swing.JPanel okPanel = new javax.swing.JPanel();
                cancelButton = new javax.swing.JButton();
                okButton = new javax.swing.JButton();

                setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
                setAlwaysOnTop(true);
                getContentPane().setLayout(new java.awt.GridBagLayout());

                operationPanel.setMinimumSize(new java.awt.Dimension(211, 20));
                operationPanel.setLayout(new javax.swing.BoxLayout(operationPanel, javax.swing.BoxLayout.LINE_AXIS));

                operationLabel.setText("Operation:   ");
                operationPanel.add(operationLabel);

                operationComboBox.setModel(new javax.swing.DefaultComboBoxModel(new Operation[] { Operation.UNION,Operation.INTERSECTION,Operation.DIFFERENCE }));
                operationComboBox.setPreferredSize(new java.awt.Dimension(150, 20));
                operationComboBox.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                operationIcon.setIcon(OPERATION_ICONS[operationComboBox.getSelectedIndex()]);
                                //mergeNodeAttributeTable.setMergedNetworkName(getDefaultMergedNetworkName());
                                //mergeEdgeAttributeTable.setMergedNetworkName(getDefaultMergedNetworkName());
                                pack();
                        }
                });
                operationPanel.add(operationComboBox);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                getContentPane().add(operationPanel, gridBagConstraints);

                operationIcon.setIcon(UNION_ICON);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                getContentPane().add(operationIcon, gridBagConstraints);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                getContentPane().add(jSeparator1, gridBagConstraints);

                selectNetworkPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Please select network to merge"));
                selectNetworkPanel.setMinimumSize(new java.awt.Dimension(490, 100));
                selectNetworkPanel.setPreferredSize(new java.awt.Dimension(490, 100));
                selectNetworkPanel.setLayout(new java.awt.GridBagLayout());

                unselectedNetworkScrollPane.setPreferredSize(new java.awt.Dimension(200, 100));

                for (Iterator<CyNetwork> it = Cytoscape.getNetworkSet().iterator(); it.hasNext(); ) {
                        CyNetwork network = it.next();
                        unselectedNetworkData.add(network);
                }

                unselectedNetworkList.setCellRenderer(new ListCellRenderer() {
                        private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
                        public Component getListCellRendererComponent(
                                JList list,
                                Object value,
                                int index,
                                boolean isSelected,
                                boolean cellHasFocus) {
                                JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                                renderer.setText(((CyNetwork)value).getTitle());
                                return renderer;
                        }
                });

                unselectedNetworkList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                                int index = unselectedNetworkList.getMinSelectionIndex();
                                if (index>-1) {
                                        selectedNetworkList.getSelectionModel().clearSelection();
                                        rightButton.setEnabled(true);
                                } else {
                                        rightButton.setEnabled(false);
                                }
                        }
                });
                unselectedNetworkScrollPane.setViewportView(unselectedNetworkList);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 0.5;
                gridBagConstraints.weighty = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
                selectNetworkPanel.add(unselectedNetworkScrollPane, gridBagConstraints);

                udButtonPanel.setLayout(new java.awt.GridLayout(0, 1, 0, 2));

                rightButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/right16.gif"))); // NOI18N
                rightButton.setEnabled(false);
                rightButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                int [] indices = unselectedNetworkList.getSelectedIndices();
                                if (indices == null || indices.length == 0) {
                                        return;
                                }

                                for (int i= indices.length-1; i>=0; i--) {
                                        CyNetwork removed = unselectedNetworkData.removeElement(indices[i]);
                                        selectedNetworkData.add(removed);
                                        addRemoveAttributeMapping(removed,true);
                                }

                                if (unselectedNetworkData.getSize()==0) {
                                        unselectedNetworkList.clearSelection();
                                        rightButton.setEnabled(false);
                                } else {
                                        int minindex = unselectedNetworkList.getMinSelectionIndex();
                                        if (minindex>= unselectedNetworkData.getSize()) {
                                                minindex = 0;
                                        }
                                        unselectedNetworkList.setSelectedIndex(minindex);
                                }

                                selectedNetworkList.repaint();
                                unselectedNetworkList.repaint();
                                updataIdMappingButtonEnable();
                                updateOKButtonEnable();
                                updateAttributeTable();
                                updateMergeAttributeTable();
                        }
                });
                udButtonPanel.add(rightButton);

                leftButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/left16.gif"))); // NOI18N
                leftButton.setEnabled(false);
                leftButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                int [] indices = selectedNetworkList.getSelectedIndices();
                                if (indices == null || indices.length == 0) {
                                        return;
                                }

                                for (int i= indices.length-1; i>=0; i--) {
                                        CyNetwork removed = selectedNetworkData.removeElement(indices[i]);
                                        unselectedNetworkData.add(removed);
                                        addRemoveAttributeMapping(removed,false);
                                }

                                if (selectedNetworkData.getSize()==0) {
                                        selectedNetworkList.clearSelection();
                                        leftButton.setEnabled(false);
                                } else {
                                        int minindex = selectedNetworkList.getMinSelectionIndex();
                                        if (minindex>= selectedNetworkData.getSize()) {
                                                minindex = 0;
                                        }
                                        selectedNetworkList.setSelectedIndex(minindex);
                                }

                                selectedNetworkList.repaint();
                                unselectedNetworkList.repaint();
                                updataIdMappingButtonEnable();
                                updateOKButtonEnable();
                                updateAttributeTable();
                                updateMergeAttributeTable();
                        }
                });
                udButtonPanel.add(leftButton);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 2;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
                selectNetworkPanel.add(udButtonPanel, gridBagConstraints);

                selectedNetworkScrollPane.setPreferredSize(new java.awt.Dimension(200, 100));

                selectedNetworkList.setCellRenderer(new ListCellRenderer() {
                        private DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
                        public Component getListCellRendererComponent(
                                JList list,
                                Object value,
                                int index,
                                boolean isSelected,
                                boolean cellHasFocus) {
                                JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                                renderer.setText(((CyNetwork)value).getTitle());
                                return renderer;
                        }
                });
                selectedNetworkList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                                int index = selectedNetworkList.getMinSelectionIndex();
                                if (index>-1) {
                                        unselectedNetworkList.getSelectionModel().clearSelection();
                                        leftButton.setEnabled(true);
                                } else {
                                        leftButton.setEnabled(false);
                                }
                        }
                });
                selectedNetworkScrollPane.setViewportView(selectedNetworkList);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 3;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 0.5;
                gridBagConstraints.weighty = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
                selectNetworkPanel.add(selectedNetworkScrollPane, gridBagConstraints);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 3;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 0.5;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                getContentPane().add(selectNetworkPanel, gridBagConstraints);

                collapsiblePanelAgent.setLayout(new java.awt.BorderLayout());

                advancedPanel.setPreferredSize(new java.awt.Dimension(690, 400));
                advancedPanel.setLayout(new java.awt.GridBagLayout());
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 0;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                advancedPanel.add(jSeparator2, gridBagConstraints);

                attributePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Please select an attribute for each network to match/identify nodes"));
                attributePanel.setMinimumSize(new java.awt.Dimension(400, 70));
                attributePanel.setPreferredSize(new java.awt.Dimension(466, 73));
                attributePanel.setLayout(new javax.swing.BoxLayout(attributePanel, javax.swing.BoxLayout.LINE_AXIS));

                attributeScrollPane.setMinimumSize(new java.awt.Dimension(100, 50));
                attributeScrollPane.setPreferredSize(new java.awt.Dimension(450, 50));
                matchNodeTable.getModel().addTableModelListener(new TableModelListener() {
                        public void tableChanged(TableModelEvent e) {
                                mergeNodeAttributeTable.updateMatchingAttribute();
                        }
                });

                attributeScrollPane.setViewportView(matchNodeTable);

                attributePanel.add(attributeScrollPane);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                advancedPanel.add(attributePanel, gridBagConstraints);

                idMappingPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

                importIDMappingButton.setText("Import ID mapping");
                importIDMappingButton.setEnabled(false);
                importIDMappingButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                Map<String,Set<String>> selectedNetworkAttribute = new HashMap<String,Set<String>>();
                                Iterator<Map.Entry<String,String>> itEntry = matchingAttribute.getNetAttrMap().entrySet().iterator();
                                while (itEntry.hasNext()) {
                                        Map.Entry<String,String> entry = itEntry.next();
                                        String netID = entry.getKey();
                                        String attr = entry.getValue();
                                        Set<String> attrs = new HashSet<String>(1);
                                        attrs.add(attr);
                                        selectedNetworkAttribute.put(netID,attrs);
                                }

                                boolean isFrameAlwaysOnTop = frame.isAlwaysOnTop();
                                frame.setAlwaysOnTop(false);

                                final boolean isNode = true;
                                csplugins.id.mapping.ui.AttributeBasedIDMappingDialog dialog = new csplugins.id.mapping.ui.AttributeBasedIDMappingDialog(frame,true,selectedNetworkAttribute,isNode);
                                dialog.setLocationRelativeTo(frame);
                                dialog.setVisible(true);
                                if (!dialog.isCancelled()) {
                                        idMapping = dialog.getIDMapping();
                                        if (idMapping!=null) {
                                                if (idMapping.isEmpty()) {
                                                        idMapping = null;
                                                } else {
                                                        viewIDMappingButton.setEnabled(true);
                                                }
                                        }
                                }
                                frame.setAlwaysOnTop(isFrameAlwaysOnTop);
                        }
                });
                idMappingPanel.add(importIDMappingButton);

                viewIDMappingButton.setText("View ID mapping");
                viewIDMappingButton.setEnabled(false);
                viewIDMappingButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                viewIDMappingButtonActionPerformed(evt);
                        }
                });
                idMappingPanel.add(viewIDMappingButton);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 2;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                advancedPanel.add(idMappingPanel, gridBagConstraints);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 3;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                advancedPanel.add(jSeparator3, gridBagConstraints);

                mergeAttributePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Please specify how to merge attributes"));
                mergeAttributePanel.setMinimumSize(new java.awt.Dimension(400, 200));
                mergeAttributePanel.setPreferredSize(new java.awt.Dimension(600, 200));
                mergeAttributePanel.setLayout(new javax.swing.BoxLayout(mergeAttributePanel, javax.swing.BoxLayout.LINE_AXIS));

                mergeAttributeTabbedPane.setMinimumSize(new java.awt.Dimension(450, 150));
                mergeAttributeTabbedPane.setPreferredSize(new java.awt.Dimension(450, 200));

                mergeNodeAttributePanel.setLayout(new javax.swing.BoxLayout(mergeNodeAttributePanel, javax.swing.BoxLayout.LINE_AXIS));

                mergeNodeAttributeTable = new MergeAttributeTable(nodeAttributeMapping,matchingAttribute);
                mergeNodeAttributeScrollPane.setViewportView(mergeNodeAttributeTable);

                mergeNodeAttributePanel.add(mergeNodeAttributeScrollPane);

                mergeAttributeTabbedPane.addTab("Node", mergeNodeAttributePanel);

                mergeEdgeAttributePanel.setLayout(new javax.swing.BoxLayout(mergeEdgeAttributePanel, javax.swing.BoxLayout.LINE_AXIS));

                mergeEdgeAttributeTable = new MergeAttributeTable(edgeAttributeMapping);
                mergeEdgeAttributeScrollPane.setViewportView(mergeEdgeAttributeTable);

                mergeEdgeAttributePanel.add(mergeEdgeAttributeScrollPane);

                mergeAttributeTabbedPane.addTab("Edge", mergeEdgeAttributePanel);

                mergeAttributePanel.add(mergeAttributeTabbedPane);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 4;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                advancedPanel.add(mergeAttributePanel, gridBagConstraints);

                /*

                collapsiblePanelAgent.add(advancedPanel, java.awt.BorderLayout.CENTER);
                */
                collapsiblePanel.getContentPane().add(advancedPanel, java.awt.BorderLayout.CENTER);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 4;
                gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.weighty = 1.0;
                getContentPane().add(collapsiblePanelAgent, gridBagConstraints);
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 5;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                getContentPane().add(jSeparator4, gridBagConstraints);

                okPanel.setDoubleBuffered(false);
                okPanel.setLayout(new javax.swing.BoxLayout(okPanel, javax.swing.BoxLayout.LINE_AXIS));

                cancelButton.setText("Cancel");
                cancelButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                setVisible(false);
                                dispose();
                        }
                });
                okPanel.add(cancelButton);

                okButton.setText("   OK   ");
                okButton.setToolTipText("\"Select at least two networks to merge\"");
                okButton.setEnabled(false);
                okButton.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                                okButtonActionPerformed(evt);
                        }
                });
                okPanel.add(okButton);

                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 6;
                gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
                gridBagConstraints.weightx = 1.0;
                gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
                getContentPane().add(okPanel, gridBagConstraints);

                pack();
        }// </editor-fold>//GEN-END:initComponents

    private void viewIDMappingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewIDMappingButtonActionPerformed
            IDMappingPreviewDialog dialog = new IDMappingPreviewDialog(frame,true,idMapping);
            dialog.setLocationRelativeTo(frame);
            dialog.setVisible(true);
            if (idMapping.isEmpty()) {
                    idMapping = null;
                    viewIDMappingButton.setEnabled(false);
            }
    }//GEN-LAST:event_viewIDMappingButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
            this.setAlwaysOnTop(false);
            if (this.collapsiblePanel.isCollapsed()) {
                        if (getOperation() == Operation.UNION) {
                                GraphSetUtils.createUnionGraph(this.selectedNetworkData.getNetworkList(), true,
                                                CyNetworkNaming.getSuggestedNetworkTitle("Union"));
                        } else if (getOperation() == Operation.INTERSECTION) {
                                GraphSetUtils.createIntersectionGraph(this.selectedNetworkData.getNetworkList(), true,
                                                            CyNetworkNaming.getSuggestedNetworkTitle("Intersection"));
                        } else if (getOperation() == Operation.DIFFERENCE) {
                                GraphSetUtils.createDifferenceGraph(this.selectedNetworkData.getNetworkList(), true,
                                                CyNetworkNaming.getSuggestedNetworkTitle("Difference"));
                        }

                } else {


                        //AttributeBasedIDMappingData idMapping = getIDMapping();

                        AttributeConflictCollector conflictCollector = new AttributeConflictCollectorImpl();

                        // network merge task
                        Task task = new NetworkMergeSessionTask(
                                            this.matchingAttribute,
                                            this.nodeAttributeMapping,
                                            this.edgeAttributeMapping,
                                            this.selectedNetworkData.getNetworkList(),
                                            getOperation(),
                                            mergeNodeAttributeTable.getMergedNetworkName(),
                                            conflictCollector,
                                            idMapping);

                        // Configure JTask Dialog Pop-Up Box
                        final JTaskConfig jTaskConfig = new JTaskConfig();
                        jTaskConfig.setOwner(Cytoscape.getDesktop());
                        jTaskConfig.displayCloseButton(true);
                        jTaskConfig.displayCancelButton(false);
                        jTaskConfig.displayStatus(true);
                        jTaskConfig.setAutoDispose(false);

                        // Execute Task in New Thread; pop open JTask Dialog Box.
                        TaskManager.executeTask(task, jTaskConfig);

                        // conflict handling task
                        if (!conflictCollector.isEmpty()) {
                                task = new HandleConflictsTask(conflictCollector, idMapping);
                                TaskManager.executeTask(task, jTaskConfig);
                        }

                }

            setVisible(false);
            dispose();
    }//GEN-LAST:event_okButtonActionPerformed

/*
 * Call when adding or removing a network to/from selected network list
 * 
 */
private void addRemoveAttributeMapping(CyNetwork network, boolean isAdd) {
    final String netID = network.getIdentifier();
    
    if (isAdd) {
        nodeAttributeMapping.addNetwork(netID);
        edgeAttributeMapping.addNetwork(netID);
        matchingAttribute.addNetwork(netID);
    } else {
        nodeAttributeMapping.removeNetwork(netID);
        edgeAttributeMapping.removeNetwork(netID);
        matchingAttribute.removeNetwork(netID);
    }
}

private void updataIdMappingButtonEnable() {
    if (selectedNetworkData.getSize()<1) {
        importIDMappingButton.setToolTipText("Select at least one networks to merge");
        importIDMappingButton.setEnabled(false);
        return;
    }
    
    importIDMappingButton.setToolTipText("Click to import ID mappings for matching nodes");
    importIDMappingButton.setEnabled(true);
}

private void updateOKButtonEnable() {
    if (selectedNetworkData.getSize()<1) {
        okButton.setToolTipText("Select at least one networks to merge");
        okButton.setEnabled(false);
        return;
    }
    
    /*
    if (idMappingCheckBox.isSelected()) { // if use ID mappins
        // Check whether ID mappings for all the selected attributes of
        // the selected networks have been imported
        int n = selectedNetworkData.getSize();
        for (int i=0; i<n; i++) {
            String network = (String)selectedNetworkData.getElementAt(i);
            Map<String,Vector<CyIDMapping>> idMappingNet = idMapping.get(network);
            if (null==idMappingNet) {
                okButton.setToolTipText("Please import the ID mappings for all the networks");
                okButton.setEnabled(false);
                return;                
            } else if (!mapNetCombo.containsKey(network)) {
                okButton.setToolTipText("Please import the ID mappings for all the networks");
                okButton.setEnabled(false);
                return;
            }
        }
    }*/
    
    
    okButton.setToolTipText(null);
    okButton.setEnabled(true);
}

private void updateAttributeTable() {
    matchNodeTable.fireTableStructureChanged();
}


private void updateMergeAttributeTable() {
    mergeNodeAttributeTable.fireTableStructureChanged();
    mergeEdgeAttributeTable.fireTableStructureChanged();
}

/*
 * Get currently selected operation
 * 
 */
private Operation getOperation() {
    return (Operation) operationComboBox.getSelectedItem();
}

    private MergeAttributeTable mergeNodeAttributeTable;
    private MergeAttributeTable mergeEdgeAttributeTable;
    private MatchNodeTable matchNodeTable;
    private AttributeMapping nodeAttributeMapping;
    private AttributeMapping edgeAttributeMapping;
    private MatchingAttribute matchingAttribute;
    private AttributeBasedIDMappingData idMapping;

    private Frame frame;

    private final ImageIcon UNION_ICON = new ImageIcon(getClass().getResource("/images/union.png"));
    private final ImageIcon INTERSECTION_ICON = new ImageIcon(getClass().getResource("/images/intersection.png"));
    private final ImageIcon DIFFERENCE_ICON = new ImageIcon(getClass().getResource("/images/difference.png"));
    private final ImageIcon[] OPERATION_ICONS =  { UNION_ICON, INTERSECTION_ICON, DIFFERENCE_ICON };

    CollapsiblePanel collapsiblePanel;

        // Variables declaration - do not modify//GEN-BEGIN:variables
        private javax.swing.JPanel advancedPanel;
        private javax.swing.JPanel attributePanel;
        private javax.swing.JScrollPane attributeScrollPane;
        private javax.swing.JButton cancelButton;
        private javax.swing.JPanel collapsiblePanelAgent;
        private javax.swing.JButton importIDMappingButton;
        private javax.swing.JButton leftButton;
        private javax.swing.JButton okButton;
        private javax.swing.JComboBox operationComboBox;
        private javax.swing.JLabel operationIcon;
        private javax.swing.JButton rightButton;
        private javax.swing.JList selectedNetworkList;
        private NetworkListModel selectedNetworkData;
        private javax.swing.JList unselectedNetworkList;
        private NetworkListModel unselectedNetworkData;
        private javax.swing.JButton viewIDMappingButton;
        // End of variables declaration//GEN-END:variables

        class ResizeCollapeListener implements CollapsiblePanel.CollapeListener {
                private final JFrame frame;
                //private final Dimension collapedDim, expandedDim;

                public ResizeCollapeListener(JFrame frame) {
                        this.frame = frame;
                        //this.collapedDim = collapedDim;
                        //this.expandedDim = expandedDim;
                }

                public void collaped() {Dimension dim;
                        if (getOperation()!=Operation.DIFFERENCE) {
                                dim = new Dimension(500,300);
                        } else {
                                dim = new Dimension(500,400);
                        }

                        frame.setSize(dim);
                }

                public void expanded() {
                        if (frame.getExtendedState()==Frame.MAXIMIZED_BOTH) {
                                return;
                        }

                        Dimension dim_curr = frame.getSize(); // current dim
                        int width_curr = dim_curr.width;
                        int height_curr = dim_curr.height;

                        int width = 700;
                        int height = getOperation()==Operation.DIFFERENCE?800:700;

                        if (width < width_curr) {
                                width = width_curr;
                        }

                        if (height < height_curr) {
                                height = height_curr;
                        }

                        Dimension dim = new Dimension(width,height);

                        frame.setSize(dim);
                }
        }
}

class NetworkListModel extends AbstractListModel {
        // Using a SortedMap from String to network
        TreeMap<String,CyNetwork> model;

        public NetworkListModel() {
            model = new TreeMap<String,CyNetwork>();
        }

        @Override
        public int getSize() {
            return model.size();
        }

        @Override
        public CyNetwork getElementAt(int index) {
            return (CyNetwork) model.values().toArray()[index];
        }

        public void add(CyNetwork network) {
            String title = network.getTitle();
            model.put(title.toUpperCase(),network);
            fireContentsChanged(this, 0, getSize());
        }

        public CyNetwork removeElement(int index) {
            CyNetwork removed = model.remove(getElementAt(index).getTitle().toUpperCase());
            if (removed!=null) {
                fireContentsChanged(this, 0, getSize());
            }
            return removed;   
        }
        
        public List<CyNetwork> getNetworkList() {
            return new Vector<CyNetwork>(model.values());
        }
}

class NetworkMergeSessionTask implements Task {
    private MatchingAttribute matchingAttribute;
    private AttributeMapping nodeAttributeMapping;
    private AttributeMapping edgeAttributeMapping;
    private List<CyNetwork> selectedNetworkList;
    private Operation operation;
    private String mergedNetworkName;
    AttributeConflictCollector conflictCollector;
    AttributeBasedIDMappingData idMapping;

    private TaskMonitor taskMonitor;

    /**
     * Constructor.<br>
     *
     */
    NetworkMergeSessionTask( final MatchingAttribute matchingAttribute,
                             final AttributeMapping nodeAttributeMapping,
                             final AttributeMapping edgeAttributeMapping,
                             final List<CyNetwork> selectedNetworkList,
                             final Operation operation,
                             final String mergedNetworkName,
                             final AttributeConflictCollector conflictCollector,
                             final AttributeBasedIDMappingData idMapping) {
        this.matchingAttribute = matchingAttribute;
        this.nodeAttributeMapping = nodeAttributeMapping;
        this.edgeAttributeMapping = edgeAttributeMapping;
        this.selectedNetworkList = selectedNetworkList;
        this.operation = operation;
        this.mergedNetworkName = mergedNetworkName;
        this.conflictCollector = conflictCollector;
        this.idMapping = idMapping;
    }

    /**
     * Executes Task
     *
     * @throws
     * @throws Exception
     */
    @Override
    public void run() {
        taskMonitor.setStatus("Merging networks.\n\nIt may take a while.\nPlease wait...");
        taskMonitor.setPercentCompleted(0);



        try {
            final AttributeValueMatcher attributeValueMatcher;
            final AttributeMerger attributeMerger;
            if (idMapping==null) {
                    attributeValueMatcher = new DefaultAttributeValueMatcher();
                    attributeMerger = new DefaultAttributeMerger(conflictCollector);
            } else {
                    attributeValueMatcher = new IDMappingAttributeValueMatcher(idMapping);
                    attributeMerger = new IDMappingAttributeMerger(conflictCollector,idMapping);
            }

            final NetworkMerge networkMerge = new AttributeBasedNetworkMerge(
                                matchingAttribute,
                                nodeAttributeMapping,
                                edgeAttributeMapping,
                                attributeMerger,
                                attributeValueMatcher);

            CyNetwork mergedNetwork = networkMerge.mergeNetwork(
                                selectedNetworkList,
                                operation,
                                mergedNetworkName);


/*
            cytoscape.view.CyNetworkView networkView = Cytoscape.getNetworkView(mergedNetworkName);

            // get the VisualMappingManager and CalculatorCatalog
            cytoscape.visual.VisualMappingManager manager = Cytoscape.getVisualMappingManager();
            cytoscape.visual.CalculatorCatalog catalog = manager.getCalculatorCatalog();

            cytoscape.visual.VisualStyle vs = catalog.getVisualStyle(mergedNetworkName+" Visual Style");
            if (vs == null) {
                    // if not, create it and add it to the catalog
                    //vs = createVisualStyle(networkMerge);
                    cytoscape.visual.NodeAppearanceCalculator nodeAppCalc = new cytoscape.visual.NodeAppearanceCalculator();
                    cytoscape.visual.mappings.PassThroughMapping pm = new cytoscape.visual.mappings.PassThroughMapping(new String(), cytoscape.data.Semantics.CANONICAL_NAME);

                    cytoscape.visual.calculators.Calculator nlc = new cytoscape.visual.calculators.BasicCalculator(null,
                                                     pm, cytoscape.visual.VisualPropertyType.NODE_LABEL);
                    nodeAppCalc.setCalculator(nlc);

                    vs.setNodeAppearanceCalculator(nodeAppCalc);

                    catalog.addVisualStyle(vs);
            }
            // actually apply the visual style
            manager.setVisualStyle(vs);
            networkView.redrawGraph(true,true);
*/

            taskMonitor.setPercentCompleted(100);
            taskMonitor.setStatus("The selected networks were successfully merged into network '"
                                  + mergedNetwork.getTitle()
                                  + "' with "
                                  + conflictCollector.getConfilctCount()
                                  + " attribute conflicts.");

        } catch(Exception e) {
            taskMonitor.setPercentCompleted(100);
            taskMonitor.setStatus("Network Merge Failed!");
            e.printStackTrace();
        }

    }

    /**
     * Halts the Task: Not Currently Implemented.
     */
    @Override
    public void halt() {
            // Task can not currently be halted.
            taskMonitor.setPercentCompleted(100);
            taskMonitor.setStatus("Failed!!!");
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor
     *            TaskMonitor Object.
     */
    @Override
    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
            this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     *
     * @return Task Title.
     */
    @Override
    public String getTitle() {
            return "Merging networks";
    }
}

class HandleConflictsTask implements Task {
    private AttributeConflictCollector conflictCollector;
    private AttributeBasedIDMappingData idMapping;

    private TaskMonitor taskMonitor;

    /**
     * Constructor.<br>
     *
     */
    HandleConflictsTask(final AttributeConflictCollector conflictCollector,
                        final AttributeBasedIDMappingData idMapping) {
        this.conflictCollector = conflictCollector;
        this.idMapping = idMapping;
    }

    /**
     * Executes Task
     *
     * @throws
     * @throws Exception
     */
    @Override
    public void run() {
        taskMonitor.setStatus("Handle conflicts.\n\nIt may take a while.\nPlease wait...");
        taskMonitor.setPercentCompleted(0);

        try {
             int nBefore = conflictCollector.getConfilctCount();

             List<AttributeConflictHandler> conflictHandlers = new Vector<AttributeConflictHandler>();

             AttributeConflictHandler conflictHandler;

//             if (idMapping!=null) {
//                conflictHandler = new IDMappingAttributeConflictHandler(idMapping);
//                conflictHandlers.add(conflictHandler);
//             }

             conflictHandler = new DefaultAttributeConflictHandler();
             conflictHandlers.add(conflictHandler);

             AttributeConflictManager conflictManager = new AttributeConflictManager(conflictCollector,conflictHandlers);
             conflictManager.handleConflicts();

             int nAfter = conflictCollector.getConfilctCount();

             taskMonitor.setPercentCompleted(100);
             taskMonitor.setStatus("Successfully handled " + (nBefore-nAfter) + " attribute conflicts. "
                                        + nAfter+" conflicts remains.");
        } catch(Exception e) {
                taskMonitor.setPercentCompleted(100);
                taskMonitor.setStatus("Conflict handle Failed!");
                e.printStackTrace();
        }

    }

    /**
     * Halts the Task: Not Currently Implemented.
     */
    @Override
    public void halt() {
            // Task can not currently be halted.
            taskMonitor.setPercentCompleted(100);
            taskMonitor.setStatus("Failed!!!");
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor
     *            TaskMonitor Object.
     */
    @Override
    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
            this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     *
     * @return Task Title.
     */
    @Override
    public String getTitle() {
            return "Merging networks";
    }
}
