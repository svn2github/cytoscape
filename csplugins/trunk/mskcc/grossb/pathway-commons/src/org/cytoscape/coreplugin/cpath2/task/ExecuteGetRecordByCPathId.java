package org.cytoscape.coreplugin.cpath2.task;

import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.CytoscapeInit;
import cytoscape.visual.VisualStyle;
import cytoscape.data.readers.GraphReader;
import cytoscape.data.CyAttributes;
import org.cytoscape.coreplugin.cpath2.web_service.*;
import org.cytoscape.coreplugin.cpath2.cytoscape.BinarySifVisualStyleUtil;
import org.mskcc.biopax_plugin.mapping.MapNodeAttributes;
import org.mskcc.biopax_plugin.util.biopax.BioPaxUtil;
import org.mskcc.biopax_plugin.util.cytoscape.CytoscapeWrapper;
import org.mskcc.biopax_plugin.util.cytoscape.NetworkListener;
import org.mskcc.biopax_plugin.util.cytoscape.LayoutUtil;
import org.mskcc.biopax_plugin.view.BioPaxContainer;
import org.jdom.JDOMException;
import org.jdom.Element;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.ArrayList;

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
    private boolean haltFlag = false;

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
        haltFlag = true;
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
        return "Retrieving " + networkTitle + " from "
            + CPathProperties.getInstance().getCPathServerName() +"...";
    }

    /**
     * Our implementation of Task.run().
     */
    public void run() {
        try {
            // read the network from cpath instance
            taskMonitor.setPercentCompleted(-1);
            taskMonitor.setStatus("Retrieving " + networkTitle + ".");

            //  Store BioPAX to Temp File
            String tmpDir = System.getProperty("java.io.tmpdir");
            CPathProperties config = CPathProperties.getInstance();
            //  Branch based on download mode setting.
            File tmpFile;
            String format;
            if (config.getDownloadMode() == CPathProperties.DOWNLOAD_FULL_BIOPAX) {
                tmpFile =  File.createTempFile("temp", ".xml", new File(tmpDir));
                format = CPathProtocol.FORMAT_BIOPAX;
            } else {
                tmpFile =  File.createTempFile("temp", ".sif", new File(tmpDir));
                format = CPathProtocol.FORMAT_BINARY_SIF;
            }
            tmpFile.deleteOnExit();

            //  Get BioPAX XML
            String xml = webApi.getRecordsByIds(ids, format, taskMonitor);


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
            taskMonitor.setPercentCompleted(-1);

            CyNetwork cyNetwork = null;
            // Branch, based on download mode.
            if (config.getDownloadMode() == CPathProperties.DOWNLOAD_REDUCED_BINARY_SIF) {
                // create network, without the view.
                cyNetwork = Cytoscape.createNetwork(reader, false, null);
                postProcessingBinarySif(cyNetwork);
            } else {
                //  create network, with the view.
                cyNetwork = Cytoscape.createNetwork(reader, true, null);
            }

            // update the task monitor
            Object[] ret_val = new Object[2];
            ret_val[0] = cyNetwork;
            ret_val[1] = networkTitle;
            Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, ret_val);

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

    /**
     * Execute Post-Processing on BINARY SIF Network.
     * @param cyNetwork Cytoscape Network Object.
     */
    private void postProcessingBinarySif(final CyNetwork cyNetwork) {
        Iterator nodeIterator = cyNetwork.nodesIterator();
        CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();

        //  Init the node attribute meta data, e.g. description, visibility, etc.
        MapNodeAttributes.initAttributes(nodeAttributes);

        //  Set the Quick Find Default Index
        Cytoscape.getNetworkAttributes().setAttribute(cyNetwork.getIdentifier(),
                "quickfind.default_index", "biopax.node_label");

        //  Get all node details.
        getNodeDetails(cyNetwork, nodeIterator, nodeAttributes);

        //  Create the view, visual style, and layout.
        if (haltFlag == false &&
                cyNetwork.getNodeCount() < Integer.parseInt(CytoscapeInit.getProperties()
                .getProperty("viewThreshold"))) {
            taskMonitor.setStatus("Creating Network View...");
            taskMonitor.setPercentCompleted(-1);            

            //  Set up the right visual style
            VisualStyle visualStyle = BinarySifVisualStyleUtil.getVisualStyle();

            //  Set up the right layout algorithm.
            LayoutUtil layoutAlgorithm = new LayoutUtil();

            //  Now, create the view.
            Cytoscape.createNetworkView(cyNetwork, cyNetwork.getTitle(), layoutAlgorithm,
                    visualStyle);

            // Set up clickable node details.
            CytoscapeWrapper.initBioPaxPlugInUI();
            final BioPaxContainer bpContainer = BioPaxContainer.getInstance();
            NetworkListener networkListener = bpContainer.getNetworkListener();
            networkListener.registerNetwork(cyNetwork);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    CytoscapeWrapper.activateBioPaxPlugInTab(bpContainer);
                    bpContainer.showLegend();
                    Cytoscape.getCurrentNetworkView().fitContent();
                    cyNetwork.setTitle(networkTitle);
                }
            });
        } else {
            //  If we have requested a halt, and we have a network, destroy it.
            if (cyNetwork != null) {
                Cytoscape.destroyNetwork(cyNetwork);
            }
        }
    }

    /**
     * Gets Details for Each Node from Web Service API.
     */
    private void getNodeDetails(CyNetwork cyNetwork, Iterator nodeIterator,
            CyAttributes nodeAttributes) {
        taskMonitor.setStatus("Retrieving node details...");
        taskMonitor.setPercentCompleted(0);
        int numNodes = cyNetwork.nodesList().size();
        int counter = 0;
        while (nodeIterator.hasNext() && haltFlag == false) {
            CyNode node = (CyNode) nodeIterator.next();
            String nodeId = node.getIdentifier();
            long ids[] = new long[1];
            ids[0] = Long.valueOf(nodeId);

            try {
                String xml = webApi.getRecordsByIds(ids, CPathProtocol.FORMAT_BIOPAX,
                        new NullTaskMonitor());
                StringReader reader = new StringReader (xml);
                BioPaxUtil bpUtil = new BioPaxUtil(reader, new NullTaskMonitor());
                ArrayList peList = bpUtil.getPhysicalEntityList();
                if (peList.size() > 0) {
                    Element element = (Element) peList.get(0);
                    MapNodeAttributes.mapNodeAttribute(element, nodeId, nodeAttributes, bpUtil);
                }
            } catch (CPathException e) {
                e.printStackTrace();
            } catch (EmptySetException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JDOMException e) {
                e.printStackTrace();
            }
            int percentComplete = (int) (100.0 * (counter / (double) numNodes));
            taskMonitor.setPercentCompleted(percentComplete);
            counter++;
        }
    }

    private String convertToSifFileName (String networkFileName) {
        String temp = networkFileName.replaceAll(" ", "_");
        return temp + "_";
    }
}

class NullTaskMonitor implements TaskMonitor {

    public void setPercentCompleted(int i) throws IllegalArgumentException {
    }

    public void setEstimatedTimeRemaining(long l) throws IllegalThreadStateException {
    }

    public void setException(Throwable throwable, String string)
            throws IllegalThreadStateException {
    }

    public void setException(Throwable throwable, String string, String string1)
            throws IllegalThreadStateException {
    }

    public void setStatus(String string) throws IllegalThreadStateException, NullPointerException {
    }
}