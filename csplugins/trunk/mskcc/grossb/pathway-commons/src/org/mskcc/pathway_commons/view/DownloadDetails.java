package org.mskcc.pathway_commons.view;

import cytoscape.Cytoscape;
import org.mskcc.pathway_commons.schemas.summary_response.RecordType;
import org.mskcc.pathway_commons.web_service.CPathProtocol;
import org.mskcc.pathway_commons.util.NetworkUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
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

    public DownloadDetails(java.util.List<RecordType> passedRecordList) {
        super();
        this.setTitle("Download Confirmation");
        this.setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        GradientHeader header = new GradientHeader("Confirm Download: "+ passedRecordList.size()
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
        Dimension d = table.getPreferredSize();
        d.width = d.width * 2;
        table.setPreferredScrollableViewportSize(d);
        table.setAutoCreateColumnsFromModel(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        final long ids[] = new long[passedRecordList.size()];
        int i = 0;
        for (RecordType record : passedRecordList) {
            tableModel.setValueAt(record.getName(), i, 0);
            tableModel.setValueAt(record.getType(), i, 1);
            if (record.getDataSource() != null) {
                tableModel.setValueAt(record.getDataSource().getName(), i, 2);
            } else {
                tableModel.setValueAt("N/A", i, 3);
            }
            ids[i++] = record.getPrimaryId();
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel(ids, this);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(Cytoscape.getDesktop());
    }

    /**
     * Button Panel.
     */
    private JPanel createButtonPanel(final long[] ids, final JDialog dialog) {
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent actionEvent) {
                dialog.setVisible(false);
                dialog.dispose();
                downloadInteractions(ids);
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
     * Downloads a single pathway in a new thread.
     */
    private void downloadInteractions(long ids[]) {
        CPathProtocol protocol = new CPathProtocol();
        protocol.setCommand(CPathProtocol.COMMAND_GET_RECORD_BY_CPATH_ID);
        StringBuffer q = new StringBuffer();
        for (int i=0; i<ids.length; i++) {
            q.append (Long.toString(ids[i])+",");
        }
        protocol.setQuery(q.toString());
        protocol.setFormat(CPathProtocol.FORMAT_BIOPAX);
        String uri = protocol.getURI();
        System.out.println ("Connecting to:  " + uri);
        NetworkUtil networkUtil = new NetworkUtil(uri, null, false, null);
        networkUtil.start();
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
     * Is the specified cell editable?
     *
     * @param row row index.
     * @param col col index.
     * @return true or false.
     */
    public boolean isCellEditable(int row, int col) {
        return false;
    }
}