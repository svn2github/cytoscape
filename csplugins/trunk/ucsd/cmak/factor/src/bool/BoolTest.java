package bool;

import junit.framework.*;
import java.util.*;

public class BoolTest extends TestCase
{
    protected void setUp()
    {

    }

    public void testSumProduct()
    {
        BooleanAttributes a1 = new BooleanAttributes(0.5, 0.5);
        a1.setId(5);
        Message m1 = new Message(1, new double[]{.6, .4});
        Message m2 = new Message(2, new double[]{.6, .4});
        List msgs = new ArrayList();
        msgs.add(m1);        
        msgs.add(m2);

        Message result = a1.sumProduct(msgs, 0);

        assertEquals(result.getSourceId(), a1.getId());
        assertEquals(.16/.52, result.getProb(BooleanAttributes.T), .001);
        assertEquals(.36/.52, result.getProb(BooleanAttributes.F), .001);
    }

    public static Test suite() {
        return new TestSuite(BoolTest.class);
    }

    public static void main(String args[]) { 
        junit.textui.TestRunner.run(suite());
    }
}
