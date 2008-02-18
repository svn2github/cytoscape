package org.cytoscape.coreplugin.cpath2.task;

import cytoscape.*;
import cytoscape.ding.DingNetworkView;
import cytoscape.ding.CyGraphLOD;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.view.CyNetworkView;
import cytoscape.data.CyAttributes;
import cytoscape.data.readers.GraphReader;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.visual.VisualStyle;
import cytoscape.visual.VisualMappingManager;
import org.cytoscape.coreplugin.cpath2.cytoscape.BinarySifVisualStyleUtil;
import org.cytoscape.coreplugin.cpath2.web_service.*;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.mskcc.biopax_plugin.mapping.MapNodeAttributes;
import org.mskcc.biopax_plugin.util.biopax.BioPaxUtil;
import org.mskcc.biopax_plugin.util.cytoscape.CytoscapeWrapper;
import org.mskcc.biopax_plugin.util.cytoscape.LayoutUtil;
import org.mskcc.biopax_plugin.util.cytoscape.NetworkListener;
import org.mskcc.biopax_plugin.view.BioPaxContainer;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private CyNetwork mergedNetwork;

    /**
     * Constructor.
     *
     * @param webApi cPath Web Api.
     * @param ids    Array of CPath IDs.
     */
    public ExecuteGetRecordByCPathId(CPathWebService webApi, long ids[], String networkTitle) {
        this.webApi = webApi;
        this.ids = ids;
        this.networkTitle = networkTitle;
    }

    /**
     * Constructor.
     *
     * @param webApi        CPathWebService AI.
     * @param ids           List of IDs.
     * @param networkTitle  Network Title.
     * @param mergedNetwork Network to merge with.
     */
    public ExecuteGetRecordByCPathId(CPathWebService webApi, long ids[], String networkTitle,
            CyNetwork mergedNetwork) {
        this.webApi = webApi;
        this.ids = ids;
        this.networkTitle = networkTitle;
        this.mergedNetwork = mergedNetwork;
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
                + CPathProperties.getInstance().getCPathServerName() + "...";
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
                tmpFile = File.createTempFile("temp", ".xml", new File(tmpDir));
                format = CPathProtocol.FORMAT_BIOPAX;
            } else {
                tmpFile = File.createTempFile("temp", ".sif", new File(tmpDir));
                format = CPathProtocol.FORMAT_BINARY_SIF;
            }
            tmpFile.deleteOnExit();

            //  Get Data, and write to temp file.
            String data = webApi.getRecordsByIds(ids, format, taskMonitor);
            FileWriter writer = new FileWriter(tmpFile);
            writer.write(data);
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

            // Fire appropriate network event.
            if (mergedNetwork == null) {
                //  Fire a Network Loaded Event
                Object[] ret_val = new Object[2];
                ret_val[0] = cyNetwork;
                ret_val[1] = networkTitle;
                Cytoscape.firePropertyChange(Cytoscape.NETWORK_LOADED, null, ret_val);
            } else {
                //  Fire a Network Modified Event;  causes Quick Find to Re-Index.
                Object[] ret_val = new Object[2];
                ret_val[0] = mergedNetwork;
                ret_val[1] = networkTitle;
                Cytoscape.firePropertyChange(Cytoscape.NETWORK_MODIFIED, null,
                    ret_val);
            }
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
     *
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

        //  Specify that this is a BINARY_NETWORK
        Cytoscape.getNetworkAttributes().setAttribute(cyNetwork.getIdentifier(),
                BinarySifVisualStyleUtil.BINARY_NETWORK, Boolean.TRUE);

        //  Get all node details.
        //  TODO:  For efficiency, only get the new nodes
        getNodeDetails(cyNetwork, nodeIterator, nodeAttributes);

        if (haltFlag == false) {
            if (mergedNetwork != null) {
                taskMonitor.setStatus("Merging Network...");
                List nodeList = cyNetwork.nodesList();
                for (int i = 0; i < nodeList.size(); i++) {
                    CyNode node = (CyNode) nodeList.get(i);
                    mergedNetwork.addNode(node);
                }
                List edgeList = cyNetwork.edgesList();
                for (int i = 0; i < edgeList.size(); i++) {
                    CyEdge edge = (CyEdge) edgeList.get(i);
                    mergedNetwork.addEdge(edge);
                }

                //  Select this view
                CyNetworkView networkView = Cytoscape.getNetworkView(mergedNetwork.getIdentifier());
                Cytoscape.setCurrentNetwork(mergedNetwork.getIdentifier());
                Cytoscape.setCurrentNetworkView(mergedNetwork.getIdentifier());

                //  Apply Layout
                LayoutUtil layoutAlgorithm = new LayoutUtil();
                networkView.applyLayout(layoutAlgorithm);

                final BioPaxContainer bpContainer = BioPaxContainer.getInstance();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        CytoscapeWrapper.activateBioPaxPlugInTab(bpContainer);
                        bpContainer.showLegend();
                        Cytoscape.getCurrentNetworkView().fitContent();
                        VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
                        vizmapper.applyAppearances();
                    }
                });

                //  Select only the new nodes
                mergedNetwork.unselectAllEdges();
                mergedNetwork.unselectAllNodes();
                mergedNetwork.setSelectedNodeState(nodeList, true);
                mergedNetwork.setSelectedEdgeState(edgeList, true);

                //  Delete the temp network.
                Cytoscape.destroyNetwork(cyNetwork);

            } else if (cyNetwork.getNodeCount() < Integer.parseInt(CytoscapeInit.getProperties()
                    .getProperty("viewThreshold"))) {
                taskMonitor.setStatus("Creating Network View...");
                taskMonitor.setPercentCompleted(-1);

                //  Set up the right visual style
                VisualStyle visualStyle = BinarySifVisualStyleUtil.getVisualStyle();

                //  Set up the right layout algorithm.
                LayoutUtil layoutAlgorithm = new LayoutUtil();

                //  Now, create the view.
                //  Use local create view option, so that we don't mess up the visual style.
                createNetworkView(cyNetwork, cyNetwork.getTitle(), layoutAlgorithm,
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
            }
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
    private void getNodeDetails
            (CyNetwork
                    cyNetwork, Iterator
                    nodeIterator,
                    CyAttributes
                            nodeAttributes) {
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
                StringReader reader = new StringReader(xml);
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

    private CyNetworkView createNetworkView (CyNetwork network, String title, CyLayoutAlgorithm
            layout, VisualStyle vs) {

		if (Cytoscape.viewExists(network.getIdentifier())) {
			return Cytoscape.getNetworkView(network.getIdentifier());
		}

		final DingNetworkView view = new DingNetworkView(network, title);
		view.setGraphLOD(new CyGraphLOD());
		view.setIdentifier(network.getIdentifier());
		view.setTitle(network.getTitle());
		Cytoscape.getNetworkViewMap().put(network.getIdentifier(), view);
		Cytoscape.setSelectionMode(Cytoscape.getSelectionMode(), view);

		if (vs != null) {
			view.setVisualStyle(vs.getName());
		}

		if (layout == null) {
			layout = CyLayouts.getDefaultLayout();
		}

		Cytoscape.firePropertyChange(cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED,
                null, view);
		layout.doLayout(view);
		view.fitContent();
		view.redrawGraph(false, true);
		return view;
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