import cern.colt.matrix.DoubleMatrix2D;
import infovis.Graph;
import infovis.graph.DefaultGraph;
import infovis.graph.algorithm.DijkstraShortestPath;
import junit.framework.TestCase;

/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

/**
 * Class DijkstraTest
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class DijkstraTest extends TestCase {
    static final int g1VertexCount = 20;
    static final int[] g1Edges = {
        12, 5,
        4, 13,
        14, 9,
        10, 4,
        1, 10,
        14, 4,
        9, 3,
        11, 15,
        3, 15,
        5, 7,
        9, 10,
        13, 9,
        16, 11,
    };
    static final int[] g1Dist = {
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 3, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 1, 5, Integer.MAX_VALUE, 3, 3, 4, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 2, 2, Integer.MAX_VALUE, 2, 2, 1, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 1, 5, Integer.MAX_VALUE, 1, 1, 4, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        1, 3, Integer.MAX_VALUE, 1, 1, 2, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        4, Integer.MAX_VALUE, 2, 2, 3, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 4, 4, 1, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        2, 3, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        3, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 
    };
    static final int g2VertexCount = 40;
    static final int[] g2Edges = {
        16, 32,
        18, 21,
        34, 1,
        1, 5,
        6, 30,
        28, 27,
        25, 16,
        2, 31,
        16, 33,
        21, 2,
        6, 5,
        1, 29,
        0, 38,
        17, 6,
        26, 39,
        36, 39,
        0, 26,
        25, 6,
        19, 31,
        28, 15,
        15, 17,
        18, 9,
        23, 3,
        5, 32,
        13, 36,
        12, 3,
        34, 3,
    };
    static final int[] g2Dist = {
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 1, 2, 
        Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 1, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, 3, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 6, 5, 1, 3, Integer.MAX_VALUE, 2, 4, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 2, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 3, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, 5, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 8, 7, 3, 5, Integer.MAX_VALUE, 4, 6, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 2, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 5, 4, 2, 2, Integer.MAX_VALUE, 1, 3, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 2, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 4, 3, 3, 1, Integer.MAX_VALUE, 2, 3, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 5, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, 7, 6, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 9, 8, 4, 6, Integer.MAX_VALUE, 5, 7, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 5, 2, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        4, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 7, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 2, 1, 5, 3, Integer.MAX_VALUE, 4, 5, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 6, 5, 4, 3, Integer.MAX_VALUE, 1, 1, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 3, 2, 4, 2, Integer.MAX_VALUE, 3, 4, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        4, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 9, 8, 4, 6, Integer.MAX_VALUE, 5, 7, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 5, 4, 4, 2, Integer.MAX_VALUE, 2, 2, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 2, 1, 
        1, 7, 5, Integer.MAX_VALUE, 6, 7, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        6, 4, Integer.MAX_VALUE, 5, 6, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        4, Integer.MAX_VALUE, 3, 5, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 3, 4, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        2, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 4, 1, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, 
        3, 
    };
    static final int g3VertexCount = 60;
    static final int[] g3Edges = {
        42, 34,
        8, 0,
        19, 46,
        10, 20,
        1, 32,
        0, 53,
        48, 8,
        46, 11,
        44, 41,
        27, 43,
        9, 7,
        27, 10,
        10, 25,
        16, 41,
        57, 6,
        52, 59,
        32, 20,
        28, 17,
        49, 57,
        53, 28,
        1, 8,
        46, 36,
        21, 20,
        29, 11,
        21, 52,
        20, 3,
        2, 34,
        14, 26,
        39, 10,
        2, 30,
        13, 30,
        45, 1,
        39, 14,
        32, 14,
        45, 29,
        51, 9,
        49, 41,
        25, 49,
        3, 9,
        15, 49,
        15, 33,
        37, 21,
    };
    static final int[] g3Dist = {
        2, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, 7, 1, 6, 5, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, 8, 9, 3, Integer.MAX_VALUE, 7, 4, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, 5, 6, 2, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 9, Integer.MAX_VALUE, Integer.MAX_VALUE, 7, 6, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 8, Integer.MAX_VALUE, 7, 9, 3, 6, Integer.MAX_VALUE, 2, 7, Integer.MAX_VALUE, 7, 6, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 8, Integer.MAX_VALUE, 7, 
        Integer.MAX_VALUE, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, 7, 5, 1, 4, 3, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 6, 7, 5, Integer.MAX_VALUE, 5, 2, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, 3, 4, 4, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, 4, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 5, 7, 1, 4, Integer.MAX_VALUE, 2, 5, Integer.MAX_VALUE, 5, 4, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 5, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, 6, 2, 4, 1, 2, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 5, 6, 8, Integer.MAX_VALUE, 8, 1, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 4, 3, 7, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, 8, 3, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 4, 6, 4, 7, Integer.MAX_VALUE, 5, 4, Integer.MAX_VALUE, 2, 3, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 4, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        8, 8, 7, 4, 10, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, 3, 4, 12, Integer.MAX_VALUE, 12, 5, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 7, 5, 11, 9, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, 12, 7, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 6, 4, 8, 11, Integer.MAX_VALUE, 9, 2, Integer.MAX_VALUE, 8, 7, 10, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 8, 
        6, 1, 4, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, 7, 8, 10, Integer.MAX_VALUE, 10, 3, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, 6, 5, 9, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, 10, 5, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 7, Integer.MAX_VALUE, 6, 8, 6, 9, Integer.MAX_VALUE, 7, 6, Integer.MAX_VALUE, 2, 5, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 7, Integer.MAX_VALUE, 6, 
        5, 4, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 7, 8, 4, Integer.MAX_VALUE, 6, 3, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, 4, 5, 3, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, 5, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 7, Integer.MAX_VALUE, 6, 8, 2, 5, Integer.MAX_VALUE, 1, 6, Integer.MAX_VALUE, 6, 5, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 7, Integer.MAX_VALUE, 6, 
        3, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, 6, 7, 9, Integer.MAX_VALUE, 9, 2, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, 5, 4, 8, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, 4, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 5, 7, 5, 8, Integer.MAX_VALUE, 6, 5, Integer.MAX_VALUE, 1, 4, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 5, 
        6, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 3, 4, 8, Integer.MAX_VALUE, 8, 1, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 3, 1, 7, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, 8, 3, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 2, 4, 4, 7, Integer.MAX_VALUE, 5, 2, Integer.MAX_VALUE, 4, 3, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 4, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, 5, 9, 10, 8, Integer.MAX_VALUE, 2, 5, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 7, 6, 7, 7, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, 10, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 7, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 9, Integer.MAX_VALUE, 8, 10, 2, 1, Integer.MAX_VALUE, 5, 8, Integer.MAX_VALUE, 8, 7, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, Integer.MAX_VALUE, 8, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        5, 6, 7, Integer.MAX_VALUE, 7, 2, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 1, 3, 6, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, 7, 4, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 4, 6, 3, 6, Integer.MAX_VALUE, 4, 4, Integer.MAX_VALUE, 5, 4, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 5, 
        3, 11, Integer.MAX_VALUE, 11, 4, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 6, 4, 10, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, 11, 6, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 5, 3, 7, 10, Integer.MAX_VALUE, 8, 1, Integer.MAX_VALUE, 7, 6, 9, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 7, 
        12, Integer.MAX_VALUE, 12, 5, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 7, 5, 11, 9, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, 12, 7, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 6, 2, 8, 11, Integer.MAX_VALUE, 9, 2, Integer.MAX_VALUE, 8, 7, 10, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 8, 
        Integer.MAX_VALUE, 10, 7, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, 8, 9, 1, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, 12, Integer.MAX_VALUE, Integer.MAX_VALUE, 10, 9, Integer.MAX_VALUE, 8, Integer.MAX_VALUE, 11, Integer.MAX_VALUE, 10, 12, 6, 9, Integer.MAX_VALUE, 5, 10, Integer.MAX_VALUE, 10, 9, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 11, Integer.MAX_VALUE, 10, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        7, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, 8, 9, 9, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, 12, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 9, Integer.MAX_VALUE, 8, Integer.MAX_VALUE, 11, Integer.MAX_VALUE, 10, 12, 4, 1, Integer.MAX_VALUE, 7, 10, Integer.MAX_VALUE, 10, 9, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 11, Integer.MAX_VALUE, 10, 
        1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 3, 2, 6, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, 7, 2, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 3, 5, 3, 6, Integer.MAX_VALUE, 4, 3, Integer.MAX_VALUE, 3, 2, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 3, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 4, 3, 7, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, 8, 1, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 4, 6, 4, 7, Integer.MAX_VALUE, 5, 4, Integer.MAX_VALUE, 4, 1, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 2, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        4, 2, 8, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, 4, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 3, 3, 5, 8, Integer.MAX_VALUE, 6, 1, Integer.MAX_VALUE, 5, 4, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 5, 
        4, 7, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, 8, 5, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 5, 7, 4, 7, Integer.MAX_VALUE, 5, 5, Integer.MAX_VALUE, 6, 5, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 6, 
        8, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, 4, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 1, 5, 5, 8, Integer.MAX_VALUE, 6, 3, Integer.MAX_VALUE, 5, 4, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 5, 
        6, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, 11, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, 8, Integer.MAX_VALUE, 7, Integer.MAX_VALUE, 10, Integer.MAX_VALUE, 9, 11, 5, 8, Integer.MAX_VALUE, 4, 9, Integer.MAX_VALUE, 9, 8, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 10, Integer.MAX_VALUE, 9, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 9, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, 6, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 8, Integer.MAX_VALUE, 7, 9, 1, 2, Integer.MAX_VALUE, 4, 7, Integer.MAX_VALUE, 7, 6, 5, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 8, Integer.MAX_VALUE, 7, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        6, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, 3, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 4, 6, 2, 5, Integer.MAX_VALUE, 3, 4, Integer.MAX_VALUE, 4, 3, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 4, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, 12, 7, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 6, 4, 8, 11, Integer.MAX_VALUE, 9, 2, Integer.MAX_VALUE, 8, 7, 10, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 8, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        9, Integer.MAX_VALUE, 8, Integer.MAX_VALUE, 11, Integer.MAX_VALUE, 10, 12, 4, 1, Integer.MAX_VALUE, 7, 10, Integer.MAX_VALUE, 10, 9, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 11, Integer.MAX_VALUE, 10, 
        Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 5, 7, 5, 8, Integer.MAX_VALUE, 6, 5, Integer.MAX_VALUE, 5, 2, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 3, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 3, 5, 4, 7, Integer.MAX_VALUE, 5, 3, Integer.MAX_VALUE, 5, 4, 6, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 4, Integer.MAX_VALUE, 5, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 5, 1, 7, 10, Integer.MAX_VALUE, 8, 1, Integer.MAX_VALUE, 7, 6, 9, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 2, Integer.MAX_VALUE, 7, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        6, 6, 9, Integer.MAX_VALUE, 7, 4, Integer.MAX_VALUE, 6, 5, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 5, Integer.MAX_VALUE, 6, 
        8, 11, Integer.MAX_VALUE, 9, 2, Integer.MAX_VALUE, 8, 7, 10, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 3, Integer.MAX_VALUE, 8, 
        3, Integer.MAX_VALUE, 3, 6, Integer.MAX_VALUE, 6, 5, 4, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 7, Integer.MAX_VALUE, 6, 
        Integer.MAX_VALUE, 6, 9, Integer.MAX_VALUE, 9, 8, 7, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 10, Integer.MAX_VALUE, 9, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        7, Integer.MAX_VALUE, 7, 6, 3, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 8, Integer.MAX_VALUE, 7, 
        Integer.MAX_VALUE, 6, 5, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 1, Integer.MAX_VALUE, 6, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        5, 8, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 7, Integer.MAX_VALUE, 6, 
        7, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 6, Integer.MAX_VALUE, 1, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 9, Integer.MAX_VALUE, 8, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, 
        Integer.MAX_VALUE, 7, 
        Integer.MAX_VALUE, 
    };

    public DijkstraTest(String name) {
        super(name);
    }
    
    public void testDijkstra(String name, int vertices, int[] edges, int[] dist) {
        Graph g = new DefaultGraph();
        g.setDirected(false);
        int i;
        for (i = 0; i < vertices; i++) {
            assertEquals(i, g.addVertex());
        }
        for (i = 0; i < edges.length; i += 2) {
            assertEquals(i/2, g.addEdge(edges[i], edges[i+1]));
        }
        DijkstraShortestPath dsp = new DijkstraShortestPath(g);
        int k = 0;
        for (i = 0; i < (vertices-1); i++) {
            for (int j = i+1; j < vertices; j++) {
                DijkstraShortestPath.Predecessor p = dsp.shortestPath(i, j);
                if (p == null) {
                    assertEquals(dist[k], Integer.MAX_VALUE);
                }
                else {
                    if (dist[k] != p.getWeight()) {
                        System.err.println(name+":dist("+i+","+j+"), "+dist[k]+"!="+p.getWeight());
                        while (p.getPred() != Graph.NIL) {
                            p = dsp.getPredecessor(i, p);
                            System.err.print(p.getVertex() + " ");
                        }
                        System.err.println();
                    }
                    assertEquals(name+":dist("+i+","+j+")", dist[k], (int)p.getWeight());
                }
                k++;
            }
        }
        DoubleMatrix2D mat = DijkstraShortestPath.allShortestPaths(g, null, null);
        k = 0;
        for (i = 0; i < (vertices-1); i++) {
            for (int j = i+1; j < vertices; j++) {
                double d = mat.getQuick(i, j);
                assertEquals(d, mat.getQuick(j, i), 0); 
                if (d == Double.POSITIVE_INFINITY) {
                    assertEquals(Integer.MAX_VALUE, dist[k]);
                }
                else {
                    assertTrue(name+":dist("+i+","+j+"), "+dist[k]+"!="+d,
                            dist[k] == d);
                }
                k++;
            }
        }
        
    }
    
    public void testDijkstra() {
        testDijkstra("g1", g1VertexCount, g1Edges, g1Dist);
        testDijkstra("g2", g2VertexCount, g2Edges, g2Dist);
        testDijkstra("g3", g3VertexCount, g3Edges, g3Dist);
    }

}
