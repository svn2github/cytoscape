/*
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.coreplugin.cpath.task;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.readers.GraphReader;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.coreplugin.cpath.model.CPathException;
import org.cytoscape.coreplugin.cpath.model.EmptySetException;
import org.cytoscape.coreplugin.cpath.model.OrganismOption;
import org.cytoscape.coreplugin.cpath.model.SearchBundle;
import org.cytoscape.coreplugin.cpath.model.SearchBundleList;
import org.cytoscape.coreplugin.cpath.model.SearchRequest;
import org.cytoscape.coreplugin.cpath.model.SearchResponse;
import org.cytoscape.coreplugin.cpath.protocol.CPathProtocol;
import org.cytoscape.coreplugin.cpath.ui.Console;
import org.cytoscape.coreplugin.cpath.util.CPathProperties;
import org.cytoscape.layout.CyLayoutAlgorithm;
import org.cytoscape.view.GraphView;

import javax.swing.*;
import java.io.IOException;
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
    private static final int LARGER_INCREMENT = 100;
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
    public QueryCPathTask (HashMap cyMap, SearchRequest searchRequest,
            SearchBundleList searchList, Console console) {
        this.logToConsole("Retrieving Data from cPath:  "
                + searchRequest.toString() + "...", "bold");

        String url = CPathProperties.getCPathUrl();
        logToConsole("Connecting to cPath:  " + url);

        this.cyMap = cyMap;
        this.searchRequest = searchRequest;
        this.searchList = searchList;
        this.console = console;
    }

    /**
     * Halts cPath Task.
     */
    public void halt () {
        isInterrupted = true;
    }

    /**
     * Sets Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
     * @throws IllegalThreadStateException Illegal Thread State.
     */
    public void setTaskMonitor (TaskMonitor taskMonitor) throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets Task Title.
     *
     * @return task title.
     */
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
        searchResponse = new SearchResponse();
        try {
            OrganismOption organism = searchRequest.getOrganism();
            int taxonomyId = organism.getTaxonomyId();
            if (organism == OrganismOption.ALL_ORGANISMS) {
                taxonomyId = CPathProtocol.NOT_SPECIFIED;
            }
            int maxHits = searchRequest.getMaxHitsOption().getMaxHits();
            getAllInteractions(taxonomyId, maxHits);
            taskMonitor.setPercentCompleted(100);
        } catch (EmptySetException e) {
            this.logToConsole ("No Matching Results Found for:  "
                    + searchRequest.getQuery() + ".  Please Try Again.", "red");
            taskMonitor.setPercentCompleted(100);
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
                logToConsole("Data Retrieval Cancelled by User.");
            }
        }
    }

    /**
     * Gets All Interactions.
     */
    private void getAllInteractions (int taxonomyId, int maxHits)
            throws InterruptedException, CPathException, EmptySetException, IOException {
        searchResponse = new SearchResponse();

        //  First, determine how many interactions we have in total
        ReadPsiFromCPath reader = new ReadPsiFromCPath();
        int totalNumInteractions = reader.getInteractionsCount
                (searchRequest.getQuery(), taxonomyId);
        logToConsole("Total Number of Matching Interactions:  "
                + totalNumInteractions);

        //  0% Complete
        taskMonitor.setPercentCompleted(0);

        //  Retrieve the interactions
        int index = 0;
        int endIndex = Math.min(maxHits, totalNumInteractions);
        int increment = DEFAULT_INCREMENT;
        if (maxHits > 100) {
            increment = LARGER_INCREMENT;
        }

        //  Create CyNetwork
        String title = searchRequest.toString();

        if (title.length() > 25) {
           	title = title.substring(0,24) + "..."; 
        }

        //  Create Network w/o view
        CyNetwork cyNetwork = Cytoscape.createNetwork(title, false);

        GraphReader graphReader = null;
        while (index < endIndex && !isInterrupted) {
            graphReader = getInteractions(taxonomyId, index, increment, endIndex);
            graphReader.read();
            addToCyNetwork(graphReader, cyNetwork);
            index += increment;
            if (isInterrupted) {
                throw new InterruptedException();
            }

        }

        GraphView networkView = createNetworkView(cyNetwork);
        if (networkView != null) {
            CyLayoutAlgorithm layoutAlgorithm = graphReader.getLayoutAlgorithm();
            if (layoutAlgorithm != null) {
                layoutAlgorithm.doLayout(networkView);
            }
        }
    }

    /**
     * Iteratively Get Interactions from cPath.
     */
    private GraphReader getInteractions (int taxonomyId, int startIndex, int increment,
            int totalNumInteractions) throws CPathException, EmptySetException {

        ReadPsiFromCPath reader = new ReadPsiFromCPath();
        int endIndex = Math.min(startIndex + increment, totalNumInteractions);
        taskMonitor.setStatus("Getting Interactions:  " + startIndex
                + " - " + endIndex + " of "
                + totalNumInteractions);

        GraphReader graphReader = reader.getInteractionsByKeyword
                (searchRequest.getQuery(), taxonomyId,
                        startIndex, increment);

        //logToConsole("Getting Interactions:  " + startIndex
        //        + " - " + endIndex + " of "
        //        + totalNumInteractions + " [OK]");

        double percentCompleted = (startIndex + increment) / (double) totalNumInteractions;
        int percent = (int) (percentCompleted * 100.0);
        if (percent > 100) {
            percent = 100;
        }
        taskMonitor.setPercentCompleted(percent);
        return graphReader;
    }

    private void addToCyNetwork (GraphReader reader, CyNetwork cyNetwork) {
        //  Add new nodes/edges to network
        int nodeIndices[] = reader.getNodeIndicesArray();
        int edgeIndices[] = reader.getEdgeIndicesArray();
        for (int i = 0; i < nodeIndices.length; i++) {
            cyNetwork.addNode(nodeIndices[i]);
        }
        for (int i = 0; i < edgeIndices.length; i++) {
            cyNetwork.addEdge(edgeIndices[i]);
        }
    }

    private GraphView createNetworkView (CyNetwork cyNetwork) {
        //  Conditionally Create a View, based on Number of Nodes.
        //  GetViewThreshold is settable by the End User.
        logToConsole("Total Number of Nodes in Network:  "
                + cyNetwork.getNodeCount());
        logToConsole("Total Number of Edges in Network:  "
                + cyNetwork.getEdgeCount());
        int threshold = Integer.parseInt(CytoscapeInit.getProperties().getProperty
                ("viewThreshold", "5000"));
        GraphView view = null;
        if (cyNetwork.getNodeCount() < threshold) {
            logToConsole("Your Network is Under "
                    + threshold
                    + " nodes --> a Cytoscape View  will be "
                    + "automatically created.");
            taskMonitor.setStatus("Creating Network View.  Please wait.");
            view = Cytoscape.createNetworkView(cyNetwork);
            searchResponse.setGraphView(view);
            taskMonitor.setStatus("Applying Visual Styles.");
            Cytoscape.getVisualMappingManager().applyAppearances();
        } else {
            logToConsole("Your Network is Over " + threshold
                    + " nodes --> a Cytoscape View  will not be "
                    + "automatically created.");
        }
        return view;
    }

    /**
     * Logs to Console by queing an event to the Event-Dispatch Thread.
     *
     * @param msg Message to Log.
     */
    private void logToConsole (final String msg) {
        Runnable runnable = new Runnable() {
            public void run () {
                console.logMessage(msg);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }

    /**
     * Logs to Console by queing an event to the Event-Dispatch Thread.
     *
     * @param msg Message to Log.
     */
    private void logToConsole (final String msg, final String style) {
        Runnable runnable = new Runnable() {
            public void run () {
                console.logMessage(msg, style);
            }
        };
        SwingUtilities.invokeLater(runnable);
    }
}
