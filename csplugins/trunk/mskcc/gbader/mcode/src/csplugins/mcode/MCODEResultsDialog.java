package csplugins.mcode;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.actions.GinyUtils;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.NodeView;
import phoebe.PGraphView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 ** User: Gary Bader
 ** Date: Jan 30, 2004
 ** Time: 11:45:02 AM
 ** Description Draws the dialog box that shows the results of MCODE
 **/

/**
 * Reports the results of MCODE complex finding. This class sets up the UI.
 */
public class MCODEResultsDialog extends JFrame {
    MCODEResultsDialog parentDialog;
    protected JTable table;
    JScrollPane scrollPane;
    protected MCODEResultsDialog.MCODEResultsTableModel model;
    //table size parameters
    protected final int defaultRowHeight = 80;
    protected final int preferredTableHeight = defaultRowHeight * 3;
    protected int preferredTableWidth = 0; // incremented below
    //User preference
    protected boolean openAsNewChild = false;
    //Actual complex data
    protected GraphPerspective[] gpComplexArray;    //The list of complexes, sorted by score when !null
    CyNetwork originalInputNetwork;                 //Keep a record of the original input record for use in the
    //table row selection listener
    CyNetworkView originalInputNetworkView;         //Keep a record of this too, if it exists
    HashMap hmNetworkNames;                         //Keep a record of network names we create from the table
    //algorithm object for access to the complex scoring function
    MCODEAlgorithm alg;

    //If imageList is present, will use those images for the complex display
    public MCODEResultsDialog(Frame parentFrame, ArrayList complexes, CyNetwork network, Image imageList[]) {
        super("MCODE Results Summary");
        parentDialog = this;

        //network data (currently focused network)
        originalInputNetwork = network;
        alg = (MCODEAlgorithm) network.getClientData("MCODE_alg");
        //the view may not exist, but we only test for that when we need to (in the
        //TableRowSelectionHandler below)
        originalInputNetworkView = Cytoscape.getNetworkView(network.getIdentifier());
        hmNetworkNames = new HashMap();

        //main panel for dialog box
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        //main data table
        model = new MCODEResultsDialog.MCODEResultsTableModel(network, complexes, imageList);
        table = new JTable(model) {
            //Implement table cell tool tips.
            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                int realColumnIndex = convertColumnIndexToModel(colIndex);

                if (realColumnIndex == 2) { //Size column
                    String value = (String) getValueAt(rowIndex, colIndex);
                    String nodes = null;
                    String edges = null;
                    int i = 0;
                    for (StringTokenizer st = new StringTokenizer(value, ","); st.hasMoreTokens(); i++) {
                        String s = st.nextToken();
                        //string is assumed to only have two tokens, since we build it in MCODEResultsTableModel
                        if (i == 0) {
                            nodes = s;
                        } else if (i == 1) {
                            edges = s;
                        }
                    }
                    tip = "Complex has " + nodes + " nodes and " + edges + " edges.";
                }
                return tip;
            }
        };
        table.setRowHeight(defaultRowHeight);
        initColumnSizes(table);
        table.setPreferredScrollableViewportSize(new Dimension(preferredTableWidth, preferredTableHeight));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setDefaultRenderer(String.class, new CenterAndBoldRenderer());
        table.setDefaultRenderer(StringBuffer.class, new JTextAreaRenderer());
        //Ask to be notified of selection changes.
        ListSelectionModel rowSM = table.getSelectionModel();
        rowSM.addListSelectionListener(new TableRowSelectionHandler());

        //allow the table to scroll
        scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        //new window preference checkbox
        JCheckBox newWindowCheckBox = new JCheckBox("Create a new child network.", false) {
            public JToolTip createToolTip() {
                return new JMultiLineToolTip();
            }
        };
        newWindowCheckBox.addItemListener(new MCODEResultsDialog.newWindowCheckBoxAction());
        newWindowCheckBox.setToolTipText("If checked, will create a new child network of the selected complex.\n" +
                "If not checked, will just select complexes in the main window.");
        bottomPanel.add(newWindowCheckBox, BorderLayout.WEST);
        //the Save button
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new MCODEResultsDialog.SaveAction(this, complexes, network));
        saveButton.setToolTipText("Save result summary to a file");
        bottomPanel.add(saveButton, BorderLayout.CENTER);
        //the OK button
        JButton okButton = new JButton("Done");
        okButton.addActionListener(new MCODEResultsDialog.OKAction(this));
        bottomPanel.add(okButton, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    /**
     * Handles the data to be displayed in the table in this dialog box
     */
    private class MCODEResultsTableModel extends AbstractTableModel {

        //Create column headings
        String[] columnNames = {"Rank", "Score", "Size", "Names", "Complex"};
        Object[][] data;    //the actual table data

        public MCODEResultsTableModel(CyNetwork network, ArrayList complexes, Image imageList[]) {
            GraphPerspective gpComplex;

            //get GraphPerspectives for all complexes, score and rank them
            //convert the ArrayList to an array of GraphPerspectives and sort it by complex score
            gpComplexArray = MCODEUtil.convertComplexListToSortedNetworkList(complexes, network, alg);

            //copy information into the data array that represents the table
            data = new Object[gpComplexArray.length][columnNames.length];
            for (int i = 0; i < gpComplexArray.length; i++) {
                gpComplex = gpComplexArray[i];
                data[i][0] = new String((new Integer(i + 1)).toString()); //rank
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(3);
                data[i][1] = nf.format(alg.scoreComplex(gpComplex));
                //complex size - format: (# prot, # intx)
                data[i][2] = new String(gpComplex.getNodeCount() + "," + gpComplex.getEdgeCount());
                //create a string of node names - this can be long
                data[i][3] = MCODEUtil.getNodeNameList(gpComplex);
                //create an image for each complex - make it a nice layout of the complex
                Image image;
                if (imageList != null) {
                    image = imageList[i];
                } else {
                    image = MCODEUtil.convertNetworkToImage(gpComplex, defaultRowHeight, defaultRowHeight);
                }
                data[i][4] = new ImageIcon(image);
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

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }

    /**
     * Utility method to initialize the column sizes of the table
     *
     * @param table Table to initialize sizes for
     */
    private void initColumnSizes(JTable table) {
        TableColumn column = null;

        for (int i = 0; i < 5; i++) {
            column = table.getColumnModel().getColumn(i);
            if ((i == 0) || (i == 1) || (i == 2)) {
                column.sizeWidthToFit();
            } else if (i == 3) {
                column.setPreferredWidth(100);
            } else if (i == 4) {
                column.setPreferredWidth(defaultRowHeight);
            }
            preferredTableWidth += column.getPreferredWidth();
        }
    }

    /**
     * Handles the OK press for this dialog (makes the dialog disappear)
     */
    private class OKAction extends AbstractAction {
        private JFrame dialog;

        OKAction(JFrame popup) {
            super("");
            this.dialog = popup;
        }

        public void actionPerformed(ActionEvent e) {
            originalInputNetwork.putClientData("MCODE_running", new Boolean(false));
            dialog.dispose();
        }
    }

    /**
     * Handles the Save press for this dialog (save results to a file)
     */
    private class SaveAction extends AbstractAction {
        private JFrame popup;
        private ArrayList complexes;
        private CyNetwork network;

        /**
         * Save action constructor
         *
         * @param popup     The parent dialog
         * @param complexes Complexes to save
         * @param network   Network complexes are from for information about complex components
         */
        SaveAction(JFrame popup, ArrayList complexes, CyNetwork network) {
            super("");
            this.popup = popup;
            this.complexes = complexes;
            this.network = network;
        }

        public void actionPerformed(ActionEvent e) {
            //call save method in MCODE
            // get the file name
            File file = FileUtil.getFile("Save Graph as Interactions",
                    FileUtil.SAVE, new CyFileFilter[]{});

            if (file != null) {
                String fileName = file.getAbsolutePath();
                MCODEUtil.saveMCODEResults(alg, complexes, network, fileName);
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
     */
    private class TableRowSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            //Ignore extra messages.
            if (e.getValueIsAdjusting()) return;
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            final GraphPerspective gpComplex;
            NodeView nv;
            if (!lsm.isSelectionEmpty()) {
                final int selectedRow = lsm.getMinSelectionIndex();
                gpComplex = gpComplexArray[selectedRow];
                //only do this if a view has been created on this network
                if (originalInputNetworkView != null) {
                    //start with no selected nodes
                    GinyUtils.deselectAllNodes(originalInputNetworkView);
                    //go through graph and select nodes in the complex
                    List nodeList = gpComplex.nodesList();
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
                if (openAsNewChild) {
                    NumberFormat nf = NumberFormat.getInstance();
                    nf.setMaximumFractionDigits(3);
                    final String title = "Complex " + (selectedRow + 1) + " Score: " +
                            nf.format(alg.scoreComplex(gpComplex));
                    //check if a network has already been created
                    String id = (String) hmNetworkNames.get(new Integer(selectedRow + 1));
                    if (id != null) {
                        //just switch focus to the already created network
                        Cytoscape.getDesktop().setFocus(id);
                    } else {
                        //create the child network and view
                        final SwingWorker worker = new SwingWorker() {
                            public Object construct() {
                                CyNetwork newNetwork = Cytoscape.createNetwork(gpComplex.getNodeIndicesArray(),
                                        gpComplex.getEdgeIndicesArray(), title, originalInputNetwork);
                                hmNetworkNames.put(new Integer(selectedRow + 1), newNetwork.getIdentifier());
                                PGraphView view = (PGraphView) Cytoscape.createNetworkView(newNetwork);
                                //layout new complex and fit it to window
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

    /**
     * A table cell rendered that centers the item in the cell
     */
    private class CenterAndBoldRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            cell.setFont(new Font(this.getFont().getFontName(), Font.BOLD, 14));
            this.setHorizontalAlignment(CENTER);
            return cell;
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


}
