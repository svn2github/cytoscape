import junit.framework.*;
import java.util.*;

import giny.model.*;

public class MaxProductTest extends TestCase
{
    private String _sif = "test2.sif";
    private String _dfs = "pathtest.sif";
    private String _all = "all.sif";
    private String _allPvals = "/home/cmak/data/data_expts1-300_geneerr_test.pvals";
    private String _allEdge = "/home/cmak/data/all.edgeattr";

    private String _fg = "fgtest.sif";
    private String _fgpval = "fg.pvals";

    protected void setUp()
    {

    }

    public void testMaxProduct() throws Exception
    {
        System.out.println("Reading file: " + _fg);
        InteractionGraph ig = InteractionGraph.createFromSif(_fg);
        ig.loadExpressionData(_fgpval);
        ig.setExpressionPvalThreshold(1e-2);
        ig.loadEdgeData("fgtest.eda");

        System.out.println(ig.toString());

        PathResult r = runPath(ig, ig.name2Node("A"), 5, 5);
        r.print(ig);
        
        FactorGraph fg = new FactorGraph(ig, r); 

        fg.writeNodeProbs(System.out);

        fg.runMaxProduct();
        System.out.println(fg.printAdj());
    }


    /*
    public void testReadSif() throws Exception
    {
        System.out.println("Reading file: " + _sif);
        InteractionGraph ig = InteractionGraph.createFromSif(_sif);
        ig.setExpressionData("test.pvals");
        ig.setPvalThreshold(1e-2);
        System.out.println(ig.toString());

        DFSPath d = runPath(ig, ig.name2Node("B"), 5, 3);
        
        FactorGraph fg = new FactorGraph(ig, d.getEdge2PathMap(), 
                                         d.getTarget2PathMap());
        System.out.println(fg);
        fg.writeSif("fgtest2_out.sif");
    }

    
    
    public void testReadPathSif() throws Exception
    {
        System.out.println("Reading file: " + _dfs);
        InteractionGraph ig = InteractionGraph.createFromSif(_dfs);

        ig.setExpressionData("pathtest.pvals");
        ig.setPvalThreshold(1e-2);
        System.out.println(ig.toString());
        
        DFSPath d = runPath(ig, ig.name2Node("a"), 5, 6);
        
        FactorGraph fg = new FactorGraph(ig, d.getEdge2PathMap(), 
                                         d.getTarget2PathMap());
        System.out.println(fg);
        fg.writeSif("fgpathtest_out.sif");
    }
    */

    /*
    public void testReadAllSif() throws Exception
    {
        
        System.out.println("Reading file: " + _all);
        InteractionGraph ig = InteractionGraph.createFromSif(_all);

        System.out.println("Reading pvals: " + _allPvals);
        ig.loadExpressionData(_allPvals);
        ig.setExpressionPvalThreshold(.8);
        ig.loadEdgeData(_allEdge);
        //System.out.println(ig.toString());

        System.out.println("Running DFS");
        PathResult paths = runPath(ig, ig.name2Node("YGL200C"), 3, 539);
        //runPath(ig, ig.name2Node("YGL200C"), 4, 11532);
        //runPath(ig, ig.name2Node("YGL200C"), 5, 242559);

        FactorGraph fg = new FactorGraph(ig, paths);

        //System.out.println(fg);
        fg.writeSif("fgall_facgraph.sif");
        fg.writeNodeAttr("fgall_facgraph");
        fg.writeNodeProbs(System.out);
    }
    */    
    protected PathResult runPath(InteractionGraph ig, int source, 
                                 int depth, int expected)
    {
        System.out.println("Finding paths on graph:");
        System.out.println("===");
        
        DFSPath d = new DFSPath(ig);

        PathResult r = d.findPaths(source, depth);

        assertEquals("number of paths", expected, r.getPathCount());

        return r;
    }
}
