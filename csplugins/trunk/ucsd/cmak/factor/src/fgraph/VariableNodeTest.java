package fgraph;

import junit.framework.*;
import java.util.*;

public class VariableNodeTest extends AbstractNodeTest
{
    List l;

    protected void setUp()
    {
        l = new ArrayList();
        l.add(pt2em(createEdge(.7, .3), NodeType.EDGE));
        l.add(pt2em(createEdge(.65, .35), NodeType.EDGE));
        l.add(pt2em(createEdge(.5, .5), NodeType.EDGE));
        l.add(pt2em(createEdge(.4, .6), NodeType.EDGE));
        l.add(pt2em(createEdge(.2, .8), NodeType.EDGE));
        
    }

    public void testFix()
    {
        VariableNode n = VariableNode.createEdge(1);
        n.fixState(State.ONE);

        assertTrue("variable is fixed", n.isFixed());
        assertEquals("state is one", State.ONE, n.fixedState());
        
        n.maxProduct(l);

        double p1 = 1;
        double p0 = 0;

        checkProbs10(n.getProbs(), p1, p0);
    }


    public void testFixDir()
    {
        VariableNode n = VariableNode.createDirection(1);
        n.fixState(State.MINUS);

        assertTrue("variable is fixed", n.isFixed());
        assertEquals("state is one", State.MINUS, n.fixedState());
        
        n.maxProduct(l);

        double pp = 0;
        double pm = 1;

        checkProbsPM(n.getProbs(), pp, pm);
    }
    
    
    public void testMaxProduct()
    {
        VariableNode n = VariableNode.createEdge(1);
        n.maxProduct(l);

        double p1 = .7*.65*.5*.4*.2;
        double p0 = .3*.35*.5*.6*.8;

        checkProbs10(n.getProbs(), p1, p0);

        l.add(pt2em(createEdge(.42, .58), NodeType.EDGE));

        n.maxProduct(l);
        p1 *= .42;
        p0 *= .58;

        checkProbs10(n.getProbs(), p1, p0);
    }

    
    public void testDefaultProbs()
    {
        VariableNode n = VariableNode.createEdge(1);

        StateSet ss = n.stateSet();
        double[] df = new double[ss.size()];
        df[ss.getIndex(State.ONE)] = .9;
        df[ss.getIndex(State.ZERO)] = .1;
        
        n.setDefaultProbs(df);

        n.maxProduct(l);

        double p1 = .9*.7*.65*.5*.4*.2;
        double p0 = .1*.3*.35*.5*.6*.8;

        checkProbs10(n.getProbs(), p1, p0);

        l.add(pt2em(createEdge(.42, .58), NodeType.EDGE));

        n.maxProduct(l);
        p1 *= .42;
        p0 *= .58;

        checkProbs10(n.getProbs(), p1, p0);
    }

    
    public void testMaxProductBadMessages()
    {
        l.add(pt2em(createSign(.8, .2), NodeType.SIGN));
        
        VariableNode n = VariableNode.createEdge(1);
        n.maxProduct(l);

        // if the messages going into a variable node are not all
        // over the same states as the variable, then ERROR.
        // The probs should be set to 0.

        assertEquals("prob(ZERO)", 0, n.prob(State.ZERO), 0.000000001);
        assertEquals("prob(ONE)", 0, n.prob(State.ONE), 0.000000001);
    }
}
