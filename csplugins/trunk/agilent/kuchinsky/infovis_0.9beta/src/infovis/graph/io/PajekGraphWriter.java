/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Column;
import infovis.Graph;
import infovis.utils.RowIterator;
import infovis.visualization.render.DefaultVisualLabel;

import java.io.IOException;
import java.io.OutputStream;

import cern.colt.map.OpenIntIntHashMap;

/**
 * 
 * Class PajekGraphWriter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 * @infovis.factory GraphWriterFactory net Pajek
 */
public class PajekGraphWriter extends DOTGraphWriter {
    public PajekGraphWriter(OutputStream out, String name, Graph graph) {
        super(out, name, graph);
    }

    public PajekGraphWriter(OutputStream out, Graph graph) {
        super(out, graph.getName(), graph);
    }

    public boolean write() {
        try {
            String name = getName();
            if (name == null) {
                name = "Infovis";
            }
            OpenIntIntHashMap idToVertex = new OpenIntIntHashMap();
            OpenIntIntHashMap vertexToId = new OpenIntIntHashMap();
            Column lab = DefaultVisualLabel.findDefaultLabelColumn(graph
                    .getVertexTable());
            write("*Network " + name + "\n");

            int id = 0;
            write("*Vertices " + graph.getVertexTable().getRowCount() + "\n");
            for (RowIterator viter = graph.vertexIterator(); viter.hasNext();) {
                int v = viter.nextRow();
                id++;
                idToVertex.put(id, v);
                vertexToId.put(v, id);
                write(Integer.toString(id) + " ");
                if (lab != null && !lab.isValueUndefined(v)) {
                    write("\"" + lab.getValueAt(v) + "\""); // TODO fix quotes
                }
                else {
                    write("v" + v);
                }
                write("\n");
            }
            if (graph.isDirected()) {
                write("*Arcs ");
            }
            else {
                write("*Edges ");
            }
            write(Integer.toString(graph.getEdgesCount()) + "\n");
            for (RowIterator eiter = graph.edgeIterator(); eiter.hasNext();) {
                int e = eiter.nextRow();
                write(Integer.toString(vertexToId.get(graph.getFirstVertex(e))));
                write(" ");
                write(Integer.toString(vertexToId.get(graph.getSecondVertex(e))));
                write("\n");
            }
        }

        catch (IOException e) {
            return false;
        }
        return true;
    }

}
