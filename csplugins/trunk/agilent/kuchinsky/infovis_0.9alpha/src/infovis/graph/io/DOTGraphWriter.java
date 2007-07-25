/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.io;

import infovis.Graph;
import infovis.column.ShapeColumn;
import infovis.column.StringColumn;
import infovis.io.AbstractWriter;
import infovis.utils.RowIterator;
import infovis.visualization.Orientable;

import java.awt.Dimension;
import java.awt.Shape;
import java.io.IOException;
import java.io.Writer;


/**
 * Class DOTGraphWriter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */
public class DOTGraphWriter extends AbstractWriter implements Orientable {
    public static final String ID_COLUMN = "id";
    protected Graph graph;
    protected StringColumn vertexIdColumn;
    protected StringColumn edgeIdColumn;
    protected ShapeColumn shapes;
    protected Dimension size;
    protected short orientation = ORIENTATION_SOUTH;
    protected String layoutRatio;
    
    public DOTGraphWriter(Writer out, Graph graph) {
        super(out, graph.getEdgeTable());
        this.graph = graph;
        vertexIdColumn = StringColumn.findColumn(graph.getVertexTable(), ID_COLUMN);
        edgeIdColumn = StringColumn.findColumn(graph.getEdgeTable(), ID_COLUMN);
    }
    
    public String getVertexId(int vertex) {
        if (vertexIdColumn.isValueUndefined(vertex)) {
            String id = "v"+vertex;
            vertexIdColumn.setExtend(vertex, id);
            return id;
        }
        return vertexIdColumn.get(vertex);
    }
    
    public String getEdgeId(int edge) {
        if (edgeIdColumn.isValueUndefined(edge)) {
            String id = "e"+edge;
            edgeIdColumn.setExtend(edge, id);
            return id;
        }
        return edgeIdColumn.get(edge);
    }
    
    public void writeVertex(int vertex) throws IOException {
        write("\""+getVertexId(vertex)+"\"");
    }
    
    public void writeEdge(int edge) throws IOException {
        int vertex = graph.getInVertex(edge);
        int otherVertex = graph.getOutVertex(edge);

        writeVertex(vertex);
        if (graph.isDirected()) {
            write("->");
        }
        else {
            write("--");
        }
        writeVertex(otherVertex);
    }
    
    public boolean write() {
        try {
            String name = graph.getName();
            if (name == null) {
                name = "Infovis";
            }
            if (graph.isDirected()) {
                write ("digraph "+name +" {\n");
            }
            else {
                write("graph "+name+" {\n");
            }
            if (size != null) {
                write(" ");
                write("size=\""+size.width/72.0+","+size.height/72.0+"\";\n");
            }
            if (orientation == ORIENTATION_EAST || orientation == ORIENTATION_WEST) {
                write(" ");
                write("rankdir=LR;\n");
            }
            if (layoutRatio != null) {
                write(" ");
                write("ratio=\""+layoutRatio+"\";\n");
            }
            if (shapes != null) {
                write(" ");
                write("node [label=\"\", fixedsize=true]\n");
            }
            for (RowIterator iter = graph.getVertexTable().iterator(); iter.hasNext(); ) {
                int vertex = iter.nextRow();
                write(" ");
                writeVertex(vertex);
                if (shapes != null) {
                    Shape s = (Shape)shapes.get(vertex);
                    if (s != null) {
                        double w = s.getBounds2D().getWidth() / 72;
                        double h = s.getBounds2D().getHeight() / 72;
                        write("[width=\""+w+"\",height=\""+h+"\"]");
                    }
                }
                write(";\n");
            }
            
            for (RowIterator iter = graph.getVertexTable().iterator(); iter.hasNext(); ) {
                int vertex = iter.nextRow();
                for (RowIterator eiter = graph.outEdgeIterator(vertex); eiter.hasNext(); ) {
                    int edge = eiter.nextRow();
                    write(" ");
                    writeEdge(edge);
                    write(";\n");
                }
            }
            write("}\n");
            out.flush();
        }
        catch(IOException e) {
            return false;
        }
        return true;
    }
    
    public ShapeColumn getShapes() {
        return shapes;
    }

    public void setShapes(ShapeColumn column) {
        shapes = column;
    }

    public Dimension getSize() {
        return size;
    }

    public void setSize(Dimension dimension) {
        size = dimension;
    }

    public short getOrientation() {
        return orientation;
    }

    public void setOrientation(short s) {
        orientation = s;
    }

    public String getLayoutRatio() {
        return layoutRatio;
    }

    public void setLayoutRatio(String string) {
        layoutRatio = string;
    }

    public StringColumn getEdgeIdColumn() {
        return edgeIdColumn;
    }
    public void setEdgeIdColumn(StringColumn edgeIdColumn) {
        this.edgeIdColumn = edgeIdColumn;
    }
    public StringColumn getVertexIdColumn() {
        return vertexIdColumn;
    }
    public void setVertexIdColumn(StringColumn vertexIdColumn) {
        this.vertexIdColumn = vertexIdColumn;
    }
}
