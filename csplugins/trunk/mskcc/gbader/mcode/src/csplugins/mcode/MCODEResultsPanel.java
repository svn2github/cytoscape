package csplugins.mcode;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.actions.GinyUtils;
import cytoscape.task.TaskMonitor;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.NodeView;
import phoebe.PGraphView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * Reports the results of MCODE cluster finding. This class sets up the UI.
 */
public class MCODEResultsPanel extends JPanel {
    String componentTitle;
    protected JTable table;
    //JScrollPane scrollPane;
    protected MCODEResultsPanel.MCODEResultsTableModel model;
    //table size parameters
    protected final int graphPicSize = 80;
    protected final int defaultRowHeight = graphPicSize + 8;
    //protected final int preferredTableHeight = defaultRowHeight * 3;
    protected int preferredTableWidth = 0; // incremented below
    //User preference
    protected boolean openAsNewChild = false;
    //Actual cluster data
    //protected GraphPerspective[] gpClusterArray;    //The list of clusters, sorted by score when !null
    CyNetwork originalInputNetwork;                 //Keep a record of the original input record for use in the
    //table row selection listener
    CyNetworkView originalInputNetworkView;         //Keep a record of this too, if it exists
    HashMap hmNetworkNames;                         //Keep a record of network names we create from the table
    //algorithm object for access to the cluster scoring function
    MCODEAlgorithm alg;
    MCODECollapsablePanel explorePanel;
    JPanel[] exploreContent;
    int rank;
    MCODEParameterSet currentParamsCopy;

    //If imageList is present, will use those images for the cluster display
    public MCODEResultsPanel(MCODECluster[] clusters, CyNetwork network, Image[] imageList) {
        setLayout(new BorderLayout());

        currentParamsCopy = MCODECurrentParameters.getInstance().getParamsCopy();

        JPanel clusterBrowserPanel = createClusterBrowserPanel(clusters, network, imageList);

        explorePanel = new MCODECollapsablePanel("Explore");
        explorePanel.setCollapsed(false);
        explorePanel.setVisible(false);

        add(clusterBrowserPanel, BorderLayout.CENTER);
        add(explorePanel, BorderLayout.SOUTH);
    }

    /**
     *
     * @param clusters
     * @param network
     * @param imageList
     * @return panel
     */
    private JPanel createClusterBrowserPanel(MCODECluster[] clusters, CyNetwork network, Image imageList[]) {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Cluster Browser"));

        //network data (currently focused network)
        originalInputNetwork = network;
        alg = (MCODEAlgorithm) network.getClientData("MCODE_alg");
        //the view may not exist, but we only test for that when we need to (in the
        //TableRowSelectionHandler below)
        originalInputNetworkView = Cytoscape.getNetworkView(network.getIdentifier());
        hmNetworkNames = new HashMap();

        //main data table
        model = new MCODEResultsPanel.MCODEResultsTableModel(clusters, imageList);

        table = new JTable(model);
        table.setRowHeight(defaultRowHeight);
        initColumnSizes(table);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(StringBuffer.class, new MCODEResultsPanel.JTextAreaRenderer());

        //Ask to be notified of selection changes.
        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new MCODEResultsPanel.TableRowSelectionHandler(clusters, network));

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.getViewport().setBackground(Color.WHITE);


        /*
        JPanel bottomPanel = new JPanel();
        //new window preference checkbox
        JCheckBox newWindowCheckBox = new JCheckBox("Create a new child network.", false) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        newWindowCheckBox.addItemListener(new MCODEResultsPanel.newWindowCheckBoxAction());
        newWindowCheckBox.setToolTipText("If checked, will create a new child network of the selected cluster.\n" +
                "If not checked, will just select clusters in the main window.");
        //bottomPanel.add(newWindowCheckBox, BorderLayout.WEST);
        //the Save button
        JButton saveButton = new JButton("Export");
        saveButton.addActionListener(new MCODEResultsPanel.ExportAction(this, clusters, network));
        saveButton.setToolTipText("Save result summary to a file");
        bottomPanel.add(saveButton, BorderLayout.CENTER);
        //the OK button
        JButton okButton = new JButton("Done");
        okButton.addActionListener(new MCODEResultsPanel.OKAction(this));
        //bottomPanel.add(okButton, BorderLayout.EAST);
        //panel.add(bottomPanel, BorderLayout.SOUTH);
        */
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * This method creates a JPanel containing a node score cutoff slider and a node attribute enumeration viewer
     * @param clusters
     * @param selectedRow The cluster that is selected in the cluster browser
     * @return panel A JPanel with the contents of the explore panel, get's added to the explore collapsable panel's content pane
     */
    private JPanel createExploreContent(MCODECluster[] clusters, int selectedRow, CyNetwork inputNetwork) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel nodeScoreCutoffPanel = new JPanel(new BorderLayout());
        nodeScoreCutoffPanel.setBorder(BorderFactory.createTitledBorder("Node Score Cutoff"));

        //Create a slider to manipulate node score cutoff (goes to 1000 so that we get a more precise double variable out of it)
        JSlider nodeScoreCutoffSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, (int) (currentParamsCopy.getNodeScoreCutOff() * 1000)) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        //Turn on ticks and labels at major and minor intervals.
        nodeScoreCutoffSlider.setMajorTickSpacing(200);
        nodeScoreCutoffSlider.setMinorTickSpacing(50);
        nodeScoreCutoffSlider.setPaintTicks(true);
        nodeScoreCutoffSlider.setPaintLabels(true);
        //Set labels ranging from 0 to 100
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(0), new JLabel("0"));
        labelTable.put(new Integer(200), new JLabel("20"));
        labelTable.put(new Integer(400), new JLabel("40"));
        labelTable.put(new Integer(600), new JLabel("60"));
        labelTable.put(new Integer(800), new JLabel("80"));
        labelTable.put(new Integer(1000), new JLabel("100"));
        nodeScoreCutoffSlider.setLabelTable(labelTable);
        
        nodeScoreCutoffSlider.addChangeListener(new MCODEResultsPanel.nodeScoreCutoffAction(clusters, selectedRow, alg, inputNetwork));

        String nodeScoreCutoffTip = "Sets the node score cutoff for expanding a cluster as a percentage from the seed node score.\n" +
                "This is the most important parameter to control the size of MCODE clusters,\n" +
                "with smaller values creating smaller clusters.";
        nodeScoreCutoffSlider.setToolTipText(nodeScoreCutoffTip);

        nodeScoreCutoffPanel.add(nodeScoreCutoffSlider, BorderLayout.NORTH);

        JPanel nodeAttributesPanel = new JPanel(new BorderLayout());
        nodeAttributesPanel.setBorder(BorderFactory.createTitledBorder("Node Attributes"));

        String[] availableAttributes = {"Name","Biological Function","MCODE Score","Something else"};
        JComboBox nodeAttributesComboBox = new JComboBox(availableAttributes);

        nodeAttributesPanel.add(nodeAttributesComboBox, BorderLayout.NORTH);

        panel.add(nodeScoreCutoffPanel);
        panel.add(nodeAttributesPanel);

        return panel;
    }

    /**
     * Handles the data to be displayed in the table in this dialog box
     */
    private class MCODEResultsTableModel extends AbstractTableModel {

        //Create column headings
        String[] columnNames = {"Graph", "Details"};
        Object[][] data;    //the actual table data

        public MCODEResultsTableModel(MCODECluster[] clusters, Image imageList[]) {
            exploreContent = new JPanel[clusters.length];

            data = new Object[clusters.length][columnNames.length];
            for (int i = 0; i < clusters.length; i++) {
                StringBuffer details = new StringBuffer(getClusterDetails(i, clusters[i]));
                data[i][1] = new StringBuffer(details);
                //create an image for each cluster - make it a nice layout of the cluster
                Image image;
                if (imageList != null) {
                    image = imageList[i];
                } else {
                    image = MCODEUtil.convertNetworkToImage(clusters[i].getGPCluster(), graphPicSize, graphPicSize);
                }
                data[i][0] = new ImageIcon(image);
            }
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public void setValueAt(Object object, int row, int col) {
            data[row][col] = object;
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }

    private StringBuffer getClusterDetails(int i, MCODECluster cluster) {
        StringBuffer details = new StringBuffer();

        details.append("Rank:");
        details.append((new Integer(i + 1)).toString());
        details.append("\n");
        details.append("Score: ");
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        details.append(nf.format(cluster.getClusterScore()));
        details.append("\n");
        details.append("Nodes: ");
        details.append(cluster.getGPCluster().getNodeCount());
        details.append("\n");
        details.append("Edges: ");
        details.append(cluster.getGPCluster().getEdgeCount());

        return details;
    }

    /**
     * Utility method to initialize the column sizes of the table
     * @param table Table to initialize sizes for
     */
    private void initColumnSizes(JTable table) {
        table.getColumnModel().getColumn(0).sizeWidthToFit();
        table.getColumnModel().getColumn(1).setPreferredWidth(defaultRowHeight);
        preferredTableWidth = table.getColumnModel().getColumn(0).getPreferredWidth() + table.getColumnModel().getColumn(1).getPreferredWidth();
    }

    /**
     * Handles the OK press for this dialog (makes the dialog disappear)
     */
    private class OKAction extends AbstractAction {
        private JPanel dialog;

        OKAction(JPanel popup) {
            super("");
            this.dialog = popup;
        }

        public void actionPerformed(ActionEvent e) {
            originalInputNetwork.putClientData("MCODE_running", new Boolean(false));
            //dialog.dispose();
        }
    }

    /**
     * Handles the Export press for this panel (export results to a text file)
     */
    private class ExportAction extends AbstractAction {
        private JPanel popup;
        private MCODECluster[] clusters;
        private CyNetwork network;

        /**
         * Save action constructor
         *
         * @param popup     The parent dialog
         * @param clusters Clusters to save
         * @param network   Network clusters are from for information about cluster components
         */
        ExportAction(JPanel popup, MCODECluster[] clusters, CyNetwork network) {
            super("");
            this.popup = popup;
            this.clusters = clusters;
            this.network = network;
        }

        public void actionPerformed(ActionEvent e) {
            //call save method in MCODE
            // get the file name
            File file = FileUtil.getFile("Save Graph as Interactions",
                    FileUtil.SAVE, new CyFileFilter[]{});

            if (file != null) {
                String fileName = file.getAbsolutePath();
                MCODEUtil.saveMCODEResults(alg, clusters, network, fileName);
            }
        }
    }

    /**
     * Handles the new window parameter choice
     */
    private class newWindowCheckBoxAction implements ItemListener {
        public void itemStateChanged(ItemEvent e) {
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                openAsNewChild = false;
            } else {
                openAsNewChild = true;
            }
        }
    }

    /**
     * Handler to selects nodes in graph or create a new network when a row is selected
     * Note: There is some fairly detailed logic in here to deal with all the cases that a user can interact
     * with this dialog box.  Be careful when editing this code.
     * TODO: selecting an already selected cell should still select the appropriate cluster nodes
     * TODO: ie: since that seems impossible, unslecting the cluster in the view should automatically unselect the table row
     */
    private class TableRowSelectionHandler implements ListSelectionListener {
        MCODECluster[] clusters;
        CyNetwork inputNetwork;
        TableRowSelectionHandler(MCODECluster[] clusters, CyNetwork inputNetwork) {
            this.clusters = clusters;
            this.inputNetwork = inputNetwork;
        }
        public void valueChanged(ListSelectionEvent e) {
            //Ignore extra messages.
            if (e.getValueIsAdjusting()) return;
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            final GraphPerspective gpCluster;
            //NodeView nv;
            if (!lsm.isSelectionEmpty()) {
                final int selectedRow = lsm.getMinSelectionIndex();
                gpCluster = clusters[selectedRow].getGPCluster();
                selectCluster(gpCluster);

                //Upon selection of a cluster, we must show the corresponding explore panel content
                //First we test if this cluster has been selected yet and if its content exists
                //If it does not, we create it
                if (exploreContent[selectedRow] == null) {
                    exploreContent[selectedRow] = createExploreContent(clusters, selectedRow, inputNetwork);
                }
                //Next, if this is the first time explore panel content is being displayed, then the
                //explore panel is not visible yet, and there is no content in it yet, so we do not
                //have to remove it, otherwise, if the panel is visible, then it must have content
                //which needs to be removed
                if (explorePanel.isVisible()) {
                    explorePanel.getContentPane().remove(0);
                }
                //Now we add the currently selected cluster's explore panel content
                explorePanel.getContentPane().add(exploreContent[selectedRow], BorderLayout.CENTER);
                //and set the explore panel to visible so that it can be seen (this only happens once
                //after the first time the user selects a cluster
                if (!explorePanel.isVisible()){
                    explorePanel.setVisible(true);
                }
                //Finally the explore panel must be redrawn upon the selection event to display the
                //new content
                explorePanel.setTitleComponentText("Explore: Cluster " + (selectedRow + 1));
                explorePanel.updateUI();

                if (openAsNewChild) {
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(3);
                    final String title = "Cluster " + (selectedRow + 1) + " Score: " +
                            nf.format(clusters[selectedRow].getClusterScore());
                    //check if a network has already been created
                    String id = (String) hmNetworkNames.get(new Integer(selectedRow + 1));                                                                      
                    if (id != null) {
                        //just switch focus to the already created network
                        Cytoscape.getDesktop().setFocus(id);
                    } else {
                        //create the child network and view
                        final SwingWorker worker = new SwingWorker() {
                            public Object construct() {
                                CyNetwork newNetwork = Cytoscape.createNetwork(gpCluster.getNodeIndicesArray(),
                                        gpCluster.getEdgeIndicesArray(), title, originalInputNetwork);
                                hmNetworkNames.put(new Integer(selectedRow + 1), newNetwork.getIdentifier());
                                PGraphView view = (PGraphView) Cytoscape.createNetworkView(newNetwork);
                                //layout new cluster and fit it to window
                                //randomize node positions before layout so that they don't all layout in a line
                                //(so they don't fall into a local minimum for the SpringEmbedder)
                                //If the SpringEmbedder implementation changes, this code may need to be removed
                                NodeView nv;
                                for (Iterator in = view.getNodeViewsIterator(); in.hasNext();) {
                                    nv = (NodeView) in.next();
                                    nv.setXPosition(view.getCanvas().getLayer().getGlobalFullBounds().getWidth() * Math.random());
                                    //height is small for many default drawn graphs, thus +100
                                    nv.setYPosition((view.getCanvas().getLayer().getGlobalFullBounds().getHeight() + 100) * Math.random());
                                }
                                SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter(view);
                                lay.doLayout();
                                view.fitContent();
                                return null;
                            }
                        };
                        worker.start();
                    }
                }
            }
        }
    }

    private void selectCluster(GraphPerspective gpCluster) {
        NodeView nv;
        //only do this if a view has been created on this network
        if (originalInputNetworkView != null) {
            //start with no selected nodes
            GinyUtils.deselectAllNodes(originalInputNetworkView);
            //go through graph and select nodes in the cluster
            java.util.List nodeList = gpCluster.nodesList();
            for (int i = 0; i < nodeList.size(); i++) {
                Node n = (Node) nodeList.get(i);
                if (originalInputNetwork.containsNode(n)) {
                    nv = originalInputNetworkView.getNodeView(n);
                    nv.setSelected(true);
                }
            }
            if (!openAsNewChild) {
                //switch focus to the original network if not going to create a new network
                Cytoscape.getDesktop().setFocus(originalInputNetworkView.getIdentifier());
            }
        } else if (!openAsNewChild) {
            //Warn user that nothing will happen in this case because there is no view to select nodes with
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                    "You must have a network view created to select nodes.");
        }
    }

    /**
     * A text area renderer that creates a line wrapped, non-editable text area
     */
    private class JTextAreaRenderer extends JTextArea implements TableCellRenderer {

        /**
         * Constructor
         */
        public JTextAreaRenderer() {
            this.setLineWrap(true);
            this.setWrapStyleWord(true);
            this.setEditable(false);
            this.setFont(new Font(this.getFont().getFontName(), Font.PLAIN, 11));
            this.setMargin(new Insets (4,4,4,4));
        }

        /**
         * Used to render a table cell.  Handles selection color and cell heigh and width.
         * Note: Be careful changing this code as there could easily be infinite loops created
         * when calculating preferred cell size as the user changes the dialog box size.
         *
         * @param table      Parent table of cell
         * @param value      Value of cell
         * @param isSelected True if cell is selected
         * @param hasFocus   True if cell has focus
         * @param row        The row of this cell
         * @param column     The column of this cell
         * @return The cell to render by the calling code
         */
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            StringBuffer sb = (StringBuffer) value;
            this.setText(sb.toString());
            if (isSelected) {
                this.setBackground(table.getSelectionBackground());
                this.setForeground(table.getSelectionForeground());
            } else {
                this.setBackground(table.getBackground());
                this.setForeground(table.getForeground());
            }
            //row height calculations
            int currentRowHeight = table.getRowHeight(row);
            this.setSize(table.getColumnModel().getColumn(column).getWidth(), currentRowHeight);
            int textAreaPreferredHeight = (int) this.getPreferredSize().getHeight();
            //JTextArea can grow and shrink here
            if (currentRowHeight < textAreaPreferredHeight) {
                //grow row height
                table.setRowHeight(row, textAreaPreferredHeight);
            } else if ((currentRowHeight > textAreaPreferredHeight) && (currentRowHeight != defaultRowHeight)) {
                //defaultRowHeight check in if statement avoids infinite loop
                //shrink row height
                table.setRowHeight(row, defaultRowHeight);
            }
            return this;
        }
    }

    public String getComponentTitle() {
        return componentTitle;
    }

    public void setComponentTitle( String title) {
        componentTitle = title;
    }

    private class nodeScoreCutoffAction implements ChangeListener {
        private MCODEAlgorithm alg = null;
        private TaskMonitor taskMonitor = null;
        MCODECluster[] clusters;
        int selectedRow;
        private CyNetwork inputNetwork;

        nodeScoreCutoffAction(MCODECluster[] clusters, int selectedRow, MCODEAlgorithm alg, CyNetwork inputNetwork){
            this.alg = alg;
            this.selectedRow = selectedRow;
            this.clusters = clusters;
            this.inputNetwork = inputNetwork;
        }

        public void stateChanged(ChangeEvent e) {
            boolean drawn = false;
            JSlider source = (JSlider)e.getSource();
            double nodeScoreCutoff = (((double)source.getValue())/1000);

            MCODECluster cluster = alg.exploreCluster(clusters[selectedRow], nodeScoreCutoff, inputNetwork);
            clusters[selectedRow] = cluster;

            selectCluster(cluster.getGPCluster());

            if (clusters[selectedRow].getGPCluster().getNodeCount() < 20) {
                Image image;
                image = MCODEUtil.convertNetworkToImage(clusters[selectedRow].getGPCluster(), graphPicSize, graphPicSize);
                StringBuffer details = getClusterDetails(selectedRow, clusters[selectedRow]);
                table.setValueAt(new ImageIcon(image), selectedRow, 0);
                table.setValueAt(details, selectedRow, 1);
                table.updateUI();
                drawn = true;
            }

            if (!source.getValueIsAdjusting()) {
                //once the user setles on a node score cutoff, the cluster can be updated in the cluster array list
                //gpClusterArray.set(index, cluster);

                //taskMonitor.setPercentCompleted(0);
                //taskMonitor.setStatus("Drawing Results");
                
                if (drawn==false) {
                    Image image;
                    image = MCODEUtil.convertNetworkToImage(clusters[selectedRow].getGPCluster(), graphPicSize, graphPicSize);
                    StringBuffer details = getClusterDetails(selectedRow, clusters[selectedRow]);
                    table.setValueAt(new ImageIcon(image), selectedRow, 0);
                    table.setValueAt(details, selectedRow, 1);
                    updateUI();
                }
            }
        }
    }
}