/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.utils.RowIterator;

import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class VCGGraphWriter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 * @infovis.factory GraphWriterFactory vcg
 */
public class VCGGraphWriter extends DOTGraphWriter {
    public static final String[] VCG_ORIENTATION = { "bottom_to_top", // north
                                                "top_to_bottom", // south
                                                "left_to_right", // east
                                                "right_to_left" // west
                                                };

    public VCGGraphWriter(OutputStream out, String name, Graph graph) {
        super(out, name, graph);
    }

    public VCGGraphWriter(OutputStream out, Graph graph) {
        super(out, graph);
    }

    public void writeVertex(int vertex) throws IOException {
        write("node: { ");
        write("title: \"" + getVertexId(vertex) + "\" ");
        if (shapes != null) {
            Shape s = (Shape) shapes.get(vertex);
            if (s != null) {
                Rectangle r = s.getBounds();
                write("width: " + r.width + " ");
                write("height: " + r.height + " ");
                write("loc: { x: " + r.x + " y: " + r.y + " } ");
            }
        }
        write("}");
    }

    public void writeEdge(int edge) throws IOException {
        write("edge: { ");
        write("sourcename: \"" + getVertexId(graph.getFirstVertex(edge)) + "\" ");
        write("targetname: \"" + getVertexId(graph.getSecondVertex(edge)) + "\" ");
        write("label: \"" + getEdgeId(edge) + "\" ");
        write("}");
    }

    public String getVCG_ORIENTATION() {
        return VCG_ORIENTATION[orientation];
    }

    public boolean write() {
        try {
            write("graph: {\n");
            write("\ttitle: \"" + graph.getName() + "\"\n");
            write("\tdisplay_edge_labels: no\n");
            write("\torientation: " + getVCG_ORIENTATION() + "\n");

            for (RowIterator iter = graph.getVertexTable().iterator(); iter
                    .hasNext();) {
                int vertex = iter.nextRow();
                write("\t");
                writeVertex(vertex);
                write("\n");
            }

            for (RowIterator iter = graph.getVertexTable().iterator(); iter
                    .hasNext();) {
                int vertex = iter.nextRow();
                for (RowIterator eiter = graph.outEdgeIterator(vertex); eiter
                        .hasNext();) {
                    int edge = eiter.nextRow();
                    write("\t");
                    writeEdge(edge);
                    write("\n");
                }
            }

            write("}\n");
            flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public short getOrientation() {
        return orientation;
    }

    public void setOrientation(short s) {
        orientation = s;
    }
}
