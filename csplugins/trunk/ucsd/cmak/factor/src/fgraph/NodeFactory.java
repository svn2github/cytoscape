package fgraph;

import junit.framework.*;
import java.util.*;

import cern.colt.bitvector.BitVector;

public class NodeFactory
{

    protected double delta = 0.0000001;
    
    private NodeFactory()
    {
    }

    /*
    public static EdgeMessage pt2em(ProbTable pt)
    {
        EdgeMessage em = new EdgeMessage(null, 0, 0);
        em.v2f(pt);
        em.f2v(pt);

        return em;
    }

    public static EdgeMessage pt2em(ProbTable pt, State dir)
    {
        EdgeMessage em = new EdgeMessage(null, 0, 0, dir);
        em.v2f(pt);
        em.f2v(pt);

        return em;
    }
    */
    
    public static ProbTable createSign(double m, double p)
    {
        return createMinusPlus(StateSet.SIGN, m, p);
    }

    public static ProbTable createDir(double m, double p)
    {
        return createMinusPlus(StateSet.DIR, m, p);
    }

    public static ProbTable createPathActive(double p0, double p1)
    {
        return createZeroOne(StateSet.PATH_ACTIVE, p0, p1);
    }

    public static ProbTable createEdge(double p0, double p1)
    {
        return createZeroOne(StateSet.EDGE, p0, p1);
    }

    public static ProbTable createKO(double z, double p, double m)
    {
        ProbTable s1 = new ProbTable(StateSet.KO);
        double[] d = new double[3];
        d[StateSet.KO.getIndex(State.ZERO)] = z;
        d[StateSet.KO.getIndex(State.PLUS)] = p;
        d[StateSet.KO.getIndex(State.MINUS)] = m;
        s1.init(d);

        return s1;
    }


    
    private static ProbTable createMinusPlus(StateSet ss, double m, double p)
    {
        ProbTable s1 = new ProbTable(ss);
        double[] d = new double[2];
        d[ss.getIndex(State.PLUS)] = p;
        d[ss.getIndex(State.MINUS)] = m;
        s1.init(d);

        return s1;
    }


    private static ProbTable createZeroOne(StateSet ss, double zero, double one)
    {
        ProbTable s1 = new ProbTable(ss);
        double[] d = new double[2];
        d[ss.getIndex(State.ZERO)] = zero;
        d[ss.getIndex(State.ONE)] = one;
        s1.init(d);

        return s1;
    }

   

}
