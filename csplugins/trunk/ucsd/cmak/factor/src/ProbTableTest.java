import junit.framework.*;
import java.util.*;

public class ProbTableTest extends TestCase
{
    protected void setUp()
    {

    }

    public void testEquals()
    {
        System.out.println("### testEquals");
        double ep = 0.001;
        
        ProbTable pt1 = create(StateSet.EDGE, 0.1, 0.9);
        ProbTable pt2 = create(StateSet.EDGE, 0.1, 0.9);
        ProbTable pt3 = create(StateSet.EDGE, 0.2, 0.8);
        ProbTable pt4 = create(StateSet.EDGE, 0.101, 0.899);
        ProbTable pt5 = create(StateSet.EDGE, 0.1001, 0.8999);

        assertTrue("equal probs", pt1.equals(pt2, ep));
        assertTrue("unequal probs", !pt1.equals(pt3, ep));
        assertTrue("equal probs small difference", !pt1.equals(pt4, ep));
        assertTrue("equal probs small difference", pt1.equals(pt5, ep));
    }
    
    // helper to create a ptable from a StateSet with 2 states.
    // p0 is expected to be the max
    public void helper2(StateSet s, double p0, double p1)
    {
        ProbTable pt1 = create(s, p0, p1);
        State s0 = s.getState(0);
        State s1 = s.getState(1);
        
        assertTrue("uniqueMax", pt1.hasUniqueMax());
        assertEquals("max", p0, pt1.max(), .01);
        assertEquals("max state", s0, pt1.maxState());
        assertEquals("state set", s, pt1.stateSet());
        assertEquals("p(" + s0 + ")", p0, pt1.prob(s0), .01);
        assertEquals("p(" + s1 + ")", p1, pt1.prob(s1), .01);
        
    }

    // helper to create a ptable from a StateSet with 2 states.
    // p0 is expected to be the max
    public void helper_2max(StateSet s, double p0, double p1)
    {
        ProbTable pt1 = create(s, p0, p1);
        State s0 = s.getState(0);
        State s1 = s.getState(1);
        
        assertFalse("no uniqueMax", pt1.hasUniqueMax());
        assertEquals("max", p0, pt1.max(), .01);
        assertEquals("max state", s0, pt1.maxState());
        assertEquals("state set", s, pt1.stateSet());
        assertEquals("p(" + s0 + ")", p0, pt1.prob(s0), .01);
        assertEquals("p(" + s1 + ")", p1, pt1.prob(s1), .01);
        
    }



    public void testEdge_2Max()
    {
        StateSet ss = StateSet.EDGE;

        helper_2max(ss, .5, .5);
    }


    public void testEdge()
    {
        StateSet ss = StateSet.EDGE;

        helper2(ss, .8, .2);
    }


    public void testPath()
    {
        StateSet ss = StateSet.PATH_ACTIVE;

        helper2(ss, .8, .2);
    }


    public void testDir()
    {
        StateSet ss = StateSet.DIR;

        helper2(ss, .8, .2);
    }


    public void testSign()
    {
        StateSet ss = StateSet.SIGN;

        helper2(ss, .8, .2);
    }


    // helper to create a ptable from a StateSet with 2 states.
    // p0 is expected to be the max
    public void testKO()
    {
        ProbTable pt1 = createKO(.3, .6, .1);
        
        assertTrue("uniqueMax", pt1.hasUniqueMax());
        assertEquals("max", .6, pt1.max(), .01);
        assertEquals("max state", State.PLUS, pt1.maxState());
        assertEquals("state set", StateSet.KO, pt1.stateSet());
        assertEquals("p(PLUS)", .6, pt1.prob(State.PLUS), .01);
        assertEquals("p(MINUS)", .1, pt1.prob(State.MINUS), .01);
        assertEquals("p(ZERO)", .3, pt1.prob(State.ZERO), .01);
    }

    // helper to create a ptable from a StateSet with 2 states.
    // p0 is expected to be the max
    public void testKO_2Max()
    {
        ProbTable pt1 = createKO(.4, .2, .4);
        
        assertFalse("uniqueMax", pt1.hasUniqueMax());
        assertEquals("max", .4, pt1.max(), .01);
        assertEquals("max state", State.ZERO, pt1.maxState());
        assertEquals("state set", StateSet.KO, pt1.stateSet());
        assertEquals("p(PLUS)", .2, pt1.prob(State.PLUS), .01);
        assertEquals("p(MINUS)", .4, pt1.prob(State.MINUS), .01);
        assertEquals("p(ZERO)", .4, pt1.prob(State.ZERO), .01);
    }
    
    public void testMultiply()
    {
        StateSet ss = StateSet.EDGE;

        ProbTable pt1 = create(ss, .8, .2);
        ProbTable pt2 = create(ss, .25, .75);
        
        pt1.multiplyBy(pt2);
                
        assertTrue("uniqueMax", pt1.hasUniqueMax());
        assertEquals("max", .2/.35, pt1.max(), .0001);
        assertEquals("max state", State.ZERO, pt1.maxState());
        assertEquals("state set", StateSet.EDGE, pt1.stateSet());
        assertEquals("p(ZERO)", .2/.35, pt1.prob(State.ZERO), .0001);
        assertEquals("p(ONE)", .15/.35, pt1.prob(State.ONE), .0001);
    }
        
    private ProbTable create(StateSet s, double p0, double p1)
    {
        ProbTable t = new ProbTable(s);

        double[] p = new double[s.size()];

        Iterator it=s.iterator();

        p[s.getIndex((State)it.next())] = p0;
        p[s.getIndex((State)it.next())] = p1;

        t.init(p);
        System.out.println("### created ProbTable");
        System.out.println(t.toStringDetailed());
        System.out.println("###");
        return t;
    }

    private ProbTable createKO(double p0, double pPlus, double pMinus)
    {
        StateSet s = StateSet.KO;
        ProbTable t = new ProbTable(s);

        double[] p = new double[s.size()];

        p[s.getIndex(State.ZERO)] = p0;
        p[s.getIndex(State.PLUS)] = pPlus;
        p[s.getIndex(State.MINUS)] = pMinus;

        t.init(p);
        System.out.println("### created ProbTable");
        System.out.println(t.toStringDetailed());
        System.out.println("###");
        return t;
    }
}
