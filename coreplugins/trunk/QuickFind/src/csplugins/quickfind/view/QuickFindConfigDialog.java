package csplugins.quickfind.view;

import csplugins.quickfind.util.CyAttributesUtil;
import csplugins.quickfind.util.IndexType;
import csplugins.quickfind.util.QuickFind;
import csplugins.quickfind.util.QuickFindFactory;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

/**
 * Quick Find Config Dialog Box.
 *
 * @author Ethan Cerami.
 */
public class QuickFindConfigDialog extends JDialog {
    /**
     * Attribute ComboBox
     */
    private JComboBox attributeComboBox;

    /**
     * Table of Sample Attribute Values
     */
    private JTable sampleAttributeValuesTable;

    /**
     * Constructor.
     */
    public QuickFindConfigDialog() {
        Container container = getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        this.setTitle("Configure Find Options");

        //  Create Master Panel
        JPanel masterPanel = new JPanel();
        masterPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        masterPanel.setLayout(new BoxLayout(masterPanel, BoxLayout.Y_AXIS));

        //  Add Attribute ComboBox Panel
        JPanel attributePanel = createAttributeSelectionPanel();
        masterPanel.add(attributePanel);

        //  Add Sample Attribute Values Panel
        JPanel attributeValuePanel = createAttributeValuePanel();
        masterPanel.add(attributeValuePanel);

        //  Add Button Panel
        masterPanel.add(Box.createVerticalGlue());
        JPanel buttonPanel = createButtonPanel();
        masterPanel.add(buttonPanel);
        container.add(masterPanel);

        //  Pack, set modality, and center on screen
        pack();
        setModal(true);
        setLocationRelativeTo(Cytoscape.getDesktop());
        show();
    }

    /**
     * Creates Button Panel.
     *
     * @return JPanel Object.
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        // Cancel Button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                QuickFindConfigDialog.this.hide();
                QuickFindConfigDialog.this.dispose();
            }
        });

        //  Apply Button
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                QuickFindConfigDialog.this.hide();
                QuickFindConfigDialog.this.dispose();

                String newAttribute = (String)
                        attributeComboBox.getSelectedItem();
                QuickFind quickFind =
                        QuickFindFactory.getGlobalQuickFindInstance();
                String currentAttribute = quickFind.getCurrentAttributeKey();
                if (!newAttribute.equals(currentAttribute)) {
                    ReindexQuickFind task = new ReindexQuickFind(newAttribute);
                    JTaskConfig config = new JTaskConfig();
                    config.setAutoDispose(true);
                    config.displayStatus(true);
                    config.displayTimeElapsed(false);
                    config.displayCloseButton(true);
                    config.setOwner(Cytoscape.getDesktop());
                    config.setModal(true);

                    //  Execute Task via TaskManager
                    //  This automatically pops-open a JTask Dialog Box.
                    //  This method will block until the JTask Dialog Box
                    //  is disposed.
                    boolean success = TaskManager.executeTask(task, config);
                }
            }
        });
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);
        return buttonPanel;
    }

    /**
     * Creates a Panel of Sample Attribute Values.
     *
     * @return JPanel Object.
     */
    private JPanel createAttributeValuePanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder("Sample Attribute Values:"));
        panel.setLayout(new GridLayout(1, 0));

        //  Table Cells are not editable
        sampleAttributeValuesTable = new JTable() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        addTableModel (sampleAttributeValuesTable);
        this.setVisibleRowCount(sampleAttributeValuesTable, 5);
        panel.add(sampleAttributeValuesTable);
        return panel;
    }

    /**
     * Creates TableModel consisting of Distinct Attribute Values.
     *
     */
    private void addTableModel(JTable table) {
        QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
        Object selectedAttribute = attributeComboBox.getSelectedItem();

        //  Determine current attribute key
        String attributeKey;
        if (selectedAttribute != null) {
            attributeKey = selectedAttribute.toString();
        } else {
            attributeKey = quickFind.getCurrentAttributeKey();
        }

        //  Create column names
        Vector columnNames = new Vector();
        columnNames.add(attributeKey);
        TableModel model = new DefaultTableModel(columnNames, 5);

        DetermineDistinctValuesTask task = new DetermineDistinctValuesTask
                (model, attributeKey);

        JTaskConfig config = new JTaskConfig();
        config.setAutoDispose(true);
        config.displayStatus(true);
        config.displayTimeElapsed(false);
        config.displayCloseButton(true);
        config.setOwner(Cytoscape.getDesktop());
        config.setModal(true);

        //  Execute Task via TaskManager
        //  This automatically pops-open a JTask Dialog Box.
        //  This method will block until the JTask Dialog Box
        //  is disposed.
        table.setModel(model);
        boolean success = TaskManager.executeTask(task, config);
    }


    /**
     * Creates the Attribute Selection Panel.
     *
     * @return JPanel Object.
     */
    private JPanel createAttributeSelectionPanel() {
        QuickFind quickFind = QuickFindFactory.getGlobalQuickFindInstance();
        JPanel attributePanel = new JPanel();
        attributePanel.setLayout(new BoxLayout(attributePanel,
                BoxLayout.X_AXIS));

        //  Obtain Node Attributes
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
        String attributeNames[] = nodeAttributes.getAttributeNames();

        if (attributeNames != null && attributeNames.length > 0) {
            JLabel label = new JLabel("Search on Attribute:  ");
            label.setBorder(new EmptyBorder(5, 5, 5, 5));
            attributePanel.add(label);
            Vector attributeList = new Vector();

            //  Show all attributes, except those of TYPE_COMPLEX
            //  Add default:  Unique Identifier
            attributeList.add(QuickFind.UNIQUE_IDENTIFIER);
            for (int i = 0; i < attributeNames.length; i++) {
                int type = nodeAttributes.getType(attributeNames[i]);
                if (type != CyAttributes.TYPE_COMPLEX) {
                    //  Explicitly filter out CANONICAL_NAME, as it is
                    //  now deprecated.
                    if (!attributeNames[i].equals(Semantics.CANONICAL_NAME)) {
                        attributeList.add(attributeNames[i]);
                    }
                }
            }

            //  Alphabetical sort
            Collections.sort(attributeList);

            //  Create ComboBox
            attributeComboBox = new JComboBox(attributeList);
            String currentAttribute = quickFind.getCurrentAttributeKey();
            if (currentAttribute != null) {
                attributeComboBox.setSelectedItem(currentAttribute);
            }
            attributePanel.add(attributeComboBox);
            attributePanel.add(Box.createHorizontalGlue());

            //  Add Action Listener
            attributeComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    addTableModel(sampleAttributeValuesTable);
                }
            });
        }
        return attributePanel;
    }

    /**
     * Sets the Visible Row Count.
     *
     * @param table JTable Object.
     * @param rows  Number of Visible Rows.
     */
    private void setVisibleRowCount(JTable table, int rows) {
        int height = 0;
        for (int row = 0; row < rows; row++) {
            height += table.getRowHeight(row);
        }

        table.setPreferredScrollableViewportSize(new Dimension(
                table.getPreferredScrollableViewportSize().width,
                height
        ));
    }

}

/**
 * Long-term task to Reindex QuickFind.
 *
 * @author Ethan Cerami.
 */
class ReindexQuickFind implements Task {
    private String newAttributeKey;
    private TaskMonitor taskMonitor;

    /**
     * Constructor.
     *
     * @param newAttributeKey New Attribute Key for Indexing.
     */
    ReindexQuickFind(String newAttributeKey) {
        this.newAttributeKey = newAttributeKey;
    }

    /**
     * Executes Task:  Reindex Quick Find.
     */
    public void run() {
        QuickFind quickFind =
                QuickFindFactory.getGlobalQuickFindInstance();
        quickFind.reindexAllNetworks(IndexType.NODE_INDEX,
                newAttributeKey, taskMonitor);
    }

    public void halt() {
        // No-op
    }

    /**
     * Sets the TaskMonitor.
     *
     * @param taskMonitor TaskMonitor Object.
     * @throws IllegalThreadStateException Illegal Thread State.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor)
            throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets Title of Task.
     *
     * @return Title of Task.
     */
    public String getTitle() {
        return "ReIndexing";
    }
}

/**
 * Long-term task to determine distinct attribute values.
 *
 * @author Ethan Cerami.
 */
class DetermineDistinctValuesTask implements Task {
    private TableModel tableModel;
    private String attributeKey;
    private TaskMonitor taskMonitor;

    public DetermineDistinctValuesTask (TableModel tableModel,
            String attributeKey) {
        this.tableModel = tableModel;
        this.attributeKey = attributeKey;
    }

    public void run() {
        taskMonitor.setPercentCompleted(-1);
        //  Obtain distinct attribute values
        CyNetwork network = Cytoscape.getCurrentNetwork();
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

        Iterator nodeIterator = network.nodesIterator();
        String values[] = CyAttributesUtil.getDistinctAttributeValues
                (nodeIterator, nodeAttributes, attributeKey, 5);
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length; i++) {
                tableModel.setValueAt(values[i], i, 0);
            }
        } else {
            tableModel.setValueAt("No values found", 0, 0);
        }
    }

    public void halt() {
        //  No-op
    }

    public void setTaskMonitor(TaskMonitor taskMonitor)
            throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle() {
        return "Accessing sample attribute data";
    }
}