import junit.framework.*;
import java.util.*;

public class SubmodelTest extends TestCase
{
    Submodel s1;

    protected void setUp()
    {
        s1 = new Submodel();
    }

    public void testAccepts()
    {
        assertTrue("model accepts DIR?",  s1.acceptsType(NodeType.DIR));
        assertTrue("model accepts SIGN?",  s1.acceptsType(NodeType.SIGN));
        assertTrue("model accepts KO?",  s1.acceptsType(NodeType.KO));

        assertTrue("model accepts EDGE?",  !s1.acceptsType(NodeType.EDGE));
        assertTrue("model accepts PATH_ACTIVE?",  !s1.acceptsType(NodeType.PATH_ACTIVE));

    }

    public void testEdge()
    {
        s1.addEdge(makeEdge(10));
        s1.addEdge(makeEdge(20));
        s1.addEdge(makeEdge(30));
        s1.addEdge(makeEdge(40));

        Submodel s2 = new Submodel();

        s2.addEdge(makeEdge(40));
        s2.addEdge(makeEdge(50));
        s2.addEdge(makeEdge(60));
        s2.addEdge(makeEdge(70));

    }

    public void testInvariant()
    {
        assertTrue("default state is not invariant", !s1.isInvariant());
        s1.setInvariant(true);
        assertTrue("set invariant works", s1.isInvariant());
    }

    public void testIndependent()
    {
        s1.setIndependentVar(1);
        assertTrue("setting independent var makes model non-invariant", !s1.isInvariant());

        s1.setInvariant(true);
        assertTrue("model cannot be made invariant if independent var has been set", !s1.isInvariant());


    }

    
    public void testOverlap()
    {
        s1.addVar(10);
        s1.addVar(20);
        s1.addVar(30);
        s1.addVar(40);

        Submodel s2 = new Submodel();

        s2.addVar(40);
        s2.addVar(50);
        s2.addVar(60);
        s2.addVar(70);

        assertTrue("overlap ok s1.overlaps(s2)", s1.overlaps(s2));
        assertTrue("overlap ok s2.overlaps(s1)", s2.overlaps(s1));
        
    }

    public void testMerge()
    {
        s1.addVar(1);
        s1.addVar(2);
        s1.addVar(3);
        s1.addVar(4);

        Submodel s2 = new Submodel();

        s2.addVar(4);
        s2.addVar(5);
        s2.addVar(6);
        s2.addVar(7);

        s1.merge(s2);

        for(int x=1; x <= 7; x++)
        {
            assertTrue("s1 contains " + x, s1.containsVar(x));
        }
        
    }
    
    
    private AnnotatedEdge makeEdge(int startIndex)
    {
        AnnotatedEdge ae = new AnnotatedEdge(startIndex);
        ae.fgIndex = startIndex + 1;
        ae.signIndex = startIndex + 2;
        ae.dirIndex = startIndex + 3;
        return ae;
    }
}
