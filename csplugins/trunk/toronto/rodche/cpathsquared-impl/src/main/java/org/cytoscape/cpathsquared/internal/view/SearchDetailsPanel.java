package org.cytoscape.cpathsquared.internal.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.cytoscape.cpathsquared.internal.CPath2Factory;
import org.cytoscape.cpathsquared.internal.task.ExecuteGetRecordByCPathIdTaskFactory;
import org.cytoscape.cpathsquared.internal.webservice.CPathProperties;
import org.cytoscape.cpathsquared.internal.webservice.CPathResponseFormat;
import org.cytoscape.cpathsquared.internal.webservice.CPathWebService;
import org.cytoscape.cpathsquared.internal.webservice.CPathWebServiceImpl;

/**
 * Search Details Panel.
 *
 * @author Ethan Cerami.
 */
public class SearchDetailsPanel extends JPanel {
	
    private CPath2Factory factory;

	/**
     * Constructor.
     *
     * @param interactionBundleModel InteractionBundleModel Object.
     * @param pathwayTableModel     PathwayTableModel Object.
     * @param application 
     * @param taskManager 
     */
    public SearchDetailsPanel(InteractionBundleModel interactionBundleModel,
            PathwayTableModel pathwayTableModel, CPath2Factory factory) {
    	this.factory = factory;
        GradientHeader header = new GradientHeader("Step 3:  Select Network(s)");
        setLayout(new BorderLayout());
        this.add(header, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel interactionPanel = factory.createInteractionBundlePanel(interactionBundleModel);
        JPanel pathwayPane = createPathwayPane(pathwayTableModel);
        Font font = tabbedPane.getFont();
        Font newFont = new Font (font.getFamily(), Font.PLAIN, font.getSize()-2);
        tabbedPane.setFont(newFont);

        tabbedPane.add("Pathways", pathwayPane);
        tabbedPane.add("Interaction Networks", interactionPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createPathwayPane(PathwayTableModel pathwayTableModel) {
        JPanel pathwayPane = new JPanel(new BorderLayout());
        JScrollPane pathwayTable = createPathwayTable(pathwayTableModel);
        pathwayPane.add(pathwayTable, BorderLayout.CENTER);
        JLabel label = new JLabel ("> Double-click pathway to retrieve.");
        label.setForeground(Color.BLUE);
        Font font = label.getFont();
        Font newFont = new Font(font.getFamily(), Font.PLAIN, font.getSize()-2);
        label.setFont(newFont);
        pathwayPane.add(label, BorderLayout.SOUTH);
        return pathwayPane;
    }
    /**
     * Creates the Pathway Table.
     *
     * @return JScrollPane Object.
     */
    private JScrollPane createPathwayTable(final PathwayTableModel pathwayTableModel) {
        final JTable pathwayTable = new JTable(pathwayTableModel);
        pathwayTable.setAutoCreateColumnsFromModel(true);
        pathwayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        pathwayTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int rows[] = pathwayTable.getSelectedRows();
                    if (rows.length > 0) {
                        downloadPathway(rows, pathwayTableModel);
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(pathwayTable);
        return scrollPane;
    }

    /**
     * Downloads a single pathway in a new thread.
     * @param rows                  Selected row.
     * @param pathwayTableModel     Pathway Table Model.
     */
    private void downloadPathway(int[] rows, PathwayTableModel pathwayTableModel) {
        try {
            String internalId = pathwayTableModel.getInternalId(rows[0]);
            String title = pathwayTableModel.getValueAt(rows[0], 0)
                    + " (" + pathwayTableModel.getValueAt(rows[0], 1) + ")";

            CPathWebService webApi = CPathWebServiceImpl.getInstance();
            ExecuteGetRecordByCPathIdTaskFactory taskFactory;

            CPathResponseFormat format;
            if (CPathProperties.downloadMode == CPathProperties.DOWNLOAD_FULL_BIOPAX) {
                format = CPathResponseFormat.BIOPAX;
            } else {
                format = CPathResponseFormat.BINARY_SIF;
            }

            taskFactory = factory.createExecuteGetRecordByCPathIdTaskFactory(
            	webApi, new String[]{internalId}, format, title);
            
            factory.getTaskManager().execute(taskFactory);
            
        } catch (IndexOutOfBoundsException e) {
            //  Ignore TODO strange...
        }
    }
}

