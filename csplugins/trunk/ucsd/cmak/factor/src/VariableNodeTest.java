import junit.framework.*;
import java.util.*;

public class VariableNodeTest extends AbstractNodeTest
{
    List l;
    
    protected void setUp()
    {
        l = new ArrayList();
        l.add(pt2em(createEdge(.7, .3)));
        l.add(pt2em(createEdge(.65, .35)));
        l.add(pt2em(createEdge(.5, .5)));
        l.add(pt2em(createEdge(.4, .6)));
        l.add(pt2em(createEdge(.2, .8)));
        
    }

    public void testMaxProduct()
    {
        VariableNode n = VariableNode.createEdge();
        n.maxProduct(l);

        double p1 = .7*.65*.5*.4*.2;
        double p0 = .3*.35*.5*.6*.8;

        checkProbs10(n.getProbs(), p1, p0);

        l.add(pt2em(createEdge(.42, .58)));

        n.maxProduct(l);
        p1 *= .42;
        p0 *= .58;

        checkProbs10(n.getProbs(), p1, p0);
    }

        public void testMaxProductBadMessages()
    {
        l.add(pt2em(createSign(.8, .2)));
        
        VariableNode n = VariableNode.createEdge();
        n.maxProduct(l);

        // if the messages going into a variable node are not all
        // over the same states as the variable, then ERROR.
        // The probs should be set to 0.

        assertEquals("prob(ZERO)", 0, n.prob(State.ZERO), 0.000000001);
        assertEquals("prob(ONE)", 0, n.prob(State.ONE), 0.000000001);
    }
}
