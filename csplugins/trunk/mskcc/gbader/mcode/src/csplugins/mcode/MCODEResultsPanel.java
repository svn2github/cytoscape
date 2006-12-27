package csplugins.mcode;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.actions.GinyUtils;
import cytoscape.data.CyAttributes;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import ding.view.DGraphView;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.NodeView;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;


/**
 * Reports the results of MCODE cluster finding. This class sets up the UI.
 */
public class MCODEResultsPanel extends JPanel {
    String resultsTitle;
    protected JTable table;
    protected MCODEResultsPanel.MCODEResultsBrowserTableModel modelBrowser;
    //table size parameters
    protected final int graphPicSize = 80;
    protected final int defaultRowHeight = graphPicSize + 8;
    protected int preferredTableWidth = 0; // incremented below
    //Actual cluster data
    CyNetwork originalInputNetwork;                 //Keep a record of the original input record for use in the
    //table row selection listener
    CyNetworkView originalInputNetworkView;         //Keep a record of this too, if it exists
    //HashMap hmNetworkNames;                         //Keep a record of network names we create from the table
    MCODECollapsablePanel explorePanel;
    JPanel[] exploreContent;
    MCODEParameterSet currentParamsCopy;

    GraphDrawer drawer;
    MCODELoader loader;

    //If imageList is present, will use those images for the cluster display
    /**
     *
     * @param clusters
     * @param network
     * @param imageList
     */
    public MCODEResultsPanel(MCODECluster[] clusters, MCODEAlgorithm alg, CyNetwork network, Image[] imageList, String resultsTitle) {
        setLayout(new BorderLayout());

        currentParamsCopy = MCODECurrentParameters.getInstance().getResultParams(resultsTitle);

        JPanel clusterBrowserPanel = createClusterBrowserPanel(clusters, network, alg, imageList);
        JPanel bottomPanel = createBottomPanel();

        add(clusterBrowserPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        drawer = new GraphDrawer();
        loader = new MCODELoader(table, graphPicSize, graphPicSize);
    }

    /**
     *
     * @param clusters array of MCODE clusters
     * @param network network
     * @param imageList images of cluster graphs
     * @return panel
     */
    private JPanel createClusterBrowserPanel(MCODECluster[] clusters, CyNetwork network, MCODEAlgorithm alg, Image imageList[]) {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Cluster Browser"));

        //network data (currently focused network)
        originalInputNetwork = network;
        
        //the view may not exist, but we only test for that when we need to (in the
        //TableRowSelectionHandler below)
        originalInputNetworkView = Cytoscape.getNetworkView(network.getIdentifier());
        //hmNetworkNames = new HashMap();

        //main data table
        modelBrowser = new MCODEResultsPanel.MCODEResultsBrowserTableModel(clusters, imageList);

        table = new JTable(modelBrowser);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(StringBuffer.class, new MCODEResultsPanel.JTextAreaRenderer(defaultRowHeight));
        table.setIntercellSpacing(new Dimension(0, 4));
        table.setFocusable(false);

        //Ask to be notified of selection changes.
        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new MCODEResultsPanel.TableRowSelectionHandler(clusters, network, alg));

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.getViewport().setBackground(Color.WHITE);

        /*
        //the Save button
        JButton saveButton = new JButton("Export");
        saveButton.addActionListener(new MCODEResultsPanel.ExportAction(this, clusters, network));
        saveButton.setToolTipText("Save result summary to a file");
        bottomPanel.add(saveButton, BorderLayout.CENTER);
        */
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     *
     * @return
     */
    private JPanel createBottomPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        explorePanel = new MCODECollapsablePanel("Explore");
        explorePanel.setCollapsed(false);
        explorePanel.setVisible(false);

        JPanel buttonPanel = new JPanel();

        JButton exportAllButton = new JButton("Export All");
        //TODO: DO THIS
        JButton closeButton = new JButton("Close Results");
        closeButton.addActionListener(new MCODEResultsPanel.CloseAction(this));

        buttonPanel.add(exportAllButton);
        buttonPanel.add(closeButton);

        panel.add(explorePanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * This method creates a JPanel containing a node score cutoff slider and a node attribute enumeration viewer
     *
     * @param clusters Array of all cluster objects
     * @param selectedRow The cluster that is selected in the cluster browser
     * @param inputNetwork Network
     * @return panel A JPanel with the contents of the explore panel, get's added to the explore collapsable panel's content pane
     */
    private JPanel createExploreContent(MCODECluster[] clusters, int selectedRow, CyNetwork inputNetwork, MCODEAlgorithm alg) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JPanel sizePanel = new JPanel(new BorderLayout());
        sizePanel.setBorder(BorderFactory.createTitledBorder("Size"));

        //Create a slider to manipulate node score cutoff (goes to 1000 so that we get a more precise double variable out of it)
        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, (int) (currentParamsCopy.getNodeScoreCutoff() * 1000)) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        //Turn on ticks and labels at major and minor intervals.
        sizeSlider.setMajorTickSpacing(200);
        sizeSlider.setMinorTickSpacing(50);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        //Set labels ranging from 0 to 100
        Hashtable labelTable = new Hashtable();
        labelTable.put(new Integer(0), new JLabel("Min"));
        //labelTable.put(new Integer(200), new JLabel("20"));
        //labelTable.put(new Integer(400), new JLabel("40"));
        //labelTable.put(new Integer(600), new JLabel("60"));
        //labelTable.put(new Integer(800), new JLabel("80"));
        labelTable.put(new Integer(1000), new JLabel("Max"));
        //Make a special label for the initial position
        labelTable.put(new Integer((int) (currentParamsCopy.getNodeScoreCutoff() * 1000)), new JLabel("^"));

        sizeSlider.setLabelTable(labelTable);
        sizeSlider.setFont(new Font("Arial", Font.PLAIN, 8));

        String sizeTip = "WRITE ME PLEASE!";//TODO: Write size slider tooltip
        sizeSlider.setToolTipText(sizeTip);

        sizePanel.add(sizeSlider, BorderLayout.NORTH);

        JPanel nodeAttributesPanel = new JPanel(new BorderLayout());
        nodeAttributesPanel.setBorder(BorderFactory.createTitledBorder("Node Attributes"));

        String[] availableAttributes = Cytoscape.getNodeAttributes().getAttributeNames();
        String[] attributesList = new String[availableAttributes.length+1];
        System.arraycopy(availableAttributes, 0, attributesList, 1, availableAttributes.length);
        attributesList[0] = "Please Select";
        JComboBox nodeAttributesComboBox = new JComboBox(attributesList);

        sizeSlider.addChangeListener(new MCODEResultsPanel.SizeAction(clusters, selectedRow, alg, inputNetwork, nodeAttributesComboBox));

        MCODEResultsPanel.MCODEResultsEnumeratorTableModel modelEnumerator;
        modelEnumerator = new MCODEResultsPanel.MCODEResultsEnumeratorTableModel(new HashMap());

        JTable enumerationsTable = new JTable(modelEnumerator);

        JScrollPane tableScrollPane = new JScrollPane(enumerationsTable);
        tableScrollPane.getViewport().setBackground(Color.WHITE);
        enumerationsTable.setPreferredScrollableViewportSize(new Dimension(100, graphPicSize));
        enumerationsTable.setGridColor(Color.LIGHT_GRAY);
        enumerationsTable.setFont(new Font(enumerationsTable.getFont().getFontName(), Font.PLAIN, 11));
        enumerationsTable.setDefaultRenderer(StringBuffer.class, new MCODEResultsPanel.JTextAreaRenderer(0));
        enumerationsTable.setFocusable(false);

        nodeAttributesComboBox.addActionListener(new MCODEResultsPanel.enumerateAction(enumerationsTable, modelEnumerator, clusters, selectedRow));

        nodeAttributesPanel.add(nodeAttributesComboBox, BorderLayout.NORTH);
        nodeAttributesPanel.add(tableScrollPane, BorderLayout.SOUTH);

        JPanel bottomExplorePanel = createBottomExplorePanel(clusters, selectedRow);

        panel.add(sizePanel);
        panel.add(nodeAttributesPanel);
        panel.add(bottomExplorePanel);

        return panel;
    }

    /**
     *
     * @param clusters
     * @param selectedRow
     * @return
     */
    private JPanel createBottomExplorePanel(MCODECluster[] clusters, int selectedRow) {
        JPanel panel = new JPanel();
        JButton createChildButton = new JButton("Create Child Network");
        createChildButton.addActionListener(new MCODEResultsPanel.CreateChildAction(this, clusters, selectedRow));
        panel.add(createChildButton);
        return panel;
    }

    /**
     * Handles the create child network press in the cluster exploration panel
     */
    private class CreateChildAction extends AbstractAction {
        int selectedRow;
        MCODECluster[] clusters;
        MCODEResultsPanel trigger;
        /**
         *
         * @param clusters Reference to all clusters in this result set
         * @param selectedRow The selected cluster
         */
        CreateChildAction (MCODEResultsPanel trigger, MCODECluster[] clusters, int selectedRow) {
            this.selectedRow = selectedRow;
            this.clusters = clusters;
            this.trigger = trigger;
        }

        public void actionPerformed(ActionEvent actionEvent) {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(3);
            final MCODECluster cluster = clusters[selectedRow];
            final GraphPerspective gpCluster = cluster.getGPCluster();
            final String title = trigger.getResultsTitle() + ": " + cluster.getClusterName() + " (Score: "+ nf.format(cluster.getClusterScore()) + ")";
            //check if a network has already been created
            //String id = (String) hmNetworkNames.get(new Integer(selectedRow + 1));
            //if (id != null) {
                //just switch focus to the already created network
            //    Cytoscape.getDesktop().setFocus(id);
            //} else {
                //create the child network and view
                final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        CyNetwork newNetwork = Cytoscape.createNetwork(gpCluster.getNodeIndicesArray(),
                                gpCluster.getEdgeIndicesArray(), title, originalInputNetwork);
                        //hmNetworkNames.put(new Integer(selectedRow + 1), newNetwork.getIdentifier());
                        DGraphView view = (DGraphView) Cytoscape.createNetworkView(newNetwork);
                        //layout new cluster and fit it to window
                        //randomize node positions before layout so that they don't all layout in a line
                        //(so they don't fall into a local minimum for the SpringEmbedder)
                        //If the SpringEmbedder implementation changes, this code may need to be removed
                        NodeView nv;
                        boolean layoutNecessary = false;
                        for (Iterator in = view.getNodeViewsIterator(); in.hasNext();) {
                            nv = (NodeView) in.next();
                            if (cluster.getPGView() != null && cluster.getPGView().getNodeView(nv.getNode().getRootGraphIndex()) != null) {
                                //If it does, then we take the layout position that was already generated for it
                                nv.setXPosition(cluster.getPGView().getNodeView(nv.getNode().getRootGraphIndex()).getXPosition());
                                nv.setYPosition(cluster.getPGView().getNodeView(nv.getNode().getRootGraphIndex()).getYPosition());
                            } else {
                                //this will likely never occur
                                //Otherwise, randomize node positions before layout so that they don't all layout in a line
                                //(so they don't fall into a local minimum for the SpringEmbedder)
                                //If the SpringEmbedder implementation changes, this code may need to be removed
                                nv.setXPosition(view.getCanvas().getWidth() * Math.random());
                                //height is small for many default drawn graphs, thus +100
                                nv.setYPosition((view.getCanvas().getHeight() + 100) * Math.random());
                                layoutNecessary = true;
                            }
                        }
                        if (layoutNecessary) {
                            SpringEmbeddedLayouter lay = new SpringEmbeddedLayouter(view);
                            lay.doLayout(0, 0, 0, null);
                        }
                        view.fitContent();
                        return null;
                    }
                };
                worker.start();
            //}

        }
    }

    /**
     * Handles the data to be displayed in the cluster browser table
     */
    private class MCODEResultsBrowserTableModel extends AbstractTableModel {

        //Create column headings
        String[] columnNames = {"Graph", "Details"};
        Object[][] data;    //the actual table data

        public MCODEResultsBrowserTableModel(MCODECluster[] clusters, Image imageList[]) {
            exploreContent = new JPanel[clusters.length];

            data = new Object[clusters.length][columnNames.length];
            for (int i = 0; i < clusters.length; i++) {
                clusters[i].setRank(i);
                StringBuffer details = new StringBuffer(getClusterDetails(clusters[i]));
                data[i][1] = new StringBuffer(details);
                //create an image for each cluster - make it a nice layout of the cluster
                Image image;
                if (imageList != null) {
                    image = imageList[i];
                } else {
                    image = MCODEUtil.convertNetworkToImage(null, clusters[i], graphPicSize, graphPicSize, null, true);
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
            fireTableCellUpdated(row, col);
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }

    /**
     * Generates a string buffer with the cluster's details
     * 
     * @param cluster The cluster
     * @return details String buffer containing the details
     */
    private StringBuffer getClusterDetails(MCODECluster cluster) {
        StringBuffer details = new StringBuffer();

        details.append("Rank: ");
        details.append((new Integer(cluster.getRank() + 1)).toString());

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
     * Handles the data to be displayed in the node attribute enumeration table
     */
    private class MCODEResultsEnumeratorTableModel extends AbstractTableModel {

        //Create column headings
        String[] columnNames = {"Value", "Occurance"};
        Object[][] data = new Object[0][columnNames.length];    //the actual table data

        public MCODEResultsEnumeratorTableModel(HashMap enumerations) {
            listIt(enumerations);
        }

        public void listIt(HashMap enumerations) {
            //first we sort the hash map of attributes values and their occurances
            ArrayList enumerationsSorted = sortMap(enumerations);
            //then we put it into the data array in reverse order so that the most
            //frequent attribute value is on top
            Object[][] newData = new Object[enumerationsSorted.size()][columnNames.length];
            int c = enumerationsSorted.size()-1;
            for (Iterator i = enumerationsSorted.iterator(); i.hasNext();) {
                Map.Entry mp = (Map.Entry) i.next();
                newData[c][0] = new StringBuffer(mp.getKey().toString());
                newData[c][1] = new String(mp.getValue().toString());
                c--;
            }
            //finally we redraw the table, however, in order to prevent constant flickering
            //we only fire the data change if the number or rows is altered.  That way,
            //when the number of rows stays the same, which is most of the time, there is no
            //flicker.
            if (getRowCount() == newData.length) {
                data = new Object[newData.length][columnNames.length];
                System.arraycopy(newData, 0, data, 0, data.length);
                fireTableRowsUpdated(0, getRowCount());
            } else {
                data = new Object[newData.length][columnNames.length];
                System.arraycopy(newData, 0, data, 0, data.length);
                fireTableDataChanged();
            }
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public int getRowCount() {
            return data.length;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public void setValueAt(Object object, int row, int col) {
            data[row][col] = object;
            fireTableCellUpdated(row, col);
        }
        
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

    /**
    * This method uses Arrays.sort for sorting a Map by the entries' values
    *
    * @param map Has values mapped to keys
    * @return outputList of Map.Entries
    */
    public ArrayList sortMap(Map map) {
        ArrayList outputList = null;
        int count = 0;
        Set set = null;
        Map.Entry[] entries = null;

        set = (Set) map.entrySet();
        Iterator iterator = set.iterator();
        entries = new Map.Entry[set.size()];
        while(iterator.hasNext()) {
            entries[count++] = (Map.Entry) iterator.next();
        }

        // Sort the entries with own comparator for the values:
        Arrays.sort(entries, new Comparator() {
            public int compareTo(Object o1, Object o2) {
                Map.Entry le = (Map.Entry)o1;
                Map.Entry re = (Map.Entry)o2;
                return ((Comparable)le.getValue()).compareTo((Comparable)re.getValue());
            }

            public int compare(Object o1, Object o2) {
                Map.Entry le = (Map.Entry)o1;
                Map.Entry re = (Map.Entry)o2;
                return ((Comparable)le.getValue()).compareTo((Comparable)re.getValue());
            }
        });
        outputList = new ArrayList();
        for(int i = 0; i < entries.length; i++) {
            outputList.add(entries[i]);
        }
        return outputList;
    }

    /**
     * Handles the selection of all available node attributes for the enumeration within the cluster
     */
    private class enumerateAction extends AbstractAction {
        JTable enumerationsTable;
        MCODECluster[] clusters;
        int selectedRow;
        MCODEResultsPanel.MCODEResultsEnumeratorTableModel modelEnumerator;

        enumerateAction(JTable enumerationsTable, MCODEResultsPanel.MCODEResultsEnumeratorTableModel modelEnumerator, MCODECluster[] clusters, int selectedRow) {
            this.clusters = clusters;
            this.selectedRow = selectedRow;
            this.enumerationsTable = enumerationsTable;
            this.modelEnumerator = modelEnumerator;
        }

        public void actionPerformed(ActionEvent e) {
            HashMap attributeEnumerations = new HashMap(); //the key is the attribute value and the value is the number of times that value appears in the cluster
            //First we want to see which attribute was selected in the combo box
            String attributeName = (String) ((JComboBox) e.getSource()).getSelectedItem();
            //If its the generic 'please select' option then we don't do any enumeration
            if (!attributeName.equals("Please Select")) {
                //otherwise, we want to get the selected attribute's value for each node in the selected cluster
                for (Iterator i = clusters[selectedRow].getGPCluster().nodesIterator(); i.hasNext();) {
                    Node node = (Node) i.next();
                    //The attribute value will be stored as a string no matter what it is but we need an array list
                    //because some attributes are maps or lists of any size
                    ArrayList attributeValue = new ArrayList();
                    //Every type of attribute has its own get method so we have to see which one to use
                    //When we find the type, we get its value(s)
                    if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_STRING) {
                        attributeValue.add(Cytoscape.getNodeAttributes().getStringAttribute(node.getIdentifier(), attributeName));
                    } else if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_FLOATING) {
                        attributeValue.add(Cytoscape.getNodeAttributes().getDoubleAttribute(node.getIdentifier(), attributeName));
                    } else if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_INTEGER) {
                        attributeValue.add(Cytoscape.getNodeAttributes().getIntegerAttribute(node.getIdentifier(), attributeName));
                    } else if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_BOOLEAN) {
                        attributeValue.add(Cytoscape.getNodeAttributes().getBooleanAttribute(node.getIdentifier(), attributeName));
                    } else if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST) {
                        List valueList = Cytoscape.getNodeAttributes().getAttributeList(node.getIdentifier(), attributeName);
                        for (Iterator vli = valueList.iterator(); vli.hasNext();) {
                            attributeValue.add(vli.next());
                        }
                    } else if (Cytoscape.getNodeAttributes().getType(attributeName) == CyAttributes.TYPE_SIMPLE_MAP) {
                        Map valueMap = Cytoscape.getNodeAttributes().getAttributeMap(node.getIdentifier(), attributeName);
                        for (Iterator vmki = valueMap.keySet().iterator(); vmki.hasNext();) {
                            String key = (String) vmki.next();
                            Object value = valueMap.get(key);
                            attributeValue.add(new String(key + " -> " + value));
                        }
                    }
                    //Next we must make a non-repeating list with the attribute values and enumerate the repetitions
                    for (Iterator avi = attributeValue.iterator(); avi.hasNext();) {
                        Object aviElement = avi.next();
                        if (aviElement != null) {
                            String value = aviElement.toString();

                            if (!attributeEnumerations.containsKey(value)) {
                                //If the attribute value appears for the first time, we give it an enumeration of 1 and add it to the enumerations
                                attributeEnumerations.put(value, new Integer(1));
                            } else {
                                //If it already appeared before, we want to add to the enumeration of the value
                                Integer enumeration = (Integer) attributeEnumerations.get(value);
                                enumeration = new Integer(enumeration.intValue()+1);
                                attributeEnumerations.put(value, enumeration);
                            }
                        }
                    }
                }
            }
            modelEnumerator.listIt(attributeEnumerations);
        }
    }

    /**
     * Handles the close press for this results panel
     */
    private class CloseAction extends AbstractAction {
        MCODEResultsPanel trigger;

        CloseAction(MCODEResultsPanel trigger) {
            this.trigger = trigger;
        }

        public void actionPerformed(ActionEvent e) {
            CytoscapeDesktop desktop = Cytoscape.getDesktop();
            CytoPanel cytoPanel = desktop.getCytoPanel(SwingConstants.EAST);

            String message = "You are about to dispose of " + resultsTitle + ".\nDo you wish to continue?";
            int result = JOptionPane.showOptionDialog(Cytoscape.getDesktop(), new Object[]{message}, "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (result == JOptionPane.YES_OPTION) {
                cytoPanel.remove(trigger);
                MCODECurrentParameters.removeResultParams(getResultsTitle());
            }
            
            if (cytoPanel.getCytoPanelComponentCount() == 0) {
                cytoPanel.setState(CytoPanelState.HIDE);
            }
        }
    }

    /**
     * Handles the Export press for this panel (export results to a text file)
     * TODO: make this work
     */
    private class ExportAction extends AbstractAction {
        private JPanel popup;
        private MCODECluster[] clusters;
        private CyNetwork network;
        private MCODEAlgorithm alg;

        /**
         * Export action constructor
         *
         * @param popup     The parent dialog
         * @param clusters Clusters to save
         * @param network   Network clusters are from for information about cluster components
         */
        ExportAction(JPanel popup, MCODECluster[] clusters, CyNetwork network, MCODEAlgorithm alg) {
            super("");
            this.popup = popup;
            this.clusters = clusters;
            this.network = network;
            this.alg = alg;
        }

        public void actionPerformed(ActionEvent e) {
            //call save method in MCODE
            //get the file name
            File file = FileUtil.getFile("Save Graph as Interactions",
                    FileUtil.SAVE, new CyFileFilter[]{});

            if (file != null) {
                String fileName = file.getAbsolutePath();
                MCODEUtil.saveMCODEResults(alg, clusters, network, fileName);
            }
        }
    }

    /**
     * Handler to selects nodes in graph or create a new network when a row is selected
     * Note: There is some fairly detailed logic in here to deal with all the cases that a user can interact
     * with this dialog box.  Be careful when editing this code.
     */
    private class TableRowSelectionHandler implements ListSelectionListener {
        MCODECluster[] clusters;
        CyNetwork inputNetwork;
        MCODEAlgorithm alg;

        /**
         *
         * @param clusters
         * @param inputNetwork
         */
        TableRowSelectionHandler(MCODECluster[] clusters, CyNetwork inputNetwork, MCODEAlgorithm alg) {
            this.clusters = clusters;
            this.inputNetwork = inputNetwork;
            this.alg = alg;
            inputNetwork.addSelectEventListener(new MCODEResultsPanel.networkSelectionAction(clusters));
        }

        public void valueChanged(ListSelectionEvent e) {
            //Ignore extra messages.
            if (e.getValueIsAdjusting()) return;
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            final GraphPerspective gpCluster;

            if (!lsm.isSelectionEmpty()) {
                final int selectedRow = lsm.getMinSelectionIndex();
                gpCluster = clusters[selectedRow].getGPCluster();
                selectCluster(gpCluster);

                //Upon selection of a cluster, we must show the corresponding explore panel content
                //First we test if this cluster has been selected yet and if its content exists
                //If it does not, we create it
                if (exploreContent[selectedRow] == null) {
                    exploreContent[selectedRow] = createExploreContent(clusters, selectedRow, inputNetwork, alg);
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
                //new content with the name of the cluster, if it exists
                String title = "Explore: ";
                if (clusters[selectedRow].getClusterName() != null) {
                    title = title + clusters[selectedRow].getClusterName();
                } else {
                    title = title + "Cluster " + (selectedRow + 1);
                }
                explorePanel.setTitleComponentText(title);
                explorePanel.updateUI();
            }
        }
    }

    /**
     *
     * @param gpCluster
     */
    private void selectCluster(GraphPerspective gpCluster) {
        NodeView nv;
        //only do this if a view has been created on this network
        if (originalInputNetworkView != null) {
            //start with no selected nodes
            GinyUtils.deselectAllNodes(originalInputNetworkView);
            originalInputNetwork.setSelectedNodeState(gpCluster.nodesList(), true);

            Cytoscape.getDesktop().setFocus(originalInputNetworkView.getIdentifier());
        } else {
            //Warn user that nothing will happen in this case because there is no view to select nodes with
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "You must have a network view created to select nodes.");
        }
    }

    /**
     * A text area renderer that creates a line wrapped, non-editable text area
     */
    private class JTextAreaRenderer extends JTextArea implements TableCellRenderer {
        int minHeight;

        /**
         * Constructor
         *
         * @param minHeight The minimum height of the row, either the size of the graph picture or zero
         */
        public JTextAreaRenderer(int minHeight) {
            this.setLineWrap(true);
            this.setWrapStyleWord(true);
            this.setEditable(false);
            this.setFont(new Font(this.getFont().getFontName(), Font.PLAIN, 11));
            this.minHeight = minHeight;
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
            int rowMargin = table.getRowMargin();
            this.setSize(table.getColumnModel().getColumn(column).getWidth(), currentRowHeight - (2 * rowMargin));
            int textAreaPreferredHeight = (int) this.getPreferredSize().getHeight();
            //JTextArea can grow and shrink here
            if (currentRowHeight != Math.max(textAreaPreferredHeight + (2 * rowMargin) , minHeight + (2 * rowMargin))) {
                table.setRowHeight(row, Math.max(textAreaPreferredHeight + (2 * rowMargin), minHeight + (2 * rowMargin)));
            }
            return this;
        }
    }

    /**
     *
     * @return
     */
    public String getResultsTitle() {
        return resultsTitle;
    }

    /**
     *
     * @param title
     */
    public void setResultsTitle(String title) {
        resultsTitle = title;
    }

    /**
     *
     */
    private class networkSelectionAction implements SelectEventListener {
        MCODECluster[] clusters;

        networkSelectionAction(MCODECluster[] clusters) {
            this.clusters = clusters;
        }

        public void onSelectEvent(SelectEvent event) {
            //TODO: check if any clusters match the selection and select them if they do and are not selected already

            //table.clearSelection();
            //if (table.getSelectedRowCount() > 0) {
            ArrayList alSelection = new ArrayList();
            for (Iterator in = event.getSource().getSelectedNodes().iterator(); in.hasNext();) {
                CyNode n = (CyNode) in.next();
                alSelection.add(new Integer (n.getRootGraphIndex()));
            }
            for (int c = 0; c < clusters.length; c++) {
                ArrayList alCluster = clusters[c].getALCluster();

                if (alCluster.containsAll(alSelection) && alSelection.containsAll(alCluster)) {
                    //System.out.println("Should select " + clusters[c].getClusterName());
                    if (table.getSelectedRow() != c) {
                        //table.setRowSelectionInterval(c, c);
                        //TODO: there must be some discriminator between user and computer actions!
                    }
                    break;
                } else if (alSelection.size() > 0 && alCluster.containsAll(alSelection)) {
                    //System.out.println("belongs to " + clusters[c].getClusterName());
                    //TODO: how do you tell the user that the node the selected belongs to a cluster?
                    //TODO: maybe set node attributes so the user can see it in the attribute browser
                }
            }
        }
    }

    /**
     * Handles the dynamic cluster size manipulation via the JSlider
     */
    private class SizeAction implements ChangeListener {
        private MCODEAlgorithm alg = null;
        private MCODECluster[] clusters;
        private int selectedRow;
        private CyNetwork inputNetwork;
        public boolean loaderSet = false;
        private JComboBox nodeAttributesComboBox;
        private SpringEmbeddedLayouter layouter;
        private GraphDrawer drawer;

        /**
         * Constructor
         *
         * @param clusters Reference to cluster result set
         * @param selectedRow The selected cluster
         * @param alg Reference to the algorithm
         * @param inputNetwork Reference to the focused network
         * @param nodeAttributesComboBox Reference to the attribute enumeration picker
         */
        SizeAction(MCODECluster[] clusters, int selectedRow, MCODEAlgorithm alg, CyNetwork inputNetwork, JComboBox nodeAttributesComboBox){
            this.alg = alg;
            this.selectedRow = selectedRow;
            this.clusters = clusters;
            this.inputNetwork = inputNetwork;
            this.nodeAttributesComboBox = nodeAttributesComboBox;
            layouter = new SpringEmbeddedLayouter();
            drawer = new GraphDrawer();
            loaderSet = false;
        }

        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider)e.getSource();
            double nodeScoreCutoff = (((double)source.getValue())/1000);
            
            //Store current cluster content for comparison
            ArrayList oldCluster = clusters[selectedRow].getALCluster();

            //Find the new cluster given the node score cutoff
            MCODECluster cluster = alg.exploreCluster(clusters[selectedRow], nodeScoreCutoff, inputNetwork, resultsTitle);

            //We only want to do the following work if the newly found cluster is actually different
            //So we get the new cluster content
            ArrayList newCluster = cluster.getALCluster();

            //And compare the old and new
            if (!newCluster.equals(oldCluster)) {
                //There is a small difference between expanding and retracting the cluster size
                //When expanding, new nodes need random position and thus must go through the layout
                //When retracting, we simply use the layout that was generated and stored
                //This speeds up the drawing process greatly
                boolean layoutNecessary = false;
                if (newCluster.size() - oldCluster.size() > 0) {
                    layoutNecessary = true;
                }
                //If the cluster has changed, then we:
                //Interrupt the drawing
                layouter.interruptDoLayout();
                MCODEUtil.interruptLoading();
                //Update the cluster array
                clusters[selectedRow] = cluster;
                //Select the new cluster
                selectCluster(cluster.getGPCluster());
                //Update the details
                StringBuffer details = getClusterDetails(cluster);
                table.setValueAt(details, selectedRow, 1);
                //Fire the enumeration action
                nodeAttributesComboBox.setSelectedIndex(nodeAttributesComboBox.getSelectedIndex());

                //Ensure that a loader is set with the selected row and table object
                //Also, we want to set the loader only once during continuous exploration
                //It is only set again when a graph is fully loaded and placed in the table
                if (!loaderSet) { //cluster.getGPCluster().getNodeCount() > 15
                    //internally, the loader is only drawn into the appropriate cell after a short sleep period
                    //to ensure that quick loads are not displayed unecessarily
                    loader.setLoader(selectedRow, table);
                    loaderSet = true;
                }
                //Draw Graph in a separate thread so that it can be interrupted by the slider movement
                drawer.drawGraph(cluster, layouter, layoutNecessary, this);
            }
        }
    }

    /**
     *
     */
    private class GraphDrawer implements Runnable {
        private Thread t;
        private boolean drawGraph;
        MCODECluster cluster;
        SpringEmbeddedLayouter layouter;
        MCODEResultsPanel.SizeAction slider;
        boolean layoutNecessary;

        GraphDrawer () {
            drawGraph = false;
            t = new Thread(this);
            t.start();
        }

        /**
         *
         * @param cluster
         * @param layouter
         * @param layoutNecessary
         * @param slider
         */
        public void drawGraph(MCODECluster cluster, SpringEmbeddedLayouter layouter, boolean layoutNecessary, MCODEResultsPanel.SizeAction slider) {
            this.cluster = cluster;
            this.layouter = layouter;
            this.slider = slider;
            this.layoutNecessary = layoutNecessary;

            layouter.resetDoLayout();
            MCODEUtil.resetLoading();

            drawGraph = true;
        }

        public void run () {
            try {
            while (true) {
                //This ensures that the drawing of this cluster is only attempted once
                //if it is unsuccessful it is because the setup or layout process was interrupted by the slider movement
                //In that case the drawing must occur for a new cluster using the drawGraph method
                if (drawGraph) {
                    Image image = MCODEUtil.convertNetworkToImage(loader, cluster, graphPicSize, graphPicSize, layouter, layoutNecessary);
                    if (image != null) {
                        table.setValueAt(new ImageIcon(image), cluster.getRank(), 0);
                        slider.loaderSet = false;
                    }
                    //If the process was interrupted then the image will return null and the drawing will have to be recalled (with the new cluster)
                    drawGraph = false;
                }
                //This sleep time produces the drawing response time of 1 20th of a second
                Thread.sleep(100);
            }
        } catch (Exception e) {}
        }
    }
}