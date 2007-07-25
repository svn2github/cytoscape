/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/
package infovis.example.visualization.graphontreemap;

import infovis.*;
import infovis.column.IntColumn;
import infovis.column.StringColumn;
import infovis.example.ExampleRunner;
import infovis.graph.DefaultGraph;
import infovis.graph.io.HTMLGraphReader;
import infovis.table.DefaultTable;
import infovis.tree.DefaultTree;
import infovis.tree.io.FileListTreeReader;
import infovis.utils.RowIterator;

import java.io.*;

/**
 * Class GraphOnTreemapExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 */
public class GraphOnTreemapExample {

    public static void main(String[] args) {
        ExampleRunner example =
            new ExampleRunner(args, "GraphOnTreemapExample");
        if (example.fileCount() > 2) {
            System.err.println(
                "Syntax: GraphAsTreemapExample <root-url> [start-name]");
            System.exit(1);
        }

        String url;
        String startPath;
        if (example.fileCount() == 0) {
            url = "doc/api/infovis/panel/";
            startPath = "package-summary.html";
        }
        else if (example.fileCount() == 1) {
            url = example.getArg(0);
            int index = url.lastIndexOf('/');
            if (index == -1) {
                startPath = "index.html";
            }
            else {
                startPath = url.substring(index+1);
                url = url.substring(0, index+1);
            }
        }
        else {
            url = example.getArg(0);
            startPath = example.getArg(1);
        }
        
        Graph g = new DefaultGraph();
        HTMLGraphReader reader = new HTMLGraphReader(url, g);

        reader.setLog(new PrintStream(System.out));
        reader.add(startPath);
        if (!reader.load()) {
            System.out.println("Cannot load url " + url);
            System.exit(1);
        }

        Tree tree;
        try {
            tree = extractTree(g, reader.getBase().toExternalForm());

            Table edges = extractEdges(g, tree);

            GraphOnTreemapVisualization visualization =
                new GraphOnTreemapVisualization(
                    tree,
                    edges);

            StringColumn nameColumn =
                StringColumn.findColumn(
                    g.getVertexTable(),
                    HTMLGraphReader.COLUMN_NAME);

            visualization.setVisualColumn(
                Visualization.VISUAL_LABEL,
                nameColumn);
            visualization.setVisualColumn(
                    Visualization.VISUAL_COLOR,
                    g.getVertexTable().getColumn("ref"));
            nameColumn.setExtend(1, null); // remove name of root directory.
            example.createFrame(visualization);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Tree extractTree(Graph g, String prefix)
        throws IOException {
        DefaultTree tree = new DefaultTree();
        StringColumn urlColumn =
            StringColumn.findColumn(
                g.getVertexTable(),
                HTMLGraphReader.COLUMN_URL);
        IntColumn vertexColumn = IntColumn.findColumn(tree, "vertex");

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Writer out = new OutputStreamWriter(bout);
        //StringBuffer fileList = new StringBuffer();
        for (int i = 0; i < urlColumn.size(); i++) {
            out.write(urlColumn.get(i));
            out.write('\n');
        }
        out.flush();
        byte[] b = bout.toByteArray();
        InputStream in = new ByteArrayInputStream(b);
        FileListTreeReader reader =
            new FileListTreeReader(in, "fileList", tree);
        reader.setPrefix(prefix);
        reader.setIndexColumn(vertexColumn);

        if (reader.load()) {
            return tree;
        }
        return null;
    }

    public static Table extractEdges(Graph g, Tree tree) {
        DefaultTable edges = new DefaultTable();
        IntColumn inColumn =
            IntColumn.findColumn(edges, DefaultGraph.INVERTEX_COLUMN);
        IntColumn outColumn =
            IntColumn.findColumn(edges, DefaultGraph.OUTVERTEX_COLUMN);
        IntColumn vertexColumn = IntColumn.findColumn(tree, "vertex");
        IntColumn refColumn = IntColumn.findColumn(tree, "degree");
        int vertex2node[] = new int[vertexColumn.size()];
        int node;
        for (node = 0; node < vertexColumn.size(); node++) {
            if (vertexColumn.isValueUndefined(node))
                continue;
            vertex2node[vertexColumn.get(node)] = node;
        }
        for (node = 0; node < vertexColumn.size(); node++) {
            if (vertexColumn.isValueUndefined(node))
                continue;
            int vertex = vertexColumn.get(node);
            refColumn.setExtend(node, g.getOutDegree(vertex) + g.getInDegree(vertex));
            for (RowIterator iter = g.outEdgeIterator(vertex); iter.hasNext(); ) {
                int edge = iter.nextRow();
                inColumn.add(node);
                outColumn.add(vertex2node[g.getSecondVertex(edge)]);
            }
        }
        return edges;
    }
}
