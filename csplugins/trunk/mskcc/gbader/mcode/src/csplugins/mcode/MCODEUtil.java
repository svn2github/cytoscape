package csplugins.mcode;

import cytoscape.CyNetwork;
import cytoscape.util.GinyFactory;
import giny.model.GraphPerspective;
import giny.util.SpringEmbeddedLayouter;
import giny.view.EdgeView;
import giny.view.NodeView;
import phoebe.PGraphView;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
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
public class MCODEUtil {
    public static Image convertNetworkToImage(GraphPerspective gpInput, int height, int width) {
        PGraphView view;
        SpringEmbeddedLayouter lay;
        Image image;
        view = (PGraphView) GinyFactory.createGraphView(gpInput);
        //TODO optionally apply a visual style here instead of doing this manually - visual style calls init code that might not be called manually
        for (Iterator in = view.getNodeViewsIterator(); in.hasNext();) {
            NodeView nv = (NodeView) in.next();
            String label = nv.getNode().getIdentifier();
            nv.getLabel().setText(label);
            nv.setWidth(40);
            nv.setHeight(40);
            nv.setShape(NodeView.ELLIPSE);
            nv.setUnselectedPaint(Color.red);
            nv.setBorderPaint(Color.black);
            //randomize node positions before layout so that they don't all layout in a line
            //(so they don't fall into a local minimum for the SpringEmbedder)
            //If the SpringEmbedder implementation changes, this code may need to be removed
            nv.setXPosition(view.getCanvas().getLayer().getGlobalFullBounds().getWidth() * Math.random());
            //height is small for many default drawn graphs, thus +100
            nv.setYPosition((view.getCanvas().getLayer().getGlobalFullBounds().getHeight() + 100) * Math.random());
        }
        for (Iterator ie = view.getEdgeViewsIterator(); ie.hasNext();) {
            EdgeView ev = (EdgeView) ie.next();
            ev.setUnselectedPaint(Color.blue);
            ev.setTargetEdgeEnd(EdgeView.BLACK_ARROW);
            ev.setTargetEdgeEndPaint(Color.CYAN);
            ev.setSourceEdgeEndPaint(Color.CYAN);
            ev.setStroke(new BasicStroke(5f));
        }
        lay = new SpringEmbeddedLayouter(view);
        lay.doLayout();
        image = view.getCanvas().getLayer().toImage(width, height, null);
        double largestSide = view.getCanvas().getLayer().getFullBounds().width;
        if (view.getCanvas().getLayer().getFullBounds().height > largestSide) {
            largestSide = view.getCanvas().getLayer().getFullBounds().height;
        }
        return(image);
    }

    public static GraphPerspective convertComplexToNetwork(ArrayList complex, CyNetwork sourceNetwork) {
        GraphPerspective gpComplex;
        int[] complexArray = convertIntArrayList2array(complex);
        gpComplex = sourceNetwork.createGraphPerspective(complexArray);
        return gpComplex;
    }

    public static GraphPerspective[] convertComplexListToSortedNetworkList(ArrayList complexList, CyNetwork sourceNetwork, final MCODEAlgorithm alg) {
        GraphPerspective gpComplexArray[] = new GraphPerspective[complexList.size()];
        for (int i = 0; i < complexList.size(); i++) {
            gpComplexArray[i] = convertComplexToNetwork((ArrayList) complexList.get(i), sourceNetwork);
        }
        Arrays.sort(gpComplexArray, new Comparator() {
            //sorting GraphPerpectives by decreasing score
            public int compare(Object o1, Object o2) {
                double d1 = alg.scoreComplex((GraphPerspective) o1);
                double d2 = alg.scoreComplex((GraphPerspective) o2);
                if (d1 == d2) {
                    return 0;
                } else if (d1 < d2) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        return(gpComplexArray);
    }

    //Utility method - convert ArrayList to int[]
    private static int[] convertIntArrayList2array(ArrayList alInput) {
        int[] outputNodeIndices = new int[alInput.size()];
        int j = 0;
        for (Iterator i = alInput.iterator(); i.hasNext(); j++) {
            outputNodeIndices[j] = ((Integer) i.next()).intValue();
        }
        return (outputNodeIndices);
    }
}
