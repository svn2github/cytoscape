package org.cytoscape.coreplugin.cpath2.task;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.readers.GraphReader;
import org.cytoscape.coreplugin.cpath2.util.PluginProperties;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;
import org.cytoscape.coreplugin.cpath2.web_service.EmptySetException;
import org.cytoscape.coreplugin.cpath2.web_service.CPathException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Controller for Executing a Get Record(s) by CPath ID(s) command.
 *
 * @author Ethan Cerami.
 */
public class ExecuteGetRecordByCPathId implements Task {
    private CPathWebService webApi;
    private TaskMonitor taskMonitor;
    private long ids[];
    private String networkTitle;

    /**
     * Constructor.
     *
     * @param webApi         cPath Web Api.
     * @param ids            Array of CPath IDs.
     */
    public ExecuteGetRecordByCPathId(CPathWebService webApi, long ids[], String networkTitle) {
        this.webApi = webApi;
        this.ids = ids;
        this.networkTitle = networkTitle;
    }

    /**
     * Our implementation of Task.abort()
     */
    public void halt() {
        webApi.abort();
    }

    /**
     * Our implementation of Task.setTaskMonitor().
     *
     * @param taskMonitor TaskMonitor
     */
    public void setTaskMonitor(TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Our implementation of Task.getTitle.
     *
     * @return Task Title.
     */
    public String getTitle() {
        return "Retrieving Records from " + PluginProperties.getNameOfCPathInstance() +"...";
    }

    /**
     * Our implementation of Task.run().
     */
    public void run() {
        try {
            // read the network from cpath instance
            taskMonitor.setPercentCompleted(-1);
            taskMonitor.setStatus("Retrieving Records");

            //  Get BioPAX XML
            String xml = webApi.getRecordsByIds(ids, taskMonitor);

            //  Store BioPAX to Temp File
            String tmpDir = System.getProperty("java.io.tmpdir");
            File tmpFile =  File.createTempFile("temp", ".xml", new File(tmpDir));
            tmpFile.deleteOnExit();
            FileWriter writer = new FileWriter(tmpFile);
            writer.write(xml);
            writer.close();

            //  Load up File via ImportHandler Framework
            //  the biopax graph reader is going to be called
            //  it will look for the network view title
            //  via system properties, so lets set it now
            if (networkTitle != null && networkTitle.length() > 0) {
                System.setProperty("biopax.network_view_title", networkTitle);
            }
            GraphReader reader = Cytoscape.getImportHandler().getReader(tmpFile.getAbsolutePath());
            taskMonitor.setStatus("Creating Cytoscape Network...");
            CyNetwork cyNetwork = Cytoscape.createNetwork(reader, true, null);

            Object[] ret_val = new Object[2];
            ret_val[0] = cyNetwork;
            ret_val[1] = networkTitle;
            Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, ret_val);
            Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null, ret_val);
            taskMonitor.setPercentCompleted(1);

            // update the task monitor
            taskMonitor.setStatus("Done");
            taskMonitor.setPercentCompleted(100);
        } catch (IOException e) {
            taskMonitor.setException(e, "Failed to retrieve records.",
                    "Please try again.");
        } catch (EmptySetException e) {
            taskMonitor.setException(e, "No matches found for your request.  ",
                    "Please try again.");
        } catch (CPathException e) {
            if (e.getErrorCode() != CPathException.ERROR_CANCELED_BY_USER) {
                taskMonitor.setException(e, e.getMessage(), e.getRecoveryTip());
            }
        }
    }
}