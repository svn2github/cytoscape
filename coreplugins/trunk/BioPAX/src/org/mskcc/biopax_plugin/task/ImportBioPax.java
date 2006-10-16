// $Id: ImportBioPax.java,v 1.23 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.task;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualStyle;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.mskcc.biopax_plugin.mapping.MapBioPaxToCytoscape;
import org.mskcc.biopax_plugin.style.BioPaxVisualStyleUtil;
import org.mskcc.biopax_plugin.util.biopax.BioPaxFileChecker;
import org.mskcc.biopax_plugin.util.biopax.BioPaxUtil;
import org.mskcc.biopax_plugin.util.cytoscape.CyNetworkUtil;
import org.mskcc.biopax_plugin.util.cytoscape.CyNetworkViewUtil;
import org.mskcc.biopax_plugin.util.cytoscape.CytoscapeWrapper;
import org.mskcc.biopax_plugin.util.net.WebFileConnect;
import org.mskcc.biopax_plugin.util.rdf.RdfQuery;

import java.io.*;
import java.net.MalformedURLException;

/**
 * Task to Import a BioPAX File.
 *
 * @author Ethan Cerami.
 */
public class ImportBioPax implements Task {
    private TaskMonitor taskMonitor;
    private File bioPaxFile;
    private String bioPaxUrl;
    private String xmlContent;
    private String pathwayName;
    private CyNetwork cyNetwork;

    /**
     * Constructor.
     *
     * @param file File Object.
     */
    public ImportBioPax(File file) {
        this.bioPaxFile = file;
    }

    /**
     * Constructor.
     *
     * @param url URL String.
     */
    public ImportBioPax(String url) {
        this.bioPaxUrl = url;
    }

    /**
     * Constructor.
     *
     * @param xmlContent  XML Content.
     * @param pathwayName Pathway name.
     */
    public ImportBioPax(String xmlContent, String pathwayName) {
        this.xmlContent = xmlContent;
        this.pathwayName = pathwayName;
    }

    /**
     * Halts the Task:  Not Currently Supported.
     */
    public void halt() {
        //  No Op.  Not Currently Supported.
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor Task Monitor Object.
     * @throws IllegalThreadStateException Illegal Thread State Exception.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor)
            throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets Task Title.
     *
     * @return Task Title.
     */
    public String getTitle() {
        if (pathwayName == null) {
            return "Importing BioPAX File";
        } else {
            return "Importing Pathway:  " + pathwayName;
        }
    }

    /**
     * Gets the newly created CyNetwork, if there is one.
     *
     * @return CyNetwork Object.
     */
    public CyNetwork getCyNetwork() {
        return cyNetwork;
    }

    /**
     * Executes the Task.
     */
    public void run() {
        try {
            if (taskMonitor != null) {
                if (pathwayName == null) {
                    taskMonitor.setStatus("Importing BioPAX File");
                } else {
                    taskMonitor.setStatus("Importing Pathway:  " + pathwayName);
                }
                taskMonitor.setPercentCompleted(-1);
            }
            if (bioPaxFile != null) {
                xmlContent = WebFileConnect.retrieveDocument(bioPaxFile);
            } else if (bioPaxUrl != null) {
                xmlContent = WebFileConnect.retrieveDocument(bioPaxUrl);
            }

            //  First, check that this is actually a BioPAX File.
            StringReader reader1 = new StringReader(xmlContent);
            BioPaxFileChecker checker = new BioPaxFileChecker(reader1);
            if (!checker.isProbablyBioPaxFile()) {
                taskMonitor.setStatus("The specified file"
                        + " does not appear to be a BioPAX document."
                        + "  Please check and try again.");
            } else {
                StringReader reader2 = new StringReader(xmlContent);
                loadFile(reader2);
            }
        } catch (FileNotFoundException e) {
            if (bioPaxFile != null) {
                taskMonitor.setStatus("Could not find the specified BioPAX "
                        + "bioPaxFile:  " + bioPaxFile.getName());
            } else {
                taskMonitor.setStatus("Could not find the specified BioPAX "
                        + "bioPaxFile:  " + bioPaxUrl);
            }
        } catch (MalformedURLException e) {
            taskMonitor.setStatus("You entered an invalid URL:  "
                    + bioPaxUrl + ".  Please check the URL and try again.");
        } catch (IOException e) {
            taskMonitor.setException(e,
                    "Could not load specified BioPAX File");
        } catch (JDOMException e) {
            taskMonitor.setException(e,
                    "Could not parse the specified BioPAX File.");
        } catch (NullPointerException e) {
            taskMonitor.setException(e,
                    "Could not load the specified BioPAX File");
        } catch (Throwable t) {
            taskMonitor.setException(t,
                    "Could not load the specified BioPAX File");
        } finally {
            taskMonitor.setPercentCompleted(100);
        }
    }

    /**
     * Loads the Specified BioPAX File.
     */
    protected void loadFile(Reader reader) throws IOException, JDOMException {

        // set status bar message
        if (pathwayName == null) {
            CytoscapeWrapper.setStatusBarMsg("Importing BioPAX File...");
        } else {
            CytoscapeWrapper.setStatusBarMsg("Importing Pathway:  "
                    + pathwayName + " ...");
        }

        //  Load up Data into BioPAX Util Object
        BioPaxUtil bpUtil = new BioPaxUtil(reader, taskMonitor);

        //  set network name - use pathway name
        String networkName = getPathwayName(bpUtil);
        networkName = (networkName == null) ? "Unknown" : networkName;

        //  Create CyNetwork Object via CyNetworkUtil
        cyNetwork = CyNetworkUtil.createCyNetwork(networkName);

        //  Map BioPAX Data to Cytoscape Nodes/Edges
        MapBioPaxToCytoscape mapper = new MapBioPaxToCytoscape
                (bpUtil, cyNetwork, taskMonitor);
        mapper.doMapping();

        //  Set-up the BioPax Visual Style
        VisualStyle bioPaxVisualStyle =
                BioPaxVisualStyleUtil.getBioPaxVisualStyle();
        VisualMappingManager manager =
                Cytoscape.getDesktop().getVizMapManager();
        manager.setVisualStyle(bioPaxVisualStyle);

        //  Conditionally Create Network View
        CyNetworkViewUtil.createNetworkView(cyNetwork, taskMonitor,
                true, true);

        //  Tell User what just happened
        String networkStats = CyNetworkUtil.getNetworkStats(cyNetwork,
                mapper.getWarningList());
        taskMonitor.setStatus(networkStats);
        CytoscapeWrapper.clearStatusBar();
    }

    /**
     * Grabs the pathway name from xml document.
     *
     * @param bpUtil - BioPaxUtil
     * @return - String
     */
    private String getPathwayName(BioPaxUtil bpUtil) {

        RdfQuery rdfQuery = new RdfQuery(bpUtil.getRdfResourceMap());

        // grab all complex components
        Element root = bpUtil.getRootElement();
        Element pathwayName = rdfQuery.getNode(root, "*/NAME");
        if (pathwayName != null && pathwayName.getTextNormalize().length() > 0) {
            return pathwayName.getTextNormalize();
        }

        // outta here
        return null;
    }
}

