
import giny.model.RootGraph;
import giny.model.Node;
import giny.model.Edge;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.Iterator;

import cern.colt.map.OpenIntObjectHashMap;


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
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        /**
        System.out.println("Printing E=" + g.getEdgeCount() 
                           + " N=" + g.getNodeCount());
        */
        int[] edges = g.getEdgeIndicesArray();
        for(int x=0; x < edges.length; x++ )
        {
            int e = edges[x];

            out.println(g.getEdgeSourceIndex(e) + " x " + g.getEdgeTargetIndex(e));
        }
        
        out.close();
   }

    public static void writeSif(RootGraph g, String filename,
                                OpenIntObjectHashMap edge2type) throws IOException
    {
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        /**
        System.out.println("Printing E=" + g.getEdgeCount() 
                           + " N=" + g.getNodeCount());
        */

        int[] edges = g.getEdgeIndicesArray();
        for(int x=0; x < edges.length; x++ )
        {
            int e = edges[x];

            String type = " x ";
            if(edge2type.containsKey(e))
            {
                type = (String) edge2type.get(e);
            }
            StringBuffer b = new StringBuffer();
            b.append(g.getEdgeSourceIndex(e));
            b.append(" ");
            b.append(type);
            b.append(" ");
            b.append(g.getEdgeTargetIndex(e));

            out.println(b.toString());
        }
        
        out.close();
   }

    
    /**
     * Write a RootGraph to a Cytoscape .sif file.
     */
    public static String toString(RootGraph g)
    {
        StringBuffer b = new StringBuffer();

        int[] edges = g.getEdgeIndicesArray();
        for(int x=0; x < edges.length; x++ )
        {
            int e = edges[x];

            b.append(g.getEdgeSourceIndex(e));
            b.append(S);
            if(g.isEdgeDirected(e))
            {
                b.append(dE);
            }
            else
            {
                b.append(uE);
            }
            b.append(S);
            b.append(g.getEdgeTargetIndex(e));
            b.append(NL);
        }
        
        return b.toString();
   }


}
