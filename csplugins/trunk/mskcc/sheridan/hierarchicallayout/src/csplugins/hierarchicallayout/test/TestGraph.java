/** Copyright (c) 2004 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Robert Sheridan
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
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package csplugins.hierarchicallayout.test;

import junit.framework.TestCase;
import csplugins.hierarchicallayout.*;

/**
 * Tests the Graph class.
 *
 * @author Robert Sheridan
 */
public class TestGraph extends TestCase {
	private final static Edge edge0_1 = new Edge(0,1);
	private final static Edge edge1_2 = new Edge(1,2);
	private final static Edge edge2_0 = new Edge(2,0);
    private final static Edge edge2_2 = new Edge(2,2);
    private final static Edge edge0_7 = new Edge(0,7);
    private final static Edge edge7_13 = new Edge(7,13);
    private final static Edge edge13_6 = new Edge(13,6);
    private final static Edge edge13_8= new Edge(13,8);
    private final static Edge edge8_1 = new Edge(8,1);
    private final static Edge edge0_6 = new Edge(0,6);
    private final static Edge edge1_6 = new Edge(1,6);
    private final static Edge edge1_12 = new Edge(1,12);
    private final static Edge edge12_5 = new Edge(12,5);
    private final static Edge edge6_5 = new Edge(6,5);
    private final static Edge edge5_15 = new Edge(5,15);
    private final static Edge edge15_0 = new Edge(15,0);
    private final static Edge edge9_9 = new Edge(9,9);
    private final static Edge edge9_3 = new Edge(9,3);
    private final static Edge edge3_3 = new Edge(3,3);
    private final static Edge edge11_9 = new Edge(11,9);
    private final static Edge edge14_11 = new Edge(14,11);
    private final static Edge edge16_11 = new Edge(16,11);
    private final static Edge edge3_14 = new Edge(3,14);
    private final static Edge edge10_2 = new Edge(10,2);
    private final static Edge edge3_16 = new Edge(3,16);
    private final static Edge edge1_0 = new Edge(1,0);
    private final static Edge graph0edges[] = {};
	private final static Edge graph1edges[] = {};
	private final static Edge graph2edges[] = {edge0_1,edge0_1};
	private final static Edge graph3edges[] = {edge0_1,edge1_2,edge2_0,edge2_2};
    private final static Edge graph4edges[] = {edge0_7,edge7_13,edge13_6,edge13_8,edge8_1,
                                               edge0_6,edge1_6,edge1_12,edge12_5,edge6_5,
                                               edge5_15,edge15_0,edge9_9,edge9_3,edge3_3,
                                               edge11_9,edge14_11,edge16_11,edge3_14,edge3_14,
                                               edge10_2,edge2_2,edge3_14,edge3_16};
    private final static Edge graph5edges[] = {edge1_0,edge1_0,edge0_1,edge1_0};
	private final static Graph graph0 = new Graph(0, graph0edges);
	private final static Graph graph1 = new Graph(1, graph1edges);
	private final static Graph graph2 = new Graph(2, graph2edges);
	private final static Graph graph3 = new Graph(3, graph3edges);
    private final static Graph graph4 = new Graph(17, graph4edges);
    private final static Graph graph5 = new Graph(2, graph5edges);
    /**
     * Test the Graph constructor on size 0, 1, 2 graphs
     * @throws Exception All Exceptions.
     */
    public void testConstructor() throws Exception {
        if (graph0.getNodecount() != 0 || graph0.getEdgecount() != 0) {
            fail("graph0 construction error");
        }
        if (graph1.getNodecount() != 1 || graph1.getEdgecount() != 0) {
            fail("graph1 construction error");
        }
        if (graph2.getNodecount() != 2 || graph2.getEdgecount() != 2
                || !graph2.hasEdge(0,1)) {
            fail("graph2 construction error");
        }
        if (graph3.getNodecount() != 3 || graph3.getEdgecount() != 4
                || !graph3.hasEdge(0,1)
            || !graph3.hasEdge(1,2) || !graph3.hasEdge(2,0) || !graph3.hasEdge(2,2) ) {
            fail("graph3 construction error");
        }
        if (graph4.getNodecount() != 17 || graph4.getEdgecount() != 24
                || !graph4.hasEdge(0,7)
                || !graph4.hasEdge(0,7) || !graph4.hasEdge(7,13)
                || !graph4.hasEdge(13,6) || !graph4.hasEdge(13,8)
                || !graph4.hasEdge(8,1) || !graph4.hasEdge(0,6)
                || !graph4.hasEdge(1,6) || !graph4.hasEdge(1,12)
                || !graph4.hasEdge(12,5) || !graph4.hasEdge(6,5)
                || !graph4.hasEdge(5,15) || !graph4.hasEdge(15,0)
                || !graph4.hasEdge(9,9) || !graph4.hasEdge(9,3)
                || !graph4.hasEdge(3,3) || !graph4.hasEdge(11,9)
                || !graph4.hasEdge(14,11) || !graph4.hasEdge(16,11)
                || !graph4.hasEdge(3,14) || !graph4.hasEdge(10,2)
                || !graph4.hasEdge(2,2) || !graph4.hasEdge(3,16)) {
            fail("graph4 construction error");
        }
    }

    /**
     * Test component index
     * @throws Exception All Exceptions.
     */
    public void testComponentIndex() throws Exception {
        int cI[];
        cI = graph0.componentIndex();
        if (cI.length != 0) {
            fail("graph0 component index error");
        }
        cI = graph1.componentIndex();
        if (cI.length != 1 || cI[0] != 0) {
            fail("graph1 component index error");
        }
        cI = graph2.componentIndex();
        if (cI.length != 2 || cI[0] != 0 || cI[1] != 0) {
            fail("graph2 component index error");
        }
        cI = graph3.componentIndex();
        if (cI.length != 3 || cI[0] != 0 || cI[1] != 0
            || cI[2] != 0) {
            fail("graph3 component index error");
        }
        cI = graph4.componentIndex();
        if (cI.length != 17 || cI[0] != 0 || cI[1] != 0
                || cI[2] != 1 || cI[3] != 2 || cI[4] != 3 || cI[5] != 0
                || cI[6] != 0 || cI[7] != 0 || cI[8] != 0 || cI[9] != 2
                || cI[10] != 1 || cI[11] != 2 || cI[12] != 0 || cI[13] != 0
                || cI[14] != 2 || cI[15] != 0 || cI[16] != 2) {
            fail("graph4 component index error");
        }
    }

    /**
     * Test partition
     * @throws Exception All Exceptions.
     */
    public void testPartition() throws Exception {
        int cI[];
        int renumbered[];
        Graph g[];
        cI = graph0.componentIndex();
        renumbered = new int[cI.length];
        g = graph0.partition(cI,renumbered);
        if (g.length != 0) {
            fail("graph0 partition error");
        }
        cI = graph1.componentIndex();
        renumbered = new int[cI.length];
        g = graph1.partition(cI,renumbered);
        if (g.length != 1 || g[0].getNodecount() != 1
                || g[0].getEdgecount() != 0
            || renumbered[0] != 0) {
            fail("graph1 partition error");
        }
        cI = graph2.componentIndex();
        renumbered = new int[cI.length];
        g = graph2.partition(cI,renumbered);
        if (g.length != 1 || g[0].getNodecount() != 2
                || g[0].getEdgecount() != 2
            || renumbered[0] != 0
            || renumbered[1] != 1 || !g[0].hasEdge(0,1)) {
            fail("graph2 partition error");
        }
        cI = graph3.componentIndex();
        renumbered = new int[cI.length];
        g = graph3.partition(cI,renumbered);
        if (g.length != 1 || g[0].getNodecount() != 3
                || g[0].getEdgecount() != 4
            || renumbered[0] != 0
            || renumbered[1] != 1 || renumbered[2] != 2
            || !g[0].hasEdge(0,1) || !g[0].hasEdge(1,2)
            || !g[0].hasEdge(2,0) || !g[0].hasEdge(2,2)) {
            fail("graph3 partition error");
        }
        cI = graph4.componentIndex();
        renumbered = new int[cI.length];
        g = graph4.partition(cI,renumbered);
        if (g.length != 4 || g[0].getNodecount() != 9
                || g[0].getEdgecount() != 12 || g[1].getNodecount() != 2
                || g[1].getEdgecount() != 2|| g[2].getNodecount() != 5
                || g[2].getEdgecount() != 10 || g[3].getNodecount() != 1
                || g[3].getEdgecount() != 0  || renumbered[0] != 0
                || renumbered[1] != 1 || renumbered[2] != 0
                || renumbered[3] != 0 || renumbered[4] != 0
                || renumbered[5] != 2 || renumbered[6] != 3
                || renumbered[7] != 4 || renumbered[8] != 5
                || renumbered[9] != 1 || renumbered[10] != 1
                || renumbered[11] != 2 || renumbered[12] != 6
                || renumbered[13] != 7 || renumbered[14] != 3
                || renumbered[15] != 8 || renumbered[16] != 4
                || !g[0].hasEdge(4,7) || !g[0].hasEdge(0,4)
                || !g[0].hasEdge(0,3) || !g[0].hasEdge(8,0)
                || !g[0].hasEdge(2,8) || !g[0].hasEdge(7,3)
                || !g[0].hasEdge(7,5) || !g[0].hasEdge(3,2)
                || !g[0].hasEdge(1,3) || !g[0].hasEdge(5,1)
                || !g[0].hasEdge(6,2) || !g[0].hasEdge(1,6)
                || !g[2].hasEdge(2,1) || !g[2].hasEdge(0,3)
                || !g[2].hasEdge(1,1) || !g[2].hasEdge(0,4)
                || !g[2].hasEdge(0,0) || !g[2].hasEdge(4,2)
                || !g[2].hasEdge(1,0) || !g[2].hasEdge(3,2)
                || !g[1].hasEdge(1,0) || !g[1].hasEdge(0,0)) {
            fail("graph4 partition error");
        }
    }

    /**
     * Test multi-edge elimination
     * @throws Exception All Exceptions.
     */
    public void testMultiEdgeElimination() throws Exception {
        Graph g;
        g = graph0.getGraphWithoutMultipleEdges();
        if (g.getNodecount() != 0 || g.getEdgecount() != 0) {
            fail("graph0 multiedge elimination error");
        }
        g = graph1.getGraphWithoutMultipleEdges();
        if (g.getNodecount() != 1 || g.getEdgecount() != 0) {
            fail("graph1 multiedge elimination error");
        }
        g = graph2.getGraphWithoutMultipleEdges();
        if (g.getNodecount() != 2 || g.getEdgecount() != 1 || !g.hasEdge(0,1)) {
            fail("graph2 multiedge elimination error");
        }
        g = graph3.getGraphWithoutMultipleEdges();
        if (g.getNodecount() != 3 || g.getEdgecount() != 4 || !g.hasEdge(0,1)
            || !g.hasEdge(1,2) || !g.hasEdge(2,0) || !g.hasEdge(2,2) ) {
            fail("graph3 multiedge elimination error");
        }
        g = graph4.getGraphWithoutMultipleEdges();
        if (g.getNodecount() != 17 || g.getEdgecount() != 22 || !g.hasEdge(0,7)
                || !g.hasEdge(0,7) || !g.hasEdge(7,13)
                || !g.hasEdge(13,6) || !g.hasEdge(13,8)
                || !g.hasEdge(8,1) || !g.hasEdge(0,6)
                || !g.hasEdge(1,6) || !g.hasEdge(1,12)
                || !g.hasEdge(12,5) || !g.hasEdge(6,5)
                || !g.hasEdge(5,15) || !g.hasEdge(15,0)
                || !g.hasEdge(9,9) || !g.hasEdge(9,3)
                || !g.hasEdge(3,3) || !g.hasEdge(11,9)
                || !g.hasEdge(14,11) || !g.hasEdge(16,11)
                || !g.hasEdge(3,14) || !g.hasEdge(10,2)
                || !g.hasEdge(2,2) || !g.hasEdge(3,16)) {
            fail("graph4 multiedge elimination error");
        }
     }

    /**
     * Test cycle elimination
     * @throws Exception All Exceptions.
     */
    public void testOneOrTwoCycleElimination() throws Exception {
        Graph g;
        g = graph0.getGraphWithoutOneOrTwoCycles();
        if (g.getNodecount() != 0 || g.getEdgecount() != 0) {
            fail("graph0 1/2 cycle elimination error");
        }
        g = graph1.getGraphWithoutOneOrTwoCycles();
        if (g.getNodecount() != 1 || g.getEdgecount() != 0) {
            fail("graph1 1/2 cycle elimination error");
        }
        g = graph2.getGraphWithoutOneOrTwoCycles();
        if (g.getNodecount() != 2 || g.getEdgecount() != 2
                || !g.hasEdge(0,1)) {
            fail("graph2 1/2 cycle elimination error");
        }
        g = graph3.getGraphWithoutOneOrTwoCycles();
        if (g.getNodecount() != 3 || g.getEdgecount() != 3
                || !g.hasEdge(0,1)
            || !g.hasEdge(1,2) || !g.hasEdge(2,0) || g.hasEdge(2,2) ) {
            fail("graph3 1/2 cycle elimination error");
        }
        g = graph4.getGraphWithoutOneOrTwoCycles();
        if (g.getNodecount() != 17 || g.getEdgecount() != 21
                || !g.hasEdge(0,7)
                || !g.hasEdge(0,7) || !g.hasEdge(7,13)
                || !g.hasEdge(13,6) || !g.hasEdge(13,8)
                || !g.hasEdge(8,1) || !g.hasEdge(0,6)
                || !g.hasEdge(1,6) || !g.hasEdge(1,12)
                || !g.hasEdge(12,5) || !g.hasEdge(6,5)
                || !g.hasEdge(5,15) || !g.hasEdge(15,0)
                || g.hasEdge(9,9) || !g.hasEdge(9,3)
                || g.hasEdge(3,3) || !g.hasEdge(11,9)
                || !g.hasEdge(14,11) || !g.hasEdge(16,11)
                || !g.hasEdge(3,14) || !g.hasEdge(10,2)
                || g.hasEdge(2,2) || !g.hasEdge(3,16)) {
            fail("graph4 1/2 cycle elimination error");
        }
        g = graph5.getGraphWithoutOneOrTwoCycles();
        if (g.getNodecount() != 2 || g.getEdgecount() != 0
                || g.hasEdge(0,1)
            || g.hasEdge(1,0)) {
            fail("graph5 1/2 cycle elimination error");
        }
     }

    /**
     * Test cycle elimination
     * @throws Exception All Exceptions.
     */
    public void testCycleElimination() throws Exception {
        int cycleEliminationPriority[];
        int renumber[];
        Graph g;
        Graph p[];
        Graph dag;
        g = graph0;
        cycleEliminationPriority = g.getCycleEliminationVertexPriority();
        if (cycleEliminationPriority.length != 0) {
            fail("graph0 cycle Elimination priority error");
        }
        dag = g.getGraphWithoutCycles(cycleEliminationPriority);
        if (dag.getNodecount() != 0 || dag.getEdgecount() != 0) {
            fail("graph0 cycle Elimination error");
        }
        g = graph1;
        cycleEliminationPriority = g.getCycleEliminationVertexPriority();
        if (cycleEliminationPriority.length != 1 || cycleEliminationPriority[0] != 0) {
            fail("graph1 cycle Elimination priority error");
        }
        dag = g.getGraphWithoutCycles(cycleEliminationPriority);
        if (dag.getNodecount() != 1 || dag.getEdgecount() != 0) {
            fail("graph1 cycle Elimination error");
        }
        g = graph2;
        cycleEliminationPriority = g.getCycleEliminationVertexPriority();
        if (cycleEliminationPriority.length != 2 || cycleEliminationPriority[0] != 0
            || cycleEliminationPriority[1] != 1) {
            fail("graph2 cycle Elimination priority error");
        }
        dag = g.getGraphWithoutCycles(cycleEliminationPriority);
        if (dag.getNodecount() != 2 || dag.getEdgecount() != 2 || !dag.hasEdge(0,1)) {
            fail("graph2 cycle Elimination error");
        }
        g = graph3;
        cycleEliminationPriority = g.getCycleEliminationVertexPriority();
        if (cycleEliminationPriority.length != 3 || cycleEliminationPriority[0] != 0
            || cycleEliminationPriority[1] != 1 || cycleEliminationPriority[2] != 2) {
            fail("graph3 cycle Elimination priority error");
        }
        dag = g.getGraphWithoutCycles(cycleEliminationPriority);
        if (dag.getNodecount() != 3 || dag.getEdgecount() != 3 || !dag.hasEdge(0,1) || !dag.hasEdge(1,2)
            || !dag.hasEdge(0,2) || dag.hasEdge(2,2)) {
            fail("graph3 cycle Elimination error");
        }
        renumber = new int[17];
        p = graph4.partition(graph4.componentIndex(),renumber);
        g = p[0];
        cycleEliminationPriority = g.getCycleEliminationVertexPriority();
        if (cycleEliminationPriority.length != 9 || cycleEliminationPriority[0] != 0
            || cycleEliminationPriority[1] != 4 || cycleEliminationPriority[2] != 7
            || cycleEliminationPriority[3] != 5 || cycleEliminationPriority[4] != 1
            || cycleEliminationPriority[5] != 6 || cycleEliminationPriority[6] != 3
            || cycleEliminationPriority[7] != 2 || cycleEliminationPriority[8] != 8) {
            fail("graph4_0 cycle Elimination priority error");
        }
        dag = g.getGraphWithoutCycles(cycleEliminationPriority);
        if (dag.getNodecount() != 9 || dag.getEdgecount() != 12
                || !dag.hasEdge(4,7) || !dag.hasEdge(0,4)
                || !dag.hasEdge(0,3) || !dag.hasEdge(0,8)
                || !dag.hasEdge(2,8) || !dag.hasEdge(7,3)
                || !dag.hasEdge(7,5) || !dag.hasEdge(3,2)
                || !dag.hasEdge(1,3) || !dag.hasEdge(5,1)
                || !dag.hasEdge(6,2) || !dag.hasEdge(1,6)) {
            fail("graph4_0 cycle Elimination error");
        }
        g = p[1];
        cycleEliminationPriority = g.getCycleEliminationVertexPriority();
        if (cycleEliminationPriority.length != 2 || cycleEliminationPriority[0] != 1
            || cycleEliminationPriority[1] != 0) {
            fail("graph4_1 cycle Elimination priority error");
        }
        dag = g.getGraphWithoutCycles(cycleEliminationPriority);
        if (dag.getNodecount() != 2 || dag.getEdgecount() != 1
                || !dag.hasEdge(1,0) || dag.hasEdge(0,0)) {
            fail("graph4_1 cycle Elimination error");
        }
        g = p[2];
        cycleEliminationPriority = g.getCycleEliminationVertexPriority();
        if (cycleEliminationPriority.length != 5 || cycleEliminationPriority[0] != 0
            || cycleEliminationPriority[1] != 4 || cycleEliminationPriority[2] != 3
            || cycleEliminationPriority[3] != 2 || cycleEliminationPriority[4] != 1) {
            fail("graph4_2 cycle Elimination priority error");
        }
        dag = g.getGraphWithoutCycles(cycleEliminationPriority);
        if (dag.getNodecount() != 5 || dag.getEdgecount() != 8
                || !dag.hasEdge(2,1) || !dag.hasEdge(0,3)
                || dag.hasEdge(1,1) || !dag.hasEdge(0,4)
                || dag.hasEdge(0,0) || !dag.hasEdge(4,2)
                || !dag.hasEdge(0,1) || !dag.hasEdge(3,2)) {
            fail("graph4_2 cycle Elimination error");
        }
        g = p[3];
        cycleEliminationPriority = g.getCycleEliminationVertexPriority();
        if (cycleEliminationPriority.length != 1 || cycleEliminationPriority[0] != 0) {
            fail("graph4_3 cycle Elimination priority error");
        }
        dag = g.getGraphWithoutCycles(cycleEliminationPriority);
        if (dag.getNodecount() != 1 || dag.getEdgecount() != 0) {
            fail("graph4_3 cycle Elimination error");
        }
    }

    /**
     * Test transitive reduction
     * @throws Exception All Exceptions.
     */
    public void testTransitiveReduction() throws Exception {
        int renumber[];
        Graph p[];
        Graph g;
        g = graph0.getReducedGraph();
        if (g.getNodecount() != 0 || g.getEdgecount() != 0) {
            fail("graph0 transitive reduction error");
        }
        g = graph1.getReducedGraph();
        if (g.getNodecount() != 1 || g.getEdgecount() != 0) {
            fail("graph1 transitive reduction error");
        }
        g = graph2.getReducedGraph();
        if (g.getNodecount() != 2 || g.getEdgecount() != 1
                || !g.hasEdge(0,1)) {
            fail("graph2 transitive reduction error");
        }
        g = graph3.getReducedGraph();
        if (g.getNodecount() != 3 || g.getEdgecount() != 2
                || !g.hasEdge(0,1) || !g.hasEdge(1,2)
            || g.hasEdge(0,2) || g.hasEdge(2,2)) {
            fail("graph3 transitive reduction error");
        }
        renumber = new int[17];
        p = graph4.partition(graph4.componentIndex(),renumber);
        g = p[0].getReducedGraph();
        if (g.getNodecount() != 9 || g.getEdgecount() != 9
            || !g.hasEdge(4,7) || !g.hasEdge(0,4)
            || g.hasEdge(0,3) || g.hasEdge(0,8)
            || !g.hasEdge(2,8) || g.hasEdge(7,3)
            || !g.hasEdge(7,5) || !g.hasEdge(3,2)
            || !g.hasEdge(1,3) || !g.hasEdge(5,1)
            || !g.hasEdge(6,2) || !g.hasEdge(1,6)) {
            fail("graph4_0 transitive reduction error");
        }

        g = p[1].getReducedGraph();
        if (g.getNodecount() != 2 || g.getEdgecount() != 1
            || !g.hasEdge(1,0) || g.hasEdge(0,0)) {
            fail("graph4_1 transitive reduction error");
        }
        g = p[2].getReducedGraph();
        if (g.getNodecount() != 5 || g.getEdgecount() != 5
            || !g.hasEdge(2,1) || !g.hasEdge(0,3)
            || g.hasEdge(1,1) || !g.hasEdge(0,4)
            || g.hasEdge(0,0) || !g.hasEdge(4,2)
            || g.hasEdge(0,1) || !g.hasEdge(3,2)) {
            fail("graph4_2 transitive reduction error");
        }
        g = p[3].getReducedGraph();
        if (g.getNodecount() != 1 || g.getEdgecount() != 0) {
            fail("graph4_3 transitive reduction error");
        }
    }

    /**
     * Test layer assignment
     * @throws Exception All Exceptions.
     */
    public void testLayerAssignment() throws Exception {
        int renumber[];
        Graph p[];
        int layer[];
        layer = graph0.getReducedGraph().getVertexLayers();
        if (layer.length != 0) {
            fail("graph0 layer assignment error");
        }
        layer = graph1.getReducedGraph().getVertexLayers();
        if (layer.length != 1 || layer[0] != 1) {
            fail("graph1 layer assignment error");
        }
        layer = graph2.getReducedGraph().getVertexLayers();
        if (layer.length != 2 || layer[0] != 2 || layer[1] != 1) {
            fail("graph2 layer assignment error");
        }
        layer = graph3.getReducedGraph().getVertexLayers();
        if (layer.length != 3 || layer[0] != 3 || layer[1] != 2
             || layer[2] != 1) {
            fail("graph3 layer assignment error");
        }
        renumber = new int[17];
        p = graph4.partition(graph4.componentIndex(),renumber);
        layer = p[0].getReducedGraph().getVertexLayers();
        if (layer.length != 9 || layer[0] != 8 || layer[1] != 4
             || layer[2] != 2 || layer[3] != 3
             || layer[4] != 7 || layer[5] != 5
             || layer[6] != 3 || layer[7] != 6
             || layer[8] != 1) {
            fail("graph4_0 layer assignment error");
        }
        layer = p[1].getReducedGraph().getVertexLayers();
        if (layer.length != 2 || layer[0] != 1 || layer[1] != 2) {
            fail("graph4_1 layer assignment error");
        }
        layer = p[2].getReducedGraph().getVertexLayers();
        if (layer.length != 5 || layer[0] != 4 || layer[1] != 1
                || layer[2] != 2 || layer[3] != 3 || layer[4] != 3) {
            fail("graph4_2 layer assignment error");
        }
        layer = p[3].getReducedGraph().getVertexLayers();
        if (layer.length != 1 || layer[0] != 1) {
            fail("graph4_3 layer assignment error");
        }
    }

    /**
     * Test horizontal position
     * @throws Exception All Exceptions.
     */
    public void testHorizontalPosition() throws Exception {
        int layer[];
        int horizontalPosition[];
        int renumber[];
        Graph p[];
        Graph g;
        g = graph0.getReducedGraph();
        layer = g.getVertexLayers();
        horizontalPosition = g.getHorizontalPosition(layer);
        if (layer.length != 0) {
            fail("graph0 horizontal position error");
        }
        g = graph1.getReducedGraph();
        layer = g.getVertexLayers();
        horizontalPosition = g.getHorizontalPosition(layer);
        if (layer.length != 1 || horizontalPosition[0] != 1) {
            fail("graph1 horizontal position error");
        }
        g = graph2.getReducedGraph();
        layer = g.getVertexLayers();
        horizontalPosition = g.getHorizontalPosition(layer);
        if (layer.length != 2 || horizontalPosition[0] != 1 || horizontalPosition[1] != 1) {
            fail("graph2 horizontal position error");
        }
        g = graph3.getReducedGraph();
        layer = g.getVertexLayers();
        horizontalPosition = g.getHorizontalPosition(layer);
        if (layer.length != 3 || horizontalPosition[0] != 1 || horizontalPosition[1] != 1
            || horizontalPosition[2] != 1) {
            fail("graph3 horizontal position error");
        }
        renumber = new int[17];
        p = graph4.partition(graph4.componentIndex(),renumber);
        g = p[0].getReducedGraph();
        layer = g.getVertexLayers();
        horizontalPosition = g.getHorizontalPosition(layer);
        if (horizontalPosition.length != 9 || horizontalPosition[0] != 1
             || horizontalPosition[1] != 1
             || horizontalPosition[2] != 1 || horizontalPosition[3] != 1
             || horizontalPosition[4] != 1 || horizontalPosition[5] != 1
             || horizontalPosition[6] != 2 || horizontalPosition[7] != 1
             || horizontalPosition[8] != 1) {
            fail("graph4_0 horizontalPosition assignment error");
        }
        g = p[1].getReducedGraph();
        layer = g.getVertexLayers();
        horizontalPosition = g.getHorizontalPosition(layer);
        if (horizontalPosition.length != 2 || horizontalPosition[0] != 1
                || horizontalPosition[1] != 1) {
            fail("graph4_1 horizontalPosition assignment error");
        }
        g = p[2].getReducedGraph();
        layer = g.getVertexLayers();
        horizontalPosition = g.getHorizontalPosition(layer);
        if (horizontalPosition.length != 5 || horizontalPosition[0] != 1
                || horizontalPosition[1] != 1 || horizontalPosition[2] != 1
                || horizontalPosition[3] != 1 || horizontalPosition[4] != 2) {
            fail("graph4_2 horizontalPosition assignment error");
        }
        g = p[3].getReducedGraph();
        layer = g.getVertexLayers();
        horizontalPosition = g.getHorizontalPosition(layer);
        if (horizontalPosition.length != 1 || horizontalPosition[0] != 1) {
            fail("graph4_3 horizontalPosition assignment error");
        }
    }
}