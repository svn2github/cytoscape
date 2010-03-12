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

import csplugins.id.mapping.IDMapperClient;
import csplugins.id.mapping.IDMapperClientManager;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultMutableTreeNode;

import org.bridgedb.AttributeMapper;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperCapabilities;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
/**
 *
 * @author gjj
 */
public class IDMappingSourceConfigDialog extends javax.swing.JDialog {


    public IDMappingSourceConfigDialog(javax.swing.JFrame parent, boolean modal) {
        super(parent, modal);
        init();
    }

    public IDMappingSourceConfigDialog(javax.swing.JDialog parent, boolean modal) {
        super(parent, modal);
        init();
    }

    public void init() {
        initComponents();

        srcTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath path = e.getPath();
                if (path==null) {
                    descTextArea.setText(msg);
                } else {
                    Object nodeObj = path.getLastPathComponent();
                    if (nodeObj instanceof DefaultMutableTreeNode) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode)nodeObj;
                        Object clientObj = node.getUserObject();
                        if (clientObj instanceof IDMapperClient) {
                            IDMapperClient client = (IDMapperClient)clientObj;

                            final JTaskConfig jTaskConfig = new JTaskConfig();
                            jTaskConfig.setOwner(cytoscape.Cytoscape.getDesktop());
                            jTaskConfig.displayCloseButton(true);
                            jTaskConfig.displayCancelButton(false);
                            jTaskConfig.displayStatus(true);
                            jTaskConfig.setAutoDispose(true);
                            jTaskConfig.setMillisToPopup(100);

                            LoadClientDescTask task = new LoadClientDescTask(client);
                            TaskManager.executeTask(task, jTaskConfig);

                            String desc;
                            if (task.success()) {
                                desc = task.description();
                            } else {
                                desc = "Failed to retrieve the information about this ID mapping client.";
                            }

                            descTextArea.setText(desc);
                        } else {
                            descTextArea.setText(msg);
                        }
                    } else {
                        descTextArea.setText(msg);
                    }
                }

                descTextArea.repaint();
                descScrollPane.repaint();
            }
        });
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

        javax.swing.JPanel sourceConfPanel = new javax.swing.JPanel();
        javax.swing.JSplitPane sourceDescSplitPane = new javax.swing.JSplitPane();
        javax.swing.JScrollPane srcTreeScrollPane = new javax.swing.JScrollPane();
        descScrollPane = new javax.swing.JScrollPane();
        descTextArea = new javax.swing.JTextArea();
        javax.swing.JPanel defaultPanel = new javax.swing.JPanel();
        javax.swing.JButton loadDefaultButton = new javax.swing.JButton();
        javax.swing.JButton saveAsDefaultButton = new javax.swing.JButton();
        javax.swing.JPanel closePanel = new javax.swing.JPanel();
        javax.swing.JButton closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ID Mapping Source Configuration");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        sourceConfPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("ID Mapping Resources Configuration"));
        sourceConfPanel.setPreferredSize(new java.awt.Dimension(833, 542));
        sourceConfPanel.setLayout(new java.awt.GridBagLayout());

        sourceDescSplitPane.setPreferredSize(new java.awt.Dimension(800, 150));

        srcTreeScrollPane.setPreferredSize(new java.awt.Dimension(300, 500));

        srcTree = new IDMappingSourceSelectionTree(this);
        srcTreeScrollPane.setViewportView(srcTree);

        sourceDescSplitPane.setLeftComponent(srcTreeScrollPane);

        descScrollPane.setPreferredSize(new java.awt.Dimension(500, 500));

        descTextArea.setEditable(false);
        descTextArea.setLineWrap(true);
        descTextArea.setTabSize(4);
        descTextArea.setText(msg);
        descTextArea.setWrapStyleWord(true);
        descScrollPane.setViewportView(descTextArea);

        sourceDescSplitPane.setRightComponent(descScrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        sourceConfPanel.add(sourceDescSplitPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(sourceConfPanel, gridBagConstraints);

        loadDefaultButton.setText("Load default resources");
        loadDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadDefaultButtonActionPerformed(evt);
            }
        });
        defaultPanel.add(loadDefaultButton);

        saveAsDefaultButton.setText("Save current resources as default");
        saveAsDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsDefaultButtonActionPerformed(evt);
            }
        });
        defaultPanel.add(saveAsDefaultButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(defaultPanel, gridBagConstraints);

        closePanel.setLayout(new javax.swing.BoxLayout(closePanel, javax.swing.BoxLayout.LINE_AXIS));

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        closePanel.add(closeButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(closePanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
        this.dispose();
}//GEN-LAST:event_closeButtonActionPerformed

    private void saveAsDefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsDefaultButtonActionPerformed
        int ret = JOptionPane.showConfirmDialog(this, "Are you sure to replace the default resources with" +
                " the current ones?", null, JOptionPane.YES_NO_OPTION);
        if (ret==JOptionPane.NO_OPTION)
            return;

        try {
            IDMapperClientManager.saveCurrentToCytoscapeGlobalProperties();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save the current resources.");
        }

        JOptionPane.showMessageDialog(this, "The current resources has been save as default.");
    }//GEN-LAST:event_saveAsDefaultButtonActionPerformed

    private void loadDefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadDefaultButtonActionPerformed
        int ret = JOptionPane.showConfirmDialog(this, "Are you sure to load the default resources?\n" +
                "The current resources will be replaced with the default ones.", null, JOptionPane.YES_NO_OPTION);
        if (ret==JOptionPane.NO_OPTION)
            return;
        
        IDMapperClientManager.reloadFromCytoscapeGlobalProperties();

        srcTree.reset();
        JOptionPane.showMessageDialog(this, "The default resources has been loaded.");
    }//GEN-LAST:event_loadDefaultButtonActionPerformed

    public boolean isModified() {
        return srcTree.isModified();
    }

    private static final String msg = "Click on a 2nd level tree node to " +
            "add an ID mapping sources.\n\nClick on a tree node of a ID " +
            "mapping source for information about it. \n\nRight click on a ID " +
            "mapping source to delete it.\n\nClick the checkboxes to " +
            "select/unselect ID mapping sources to use.";
    private IDMappingSourceSelectionTree srcTree;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane descScrollPane;
    private javax.swing.JTextArea descTextArea;
    // End of variables declaration//GEN-END:variables

    private class LoadClientDescTask implements Task {
        private TaskMonitor taskMonitor;
        private boolean success = false;

        private final IDMapperClient client;
        private String description;

        public LoadClientDescTask(final IDMapperClient client) {
            this.client = client;
            description = null;
        }

        public void run() {
                try {
                        taskMonitor.setStatus("Connecting to "+client.getDisplayName()+"...");
                        taskMonitor.setPercentCompleted(-1);
                        description = getDescription();
                        taskMonitor.setStatus("Done");
                        taskMonitor.setPercentCompleted(100);
                        success = true;
                } catch (Exception e) {
                        taskMonitor.setPercentCompleted(100);
                        taskMonitor.setStatus("failed.\n");
                        e.printStackTrace();
                }

	}

        public boolean success() {
            return success;
        }

        public void halt() {
	}

        public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
		this.taskMonitor = taskMonitor;
	}

        public String getTitle() {
		return new String("Connecting to client");
	}

        public String description() {
            return description;
        }

        private String getDescription() throws IDMapperException {
            IDMapper idMapper = client.getIDMapper();
            if (idMapper==null) {
                return "This ID mapping client cannot be connected.";
            }

            StringBuilder desc = new StringBuilder(client.getDisplayName());
            desc.append("\nCapacities:\n");

            desc.append(">> Supported source ID types:\n");
            IDMapperCapabilities capabilities = idMapper.getCapabilities();

            Set<DataSource> dss = capabilities.getSupportedSrcDataSources();

            if (dss!=null) {
                ArrayList<String> vec = new ArrayList(dss.size());
                for (DataSource ds : dss) {
                    vec.add(getDescription(ds));
                }

                Collections.sort(vec);
                for (String str : vec) {
                    desc.append("\t"+str+"\n");
                }
            }

            desc.append(">> Supported target ID types:\n");
            dss = capabilities.getSupportedTgtDataSources();

            if (dss!=null) {
                ArrayList<String> vec = new ArrayList(dss.size());
                int i=0;
                for (DataSource ds : dss) {
                    i++;
                    vec.add(getDescription(ds));
                }

                Collections.sort(vec);
                for (String str : vec) {
                    desc.append("\t"+str+"\n");
                }
            }

    //        desc.append(">> Is free-text search supported?\n");
    //        desc.append(capabilities.isFreeSearchSupported()? "\tYes":"\tNo");
    //        desc.append("\n");

            // TODO: remove next line after the problem of AttributeMapper in BridgeRest is solved.
            if (!(idMapper instanceof org.bridgedb.webservice.bridgerest.BridgeRest))
            if (idMapper instanceof AttributeMapper) {
                desc.append(">>Supported Attributes\n");
                Set<String> attrs = ((AttributeMapper)idMapper).getAttributeSet();

                if (attrs!=null) {
                    ArrayList<String> vec = new ArrayList(attrs.size());
                    int i=0;
                    for (String attr : attrs) {
                        i++;
                        vec.add(attr);
                    }

                    Collections.sort(vec);
                    for (String str : vec) {
                        desc.append("\t"+str+"\n");
                    }
                }
            }

            return desc.toString();
        }

        private String getDescription(DataSource dataSource) {
            StringBuilder desc = new StringBuilder();
            if (dataSource==null) {
                System.err.print("wrong");
            }
            String sysName = dataSource.getSystemCode();
            if (sysName!=null) {
                desc.append(sysName);
            }
            desc.append("\t");

            String fullName = dataSource.getFullName();
            if (fullName!=null) {
                desc.append(fullName);
            }
            desc.append("\t");

            Xref example = dataSource.getExample();
            if (example!=null) {
                String id = example.getId();
                if (id!=null) {
                    desc.append(id);
                }
            }

            return desc.toString();
        }
    }
}
