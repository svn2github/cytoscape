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
        System.out.println("Loading: " + _sif);
        InteractionGraph ig = InteractionGraphFactory.createFromSif(_sif);

        RootGraph g = ig.getRootGraph();

        assertEquals("num nodes", 4, g.getNodeCount());
        assertEquals("num edges", 4, g.getEdgeCount());

        System.out.println(ig.toString());
    }

    public void testLoadExpressionData() throws Exception
    {
        String sif = "fgtest.sif";
        String pv = "fg.pvals";
        System.out.println("Loading: " + sif +", " + pv);

        InteractionGraph ig = InteractionGraphFactory.createFromSif(sif);
        ig.loadExpressionData(pv);
        ig.setExpressionPvalThreshold(1e-2);

        System.out.println(ig.toString());
        
        int a = ig.name2Node("A");
        int b = ig.name2Node("B");
        int f = ig.name2Node("F");
        int z = ig.name2Node("Z");

        assertTrue("expression changes a z", ig.expressionChanges(a, z));
        assertTrue("expression changes f z", ig.expressionChanges(f, z));
        assertFalse("expression changes b z", ig.expressionChanges(b, z));
        assertFalse("expression changes a b", ig.expressionChanges(a, b));
    }
    
    public void testPDThreshold() throws Exception
    {
        String sif = "fgtest.sif";
        String ed = "fgtest.eda";
        System.out.println("Loading: " + sif +", " + ed);
        
        InteractionGraph ig = InteractionGraphFactory.createFromSif(sif);
        RootGraph g = ig.getRootGraph();
        
        assertEquals("num nodes before threshold set", 7, g.getNodeCount());
        assertEquals("num edges before threshold set", 11, g.getEdgeCount());
        
        InteractionGraphFactory.loadEdgeData(ig, ed);
        ig.setProteinDNAThreshold(0.01);
        
        assertEquals("num nodes after threshold set", 7, g.getNodeCount());
        assertEquals("num edges after threshold set", 9, g.getEdgeCount());

        int b = ig.name2Node("B");
        int z = ig.name2Node("Z");
        int f = ig.name2Node("F");
        int c = ig.name2Node("C");
        
        assertFalse("edge exists that should have been removed: B->Z",
                    g.edgeExists(b, z));
        assertFalse("edge exists that should have been removed: F->C",
                    g.edgeExists(f, c));
        
        System.out.println(ig.toString());
    }

    
        /*
    public void testReadAllSif() throws Exception
    {
        InteractionGraph ig = InteractionGraphFactory.createFromSif(_all);

        RootGraph g = ig.getRootGraph();

        assertEquals("num nodes", 5508, g.getNodeCount());
        assertEquals("num edges", 35924, g.getEdgeCount());

        }*/
}
