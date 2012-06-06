package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.task.ExecuteGetRecordByCPathIdTask;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskManager;

import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchHit;

/**
 * Download Details Frame.
 *
 */
final class DownloadDetails extends JDialog {
    private String ids[];
    
    /**
     * Constructor.
     * @param passedRecordList      List of Records that Passed over Filter.
     * @param bpContainer 
     */
    public DownloadDetails(List<SearchHit> passedRecordList) {    	
        this.setTitle("Retrieval Confirmation");
        this.setModal(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        GradientHeader header = new GradientHeader("Confirm Retrieval: " 
        		+ passedRecordList.size() + " records");
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

        ids = new String[passedRecordList.size()];
        int i = 0;
        for (SearchHit record : passedRecordList) {
            tableModel.setValueAt(record.getName(), i, 0);
            tableModel.setValueAt(record.getBiopaxClass(), i, 1);
            if (record.getDataSource() != null) {
                tableModel.setValueAt(record.getDataSource(), i, 2);
            } else {
                tableModel.setValueAt("---", i, 3);
            }
            ids[i++] = record.getUri();
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel(this);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(buttonPanel);
        contentPane.add(panel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(CPath2Factory.getCySwingApplication().getJFrame());
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
    public void downloadInteractions() {
        String networkTitle = "Network";

        OutputFormat format = CPath2Factory.downloadMode;

        TaskManager<?,?> taskManager = CPath2Factory.getTaskManager();
        TaskFactory taskFactory = CPath2Factory.newTaskFactory(
        		new ExecuteGetRecordByCPathIdTask(ids, format, networkTitle));
        taskManager.execute(taskFactory.createTaskIterator());
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

