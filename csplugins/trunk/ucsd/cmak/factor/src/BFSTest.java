import junit.framework.*;
import java.util.*;

import giny.model.*;

public class BFSTest extends TestCase
{
    private String _sif = "test2.sif";
    private String _bfs = "bfstest.sif";
    private String _all = "all.sif";

    protected void setUp()
    {

    }

    public void testReadSif() throws Exception
    {
        System.out.println("Reading file: " + _sif);
        InteractionGraph ig = InteractionGraph.createFromSif(_sif);
        System.out.println(ig.toString());

        BFS b = runBFS(ig, ig.name2Node("C"));

        assertTrue("distances C", b.checkDist(new int[][] { {-1, 1}, {-2, 5}, 
                                                          {-4, 5}, {-3, 0} }));

        b.traverse(ig.name2Node("D"));
        assertTrue("distances D", b.checkDist(new int[][] {{-1, 0}, {-2, 5}, 
                                                         {-4, 5}, {-3, 5} }));
    }

    
    public void testReadBFSSif() throws Exception
    {
        System.out.println("Reading file: " + _bfs);
        InteractionGraph ig = InteractionGraph.createFromSif(_bfs);
        System.out.println(ig.toString());
        
        BFS b = runBFS(ig, ig.name2Node("s"));

        assertTrue("distances from s", b.checkDist(new int[][] { {-1, 1}, {-2, 1},
                                                                 {-3, 3}, {-4, 2},  
                                                                 {-5, 3}, {-6, 2},  
                                                                 {-7, 0}, {-8, 2} }
                                                   ));
                
    }

    /*
   public void testReadAllSif() throws Exception
    {
        System.out.println("Reading file: " + _all);
        InteractionGraph ig = InteractionGraph.createFromSif(_all);

        runBFS(ig, ig.name2Node("YGL200C"));
    }
    */

    private BFS runBFS(InteractionGraph ig, int node)
    {
        System.out.println("Running BFS from node: " + node);

        System.out.println("===");

        BFS b = new BFS(ig);
        b.traverse(node);
        b.trace();

        return b;
    }
}
