import junit.framework.*;
import java.util.*;

import cern.colt.bitvector.BitVector;

public class FactorNodeTest extends AbstractNodeTest
{

    private double ep1 = PathFactorNode.ep1;
    private double ep2 = PathFactorNode.ep2;

    PathFactorNode f;
    List x; // list of edge messages

    public FactorNodeTest(String s)
    {
        super(s);
    }
    
    public static void main(String[] args)
    {
        long start = System.currentTimeMillis();
        TestCase c = new FactorNodeTest(args[0]);
        TestResult r = c.run();
        System.out.println("\n" + r.runCount()
                           + " test. Success=" + r.wasSuccessful());
        printFailures(r.errors());
        printFailures(r.failures());
    }

    protected static void printFailures(Enumeration e)
    {
        while(e.hasMoreElements())
        {
            System.out.println(((TestFailure) e.nextElement()).trace());
        }
    }
    
    protected void setUp()
    {
        f = PathFactorNode.getInstance();

        x = new ArrayList();
        x.add(pt2em(createEdge( .55, .45))); // target for edge
        x.add(pt2em(createEdge( .35, .65)));
        x.add(pt2em(createDir( .2, .8), State.PLUS)); // target for dir
        x.add(pt2em(createDir( .4, .6), State.MINUS));
        x.add(pt2em(createSign(.2, .8)));
        x.add(pt2em(createSign(.4, .6))); // target for sign
        x.add(pt2em(createKO(.1, .23, .67))); // target for ko
        x.add(pt2em(createPathActive(.85, .15))); //target for path active

    }

    public void testMaxProductEdge() throws AlgorithmException
    {
        ProbTable pt = f.maxProduct(x, 0, VariableNode.createEdge());

        double cs = ep1*.65*.8*.6*.8*.6*.67*.85;
        double pe = 1*.35*.2*.6*.8*.6*.67*.85;

        checkProbs10(pt, pe, cs);
        
    }

    public void testMaxProductDir() throws AlgorithmException
    {
        ProbTable pt = f.maxProduct(x, 2, VariableNode.createDirection());

        double cs = ep1*.55*.65*.6*.8*.6*.67*.85;
        double pe = 1*.55*.35*.6*.8*.6*.67*.85;

        checkProbsPM(pt, pe, cs);
    }


    public void testMaxProductSign() throws AlgorithmException
    {
        System.out.println("### test Sign");
        ProbTable pt = f.maxProduct(x, 5,  VariableNode.createSign());

        double p = ep1*.55*.65*.8*.6*.8*.67*.15;
        double m = 1*.55*.35*.2*.6*.8*.67*.85;

        checkProbsPM(pt, p, m);
    }

    public void testMaxProductPathActive() throws AlgorithmException
    {
        ProbTable pt = f.maxProduct(x, 7, VariableNode.createPathActive());

        double cs = ep1*.55*.65*.8*.6*.8*.6*.67;
        double pe = ep1*.55*.65*.8*.6*.8*.6*.67;


        checkProbs10(pt, pe, cs);
    }


    public void testMaxProductKO() throws AlgorithmException
    {
        System.out.println("### test KO");
        ProbTable pt = f.maxProduct(x, 6, VariableNode.createKO());

        double z = ep1*.55*.65*.8*.6*.8*.6*.85;
        double p = ep2*.55*.65*.8*.6*.8*.6*.85;
        double m = ep2*.55*.65*.8*.6*.8*.6*.85;

        System.out.println("z=" + z);
        System.out.println("p=" + p);
        System.out.println("m=" + m);
        
        checkProbsPMZ(pt, p, m, z);
        System.out.println("### done KO");
    }


    public void testPathExplains()
    {
        List x = new ArrayList();
        x.add(createEdge( .55, .45));
        x.add(createEdge( .35, .65));

        List d = new ArrayList();
        d.add(createDir( .2, .8));
        d.add(createDir( .4, .6));

        List dirStates = new ArrayList();
        dirStates.add(State.PLUS);
        dirStates.add(State.MINUS);
        
        List s = new ArrayList();
        s.add(createSign(.2, .8));
        s.add(createSign(.4, .6));

        ProbTable k1 = createKO(.1, .23, .67);
        ProbTable sigma = createPathActive(.85, .15);

        double pe = f.computePathExplains(x, d, dirStates, k1, sigma, s);

        assertEquals("path explains", 1*.55*.35*.2*.6*.8*.6*.67*.85, pe, 0.000000000001);

        ProbTable k2 = createKO(.1, .67, .23);
        pe = f.computePathExplains(x, d, dirStates, k2, sigma, s);

        assertEquals("path explains", 1*.55*.35*.2*.6*.8*.4*.67*.85, pe, 0.000000000001);
        
    }
    
    public void testMaximizeSign()
    {
        List l = new ArrayList();
        l.add(createSign(.2, .8));
        maxSignHelper(l, State.PLUS, 0.8);
        maxSignHelper(l, State.MINUS, 0.2);
        
        l.add(createSign(.4, .6));
     
        maxSignHelper(l, State.PLUS, 0.8 * 0.4);
        maxSignHelper(l, State.MINUS, 0.8 * 0.6);

        l.add(createSign(.7, .3));

        maxSignHelper(l, State.PLUS, 0.8*0.4*0.7);
        maxSignHelper(l, State.MINUS, 0.8*0.6*0.7);

        l.add(createSign(.5, .5));

        maxSignHelper(l, State.PLUS, 0.8*0.6*0.7*0.5);
        maxSignHelper(l, State.MINUS, 0.8*0.6*0.7*0.5);
    }

    private void maxSignHelper(List signs, State s, double expected)
    {
        double max = f.maximizeSign(signs, s);

        System.out.println("max is " + max);
        assertEquals("max sz=" + signs.size(), expected, max, .0001);
    }

    public void testEnumerate()
    {
        for(int num=1; num < 6; num++)
        {
            BitVector[] combos = f.enumerate(num, State.PLUS);

            _checkCombo(num, combos, 1);

            combos = f.enumerate(num, State.MINUS);
            _checkCombo(num, combos, 0);
        }
    }

    
    public void testMaximizeSignK()
    {
        ProbTable kM = createKO(.1, .15, .75);
        ProbTable kP = createKO(.1, .75, .15);

        List l = new ArrayList();
        l.add(createSign(.2, .8));

        maxSignHelperK(l, kM, 0.75*.2);
        maxSignHelperK(l, kP, 0.75*.8);
        
        l.add(createSign(.4, .6));

        maxSignHelperK(l, kP, .75*0.8 * 0.4);
        maxSignHelperK(l, kM, .75*0.8 * 0.6);

        l.add(createSign(.7, .3));

        maxSignHelperK(l, kP, .75*0.8 * 0.4 * .7);
        maxSignHelperK(l, kM, .75*0.8 * 0.6 * .7);


        l.add(createSign(.5, .5));
        
        maxSignHelperK(l, kP, .75*0.8 * 0.6 * .7 *.5);
        maxSignHelperK(l, kM, .75*0.8 * 0.6 * .7*.5);

    }

    private void maxSignHelperK(List signs, ProbTable k, double expected)
    {
        double max = f.maximizeSign(signs, k);

        System.out.print("max is " + max);
        assertEquals("numsigns=" + signs.size(), expected, max, .0001);
        System.out.println(" ok");
    }

    public void testEnumerate2()
    {
        for(int num=1; num < 6; num++)
        {
            BitVector[][] combos = f.enumerate(num);

            System.out.println(combos.length);
            
            _checkCombo(num, combos[0], 1);
            _checkCombo(num, combos[1], 0);
        }
    }

    private void _checkCombo(int num, BitVector[] combos, int expectedCardinality)
    {
        System.out.println("checking combo num-sign-vars=" + num
                           + " num-configs=" + combos.length);
        
        for(int x=0; x < combos.length; x++)
        {
            if(num==3)
            {
                System.out.println("   " + combos[x] + " card=" + combos[x].cardinality());
            }
            assertEquals(num + " sign vars. combo " + x + 
                         ". Number of bits should be odd", 
                         expectedCardinality, 0x1 & combos[x].cardinality());
        }
    }

    /**
     * test countBits() and parity()
     */
    public void testBitOps()
    {
        int[] cnt = {0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4};
        int[] par = {0, 1, 1, 0, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0};

        for(int x=0; x < 16; x++)
        {
            System.out.println(x + " cnt=" + f.countBits(x) + " par=" + f.parity(x));
            assertEquals(x + " num bits", cnt[x], f.countBits(x));
            assertEquals(x + " parity", par[x], f.parity(x));

        }

        int m = 0x7fffffff; // 2^31
        assertEquals(m + " num bits", 31, f.countBits(m));
        assertEquals(m + " parity", 1, f.parity(m));

        m -= 1;
        assertEquals(m + " num bits", 30, f.countBits(m));
        assertEquals(m + " parity", 0, f.parity(m));
    }
}
