
import giny.model.RootGraph;
import giny.model.Node;
import giny.model.Edge;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.StringWriter;

import java.util.Iterator;

import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.OpenIntIntHashMap;
import cern.colt.list.IntArrayList;



public class CyUtil
{
    private static final String NL = "\n";
    private static final String S = " ";

    private static final String uE = "pp";
    private static final String dE = "pd";

    /**
     * Write a RootGraph to a Cytoscape .sif file.
     */
    public static void writeSif(RootGraph g, String filename) throws IOException
    {
        writeSif(g, filename, null);
    }

    public static void writeSif(RootGraph g, String filename,
                                OpenIntObjectHashMap edge2type) throws IOException
    {
        writeSif(g, filename, edge2type, null);
    }

    public static void writeSif(RootGraph g, String filename,
                                OpenIntObjectHashMap edge2type,
                                OpenIntIntHashMap nodeMap) throws IOException
    {
        PrintWriter out = new PrintWriter(new FileWriter(filename));
        writeSif(g, out, edge2type, nodeMap);
    }

        
    public static void writeSif(RootGraph g, PrintWriter out,
                                OpenIntObjectHashMap edge2type,
                                OpenIntIntHashMap nodeMap)
    {
        /**
        System.out.println("Printing E=" + g.getEdgeCount() 
                           + " N=" + g.getNodeCount());
        */

        int[] edges = g.getEdgeIndicesArray();
        for(int x=0; x < edges.length; x++ )
        {
            int e = edges[x];

            String type;
            
            if(g.isEdgeDirected(e))
            {
                type = " d ";
            }
            else
            {
                type = " u ";
            }
            
            if(edge2type != null && edge2type.containsKey(e))
            {
                type = (String) edge2type.get(e);
            }

            int source = g.getEdgeSourceIndex(e);
            int target = g.getEdgeTargetIndex(e); 

            if(nodeMap != null)
            {
                if(nodeMap.containsKey(source))
                {
                    source = nodeMap.get(source);
                }
                if(nodeMap.containsKey(target))
                {
                    target = nodeMap.get(target);
                }
            }
            
            StringBuffer b = new StringBuffer();
            b.append(source);
            b.append(" ");
            b.append(type);
            b.append(" ");
            b.append(target);

            out.println(b.toString());
        }
        
        out.close();
   }

        /**
     * Write a RootGraph to a string.
     */
    public static String toString(RootGraph g)
    {
        return toString(g, null);
    }

    
    /**
     * Write a RootGraph to a string.
     */
    public static String toString(RootGraph g, OpenIntIntHashMap nodeMap)
    {
        StringWriter s = new StringWriter();
        writeSif(g, new PrintWriter(s), null, nodeMap);

        return s.toString();
   }


}
