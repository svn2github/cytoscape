import junit.framework.*;
import java.util.*;

import giny.model.*;

public class PathTest extends TestCase
{
    private String _sif = "test2.sif";
    private String _dfs = "pathtest.sif";
    private String _all = "all.sif";

    protected void setUp()
    {

    }

    public void testReadSif() throws Exception
    {
        System.out.println("Reading file: " + _sif);
        InteractionGraph ig = InteractionGraphFactory.createFromSif(_sif);
        //System.out.println(ig.toString());

        List paths = runPath(ig, ig.name2Node("B"), 5);

        printPaths(paths);
    }

    
    public void testReadPathSif() throws Exception
    {
        System.out.println("Reading file: " + _dfs);
        InteractionGraph ig = InteractionGraphFactory.createFromSif(_dfs);
        //System.out.println(ig.toString());

        List paths = runPath(ig, ig.name2Node("a"), 10);

        printPaths(paths);
    }

    
    
    public void testReadAllSif() throws Exception
    {
        
        System.out.println("Reading file: " + _all);
        InteractionGraph ig = InteractionGraphFactory.createFromSif(_all);
        //System.out.println(ig.toString());

        runPath(ig, ig.name2Node("YGL200C"), 55746);
    }
    
    
    private List runPath(InteractionGraph ig, int source, int expected)
    {
        System.out.println("Finding paths on graph:");
        System.out.println("===");

        DFS d = new DFS(ig.getRootGraph());

        List paths = d.genPaths(source, 5);

        assertEquals("number of paths", expected, paths.size());

        return paths;
    }

    private void printPaths(List paths)
    {
        for(int p=0; p < paths.size(); p++)
        {
            int[] path = (int[]) paths.get(p);
            StringBuffer b = new StringBuffer("> ");
            String space = " ";
            for(int x=0; x < path.length; x++)
            {
                b.append(path[x]);
                b.append(space);
            }
            
            System.out.println(b.toString());
        }
    }
}
