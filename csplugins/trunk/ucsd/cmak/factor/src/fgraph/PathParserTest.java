package fgraph;

import junit.framework.*;
import java.util.*;

public class PathParserTest extends TestCase
{
    String sif = "/cellar/users/cmak/data/yeang-data/model.sif";
    
    protected void setUp()
    {
 
    }
 
    public void testA() throws Exception
    {
        InteractionGraph ig = InteractionGraphFactory.createFromSif(sif);

        PathResult pp = PathParser.parse(ig, "yeang-valid-paths.out");
    }
}
