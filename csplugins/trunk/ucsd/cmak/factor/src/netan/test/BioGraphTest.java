package netan.test;

import junit.framework.*;
import java.util.*;

import netan.BioGraph;

public class BioGraphTest extends TestCase
{
    String sif = "/cellar/users/cmak/data/location/ymp-27Feb05-p0.05.sif";
    String sif2= "/cellar/users/cmak/data/buffering/all-p0.02-27Feb05.sif";
    
    protected void setUp() throws Exception
    {
    }
 
    public void testGraph() throws Exception
    {
        BioGraph g = new BioGraph(sif);
        assertTrue("Edge exists YDR216W pd YPR025C",
                   g.edgeExists("YDR216W", "YPR025C", "pd"));


        assertTrue("Edge should not exist YPR025C pd YDR216W",
                   !g.edgeExists("YPR025C", "YDR216W", "pd"));
        
        
        assertTrue("Edge exists YBR123C pp YBR123C",
                   g.edgeExists("YAL001C", "YBR123C", "pp"));
        
        assertTrue("Edge exists YBR123C pp YBR123C",
                   g.edgeExists("YBR123C", "YAL001C", "pp"));
        
         
        
        assertEquals("Num nodes", 5411, g.numNodes());
        assertEquals("Num edges", 35941, g.numEdges());
    }

    
    public void testGraph2() throws Exception
    {
        BioGraph g = new BioGraph(sif2);
        assertTrue("Edge exists YDR216W pd YPR025C",
                   g.edgeExists("YDR216W", "YPR025C", "pd"));

        assertTrue("Edge should not exist YPR025C pd YDR216W",
                   !g.edgeExists("YPR025C", "YDR216W", "pd"));
        
        
        assertTrue("Edge exists YBR123C pp YBR123C",
                   g.edgeExists("YAL001C", "YBR123C", "pp"));
        
        assertTrue("Edge exists YBR123C pp YBR123C",
                   g.edgeExists("YBR123C", "YAL001C", "pp"));
        
         
        assertEquals("Num nodes", 5075, g.numNodes());
        assertEquals("Num edges", 22357, g.numEdges());
    }
       
}
