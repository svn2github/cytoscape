import junit.framework.*;
import java.util.*;

import giny.model.*;

public class InteractionGraphTest extends TestCase
{
    private String _sif = "test2.sif";
    private String _all = "all.sif";

    protected void setUp()
    {

    }

    public void testReadSif() throws Exception
    {
        InteractionGraph ig = InteractionGraph.createFromSif(_sif);

        RootGraph g = ig.getRootGraph();

        assertEquals("num nodes", 4, g.getNodeCount());
        assertEquals("num edges", 4, g.getEdgeCount());

        System.out.println(ig.toString());
    }

    
    public void testReadAllSif() throws Exception
    {
        InteractionGraph ig = InteractionGraph.createFromSif(_all);

        RootGraph g = ig.getRootGraph();

        assertEquals("num nodes", 5508, g.getNodeCount());
        assertEquals("num edges", 35924, g.getEdgeCount());

    }
}
