import junit.framework.*;
import java.util.*;

import giny.model.*;

public class DFSPathTest extends AbstractPathTest
{
    private String _sif = "test2.sif";
    private String _dfs = "pathtest.sif";
    private String _fg = "fgtest.sif";

    protected void setUp()
    {

    }

    public void testReadSif() throws Exception
    {
        System.out.println("Reading file: " + _sif);
        InteractionGraph ig = InteractionGraphFactory.createFromSif(_sif);
        ig.loadExpressionData("test.pvals");
        ig.setExpressionPvalThreshold(1e-2);
        System.out.println(ig.toString());

        PathResult results = runPath(ig, ig.name2Node("B"), 5, 3);
        System.out.println(results.toString(ig));
    }

    
    public void testReadPathSif() throws Exception
    {
        System.out.println("Reading file: " + _dfs);
        InteractionGraph ig = InteractionGraphFactory.createFromSif(_dfs);

        ig.loadExpressionData("pathtest.pvals");
        ig.setExpressionPvalThreshold(1e-2);
        System.out.println(ig.toString());

        PathResult results = runPath(ig, ig.name2Node("a"), 5, 3);
        System.out.println(results.toString(ig));
    }

    
    public void testReadFGSif() throws Exception
    {
        System.out.println("Reading file: " + _fg);
        InteractionGraph ig = InteractionGraphFactory.createFromSif(_fg);
        ig.loadExpressionData("fg.pvals");
        ig.setExpressionPvalThreshold(1e-2);
        System.out.println(ig.toString());

        int[] sources = {ig.name2Node("A"), ig.name2Node("F")};

        PathResult results = runPath(ig, sources , 5, 10);
        System.out.println(results.toString(ig));
    }


}
