import junit.framework.*;

import java.util.*;
import java.io.*;

import giny.model.*;

public class MaxProductTest extends AbstractPathTest
{
    private String _sif = "test2.sif";
    private String _dfs = "pathtest.sif";
    private String _all = "all.sif";
    private String _allPvals = "/home/cmak/data/data_expts1-300_geneerr_test.pvals";
    private String _allEdge = "/home/cmak/data/all.edgeattr";

    private String outputDir = "testOut";

    protected void setUp()
    {
        File f = new File(outputDir);
        if(!f.exists())
        {
            f.mkdirs();
        }
    }

    /*
    
    public void testFG2() throws Exception
    {
        MaxProduct mp = new PrintFGMaxProduct();
        mp.setInteractionFile("fgtest.sif");
        mp.setMaxPathLength(5);
        mp.setExpressionFile("fg2.pvals", 1e-2);
        mp.setEdgeFile("fgtest.eda", -1);
        mp.run(outputDir, "fgtest_fg2");
        
    }
    */

    public void testFG2Decompose() throws Exception
    {
        MaxProduct mp = new MaxProduct();
        mp.setInteractionFile("fgtest.sif");
        mp.setMaxPathLength(5);
        mp.setKOExplainCutoff(2);
        mp.setExpressionFile("fg2.pvals", 1e-2);
        mp.setEdgeFile("fgtest.eda", -1);
        mp.run(outputDir, "fgtest_fg2");
        
    }

    
    
    /*    public void testFG3() throws Exception
    {
        MaxProduct mp = new PrintFGMaxProduct();
        mp.setInteractionFile("fgtest.sif");
        mp.setMaxPathLength(5);
        mp.setExpressionFile("fg3.pvals", 1e-2);
        mp.setEdgeFile("fgtest.eda", -1);
        mp.run(outputDir, "fgtest_fg3");
        
        }*/
}
