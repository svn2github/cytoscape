package fgraph;

import junit.framework.*;
import java.util.*;

import cern.colt.bitvector.BitVector;

public abstract class AbstractNodeTest extends TestCase
{

    protected double delta = 0.0000001;
    
    public AbstractNodeTest()
    {
        super();
    }
    
    public AbstractNodeTest(String s)
    {
        super(s);
    }
    
    protected void setUp() throws Exception
    {
    }


    protected void checkProbs10(ProbTable pt, double p1, double p0)
    {
        double sum = p1 + p0;
        
        System.out.println(pt.toStringDetailed());

        assertEquals("prob(ZERO)", p0 / sum, pt.prob(State.ZERO), delta);
        assertEquals("prob(ONE)", p1 / sum, pt.prob(State.ONE), delta);
    }


    protected void checkProbsPM(ProbTable pt, double pP, double pM)
    {
        double sum = pP + pM;
        
        System.out.println(pt.toStringDetailed());
        
        assertEquals("prob(PLUS)", pP / sum, pt.prob(State.PLUS), delta);
        assertEquals("prob(MINUS)", pM / sum, pt.prob(State.MINUS), delta);
    }

    
    protected void checkProbsPMZ(ProbTable pt, double pP, double pM, double pZ)
    {
        double sum = pP + pM + pZ;
        
        System.out.println(pt.toStringDetailed());
        
        assertEquals("prob(PLUS)", pP / sum, pt.prob(State.PLUS), delta);
        assertEquals("prob(MINUS)", pM / sum, pt.prob(State.MINUS), delta);
        assertEquals("prob(ZERO)", pZ / sum, pt.prob(State.ZERO), delta);
    }
    
    public EdgeMessage pt2em(ProbTable pt, NodeType t)
    {
        EdgeMessage em = new EdgeMessage(t, 0, 0);
        em.v2f(pt);
        em.f2v(pt);

        return em;
    }


    public EdgeMessage pt2em(ProbTable pt, NodeType t, State dir)
    {
        EdgeMessage em = new EdgeMessage(t, 0, 0, dir);
        em.v2f(pt);
        em.f2v(pt);

        return em;
    }

    
        
    protected ProbTable createSign(double p, double m)
    {
        return NodeFactory.createSign(m ,p);
    }

    protected ProbTable createDir(double p, double m)
    {
        return NodeFactory.createDir(m, p);
    }

    protected ProbTable createPathActive(double p0, double p1)
    {
        return NodeFactory.createPathActive(p0, p1);
    }

    protected ProbTable createEdge(double p0, double p1)
    {
        return NodeFactory.createEdge(p0, p1);
    }

    protected ProbTable createKO(double z, double p, double m)
    {
        return NodeFactory.createKO(z, p, m);
    }

}
