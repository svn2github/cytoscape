package fgraph;

import junit.framework.*;
import java.util.*;

import giny.model.*;

public abstract class AbstractPathTest extends TestCase
{
    protected PathResult runPath(InteractionGraph ig, int source,
                                 int depth, int expected)
    {
        return runPath(ig, new int[] {source}, depth, expected);
    }

    protected PathResult runPath(InteractionGraph ig, int[] source,
                                 int depth, int expected)
    {
        System.out.println("Finding paths on graph:");
        System.out.println("===");
        
        DFSPath d = new DFSPath(ig);

        PathResult p = d.findPaths(source, depth);

        assertEquals("number of paths", expected, p.getPathCount());

        return p;
    }
}
