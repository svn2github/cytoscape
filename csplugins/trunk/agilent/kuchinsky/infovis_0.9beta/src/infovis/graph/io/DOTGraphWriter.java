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
import infovis.column.ShapeColumn;
import infovis.column.StringColumn;
import infovis.io.AbstractWriter;
import infovis.utils.RowIterator;
import infovis.visualization.Orientable;
import infovis.visualization.render.VisualColor;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.swing.text.MutableAttributeSet;


/**
 * Class DOTGraphWriter
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.17 $
 * @infovis.factory GraphWriterFactory dot GraphViz
 */
public class DOTGraphWriter extends AbstractWriter implements Orientable {
    public static final String DOT_PROPERTY_PREFIX = "dot::";
    public static final String ID_COLUMN = "id";
    protected Graph graph;
    protected StringColumn vertexIdColumn;
    protected StringColumn edgeIdColumn;
    protected ShapeColumn shapes;
    protected Column labels;
    protected VisualColor colors;
    protected Dimension size;
    protected short orientation = ORIENTATION_SOUTH;
    protected String layoutRatio;
    
    public DOTGraphWriter(OutputStream out, String name, Graph graph) {
        super(out, name, graph.getEdgeTable());
        this.graph = graph;
        vertexIdColumn = StringColumn.findColumn(graph.getVertexTable(), ID_COLUMN);
        edgeIdColumn = StringColumn.findColumn(graph.getEdgeTable(), ID_COLUMN);
    }
    
    public DOTGraphWriter(OutputStream out, Graph graph) {
        this(out, graph.getName(), graph);
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
        int vertex = graph.getFirstVertex(edge);
        int otherVertex = graph.getSecondVertex(edge);

        writeVertex(vertex);
        if (graph.isDirected()) {
            write("->");
        }
        else {
            write("--");
        }
        writeVertex(otherVertex);
    }
    
    public String quoteString(String s) {
        int i = s.indexOf('"');
        if (i == -1) return s;
        StringBuffer sb = new StringBuffer();
        int j = 0;
        while (i != -1) {
            sb.append(s.substring(j, i-1));
            sb.append('\\');
            j = i;
            i = s.indexOf('"', j);
        }
        sb.append('\\');
        sb.append(s.substring(j, s.length()));
        return sb.toString();
    }
    
    public boolean write() {
        try {
            String name = getName();
            if (name == null) {
                name = "Infovis";
            }
            if (graph.isDirected()) {
                write ("digraph "+name +" {\n");
            }
            else {
                write("graph "+name+" {\n");
            }
            MutableAttributeSet cp = graph.getClientProperty();
            if (cp != null) {
               for (Enumeration iter = cp.getAttributeNames(); iter.hasMoreElements(); ) {
                   String key = (String)iter.nextElement();
                   if (key.startsWith(DOT_PROPERTY_PREFIX)) {
                       write(key.substring(DOT_PROPERTY_PREFIX.length()));
                       write("=");
                       write(cp.getAttribute(key).toString());
                       write(";\n");
                   }
               }
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
            StringBuffer props = new StringBuffer(); 
            for (RowIterator iter = graph.getVertexTable().iterator(); iter.hasNext(); ) {
                int vertex = iter.nextRow();
                write(" ");
                writeVertex(vertex);
                if (labels != null) {
                    String label = labels.getValueAt(vertex);
                    if (label != null) {
                        props.append("label=\""+quoteString(label)+"\",");
                    }
                }

                if (shapes != null) {
                    Shape s = (Shape)shapes.get(vertex);
                    if (s != null) {
                        double w = s.getBounds2D().getWidth() / 72;
                        double h = s.getBounds2D().getHeight() / 72;
                        props.append("width=\""+w+"\",height=\""+h+"\",");
                    }
                }
                
                if (colors != null) {
                    Color c = colors.getColorAt(vertex);
                    if (c != null) {
                        String r = Integer.toHexString(c.getRed());
                        if (r.length() ==1) {
                            r = "0"+r;
                        }
                        String g = Integer.toHexString(c.getGreen());
                        if (g.length() == 1) {
                            g = "0"+g;
                        }
                        String b = Integer.toHexString(c.getBlue());
                        if (b.length()==1) {
                            b = "0"+b;
                        }
                        props.append("color=\"#"+r+g+b+"\",");
                    }
                }
                if (props.length() != 0) {
                    write("["+props.toString()+"]");
                    props.setLength(0);
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
            flush();
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
    
    public Column getLabels() {
        return labels;
    }
    
    public void setLabels(Column labels) {
        this.labels = labels;
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

    public VisualColor getColors() {
        return colors;
    }

    public void setColors(VisualColor colors) {
        this.colors = colors;
    }
}
