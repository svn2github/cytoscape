
import giny.model.RootGraph;
import giny.model.Node;
import giny.model.Edge;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import java.util.Iterator;

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
        for(Iterator it = g.edgesIterator(); it.hasNext(); )
        {
            Edge e = (Edge) it.next();

            Node s = (Node) e.getSource();
            Node t = (Node) e.getTarget();

            /*
            System.out.println("Writing E=" + e.getIdentifier()
                               + " from=" + s.getIdentifier() + " to=" 
                               + t.getIdentifier());
            */
            out.println(s.getIdentifier() + " x " + t.getIdentifier());
        }
        
        out.close();
   }

    /**
     * Write a RootGraph to a Cytoscape .sif file.
     */
    public static String toString(RootGraph g)
    {
        StringBuffer b = new StringBuffer();

        for(Iterator it = g.edgesIterator(); it.hasNext(); )
        {
            Edge e = (Edge) it.next();

            Node s = (Node) e.getSource();
            Node t = (Node) e.getTarget();

            b.append(s.getRootGraphIndex());
            b.append(S);
            if(e.isDirected())
            {
                b.append(dE);
            }
            else
            {
                b.append(uE);
            }
            b.append(S);
            b.append(t.getRootGraphIndex());
            b.append(NL);
        }
        
        return b.toString();
   }


}
