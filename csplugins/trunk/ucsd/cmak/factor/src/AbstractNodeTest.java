import junit.framework.*;
import java.util.*;

import cern.colt.bitvector.BitVector;

public abstract class AbstractNodeTest extends TestCase
{
    
    public AbstractNodeTest(String s)
    {
        super(s);
    }
    
    protected void setUp()
    {
    }


    protected void checkProbs10(ProbTable pt, double p1, double p0)
    {
        double sum = p1 + p0;
        
        System.out.println(pt.toStringDetailed());

        assertEquals("prob(ZERO)", p0 / sum, pt.prob(State.ZERO), 0.000000001);
        assertEquals("prob(ONE)", p1 / sum, pt.prob(State.ONE), 0.000000001);
    }


    protected void checkProbsPM(ProbTable pt, double pP, double pM)
    {
        double sum = pP + pM;
        
        System.out.println(pt.toStringDetailed());
        
        assertEquals("prob(PLUS)", pP / sum, pt.prob(State.PLUS), 0.000000001);
        assertEquals("prob(MINUS)", pM / sum, pt.prob(State.MINUS), 0.000000001);
    }

    
    protected void checkProbsPMZ(ProbTable pt, double pP, double pM, double pZ)
    {
        double sum = pP + pM + pZ;
        
        System.out.println(pt.toStringDetailed());
        
        assertEquals("prob(PLUS)", pP / sum, pt.prob(State.PLUS), 0.000000001);
        assertEquals("prob(MINUS)", pM / sum, pt.prob(State.MINUS), 0.000000001);
        assertEquals("prob(ZERO)", pZ / sum, pt.prob(State.ZERO), 0.000000001);
    }
    
    public EdgeMessage pt2em(ProbTable pt)
    {
        EdgeMessage em = new EdgeMessage(null, null);
        em.v2f(pt);
        em.f2v(pt);

        return em;
    }


    public EdgeMessage pt2em(ProbTable pt, State dir)
    {
        EdgeMessage em = new EdgeMessage(null, null, dir);
        em.v2f(pt);
        em.f2v(pt);

        return em;
    }

    
        
    protected ProbTable createSign(double p, double m)
    {
        return createPlusMinus(StateSet.SIGN, p, m);
    }

    protected ProbTable createDir(double p, double m)
    {
        return createPlusMinus(StateSet.DIR, p, m);
    }

    protected ProbTable createPathActive(double p1, double p0)
    {
        return createOneZero(StateSet.PATH_ACTIVE, p1, p0);
    }

    protected ProbTable createEdge(double p1, double p0)
    {
        return createOneZero(StateSet.EDGE, p1, p0);
    }

    protected ProbTable createKO(double z, double p,  double m)
    {
        ProbTable s1 = new ProbTable(StateSet.KO);
        double[] d = new double[3];
        d[StateSet.KO.getIndex(State.ZERO)] = z;
        d[StateSet.KO.getIndex(State.PLUS)] = p;
        d[StateSet.KO.getIndex(State.MINUS)] = m;
        s1.init(d);

        return s1;
    }

    private ProbTable createPlusMinus(StateSet ss, double p, double m)
    {
        ProbTable s1 = new ProbTable(ss);
        double[] d = new double[2];
        d[ss.getIndex(State.PLUS)] = p;
        d[ss.getIndex(State.MINUS)] = m;
        s1.init(d);

        return s1;
    }

    private ProbTable createOneZero(StateSet ss, double one, double zero)
    {
        ProbTable s1 = new ProbTable(ss);
        double[] d = new double[2];
        d[ss.getIndex(State.ONE)] = one;
        d[ss.getIndex(State.ZERO)] = zero;
        s1.init(d);

        return s1;
    }



}
