import junit.framework.*;
import java.util.*;

import giny.model.*;

public class DFSTest extends TestCase
{
    private String _sif = "test2.sif";
    private String _dfs = "dfstest.sif";
    private String _all = "all.sif";

    protected void setUp()
    {

    }

    public void testReadSif() throws Exception
    {
        System.out.println("Reading .sif: " + _sif);
        InteractionGraph ig = InteractionGraph.createFromSif(_sif);
        System.out.println(ig.toString());

        DFS d = runDFS(ig.getRootGraph());

        assertTrue("pred", d.checkPred(new int[][] { {-1, 0}, {-2, 0},
                                                     {-3, -4}, {-4, -2}}));
        assertTrue("discovery time", 
                   d.checkDiscovery(new int[][] { {-1, 1}, {-2, 3},
                                                  {-3, 5}, {-4, 4}}));
        assertTrue("finish time", 
                   d.checkFinish(new int[][] { {-1, 2}, {-2, 8},
                                               {-3, 6}, {-4, 7}}));
    }

    
    public void testReadDFSSif() throws Exception
    {
        System.out.println("Reading .sif: " + _dfs);
        InteractionGraph ig = InteractionGraph.createFromSif(_dfs);
        System.out.println(ig.toString());

        runDFS(ig.getRootGraph());
    }

   public void testReadAllSif() throws Exception
    {
        System.out.println("Reading .sif: " + _all);
        InteractionGraph ig = InteractionGraph.createFromSif(_all);

        //runDFS(ig.getRootGraph());
    }


    private DFS runDFS(RootGraph g)
    {
        System.out.println("Running DFS on graph:");
        System.out.println("===");

        DFS d = new DFS(g);
        d.traverse();
        d.trace();

        return d;
    }
}
