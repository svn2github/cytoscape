package fgraph;

import junit.framework.*;
import java.util.*;

import giny.model.*;

public class AllPathTest extends AbstractPathTest
{
    private String _all = "all.sif";
    private String _allPvals = "/home/cmak/data/data_expts1-300_geneerr_test.pvals";

    protected void setUp()
    {

    }

    
    public void testReadAllSif() throws Exception
    {
        
        System.out.println("Reading file: " + _all);
        InteractionGraph ig = InteractionGraphFactory.createFromSif(_all);

        System.out.println("Reading pvals: " + _allPvals);

        ig.setExpressionData(CytoscapeExpressionData.load(_allPvals, 0.8));
        
        
        //System.out.println(ig.toString());

        System.out.println("Running DFS");
        //runPath(ig, ig.name2Node("YGL200C"), 3, 0);
        runPath(ig, ig.name2Node("YGL200C"), 4, 2018);
        //runPath(ig, ig.name2Node("YGL200C"), 5, 51199);
    }
}
