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

import csplugins.id.mapping.ui.checktree.CheckTreeManager;
import csplugins.id.mapping.ui.checktree.CheckTreeSelectionModel;
import csplugins.id.mapping.ui.checktree.TreePathSelectable;
import csplugins.id.mapping.ui.checktree.SelectionChangeListener;
import csplugins.id.mapping.ui.checktree.SelectionChangeEvent;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.bridgedb.IDMapper;
import org.bridgedb.file.IDMapperFile;
import org.bridgedb.rdb.IDMapperRdb;
import org.bridgedb.webservice.IDMapperWebservice;

/**
 *
 * @author gjj
 */
class IDMappingSourceSelectionTree extends JTree {
    public enum ClientType {
        FILE,
        RDB,
        WEBSERVICE,
        OTHER;
    }

    private final CheckTreeManager checkTreeManager;
    private DefaultTreeModel tree_Model;
    private final CheckTreeSelectionModel selection_Model;
    private final JDialog parent;
    private boolean modified = false;

    private DefaultMutableTreeNode rootNode;
    private DefaultMutableTreeNode dbTreeNode;
    private DefaultMutableTreeNode wsTreeNode;
    private DefaultMutableTreeNode fileTreeNode;

    private final String root = "Sources of ID mapping";
    private final String db = "Databases";
    private final String ws = "Web Services";
    private final String file = "Local/remote Files";

    public IDMappingSourceSelectionTree(JDialog parent) {

        this.parent = parent;

        boolean dig = false;
        checkTreeManager = new CheckTreeManager(this, false,
                new TreePathSelectable() {
            public boolean isSelectable(TreePath path) {
                return path.getPathCount()>2;
//                if (path.getPathCount()>2) {
//                    return true;
//                } else if (path.getPathCount()==2) {
//                    return !((DefaultMutableTreeNode)path.getLastPathComponent()).isLeaf();
//                } else {
//                    return !(dbTreeNode.isLeaf() && wsTreeNode.isLeaf() && fileTreeNode.isLeaf());
//                }
            }
        });
        checkTreeManager.addSelectionChangeListener(new SelectionChangeListener() {
            public void selectionChanged(SelectionChangeEvent e) {
                Object source = e.getSource();
                if (!(source instanceof TreePath)) return;
                TreePath path = (TreePath)e.getSource();
                Object nodeObj = path.getLastPathComponent();
                if (nodeObj instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode clientNode = (DefaultMutableTreeNode)nodeObj;
                    Object clientObj = clientNode.getUserObject();
                    if (clientObj instanceof IDMapperClient) {
                        IDMapperClient client = (IDMapperClient)clientObj;
                        client.setSelected(selection_Model.isPathSelected(path, true));
                        modified = true;
                    } else {
                    //TODO if 2nd level can be selected
                    }
                }
            }
        });

        selection_Model = checkTreeManager.getSelectionModel();
        setupTree();
        setupMouse();
    }

    public boolean isModified() {
        return modified;
    }

//    public Set<IDMappingClient> getSelectedIDMapperClients() {
//        Set<IDMappingClient> ret = new HashSet<IDMappingClient>();
//
//        TreePath[] checkedPaths = selection_Model.getSelectionPaths();
//        if (checkedPaths==null) {
//            return ret;
//        }
//
//        for (TreePath path : checkedPaths) {
//            Object nodeObj = path.getLastPathComponent();
//            if (nodeObj instanceof DefaultMutableTreeNode) {
//                DefaultMutableTreeNode clientNode = (DefaultMutableTreeNode)nodeObj;
//                Object clientObj = clientNode.getUserObject();
//                if (clientObj instanceof IDMappingClient) {
//                    ret.add((IDMappingClient)clientObj);
//                } else if (!clientNode.isLeaf()) {
//                    DefaultMutableTreeNode leaf = clientNode.getFirstLeaf();
//                    DefaultMutableTreeNode lastLeaf = clientNode.getLastLeaf();
//                    while (true) {
//                        clientObj = leaf.getUserObject();
//                        if (clientObj instanceof IDMappingClient) {
//                            ret.add((IDMappingClient)clientObj);
//                        }
//
//                        if (leaf==lastLeaf) break;
//
//                        leaf = leaf.getNextLeaf();
//                    }
//                }
//            }
//        }
//
//        return ret;
//    }

    private void setupTree() {
        // set up tree
        rootNode = new DefaultMutableTreeNode(root);
        
        dbTreeNode = new DefaultMutableTreeNode(db);
        dbTreeNode.setAllowsChildren(true);
        rootNode.add(dbTreeNode);

        wsTreeNode = new DefaultMutableTreeNode(ws);
        wsTreeNode.setAllowsChildren(true);
        rootNode.add(wsTreeNode);

        fileTreeNode = new javax.swing.tree.DefaultMutableTreeNode(file);
        fileTreeNode.setAllowsChildren(true);
        rootNode.add(fileTreeNode);

        tree_Model = new DefaultTreeModel(rootNode);
        this.setModel(tree_Model);

        boolean expandFile = false;
        boolean expandWs = false;
        boolean expandDb = false;

        for (IDMapperClient client : IDMapperClientManager.allClients()) {
            DefaultMutableTreeNode clientNode = new DefaultMutableTreeNode(client);
            ClientType clientType = getClientType(client);
            if (clientType==ClientType.FILE) {
                //fileTreeNode.add(clientNode);
                insertAlphabetically(fileTreeNode, clientNode);
                if (client.isSelected()) {
                    expandFile = true;
                    //expandPath(new TreePath(new DefaultMutableTreeNode[]{rootNode,fileTreeNode}));
                    //set selected
                    TreePath treePath = new TreePath(new DefaultMutableTreeNode[]{rootNode,fileTreeNode,clientNode});
                    //if (!selection_Model.isPathSelected(treePath, true)) {
                        selection_Model.addSelectionPaths(new TreePath[] {treePath});
                    //}
                }
            } else if (clientType==ClientType.RDB) {
                //dbTreeNode.add(clientNode);
                insertAlphabetically(dbTreeNode, clientNode);
                if (client.isSelected()) {
                    expandDb = true;
                    //expandPath(new TreePath(new DefaultMutableTreeNode[]{rootNode,dbTreeNode}));
                    //set selected
                    TreePath treePath = new TreePath(new DefaultMutableTreeNode[]{rootNode,dbTreeNode,clientNode});
                    //if (!selection_Model.isPathSelected(treePath, true)) {
                        selection_Model.addSelectionPaths(new TreePath[] {treePath});
                    //}
                }
            } else if (clientType==ClientType.WEBSERVICE) {
                //wsTreeNode.add(clientNode);
                insertAlphabetically(wsTreeNode, clientNode);
                if (client.isSelected()) {
                    expandWs = true;
                    //expandPath(new TreePath(new DefaultMutableTreeNode[]{rootNode,wsTreeNode}));
                    //set selected
                    TreePath treePath = new TreePath(new DefaultMutableTreeNode[]{rootNode,wsTreeNode,clientNode});
                    //if (!selection_Model.isPathSelected(treePath, true)) {
                        selection_Model.addSelectionPaths(new TreePath[] {treePath});
                    //}
                }
            }
        }

        // expand selected nodes        
        if (expandFile) {
            TreePath treePath = new TreePath(new DefaultMutableTreeNode[]{rootNode,fileTreeNode});
            expandPath(treePath);
        }

        if (expandDb) {
            TreePath treePath = new TreePath(new DefaultMutableTreeNode[]{rootNode,dbTreeNode});
            expandPath(treePath);
        }
        
        if (expandWs) {
            TreePath treePath = new TreePath(new DefaultMutableTreeNode[]{rootNode,wsTreeNode});
            expandPath(treePath);
        }

    }

    private ClientType getClientType(IDMapperClient client) {
        IDMapper mapper = client.getIDMapper();
        if (mapper instanceof IDMapperWebservice) {
            return ClientType.WEBSERVICE;
        } else if (mapper instanceof IDMapperFile) {
            return ClientType.FILE;
        } else if(mapper instanceof IDMapperRdb) {
            return ClientType.RDB;
        } else {
            return ClientType.OTHER;
        }
    }

    private void setupMouse() {
        // popup menus
        final JPopupMenu rootPopup = new JPopupMenu();
        JMenuItem mi = new JMenuItem("Add an ID mapping source by connecting string...");
        mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSourceByString();
            }
        });
        rootPopup.add(mi);

        final JPopupMenu dbPopup = new JPopupMenu();
        mi = new JMenuItem("Add an ID mapping database...");
        mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addDatabase();
            }
        });
        dbPopup.add(mi);

        final JPopupMenu wsPopup = new JPopupMenu();
        mi = new JMenuItem("Add an ID mapping web service...");
        mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addWebservice();
            }
        });
        wsPopup.add(mi);

        final JPopupMenu filePopup = new JPopupMenu();
        mi = new JMenuItem("Add an ID mapping file...");
        mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addFile();
            }
        });
        filePopup.add(mi);

        final TreeNodePopupMenu dbClientPopup = new TreeNodePopupMenu();
        mi = new JMenuItem("Delete");
        mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeClient(dbClientPopup.getTreeNode());
            }
        });
        dbClientPopup.add(mi);

        final TreeNodePopupMenu wsClientPopup = new TreeNodePopupMenu();
        mi = new JMenuItem("Delete");
        mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeClient(wsClientPopup.getTreeNode());
            }
        });
        wsClientPopup.add(mi);

        mi = new JMenuItem("Configure");
        mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configWsClient(wsClientPopup.getTreeNode());
            }
        });
        wsClientPopup.add(mi);

        final TreeNodePopupMenu fileClientPopup = new TreeNodePopupMenu();
        mi = new JMenuItem("Delete");
        mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeClient(fileClientPopup.getTreeNode());
            }
        });
        fileClientPopup.add(mi);
        
        mi = new JMenuItem("Configure");
        mi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configTextClient(fileClientPopup.getTreeNode());
            }
        });
        fileClientPopup.add(mi);

        // hook the menus on the tree
        final JTree thisTree = this;
        this.addMouseListener( new MouseAdapter () {
            @Override
            public void mousePressed(MouseEvent e) {
                popup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!popup(e)) {
                    int row = thisTree.getRowForLocation(e.getX(), e.getY());
                    if(row == -1)
                        return;
                    thisTree.setSelectionRow(row);
                    TreePath path = thisTree.getPathForLocation(e.getX(), e.getY());

                    if (path.getPathCount()==2) {
                        if (path.getLastPathComponent()==dbTreeNode) {
                            addDatabase();
                        } else if(path.getLastPathComponent()==wsTreeNode) {
                            addWebservice();
                        } else if(path.getLastPathComponent()==fileTreeNode) {
                            addFile();
                        }
                    }
                }
            }

            private boolean popup(MouseEvent e) {
                if ( e.isPopupTrigger()) {
                    int row = thisTree.getRowForLocation(e.getX(), e.getY());
                    if(row == -1)
                        return true;
                    thisTree.setSelectionRow(row);
                    TreePath path = thisTree.getPathForLocation(e.getX(), e.getY());

                    switch (path.getPathCount()) {
                        case 2:
                            if (path.getLastPathComponent()==dbTreeNode) {
                                dbPopup.show((JComponent)e.getSource(), e.getX(), e.getY() );
                            } else if(path.getLastPathComponent()==wsTreeNode) {
                                wsPopup.show((JComponent)e.getSource(), e.getX(), e.getY() );
                            } else if(path.getLastPathComponent()==fileTreeNode) {
                                filePopup.show((JComponent)e.getSource(), e.getX(), e.getY() );
                            }
                            return true;
                        case 3:
                            if (path.getParentPath().getLastPathComponent()==dbTreeNode) {
                                dbClientPopup.setTreeNode((DefaultMutableTreeNode)path.getLastPathComponent());
                                dbClientPopup.show((JComponent)e.getSource(), e.getX(), e.getY() );
                            } else if(path.getParentPath().getLastPathComponent()==wsTreeNode) {
                                wsClientPopup.setTreeNode((DefaultMutableTreeNode)path.getLastPathComponent());
                                wsClientPopup.show((JComponent)e.getSource(), e.getX(), e.getY() );
                            } else if(path.getParentPath().getLastPathComponent()==fileTreeNode) {
                                fileClientPopup.setTreeNode((DefaultMutableTreeNode)path.getLastPathComponent());
                                fileClientPopup.show((JComponent)e.getSource(), e.getX(), e.getY() );
                            }
                            return true;
                        default:
                            return true;
                    }
                } else {
                    return false;
                }
            }
        });
    }

    private void addSourceByString() {
        
    }

    private void addDatabase() {
        RDBIDMappingClientConfigDialog dialog = new RDBIDMappingClientConfigDialog(parent, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        if (!dialog.isCancelled()) {
            IDMapperClient client = null;
            try {
                client = dialog.getIDMappingClient();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, "Failed to add database.");
                return;
            }

            if (client!=null) {
                if (!IDMapperClientManager.registerClient(client)) {
                    JOptionPane.showMessageDialog(parent, "This database has already been added as an ID mapping source.");
                    return;
                }

                DefaultMutableTreeNode clientNode = new DefaultMutableTreeNode(client);
                //dbTreeNode.add(clientNode);
                insertAlphabetically(dbTreeNode, clientNode);
                clientNode.setAllowsChildren(false);

                //expand path
                this.expandPath(new TreePath(new DefaultMutableTreeNode[]{rootNode,dbTreeNode}));

                //set selected
                TreePath path = new TreePath(new DefaultMutableTreeNode[]{rootNode,dbTreeNode,clientNode});
                if (!selection_Model.isPathSelected(path, true)) {
                    selection_Model.addSelectionPaths(new TreePath[] {path});
                }
                setSelectionPath(path);

                tree_Model.reload(dbTreeNode);
                
                modified = true;
            }
        }
    }

    private void addWebservice() {
        WebserviceIDMappingClientConfigDialog dialog = new WebserviceIDMappingClientConfigDialog(parent, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        if (!dialog.isCancelled()) {
            IDMapperClient client = null;
            try {
                client = dialog.getIDMappingClient();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, "Error: failed to add Biomart as source.");
            }

            if (client!=null) {
                if (!IDMapperClientManager.registerClient(client)) {
                    JOptionPane.showMessageDialog(parent, "This web service has already been added as an ID mapping source.");
                    return;
                }

                DefaultMutableTreeNode clientNode = new DefaultMutableTreeNode(client);

                //wsTreeNode.add(clientNode);
                insertAlphabetically(wsTreeNode, clientNode);
                clientNode.setAllowsChildren(false);

                //expand path
                this.expandPath(new TreePath(new DefaultMutableTreeNode[]{rootNode,wsTreeNode}));

                //set selected
                TreePath path = new TreePath(new DefaultMutableTreeNode[]{rootNode,wsTreeNode,clientNode});
                if (!selection_Model.isPathSelected(path, true)) {
                    selection_Model.addSelectionPaths(new TreePath[] {path});
                }
                setSelectionPath(path);

                tree_Model.reload(wsTreeNode);

                modified = true;
            }
        }
        
    }

    private void addFile() {
        FileIDMappingClientConfigDialog dialog = new FileIDMappingClientConfigDialog(parent, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        if (!dialog.isCancelled()) {
            IDMapperClient client = null;
            try {
                client = dialog.getIDMappingClient();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parent, "Error: failed to add this file as source.");
            }

            if (client!=null) {
                if (!IDMapperClientManager.registerClient(client)) {
                    JOptionPane.showMessageDialog(parent, "This file has already been added as an ID mapping source.");
                    return;
                }

                DefaultMutableTreeNode clientNode = new DefaultMutableTreeNode(client);

//                fileTreeNode.add(clientNode);
                insertAlphabetically(fileTreeNode, clientNode);
                clientNode.setAllowsChildren(false);
                
                //expand path
                this.expandPath(new TreePath(new DefaultMutableTreeNode[]{rootNode,fileTreeNode}));

                //set selected
                TreePath path = new TreePath(new DefaultMutableTreeNode[]{rootNode,fileTreeNode,clientNode});
                if (!selection_Model.isPathSelected(path, true)) {
                    selection_Model.addSelectionPaths(new TreePath[] {path});
                }
                setSelectionPath(path);
                
                tree_Model.reload(fileTreeNode);
                
                modified = true;
            }
        }
    }

    private void removeClient(final DefaultMutableTreeNode node) {
        if (node==null) return;
        IDMapperClient client = (IDMapperClient)node.getUserObject();
        IDMapperClientManager.removeClient(client.getId());

        TreeNode parentNode = node.getParent();
        node.removeFromParent();
        tree_Model.reload(parentNode);
        modified = true;
    }

    private void configTextClient(final DefaultMutableTreeNode node) {
        if (node==null) return;
        IDMapperClient client = (IDMapperClient)node.getUserObject();

        FileIDMappingClientConfigDialog dialog =
                new FileIDMappingClientConfigDialog(parent, true, client);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        if (!dialog.isCancelled()) {
            TreePath path = new TreePath(new DefaultMutableTreeNode[]{rootNode,fileTreeNode,node});
            setSelectionPath(path);
            modified = true;
        }
    }

    private void configWsClient(final DefaultMutableTreeNode node) {
        if (node==null) return;
        IDMapperClient client = (IDMapperClient)node.getUserObject();
        WebserviceIDMappingClientConfigDialog dialog =
                new WebserviceIDMappingClientConfigDialog(parent, true, client);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        if (!dialog.isCancelled()) {
            TreePath path = new TreePath(new DefaultMutableTreeNode[]{rootNode,fileTreeNode,node});
            setSelectionPath(path);
            modified = true;
        }
    }

    private void insertAlphabetically(DefaultMutableTreeNode parent,
            DefaultMutableTreeNode insertionNode) {
        String insertionValue = insertionNode.getUserObject().toString();
        int n = parent.getChildCount();
        int i=0;
        for (; i<n; i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent.getChildAt(i);
            if (child.getUserObject().toString().compareTo(insertionValue)>0) {
                break;
            }
        }

        parent.insert(insertionNode, i);
    }

    private class TreeNodePopupMenu extends JPopupMenu {
        private DefaultMutableTreeNode node;

        public void setTreeNode(final DefaultMutableTreeNode node) {
            this.node = node;
        }

        public DefaultMutableTreeNode getTreeNode() {
            return node;
        }
    }
    
}
