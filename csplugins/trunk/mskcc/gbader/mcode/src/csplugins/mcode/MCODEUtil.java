package csplugins.mcode;

import cytoscape.CyNetwork;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;
import phoebe.PGraphView;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 * * User: Gary Bader
 * * Date: Jun 25, 2004
 * * Time: 7:00:13 PM
 * * Description: Utilities for MCODE
 */

/**
 * Utilities for MCODE
 */
public class MCODEUtil {

    private static boolean INTERRUPTED = false;
    /**
     * Convert a network to an image.  This is used by the result dialog code.
     *
     * @param loader results panel
     * @param cluster Input network to convert to an image
     * @param height  Height that the resulting image should be
     * @param width   Width that the resulting image should be
     * @return The resulting image
     */
    public static Image convertNetworkToImage(MCODELoader loader, MCODECluster cluster, int height, int width, SpringEmbeddedLayouter layouter, boolean layoutNecessary) {
        PGraphView view;
        Image image;
        //boolean completedSuccessfully = true;

        //Progress reporters.  There are three basic tasks, the progress of each is calculated and then combined
        //using the respective weighting to get an overall progress
        //global progress
        int weightSetupNodes = 20;  // setting up the nodes and edges is deemed as 25% of the whole task
        int weightSetupEdges = 5;
        int weightLayout = 75;      // layout it is 70%
        int goalTotal = weightSetupNodes + weightSetupEdges;
        if (layoutNecessary) {
            goalTotal += weightLayout;
        }
        double progress = 0;        // keeps track of progress as a percent of the totalGoal

        view = new PGraphView(cluster.getGPCluster());

        //TODO optionally apply a visual style here instead of doing this manually - visual style calls init code that might not be called manually
        for (Iterator in = view.getNodeViewsIterator(); in.hasNext();) {
            if (INTERRUPTED) {
                resetLoading();
                return null;
            }
            NodeView nv = (NodeView) in.next();

            //Otherwise we give it new generic data
            String label = nv.getNode().getIdentifier();
            nv.getLabel().setText(label);
            nv.setWidth(40);
            nv.setHeight(40);
            if (cluster.getSeedNode().intValue() == nv.getRootGraphIndex()) {
                nv.setShape(NodeView.RECTANGLE);
            } else {
                nv.setShape(NodeView.ELLIPSE);
            }
            nv.setUnselectedPaint(Color.RED);
            nv.setBorderPaint(Color.BLACK);

            //First we check if the MCODECluster already has a node view of this node
            if (cluster.getPGView() != null && cluster.getPGView().getNodeView(nv.getNode().getRootGraphIndex()) != null) {
                //If it does, then we take the layout position that was already generated for it
                nv.setXPosition(cluster.getPGView().getNodeView(nv.getNode().getRootGraphIndex()).getXPosition());
                nv.setYPosition(cluster.getPGView().getNodeView(nv.getNode().getRootGraphIndex()).getYPosition());
            } else {
                //Otherwise, randomize node positions before layout so that they don't all layout in a line
                //(so they don't fall into a local minimum for the SpringEmbedder)
                //If the SpringEmbedder implementation changes, this code may need to be removed
                nv.setXPosition(view.getCanvas().getLayer().getGlobalFullBounds().getWidth() * Math.random());
                //height is small for many default drawn graphs, thus +100
                nv.setYPosition((view.getCanvas().getLayer().getGlobalFullBounds().getHeight() + 100) * Math.random());
                if (!layoutNecessary) {
                    goalTotal += weightLayout;
                    progress /= (goalTotal / (goalTotal - weightLayout));
                    layoutNecessary = true;
                }
            }
            if (loader != null) {
                progress += 100.0 * (1.0 / (double) view.nodeCount()) * ((double) weightSetupNodes / (double) goalTotal);
                loader.setProgress((int) progress, "Setup: nodes");
            }
        }

        for (Iterator ie = view.getEdgeViewsIterator(); ie.hasNext();) {
            if (INTERRUPTED) {
                resetLoading();
                return null;
            }
            EdgeView ev = (EdgeView) ie.next();
            ev.setUnselectedPaint(Color.BLUE);
            ev.setTargetEdgeEnd(EdgeView.BLACK_ARROW);
            ev.setTargetEdgeEndPaint(Color.CYAN);
            ev.setSourceEdgeEndPaint(Color.CYAN);
            ev.setStroke(new BasicStroke(5f));

            if (loader != null) {
                progress += 100.0 * (1.0 / (double) view.edgeCount()) * ((double) weightSetupEdges / (double) goalTotal);
                loader.setProgress((int) progress, "Setup: edges");
            }
        }
        if (layoutNecessary) {
            if (layouter == null) {
                layouter = new SpringEmbeddedLayouter();
            }
            layouter.setGraphView(view);
            if (!layouter.doLayout(weightLayout, goalTotal, progress, loader)) {
                resetLoading();
                return null;
            }
        }

        image = view.getCanvas().getLayer().toImage(width, height, null);

        double largestSide = view.getCanvas().getLayer().getFullBounds().width;
        if (view.getCanvas().getLayer().getFullBounds().height > largestSide) {
            largestSide = view.getCanvas().getLayer().getFullBounds().height;
        }
        if (view.getNodeViewCount() >= 1) {
            cluster.setPGView(view);
        }
        if (loader != null) {
            loader.loaded();
        }

        return (image);
    }

    public static void interruptLoading() {
        INTERRUPTED = true;
    }

    public static void resetLoading() {
        INTERRUPTED = false;
    }

    /**
     * Convert a cluster generated by MCODE to a Cytoscape network.
     *
     * @param cluster       The MCODE cluster
     * @param sourceNetwork The original network that contained the cluster
     * @return The network representing the cluster
     */
    public static GraphPerspective convertClusterToNetwork(ArrayList cluster, CyNetwork sourceNetwork) {
        GraphPerspective gpCluster;
        int[] clusterArray = convertIntArrayList2array(cluster);
        gpCluster = sourceNetwork.createGraphPerspective(clusterArray);
        return gpCluster;
    }

    /**
     * Converts a list of MCODE generated clusters to a list of networks that is sorted by the score of the cluster
     *
     * @param clusters   List of MCODE generated clusters
     * @return A sorted array of cluster objects based on cluster score.
     */
    public static MCODECluster[] sortClusters(MCODECluster[] clusters) {
        Arrays.sort(clusters, new Comparator() {
            //sorting clusters by decreasing score
            public int compare(Object o1, Object o2) {
                double d1 = ((MCODECluster) o1).getClusterScore();
                double d2 = ((MCODECluster) o2).getClusterScore();
                if (d1 == d2) {
                    return 0;
                } else if (d1 < d2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return clusters;
    }

    /**
     * A utility method to convert ArrayList to int[]
     *
     * @param alInput ArrayList input
     * @return int array
     */
    private static int[] convertIntArrayList2array(ArrayList alInput) {
        int[] outputNodeIndices = new int[alInput.size()];
        int j = 0;
        for (Iterator i = alInput.iterator(); i.hasNext(); j++) {
            outputNodeIndices[j] = ((Integer) i.next()).intValue();
        }
        return (outputNodeIndices);
    }

    /**
     * Utility method to get the names of all the nodes in a GraphPerspective
     *
     * @param gpInput The input graph perspective to get the names from
     * @return A concatenated set of all node names (separated by a comma)
     */
    public static StringBuffer getNodeNameList(GraphPerspective gpInput) {
        Iterator i = gpInput.nodesIterator();
        StringBuffer sb = new StringBuffer();
        while (i.hasNext()) {
            Node node = (Node) i.next();
            sb.append(node.getIdentifier());
            if (i.hasNext()) {
                sb.append(", ");
            }
        }
        return (sb);
    }

    /**
     * Save MCODE results to a file
     *
     * @param alg       The algorithm instance containing parameters, etc.
     * @param clusters  The list of clusters
     * @param network   The network source of the clusters
     * @param fileName  The file name to write to
     * @return True if the file was written, false otherwise
     */
    public static boolean saveMCODEResults(MCODEAlgorithm alg, MCODECluster[] clusters, CyNetwork network, String fileName) {
        if (alg == null || clusters == null || network == null || fileName == null) {
            return false;
        }
        String lineSep = System.getProperty("line.separator");
        try {
            File file = new File(fileName);
            FileWriter fout = new FileWriter(file);
            //write header
            fout.write("MCODE Plugin Results" + lineSep);
            fout.write("Date: " + DateFormat.getDateTimeInstance().format(new Date()) + lineSep + lineSep);
            fout.write("Parameters:" + lineSep + alg.getParams().toString() + lineSep);
            fout.write("Cluster	Score (Density*#Proteins)\tProteins\tInteractions\tProtein names" + lineSep);
            //get GraphPerspectives for all clusters, score and rank them
            //convert the ArrayList to an array of GraphPerspectives and sort it by cluster score
            //GraphPerspective[] gpClusterArray = MCODEUtil.convertClusterListToSortedNetworkList(clusters, network, alg);
            for (int i = 0; i < clusters.length; i++) {
                GraphPerspective gpCluster = clusters[i].getGPCluster();
                fout.write((i + 1) + "\t"); //rank
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(3);
                fout.write(nf.format(clusters[i].getClusterScore()) + "\t");
                //cluster size - format: (# prot, # intx)
                fout.write(gpCluster.getNodeCount() + "\t");
                fout.write(gpCluster.getEdgeCount() + "\t");
                //create a string of node names - this can be long
                fout.write(getNodeNameList(gpCluster).toString() + lineSep);
            }
            fout.close();
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.toString(),
                    "Error Writing to \"" + fileName + "\"",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
