package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.CPath2Properties;
import org.cytoscape.cpathsquared.internal.CPath2WebService;
import org.cytoscape.cpathsquared.internal.task.ExecuteGetRecordByCPathIdTaskFactory;

import cpath.service.OutputFormat;
import cpath.service.jaxb.SearchHit;
import cpath.service.jaxb.SearchResponse;


public class TopPathwaysPanel extends JPanel {
    private SearchResponse model;
	private final CPath2Factory factory;
	private final CPath2WebService webApi;

    public TopPathwaysPanel(SearchResponse topPathways, CPath2Factory factory, CPath2WebService webApi) {
        this.factory = factory;
        this.model = topPathways;
        this.webApi = webApi;

        JScrollPane pathwayTable = createPathwayTable(model);
        add(pathwayTable, BorderLayout.CENTER);
        JLabel label = new JLabel ("> Double-click pathway to retrieve.");
        label.setForeground(Color.BLUE);
        Font font = label.getFont();
        Font newFont = new Font(font.getFamily(), Font.PLAIN, font.getSize()-2);
        label.setFont(newFont);
        add(label, BorderLayout.SOUTH);
    }

 
    private JScrollPane createPathwayTable(final SearchResponse model) {
    	final DefaultTableModel tableModel = new DefaultTableModel();
    	tableModel.setColumnIdentifiers(new String[] {"Pathway"});
        final JTable pathwayTable = new JTable(tableModel);
        pathwayTable.setAutoCreateColumnsFromModel(true);
        pathwayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);       

        pathwayTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int rows[] = pathwayTable.getSelectedRows();
                    if (rows.length > 0) {
                        downloadPathway(rows, tableModel);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(pathwayTable);
        return scrollPane;
    }

    /**
     * Downloads a single pathway in a new thread.
     * @param rows             Selected row.
     * @param pathwayModel     Pathway Model.
     */
    private void downloadPathway(int[] rows, DefaultTableModel model) {
        try {
        	SearchHit hit = (SearchHit) model.getDataVector().get(rows[0]);
        	String internalId = hit.getUri();
            String title = model.getValueAt(rows[0], 0)
                    + " (" + model.getValueAt(rows[0], 1) + ")";
            ExecuteGetRecordByCPathIdTaskFactory taskFactory;

            OutputFormat format;
            //TODO add EXTENDED_BINARY_SIF
            if (CPath2Properties.downloadMode == CPath2Properties.DOWNLOAD_BIOPAX) {
                format = OutputFormat.BIOPAX;
            } else {
                format = OutputFormat.BINARY_SIF;
            }

            taskFactory = factory.createExecuteGetRecordByCPathIdTaskFactory(
            	webApi, new String[]{internalId}, format, title);
            
            factory.getTaskManager().execute(taskFactory);
            
        } catch (IndexOutOfBoundsException e) {
            //  Ignore TODO strange...
        }
    }

    
}
