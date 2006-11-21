/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
package org.cytoscape.coreplugin.cpath.task;

import org.cytoscape.coreplugin.cpath.model.*;
import org.cytoscape.coreplugin.cpath.ui.Console;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.view.CyNetworkView;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Task to Query cPath.
 *
 * @author Ethan Cerami.
 */
public class QueryCPathTask implements Task {
    private HashMap cyMap;
    private SearchRequest searchRequest;
    private SearchResponse searchResponse;
    private SearchBundle searchBundle;
    private SearchBundleList searchList;
    private Console console;
    private static final int DEFAULT_INCREMENT = 10;
    private static final int LARGER_INCREMENT = 50;
    private TaskMonitor taskMonitor;
    private boolean isInterrupted;

    /**
     * Constructor.
     *
     * @param cyMap         HashMap
     * @param searchRequest SearchRequest Object.
     * @param searchList    List of Search Bundles.
     * @param console       Console Object.
     */
    public QueryCPathTask(HashMap cyMap, SearchRequest searchRequest,
            SearchBundleList searchList, Console console) {
//        this.logToConsoleBold("Retrieving Data from cPath:  "
//                + searchRequest.toString() + "...");

//        PropertyManager pManager = PropertyManager.getInstance();
//        String url = pManager.getProperty(PropertyManager.CPATH_READ_LOCATION);
//        logToConsole("Connecting to cPath:  " + url);

        this.cyMap = cyMap;
        this.searchRequest = searchRequest;
        this.searchList = searchList;
        this.console = console;
    }

    public void halt () {
        isInterrupted = true;
    }

    public void setTaskMonitor (TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    public String getTitle () {
        return "Connecting to cPath:  " + searchRequest.toString();
    }

    /**
     * Executes Task
     */
    public void run () {
        //  Set Initial Messages
        taskMonitor.setPercentCompleted(-1);
        taskMonitor.setStatus("Connecting to cPath...");

        //  Get Property or use Default Location.
//        PropertyManager manager = PropertyManager.getInstance();
//        String cPathUrl = manager.getProperty
//                (PropertyManager.CPATH_READ_LOCATION);
//        ReadPsiFromCPath reader = new ReadPsiFromCPath(cPathUrl);
        searchResponse = new SearchResponse();
        try {
            ArrayList interactions = null;
            OrganismOption organism = searchRequest.getOrganism();

            int taxonomyId = organism.getTaxonomyId();
            if (organism == OrganismOption.ALL_ORGANISMS) {
//                taxonomyId = CPathProtocol.NOT_SPECIFIED;
            }
            int maxHits = searchRequest.getMaxHitsOption().getMaxHits();
//            getAllInteractions(reader, taxonomyId, maxHits);
//        } catch (EmptySetException e) {
//            console.logMessage("No Matching Results Found.  Please Try Again.");
//            searchResponse.setException(e);
//        } catch (DataServiceException e) {
//            searchResponse.setException(e);
//        } catch (MapperException e) {
//            searchResponse.setException(e);
        } catch (RuntimeException e) {
            searchResponse.setException(e);
        } catch (Exception e) {
            searchResponse.setException(e);
        } catch (Throwable e) {
            searchResponse.setException(e);
        } finally {
            searchBundle = new SearchBundle
                    ((SearchRequest) searchRequest.clone(), searchResponse);
            searchList.add(searchBundle);
            if (isInterrupted) {
//                logToConsole("Data Retrieval Cancelled by User.");
            }
        }
    }

    /**
     * Gets All Interactions.
     *
     * @param reader     Reader Service.
     * @param taxonomyId NCBI Taxonomy ID.
     */
//    private void getAllInteractions(ReadPsiFromCPath reader, int taxonomyId,
//            int maxHits)
//            throws DataServiceException, MapperException, InterruptedException {
//        searchResponse = new SearchResponse();
//        int totalNumInteractions = reader.getInteractionsCount
//                (searchRequest.getQuery(), taxonomyId);
//        logToConsole("Total Number of Matching Interactions:  "
//                + totalNumInteractions);
//
//        //  Retrieve Rest of Data
//        ArrayList interactions = new ArrayList();
//
//        int index = 0;
//        int endIndex = Math.min(maxHits, totalNumInteractions);
//
//        int increment = DEFAULT_INCREMENT;
//
//        if (maxHits > 100) {
//            increment = LARGER_INCREMENT;
//        }
//        while (index < endIndex && !isInterrupted) {
//            getInteractions(reader, taxonomyId, interactions, index, increment,
//                    endIndex);
//            index += increment;
//        }
//
//        searchResponse.setInteractions(interactions);
//        if (isInterrupted) {
//            throw new InterruptedException();
//        }
//        mapToGraph();
//    }

    /**
     * Iteratively Get Interactions from cPath.
     */
//    private void getInteractions(ReadPsiFromCPath reader, int taxonomyId,
//            ArrayList interactions, int startIndex, int increment,
//            int totalNumInteractions) throws DataServiceException {
//
//        int endIndex = Math.min(startIndex + increment, totalNumInteractions);
//        taskMonitor.setStatus("Getting Interactions:  " + startIndex
//                + " - " + endIndex + " of "
//                + totalNumInteractions);
//
//        Date start = new Date();
//        ArrayList currentList = reader.getInteractionsByKeyword
//                (searchRequest.getQuery(), taxonomyId,
//                        startIndex, increment);
//        interactions.addAll(currentList);
//        Date stop = new Date();
//        long interval = stop.getTime() - start.getTime();
//
//        //  Estimate Remaining Time
//        long totalTimeInRemaining =
//                CPathTimeEstimator.calculateEsimatedTimeRemaining(interval,
//                        startIndex, increment, totalNumInteractions);
//
//        logToConsole("Getting Interactions:  " + startIndex
//                + " - " + endIndex + " of "
//                + totalNumInteractions + " [OK]");

//TODO:FIX THIS
//        this.setMaxProgressValue(totalNumInteractions);
//        this.setProgressValue(startIndex + increment);
//        taskMonitor.setEstimatedTimeRemaining(totalTimeInRemaining);
    }

    /**
     * Maps New Interactions to Cytoscape Graph.
     *
     */
//    private void mapToGraph() throws MapperException {
//        taskMonitor.setPercentCompleted(-1);
//        String title = searchRequest.toString();
//        ArrayList interactions = searchResponse.getInteractions();
//
//        if (title.length() > 25) {
//            title = new String(title + "...");
//        }
//        CyNetwork cyNetwork = Cytoscape.createNetwork(title);
//        cyNetwork.setTitle(title);
//        searchResponse.setCyNetwork(cyNetwork);
//
//        //  The two lines below are a hack, and require some explanation.
//        //  When you create an empty CyNetwork object via:
//        //  Cytoscape.createNetwork (String title) method, a CyNetworkView
//        //  is automatically created.  That's because the code conditionally
//        //  creates a network based on the number of nodes in the network.
//        //  But, since this is an empty network with 0 nodes, a view is
//        //  always created.  The trick to preventing a network view
//        //  is to programmatically create a view directly, and then destroy it.
//        CyNetworkView networkView = Cytoscape.createNetworkView(cyNetwork);
//        Cytoscape.destroyNetworkView(networkView);
//
//        //  Map Interactions to Network
//        logToConsole("Mapping Data to Cytoscape Network");
//        taskMonitor.setStatus( "Mapping Data to Cytoscape Network.  Please wait.");
//   TODO:  FIX ALL CODE BELOW
//        MapPsiInteractionsToGraph mapper =
//                new MapPsiInteractionsToGraph(interactions, cyNetwork,
//                        MapInteractionsToGraph.MATRIX_VIEW);
//        mapper.setBaseTask((BaseTask) this);
//        mapper.doMapping();
//
//        //  Log Warnings to Console.
//        ArrayList warnings = mapper.getWarnings();
//        if (warnings.size() > 0) {
//            logToConsole("------------------------------------------");
//        }
//        for (int i = 0; i < warnings.size(); i++) {
//            int counter = i + 1;
//            logToConsole("Warning # " + counter
//                    + ":  " + (String) warnings.get(i));
//            logToConsole("------------------------------------------");
//        }
//
//        //  Update CyMap
//        HashMap map = mapper.getCyMap();
//        cyMap.putAll(map);

        //  Conditionally Create a View, based on Number of Nodes.
        //  GetViewThreshold is settable by the End User.
//        logToConsole("Total Number of Nodes in Network:  "
//                + cyNetwork.getNodeCount());
//        logToConsole("Total Number of Edges in Network:  "
//                + cyNetwork.getEdgeCount());
//        int threshold = Integer.parseInt(CytoscapeInit.getProperties().getProperty
//                ("viewThreshold", "5000"));
//        if (cyNetwork.getNodeCount() < threshold) {
//            logToConsole("Your Network is Under "
//                    + threshold
//                    + " nodes --> a Cytoscape View  will be "
//                    + "automatically created.");
//            taskMonitor.setStatus("Creating Network View.  Please wait.");
//            CyNetworkView view = Cytoscape.createNetworkView(cyNetwork);
//            searchResponse.setCyNetworkView(view);
//            taskMonitor.setStatus("Applying Visual Styles.");
//            Cytoscape.getVisualMappingManager().applyAppearances();
//        } else {
//            logToConsole("Your Network is Over " + threshold
//                    + " nodes --> a Cytoscape View  will not be "
//                    + "automatically created.");
//        }
//}

    /**
     * Logs to Console by queing an event to the Event-Dispatch Thread.
     *
     * @param msg Message to Log.
     */
//    private void logToConsole(final String msg) {
//        Runnable runnable = new Runnable() {
//            public void run() {
//                console.logMessage(msg);
//            }
//        };
//        SwingUtilities.invokeLater(runnable);
//    }

    /**
     * Logs to Console by queing an event to the Event-Dispatch Thread.
     *
     * @param msg Message to Log.
     */
//    private void logToConsoleBold(final String msg) {
//        Runnable runnable = new Runnable() {
//            public void run() {
//                console.logMessageBold(msg);
//            }
//        };
//        SwingUtilities.invokeLater(runnable);
//    }
//}
