package org.cytoscape.coreplugin.cpath2.view;

import cytoscape.Cytoscape;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.coreplugin.cpath2.schemas.summary_response.BasicRecordType;
import org.cytoscape.coreplugin.cpath2.task.ExecuteGetRecordByCPathId;
import org.cytoscape.coreplugin.cpath2.view.model.NetworkWrapper;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;
import org.cytoscape.coreplugin.cpath2.web_service.CPathResponseFormat;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebServiceImpl;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/**
 * Download Details Frame.
 *
 * @author Ethan Cerami
 */
public class DownloadDetails extends JDialog {
    private MergePanel mergePanel;
    private long ids[];
    private String peName;

    /**
     * Constructor.
     * @param passedRecordList      List of Records that Passed over Filter.
     * @param peName                Name of Physical Entity.
     */
    public DownloadDetails(java.util.List<BasicRecordType> passedRecordList,
            String peName) {
        super();
        this.peName = peName;
        this.setTitle("Retrieval Confirmation");
        this.setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        GradientHeader header = new GradientHeader("Confirm Retrieval: "+ passedRecordList.size()
                + " records");
        contentPane.add(header, BorderLayout.NORTH);

        DefaultTableModel tableModel = new NonEditableTableModel();
        Vector headerList = new Vector();
        headerList.add("Name (if available)");
        headerList.add("Type");
        headerList.add("Data Source");
        tableModel.setColumnIdentifiers(headerList);
        tableModel.setRowCount(passedRecordList.size());
        JTable table = new JTable(tableModel);

        //  Adjust width / height of viewport;  fixes bug #1620.
        Dimension d = table.getPreferredSize();
        d.width = d.width * 2;
        if (d.height > 200) {
            d.height = 200;
        }
        table.setPreferredScrollableViewportSize(d);
        table.setAutoCreateColumnsFromModel(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ids = new long[passedRecordList.size()];
        int i = 0;
        for (BasicRecordType record : passedRecordList) {
            if (record.getName().equalsIgnoreCase("N/A")) {
                record.setName("---");
            }
            tableModel.setValueAt(record.getName(), i, 0);
            tableModel.setValueAt(record.getEntityType(), i, 1);
            if (record.getDataSource() != null) {
                tableModel.setValueAt(record.getDataSource().getName(), i, 2);
            } else {
                tableModel.setValueAt("---", i, 3);
            }
            ids[i++] = record.getPrimaryId();
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel(this);
        mergePanel = new MergePanel();
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        if (mergePanel != null) {
            panel.add(mergePanel);
        }
        panel.add(buttonPanel);
        contentPane.add(panel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(Cytoscape.getDesktop());
    }

    /**
     * Button Panel.
     */
    private JPanel createButtonPanel(final JDialog dialog) {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                dialog.setVisible(false);
                dialog.dispose();
                downloadInteractions();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        return buttonPanel;
    }

    /**
     * Downloads interaction bundles in a new thread.
     */
    private void downloadInteractions() {
        CyNetwork networkToMerge = null;
        JComboBox networkComboBox = mergePanel.getNetworkComboBox();
        if (networkComboBox != null) {
            NetworkWrapper netWrapper = (NetworkWrapper) networkComboBox.getSelectedItem();
            if (netWrapper != null) {
                networkToMerge = netWrapper.getNetwork();
            }
        }
        downloadInteractions(networkToMerge);
    }

    public void downloadInteractions(CyNetwork networkToMerge) {
        String networkTitle = peName + ":  Network";
        CPathWebService webApi = CPathWebServiceImpl.getInstance();

        CPathResponseFormat format;
        CPathProperties config = CPathProperties.getInstance();
        if (config.getDownloadMode() == CPathProperties.DOWNLOAD_FULL_BIOPAX) {
            format = CPathResponseFormat.BIOPAX;
        } else {
            format = CPathResponseFormat.BINARY_SIF;
        }

        ExecuteGetRecordByCPathId task = new ExecuteGetRecordByCPathId(webApi, ids, format,
                networkTitle, networkToMerge);

        JTaskConfig jTaskConfig = new JTaskConfig();
        jTaskConfig.setOwner(Cytoscape.getDesktop());
        jTaskConfig.displayStatus(true);
        jTaskConfig.setAutoDispose(true);
        jTaskConfig.displayCancelButton(true);
        TaskManager.executeTask(task, jTaskConfig);
    }
}

class NonEditableTableModel extends DefaultTableModel {

    /**
     * Constructor.
     */
    public NonEditableTableModel() {
        super();
    }

    /**
     * Is the specified cell editable?  Never!
     *
     * @param row row index.
     * @param col col index.
     * @return false.
     */
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}

